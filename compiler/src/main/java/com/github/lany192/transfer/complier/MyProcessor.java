package com.github.lany192.transfer.complier;


import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    private static final String PACKAGE_NAME = "com.github.lany192.arouter";
    private Filer filer;
    private Map<String, UIEntity> map;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        map = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Messager messager = processingEnv.getMessager();
        for (Element routeElement : roundEnvironment.getElementsAnnotatedWith(Route.class)) {
            Route route = routeElement.getAnnotation(Route.class);
            messager.printMessage(Diagnostic.Kind.NOTE, route.name() + " --> " + route.group() + " --> " + route.path());
            String packageName = processingEnv.getElementUtils().getPackageOf(routeElement).getQualifiedName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, packageName);
            String className = routeElement.getEnclosingElement().getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, className);
            String fieldType = routeElement.asType().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, fieldType);

            for (Element element : roundEnvironment.getElementsAnnotatedWith(Autowired.class)) {
                if (element instanceof VariableElement) {
                    VariableElement variableElement = (VariableElement) element;
                    messager.printMessage(Diagnostic.Kind.NOTE, variableElement.getSimpleName());
                    messager.printMessage(Diagnostic.Kind.NOTE, variableElement.asType().toString());
//                    messager.printMessage(Diagnostic.Kind.NOTE, element.getAnnotationsByType(Autowired.class).getClass().getSimpleName());


                    Autowired autowired = routeElement.getAnnotation(Autowired.class);

                }
            }

            messager.printMessage(Diagnostic.Kind.NOTE, "---------------------------------------------------------------");
        }


//        for (Element element : roundEnvironment.getElementsAnnotatedWith(Autowired.class)) {
//            if (!(element instanceof VariableElement)) {
//                return false;
//            }
//            getEachVariableElement(element);
//        }
//        try {
//            createUIHelper();
//            createInjectors();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return true;
    }

    private void getEachVariableElement(Element element) {
        VariableElement variableElement = (VariableElement) element;
        String packageName = processingEnv.getElementUtils().getPackageOf(variableElement).getQualifiedName().toString();
        String fieldName = variableElement.getSimpleName().toString();
        String fieldType = variableElement.asType().toString();
        String className = variableElement.getEnclosingElement().getSimpleName().toString();
        Autowired annotation = element.getAnnotation(Autowired.class);
        String fieldValue = annotation.name().isEmpty() ? fieldName : annotation.name();
        String canonicalClassName = packageName + "." + className;
        UIEntity entity;
        if (map.get(canonicalClassName) == null) {
            entity = new UIEntity();
            entity.setPackageName(packageName);
            entity.setClassName(className);
            map.put(canonicalClassName, entity);
        } else {
            entity = map.get(canonicalClassName);
        }
        if (fieldType.contains("<") && fieldType.contains(">")) {
            int startIndex = fieldType.indexOf("<");
            int endIndex = fieldType.indexOf(">");
            String class1 = fieldType.substring(0, startIndex);
            String class2 = fieldType.substring(startIndex + 1, endIndex);
            Field field = new Field();
            field.setName(fieldName);
            field.setValue(fieldValue);
            field.setType(class1);
            field.setParam(class2);
            entity.addField(field);
        } else {
            String[] typeArray = {
                    "boolean", "boolean[]",
                    "byte", "byte[]",
                    "short", "short[]",
                    "int", "int[]",
                    "long", "long[]",
                    "double", "double[]",
                    "float", "float[]",
                    "char", "char[]",
                    "java.lang.CharSequence", "java.lang.CharSequence[]",
                    "java.lang.String", "java.lang.String[]",
                    "android.os.Bundle"
            };
            if (Arrays.asList(typeArray).contains(fieldType)) {
                entity.addField(new Field(fieldName, fieldType, fieldValue));
            } else {
                String type = fieldType.contains("[]") ? "android.os.Parcelable[]" : "android.os.Parcelable";
                Field field = new Field(fieldName, type, fieldValue);
                field.setOriginalType(fieldType.replace("[]", ""));
                entity.addField(field);
            }
        }
    }

    private void createUIHelper() throws Exception {
        List<TypeSpec> targetActivitiesClassList = new LinkedList<>();
        List<MethodSpec> goToActivitiesMethodList = new LinkedList<>();
        for (Map.Entry<String, UIEntity> entry : map.entrySet()) {
            String className = entry.getValue().getClassName();
            String fullClassName = entry.getKey();
            List<MethodSpec> targetActivitiesMethodList = new LinkedList<>();
            for (Field field : entry.getValue().getFields()) {
                String methodName = "set" + field.getValue().substring(0, 1).toUpperCase() + field.getValue().substring(1, field.getValue().length());
                String paramName = "";
                if (field.getParam().length() > 0) {
                    String paramSimpleName = field.getParam().substring(field.getParam().lastIndexOf(".") + 1, field.getParam().length());
                    switch (paramSimpleName) {
                        case "Integer":
                            paramName = "IntegerArrayList";
                            break;
                        case "String":
                            paramName = "StringArrayList";
                            break;
                        case "CharSequence":
                            paramName = "CharSequenceArrayList";
                            break;
                        default:
                            paramName = "ParcelableArrayList";
                            break;
                    }
                }
                MethodSpec method = MethodSpec.methodBuilder(methodName)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(getFieldType(field), field.getValue() + "Extra")
                        .returns(ClassName.get(PACKAGE_NAME, "UIHelper", "To" + className))
                        .addStatement("intent.put$LExtra($S, $L)", paramName, field.getValue(), field.getValue() + "Extra")
                        .addStatement("return this")
                        .build();
                targetActivitiesMethodList.add(method);
            }

            //UIHelper里GoToXXXActivity类的start()
            MethodSpec start = MethodSpec.methodBuilder("start")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("UIHelper.start($T.class)", ClassName.bestGuess(fullClassName))
                    .build();

            MethodSpec startForResult = MethodSpec.methodBuilder("start")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(int.class, "requestCode")
                    .addStatement("UIHelper.start($T.class, requestCode)", ClassName.bestGuess(fullClassName))
                    .build();

            MethodSpec constructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .build();

            TypeSpec type = TypeSpec.classBuilder("To" + className)
                    .addModifiers(Modifier.FINAL, Modifier.STATIC, Modifier.PUBLIC)
                    .addMethod(constructor)
                    .addMethods(targetActivitiesMethodList)
                    .addMethod(start)
                    .addMethod(startForResult)
                    .build();

            MethodSpec method = MethodSpec.methodBuilder("to" + className)
                    .addModifiers(Modifier.PUBLIC)
                    .addJavadoc("@see 跳转到$T\n", ClassName.bestGuess(fullClassName))
                    .returns(ClassName.get(PACKAGE_NAME, "UIHelper", "To" + className))
                    .addStatement("return new $T()", ClassName.get(PACKAGE_NAME, "UIHelper", "To" + className))
                    .build();

            targetActivitiesClassList.add(type);
            goToActivitiesMethodList.add(method);
        }
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
        MethodSpec addFlags = MethodSpec.methodBuilder("addFlags")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.INT, "flags")
                .returns(ClassName.get(PACKAGE_NAME, "UIHelper", "ToActivity"))
                .addStatement("intent.addFlags(flags)")
                .addStatement("return this")
                .build();
        MethodSpec setAnim = MethodSpec.methodBuilder("setAnim")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.INT, "enterAnimId")
                .addParameter(TypeName.INT, "exitAnimId")
                .returns(ClassName.get(PACKAGE_NAME, "UIHelper", "ToActivity"))
                .addStatement("enterAnim = enterAnimId")
                .addStatement("exitAnim = exitAnimId")
                .addStatement("return this")
                .build();
        TypeSpec UIHelperToActivity = TypeSpec.classBuilder("ToActivity")
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC)
                .addMethod(constructor)
                .addMethod(setAnim)
                .addMethod(addFlags)
                .addMethods(goToActivitiesMethodList)
                .build();
        MethodSpec bind = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Object.class, "activity")
                .addStatement("bind(activity, null)")
                .build();
        MethodSpec bind2 = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Object.class, "activity")
                .addParameter(Object.class, "intent")
                .addCode("try {\n" +
                        "  String bindorName = activity.getClass().getCanonicalName() + \"_UIHelper\";\n" +
                        "  (($T) Class.forName(bindorName).newInstance()).bind(activity, intent);\n" +
                        "} catch (Exception e) {\n" +
                        "  e.printStackTrace();\n" +
                        "}\n", Route.class)
                .build();
        MethodSpec from = MethodSpec.methodBuilder("from")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.bestGuess("android.content.Context"), "ctx")
                .returns(ClassName.get(PACKAGE_NAME, "UIHelper", "ToActivity"))
                .addStatement("context = ctx")
                .addStatement("intent = new Intent()")
                .addStatement("return new $T()", ClassName.get(PACKAGE_NAME, "UIHelper", "ToActivity"))
                .build();
        MethodSpec go = MethodSpec.methodBuilder("start")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addParameter(Class.class, "clazz")
                .addStatement("intent.setClass(context, clazz)")
                .addStatement("context.startActivity(intent)")
                .addStatement("setTransition()")
                .addStatement("reset()")
                .build();
        MethodSpec goForResult = MethodSpec.methodBuilder("start")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addParameter(Class.class, "clazz")
                .addParameter(int.class, "requestCode")
                .addStatement("intent.setClass(context, clazz)")
                .addCode("if (!(context instanceof $T)) {\n" +
                        "  throw new $T(\"非Activity的Context，不能startActivityForResult\");\n" +
                        "} else {\n" +
                        "  ((Activity) context).startActivityForResult(intent, requestCode);\n" +
                        "}\n", ClassName.bestGuess("android.app.Activity"), ClassName.bestGuess("java.lang.IllegalArgumentException"))
                .addStatement("setTransition()")
                .addStatement("reset()")
                .build();
        MethodSpec reset = MethodSpec.methodBuilder("reset")
                .addModifiers(Modifier.PRIVATE)
                .addModifiers(Modifier.STATIC)
                .addStatement("intent = null")
                .addStatement("context = null")
                .addStatement("enterAnim = -1")
                .addStatement("exitAnim = -1")
                .build();
        MethodSpec setTransition = MethodSpec.methodBuilder("setTransition")
                .addModifiers(Modifier.PRIVATE)
                .addModifiers(Modifier.STATIC)
                .addCode("if(enterAnim < 0 || exitAnim < 0){\n" +
                        "  return;\n" +
                        "}\n" +
                        "if (!(context instanceof $T)) {\n" +
                        "  throw new $T(\"非Activity的Context，不能overridePendingTransition\");\n" +
                        "} else {\n" +
                        "  ((Activity) context).overridePendingTransition(enterAnim, exitAnim);\n" +
                        "}\n", ClassName.bestGuess("android.app.Activity"), ClassName.bestGuess("java.lang.IllegalArgumentException"))
                .build();
        FieldSpec enterAnim = FieldSpec.builder(TypeName.INT, "enterAnim", Modifier.PRIVATE, Modifier.STATIC)
                .initializer("-1")
                .build();
        FieldSpec exitAnim = FieldSpec.builder(TypeName.INT, "exitAnim", Modifier.PRIVATE, Modifier.STATIC)
                .initializer("-1")
                .build();
        TypeSpec UIHelper = TypeSpec.classBuilder("UIHelper")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(ClassName.bestGuess("android.content.Context"), "context", Modifier.PRIVATE, Modifier.STATIC)
                .addField(ClassName.bestGuess("android.content.Intent"), "intent", Modifier.PRIVATE, Modifier.STATIC)
                .addField(enterAnim)
                .addField(exitAnim)
                .addMethod(constructor)
                .addMethod(bind)
                .addMethod(bind2)
                .addMethod(from)
                .addMethod(go)
                .addMethod(goForResult)
                .addMethod(setTransition)
                .addMethod(reset)
                .addType(UIHelperToActivity)
                .addTypes(targetActivitiesClassList)
                .build();
        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, UIHelper).build();
        javaFile.writeTo(filer);
    }

    private void createInjectors() throws Exception {
        for (Map.Entry<String, UIEntity> entry : map.entrySet()) {
            String fullClassName = entry.getKey();
            String packageName = entry.getValue().getPackageName();
            String className = entry.getValue().getClassName();
            MethodSpec.Builder builder = MethodSpec.methodBuilder("bind");
            builder.addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(ClassName.bestGuess(fullClassName), "a")
                    .addParameter(Object.class, "i")
                    .addStatement("$T intent = i == null ? a.getIntent() : (Intent) i", ClassName.bestGuess("android.content.Intent"));
            for (Field field : entry.getValue().getFields()) {
                getExtras(builder, field);
            }
            TypeSpec typeSpec = TypeSpec.classBuilder(className + "_UIHelper")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.bestGuess(Route.class.getCanonicalName()), ClassName.bestGuess(fullClassName)))
                    .addMethod(builder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
            javaFile.writeTo(filer);
        }
    }

    private void getExtras(MethodSpec.Builder builder, Field field) {
        builder.addCode("if (intent.hasExtra($S)) {\n", field.getValue());
        String[] typeArray = {"boolean", "byte", "short", "int", "long", "double", "float", "char"};
        if (Arrays.asList(typeArray).contains(field.getType())) {
            String statement = "  a.%s = intent.get%sExtra(\"%s\", %s)";
            String defaultValue = "";
            switch (field.getType()) {
                case "int":
                case "long":
                case "double":
                case "float":
                    defaultValue = "0";
                    break;
                case "byte":
                    defaultValue = "(byte) 0";
                    break;
                case "short":
                    defaultValue = "(short) 0";
                    break;
                case "boolean":
                    defaultValue = "false";
                    break;
                case "char":
                    defaultValue = "'\0'";
                    break;
            }
            String extraType = field.getType().toUpperCase().substring(0, 1) + field.getType().substring(1, field.getType().length());
            builder.addStatement(String.format(statement, field.getName(), extraType, field.getValue(), defaultValue));
        } else {
            if (field.getType().contains("[]")) {
                String extraType = field.getType().replace("[]", "Array");
                String paramType = field.getParam().substring(field.getParam().lastIndexOf(".") + 1, field.getParam().length());
                if (Arrays.asList(typeArray).contains(extraType.replace("Array", ""))) {
                    extraType = extraType.substring(0, 1).toUpperCase() + extraType.substring(1, extraType.length());
                } else {
                    String type = field.getType().substring(field.getType().lastIndexOf(".") + 1, field.getType().length());
                    extraType = type.substring(0, 1).toUpperCase() + type.substring(1, type.length()).replace("[]", "Array");
                }
                if (extraType.contentEquals("ParcelableArray")) {
                    ClassName originalTypeName = ClassName.bestGuess(field.getOriginalType());
                    builder.addStatement("  $T[] $LArray = intent.getParcelableArrayExtra($S)", ClassName.bestGuess("android.os.Parcelable"), field.getValue(), field.getValue());
                    builder.addStatement("  $T[] $LTempArray = new $T[$LArray.length]", originalTypeName, field.getValue(), originalTypeName, field.getValue());
                    builder.beginControlFlow("  for (int n = 0; n < $LArray.length; n++)", field.getValue());
                    builder.addStatement("  $LTempArray[n] = ($T) $LArray[n]", field.getValue(), originalTypeName, field.getValue());
                    builder.addCode(" ");
                    builder.endControlFlow();
                    builder.addStatement("  a.$L = $LTempArray", field.getName(), field.getValue());
                } else {
                    builder.addStatement("  a.$L = intent.get$LExtra($S)", field.getName(), paramType + extraType, field.getValue());
                }
            } else {
                String[] params = {"Integer", "String", "CharSequence", ""};
                String extraType = field.getType().substring(field.getType().lastIndexOf(".") + 1, field.getType().length());
                String paramType = field.getParam().substring(field.getParam().lastIndexOf(".") + 1, field.getParam().length());
                if (!Arrays.asList(params).contains(paramType)) {
                    paramType = "Parcelable";
                }
                builder.addStatement("  a.$L = intent.get$LExtra($S)", field.getName(), paramType + extraType, field.getValue());
            }
        }
        builder.addCode("}\n");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(Autowired.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private TypeName getFieldType(Field field) {
        TypeName typeName;
        switch (field.getType()) {
            case "boolean":
                typeName = TypeName.BOOLEAN;
                break;
            case "boolean[]":
                typeName = ArrayTypeName.of(TypeName.BOOLEAN);
                break;
            case "byte":
                typeName = TypeName.BYTE;
                break;
            case "byte[]":
                typeName = ArrayTypeName.of(TypeName.BYTE);
                break;
            case "short":
                typeName = TypeName.SHORT;
                break;
            case "short[]":
                typeName = ArrayTypeName.of(TypeName.SHORT);
                break;
            case "int":
                typeName = TypeName.INT;
                break;
            case "int[]":
                typeName = ArrayTypeName.of(TypeName.INT);
                break;
            case "long":
                typeName = TypeName.LONG;
                break;
            case "long[]":
                typeName = ArrayTypeName.of(TypeName.LONG);
                break;
            case "char":
                typeName = TypeName.CHAR;
                break;
            case "char[]":
                typeName = ArrayTypeName.of(TypeName.CHAR);
                break;
            case "float":
                typeName = TypeName.FLOAT;
                break;
            case "float[]":
                typeName = ArrayTypeName.of(TypeName.FLOAT);
                break;
            case "double":
                typeName = TypeName.DOUBLE;
                break;
            case "double[]":
                typeName = ArrayTypeName.of(TypeName.DOUBLE);
                break;
            case "java.lang.CharSequence":
                typeName = TypeName.get(CharSequence.class);
                break;
            case "java.lang.CharSequence[]":
                typeName = ArrayTypeName.of(CharSequence.class);
                break;
            case "java.lang.String":
                typeName = TypeName.get(String.class);
                break;
            case "java.lang.String[]":
                typeName = ArrayTypeName.of(String.class);
                break;
            case "android.os.Parcelable":
                typeName = ClassName.bestGuess("android.os.Parcelable");
                break;
            case "android.os.Parcelable[]":
                typeName = ArrayTypeName.of(ClassName.bestGuess(field.getOriginalType()));
                break;
            case "android.os.Bundle":
                typeName = ClassName.bestGuess("android.os.Bundle");
                break;
            default:
                if (field.getParam().length() > 0) {
                    typeName = ParameterizedTypeName.get(ClassName.bestGuess(field.getType()), ClassName.bestGuess(field.getParam()));
                } else {
                    typeName = ClassName.bestGuess(field.getType());
                }
                break;
        }
        return typeName;
    }
}
