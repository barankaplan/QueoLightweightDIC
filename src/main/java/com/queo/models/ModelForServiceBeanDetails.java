package com.queo.models;

import com.queo.enums.ScopeType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class ModelForServiceBeanDetails extends ModelForServiceDetails {

    private final Method originMethod;


    private final ModelForServiceDetails rootService;

    public ModelForServiceBeanDetails(Class<?> beanType, Method originMethod,
                                      ModelForServiceDetails rootService, Annotation annotation,
                                      ScopeType scopeType,
                                      String instanceName) {
        super.setServiceType(beanType);
        super.setBeans(new ArrayList<>(0));
        this.originMethod = originMethod;
        this.rootService = rootService;
        super.setAnnotation(annotation);
        super.setScopeType(scopeType);
        super.setInstanceName(instanceName);
    }

    public Method getOriginMethod() {
        return this.originMethod;
    }

    public ModelForServiceDetails getRootService() {
        return this.rootService;
    }
}
