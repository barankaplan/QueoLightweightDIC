package com.queo.utils;

import com.queo.models.ModelForDependencies;
import com.queo.models.ModelForServiceDetails;

import java.util.Collection;

public final class DependencyParamUtils {

    private DependencyParamUtils() {
    }

    public static boolean dependencyParamsResolved(Collection<ModelForDependencies> dependencyParams) {
        for (ModelForDependencies dependencyParam : dependencyParams) {
            if (dependencyParam.getInstance() == null && dependencyParam.isValuePresent()) {
                return false;
            }
        }

        return true;
    }

    public static boolean isDependencyRequired(Collection<ModelForDependencies> dependencyParams, ModelForServiceDetails serviceDetails) {
        for (ModelForDependencies dependencyParam : dependencyParams) {
           if (isDependencyRequired(dependencyParam, serviceDetails)) {
               return true;
           }
        }

        return false;
    }

    public static boolean isDependencyRequired(ModelForDependencies dependencyParam, ModelForServiceDetails serviceDetails) {
        if (dependencyParam.getInstance() != null) {
            return false;
        }

        return ServiceCompatibilityUtils.isServiceCompatible(
                serviceDetails,
                dependencyParam.getDependencyType(),
                dependencyParam.getInstanceName());
    }
}
