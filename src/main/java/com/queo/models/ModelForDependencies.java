package com.queo.models;

import com.queo.loosely.coupled.DependencyResolver;

import java.lang.annotation.Annotation;

public class ModelForDependencies {

    private final Class<?> dependencyType;

    private final String instanceName;

    private final Annotation[] annotations;

    private Object instance;

    private boolean isRequired;

    private boolean isValuePresent;

    private DependencyResolver dependencyResolver;

    public ModelForDependencies(Class<?> dependencyType, String instanceName, Annotation[] annotations) {
        this.dependencyType = dependencyType;
        this.instanceName = instanceName;
        this.annotations = annotations;
        this.setRequired(true);
        this.setValuePresent(false);
    }

    public Class<?> getDependencyType() {
        return this.dependencyType;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public Annotation[] getAnnotations() {
        return this.annotations;
    }

    public Object getInstance() {
        return this.instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public boolean isRequired() {
        return this.isRequired;
    }

    public void setRequired(boolean required) {
        this.isRequired = required;
    }

    public boolean isValuePresent() {
        return this.isValuePresent;
    }

    public void setValuePresent(boolean valuePresent) {
        this.isValuePresent = valuePresent;
    }

    public DependencyResolver getDependencyResolver() {
        return this.dependencyResolver;
    }

    public void setDependencyResolver(DependencyResolver dependencyResolver) {
        this.dependencyResolver = dependencyResolver;
    }
}
