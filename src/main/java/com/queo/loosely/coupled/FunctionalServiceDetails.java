package com.queo.loosely.coupled;

import com.queo.models.ModelForServiceDetails;

@FunctionalInterface
public interface FunctionalServiceDetails {
    void serviceDetailsCreated(ModelForServiceDetails serviceDetails);
}
