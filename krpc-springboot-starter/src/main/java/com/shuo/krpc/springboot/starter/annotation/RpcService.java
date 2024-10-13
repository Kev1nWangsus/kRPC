package com.shuo.krpc.springboot.starter.annotation;

import com.shuo.krpc.constant.RpcConstant;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Service Provider Annotation (Used for registering services)
 * <p>
 * This annotation is used to mark a class as an RPC service provider, which allows it to be
 * registered and made available for remote procedure calls.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    /**
     * The service interface class
     *
     * @return The interface class that this service implements.
     */
    Class<?> interfaceClass() default void.class;

    /**
     * The version of the service
     *
     * @return The version of the service, defaulting to the constant defined in RpcConstant.
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;
}