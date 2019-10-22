package com.atomic.plugin.util;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:28
 * @since 1.0.0
 */
public class TypeUtils {

    public static String getTypeByName(String name, Type type) {
        return type instanceof ClassOrInterfaceType ? getClassOrInterfaceTypeByName(name, type) : (type instanceof PrimitiveType ? getPrimitiveTypeByName(name, type) : (type instanceof ReferenceType ? getReferenceTypeByName(name, type) : (type instanceof VoidType ? "void" : "")));
    }

    public static boolean normalDtoDep(String type) {
        if (isPrimitiveType(type)) {
            return false;
        } else if (isList(type)) {
            String warpType = getWarpType(type);
            return !isPrimitiveType(warpType);
        } else {
            return true;
        }
    }

    public static boolean isList(String type) {
        return StringUtils.startsWith(type, "List");
    }

    public static String getWarpType(String type) {
        int start = StringUtils.indexOf(type, "<");
        int end = StringUtils.indexOf(type, ">");
        return StringUtils.substring(type, start + 1, end);
    }

    private static boolean isPrimitiveType(String type) {
        boolean isPrimitiveType = false;
        if (StringUtils.equalsIgnoreCase(type, "Boolean")) {
            isPrimitiveType = true;
        } else if (StringUtils.equalsIgnoreCase(type, "Char")) {
            isPrimitiveType = true;
        } else if (StringUtils.equalsIgnoreCase(type, "Byte")) {
            isPrimitiveType = true;
        } else if (StringUtils.equalsIgnoreCase(type, "Short")) {
            isPrimitiveType = true;
        } else if (StringUtils.equalsIgnoreCase(type, "Integer")) {
            isPrimitiveType = true;
        } else if (StringUtils.equalsIgnoreCase(type, "Long")) {
            isPrimitiveType = true;
        } else if (StringUtils.equalsIgnoreCase(type, "Float")) {
            isPrimitiveType = true;
        } else if (StringUtils.equalsIgnoreCase(type, "Double")) {
            isPrimitiveType = true;
        } else if (StringUtils.equalsIgnoreCase(type, "String")) {
            isPrimitiveType = true;
        } else if (StringUtils.equalsIgnoreCase(type, "void")) {
            isPrimitiveType = true;
        } else if (StringUtils.equalsIgnoreCase(type, "BigDecimal")) {
            isPrimitiveType = true;
        } else if (StringUtils.equalsIgnoreCase(type, "Date")) {
            isPrimitiveType = true;
        } else {
            isPrimitiveType = false;
        }

        return isPrimitiveType;
    }

    private static String getReferenceTypeByName(String name, Type _type) {
        ReferenceType referenceType = (ReferenceType) _type;
        Type _type2 = referenceType.getElementType();
        return _type2 instanceof ClassOrInterfaceType ? getClassOrInterfaceTypeByName(name, _type2) : (_type2 instanceof PrimitiveType ? getPrimitiveTypeByName(name, _type2) : "");
    }

    private static String getClassOrInterfaceTypeByName(String name, Type _type) {
        String phpType = "";
        String javaType = "";
        String wrapJType = "";
        String isRequest = "";
        ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType) _type;
        String type = classOrInterfaceType.getName().getId();
        if (StringUtils.equalsIgnoreCase(type, "Byte")) {
            phpType = "int";
            javaType = "Byte";
        } else if (StringUtils.equalsIgnoreCase(type, "Short")) {
            phpType = "int";
            javaType = "Short";
        } else if (StringUtils.equalsIgnoreCase(type, "Integer")) {
            phpType = "int";
            javaType = "Integer";
        } else if (StringUtils.equalsIgnoreCase(type, "Long")) {
            phpType = "int";
            javaType = "Long";
        } else if (StringUtils.equalsIgnoreCase(type, "Float")) {
            phpType = "float";
            javaType = "Float";
        } else if (StringUtils.equalsIgnoreCase(type, "Double")) {
            phpType = "float";
            javaType = "Double";
        } else if (StringUtils.equalsIgnoreCase(type, "Char")) {
            phpType = "char";
            javaType = "Char";
        } else if (StringUtils.equalsIgnoreCase(type, "Boolean")) {
            phpType = "bool";
            javaType = "Boolean";
        } else if (StringUtils.equalsIgnoreCase(type, "String")) {
            phpType = "string";
            javaType = "String";
        } else if (StringUtils.equalsIgnoreCase(type, "Void")) {
            javaType = "void";
            phpType = "null";
        } else {
            String jType;
            String pType;
            if (StringUtils.equalsIgnoreCase(type, "List")) {
                jType = "";
                pType = "";
                if (classOrInterfaceType.getTypeArguments().isPresent() && Boolean.FALSE.equals(CollectionUtils.isEmpty(classOrInterfaceType.getTypeArguments().get()))) {
                    jType = getTypeByName("java", classOrInterfaceType.getTypeArguments().get().get(0));
                    pType = getTypeByName("php", classOrInterfaceType.getTypeArguments().get().get(0));
                }

                if (StringUtils.isEmpty(pType)) {
                    phpType = "array";
                } else if (isPrimitiveType(jType)) {
                    phpType = "array[" + pType + "]";
                } else {
                    phpType = pType + "[]";
                }

                javaType = "List<" + jType + ">";
                wrapJType = jType;
            } else if (StringUtils.equalsIgnoreCase(type, "Map")) {
                jType = "";
                pType = "";
                String j2Type = "";
                String p2Type = "";
                if (classOrInterfaceType.getTypeArguments().isPresent() && Boolean.FALSE.equals(CollectionUtils.isEmpty(classOrInterfaceType.getTypeArguments().get()))) {
                    jType = getTypeByName("java", classOrInterfaceType.getTypeArguments().get().get(0));
                    pType = getTypeByName("php", classOrInterfaceType.getTypeArguments().get().get(0));
                    j2Type = getTypeByName("java", classOrInterfaceType.getTypeArguments().get().get(1));
                    p2Type = getTypeByName("php", classOrInterfaceType.getTypeArguments().get().get(1));
                }

                phpType = "array[" + pType + "," + p2Type + "]";
                javaType = "Map<" + jType + "," + j2Type + ">";
            } else if (StringUtils.equalsIgnoreCase(type, "Request")) {
                jType = "";
                pType = "";
                if (classOrInterfaceType.getTypeArguments().isPresent() && Boolean.FALSE.equals(CollectionUtils.isEmpty(classOrInterfaceType.getTypeArguments().get()))) {
                    jType = getTypeByName("java", classOrInterfaceType.getTypeArguments().get().get(0));
                    pType = getTypeByName("php", classOrInterfaceType.getTypeArguments().get().get(0));
                }

                javaType = jType;
                phpType = pType;
                isRequest = "true";
            } else if (StringUtils.equalsIgnoreCase(type, "Result")) {
                jType = "";
                pType = "";
                if (classOrInterfaceType.getTypeArguments().isPresent() && Boolean.FALSE.equals(CollectionUtils.isEmpty(classOrInterfaceType.getTypeArguments().get()))) {
                    jType = getTypeByName("java", classOrInterfaceType.getTypeArguments().get().get(0));
                    pType = getTypeByName("php", classOrInterfaceType.getTypeArguments().get().get(0));
                }

                javaType = jType;
                phpType = pType;
            } else if (StringUtils.equalsIgnoreCase(type, "BigDecimal")) {
                javaType = "BigDecimal";
                phpType = "float";
            } else {
                phpType = type;
                javaType = type;
            }
        }

        return getFinalType(name, phpType, javaType, isRequest, wrapJType);
    }

    private static String getPrimitiveTypeByName(String name, Type _type) {
        String phpType = "";
        String javaType = "";
        PrimitiveType primitiveType = (PrimitiveType) _type;
        PrimitiveType.Primitive primitive = primitiveType.getType();
        switch (primitive.ordinal()) {
            case 1:
                phpType = "bool";
                javaType = "Boolean";
                break;
            case 2:
                phpType = "char";
                javaType = "Char";
                break;
            case 3:
                phpType = "int";
                javaType = "Byte";
                break;
            case 4:
                phpType = "int";
                javaType = "Short";
                break;
            case 5:
                phpType = "int";
                javaType = "Integer";
                break;
            case 6:
                phpType = "int";
                javaType = "Long";
                break;
            case 7:
                phpType = "float";
                javaType = "Float";
                break;
            case 8:
                phpType = "float";
                javaType = "Double";
                break;
            default:
                phpType = "string";
                javaType = "unkown";
        }

        return getFinalType(name, phpType, javaType, "", "");
    }

    private static String getFinalType(String name, String phpType, String javaType, String isRequest, String wrapJType) {
        return StringUtils.equalsIgnoreCase(name, "java") ? javaType : (StringUtils.equalsIgnoreCase(name, "php") ? phpType : (StringUtils.equalsIgnoreCase(name, "isrequst") ? isRequest : (StringUtils.equalsIgnoreCase(name, "wrapJType") ? wrapJType : null)));
    }

    public static String getBaseType(String javaType) {
        return javaType.equals("String") ? "java.lang.String" : (javaType.equals("Integer") ? "java.lang.Integer" : (javaType.equals("Long") ? "java.lang.Long" : (javaType.equals("Short") ? "java.lang.Short" : (javaType.equals("Boolean") ? "java.lang.Boolean" : (javaType.equals("Byte") ? "java.lang.Byte" : (javaType.equals("BigDecimal") ? "java.math.BigDecimal" : (javaType.equals("Float") ? "java.lang.Float" : (javaType.equals("Double") ? "java.lang.Double" : (javaType.equals("Date") ? "java.util.Date" : (javaType.startsWith("Map") ? "java.util.HashMap" : (javaType.startsWith("HashMap") ? "java.util.HashMap" : javaType)))))))))));
    }
}
