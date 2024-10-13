package com.shuo.krpc.springboot.starter.annotation;

import com.shuo.krpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.shuo.krpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.shuo.krpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable RPC Annotation
 * <p>
 * This annotation is used to enable the RPC framework. It can be applied to classes to indicate
 * that they require RPC capabilities.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * Indicates whether the server needs to be started.
     *
     * @return true if the server should be started, false otherwise.
     */
    boolean needServer() default true;
}