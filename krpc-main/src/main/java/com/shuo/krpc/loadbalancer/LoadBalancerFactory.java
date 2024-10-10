package com.shuo.krpc.loadbalancer;

import com.shuo.krpc.spi.SpiLoader;

/**
 * Load Balancer Factory (Factory Pattern for obtaining load balancer instances)
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class LoadBalancerFactory {

    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * Default load balancer instance.
     */
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    /**
     * Get an instance of the load balancer.
     *
     * @param key The key used to identify the load balancer instance.
     * @return The load balancer instance corresponding to the key.
     */
    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}
