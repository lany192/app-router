//package com.alibaba.android.arouter.compiler.processor;
//
//import com.alibaba.android.arouter.compiler.utils.Constants;
//import com.alibaba.android.arouter.compiler.utils.OtherUtils;
//import com.alibaba.android.arouter.compiler.utils.TypeUtils;
//import com.alibaba.android.arouter.facade.annotation.Autowired;
//import com.alibaba.android.arouter.facade.annotation.Route;
//import com.google.auto.service.AutoService;
//
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.LinkedHashSet;
//import java.util.Set;
//
//import javax.annotation.processing.ProcessingEnvironment;
//import javax.annotation.processing.Processor;
//import javax.annotation.processing.RoundEnvironment;
//import javax.lang.model.element.Element;
//import javax.lang.model.element.TypeElement;
//import javax.lang.model.type.TypeMirror;
//
///**
// * @author lany192
// */
//@AutoService(Processor.class)
////@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
//public class JsDocProcessor extends BaseProcessor {
//    private TypeMirror iProvider = null;
//    private String jsUseDoc;
//    private String routeTestDoc;
//    private String jsH5TestDoc;
//
//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnv) {
//        super.init(processingEnv);
//        types = processingEnv.getTypeUtils();
//        typeUtils = new TypeUtils(types, processingEnv.getElementUtils());
//        iProvider = processingEnv.getElementUtils().getTypeElement(Constants.IPROVIDER).asType();
//    }
//
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        Set<String> set = new LinkedHashSet<>();
//        set.add(Route.class.getCanonicalName());
//        set.add(Autowired.class.getCanonicalName());
//        return set;
//    }
//
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Route.class);
//        if (elements != null && !elements.isEmpty()) {
//            logger.info("\n\n" + moduleName + "模块文档：");
//            int index = 1;
//            for (Element element : elements) {
//                if (isActivity(element)) { // Activity
//                    jsUseDoc += getUseDoc(element, index);
//                    index++;
//                }
//            }
//            logger.info(jsUseDoc);
//            logger.info("JS测试用例：\n" + jsH5TestDoc);
//            logger.info("协议测试用例：\n" + routeTestDoc);
//            //这里要注意，要返回false，并且要放在Processor的前面，否则会影响arouter的Processor。
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 生成使用文档
//     */
//    public String getUseDoc(Element element, int index) {
//        Route route = element.getAnnotation(Route.class);
//        String doc = "\n\n### " + index + ". ";
//        if (!StringUtils.isEmpty(route.name())) {
//            doc += route.name() + "\n";
//        } else {
//            doc += "未定义\n";
//        }
//        StringBuilder uri = new StringBuilder(route.path());
//        StringBuilder parameter = new StringBuilder("参数说明:\n");
//        parameter.append("\n").append("| 名称 | 类型 | 必选 | 说明 |");
//        parameter.append("\n").append("|:----:|:----:|:----:|:----:|");
//        boolean isFirst = true;
//        boolean hasParameter = false;
//        for (Element field : element.getEnclosedElements()) {
//            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
//                hasParameter = true;
//                Autowired autowired = field.getAnnotation(Autowired.class);
//                String fieldName = field.getSimpleName().toString();
//                String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();
//
//                if (isFirst) {
//                    isFirst = false;
//                    uri.append("?").append(key).append("=xxx");
//                } else {
//                    uri.append("&").append(key).append("=xxx");
//                }
//                parameter.append("\n|").append(key).append(" | ").append(OtherUtils.getParameterType(field)).append(" | ").append(autowired.required() ? "是" : "否").append(" |").append(autowired.desc()).append(" | ");
//            }
//        }
//        String scheme = getValue(Constants.ROUTER_SCHEME);
//        String jsFun = getValue(Constants.ROUTER_JS_FUN);
//
//        doc = doc + "\n路由协议:\n```\n" + scheme + "://" + uri + "\n```";
//        doc = doc + "\nJS调用:\n```\n" + jsFun + "('" + uri + "');\n```";
//        if (hasParameter) {
//            doc = doc + "\n" + parameter;
//        }
//
//        routeTestDoc += "\n<li><a href=\"" + scheme + "://" + uri + "\">" + index + "." + route.name() + "</a></li>";
//
//        jsH5TestDoc += "\n<li><a href=\"javascript:" + jsFun + "('" + uri + "');\">" + index + "." + route.name() + "</a></li>";
//
//        return doc;
//    }
//
//    private TypeMirror getTypeMirror(String name) {
//        return processingEnv.getElementUtils().getTypeElement(name).asType();
//    }
//
//    private boolean isActivity(Element element) {
//        return types.isSubtype(element.asType(), getTypeMirror(Constants.ACTIVITY));
//    }
//}
