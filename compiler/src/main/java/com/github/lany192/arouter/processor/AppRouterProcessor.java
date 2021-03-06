package com.github.lany192.arouter.processor;

import static com.alibaba.android.arouter.facade.enums.TypeKind.PARCELABLE;
import static com.alibaba.android.arouter.facade.enums.TypeKind.SERIALIZABLE;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.enums.TypeKind;
import com.github.lany192.arouter.Consts;
import com.github.lany192.arouter.Logger;
import com.github.lany192.arouter.OtherUtils;
import com.github.lany192.arouter.TypeUtils;
import com.github.lany192.arouter.Utils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;

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

/**
 * @author Administrator
 */
@AutoService(Processor.class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.DYNAMIC)
public class AppRouterProcessor extends AbstractProcessor {
    private Logger logger;
    private Types types;
    private TypeMirror iProvider = null;
    private TypeUtils typeUtils;
    private final ClassName arouterClassName = ClassName.get("com.alibaba.android.arouter.launcher", "ARouter");
    private final ClassName routePathClassName = ClassName.get("com.alibaba.android.arouter", "RoutePath");

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        types = processingEnv.getTypeUtils();
        typeUtils = new TypeUtils(types, processingEnv.getElementUtils());
        logger = new Logger(processingEnv.getMessager());
        iProvider = processingEnv.getElementUtils().getTypeElement(Consts.IPROVIDER).asType();
        logger.info("?????????");
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
            logger.info("??????????????????");
            //???????????????????????????false??????????????????Processor???????????????????????????arouter???Processor???
            return false;
        }
        return true;
    }

    /**
     * ??????Activity??????
     */
    private MethodSpec skipActivity(Element element) {
        String simpleName = element.getSimpleName().toString().replace("Activity", "");
        String methodName = Utils.toLowerCaseFirstOne(simpleName);
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("?????????{@link " + ClassName.get((TypeElement) element) + "}");
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                builder.addParameter(OtherUtils.getParameter(field, autowired));
            }
        }
        builder.addCode("$T.builder()", ClassName.get(ClassName.get((TypeElement) element).packageName(), simpleName + "Router"));

        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                String fieldName = field.getSimpleName().toString();
                String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
                builder.addCode("." + Utils.line2hump(key) + "(" + key + ")");
            }
        }

        builder.addCode(".build();");
        builder.returns(void.class);
        return builder.build();
    }

    /**
     * ??????Provider????????????
     */
    private MethodSpec getProviderInstance(Element element) {
        String methodName = Utils.toLowerCaseFirstOne(element.getSimpleName().toString());
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("?????????{@link " + ClassName.get((TypeElement) element) + "}");
        Route route = element.getAnnotation(Route.class);
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                builder.addParameter(OtherUtils.getParameter(field, autowired));
            }
        }
        builder.addCode("$T.getInstance()", arouterClassName);
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
     * ??????Service????????????
     */
    private MethodSpec getServiceInstance(Element element) {
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder("get" + element.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("??????{@link " + ClassName.get((TypeElement) element) + "}");
        Route route = element.getAnnotation(Route.class);
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                builder.addParameter(OtherUtils.getParameter(field, autowired));
            }
        }
        builder.addCode("return (" + element.getSimpleName().toString() + ")$T.getInstance()", arouterClassName);
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
     * ??????Fragment????????????
     */
    private MethodSpec getFragmentInstance(Element element) {
        String simpleName = element.getSimpleName().toString().replace("Fragment", "");
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder("get" + simpleName)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("????????????{@link " + ClassName.get((TypeElement) element) + "}");

        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                builder.addParameter(OtherUtils.getParameter(field, autowired));
            }
        }
        builder.addCode("return $T.builder()", ClassName.get(ClassName.get((TypeElement) element).packageName(), simpleName + "Builder"));

        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                String fieldName = field.getSimpleName().toString();
                String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
                builder.addCode("." + Utils.line2hump(key) + "(" + key + ")");
            }
        }

        builder.addCode(".build();");
        builder.returns(ClassName.get((TypeElement) element));
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
            logger.info("??????:" + fieldName + " -> ????????????:" + name);
            return "\n.with" + name + "(\"" + key + "\"," + key + ")";
        } else {
            if (typeKind == SERIALIZABLE) {
                logger.info("??????:" + fieldName + " -> Serializable??????:" + name);
                return "\n.withSerializable(\"" + key + "\",(" + typeName + ")" + key + ")";
            } else if (typeKind == PARCELABLE) {
                logger.info("??????:" + fieldName + " -> Parcelable??????:" + name);
                return "\n.withParcelable(\"" + key + "\",(" + typeName + ")" + key + ")";
            } else {
                logger.info("??????:" + fieldName + " -> ????????????:" + name);
                if (typeName.startsWith("java.lang") || typeName.startsWith("java.util")) {
                    //???????????????
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

    private void createRouterHelper(List<MethodSpec> methods) throws Exception {
        TypeSpec.Builder builder = TypeSpec.classBuilder("AppRouter")
                .addJavadoc("????????????,????????????,????????????!")
                .addModifiers(Modifier.PUBLIC);

        ClassName routerType = ClassName.get("com.alibaba.android.arouter", "AppRouter");

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
                .addJavadoc("????????????")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec
                        .builder(String.class, "path")
                        .addJavadoc("????????????")
                        .build())
                .addStatement("ARouter.getInstance().build(path).navigation()")
                .returns(void.class)
                .build();
        builder.addMethod(skipMethodSpec);


        MethodSpec skipMethodSpec2 = MethodSpec.methodBuilder("skip")
                .addJavadoc("????????????")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec
                        .builder(String.class, "path")
                        .addJavadoc("????????????\n")
                        .build())
                .addParameter(ParameterSpec
                        .builder(ClassName.get("android.os", "Bundle"), "bundle")
                        .addJavadoc("Bundle??????")
                        .build())
                .addStatement("ARouter.getInstance().build(path).with(bundle).navigation()")
                .returns(void.class)
                .build();
        builder.addMethod(skipMethodSpec2);

        MethodSpec skipMethodSpec3 = MethodSpec.methodBuilder("skip")
                .addJavadoc("????????????")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec
                        .builder(ClassName.get("android.net", "Uri"), "uri")
                        .addJavadoc("????????????")
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
                // ??????????????????????????????
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
