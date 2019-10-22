package com.atomic.plugin.util;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * 实例化Class对象
     * @param cls Class对象
     * @param <T> T
     * @return T
     */
    public static <T> T initFromClass(Class<? extends T> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 实例化Class对象
     * @param classes Class对象
     */
    public static <T> void initializeClass(Class<? extends T>... classes) {
        for (Class<?> clazz : classes) {
            try {
                Class.forName(clazz.getName(), true, clazz.getClassLoader());
            } catch (ClassNotFoundException e) {
                throw new AssertionError(e);
            }
        }
    }

    /**
     * 获取所有属性，包括父类对象的属性
     * @param clazz  Class对象
     * @param fields Field对象
     */
    public static void getAllFields(Class clazz, List<Field> fields) {
        if(clazz != null && fields != null && clazz != Object.class) {
            Field[] fs = clazz.getDeclaredFields();
            for(int i = 0; i < fs.length; ++i) {
                Field field = fs[i];
                if(!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                    fields.add(field);
                }
            }

            getAllFields(clazz.getSuperclass(), fields);
        }
    }

    /**
     * 获取所有属性，包括父类对象的属性
     * @param clazz Class对象
     */
    public static List<Field> getAllFieldsList(final Class<?> clazz) {
        final List<Field> fieldList = Lists.newArrayList();
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(fieldList, declaredFields);
            currentClass = currentClass.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 获取单个类的属性
     * @param clazz
     * @param fields
     */
    public static void getFields(Class clazz, List<Field> fields) {
        if (clazz == null || fields == null || clazz == Object.class) {
            return;
        }
        Field[] newFields = clazz.getDeclaredFields();
        fields.addAll(Lists.newArrayList(newFields));
    }

    /**
     * 获取属性值
     * @param bean        实例
     * @param targetClass 属性所属class
     * @param fieldName   属性名
     * @return Object
     * @throws IllegalAccessException .{@link IllegalAccessException}
     */
    public static Object getFieldValue(Object bean, Class targetClass, String fieldName) throws IllegalAccessException {
        Optional<Field> fieldOptional = Arrays.stream(targetClass.getDeclaredFields())
                .filter(field -> field.getName().equals(fieldName))
                .findFirst();
        if (fieldOptional.isPresent()) {
            fieldOptional.get().setAccessible(true);
            return fieldOptional.get().get(bean);
        }
        return null;
    }

    /**
     * 获取属性值
     * @param bean        实例
     * @param targetClass 属性所属class
     * @param fieldName   属性名
     * @return Field
     * @throws IllegalAccessException .{@link IllegalAccessException}
     */
    public static Field getField(Object bean, Class targetClass, String fieldName) throws IllegalAccessException {
        List<Field> fields = Lists.newArrayList();
        getAllFields(bean.getClass(), fields);
        // 第一次类型和属性名都满足才返回
        Optional<Field> oneNewFields = fields.stream()
                .filter(field -> field.getName().equals(fieldName) && field.getType() == targetClass)
                .findFirst();
        // 第2次类型和属性名满足一个即返回
        Optional<Field> twoNewFields = fields.stream()
                .filter(field -> field.getName().equals(fieldName) || field.getType() == targetClass)
                .findFirst();
        return oneNewFields.orElseGet(() -> twoNewFields.orElse(null));
    }

    /**
     * 从类的Class包含父类中获取给定名称的属性
     * @param clazz     Class对象
     * @param fieldName 字段名称
     * @return 字段属性
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        List<Field> fields = getAllFieldsList(clazz);
        Optional<Field> optional = fields.stream()
                .filter(field -> fieldName.equalsIgnoreCase(field.getName()))
                .findFirst();
        return optional.orElse(null);
    }

    /**
     * 获取属性值
     * @param bean           实例
     * @param targetClass    属性所属class
     * @param fieldClassName 属性名或属性所属类名
     * @return
     * @throws IllegalAccessException .{@link IllegalAccessException}
     */
    public static Object getFieldInstance(Object bean, Class<?> targetClass, String fieldClassName) throws IllegalAccessException {
        Field[] fields = targetClass.getDeclaredFields();
        Optional<Field> filterField = Arrays.stream(fields)
                .filter(field -> field.getName().equals(fieldClassName) || field.getType().toString().toLowerCase().endsWith("." + fieldClassName.toLowerCase()))
                .findFirst();
        if (filterField.isPresent()) {
            filterField.get().setAccessible(true);
            return filterField.get().get(bean);
        }
        return null;
    }

    /**
     * 只根据方法名称来获取method，有重载函数的不要调用
     * @param clazz      Class对象
     * @param methodName 方法名称
     * @return
     */
    public static Method getMethod(Class<?> clazz, String methodName) {
        Optional<Method> method = Arrays.stream(clazz.getMethods())
                .filter(m -> m.getName().equalsIgnoreCase(methodName))
                .findFirst();
        return method.orElse(null);
    }

    /**
     * 在指定Class对象中获取指定方法
     * @param clazz          Class对象
     * @param methodName     方法名称
     * @param parameterTypes 入参Type
     * @return Method对象
     * @throws Exception .{@link Exception}
     */
    public static Method getMethod(Class<?> clazz, String methodName, final Class<?>... parameterTypes) throws Exception {
        Method method;
        try {
            method = clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() == null) {
                throw new NoSuchMethodException(String.format("方法[%s]不存在！", methodName));
            } else {
                method = getMethod(clazz.getSuperclass(), methodName, parameterTypes);
            }
        }
        return method;
    }
}
