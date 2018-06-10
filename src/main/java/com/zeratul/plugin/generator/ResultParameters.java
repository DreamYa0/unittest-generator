package com.zeratul.plugin.generator;

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

    public ResultParameters parameterName(String parameterName) {
        this.parameterName = parameterName;
        return this;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterType() {
        return parameterType;
    }

    public ResultParameters parameterType(String parameterType) {
        this.parameterType = parameterType;
        return this;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public ResultParameters parameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
        return this;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public String getParameterDesc() {
        return parameterDesc;
    }

    public ResultParameters parameterDesc(String parameterDesc) {
        this.parameterDesc = parameterDesc;
        return this;
    }

    public void setParameterDesc(String parameterDesc) {
        this.parameterDesc = parameterDesc;
    }

    public Methods getMethods() {
        return methods;
    }

    public ResultParameters methods(Methods methods) {
        this.methods = methods;
        return this;
    }

    public void setMethods(Methods methods) {
        this.methods = methods;
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
        return "ResultParameters{" +
            "id=" + getId() +
            ", parameterName='" + getParameterName() + "'" +
            ", parameterType='" + getParameterType() + "'" +
            ", parameterValue='" + getParameterValue() + "'" +
            ", parameterDesc='" + getParameterDesc() + "'" +
            "}";
    }
}
