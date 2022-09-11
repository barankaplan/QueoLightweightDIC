package com.queo.services;

import com.queo.config.configurations.ConfigurationForInstantiation;
import com.queo.exceptions.ServiceInstantiationException;
import com.queo.models.EnqueuedServiceDetails;
import com.queo.models.ModelForServiceBeanDetails;
import com.queo.models.ModelForServiceDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;


public class ServicesInstantiationServiceImpl {

    private static final String MAX_NUMBER_OF_ALLOWED_ITERATIONS_REACHED = "Maximum number of allowed iterations was reached '%s'. Remaining services: \n %s";


    private final ConfigurationForInstantiation configuration;

    private final ObjectInstantiationServiceImpl instantiationService;

    private final DependencyResolveService dependencyResolveService;


    private final LinkedList<EnqueuedServiceDetails> enqueuedServiceDetails;


    private final LightweightDIC tempContainer;

    public ServicesInstantiationServiceImpl(ConfigurationForInstantiation configuration,
                                            ObjectInstantiationServiceImpl instantiationService,
                                            DependencyResolveService dependencyResolveService) {
        this.configuration = configuration;
        this.instantiationService = instantiationService;
        this.dependencyResolveService = dependencyResolveService;
        this.enqueuedServiceDetails = new LinkedList<>();
        this.tempContainer = new KaplansLightweightDIC();
    }


    public Collection<ModelForServiceDetails> instantiateServicesAndBeans(Set<ModelForServiceDetails> mappedServices) throws ServiceInstantiationException {
        this.init(mappedServices);

        int counter = 0;
        final int maxNumberOfIterations = this.configuration.getMaximumAllowedIterations();
        while (!this.enqueuedServiceDetails.isEmpty()) {
            if (counter > maxNumberOfIterations) {
                throw new ServiceInstantiationException(String.format(
                        MAX_NUMBER_OF_ALLOWED_ITERATIONS_REACHED,
                        maxNumberOfIterations,
                        this.enqueuedServiceDetails)
                );
            }

            final EnqueuedServiceDetails enqueuedServiceDetails = this.enqueuedServiceDetails.removeFirst();

            if (this.dependencyResolveService.isServiceResolved(enqueuedServiceDetails)) {
                this.handleServiceResolved(enqueuedServiceDetails);
            } else {
                this.enqueuedServiceDetails.addLast(enqueuedServiceDetails);
                counter++;
            }
        }

        return this.tempContainer.getAllServices();
    }

    private void handleServiceResolved(EnqueuedServiceDetails enqueuedServiceDetails) {
        final ModelForServiceDetails serviceDetails = enqueuedServiceDetails.getServiceDetails();
        final Object[] constructorInstances = enqueuedServiceDetails.getConstructorInstances();

        this.instantiationService.createInstance(
                serviceDetails,
                constructorInstances,
                enqueuedServiceDetails.getFieldInstances()
        );


        this.registerResolvedDependencies(enqueuedServiceDetails);
        this.registerInstantiatedService(serviceDetails);
        this.registerBeans(serviceDetails);
    }


    private void registerBeans(ModelForServiceDetails serviceDetails) {
        for (ModelForServiceBeanDetails beanDetails : serviceDetails.getBeans()) {
            this.instantiationService.createBeanInstance(beanDetails);


            this.registerInstantiatedService(beanDetails);
        }
    }


    private void registerInstantiatedService(ModelForServiceDetails newlyCreatedService) {
        this.tempContainer.getAllServices().add(newlyCreatedService);

        for (EnqueuedServiceDetails enqueuedService : this.enqueuedServiceDetails) {
            this.addDependencyIfRequired(enqueuedService, newlyCreatedService);
        }
    }

    private void addDependencyIfRequired(EnqueuedServiceDetails enqueuedService, ModelForServiceDetails newlyCreatedService) {
        if (this.dependencyResolveService.isDependencyRequired(enqueuedService, newlyCreatedService)) {
            this.dependencyResolveService.addDependency(
                    enqueuedService,
                    this.tempContainer.getServiceDetails(
                            newlyCreatedService.getServiceType(),
                            newlyCreatedService.getInstanceName()
                    )
            );

            this.addDependencyIfRequired(enqueuedService, newlyCreatedService);
        }
    }

    private void registerResolvedDependencies(EnqueuedServiceDetails enqueuedServiceDetails) {
        final ModelForServiceDetails serviceDetails = enqueuedServiceDetails.getServiceDetails();

        serviceDetails.setResolvedConstructorParams(enqueuedServiceDetails.getConstructorParams());
        serviceDetails.setResolvedFields(enqueuedServiceDetails.getFieldDependencies());
    }


    private void init(Set<ModelForServiceDetails> mappedServices) {
        this.enqueuedServiceDetails.clear();
        this.tempContainer.init(new ArrayList<>(), new ArrayList<>(), new ObjectInstantiationServiceImpl());

        for (ModelForServiceDetails serviceDetails : mappedServices) {
            this.enqueuedServiceDetails.add(new EnqueuedServiceDetails(serviceDetails));
        }

        for (ModelForServiceDetails instantiatedService : this.configuration.getProvidedServices()) {
            this.registerInstantiatedService(instantiatedService);
        }

        this.dependencyResolveService.init(mappedServices);
        this.dependencyResolveService.checkDependencies(this.enqueuedServiceDetails);
    }
}
