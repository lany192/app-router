package com.github.lany192.arouter.processor;

import static com.alibaba.android.arouter.facade.enums.TypeKind.PARCELABLE;
import static com.alibaba.android.arouter.facade.enums.TypeKind.SERIALIZABLE;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.enums.TypeKind;
import com.github.lany192.arouter.utils.Consts;
import com.github.lany192.arouter.utils.Logger;
import com.github.lany192.arouter.utils.TypeUtils;
import com.github.lany192.arouter.utils.Utils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;
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
public class PostcardProcessor extends AbstractProcessor {
    private Logger logger;
    private Types types;
    private TypeMirror iProvider = null;
    private TypeUtils typeUtils;
    private final ClassName routerClassName = ClassName.get("com.alibaba.android.arouter.launcher", "ARouter");
    private final ClassName routePathClassName = ClassName.get("com.alibaba.android.arouter", "RoutePath");
    private final ClassName returnClassName = ClassName.get("com.alibaba.android.arouter.facade", "Postcard");

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
            for (Element element : routeElements) {
                if (isActivity(element)) { // Activity
                    try {
                        MethodSpec methodSpec = createPostcard(element);
                        createRouterHelper(element, methodSpec);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                    logger.info("忽略异常提示");
                    //这里要注意，要返回false，并且要放在Processor的前面，否则会影响arouter的Processor。
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 跳转Activity方法
     */
    private MethodSpec createPostcard(Element element) {
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder("getPostcard")
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
        builder.addCode("return $T.getInstance()", routerClassName);
        builder.addCode(".build($T." + route.path().replace("/", "_").toUpperCase().substring(1) + ")", routePathClassName);
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                builder.addCode(makeCode(field, autowired));
            }
        }
        builder.addCode(";");
//        builder.addCode("\n.navigation();");
        builder.returns(returnClassName);
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

    private void createRouterHelper(Element element, MethodSpec methodSpec) throws Exception {
        TypeSpec.Builder builder = TypeSpec.classBuilder(element.getSimpleName() + "$$ARouter$$Postcard")
                .addJavadoc("自动生成,请勿编辑!")
                .addModifiers(Modifier.PUBLIC);
        builder.addMethod(methodSpec);

        JavaFile javaFile = JavaFile
                .builder(ClassName.get((TypeElement) element).packageName(), builder.build())
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
}
