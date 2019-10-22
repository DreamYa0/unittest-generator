package com.atomic.plugin.java;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:25
 * @since 1.0.0
 */
public class JavaAstModel {

    public String packageName;
    public boolean isInterfazz;
    public boolean isEnum;
    public String className;
    public String comments;
    public Set<String> imports = Sets.newConcurrentHashSet();
    public Map<String, String> importsMap = Maps.newConcurrentMap();
    public Set<String> depDtos = Sets.newConcurrentHashSet();
    public List<String> parents = Lists.newArrayList();
    public Set<String> interfazzs = Sets.newConcurrentHashSet();
    public List<Field> fields = Lists.newArrayList();
    public List<Method> methods = Lists.newArrayList();

    public JavaAstModel() {
    }

    public String toString() {

        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("packageName", packageName)
                .add("isInterfazz", isInterfazz)
                .add("className", className)
                .add("comments", comments)
                .add("parents", parents)
                .add("interfazzs", interfazzs)
                .add("fields", fields)
                .add("methods", methods).toString();
    }
}
