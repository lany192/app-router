package com.github.lany192.arouter.processor;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.enums.TypeKind;
import com.alibaba.android.arouter.facade.model.RouteMeta;
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
        switch (TypeKind.values()[typeUtils.typeExchange(field)]) {
            case BOOLEAN:
                return ".withBoolean(\"" + key + "\"," + key + ")";
            case BYTE:
                return ".withByte(\"" + key + "\"," + key + ")";
            case SHORT:
                return ".withShort(\"" + key + "\"," + key + ")";
            case INT:
                return ".withInt(\"" + key + "\"," + key + ")";
            case LONG:
                return ".withLong(\"" + key + "\"," + key + ")";
            case CHAR:
                return ".withChar(\"" + key + "\"," + key + ")";
            case FLOAT:
                return ".withFloat(\"" + key + "\"," + key + ")";
            case DOUBLE:
                return ".withDouble(\"" + key + "\"," + key + ")";
            case STRING:
                return ".withString(\"" + key + "\"," + key + ")";
            case SERIALIZABLE:
                return ".withSerializable(\"" + key + "\"," + key + ")";
            case PARCELABLE:
                return ".withParcelable(\"" + key + "\"," + key + ")";
            default:
                return ".withObject(\"" + key + "\"," + key + ")";
        }
    }

    private ParameterSpec getParameter(Element field, Autowired autowired) {
        String fieldName = field.getSimpleName().toString();
        String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
        String typeName = field.asType().toString();
        ParameterSpec.Builder builder;
        switch (typeName) {
            case "java.lang.String":
                builder = ParameterSpec.builder(String.class, key);
                break;
            case "boolean":
                builder = ParameterSpec.builder(boolean.class, key);
                break;
            case "long":
                builder = ParameterSpec.builder(long.class, key);
                break;
            case "int":
                builder = ParameterSpec.builder(int.class, key);
                break;
            case "float":
                builder = ParameterSpec.builder(float.class, key);
                break;
            case "double":
                builder = ParameterSpec.builder(double.class, key);
                break;
            case "char":
                builder = ParameterSpec.builder(char.class, key);
                break;
            case "byte":
                builder = ParameterSpec.builder(byte.class, key);
                break;
            case "short":
                builder = ParameterSpec.builder(short.class, key);
                break;
            case "java.lang.CharSequence":
                builder = ParameterSpec.builder(CharSequence.class, key);
                break;
            default:
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
                break;
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
}
