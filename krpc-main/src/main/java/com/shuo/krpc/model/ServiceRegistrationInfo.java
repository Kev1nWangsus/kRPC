package com.shuo.krpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Service Registration Information Class
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 * @param <T> The type of the service being registered.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRegistrationInfo<T> {

    /**
     * The name of the service.
     */
    private String serviceName;

    /**
     * The implementation class of the service.
     */
    private Class<? extends T> implClass;
}

