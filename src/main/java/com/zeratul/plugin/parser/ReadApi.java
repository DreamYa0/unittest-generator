package com.zeratul.plugin.parser;

import com.coinsuper.common.dto.Request;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zeratul.plugin.java.Methods;
import com.zeratul.plugin.java.RequestParameters;
import com.zeratul.plugin.java.ResultParameters;
import com.zeratul.plugin.java.Services;
import com.zeratul.plugin.util.ClassUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.Reporter;

import javax.validation.constraints.Null;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author dreamyao
 */
public class ReadApi {

    public List<Services> getServiceList(String packageName) {
        List<Class<?>> classes = ClassUtils.getClasses(packageName);
        List<Services> serviceList = Lists.newArrayList();
        if (classes != null) {
            classes.stream().filter(Class::isInterface).forEach(clazz -> {
                Services services = new Services();
                services.setServiceName(clazz.getName());
                serviceList.add(services);
            });
        }
        return serviceList;
    }

    public List<Methods> getMethodList(String packageName, String service) {
        List<Class<?>> classes = ClassUtils.getClasses(packageName);
        List<Methods> methodList = Lists.newArrayList();
        if (classes != null) {
            classes.stream().filter(Class::isInterface).filter(clazz -> clazz.getName().equals(service)).forEach(newClass -> {
                //获取方法名
                Method[] declaredMethods = newClass.getDeclaredMethods();
                if (declaredMethods != null) {
                    Arrays.stream(declaredMethods).forEach(method -> {
                        Methods methods = new Methods();
                        methods.setMethodName(method.getName());
                        methodList.add(methods);
                    });
                }
            });
        }
        return methodList;
    }

    public Map<String, Object> getParameters(String packageName, String service, String method) {
        List<Class<?>> classes = ClassUtils.getClasses(packageName);
        //请求参数列表
        List<RequestParameters> requestParasList = Lists.newArrayList();
        //返回结果列表
        List<ResultParameters> resultParasList = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        Class<?> currentClass = null;
        if (classes != null) {
            for (Class<?> clazz : classes) {
                if (!clazz.isInterface()) {
                    continue;
                }
                if (clazz.getName().equals(service)) {
                    currentClass = clazz;
                }
            }
        }
        Method currentMethod;
        if (currentClass != null) {
            Method[] methods = currentClass.getDeclaredMethods();
            if (methods != null) {
                for (Method method1 : methods) {
                    if (method1.getName().equals(method)) {
                        currentMethod = method1;
                        //获取请求参数
                        Class<?> parasClass = currentMethod.getParameterTypes()[0];
                        //判断是否是Request参数
                        List<Field> fieldList = Lists.newArrayList();
                        //当前类属性
                        Field[] fields = parasClass.getDeclaredFields();
                        fieldList.addAll(Arrays.asList(fields));
                        //父类属性
                        Field[] superClassFields = parasClass.getSuperclass().getDeclaredFields();
                        fieldList.addAll(Arrays.asList(superClassFields));
                        if (!CollectionUtils.isEmpty(fieldList)) {
                            for (Field field : fieldList) {
                                if (field.getName().equals("serialVersionUID")) {
                                    continue;
                                }
                                if (field.getName().equals("sid")) {
                                    continue;
                                }
                                //非当前类的data参数不传
                                if (!parasClass.getName().equals(Request.class.getName())) {
                                    if (field.getName().equals("data")) {
                                        continue;
                                    }
                                }
                                RequestParameters requestParameters = new RequestParameters();
                                requestParameters.setParameterName(field.getName());

                                Null isnull = null;
                                if (parasClass.getName().equals(Request.class.getName())) {
                                    Annotation[][] parameterAnnotations = currentMethod.getParameterAnnotations();
                                    if (parameterAnnotations != null && parameterAnnotations.length > 0 && parameterAnnotations[0].length > 0) {
                                        Reporter.log(StringUtils.center("parameterAnnotations:" + new Gson().toJson(parameterAnnotations), 100, "="));

                                        Type[] pTypes = currentMethod.getGenericParameterTypes();
                                        if (pTypes != null && pTypes.length > 0) {
                                            Type pType = pTypes[0];
                                            if (pType instanceof ParameterizedType) {
                                                Class<?> pClass = (Class<?>) ((ParameterizedType) pType).getActualTypeArguments()[0];
                                                requestParameters.setParameterType(pClass.getSimpleName());
                                            }
                                        }

                                    }
                                } else {

                                    isnull = field.getAnnotation(Null.class);
                                    requestParameters.setParameterType(field.getType().getSimpleName());
                                }

                                if (isnull != null) {
                                    requestParameters.setParameterRequired(true);
                                } else {
                                    requestParameters.setParameterRequired(false);
                                }
                                requestParasList.add(requestParameters);
                                //处理当前属性的子对象(判断子对象是否是自定义的对象)
                                Type subType = field.getGenericType();
                                Class<?> subClass = null;
                                if (subType instanceof ParameterizedType) {
                                    ParameterizedType pt2 = (ParameterizedType) subType;
                                    subClass = (Class<?>) pt2.getActualTypeArguments()[0];
                                }
                                if (subClass != null && subClass.getName().startsWith("com.zhubajie")) {
                                    Field[] fields3 = subClass.getDeclaredFields();

                                    for (Field subField : fields3) {
                                        if (subField.getName().equals("serialVersionUID")) {
                                            continue;
                                        }
                                        RequestParameters paras2 = new RequestParameters();
                                        paras2.setParameterName(field.getName() + "." + subField.getName());
                                        paras2.setParameterType(subField.getType().getSimpleName());
                                        requestParasList.add(paras2);
                                    }
                                }
                            }
                        }
                        //返回结果
                        Type resultType = currentMethod.getGenericReturnType();
                        if (resultType instanceof ParameterizedType) {
                            ParameterizedType pt = (ParameterizedType) resultType;
                            //返回对象不能自己是泛型[<T>]而又继承泛型[<T>]父类
                            //会导致错误 sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl cannot be cast to java.lang.Class
                            Type type = pt.getActualTypeArguments()[0];
                            Reporter.log(StringUtils.center("类型：" + type, 100, "="));
                            if (type instanceof Class) {
                                processReturn(resultParasList, fields, (Class<?>) type);
                            } else {
                                // 待处理
                                Reporter.log(StringUtils.center("类型：" + type, 100, "*"));
                                ResultParameters paras = new ResultParameters();
                                paras.setParameterName("map");
                                paras.setParameterType("Map");
                                paras.setParameterType("" + type);
                                resultParasList.add(paras);
                            }
                        }
                    }
                }
            }
        }
        map.put("requestParasList", requestParasList);
        map.put("resultParasList", resultParasList);
        return map;
    }

    private void processReturn(List<ResultParameters> resultParasList, Field[] fields, Class<?> type) {
        if (type.getName().startsWith("java.lang")) {
            ResultParameters paras = new ResultParameters();
            paras.setParameterName("返回基础数据类型");
            paras.setParameterType(type.getSimpleName());
            resultParasList.add(paras);
        } else {
            Field[] fields2 = type.getDeclaredFields();
            if (fields != null) {
                for (Field field : fields2) {
                    if (field.getName().equals("serialVersionUID")) {
                        continue;
                    }
                    ResultParameters paras = new ResultParameters();
                    paras.setParameterName(field.getName());
                    paras.setParameterType(field.getType().getSimpleName());
                    resultParasList.add(paras);
                    //处理当前属性的子对象(判断子对象是否是自定义的对象)
                    Type subType = field.getGenericType();
                    Class<?> subClass = null;
                    if (subType instanceof ParameterizedType) {
                        ParameterizedType pt2 = (ParameterizedType) subType;
                        subClass = (Class<?>) pt2.getActualTypeArguments()[0];
                    }
                    if (subClass != null && subClass.getName().startsWith("com.zhubajie")) {
                        Field[] fields3 = subClass.getDeclaredFields();

                        for (Field subField : fields3) {
                            if (subField.getName().equals("serialVersionUID")) {
                                continue;
                            }
                            ResultParameters paras2 = new ResultParameters();
                            paras2.setParameterName(field.getName() + "." + subField.getName());
                            paras2.setParameterType(subField.getType().getSimpleName());
                            resultParasList.add(paras2);
                        }
                    }
                }
            }
        }
    }
}





