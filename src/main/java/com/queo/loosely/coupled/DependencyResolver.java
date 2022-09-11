package com.queo.loosely.coupled;

import com.queo.models.ModelForDependencies;

public interface DependencyResolver {

    boolean canResolve(ModelForDependencies dependencyParam);

    Object resolve(ModelForDependencies dependencyParam);
}
