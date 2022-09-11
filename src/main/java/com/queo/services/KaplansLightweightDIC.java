package com.queo.services;

import com.queo.exceptions.AlreadyInitializedException;
import com.queo.models.ModelForDependencies;
import com.queo.models.ModelForServiceBeanDetails;
import com.queo.models.ModelForServiceDetails;
import com.queo.utils.ServiceCompatibilityUtils;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class KaplansLightweightDIC implements LightweightDIC {

    private static final String ALREADY_INITIALIZED_MSG = "Dependency container already initialized.";

    private static final String SERVICE_NOT_FOUND_FORMAT = "Service \"%s\" was not found.";

    private boolean isInit;

    private Collection<Class<?>> allLocatedClasses;

    private Collection<ModelForServiceDetails> servicesAndBeans;

    private ObjectInstantiationServiceImpl instantiationService;

    public KaplansLightweightDIC() {
        this.isInit = false;
    }

    @Override
    public void init(Collection<Class<?>> locatedClasses, Collection<ModelForServiceDetails> servicesAndBeans,
                     ObjectInstantiationServiceImpl instantiationService) throws AlreadyInitializedException {
        if (this.isInit) {
            throw new AlreadyInitializedException(ALREADY_INITIALIZED_MSG);
        }

        this.allLocatedClasses = locatedClasses;
        this.servicesAndBeans = servicesAndBeans;
        this.instantiationService = instantiationService;

        this.isInit = true;
    }


    @Override
    public void reload(ModelForServiceDetails serviceDetails) {
        final Object newInstance = this.getNewInstance(serviceDetails.getServiceType(), serviceDetails.getInstanceName());
        serviceDetails.setInstance(newInstance);
    }

    @Override
    public void reload(Class<?> serviceType) {
        final ModelForServiceDetails serviceDetails = this.findServiceDetails(serviceType, null);
        if (serviceDetails == null) {
            throw new IllegalArgumentException(String.format(SERVICE_NOT_FOUND_FORMAT, serviceType));
        }

        this.reload(serviceDetails);
    }

    @Override
    public void update(Object service) {
        this.update(service.getClass(), service);
    }


    @Override
    public void update(Class<?> serviceType, Object serviceInstance) {
        this.update(serviceType, serviceInstance, true);
    }

    @Override
    public void update(Class<?> serviceType, Object serviceInstance, boolean destroyOldInstance) {
        final ModelForServiceDetails serviceDetails = this.findServiceDetails(serviceType, null);
        if (serviceDetails == null) {
            throw new IllegalArgumentException(String.format(SERVICE_NOT_FOUND_FORMAT, serviceType.getName()));
        }


        serviceDetails.setInstance(serviceInstance);
    }


    @Override
    public <T> T getService(Class<T> serviceType) {
        return this.getService(serviceType, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<?> serviceType, String instanceName) {
        final ModelForServiceDetails serviceDetails = this.getServiceDetails(serviceType, instanceName);

        if (serviceDetails != null) {
            return (T) serviceDetails.getInstance();
        }

        if (serviceType.isAssignableFrom(this.getClass())) {
            return (T) this;
        }

        return null;
    }

    @Override
    public <T> T getNewInstance(Class<?> serviceType) {
        return this.getNewInstance(serviceType, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getNewInstance(Class<?> serviceType, String instanceName) {
        final ModelForServiceDetails serviceDetails = this.findServiceDetails(serviceType, instanceName);

        if (serviceDetails == null) {
            throw new IllegalArgumentException(String.format(SERVICE_NOT_FOUND_FORMAT, serviceType.getName()));
        }

        final Object oldInstance = serviceDetails.getActualInstance();

        if (serviceDetails instanceof ModelForServiceBeanDetails) {
            final ModelForServiceBeanDetails serviceBeanDetails = (ModelForServiceBeanDetails) serviceDetails;
            this.instantiationService.createBeanInstance(serviceBeanDetails);
        } else {
            this.instantiationService.createInstance(
                    serviceDetails,
                    this.collectDependencies(serviceDetails),
                    this.collectAutowiredFieldsDependencies(serviceDetails)
            );
        }

        final Object newInstance = serviceDetails.getActualInstance();
        serviceDetails.setInstance(oldInstance);

        return (T) newInstance;
    }


    @Override
    public ModelForServiceDetails getServiceDetails(Class<?> serviceType) {
        return this.getServiceDetails(serviceType, null);
    }


    @Override
    public ModelForServiceDetails getServiceDetails(Class<?> serviceType, String instanceName) {
        final ModelForServiceDetails serviceDetails = this.findServiceDetails(serviceType, instanceName);


        return serviceDetails;
    }


    private ModelForServiceDetails findServiceDetails(Class<?> serviceType, String instanceName) {
        return this.servicesAndBeans.stream()
                .filter(sd -> ServiceCompatibilityUtils.isServiceCompatible(sd, serviceType, instanceName))
                .findFirst().orElse(null);
    }


    @Override
    public Collection<Class<?>> getAllScannedClasses() {
        return this.allLocatedClasses;
    }


    @Override
    public Collection<ModelForServiceDetails> getImplementations(Class<?> serviceType) {
        return this.servicesAndBeans.stream()
                .filter(sd -> serviceType.isAssignableFrom(sd.getServiceType()))
                .collect(Collectors.toList());
    }


    @Override
    public Collection<ModelForServiceDetails> getServicesByAnnotation(Class<? extends Annotation> annotationType) {
        return this.servicesAndBeans.stream()
                .filter(sd -> sd.getAnnotation() != null && sd.getAnnotation().annotationType() == annotationType)
                .collect(Collectors.toList());
    }


    @Override
    public Collection<ModelForServiceDetails> getAllServices() {
        return this.servicesAndBeans;
    }


    private Object[] collectDependencies(ModelForServiceDetails serviceDetails) {
        return this.getInstances(serviceDetails.getResolvedConstructorParams());
    }


    private Object[] collectAutowiredFieldsDependencies(ModelForServiceDetails serviceDetails) {
        return this.getInstances(serviceDetails.getResolvedFields());
    }


    private Object[] getInstances(List<ModelForDependencies> dependencyParams) {
        final Object[] instances = new Object[dependencyParams.size()];

        for (int i = 0; i < dependencyParams.size(); i++) {
            final ModelForDependencies dependencyParam = dependencyParams.get(i);

            if (dependencyParam.getDependencyResolver() != null) {
                instances[i] = dependencyParam.getDependencyResolver().resolve(dependencyParam);
                continue;
            }

            instances[i] = this.getService(dependencyParam.getDependencyType(), dependencyParam.getInstanceName());
        }

        return instances;
    }
}
