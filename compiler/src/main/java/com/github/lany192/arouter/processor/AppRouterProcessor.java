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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
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
    private Filer mFiler;
    private Logger logger;
    private Types types;
    private TypeMirror iProvider = null;
    private TypeUtils typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        types = processingEnv.getTypeUtils();
        typeUtils = new TypeUtils(types, processingEnv.getElementUtils());

        mFiler = processingEnv.getFiler();
        types = processingEnv.getTypeUtils();
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
                    String methodName = Utils.toLowerCaseFirstOne(element.getSimpleName().toString().replace("Activity", ""));
                    MethodSpec.Builder builder = MethodSpec
                            .methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC)
                            .addJavadoc("跳转到 " + ClassName.get((TypeElement) element));
                    Route route = element.getAnnotation(Route.class);
                    for (Element field : element.getEnclosedElements()) {
                        if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                            Autowired autowired = field.getAnnotation(Autowired.class);
//                        logger.info("目标类:" + element.getSimpleName() + ",路径:" + route.path() + "字段名:" + field.getSimpleName() + " ,类型：" + field.asType().toString() + "，注释:" + autowired.desc() + "，必选:" + autowired.required());
                            builder.addParameter(getParameter(field, autowired));
                        }
                    }
                    builder.addCode("$T.getInstance()", ClassName.get("com.alibaba.android.arouter.launcher", "ARouter"));
                    builder.addCode(".build(\"" + route.path() + "\")");
                    for (Element field : element.getEnclosedElements()) {
                        if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                            Autowired autowired = field.getAnnotation(Autowired.class);
                            builder.addCode(makeCode(field, autowired));
                        }
                    }
                    builder.addCode(".navigation();");
                    builder.returns(void.class);
                    methods.add(builder.build());
                } else if (isFragment(element)) { // Fragment
                    MethodSpec.Builder builder = MethodSpec
                            .methodBuilder("get" + element.getSimpleName().toString())
                            .addModifiers(Modifier.PUBLIC)
                            .addJavadoc("获取 " + ClassName.get((TypeElement) element));
                    Route route = element.getAnnotation(Route.class);
                    for (Element field : element.getEnclosedElements()) {
                        if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                            Autowired autowired = field.getAnnotation(Autowired.class);
                            builder.addParameter(getParameter(field, autowired));
                        }
                    }
                    builder.addCode("return (Fragment)$T.getInstance()", ClassName.get("com.alibaba.android.arouter.launcher", "ARouter"));
                    builder.addCode(".build(\"" + route.path() + "\")");
                    for (Element field : element.getEnclosedElements()) {
                        if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                            Autowired autowired = field.getAnnotation(Autowired.class);
                            builder.addCode(makeCode(field, autowired));
                        }
                    }
                    builder.addCode(".navigation();");
                    if (types.isSubtype(element.asType(), getTypeMirror(Consts.FRAGMENT_X))) {
                        builder.returns(ClassName.get("androidx.fragment.app", "Fragment"));
                    } else if (types.isSubtype(element.asType(), getTypeMirror(Consts.FRAGMENT_V4))) {
                        builder.returns(ClassName.get("android.support.v4.app", "Fragment"));
                    } else {
                        builder.returns(ClassName.get("android.app", "Fragment"));
                    }
                    methods.add(builder.build());
                } else if (types.isSubtype(element.asType(), iProvider)) {// IProvider
                    logger.info(">>> Found provider route: " + element.asType().toString() + " <<<");
                    String methodName = Utils.toLowerCaseFirstOne(element.getSimpleName().toString());
                    MethodSpec.Builder builder = MethodSpec
                            .methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC)
                            .addJavadoc("跳转到 " + ClassName.get((TypeElement) element));
                    Route route = element.getAnnotation(Route.class);
                    for (Element field : element.getEnclosedElements()) {
                        if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                            Autowired autowired = field.getAnnotation(Autowired.class);
                            builder.addParameter(getParameter(field, autowired));
                        }
                    }
                    builder.addCode("$T.getInstance()", ClassName.get("com.alibaba.android.arouter.launcher", "ARouter"));
                    builder.addCode(".build(\"" + route.path() + "\")");
                    for (Element field : element.getEnclosedElements()) {
                        if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                            Autowired autowired = field.getAnnotation(Autowired.class);
                            builder.addCode(makeCode(field, autowired));
                        }
                    }
                    builder.addCode(".navigation();");
                    builder.returns(void.class);
                    methods.add(builder.build());
                } else if (types.isSubtype(element.asType(), getTypeMirror(Consts.SERVICE))) {// Service
                    logger.info(">>> Found service route: " + element.asType().toString() + " <<<");
                    MethodSpec.Builder builder = MethodSpec
                            .methodBuilder("get" + element.getSimpleName().toString())
                            .addModifiers(Modifier.PUBLIC)
                            .addJavadoc("获取 " + ClassName.get((TypeElement) element));
                    Route route = element.getAnnotation(Route.class);
                    for (Element field : element.getEnclosedElements()) {
                        if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                            Autowired autowired = field.getAnnotation(Autowired.class);
                            builder.addParameter(getParameter(field, autowired));
                        }
                    }
                    builder.addCode("return ("+element.getSimpleName().toString()+")$T.getInstance()", ClassName.get("com.alibaba.android.arouter.launcher", "ARouter"));
                    builder.addCode(".build(\"" + route.path() + "\")");
                    for (Element field : element.getEnclosedElements()) {
                        if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                            Autowired autowired = field.getAnnotation(Autowired.class);
                            builder.addCode(makeCode(field, autowired));
                        }
                    }
                    builder.addCode(".navigation();");
                    builder.returns(TypeName.get(element.asType()));
                    methods.add(builder.build());
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

    private String makeCode(Element field, Autowired autowired) {
        String fieldName = field.getSimpleName().toString();
        String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
        String typeName = field.asType().toString();
        TypeMirror typeMirror = field.asType();
        TypeKind typeKind = TypeKind.values()[typeUtils.typeExchange(field)];

        String name = Utils.toUpperCaseFirstOne(typeName);
        logger.info("字段:" + fieldName + "," + name);
        if (typeMirror.getKind().isPrimitive()) {
            return ".with" + name + "(\"" + key + "\"," + key + ")";
        } else {
            if (typeKind == SERIALIZABLE) {
                return ".withSerializable(\"" + key + "\"," + key + ")";
            } else if (typeKind == PARCELABLE) {
                return ".withParcelable(\"" + key + "\"," + key + ")";
            } else {
                //是否是泛型
                if (!typeName.contains("<") && typeName.contains(".")) {
                    int index = typeName.lastIndexOf(".");
                    name = Utils.toUpperCaseFirstOne(typeName.substring(index + 1));
                    return ".with" + name + "(\"" + key + "\"," + key + ")";
                } else {
                    return ".withObject(\"" + key + "\"," + key + ")";
                }
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


        FieldSpec fieldSpec = FieldSpec.builder(routerType, "instance", Modifier.VOLATILE, Modifier.STATIC, Modifier.PRIVATE).build();
        builder.addField(fieldSpec);

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

        for (MethodSpec method : methods) {
            builder.addMethod(method);
        }
        JavaFile javaFile = JavaFile
                .builder("com.alibaba.android.arouter", builder.build())
                // 设置表示缩进的字符串
                .indent("    ")
                .build();
        javaFile.writeTo(mFiler);
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