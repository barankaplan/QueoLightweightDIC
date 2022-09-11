package com.queo.services;

import com.queo.exceptions.BeanInstantiationException;
import com.queo.exceptions.ServiceInstantiationException;
import com.queo.models.ModelForServiceBeanDetails;
import com.queo.models.ModelForServiceDetails;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObjectInstantiationServiceImpl  {
    private static final String INVALID_PARAMETERS_COUNT_MSG = "Invalid parameters count for '%s'.";

    public void createInstance(ModelForServiceDetails serviceDetails, Object[] constructorParams, Object[] autowiredFieldInstances) throws ServiceInstantiationException {
        final Constructor targetConstructor = serviceDetails.getTargetConstructor();

        if (constructorParams.length != targetConstructor.getParameterCount()) {
            throw new ServiceInstantiationException(String.format(INVALID_PARAMETERS_COUNT_MSG, serviceDetails.getServiceType().getName()));
        }

        try {
            final Object instance = targetConstructor.newInstance(constructorParams);
            serviceDetails.setInstance(instance);
            this.setAutowiredFieldInstances(serviceDetails, autowiredFieldInstances);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ServiceInstantiationException(e.getMessage(), e);
        }
    }


    private void setAutowiredFieldInstances(ModelForServiceDetails serviceDetails, Object[] autowiredFieldInstances) throws IllegalAccessException {
        final Field[] autowireAnnotatedFields = serviceDetails.getAutowireAnnotatedFields();

        for (int i = 0; i < autowireAnnotatedFields.length; i++) {
            autowireAnnotatedFields[i].set(serviceDetails.getActualInstance(), autowiredFieldInstances[i]);
        }
    }





    public void createBeanInstance(ModelForServiceBeanDetails serviceBeanDetails) throws BeanInstantiationException {
        final Method originMethod = serviceBeanDetails.getOriginMethod();
        final Object rootInstance = serviceBeanDetails.getRootService().getActualInstance();

        try {
            final Object instance = originMethod.invoke(rootInstance);
            serviceBeanDetails.setInstance(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanInstantiationException(e.getMessage(), e);
        }
    }


}
