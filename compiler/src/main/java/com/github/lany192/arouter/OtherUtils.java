package com.github.lany192.arouter;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * @author lyg
 */
public class OtherUtils {
    /**
     * 生成使用文档
     */
    public static String getUseDoc(Element element, Types types, TypeMirror iProvider) {
        Route route = element.getAnnotation(Route.class);
        String doc = "";
        if (!StringUtils.isEmpty(route.name())) {
            doc = route.name() + "\n";
        }
        StringBuilder uri = new StringBuilder(route.path());
        StringBuilder parameter = new StringBuilder("参数说明:");
        parameter.append("\n").append("| 名称 | 必选 | 说明 |");
        parameter.append("\n").append("| ---- | ---- | ---- |");
        boolean isFirst = true;
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                String fieldName = field.getSimpleName().toString();
                String key = StringUtils.isEmpty(autowired.name()) ? fieldName : autowired.name();

                if (isFirst) {
                    isFirst = false;
                    uri.append("?").append(key).append("=xxx");
                } else {
                    uri.append("&").append(key).append("=xxx");
                }
                parameter.append("\n|").append(key).append(" | ").append(autowired.required()).append(" | ").append(autowired.desc()).append(" |");
            }
        }
        doc = doc + "\n路由协议:\n```\ngamekipo://" + uri + "\n```";
        doc = doc + "\nJS调用:\n```\nwindow.app.route('" + uri + "');\n```";
        doc = doc + "\n" + parameter;
        doc = doc + "\n\n类位置：{@link " + ClassName.get((TypeElement) element) + "}";
        return doc;
    }

    public static ParameterSpec getParameter(Element field, Autowired autowired) {
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

}
