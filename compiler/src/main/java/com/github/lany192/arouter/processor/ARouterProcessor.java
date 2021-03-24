package com.github.lany192.arouter.processor;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.enums.TypeKind;
import com.github.lany192.arouter.utils.Consts;
import com.github.lany192.arouter.utils.TypeUtils;
import com.github.lany192.arouter.utils.Utils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
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
    private TypeMirror iProvider = null;

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
            List<MethodSpec> methods = new ArrayList<>();
            for (Element element : routeElements) {
                String methodName = Utils.toLowerCaseFirstOne(element.getSimpleName().toString().replace("Activity", ""));
                MethodSpec.Builder builder = MethodSpec
                        .methodBuilder(methodName)
                        .addModifiers(Modifier.PUBLIC)
                        .addJavadoc("跳转到" + element.getSimpleName());
                Route route = element.getAnnotation(Route.class);
                for (Element field : element.getEnclosedElements()) {
                    if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                        Autowired autowired = field.getAnnotation(Autowired.class);
                        logger.info("目标类:" + element.getSimpleName() + ",路径:" + route.path() + "字段名:" + field.getSimpleName() + " ,类型：" + field.asType().toString() + "，注释:" + autowired.desc() + "，必选:" + autowired.required());
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
            }
            try {
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

    private String makeCode(Element field, Autowired autowired) {
        String fieldName = field.getSimpleName().toString();
        String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();

        logger.info(field.asType().toString());

        switch (field.asType().toString()) {
            case "java.lang.String":
                return ".withString(\"" + key + "\"," + fieldName + ")";
            case "boolean":
                return ".withBoolean(\"" + key + "\"," + fieldName + ")";
            case "long":
                return ".withLong(\"" + key + "\"," + fieldName + ")";
            case "int":
                return ".withInt(\"" + key + "\"," + fieldName + ")";
            case "float":
                return ".withFloat(\"" + key + "\"," + fieldName + ")";
            case "double":
                return ".withDouble(\"" + key + "\"," + fieldName + ")";
            case "char":
                return ".withChar(\"" + key + "\"," + fieldName + ")";
            case "byte":
                return ".withByte(\"" + key + "\"," + fieldName + ")";
            case "java.lang.CharSequence":
                return ".withCharSequence(\"" + key + "\"," + fieldName + ")";
            default:
                return ".withObject(\"" + key + "\"," + fieldName + ")";
        }
    }

    private ParameterSpec getParameter(Element field, Autowired autowired) {
        ParameterSpec.Builder builder;
        switch (field.asType().toString()) {
            case "java.lang.String":
                builder = ParameterSpec.builder(String.class, field.getSimpleName().toString());
                break;
            case "boolean":
                builder = ParameterSpec.builder(boolean.class, field.getSimpleName().toString());
                break;
            case "long":
                builder = ParameterSpec.builder(long.class, field.getSimpleName().toString());
                break;
            case "int":
                builder = ParameterSpec.builder(int.class, field.getSimpleName().toString());
                break;
            case "float":
                builder = ParameterSpec.builder(float.class, field.getSimpleName().toString());
                break;
            case "double":
                builder = ParameterSpec.builder(double.class, field.getSimpleName().toString());
                break;
            case "char":
                builder = ParameterSpec.builder(char.class, field.getSimpleName().toString());
                break;
            case "byte":
                builder = ParameterSpec.builder(byte.class, field.getSimpleName().toString());
                break;
            case "java.lang.CharSequence":
                builder = ParameterSpec.builder(CharSequence.class, field.getSimpleName().toString());
                break;
            default:
                builder = ParameterSpec.builder(Object.class, field.getSimpleName().toString());
                break;
        }
        return builder.addJavadoc(autowired.desc() + "\n").build();
    }

    private void createRouterHelper(List<MethodSpec> methods) throws Exception {
        TypeSpec.Builder builder = TypeSpec.classBuilder("Router")
                .addJavadoc("路由助手,自动生成,请勿编辑!")
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

        for (MethodSpec method : methods) {
            builder.addMethod(method);
        }
        JavaFile javaFile = JavaFile.builder("com.alibaba.android.arouter", builder.build()).build();
        javaFile.writeTo(filer);
    }
}
