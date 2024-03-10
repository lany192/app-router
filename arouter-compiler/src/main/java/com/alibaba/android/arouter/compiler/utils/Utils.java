package com.alibaba.android.arouter.compiler.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 工具
 *
 * @author lany192
 */
public class Utils {
    /**
     * 划线转驼峰
     */
    public static String line2hump(String name) {
        if (StringUtils.isEmpty(name)) {
            return name;
        }
        if (!name.contains("_")) {
            return name;
        }
        String[] camels = name.split("_");
        StringBuilder builder = new StringBuilder();
        for (String camel : camels) {
            if (!camel.isEmpty()) {
                if (builder.length() == 0) {
                    builder.append(camel.toLowerCase());
                } else {
                    builder.append(camel.substring(0, 1).toUpperCase());
                    builder.append(camel.substring(1).toLowerCase());
                }
            }
        }
        return builder.toString();
    }

    /**
     * 首字母转小写
     */
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
        }
    }

    /**
     * 首字母转大写
     */
    public static String toUpperCaseFirstOne(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        } else {
            if (str.length() > 1) {
                return str.substring(0, 1).toUpperCase() + str.substring(1);
            } else {
                return str.toUpperCase();
            }
        }
    }

    /**
     * Module名称转java类名
     */
    public static String getModuleName(String moduleName) {
        if (StringUtils.isEmpty(moduleName)) {
            return "";
        } else {
            StringBuilder className = new StringBuilder();
            // 首字母大写
            boolean nextIsUpperCase = true;
            for (char charValue : moduleName.toCharArray()) {
                if (charValue == '-' || charValue == '_') {
                    nextIsUpperCase = true;
                } else {
                    if (nextIsUpperCase) {
                        className.append(Character.toUpperCase(charValue));
                        nextIsUpperCase = false;
                    } else {
                        className.append(charValue);
                    }
                }
            }
            return className.toString();
        }
    }
}
