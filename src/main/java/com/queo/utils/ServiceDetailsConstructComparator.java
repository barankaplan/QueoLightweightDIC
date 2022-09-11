package com.queo.utils;

import com.queo.models.ModelForServiceDetails;

import java.util.Comparator;


public class ServiceDetailsConstructComparator implements Comparator<ModelForServiceDetails> {
    @Override
    public int compare(ModelForServiceDetails serviceDetails1, ModelForServiceDetails serviceDetails2) {
        if (serviceDetails1.getTargetConstructor() == null || serviceDetails2.getTargetConstructor() == null) {
            return 0;
        }

        return Integer.compare(
                serviceDetails1.getTargetConstructor().getParameterCount(),
                serviceDetails2.getTargetConstructor().getParameterCount()
        );
    }
}
