package com.queo;

import com.queo.config.KaplansFacadeConfiguration;
import com.queo.models.ModelForDirectory;
import com.queo.models.ModelForServiceDetails;
import com.queo.services.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class KaplansDependencyInjector {


    public static LightweightDIC run(Class<?> startupClass) {
        KaplansFacadeConfiguration configuration = new KaplansFacadeConfiguration();
        return run(startupClass, configuration);
    }

    public static LightweightDIC run(Class<?> startupClass, KaplansFacadeConfiguration configuration) {

        final LightweightDIC dependencyContainer = run(new File[]{
                new File(new DirectoryResolverImpl().resolveDirectory(startupClass).getDirectory()),
        }, configuration);

        runStartUpMethod(startupClass, dependencyContainer);

        return dependencyContainer;
    }

    public static LightweightDIC run(File[] startupDirectories, KaplansFacadeConfiguration configuration) {
        final ServicesScanningServiceImpl scanningService = new ServicesScanningServiceImpl(configuration.scanning());
        final ObjectInstantiationServiceImpl objectInstantiationService = new ObjectInstantiationServiceImpl();
        final ServicesInstantiationServiceImpl instantiationService = new ServicesInstantiationServiceImpl(
                configuration.instantiations(),
                objectInstantiationService,
                new DependencyResolveService(configuration.instantiations())
        );

        final Set<Class<?>> locatedClasses = new HashSet<>();

        final Thread runner = new Thread(() -> locatedClasses.addAll(locateClasses(startupDirectories)));

        runner.setContextClassLoader(configuration.scanning().getClassLoader());
        runner.start();
        try {
            runner.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final Set<ModelForServiceDetails> mappedServices = new HashSet<>(scanningService.mapServices(locatedClasses));
        final List<ModelForServiceDetails> serviceDetails = new ArrayList<>(instantiationService.instantiateServicesAndBeans(mappedServices));

        final LightweightDIC dependencyContainer = new KaplansLightweightDIC();
        dependencyContainer.init(locatedClasses, serviceDetails, objectInstantiationService);

        return dependencyContainer;
    }

    private static Set<Class<?>> locateClasses(File[] startupDirectories) {
        final Set<Class<?>> locatedClasses = new HashSet<>();
        final DirectoryResolverImpl directoryResolver = new DirectoryResolverImpl();

        for (File startupDirectory : startupDirectories) {
            final ModelForDirectory directory = directoryResolver.resolveDirectory(startupDirectory);

            ClassLocatorForDirectory classLocator = new ClassLocatorForDirectory();


            locatedClasses.addAll(classLocator.locateClasses(directory.getDirectory()));
        }

        return locatedClasses;
    }


    private static void runStartUpMethod(Class<?> startupClass, LightweightDIC dependencyContainer) {
        final ModelForServiceDetails serviceDetails = dependencyContainer.getServiceDetails(startupClass, null);

        if (serviceDetails == null) {
            return;
        }

        for (Method declaredMethod : serviceDetails.getServiceType().getDeclaredMethods()) {
            if ((declaredMethod.getReturnType() != void.class &&
                    declaredMethod.getReturnType() != Void.class)) {
                continue;
            }

            declaredMethod.setAccessible(true);
            final Object[] params = Arrays.stream(declaredMethod.getParameterTypes())
                    .map(dependencyContainer::getService)
                    .toArray(Object[]::new);

            try {
                declaredMethod.invoke(serviceDetails.getActualInstance(), params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            return;
        }
    }
}
