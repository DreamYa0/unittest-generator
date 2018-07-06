package com.zeratul.plugin.generator;

import com.github.javaparser.ast.Modifier;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zeratul.plugin.java.Field;
import com.zeratul.plugin.java.JavaAstModel;
import com.zeratul.plugin.java.Method;
import com.zeratul.plugin.java.Pair;
import com.zeratul.plugin.parser.JavaParser;
import com.zeratul.plugin.util.FileUtils;
import com.zeratul.plugin.util.ReflectionUtils;
import com.zeratul.plugin.util.StringUtils;
import com.zeratul.plugin.util.VelocityUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:48
 * @since 1.0.0
 */
public class JavaTestGenerator {

    private static final Logger logger = LoggerFactory.getLogger(JavaTestGenerator.class);
    private static String applicationName;
    private static volatile String path;

    public static void generatorList(List<JavaParser> javas, String path) {
        Iterator<JavaParser> iterator = javas.iterator();

        while (iterator.hasNext()) {
            JavaParser javaParser = iterator.next();
            generator(javaParser.getModel(), path);
        }
    }

    public static void generator(JavaAstModel model, String path) {
        VelocityEngine ve = VelocityUtils.getVelocityEngine();
        VelocityContext ctx = new VelocityContext();
        Template t;
        if (org.springframework.util.StringUtils.isEmpty(applicationName)) {
            t = ve.getTemplate("/template/Test.vm");
        } else {
            t = ve.getTemplate("/template/TestSpringBoot.vm");
            // 获取SimpleClassName
            Iterable<String> split = Splitter.on(".").split(applicationName);
            List<String> names = Lists.newArrayList(split);

            ctx.put("applicationClassName", applicationName);
            ctx.put("springBootApplicationMainClass", names.get(names.size() - 1));
        }

        ctx.put("package", model.packageName + "." + model.className.toLowerCase());
        int first = model.packageName.indexOf(".");
        String packageName = "test" + model.packageName.substring(first, model.packageName.length());
        ctx.put("package", packageName + "." + model.className.toLowerCase());
        ctx.put("servicePackage", model.packageName + "." + model.className);
        String basePath = path + File.separator + "java" + File.separator + org.apache.commons.lang.StringUtils.replace(packageName, ".", File.separator);
        ctx.put("service", model.className);

        Iterator<Method> iterator = model.methods.iterator();
        while (iterator.hasNext()) {
            Method method = iterator.next();

            Iterator<Modifier> modifierIte = method.modifiers.iterator();
            while (modifierIte.hasNext()) {
                Modifier modifier = modifierIte.next();

                // public方法才生成测试用例
                if (Objects.equals(modifier, com.github.javaparser.ast.Modifier.PUBLIC)) {
                    String fileName = "Test" + StringUtils.toUpperCaseFirstOne(method.getName());
                    String genFilePath = basePath + File.separator + model.className.toLowerCase() + File.separator;
                    ctx.put("clazz", fileName);

                    try {
                        FileUtils.writeFile(t, ctx, genFilePath, fileName + ".java");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }
    }

    public static List<String> getMethodParams(JavaAstModel model, Method method) {
        List<String> paramList = Lists.newArrayList();

        try {
            // Request<XXX> XXX为基本类型时，
            String basicParam = "data";
            Iterator<Pair<Field, String>> iterator = method.getParam().iterator();

            while (true) {
                while (iterator.hasNext()) {
                    Pair<Field, String> pair = iterator.next();
                    String paramName = pair.getValue();
                    Field field = pair.getKey();
                    if (field.isRequest && StringUtils.isBasicType(field.getJavaType())) {

                        paramList.add(basicParam);

                    } else if (model.importsMap.containsKey(field.getJavaType())) {

                        String className = model.importsMap.get(field.getJavaType());
                        if (isContainsServletObject(className)) {
                            continue;
                        }

                        Class dtoClass;
                        try {
                            dtoClass = loadClass(className);
                        } catch (ClassNotFoundException e) {
                            // 获取类的属性名称
                            return obtainFieldNames(className);
                        }

                        addBasicTypeParamName(dtoClass, paramName, paramList);

                        if (noSprigInterface(dtoClass)) {

                            // 自定义对象或者 如 Request<T> 类似的泛型
                            List<java.lang.reflect.Field> fields = Lists.newArrayList();
                            ReflectionUtils.getAllFields(dtoClass, fields);
                            Iterator<java.lang.reflect.Field> fieldIterator = fields.iterator();

                            while (fieldIterator.hasNext()) {

                                java.lang.reflect.Field f = fieldIterator.next();
                                Type genericType = f.getGenericType();
                                String fieldName = f.getName();
                                if (Boolean.FALSE.equals(Objects.equals(fieldName, "serialVersionUID"))) {

                                    // 泛型中的类型参数，如Request<T> 中的 T
                                    if (genericType instanceof TypeVariable) {

                                        TypeVariable[] typeParameters = dtoClass.getTypeParameters();
                                        ParameterizedType parameterize = TypeUtils.parameterize(dtoClass,typeParameters);


                                        Map<String, Object> paramsFromType = getParamsFromType(parameterize);
                                        if (Boolean.FALSE.equals(CollectionUtils.isEmpty(paramsFromType))) {
                                            paramList.addAll(paramsFromType.keySet());
                                        }



                                    } else if (genericType instanceof Class) {

                                        // Class类型
                                        List<java.lang.reflect.Field> allFields = ReflectionUtils.getAllFieldsList((Class<?>) genericType);
                                        List<String> collect = filterAndTransformFields(allFields);

                                        paramList.addAll(collect);
                                    } else {
                                        // 基本类型
                                        paramList.add(fieldName);
                                    }
                                }
                            }

                        }

                    } else {
                        paramList.add(paramName);
                    }
                }

                return paramList;
            }
        } catch (Exception var12) {
            return null;
        }
    }

    private static boolean isContainsServletObject(String className) {
        return className.contains("HttpServletRequest") || className.contains("HttpServletResponse") || className.contains("HttpSession");
    }

    private static void addBasicTypeParamName(Class clazz, String paramName, List<String> paramNames) {
        if (StringUtils.isBasicType(clazz)) {
            // 基本类型或包装器类型
            paramNames.add(paramName);
            return;
        }
    }

    private static List<String> filterAndTransformFields(List<java.lang.reflect.Field> fields) {
        return fields.stream()
                .map(allField -> {
                    if (Boolean.FALSE.equals(Objects.equals(allField.getName(), "serialVersionUID"))) {
                        return allField.getName();
                    } else {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static Map<String, Object> getParamsFromType(Type requestType) {

        // requestType 为Request<T>
        if (requestType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) requestType;
            Type[] dataTypes = parameterizedType.getActualTypeArguments();

            for (Type dataType : dataTypes) {
                return getParamsFromType(dataType);
            }

        } else if (requestType instanceof Class) {
            // requestType 为Request<T> 中的 T
            Class clazz = (Class) requestType;
            return getParamsFromClass(clazz);
        } else {

        }
        return Maps.newHashMap();
    }

    private static Map<String, Object> getParamsFromClass(Class clazz) {
        //如果入参为Request<Integer>,Request<String>, T 为基本类型
        if (StringUtils.isBasicType(clazz)) {
            return Maps.newHashMap();
        }
        Map<String, Object> maps = Maps.newHashMap();
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {

            if (StringUtils.isBasicType(field.getType()) || field.getGenericType() instanceof ParameterizedType) {
                maps.put(field.getName(), null);
            } else if (field.getGenericType() instanceof Class) {
                //遇到对象，则只生成这个对象字段，不生成对象中值的字段
                maps.put(field.getName(), null);
            } else {
                logger.error("-------------获取生成excel入参字段异常！---------------");
            }

        }
        return maps;
    }

    private static boolean noSprigInterface(Type type) {
        return Boolean.FALSE.equals(type instanceof HttpSession) && Boolean.FALSE.equals(type instanceof HttpServletRequest) && Boolean.FALSE.equals(type instanceof HttpServletResponse);
    }


    private static Class loadClass(String className) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException ex) {
                try {
                    return ClassLoader.getSystemClassLoader().loadClass(className);
                } catch (ClassNotFoundException exc) {
                    try {
                        return JavaTestGenerator.class.getClassLoader().loadClass(className);
                    } catch (ClassNotFoundException exce) {
                        return ClassLoader.getSystemClassLoader().getParent().loadClass(className);
                    }
                }
            }
        }
    }

    public static void setSpringBootApplicationMainClass(String applicationName) {
        JavaTestGenerator.applicationName = applicationName;
    }

    public static void setPath(String path) {
        JavaTestGenerator.path = path;
    }

    private static String spliceFullClassPath(String className) {

        String currentPath = path;
        if (currentPath.contains("/") && currentPath.contains("/src/test")) {
            String newCurrentPath = currentPath.replace("/src/test", "/src/main/java/");
            String newClassName = className.replace(".", "/");
            return newCurrentPath + newClassName + ".java";
        } else if (currentPath.contains("\\") && currentPath.contains("\\src\\test")) {
            String newCurrentPath = currentPath.replace("\\src\\test", "\\src\\main\\java\\");
            String newClassName = className.replace(".", "\\");
            return newCurrentPath + newClassName + ".java";
        }
        throw new RuntimeException("生成测试包路径错误！");
    }

    private static List<String> obtainFieldNames(String className) {

        try {
            String fullClassPath = spliceFullClassPath(className);
            JavaParser parser = new JavaParser(fullClassPath);
            parser.parse();
            JavaAstModel astModel = parser.getModel();
            return astModel.fields.stream().map(field -> field.name).collect(Collectors.toList());
        } catch (Exception e) {

        }
        return Lists.newArrayList();
    }
}
