package com.zeratul.plugin.generator;

import com.github.javaparser.ast.Modifier;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.zeratul.plugin.java.Field;
import com.zeratul.plugin.java.JavaAstModel;
import com.zeratul.plugin.java.Method;
import com.zeratul.plugin.java.Pair;
import com.zeratul.plugin.parser.JavaParser;
import com.zeratul.plugin.util.FileUtils;
import com.zeratul.plugin.util.ReflectionUtils;
import com.zeratul.plugin.util.StringUtils;
import com.zeratul.plugin.util.VelocityUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:48
 * @since 1.0.0
 */
public class JavaTestGenerator {

    private static String applicationName;

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
            Iterator iterator = method.getParam().iterator();

            while (true) {
                while (iterator.hasNext()) {
                    Pair pair = (Pair) iterator.next();
                    String paramName = (String) pair.getValue();
                    Field field = (Field) pair.getKey();
                    if (field.isRequest && StringUtils.isBasicType(field.getJavaType())) {
                        paramList.add(basicParam);
                    } else if (model.importsMap.containsKey(field.getJavaType())) {
                        String className = model.importsMap.get(field.getJavaType());
                        Class dtoClass = loadClass(className);
                        List<java.lang.reflect.Field> fields = Lists.newArrayList();
                        ReflectionUtils.getAllFields(dtoClass, fields);
                        Iterator fieldIterator = fields.iterator();

                        while (fieldIterator.hasNext()) {
                            java.lang.reflect.Field f = (java.lang.reflect.Field) fieldIterator.next();
                            paramList.add(f.getName());
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

    private static Class loadClass(String className) throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    public static void setSpringBootApplicationMainClass(String applicationName) {
        JavaTestGenerator.applicationName = applicationName;
    }
}
