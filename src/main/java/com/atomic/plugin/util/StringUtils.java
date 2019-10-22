package com.atomic.plugin.util;

import java.util.Arrays;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:36
 * @since 1.0.0
 */
public class StringUtils {

    public static String toLowerCaseFirstOne(String s) {
        return Character.isLowerCase(s.charAt(0)) ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static String toUpperCaseFirstOne(String s) {
        return Character.isUpperCase(s.charAt(0)) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static boolean isBasicType(String type) {
        return Arrays.asList(new String[]{"String", "Date", "Integer", "int", "Long", "long", "Double", "double", "Boolean", "boolean", "Byte", "byte", "BigDecimal", "Short", "short", "Float", "float"}).contains(type);
    }

    /**
     * 是否基础类或者其包装类，String、Date类型
     * @param clz
     * @return
     */
    public static boolean isBasicType(Class clz) {
        try {
            return clz.isPrimitive() || Arrays.asList("String", "Date").contains(clz.getSimpleName()) || ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 把首字母转为小写
     * @param str
     * @return
     */
    public static String lowerFirst(String str) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
            return str;
        }
        return str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toLowerCase());
    }

    /**
     * 把首字母转为大写
     * @param str
     * @return
     */
    public static String upperFrist(String str) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
            return str;
        }
        return str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase());
    }

    /**
     * 拼接boolean字段方法
     * @param fieldName
     * @return
     */
    public static String parIsSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    /**
     * 拼接boolean字段方法
     * @param fieldName
     * @return
     */
    public static String parIsGetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    /**
     * 拼接在某属性的 set方法
     * @param fieldName
     * @return String
     */
    public static String parSetName(String fieldName, String fieldType) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        if (("Boolean".equals(fieldType) || "boolean".equals(fieldType)) && fieldName.startsWith("is")) {
            fieldName = fieldName.substring(2);
        }
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    /**
     * 拼接在某属性的 get方法
     * @param fieldName
     * @return
     */
    public static String parGetName(String fieldName, String fieldType) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        if (("Boolean".equals(fieldType) || "boolean".equals(fieldType)) && fieldName.startsWith("is")) {
            fieldName = fieldName.substring(2);
        }
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
