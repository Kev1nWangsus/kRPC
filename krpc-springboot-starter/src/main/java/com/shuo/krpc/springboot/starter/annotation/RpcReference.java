package com.shuo.krpc.springboot.starter.annotation;


import com.shuo.krpc.constant.RpcConstant;
import com.shuo.krpc.fault.retry.RetryStrategyKeys;
import com.shuo.krpc.fault.tolerance.ToleranceStrategyKeys;
import com.shuo.krpc.loadbalancer.LoadBalancerKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC Reference Annotation (for service injection)
 * <p>
 * This annotation is used to mark a field that requires an RPC service to be injected. It allows
 * specifying the service interface, version, load balancer, retry strategy, and fault tolerance
 * strategy.
 *
 * <p>
 * Author: <a href="https://github.com/liyupi">Programmer Yu Pi</a><br>
 * Learn: <a href="https://codefather.cn">Yu Pi's Programming Guide</a><br>
 * From: <a href="https://yupi.icu">Programming Navigation Learning Circle</a>
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcReference {

    /**
     * Service interface
     *
     * @return The interface class that this reference points to.
     */
    Class<?> interfaceClass() default void.class;

    /**
     * Service version number
     *
     * @return The version of the service, defaulting to the constant defined in RpcConstant.
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * Load balancer
     *
     * @return The load balancer key, defaulting to ROUND_ROBIN.
     */
    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;

    /**
     * Retry strategy
     *
     * @return The retry strategy key, defaulting to NO retry.
     */
    String retryStrategy() default RetryStrategyKeys.NO;

    /**
     * Tolerance strategy
     *
     * @return The fault tolerance strategy key, defaulting to FAIL_FAST.
     */
    String toleranceStrategy() default ToleranceStrategyKeys.FAIL_FAST;

    /**
     * Developer mock mode
     *
     * @return true if mock invocation should be used, false otherwise.
     */
    boolean mock() default false;
}