package com.queo.config.configurations;

import com.queo.config.BaseSubConfiguration;
import com.queo.config.KaplansFacadeConfiguration;
import com.queo.loosely.coupled.FunctionalServiceDetails;

import java.lang.annotation.Annotation;
import java.util.*;

public class ConfigurationForScanning extends BaseSubConfiguration {

    private final Set<Class<? extends Annotation>> customServiceAnnotations;

    private final Set<Class<? extends Annotation>> customBeanAnnotations;

    private final Map<Class<?>, Class<? extends Annotation>> additionalClasses;

    private final Set<FunctionalServiceDetails> serviceDetailsCreatedCallbacks;

    private ClassLoader classLoader;

    public ConfigurationForScanning(KaplansFacadeConfiguration parentConfig) {
        super(parentConfig);
        this.customServiceAnnotations = new HashSet<>();
        this.customBeanAnnotations = new HashSet<>();
        this.additionalClasses = new HashMap<>();
        this.serviceDetailsCreatedCallbacks = new HashSet<>();
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public ConfigurationForScanning addCustomServiceAnnotation(Class<? extends Annotation> annotation) {
        this.customServiceAnnotations.add(annotation);
        return this;
    }

    public ConfigurationForScanning addCustomServiceAnnotations(Collection<Class<? extends Annotation>> annotations) {
        this.customServiceAnnotations.addAll(Set.copyOf(annotations));
        return this;
    }

    public ConfigurationForScanning addCustomBeanAnnotation(Class<? extends Annotation> annotation) {
        this.customBeanAnnotations.add(annotation);
        return this;
    }

    public ConfigurationForScanning addCustomBeanAnnotations(Collection<Class<? extends Annotation>> annotations) {
        this.customBeanAnnotations.addAll(Set.copyOf(annotations));
        return this;
    }

    public ConfigurationForScanning addAdditionalClassesForScanning(Map<Class<?>, Class<? extends Annotation>> additionalClasses) {
        this.additionalClasses.putAll(additionalClasses);
        return this;
    }

    public ConfigurationForScanning addServiceDetailsCreatedCallback(FunctionalServiceDetails serviceDetailsCreated) {
        this.serviceDetailsCreatedCallbacks.add(serviceDetailsCreated);
        return this;
    }

    public ConfigurationForScanning setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public Set<Class<? extends Annotation>> getCustomBeanAnnotations() {
        return this.customBeanAnnotations;
    }

    public Set<Class<? extends Annotation>> getCustomServiceAnnotations() {
        return this.customServiceAnnotations;
    }

    public Map<Class<?>, Class<? extends Annotation>> getAdditionalClasses() {
        return this.additionalClasses;
    }

    public Set<FunctionalServiceDetails> getServiceDetailsCreatedCallbacks() {
        return this.serviceDetailsCreatedCallbacks;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
}
