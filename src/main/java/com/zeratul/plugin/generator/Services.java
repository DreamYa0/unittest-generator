package com.zeratul.plugin.generator;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Services.
 */

public class Services implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String serviceName;
    private String serviceDesc;
    private Packages packages;
    private Set<Methods> methods = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Services serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDesc() {
        return serviceDesc;
    }

    public Services serviceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
        return this;
    }

    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public Packages getPackages() {
        return packages;
    }

    public Services packages(Packages packages) {
        this.packages = packages;
        return this;
    }

    public void setPackages(Packages packages) {
        this.packages = packages;
    }

    public Set<Methods> getMethods() {
        return methods;
    }

    public Services methods(Set<Methods> methods) {
        this.methods = methods;
        return this;
    }

    public Services addMethods(Methods methods) {
        this.methods.add(methods);
        methods.setServices(this);
        return this;
    }

    public Services removeMethods(Methods methods) {
        this.methods.remove(methods);
        methods.setServices(null);
        return this;
    }

    public void setMethods(Set<Methods> methods) {
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
        Services services = (Services) o;
        if (services.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), services.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Services{" +
            "id=" + getId() +
            ", serviceName='" + getServiceName() + "'" +
            ", serviceDesc='" + getServiceDesc() + "'" +
            "}";
    }
}
