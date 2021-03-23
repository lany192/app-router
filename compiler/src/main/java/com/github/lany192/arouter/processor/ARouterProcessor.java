package com.github.lany192.arouter.processor;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.enums.RouteType;
import com.alibaba.android.arouter.facade.enums.TypeKind;
import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.lany192.arouter.entity.Param;
import com.github.lany192.arouter.entity.RouteDoc;
import com.github.lany192.arouter.utils.Consts;
import com.github.lany192.arouter.utils.Utils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import static com.github.lany192.arouter.utils.Consts.ACTIVITY;
import static com.github.lany192.arouter.utils.Consts.ANNOTATION_TYPE_AUTOWIRED;
import static com.github.lany192.arouter.utils.Consts.ANNOTATION_TYPE_ROUTE;
import static com.github.lany192.arouter.utils.Consts.FRAGMENT;
import static com.github.lany192.arouter.utils.Consts.SERVICE;

@AutoService(Processor.class)
@SupportedAnnotationTypes({ANNOTATION_TYPE_ROUTE, ANNOTATION_TYPE_AUTOWIRED})
public class ARouterProcessor extends BaseProcessor {
    private Filer filer;
    private final Map<String, Set<RouteMeta>> groupMap = new HashMap<>(); // ModuleName and routeMeta.
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
            List<MethodSpec> methods = new ArrayList<>();
            for (Element element : elements) {
                if (element instanceof VariableElement) {
                    messager.printMessage(Diagnostic.Kind.WARNING, "忽略注解在非class上的注解");
                    return false;
                }
                //获取包信息
                PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
                String className = element.getSimpleName().toString();
                messager.printMessage(Diagnostic.Kind.NOTE, " 发现目标类: " + packageElement.getQualifiedName() + "." + className);
                Route route = element.getAnnotation(Route.class);

//                /**
//                 * 支付确认界面
//                 */
//                public void pay(OrderType orderType, TargetType targetType, long targetId) {
//                    ARouter.getInstance()
//                            .build(Constants.USER_PAY)
//                            .withInt(Constants.KEY_ORDER_TYPE, orderType.ordinal())
//                            .withInt(Constants.KEY_TARGET_TYPE, targetType.ordinal())
//                            .withLong(Constants.KEY_TARGET_ID, targetId)
//                            .navigation();
//                }
                MethodSpec methodSpec = MethodSpec.methodBuilder(Utils.toLowerCaseFirstOne(className))
                        .addModifiers(Modifier.PUBLIC)
                        .addJavadoc("这里是注释" + route.group())
//                        .addParameter(String[].class, "args")
                        .addCode("$T.getInstance()", ClassName.get("com.alibaba.android.arouter.launcher", "ARouter"))
                        .addCode(".build(\"" + route.path() + "\")")
                        .addCode(".navigation();")
                        .returns(void.class)
                        .build();
                methods.add(methodSpec);
            }
            try {
                createRouterHelper(methods);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (CollectionUtils.isNotEmpty(annotations)) {
                Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Route.class);
                try {
                    this.parseRoutes(routeElements);
                } catch (Exception e) {
                    logger.error(e);
                }
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

        MethodSpec methodSpec = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addCode("if (instance == null) {")
                .addCode("synchronized (Router.class) {")
                .addCode("if (instance == null) {")
                .addCode("instance = new Router();")
                .addCode("}")
                .addCode("}")
                .addCode("}")
                .addCode("return instance;")
                .returns(routerType)
                .build();
        builder.addMethod(methodSpec);
        for (MethodSpec method : methods) {
            builder.addMethod(method);
        }
        JavaFile javaFile = JavaFile.builder("com.alibaba.android.arouter", builder.build()).build();
        javaFile.writeTo(filer);
    }

    private void parseRoutes(Set<? extends Element> routeElements) throws IOException {
        if (CollectionUtils.isNotEmpty(routeElements)) {
            // prepare the type an so on.

            logger.info(">>> Found routes, size is " + routeElements.size() + " <<<");

            TypeMirror type_Activity = elementUtils.getTypeElement(ACTIVITY).asType();
            TypeMirror type_Service = elementUtils.getTypeElement(SERVICE).asType();
            TypeMirror fragmentTm = elementUtils.getTypeElement(FRAGMENT).asType();
            TypeMirror fragmentTmV4 = elementUtils.getTypeElement(Consts.FRAGMENT_V4).asType();

            //  Follow a sequence, find out metas of group first, generate java file, then statistics them as root.
            for (Element element : routeElements) {
                TypeMirror tm = element.asType();
                Route route = element.getAnnotation(Route.class);
                RouteMeta routeMeta;

                // Activity or Fragment
                if (types.isSubtype(tm, type_Activity) || types.isSubtype(tm, fragmentTm) || types.isSubtype(tm, fragmentTmV4)) {
                    // Get all fields annotation by @Autowired
                    Map<String, Integer> paramsType = new HashMap<>();
                    Map<String, Autowired> injectConfig = new HashMap<>();
                    injectParamCollector(element, paramsType, injectConfig);

                    if (types.isSubtype(tm, type_Activity)) {
                        // Activity
                        logger.info(">>> Found activity route: " + tm.toString() + " <<<");
                        routeMeta = new RouteMeta(route, element, RouteType.ACTIVITY, paramsType);
                    } else {
                        // Fragment
                        logger.info(">>> Found fragment route: " + tm.toString() + " <<<");
                        routeMeta = new RouteMeta(route, element, RouteType.parse(FRAGMENT), paramsType);
                    }

                    routeMeta.setInjectConfig(injectConfig);
                } else if (types.isSubtype(tm, iProvider)) {         // IProvider
                    logger.info(">>> Found provider route: " + tm.toString() + " <<<");
                    routeMeta = new RouteMeta(route, element, RouteType.PROVIDER, null);
                } else if (types.isSubtype(tm, type_Service)) {           // Service
                    logger.info(">>> Found service route: " + tm.toString() + " <<<");
                    routeMeta = new RouteMeta(route, element, RouteType.parse(SERVICE), null);
                } else {
                    throw new RuntimeException("The @Route is marked on unsupported class, look at [" + tm.toString() + "].");
                }
                categories(routeMeta);
            }
            Map<String, List<RouteDoc>> docSource = new HashMap<>();

            // Start generate java source, structure is divided into upper and lower levels, used for demand initialization.
            for (Map.Entry<String, Set<RouteMeta>> entry : groupMap.entrySet()) {
                String groupName = entry.getKey();

                List<RouteDoc> routeDocList = new ArrayList<>();

                // Build group method body
                Set<RouteMeta> groupData = entry.getValue();
                for (RouteMeta routeMeta : groupData) {
                    RouteDoc routeDoc = extractDocInfo(routeMeta);

                    ClassName className = ClassName.get((TypeElement) routeMeta.getRawType());

                    switch (routeMeta.getType()) {
                        case PROVIDER:  // Need cache provider's super class
                            List<? extends TypeMirror> interfaces = ((TypeElement) routeMeta.getRawType()).getInterfaces();
                            for (TypeMirror tm : interfaces) {
                                routeDoc.addPrototype(tm.toString());
                            }
                            break;
                        default:
                            break;
                    }

                    // Make map body for paramsType
                    Map<String, Integer> paramsType = routeMeta.getParamsType();
                    Map<String, Autowired> injectConfigs = routeMeta.getInjectConfig();
                    if (MapUtils.isNotEmpty(paramsType)) {
                        List<Param> paramList = new ArrayList<>();

                        for (Map.Entry<String, Integer> types : paramsType.entrySet()) {

                            Param param = new Param();
                            Autowired injectConfig = injectConfigs.get(types.getKey());
                            param.setKey(types.getKey());
                            param.setType(TypeKind.values()[types.getValue()].name().toLowerCase());
                            param.setDescription(injectConfig.desc());
                            param.setRequired(injectConfig.required());

                            paramList.add(param);
                        }

                        routeDoc.setParams(paramList);
                    }

                    routeDoc.setClassName(className.toString());
                    routeDocList.add(routeDoc);
                }
                docSource.put(groupName, routeDocList);
            }

            String json = JSON.toJSONString(docSource, SerializerFeature.PrettyFormat);
            logger.info(json);
        }
    }

    /**
     * Recursive inject config collector.
     *
     * @param element current element.
     */
    private void injectParamCollector(Element element, Map<String, Integer> paramsType, Map<String, Autowired> injectConfig) {
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                // It must be field, then it has annotation, but it not be provider.
                Autowired paramConfig = field.getAnnotation(Autowired.class);
                String injectName = StringUtils.isEmpty(paramConfig.name()) ? field.getSimpleName().toString() : paramConfig.name();
                paramsType.put(injectName, typeUtils.typeExchange(field));
                injectConfig.put(injectName, paramConfig);
            }
        }

        // if has parent?
        TypeMirror parent = ((TypeElement) element).getSuperclass();
        if (parent instanceof DeclaredType) {
            Element parentElement = ((DeclaredType) parent).asElement();
            if (parentElement instanceof TypeElement && !((TypeElement) parentElement).getQualifiedName().toString().startsWith("android")) {
                injectParamCollector(parentElement, paramsType, injectConfig);
            }
        }
    }

    /**
     * Extra doc info from route meta
     *
     * @param routeMeta meta
     * @return doc
     */
    private RouteDoc extractDocInfo(RouteMeta routeMeta) {
        RouteDoc routeDoc = new RouteDoc();
        routeDoc.setGroup(routeMeta.getGroup());
        routeDoc.setPath(routeMeta.getPath());
        routeDoc.setDescription(routeMeta.getName());
        routeDoc.setType(routeMeta.getType().name().toLowerCase());
        routeDoc.setMark(routeMeta.getExtra());

        return routeDoc;
    }

    /**
     * Sort metas in group.
     *
     * @param routeMete metas.
     */
    private void categories(RouteMeta routeMete) {
        if (routeVerify(routeMete)) {
            logger.info(">>> Start categories, group = " + routeMete.getGroup() + ", path = " + routeMete.getPath() + " <<<");
            Set<RouteMeta> routeMetas = groupMap.get(routeMete.getGroup());
            if (CollectionUtils.isEmpty(routeMetas)) {
                Set<RouteMeta> routeMetaSet = new TreeSet<>(new Comparator<RouteMeta>() {
                    @Override
                    public int compare(RouteMeta r1, RouteMeta r2) {
                        try {
                            return r1.getPath().compareTo(r2.getPath());
                        } catch (NullPointerException npe) {
                            logger.error(npe.getMessage());
                            return 0;
                        }
                    }
                });
                routeMetaSet.add(routeMete);
                groupMap.put(routeMete.getGroup(), routeMetaSet);
            } else {
                routeMetas.add(routeMete);
            }
        } else {
            logger.warning(">>> Route meta verify error, group is " + routeMete.getGroup() + " <<<");
        }
    }

    /**
     * Verify the route meta
     *
     * @param meta raw meta
     */
    private boolean routeVerify(RouteMeta meta) {
        String path = meta.getPath();
        if (StringUtils.isEmpty(path) || !path.startsWith("/")) {   // The path must be start with '/' and not empty!
            return false;
        }
        if (StringUtils.isEmpty(meta.getGroup())) { // Use default group(the first word in path)
            try {
                String defaultGroup = path.substring(1, path.indexOf("/", 1));
                if (StringUtils.isEmpty(defaultGroup)) {
                    return false;
                }

                meta.setGroup(defaultGroup);
                return true;
            } catch (Exception e) {
                logger.error("Failed to extract default group! " + e.getMessage());
                return false;
            }
        }

        return true;
    }
}
