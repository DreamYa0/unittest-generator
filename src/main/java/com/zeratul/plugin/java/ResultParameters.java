package com.zeratul.plugin.java;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.Objects;

/**
 * A ResultParameters.
 */

public class ResultParameters implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String parameterName;

    private String parameterType;

    private String parameterValue;

    private String parameterDesc;

    private Methods methods;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public ResultParameters parameterName(String parameterName) {
        this.parameterName = parameterName;
        return this;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public ResultParameters parameterType(String parameterType) {
        this.parameterType = parameterType;
        return this;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public ResultParameters parameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
        return this;
    }

    public String getParameterDesc() {
        return parameterDesc;
    }

    public void setParameterDesc(String parameterDesc) {
        this.parameterDesc = parameterDesc;
    }

    public ResultParameters parameterDesc(String parameterDesc) {
        this.parameterDesc = parameterDesc;
        return this;
    }

    public Methods getMethods() {
        return methods;
    }

    public void setMethods(Methods methods) {
        this.methods = methods;
    }

    public ResultParameters methods(Methods methods) {
        this.methods = methods;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResultParameters resultParameters = (ResultParameters) o;
        if (resultParameters.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), resultParameters.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {

        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", getId())
                .add("parameterName", getParameterName())
                .add("parameterType", getParameterType())
                .add("parameterValue", getParameterValue())
                .add("parameterDesc", getParameterDesc())
                .toString();
    }
}
