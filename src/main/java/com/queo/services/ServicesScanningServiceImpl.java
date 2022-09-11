package com.queo.services;

import com.queo.annotations.KaplansBean;
import com.queo.annotations.KaplansInject;
import com.queo.annotations.KaplansNamedClass;
import com.queo.annotations.Scope;
import com.queo.config.configurations.ConfigurationForScanning;
import com.queo.enums.ScopeType;
import com.queo.loosely.coupled.FunctionalServiceDetails;
import com.queo.models.ModelForServiceBeanDetails;
import com.queo.models.ModelForServiceDetails;
import com.queo.utils.AliasFinder;
import com.queo.utils.AnnotationUtils;
import com.queo.utils.ServiceDetailsConstructComparator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


public class ServicesScanningServiceImpl {


    private final ConfigurationForScanning configuration;

    public ServicesScanningServiceImpl(ConfigurationForScanning configuration) {
        this.configuration = configuration;
        this.init();
    }


    public Set<ModelForServiceDetails> mapServices(Set<Class<?>> locatedClasses) {
        final Map<Class<?>, Annotation> onlyServiceClasses = this.filterServiceClasses(locatedClasses);

        final Set<ModelForServiceDetails> serviceDetailsStorage = new HashSet<>();

        for (Map.Entry<Class<?>, Annotation> serviceAnnotationEntry : onlyServiceClasses.entrySet()) {
            final Class<?> cls = serviceAnnotationEntry.getKey();
            final Annotation annotation = serviceAnnotationEntry.getValue();

            final ModelForServiceDetails serviceDetails = new ModelForServiceDetails(
                    cls,
                    annotation,
                    this.findSuitableConstructor(cls),
                    this.findInstanceName(cls.getDeclaredAnnotations()),

                    this.findScope(cls),
                    this.findAutowireAnnotatedFields(cls, new ArrayList<>()).toArray(new Field[0])
            );

            serviceDetails.setBeans(this.findBeans(serviceDetails));

            for (FunctionalServiceDetails callback : this.configuration.getServiceDetailsCreatedCallbacks()) {
                callback.serviceDetailsCreated(serviceDetails);
            }

            serviceDetailsStorage.add(serviceDetails);
        }

        return serviceDetailsStorage.stream()
                .sorted(new ServiceDetailsConstructComparator())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }


    private Map<Class<?>, Annotation> filterServiceClasses(Collection<Class<?>> scannedClasses) {
        final Set<Class<? extends Annotation>> serviceAnnotations = this.configuration.getCustomServiceAnnotations();
        final Map<Class<?>, Annotation> locatedClasses = new HashMap<>();

        for (Class<?> cls : scannedClasses) {
            if (cls.isInterface() || cls.isEnum() || cls.isAnnotation()) {
                continue;
            }

            for (Annotation annotation : cls.getAnnotations()) {
                if (serviceAnnotations.contains(annotation.annotationType())) {
                    locatedClasses.put(cls, annotation);
                    break;
                }
            }
        }

        this.configuration.getAdditionalClasses().forEach((cls, a) -> {
            Annotation annotation = null;
            if (a != null && cls.isAnnotationPresent(a)) {
                annotation = cls.getAnnotation(a);
            }

            locatedClasses.put(cls, annotation);
        });

        return locatedClasses;
    }


    private Constructor<?> findSuitableConstructor(Class<?> cls) {
        for (Constructor<?> ctr : cls.getDeclaredConstructors()) {
            if (AliasFinder.isAnnotationPresent(ctr.getDeclaredAnnotations(), KaplansInject.class)) {
                ctr.setAccessible(true);
                return ctr;
            }
        }

        return cls.getConstructors()[0];
    }

    private Method findVoidMethodWithZeroParamsAndAnnotations(Class<? extends Annotation> annotation, Class<?> cls) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getParameterCount() != 0 ||
                    (method.getReturnType() != void.class && method.getReturnType() != Void.class)) {
                continue;
            }

            if (AliasFinder.isAnnotationPresent(method.getDeclaredAnnotations(), annotation)) {
                method.setAccessible(true);
                return method;
            }
        }

        if (cls.getSuperclass() != null) {
            return this.findVoidMethodWithZeroParamsAndAnnotations(annotation, cls.getSuperclass());
        }

        return null;
    }


    private Collection<ModelForServiceBeanDetails> findBeans(ModelForServiceDetails rootService) {
        final Set<Class<? extends Annotation>> beanAnnotations = this.configuration.getCustomBeanAnnotations();
        final Set<ModelForServiceBeanDetails> beans = new HashSet<>();

        for (Method method : rootService.getServiceType().getDeclaredMethods()) {
            if (method.getParameterCount() != 0 || method.getReturnType() == void.class || method.getReturnType() == Void.class) {
                continue;
            }

            final Annotation[] methodDeclaredAnnotations = method.getDeclaredAnnotations();

            for (Class<? extends Annotation> beanAnnotation : beanAnnotations) {
                if (AliasFinder.isAnnotationPresent(methodDeclaredAnnotations, beanAnnotation)) {
                    method.setAccessible(true);
                    beans.add(new ModelForServiceBeanDetails(
                            method.getReturnType(),
                            method,
                            rootService,
                            AliasFinder.getAnnotation(methodDeclaredAnnotations, beanAnnotation),
                            this.findScope(method),
                            this.findInstanceName(method.getDeclaredAnnotations())
                    ));

                    break;
                }
            }
        }

        return beans;
    }


    private ScopeType findScope(Class<?> cls) {
        if (cls.isAnnotationPresent(Scope.class)) {
            return cls.getDeclaredAnnotation(Scope.class).value();
        }

        return ScopeType.DEFAULT_SCOPE;
    }


    private ScopeType findScope(Method method) {
        if (method.isAnnotationPresent(Scope.class)) {
            return method.getDeclaredAnnotation(Scope.class).value();
        }

        return ScopeType.DEFAULT_SCOPE;
    }

    private String findInstanceName(Annotation[] annotations) {
        if (!AliasFinder.isAnnotationPresent(annotations, KaplansNamedClass.class)) {
            return null;
        }

        final Annotation annotation = AliasFinder.getAnnotation(annotations, KaplansNamedClass.class);

        return AnnotationUtils.getAnnotationValue(annotation).toString();
    }

    private List<Field> findAutowireAnnotatedFields(Class<?> cls, List<Field> fields) {
        for (Field declaredField : cls.getDeclaredFields()) {
            if (AliasFinder.isAnnotationPresent(declaredField.getDeclaredAnnotations(), KaplansInject.class)) {
                declaredField.setAccessible(true);
                fields.add(declaredField);
            }
        }

        if (cls.getSuperclass() != null) {
            return this.findAutowireAnnotatedFields(cls.getSuperclass(), fields);
        }

        return fields;
    }


    private void init() {
        this.configuration.getCustomServiceAnnotations().add(KaplansBean.class);
    }
}
