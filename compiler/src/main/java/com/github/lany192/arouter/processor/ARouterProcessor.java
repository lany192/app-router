package com.github.lany192.arouter.processor;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.github.lany192.arouter.utils.Consts;
import com.github.lany192.arouter.utils.Utils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import static com.github.lany192.arouter.utils.Consts.ANNOTATION_TYPE_AUTOWIRED;
import static com.github.lany192.arouter.utils.Consts.ANNOTATION_TYPE_ROUTE;

@AutoService(Processor.class)
@SupportedAnnotationTypes({ANNOTATION_TYPE_ROUTE, ANNOTATION_TYPE_AUTOWIRED})
public class ARouterProcessor extends BaseProcessor {
    private Filer filer;
    private final Map<String, Set<RouteMeta>> groupMap = new HashMap<>(); // ModuleName and routeMeta.
    private TypeMirror iProvider = null;
    List<MethodSpec> methods = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        iProvider = elementUtils.getTypeElement(Consts.IPROVIDER).asType();
        filer = processingEnv.getFiler();
        logger.info(">>> 初始化 <<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Messager messager = processingEnv.getMessager();
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Route.class);
        if (CollectionUtils.isNotEmpty(elements)) {
            Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Route.class);
            try {
                for (Element element : routeElements) {
                    MethodSpec.Builder builder = MethodSpec
                            .methodBuilder(Utils.toLowerCaseFirstOne(element.getSimpleName().toString()))
                            .addModifiers(Modifier.PUBLIC)
                            .addJavadoc("跳转到" + element.getSimpleName());
                    Route route = element.getAnnotation(Route.class);
                    for (Element field : element.getEnclosedElements()) {
                        if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                            Autowired autowired = field.getAnnotation(Autowired.class);
                            logger.info("目标类:" + element.getSimpleName() + ",路径:" + route.path() + "字段名:" + field.getSimpleName() + " ,类型：" + field.asType().toString() + "，注释:" + autowired.desc());

                            switch (field.asType().toString()) {
                                case "java.lang.String":
                                    builder.addParameter(ParameterSpec
                                            .builder(String.class, field.getSimpleName().toString())
                                            .addJavadoc(autowired.desc() + "\n")
                                            .build());
                                    break;
                                case "boolean":
                                    builder.addParameter(ParameterSpec
                                            .builder(boolean.class, field.getSimpleName().toString())
                                            .addJavadoc(autowired.desc() + "\n")
                                            .build());
                                    break;
                                case "long":
                                    builder.addParameter(ParameterSpec
                                            .builder(long.class, field.getSimpleName().toString())
                                            .addJavadoc(autowired.desc() + "\n")
                                            .build());
                                    break;
                                default:
                                    builder.addParameter(ParameterSpec
                                            .builder(Object.class, field.getSimpleName().toString())
                                            .addJavadoc(autowired.desc() + "\n")
                                            .build());
                                    break;
                            }
                        }
                    }
                    builder.addCode("$T.getInstance()", ClassName.get("com.alibaba.android.arouter.launcher", "ARouter"));
                    builder.addCode(".build(\"" + route.path() + "\")");
                    for (Element field : element.getEnclosedElements()) {
                        if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                            Autowired autowired = field.getAnnotation(Autowired.class);
                            logger.info("目标类:" + element.getSimpleName() + ",路径:" + route.path() + "字段名:" + field.getSimpleName() + " ,类型：" + field.asType().toString() + "，注释:" + autowired.desc());
                            String fieldName = field.getSimpleName().toString();
                            switch (field.asType().toString()) {
                                case "java.lang.String":
                                    builder.addCode(".withString(\"" + fieldName + "\"," + fieldName + ")");
                                    break;
                                case "boolean":
                                    builder.addCode(".withBoolean(\"" + fieldName + "\"," + fieldName + ")");
                                    break;
                                case "long":
                                    builder.addCode(".withLong(\"" + fieldName + "\"," + fieldName + ")");
                                    break;
                                default:
                                    builder.addCode(".withObject(\"" + fieldName + "\"," + fieldName + ")");
                                    break;
                            }
                        }
                    }
                    builder.addCode(".navigation();");
                    builder.returns(void.class);
                    methods.add(builder.build());
                }
                createRouterHelper(methods);
            } catch (Exception e) {
                logger.error(e);
            }
            messager.printMessage(Diagnostic.Kind.WARNING, "忽略异常提示");
            //这里要注意，要返回false，并且要放在Processor的前面，否则会影响arouter的Processor。
            return false;
        }
        return true;
    }

    private void createRouterHelper(List<MethodSpec> methods) throws Exception {
        TypeSpec.Builder builder = TypeSpec.classBuilder("Router")
                .addJavadoc("路由助手，自动生成代码，请勿编辑")
                .addModifiers(Modifier.PUBLIC);

        ClassName routerType = ClassName.get("com.alibaba.android.arouter", "Router");


        FieldSpec fieldSpec = FieldSpec.builder(routerType, "instance", Modifier.VOLATILE, Modifier.STATIC, Modifier.PRIVATE).build();
        builder.addField(fieldSpec);

        builder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

        MethodSpec getMethodSpec = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .beginControlFlow("if (instance == null) ")
                .beginControlFlow("synchronized (Router.class) ")
                .beginControlFlow("if (instance == null) ")
                .addCode("instance = new Router();")
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

        for (MethodSpec method : methods) {
            builder.addMethod(method);
        }
        JavaFile javaFile = JavaFile.builder("com.alibaba.android.arouter", builder.build()).build();
        javaFile.writeTo(filer);
    }
}
