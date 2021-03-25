package com.github.lany192.arouter.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 工具
 * @author Administrator
 */
public class Utils {
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
