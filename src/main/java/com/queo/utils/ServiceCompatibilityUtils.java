package com.queo.utils;

import com.queo.models.ModelForServiceDetails;

public final class ServiceCompatibilityUtils {

    private ServiceCompatibilityUtils() {
    }

    public static boolean isServiceCompatible(ModelForServiceDetails serviceDetails, Class<?> requiredType, String instanceName) {
        final boolean isRequiredTypeAssignable = requiredType.isAssignableFrom(serviceDetails.getServiceType());
        final boolean isRequiredTypeAssignable2 = serviceDetails.getInstance() != null &&
                requiredType.isAssignableFrom(serviceDetails.getInstance().getClass());

        final boolean instanceNameMatches = instanceName == null ||
                instanceName.equalsIgnoreCase(serviceDetails.getInstanceName());

        return (isRequiredTypeAssignable || isRequiredTypeAssignable2) && instanceNameMatches;
    }
}
