package com.github.lany192.arouter.processor;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.enums.TypeKind;
import com.github.lany192.arouter.utils.Consts;
import com.github.lany192.arouter.utils.Logger;
import com.github.lany192.arouter.utils.TypeUtils;
import com.github.lany192.arouter.utils.Utils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.lang.Exception;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static com.alibaba.android.arouter.facade.enums.TypeKind.PARCELABLE;
import static com.alibaba.android.arouter.facade.enums.TypeKind.SERIALIZABLE;

/**
 * @author Administrator
 */
@AutoService(Processor.class)
public class AppRouterProcessor extends AbstractProcessor {
    private Logger logger;
    private Types types;
    private TypeMirror iProvider = null;
    private TypeUtils typeUtils;
    private ClassName routerClassName = ClassName.get("com.alibaba.android.arouter.launcher", "ARouter");
    private ClassName routePathClassName = ClassName.get("com.alibaba.android.arouter", "RoutePath");

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        types = processingEnv.getTypeUtils();
        typeUtils = new TypeUtils(types, processingEnv.getElementUtils());
        logger = new Logger(processingEnv.getMessager());
        iProvider = processingEnv.getElementUtils().getTypeElement(Consts.IPROVIDER).asType();
        logger.info("初始化");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
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
            List<MethodSpec> methods = new ArrayList<>();

            for (Element element : routeElements) {
                if (isActivity(element)) { // Activity
                    methods.add(skipActivity(element));
                } else if (isFragment(element)) { // Fragment
                    methods.add(getFragmentInstance(element));
                } else if (types.isSubtype(element.asType(), iProvider)) {// IProvider
                    logger.info(">>> Found provider route: " + element.asType().toString() + " <<<");
                    methods.add(getProviderInstance(element));
                } else if (types.isSubtype(element.asType(), getTypeMirror(Consts.SERVICE))) {// Service
                    logger.info(">>> Found service route: " + element.asType().toString() + " <<<");
                    methods.add(getServiceInstance(element));
                } else {
                    throw new RuntimeException("The @Route is marked on unsupported class, look at [" + element.asType().toString() + "].");
                }
            }
            try {
                createRouterHelper(methods);
            } catch (Exception e) {
                logger.error(e);
            }
            logger.info("忽略异常提示");
            //这里要注意，要返回false，并且要放在Processor的前面，否则会影响arouter的Processor。
            return false;
        }
        return true;
    }

    /**
     * 跳转Activity方法
     */
    private MethodSpec skipActivity(Element element) {
        String methodName = Utils.toLowerCaseFirstOne(element.getSimpleName().toString().replace("Activity", ""));
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("跳转到{@link " + ClassName.get((TypeElement) element) + "}");
        Route route = element.getAnnotation(Route.class);
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
//                        logger.info("目标类:" + element.getSimpleName() + ",路径:" + route.path() + "字段名:" + field.getSimpleName() + " ,类型：" + field.asType().toString() + "，注释:" + autowired.desc() + "，必选:" + autowired.required());
                builder.addParameter(getParameter(field, autowired));
            }
        }
        builder.addCode("$T.getInstance()", routerClassName);
        builder.addCode(".build($T." + route.path().replace("/", "_").toUpperCase().substring(1) + ")", routePathClassName);
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                builder.addCode(makeCode(field, autowired));
            }
        }
        builder.addCode("\n.navigation();");
        builder.returns(void.class);
        return builder.build();
    }

    /**
     * 获取Provider实例方法
     */
    private MethodSpec getProviderInstance(Element element) {
        String methodName = Utils.toLowerCaseFirstOne(element.getSimpleName().toString());
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("跳转到{@link " + ClassName.get((TypeElement) element) + "}");
        Route route = element.getAnnotation(Route.class);
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                builder.addParameter(getParameter(field, autowired));
            }
        }
        builder.addCode("$T.getInstance()", routerClassName);
        builder.addCode(".build($T." + route.path().replace("/", "_").toUpperCase().substring(1) + ")", routePathClassName);
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                builder.addCode(makeCode(field, autowired));
            }
        }
        builder.addCode("\n.navigation();");
        builder.returns(void.class);
        return builder.build();
    }

    /**
     * 获取Service实例方法
     */
    private MethodSpec getServiceInstance(Element element) {
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder("get" + element.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("获取{@link " + ClassName.get((TypeElement) element) + "}");
        Route route = element.getAnnotation(Route.class);
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                builder.addParameter(getParameter(field, autowired));
            }
        }
        builder.addCode("return (" + element.getSimpleName().toString() + ")$T.getInstance()", routerClassName);
        builder.addCode(".build($T." + route.path().replace("/", "_").toUpperCase().substring(1) + ")", routePathClassName);
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                builder.addCode(makeCode(field, autowired));
            }
        }
        builder.addCode("\n.navigation();");
        builder.returns(TypeName.get(element.asType()));
        return builder.build();
    }

    /**
     * 获取Fragment实例方法
     */
    private MethodSpec getFragmentInstance(Element element) {
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder("get" + element.getSimpleName().toString().replace("Fragment", ""))
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("获取实例{@link " + ClassName.get((TypeElement) element) + "}");
        Route route = element.getAnnotation(Route.class);
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                builder.addParameter(getParameter(field, autowired));
            }
        }
        builder.addCode("return (Fragment)$T.getInstance()", routerClassName);
        builder.addCode(".build($T." + route.path().replace("/", "_").toUpperCase().substring(1) + ")", routePathClassName);
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                builder.addCode(makeCode(field, autowired));
            }
        }
        builder.addCode("\n.navigation();");
        if (types.isSubtype(element.asType(), getTypeMirror(Consts.FRAGMENT_X))) {
            builder.returns(ClassName.get("androidx.fragment.app", "Fragment"));
        } else if (types.isSubtype(element.asType(), getTypeMirror(Consts.FRAGMENT_V4))) {
            builder.returns(ClassName.get("android.support.v4.app", "Fragment"));
        } else {
            builder.returns(ClassName.get("android.app", "Fragment"));
        }
        return builder.build();
    }

    private String makeCode(Element field, Autowired autowired) {
        String fieldName = field.getSimpleName().toString();
        String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
        String typeName = field.asType().toString();
        TypeMirror typeMirror = field.asType();
        TypeKind typeKind = TypeKind.values()[typeUtils.typeExchange(field)];

        String name = Utils.toUpperCaseFirstOne(typeName);
        if (typeMirror.getKind().isPrimitive()) {
            logger.info("字段:" + fieldName + " -> 基本类型:" + name);
            return "\n.with" + name + "(\"" + key + "\"," + key + ")";
        } else {
            if (typeKind == SERIALIZABLE) {
                logger.info("字段:" + fieldName + " -> Serializable类型:" + name);
                return "\n.withSerializable(\"" + key + "\",(" + typeName + ")" + key + ")";
            } else if (typeKind == PARCELABLE) {
                logger.info("字段:" + fieldName + " -> Parcelable类型:" + name);
                return "\n.withParcelable(\"" + key + "\",(" + typeName + ")" + key + ")";
            } else {
                logger.info("字段:" + fieldName + " -> 字段类型:" + name);
                if (typeName.startsWith("java.lang") || typeName.startsWith("java.util")) {
                    //是否是泛型
                    if (!typeName.contains("<") && typeName.contains(".")) {
                        int index = typeName.lastIndexOf(".");
                        name = Utils.toUpperCaseFirstOne(typeName.substring(index + 1));
                        return "\n.with" + name + "(\"" + key + "\"," + key + ")";
                    }
                }
                return "\n.withObject(\"" + key + "\"," + key + ")";
            }
        }
    }

    private ParameterSpec getParameter(Element field, Autowired autowired) {
        String fieldName = field.getSimpleName().toString();
        String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
        String typeName = field.asType().toString();
        ParameterSpec.Builder builder;
        TypeMirror typeMirror = field.asType();
//        logger.info("字段:" + fieldName + ",类型:" + typeName);
        //是否原始类型
        if (typeMirror.getKind().isPrimitive()) {
            builder = ParameterSpec.builder(TypeName.get(typeMirror), key);
        } else {
            //是否是泛型
            if (typeName.contains("<") && typeName.contains(">")) {
                int startIndex = typeName.indexOf("<");
                int endIndex = typeName.indexOf(">");
                String tmp = typeName.substring(startIndex + 1, endIndex);
                int index = tmp.lastIndexOf(".");
                ClassName className = ClassName.get(tmp.substring(0, index), tmp.substring(index + 1));
                ClassName list = ClassName.get("java.util", "List");
                builder = ParameterSpec.builder(ParameterizedTypeName.get(list, className), key);
            } else {
                if (typeName.contains(".")) {
                    int index = typeName.lastIndexOf(".");
                    ClassName className = ClassName.get(typeName.substring(0, index), typeName.substring(index + 1));
                    builder = ParameterSpec.builder(className, key);
                } else {
                    builder = ParameterSpec.builder(Object.class, key);
                }
            }
        }
        return builder.addJavadoc(autowired.desc() + "\n").build();
    }

    private void createRouterHelper(List<MethodSpec> methods) throws Exception {
        TypeSpec.Builder builder = TypeSpec.classBuilder("AppRouter")
                .addJavadoc("路由助手,自动生成,请勿编辑!")
                .addModifiers(Modifier.PUBLIC);

        ClassName routerType = ClassName.get("com.alibaba.android.arouter", "AppRouter");

        builder.addField(FieldSpec.builder(String.class, "TAG", Modifier.FINAL, Modifier.PRIVATE).initializer("\"AppRouter\"").build());
        builder.addField(FieldSpec.builder(routerType, "instance", Modifier.VOLATILE, Modifier.STATIC, Modifier.PRIVATE).build());

        builder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

        MethodSpec getMethodSpec = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .beginControlFlow("if (instance == null) ")
                .beginControlFlow("synchronized (AppRouter.class) ")
                .beginControlFlow("if (instance == null) ")
                .addCode("instance = new AppRouter();")
                .endControlFlow()
                .endControlFlow()
                .endControlFlow()
                .addCode("return instance;")
                .returns(routerType)
                .build();
        builder.addMethod(getMethodSpec);

        MethodSpec skipMethodSpec = MethodSpec.methodBuilder("skip")
                .addJavadoc("通用跳转")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec
                        .builder(String.class, "path")
                        .addJavadoc("路由路径")
                        .build())
                .addStatement("ARouter.getInstance().build(path).navigation()")
                .returns(void.class)
                .build();
        builder.addMethod(skipMethodSpec);


        MethodSpec skipMethodSpec2 = MethodSpec.methodBuilder("skip")
                .addJavadoc("通用跳转")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec
                        .builder(String.class, "path")
                        .addJavadoc("路由路径\n")
                        .build())
                .addParameter(ParameterSpec
                        .builder(ClassName.get("android.os", "Bundle"), "bundle")
                        .addJavadoc("Bundle对象")
                        .build())
                .addStatement("ARouter.getInstance().build(path).with(bundle).navigation()")
                .returns(void.class)
                .build();
        builder.addMethod(skipMethodSpec2);

        MethodSpec skipMethodSpec3 = MethodSpec.methodBuilder("skip")
                .addJavadoc("通用跳转")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec
                        .builder(ClassName.get("android.net", "Uri"), "uri")
                        .addJavadoc("路由路径")
                        .build())
                .addStatement("ARouter.getInstance().build(uri).navigation()")
                .returns(void.class)
                .build();
        builder.addMethod(skipMethodSpec3);

        MethodSpec methodSpec3 = MethodSpec.methodBuilder("show")
                .addJavadoc("显示Fragment")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec
                        .builder(ClassName.get("androidx.fragment.app", "FragmentManager"), "fragmentManager")
                        .addJavadoc("Fragment管理器\n")
                        .build())
                .addParameter(ParameterSpec
                        .builder(ClassName.get("androidx.fragment.app", "Fragment"), "fragment")
                        .addJavadoc("fragment实例\n")
                        .build())
                .addParameter(ParameterSpec
                        .builder(String.class, "tag")
                        .addJavadoc("fragment标记\n")
                        .build())
                .addCode("$T ft = fragmentManager.beginTransaction();\n", ClassName.get("androidx.fragment.app", "FragmentTransaction"))
                .addCode("ft.add(fragment, tag);\n")
                .addCode("ft.commitAllowingStateLoss();\n")
                .returns(void.class)
                .build();
        builder.addMethod(methodSpec3);

        MethodSpec methodSpec4 = MethodSpec.methodBuilder("show")
                .addJavadoc("显示Fragment")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec
                        .builder(ClassName.get("androidx.fragment.app", "FragmentManager"), "fragmentManager")
                        .addJavadoc("Fragment管理器\n")
                        .build())
                .addParameter(ParameterSpec
                        .builder(ClassName.get("androidx.fragment.app", "Fragment"), "fragment")
                        .addJavadoc("fragment实例")
                        .build())
                .addStatement("show(fragmentManager, fragment, fragment.getClass().getName())")
                .returns(void.class)
                .build();
        builder.addMethod(methodSpec4);

        MethodSpec methodSpec5 = MethodSpec.methodBuilder("show")
                .addJavadoc("显示Fragment")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec
                        .builder(ClassName.get("androidx.fragment.app", "FragmentActivity"), "activity")
                        .addJavadoc("Activity实例\n")
                        .build())
                .addParameter(ParameterSpec
                        .builder(ClassName.get("androidx.fragment.app", "Fragment"), "fragment")
                        .addJavadoc("fragment实例")
                        .build())
                .addCode("if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {\n")
                .addCode("    show(activity.getSupportFragmentManager(), fragment, fragment.getClass().getName());\n")
                .addCode("} else {\n")
                .addCode("    $T.e(TAG,\"Fragment宿主不存在或者不可用，不能调起对话框\");\n", ClassName.get("android.util", "Log"))
                .addCode("}\n")
                .returns(void.class)
                .build();
        builder.addMethod(methodSpec5);

        for (MethodSpec method : methods) {
            builder.addMethod(method);
        }
        JavaFile javaFile = JavaFile
                .builder("com.alibaba.android.arouter", builder.build())
                // 设置表示缩进的字符串
                .indent("    ")
                .build();
        javaFile.writeTo(processingEnv.getFiler());
    }


    private TypeMirror getTypeMirror(String name) {
        return processingEnv.getElementUtils().getTypeElement(name).asType();
    }

    private boolean isActivity(Element element) {
        return types.isSubtype(element.asType(), getTypeMirror(Consts.ACTIVITY));
    }

    private boolean isFragment(Element element) {
        return types.isSubtype(element.asType(), getTypeMirror(Consts.FRAGMENT))
                || types.isSubtype(element.asType(), getTypeMirror(Consts.FRAGMENT_X))
                || types.isSubtype(element.asType(), getTypeMirror(Consts.FRAGMENT_V4));
    }

}
