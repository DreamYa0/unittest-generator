package com.zeratul.plugin.java;

import com.github.javaparser.ast.Modifier;

import java.util.EnumSet;
import java.util.List;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:27
 * @since 1.0.0
 */
public class Method {

    public String name;
    public List<Pair<Field, String>> parameters;
    public boolean isGeneric;
    public String gennericType;
    public String comments;
    public EnumSet<Modifier> modifiers;
    public String body;
    public Field result;
    private String hessian;

    public Method() {
    }

    public String getHessian() {
        return this.hessian;
    }

    public void setHessian(String hessian) {
        this.hessian = hessian;
    }

    public String getName() {
        return this.name;
    }

    public String getComments() {
        return this.comments;
    }

    public List<Pair<Field, String>> getParam() {
        return this.parameters;
    }

    public Field getResult() {
        return this.result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Method{");
        sb.append("name=\'").append(this.name).append('\'');
        sb.append(", parameters=").append(this.parameters);
        sb.append(", result=").append(this.result);
        sb.append(", comments=\'").append(this.comments).append('\'');
        sb.append(", modifiers=").append(this.modifiers.toString());
        sb.append(", body=\'").append(this.body).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
