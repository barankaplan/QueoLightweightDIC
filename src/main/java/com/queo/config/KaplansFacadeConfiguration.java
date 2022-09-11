package com.queo.config;

import com.queo.config.configurations.ConfigurationForInstantiation;
import com.queo.config.configurations.ConfigurationForScanning;

public class KaplansFacadeConfiguration {

    private final ConfigurationForScanning annotations;

    private final ConfigurationForInstantiation instantiations;

    public KaplansFacadeConfiguration() {
        this.annotations = new ConfigurationForScanning(this);
        this.instantiations = new ConfigurationForInstantiation(this);
    }

    public ConfigurationForScanning scanning() {
        return this.annotations;
    }

    public ConfigurationForInstantiation instantiations() {
        return this.instantiations;
    }

    public KaplansFacadeConfiguration build() {
        return this;
    }
}
