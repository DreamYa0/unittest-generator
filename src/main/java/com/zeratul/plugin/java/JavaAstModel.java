package com.zeratul.plugin.java;

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
        StringBuilder sb = new StringBuilder("JavaAstModel{");
        sb.append("packageName=\'").append(this.packageName).append('\'');
        sb.append(", isInterfazz=").append(this.isInterfazz);
        sb.append(", className=\'").append(this.className).append('\'');
        sb.append(", comments=\'").append(this.comments).append('\'');
        sb.append(", parents=").append(this.parents);
        sb.append(", interfazzs=").append(this.interfazzs);
        sb.append(", fields=").append(this.fields);
        sb.append(", methods=").append(this.methods);
        sb.append('}');
        return sb.toString();
    }
}
