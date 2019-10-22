package com.atomic.plugin.generator;

import com.alibaba.fastjson.util.FieldInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.atomic.plugin.util.ReflectionUtils;
import com.atomic.plugin.util.StringUtils;
import org.testng.Reporter;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static com.atomic.plugin.util.StringUtils.parIsSetName;
import static com.atomic.plugin.util.StringUtils.parSetName;

/**
 * @author dreamyao
 * @title
 * @date 17/6/7 下午12:41
 * @since 1.0.0
 */
public class GenerateMethod {

    public static boolean isFilter = true;
    // 过滤清单后续可以考虑可配置
    private static List<String> fillterList = Lists.newArrayList();

    static {
        fillterList.add("actionInfo");
        fillterList.add("sid");
    }

    private Method method;
    private Type request, result;
    private Map<String, Object> requestMap, resultMap;

    public GenerateMethod(Method method) {
        this.method = method;
        init();
    }

    private static boolean filter(FieldInfo field) {
        if (!isFilter) {
            // 不开过滤
            return false;
        }
        if (field == null) {
            return true;
        }
        String name = field.name;
        return name == null || name.trim().length() == 0 || fillterList.contains(name);
    }

    private void init() {
        request = method.getGenericParameterTypes()[0];
        result = method.getGenericReturnType();
        if (isParameterizedType(request)) {
            requestMap = Maps.newHashMap();
            requestMap.put("data", null);
        } else {
            requestMap = getParamsFromType(request);
            resultMap = getParamsFromType(result);
            if (requestMap == null) {
                requestMap = Maps.newHashMap();
                requestMap.put("data", null);
            }
            if (resultMap == null) {
                resultMap = Maps.newHashMap();
                resultMap.put("data", null);
            }
        }
    }

    /**
     * 判断Request<xxxx>的xxxx对象是否是参数化类型
     * @param requestType
     * @return
     */
    private boolean isParameterizedType(Type requestType) {
        if (requestType instanceof ParameterizedType) {
            ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) requestType;
            Type[] dataTypes = parameterizedType.getActualTypeArguments();
            if (dataTypes[0] instanceof ParameterizedType) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Object> getParamsFromType(Type type) {
        if (type instanceof ParameterizedTypeImpl) {
            ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) type;
            Type[] dataTypes = parameterizedType.getActualTypeArguments();
            if (dataTypes.length == 1) {
                return getParamsFromType(dataTypes[0]);
            }
            Reporter.log("-------------错误的类型！" + method.getName() + "---------------", true);
        } else if (type instanceof Class) {
            Class clazz = (Class) type;
            return getParamsFromClass(clazz);
        } else {
            Reporter.log("-------------错误的类型！" + method.getName() + "---------------", true);
        }
        return null;
    }

    private Map<String, Object> getParamsFromClass(Class clazz) {
        //如果入参为Request<Integer>,Request<String>
        if (StringUtils.isBasicType(clazz)) {
            return null;
        }
        Map<String, Object> maps = Maps.newHashMap();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Method fieldSetMet = null;
            String fieldSetName = parSetName(field.getName(), field.getType().getSimpleName());
            String fieldIsSetName = parIsSetName(field.getName());
            if (ReflectionUtils.getMethod(clazz, fieldSetName) != null) {
                fieldSetMet = ReflectionUtils.getMethod(clazz, fieldSetName);
            } else if (ReflectionUtils.getMethod(clazz, fieldIsSetName) != null) {
                fieldSetMet = ReflectionUtils.getMethod(clazz, fieldIsSetName);
            }
            if (fieldSetMet != null) {
                if (StringUtils.isBasicType(field.getType()) || field.getGenericType() instanceof ParameterizedType) {
                    maps.put(field.getName(), null);
                } else if (field.getGenericType() instanceof Class) {
                    //遇到对象，则只生成这个对象字段，不生成对象中值的字段
                    maps.put(field.getName(), null);
                } else {
                    Reporter.log("-------------获取生成excel入参字段异常！---------------", true);
                }
            }
        }
        return maps;
    }

    public Map<String, Object> getRequestMap() {
        return requestMap;
    }

    public Map<String, Object> getResultMap() {
        return resultMap;
    }

    public Method getMethod() {
        return method;
    }

    public Type getResult() {
        return result;
    }

    public Type getRequest() {
        return request;
    }
}
