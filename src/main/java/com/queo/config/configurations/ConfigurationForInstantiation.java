package com.queo.config.configurations;

import com.queo.config.BaseSubConfiguration;
import com.queo.config.KaplansFacadeConfiguration;
import com.queo.constants.Constants;
import com.queo.loosely.coupled.DependencyResolver;
import com.queo.models.ModelForServiceDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConfigurationForInstantiation extends BaseSubConfiguration {

    private int maximumAllowedIterations;

    private final Collection<ModelForServiceDetails> providedServices;

    private final Set<DependencyResolver> dependencyResolvers;

    public ConfigurationForInstantiation(KaplansFacadeConfiguration parentConfig) {
        super(parentConfig);
        this.providedServices = new ArrayList<>();
        this.maximumAllowedIterations = Constants.MAX_NUMBER_OF_INSTANTIATION_ITERATIONS;
        this.dependencyResolvers = new HashSet<>();
    }



    public int getMaximumAllowedIterations() {
        return this.maximumAllowedIterations;
    }

    public ConfigurationForInstantiation addProvidedServices(Collection<ModelForServiceDetails> serviceDetails) {
        this.providedServices.addAll(serviceDetails);
        return this;
    }


    public Collection<ModelForServiceDetails> getProvidedServices() {
        return this.providedServices;
    }

    public Set<DependencyResolver> getDependencyResolvers() {
        return this.dependencyResolvers;
    }
}
