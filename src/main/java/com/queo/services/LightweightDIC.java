package com.queo.services;

import com.queo.exceptions.AlreadyInitializedException;
import com.queo.models.ModelForServiceDetails;

import java.lang.annotation.Annotation;
import java.util.Collection;

public interface LightweightDIC {

    void init(Collection<Class<?>> locatedClasses, Collection<ModelForServiceDetails> servicesAndBeans, ObjectInstantiationServiceImpl instantiationService) throws AlreadyInitializedException;

    void reload(ModelForServiceDetails serviceDetails);

    void reload(Class<?> serviceType);

    void update(Object service);

    void update(Class<?> serviceType, Object serviceInstance);

    void update(Class<?> serviceType, Object serviceInstance, boolean destroyOldInstance);

    <T> T getService(Class<T> serviceType);

    <T> T getService(Class<?> serviceType, String instanceName);

    <T> T getNewInstance(Class<?> serviceType);

    <T> T getNewInstance(Class<?> serviceType, String instanceName);

    ModelForServiceDetails getServiceDetails(Class<?> serviceType);

    ModelForServiceDetails getServiceDetails(Class<?> serviceType, String instanceName);

    Collection<Class<?>> getAllScannedClasses();

    Collection<ModelForServiceDetails> getImplementations(Class<?> serviceType);

    Collection<ModelForServiceDetails> getServicesByAnnotation(Class<? extends Annotation> annotationType);

    Collection<ModelForServiceDetails> getAllServices();
}
