package com.github.lany192.arouter;

import org.apache.commons.lang3.StringUtils;

/**
 * 工具
 *
 * @author Administrator
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
}
