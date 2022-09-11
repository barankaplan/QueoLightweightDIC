package com.queo.models;

import com.queo.annotations.KaplansNamedField;
import com.queo.utils.AliasFinder;
import com.queo.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.LinkedList;


public class EnqueuedServiceDetails {

    private final ModelForServiceDetails serviceDetails;


    private final LinkedList<ModelForDependencies> constructorParams;

    private final LinkedList<ModelForDependencies> fieldDependencies;

    public EnqueuedServiceDetails(ModelForServiceDetails serviceDetails) {
        this.serviceDetails = serviceDetails;
        this.constructorParams = new LinkedList<>();
        this.fieldDependencies = new LinkedList<>();
        this.fillConstructorParams();
        this.fillFieldDependencyTypes();
    }

    public ModelForServiceDetails getServiceDetails() {
        return this.serviceDetails;
    }

    public LinkedList<ModelForDependencies> getConstructorParams() {
        return this.constructorParams;
    }

    public Object[] getConstructorInstances() {
        return this.constructorParams.stream()
                .map(ModelForDependencies::getInstance)
                .toArray(Object[]::new);
    }

    public LinkedList<ModelForDependencies> getFieldDependencies() {
        return this.fieldDependencies;
    }

    public Object[] getFieldInstances() {
        return this.fieldDependencies.stream()
                .map(ModelForDependencies::getInstance)
                .toArray(Object[]::new);
    }

    private void fillConstructorParams() {
        for (Parameter parameter : this.serviceDetails.getTargetConstructor().getParameters()) {
            this.constructorParams.add(new ModelForDependencies(
                    parameter.getType(),
                    this.getInstanceName(parameter.getDeclaredAnnotations()),
                    parameter.getDeclaredAnnotations()
            ));
        }
    }

    private void fillFieldDependencyTypes() {
        for (Field autowireAnnotatedField : this.serviceDetails.getAutowireAnnotatedFields()) {
            this.fieldDependencies.add(new ModelForDependencies(
                    autowireAnnotatedField.getType(),
                    this.getInstanceName(autowireAnnotatedField.getDeclaredAnnotations()),
                    autowireAnnotatedField.getDeclaredAnnotations()
            ));
        }
    }

    private String getInstanceName(Annotation[] annotations) {
        final Annotation annotation = AliasFinder.getAnnotation(annotations, KaplansNamedField.class);

        if (annotation != null) {
            return AnnotationUtils.getAnnotationValue(annotation).toString();
        }

        return null;
    }

    @Override
    public String toString() {
        return this.serviceDetails.getServiceType().getName();
    }
}
