package com.alibaba.android.arouter.compiler.processor;

import static com.alibaba.android.arouter.facade.enums.TypeKind.PARCELABLE;
import static com.alibaba.android.arouter.facade.enums.TypeKind.SERIALIZABLE;

import com.alibaba.android.arouter.compiler.utils.Constants;
import com.alibaba.android.arouter.compiler.utils.TypeUtils;
import com.alibaba.android.arouter.compiler.utils.Utils;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.enums.TypeKind;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
//@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
public class ActivityRouterProcessor extends BaseRouterProcessor {
    private Types types;
    private TypeMirror iProvider = null;
    private TypeUtils typeUtils;
    private final ClassName arouterClassName = ClassName.get("com.alibaba.android.arouter.launcher", "ARouter");
    private final ClassName postcardClass = ClassName.get("com.alibaba.android.arouter.facade", "Postcard");
    private final ClassName callbackClass = ClassName.get("com.alibaba.android.arouter.facade.callback", "NavCallback");
    private final ClassName activityClass = ClassName.get("android.app", "Activity");
    private final ClassName contextClass = ClassName.get("android.content", "Context");
    private final ClassName bundleClass = ClassName.get("android.os", "Bundle");

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        types = processingEnv.getTypeUtils();
        typeUtils = new TypeUtils(types, processingEnv.getElementUtils());
        iProvider = processingEnv.getElementUtils().getTypeElement(Constants.IPROVIDER).asType();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(Route.class.getCanonicalName());
        set.add(Autowired.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Route.class);
        if (elements != null && !elements.isEmpty()) {
            Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Route.class);
            for (Element element : routeElements) {
                if (isActivity(element)) { // Activity
                    try {
                        createActivityRouter(element);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }
            logger.info("忽略异常提示");
            //这里要注意，要返回false，并且要放在Processor的前面，否则会影响arouter的Processor。
            return false;
        }
        return true;
    }

    private ClassName getCurrentClassName(Element element) {
        String simpleName = element.getSimpleName().toString().replace("Activity", "");
        return ClassName.get(ClassName.get((TypeElement) element).packageName(), simpleName + "Router");
    }

    private void createActivityRouter(Element element) throws Exception {
        Route route = element.getAnnotation(Route.class);

        TypeSpec.Builder builder = TypeSpec
                .classBuilder(getCurrentClassName(element).simpleName())
                .addJavadoc(route.name() + "\n类位置：{@link " + ClassName.get((TypeElement) element) + "}" + "\n自动生成,请勿编辑!")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        builder.addField(FieldSpec
                .builder(String.class, "PATH", Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc(route.name() + "\n")
                .addJavadoc("路由路径")
                .initializer("\"" + route.path() + "\"")
                .build());

        builder.addMethod(createPostcard(element));
        builder.addMethod(createStart(element));
        builder.addMethod(createStart2(element));
        builder.addMethod(createStart5(element));
        builder.addMethod(createStart3(element));
        builder.addMethod(createStart4(element));

        builder.addMethod(createPostcard2(element));
        builder.addMethod(createStarWithBundle(element));
        builder.addMethod(createStart2WithBundle(element));
        builder.addMethod(createStart5WithBundle(element));
        builder.addMethod(createStart3WithBundle(element));
        builder.addMethod(createStart4WithBundle(element));


        JavaFile javaFile = JavaFile
                .builder(ClassName.get((TypeElement) element).packageName(), builder.build())
                // 设置表示缩进的字符串
                .indent("    ")
                .build();

//        String module = getValue(OUT_MODULE_NAME);
//        Path path = Paths.get(System.getProperty("user.dir"), module, "src", "main", "java");
//        javaFile.writeTo(path);
        javaFile.writeTo(processingEnv.getFiler());
    }

    /**
     * Postcard方法
     */
    private MethodSpec createPostcard(Element element) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getPostcard").addModifiers(Modifier.PUBLIC, Modifier.STATIC).addJavadoc("构建Postcard\n");
        builder.addCode("$T postcard = $T.getInstance().build(PATH);", postcardClass, arouterClassName);
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                builder.addCode(makeCode(field, autowired));
                builder.addParameter(createParameterSpec(field, autowired));
            }
        }
        builder.addCode("\nreturn postcard;");
        builder.returns(postcardClass);
        return builder.build();
    }

    /**
     * Postcard方法
     */
    private MethodSpec createPostcard2(Element element) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getPostcard").addModifiers(Modifier.PUBLIC, Modifier.STATIC).addJavadoc("构建Postcard\n");
        builder.addCode("$T postcard = $T.getInstance().build(PATH);", postcardClass, arouterClassName);
        builder.addParameter(ParameterSpec
                .builder(bundleClass, "bundle")
                .addJavadoc("参数信息\n")
                .build());
        builder.addCode("\npostcard.with(bundle);");
        builder.addCode("\nreturn postcard;");
        builder.returns(postcardClass);
        return builder.build();
    }

    /**
     * Start方法
     */
    private MethodSpec createStart(Element element) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("start").addModifiers(Modifier.PUBLIC, Modifier.STATIC).addJavadoc("启动器\n");
        String params = "";
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                String fieldName = field.getSimpleName().toString();
                String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
                if (StringUtils.isEmpty(params)) {
                    params = key;
                } else {
                    params = params + "," + key;
                }
                builder.addParameter(createParameterSpec(field, autowired));
            }
        }
        builder.addCode("$T postcard = getPostcard(" + params + ");", postcardClass);
        builder.addCode("\npostcard.navigation();");
        builder.returns(void.class);
        return builder.build();
    }

    /**
     * Start方法
     */
    private MethodSpec createStarWithBundle(Element element) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("start").addModifiers(Modifier.PUBLIC, Modifier.STATIC).addJavadoc("启动器\n");

        builder.addParameter(ParameterSpec
                .builder(bundleClass, "bundle")
                .addJavadoc("参数信息\n")
                .build());
        builder.addCode("$T postcard = getPostcard(bundle);", postcardClass);
        builder.addCode("\npostcard.navigation();");
        builder.returns(void.class);
        return builder.build();
    }

    /**
     * Start方法
     */
    private MethodSpec createStart2(Element element) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("start").addModifiers(Modifier.PUBLIC, Modifier.STATIC).addJavadoc("启动器\n");
        String params = "";
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                String fieldName = field.getSimpleName().toString();
                String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
                if (StringUtils.isEmpty(params)) {
                    params = key;
                } else {
                    params = params + "," + key;
                }
                builder.addParameter(createParameterSpec(field, autowired));
            }
        }
        builder.addCode("$T postcard = getPostcard(" + params + ");", postcardClass);
        builder.addParameter(ParameterSpec
                .builder(callbackClass, "callback")
                .addJavadoc("导航回调\n")
                .build());
        builder.addCode("\npostcard.navigation(null, callback);");
        builder.returns(void.class);
        return builder.build();
    }


    /**
     * Start方法
     */
    private MethodSpec createStart2WithBundle(Element element) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("start").addModifiers(Modifier.PUBLIC, Modifier.STATIC).addJavadoc("启动器\n");
        builder.addParameter(ParameterSpec
                .builder(bundleClass, "bundle")
                .addJavadoc("参数信息\n")
                .build());
        builder.addCode("$T postcard = getPostcard(bundle);", postcardClass);
        builder.addParameter(ParameterSpec
                .builder(callbackClass, "callback")
                .addJavadoc("导航回调\n")
                .build());
        builder.addCode("\npostcard.navigation(null, callback);");
        builder.returns(void.class);
        return builder.build();
    }

    /**
     * Start方法
     */
    private MethodSpec createStart3(Element element) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("startForResult").addModifiers(Modifier.PUBLIC, Modifier.STATIC).addJavadoc("启动器\n");
        String params = "";
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                String fieldName = field.getSimpleName().toString();
                String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
                if (StringUtils.isEmpty(params)) {
                    params = key;
                } else {
                    params = params + ", " + key;
                }
                builder.addParameter(createParameterSpec(field, autowired));
            }
        }
        builder.addParameter(ParameterSpec
                .builder(activityClass, "activity")
                .addJavadoc("上下文\n")
                .build());
        builder.addParameter(ParameterSpec
                .builder(ClassName.get(Integer.class), "requestCode")
                .build());
        builder.addCode("$T postcard = getPostcard(" + params + ");", postcardClass);
        builder.addCode("\npostcard.navigation(activity, requestCode);");
        builder.returns(void.class);
        return builder.build();
    }

    /**
     * Start方法
     */
    private MethodSpec createStart4(Element element) {
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder("startForResult")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addJavadoc("启动器");

        String params = "";
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                String fieldName = field.getSimpleName().toString();
                String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
                if (StringUtils.isEmpty(params)) {
                    params = key;
                } else {
                    params = params + ", " + key;
                }
                builder.addParameter(createParameterSpec(field, autowired));
            }
        }
        builder.addParameter(ParameterSpec
                .builder(activityClass, "activity")
                .addJavadoc("上下文\n")
                .build());
        builder.addParameter(ParameterSpec
                .builder(ClassName.get(Integer.class), "requestCode")
                .addJavadoc("请求码\n")
                .build());
        builder.addParameter(ParameterSpec
                .builder(callbackClass, "callback")
                .addJavadoc("导航回调\n")
                .build());

        builder.addCode("$T postcard = getPostcard(" + params + ");", postcardClass);
        builder.addCode("\npostcard.navigation(activity, requestCode, callback);");
        builder.returns(void.class);
        return builder.build();
    }
    /**
     * Start方法
     */
    private MethodSpec createStart5(Element element) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("start").addModifiers(Modifier.PUBLIC, Modifier.STATIC).addJavadoc("启动器");
        String params = "";
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                String fieldName = field.getSimpleName().toString();
                String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
                if (StringUtils.isEmpty(params)) {
                    params = key;
                } else {
                    params = params + "," + key;
                }
                builder.addParameter(createParameterSpec(field, autowired));
            }
        }
        builder.addCode("$T postcard = getPostcard(" + params + ");", postcardClass);
        builder.addParameter(ParameterSpec
                .builder(contextClass, "context")
                .addJavadoc("上下文\n")
                .build());
        builder.addParameter(ParameterSpec
                .builder(callbackClass, "callback")
                .addJavadoc("导航回调\n")
                .build());
        builder.addCode("\npostcard.navigation(context, callback);");
        builder.returns(void.class);
        return builder.build();
    }
    private ParameterSpec createParameterSpec(Element field, Autowired autowired) {
        String fieldName = field.getSimpleName().toString();
        String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
        TypeMirror typeMirror = field.asType();
        String typeName = field.asType().toString();
        ParameterSpec parameterSpec;
        if (typeMirror.getKind().isPrimitive()) {
            parameterSpec = ParameterSpec.builder(TypeName.get(typeMirror), key).addJavadoc(autowired.desc() + "\n").build();
        } else {
            //是否是泛型
            if (typeName.contains("<") && typeName.contains(">")) {
                int startIndex = typeName.indexOf("<");
                int endIndex = typeName.indexOf(">");
                String tmp = typeName.substring(startIndex + 1, endIndex);
                int index = tmp.lastIndexOf(".");
                ClassName className = ClassName.get(tmp.substring(0, index), tmp.substring(index + 1));
                ClassName list = ClassName.get("java.util", "List");
                parameterSpec = ParameterSpec.builder(ParameterizedTypeName.get(list, className), key).addJavadoc(autowired.desc() + "\n").build();
            } else {
                if (typeName.contains(".")) {
                    int index = typeName.lastIndexOf(".");
                    ClassName className = ClassName.get(typeName.substring(0, index), typeName.substring(index + 1));
                    parameterSpec = ParameterSpec.builder(className, key).addJavadoc(autowired.desc() + "\n").build();
                } else {
                    parameterSpec = ParameterSpec.builder(Object.class, key).addJavadoc(autowired.desc() + "\n").build();
                }
            }
        }
        return parameterSpec;
    }

    private String makeCode(Element field, Autowired autowired) {
        String fieldName = field.getSimpleName().toString();
        String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
        String typeName = field.asType().toString();
        TypeMirror typeMirror = field.asType();
        TypeKind typeKind = TypeKind.values()[typeUtils.typeExchange(field)];

        String name = Utils.toUpperCaseFirstOne(typeName);
        String code = "";
        if (typeMirror.getKind().isPrimitive()) {
            logger.info(module + ",字段:" + fieldName + " -> 基本类型:" + name);
            code += "\npostcard.with" + name + "(\"" + key + "\"," + key + ");";
        } else {
            code = "\nif (" + key + " != null) {";
            if (typeKind == SERIALIZABLE) {
                logger.info(module + ",字段:" + fieldName + " -> Serializable类型:" + name);
                code += "\n    postcard.withSerializable(\"" + key + "\",(" + typeName + ")" + key + ");";
            } else if (typeKind == PARCELABLE) {
                logger.info(module + ",字段:" + fieldName + " -> Parcelable类型:" + name);
                code += "\n    postcard.withParcelable(\"" + key + "\",(" + typeName + ")" + key + ");";
            } else {
                logger.info(module + ",字段:" + fieldName + " -> 字段类型:" + name);
                if (name.contentEquals("Java.lang.Integer")) {
                    code += "\n    postcard.withInt(\"" + key + "\"," + key + ");";
                } else {
                    if (typeName.startsWith("java.lang") || typeName.startsWith("java.util")) {
                        //是否是泛型
                        if (!typeName.contains("<") && typeName.contains(".")) {
                            int index = typeName.lastIndexOf(".");
                            name = Utils.toUpperCaseFirstOne(typeName.substring(index + 1));
                            code += "\n    postcard.with" + name + "(\"" + key + "\"," + key + ");";
                        }
                    } else {
                        code += "\n    postcard.withObject(\"" + key + "\"," + key + ");";
                    }
                }
            }
            code += "\n}";
        }
        return code;
    }


    private TypeMirror getTypeMirror(String name) {
        return processingEnv.getElementUtils().getTypeElement(name).asType();
    }

    private boolean isActivity(Element element) {
        return types.isSubtype(element.asType(), getTypeMirror(Constants.ACTIVITY));
    }

    /**
     * Start方法
     */
    private MethodSpec createStart5WithBundle(Element element) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("start").addModifiers(Modifier.PUBLIC, Modifier.STATIC).addJavadoc("启动器");
        builder.addParameter(ParameterSpec
                .builder(bundleClass, "bundle")
                .addJavadoc("参数信息\n")
                .build());
        builder.addCode("$T postcard = getPostcard(bundle);", postcardClass);
        builder.addParameter(ParameterSpec
                .builder(contextClass, "context")
                .addJavadoc("上下文\n")
                .build());
        builder.addParameter(ParameterSpec
                .builder(callbackClass, "callback")
                .addJavadoc("导航回调\n")
                .build());
        builder.addCode("\npostcard.navigation(context, callback);");
        builder.returns(void.class);
        return builder.build();
    }


    /**
     * Start方法
     */
    private MethodSpec createStart3WithBundle(Element element) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("startForResult").addModifiers(Modifier.PUBLIC, Modifier.STATIC).addJavadoc("启动器\n");
        builder.addParameter(ParameterSpec
                .builder(bundleClass, "bundle")
                .addJavadoc("参数信息\n")
                .build());
        builder.addParameter(ParameterSpec
                .builder(activityClass, "activity")
                .addJavadoc("上下文\n")
                .build());
        builder.addParameter(ParameterSpec
                .builder(ClassName.get(Integer.class), "requestCode")
                .build());
        builder.addCode("$T postcard = getPostcard(bundle);", postcardClass);
        builder.addCode("\npostcard.navigation(activity, requestCode);");
        builder.returns(void.class);
        return builder.build();
    }

    /**
     * Start方法
     */
    private MethodSpec createStart4WithBundle(Element element) {
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder("startForResult")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addJavadoc("启动器");
        builder.addParameter(ParameterSpec
                .builder(bundleClass, "bundle")
                .addJavadoc("参数信息\n")
                .build());
        builder.addParameter(ParameterSpec
                .builder(activityClass, "activity")
                .addJavadoc("上下文\n")
                .build());
        builder.addParameter(ParameterSpec
                .builder(ClassName.get(Integer.class), "requestCode")
                .addJavadoc("请求码\n")
                .build());
        builder.addParameter(ParameterSpec
                .builder(callbackClass, "callback")
                .addJavadoc("导航回调\n")
                .build());

        builder.addCode("$T postcard = getPostcard(bundle);", postcardClass);
        builder.addCode("\npostcard.navigation(activity, requestCode, callback);");
        builder.returns(void.class);
        return builder.build();
    }
}
