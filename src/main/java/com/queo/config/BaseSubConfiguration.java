package com.queo.config;

public abstract class BaseSubConfiguration {

    private final KaplansFacadeConfiguration parentConfig;

    protected BaseSubConfiguration(KaplansFacadeConfiguration parentConfig) {
        this.parentConfig = parentConfig;
    }

    public KaplansFacadeConfiguration and() {
        return this.parentConfig;
    }
}
