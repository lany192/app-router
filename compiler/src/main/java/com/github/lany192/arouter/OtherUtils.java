package com.github.lany192.arouter;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * @author lyg
 */
public class OtherUtils {

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
