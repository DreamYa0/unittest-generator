package com.zeratul.plugin.java;

import com.github.javaparser.ast.Modifier;
import com.google.common.base.MoreObjects;

import java.util.EnumSet;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:24
 * @since 1.0.0
 */
public class Field {

    public String name;
    public EnumSet<Modifier> modifiers;
    public String phpTypeWithNoBracket;
    public String javaType;
    public String wrapJType;
    public String comment;
    public boolean isRequest;
    public boolean enableResult;
    public boolean enableCheck;
    public boolean isGeneric;
    public String trueType;

    public Field() {

    }

    public Field(String name, String javaType, String comment) {
        this.name = name;
        this.javaType = javaType;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhpTypeWithNoBracket() {
        return phpTypeWithNoBracket;
    }

    public void setPhpTypeWithNoBracket(String phpTypeWithNoBracket) {
        this.phpTypeWithNoBracket = phpTypeWithNoBracket;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getWrapJType() {
        return wrapJType;
    }

    public void setWrapJType(String wrapJType) {
        this.wrapJType = wrapJType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean request) {
        isRequest = request;
    }

    public boolean isEnableResult() {
        return enableResult;
    }

    public void setEnableResult(boolean enableResult) {
        this.enableResult = enableResult;
    }

    public boolean isEnableCheck() {
        return enableCheck;
    }

    public void setEnableCheck(boolean enableCheck) {
        this.enableCheck = enableCheck;
    }

    public boolean isGeneric() {
        return isGeneric;
    }

    public void setGeneric(boolean generic) {
        isGeneric = generic;
    }

    public String getTrueType() {
        return trueType;
    }

    public void setTrueType(String trueType) {
        this.trueType = trueType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("name", name)
                .add("modifiers", modifiers)
                .add("phpTypeWithNoBracket", phpTypeWithNoBracket)
                .add("javaType", javaType)
                .add("wrapJType", wrapJType)
                .add("comment", comment)
                .add("isRequest", isRequest)
                .add("enableResult", enableResult)
                .add("isGeneric", isGeneric)
                .add("trueType", trueType).toString();
    }
}
