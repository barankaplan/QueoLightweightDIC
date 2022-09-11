package com.queo.services;


import com.queo.config.configurations.ConfigurationForInstantiation;
import com.queo.exceptions.ServiceInstantiationException;
import com.queo.loosely.coupled.DependencyResolver;
import com.queo.models.EnqueuedServiceDetails;
import com.queo.models.ModelForDependencies;
import com.queo.models.ModelForServiceBeanDetails;
import com.queo.models.ModelForServiceDetails;
import com.queo.utils.DependencyParamUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DependencyResolveService {

    private static final String COULD_NOT_CREATE_INSTANCE_MISSING_DEPENDENCY_MSG = "Could not create instance of '%s'. Parameter '%s' implementation was not found";
    private static final String COULD_NOT_FIND_NAMED_MSG = "Could not create instance of '%s'. Named '%s' was not found.";


    private final List<Class<?>> allAvailableClasses;

    private final ConfigurationForInstantiation configuration;

    private Collection<ModelForServiceDetails> mappedServices;

    public DependencyResolveService(ConfigurationForInstantiation configuration) {
        this.allAvailableClasses = new ArrayList<>();
        this.configuration = configuration;
    }


    public void init(Collection<ModelForServiceDetails> mappedServices) {
        this.allAvailableClasses.clear();
        this.mappedServices = mappedServices;

        for (ModelForServiceDetails serviceDetails : this.mappedServices) {
            this.allAvailableClasses.add(serviceDetails.getServiceType());
            this.allAvailableClasses.addAll(serviceDetails.getBeans().stream()
                    .map(e -> e.getOriginMethod().getReturnType())
                    .collect(Collectors.toList())
            );
        }

        this.allAvailableClasses.addAll(this.configuration.getProvidedServices()
                .stream()
                .map(ModelForServiceDetails::getServiceType)
                .collect(Collectors.toList())
        );
    }


    public void checkDependencies(Collection<EnqueuedServiceDetails> enqueuedServiceDetails) throws ServiceInstantiationException {
        for (EnqueuedServiceDetails enqueuedService : enqueuedServiceDetails) {
            final Class<?> serviceType = enqueuedService.getServiceDetails().getServiceType();

            this.checkDependencyParameters(serviceType, enqueuedService.getConstructorParams());
            this.checkDependencyParameters(serviceType, enqueuedService.getFieldDependencies());
        }
    }

    private void checkDependencyParameters(Class<?> serviceType, Collection<ModelForDependencies> dependencyParams) {
        for (ModelForDependencies dependencyParam : dependencyParams) {
            final Class<?> dependencyType = dependencyParam.getDependencyType();


            if (dependencyParam.getInstanceName() != null) {
                if (this.isNamedInstancePresent(dependencyType, dependencyParam.getInstanceName())) {
                    dependencyParam.setValuePresent(true);
                    continue;
                }

                if (dependencyParam.isRequired()) {
                    throw new ServiceInstantiationException(String.format(
                            COULD_NOT_FIND_NAMED_MSG, serviceType.getName(), dependencyParam.getInstanceName()
                    ));
                }

                continue;
            }

            if (this.isAssignableTypePresent(dependencyType)) {
                dependencyParam.setValuePresent(true);
                continue;
            }

            final DependencyResolver dependencyResolver = this.getDependencyResolver(dependencyParam);
            if (dependencyResolver != null) {
                dependencyParam.setInstance(dependencyResolver.resolve(dependencyParam));
                dependencyParam.setDependencyResolver(dependencyResolver);
                dependencyParam.setValuePresent(true);
                continue;
            }

            if (dependencyParam.isRequired()) {
                throw new ServiceInstantiationException(
                        String.format(COULD_NOT_CREATE_INSTANCE_MISSING_DEPENDENCY_MSG,
                                serviceType.getName(),
                                dependencyType.getName()
                        )
                );
            }
        }
    }

    public void addDependency(EnqueuedServiceDetails enqueuedServiceDetails, ModelForServiceDetails serviceDetails) {
        if (!this.addDependency(enqueuedServiceDetails.getConstructorParams(), serviceDetails)) {
            this.addDependency(enqueuedServiceDetails.getFieldDependencies(), serviceDetails);
        }
    }

    private boolean addDependency(Collection<ModelForDependencies> dependencyParams, ModelForServiceDetails serviceDetails) {
        for (ModelForDependencies dependencyParam : dependencyParams) {
            if (DependencyParamUtils.isDependencyRequired(dependencyParam, serviceDetails)) {
                dependencyParam.setInstance(serviceDetails.getInstance());
                return true;
            }
        }

        return false;
    }


    public boolean isServiceResolved(EnqueuedServiceDetails serviceDetails) {
        return DependencyParamUtils.dependencyParamsResolved(serviceDetails.getConstructorParams()) &&
                DependencyParamUtils.dependencyParamsResolved(serviceDetails.getFieldDependencies());
    }

    public boolean isDependencyRequired(EnqueuedServiceDetails enqueuedServiceDetails, ModelForServiceDetails serviceDetails) {
        return DependencyParamUtils.isDependencyRequired(enqueuedServiceDetails.getConstructorParams(), serviceDetails) ||
                DependencyParamUtils.isDependencyRequired(enqueuedServiceDetails.getFieldDependencies(), serviceDetails);
    }


    private boolean isAssignableTypePresent(Class<?> cls) {
        for (Class<?> serviceType : this.allAvailableClasses) {
            if (cls.isAssignableFrom(serviceType)) {
                return true;
            }
        }

        return false;
    }

    private boolean isNamedInstancePresent(Class<?> cls, String nameOfInstance) {
        return this.isNamedInstancePresent(cls, nameOfInstance, this.mappedServices) ||
                this.isNamedInstancePresent(cls, nameOfInstance, this.configuration.getProvidedServices());
    }

    private boolean isNamedInstancePresent(Class<?> cls, String nameOfInstance, Collection<ModelForServiceDetails> serviceDetails) {
        for (ModelForServiceDetails providedService : serviceDetails) {
            if (nameOfInstance.equalsIgnoreCase(providedService.getInstanceName()) &&
                    cls.isAssignableFrom(providedService.getServiceType())) {
                return true;
            }

            for (ModelForServiceBeanDetails bean : providedService.getBeans()) {
                if (nameOfInstance.equalsIgnoreCase(bean.getInstanceName()) &&
                        cls.isAssignableFrom(bean.getServiceType())) {
                    return true;
                }
            }
        }

        return false;
    }

    private DependencyResolver getDependencyResolver(ModelForDependencies dependencyParam) {
        return this.configuration.getDependencyResolvers().stream()
                .filter(dr -> dr.canResolve(dependencyParam))
                .findFirst().orElse(null);
    }
}
