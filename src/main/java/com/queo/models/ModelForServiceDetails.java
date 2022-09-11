package com.queo.models;

import com.queo.enums.ScopeType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;


public class ModelForServiceDetails {


    private Class<?> serviceType;


    private Annotation annotation;

    private Constructor<?> targetConstructor;

    private String instanceName;


    private Object instance;


    private ScopeType scopeType;


    private Collection<ModelForServiceBeanDetails> beans;


    private Field[] autowireAnnotatedFields;


    private LinkedList<ModelForDependencies> resolvedConstructorParams;


    private LinkedList<ModelForDependencies> resolvedFields;

    protected ModelForServiceDetails() {

    }

    public ModelForServiceDetails(Class<?> serviceType,
                                  Annotation annotation, Constructor<?> targetConstructor,
                                  String instanceName,

                                  ScopeType scopeType,
                                  Field[] autowireAnnotatedFields) {
        this();
        this.setServiceType(serviceType);
        this.setAnnotation(annotation);
        this.setTargetConstructor(targetConstructor);
        this.setInstanceName(instanceName);
        this.setScopeType(scopeType);
        this.setAutowireAnnotatedFields(autowireAnnotatedFields);
    }

    public Class<?> getServiceType() {
        return this.serviceType;
    }

    void setServiceType(Class<?> serviceType) {
        this.serviceType = serviceType;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Constructor<?> getTargetConstructor() {
        return this.targetConstructor;
    }

    public void setTargetConstructor(Constructor<?> targetConstructor) {
        this.targetConstructor = targetConstructor;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Object getActualInstance() {
        return this.instance;
    }

    public Object getInstance() {


        return this.instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }



    public ScopeType getScopeType() {
        return this.scopeType;
    }

    public void setScopeType(ScopeType scopeType) {
        this.scopeType = scopeType;
    }

    public Collection<ModelForServiceBeanDetails> getBeans() {
        return this.beans;
    }

    public void setBeans(Collection<ModelForServiceBeanDetails> beans) {
        this.beans = beans;
    }

    public Field[] getAutowireAnnotatedFields() {
        return this.autowireAnnotatedFields;
    }

    public void setAutowireAnnotatedFields(Field[] autowireAnnotatedFields) {
        this.autowireAnnotatedFields = autowireAnnotatedFields;
    }

    public LinkedList<ModelForDependencies> getResolvedConstructorParams() {
        return this.resolvedConstructorParams;
    }

    public void setResolvedConstructorParams(LinkedList<ModelForDependencies> resolvedConstructorParams) {
        this.resolvedConstructorParams = resolvedConstructorParams;
    }

    public LinkedList<ModelForDependencies> getResolvedFields() {
        return this.resolvedFields;
    }

    public void setResolvedFields(LinkedList<ModelForDependencies> resolvedFields) {
        this.resolvedFields = resolvedFields;
    }


    @Override
    public int hashCode() {
        if (this.serviceType == null) {
            return super.hashCode();
        }

        return this.serviceType.hashCode();
    }

    @Override
    public String toString() {
        if (this.serviceType == null) {
            return super.toString();
        }

        return this.serviceType.getName();
    }
}
