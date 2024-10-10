package com.shuo.krpc.loadbalancer;

import com.shuo.krpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * Load Balancer
 * <p>
 * This interface defines the contract for a load balancer, which is responsible for selecting an
 * appropriate service instance from a list of available services based on certain criteria or
 * request parameters. The load balancer is typically used on the client side to distribute
 * requests efficiently across multiple service instances.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public interface LoadBalancer {
    /**
     * Selects a service instance to invoke.
     * <p>
     * This method takes a set of request parameters and a list of available service instances and returns the most
     * suitable service instance for the request.
     *
     * @param requestParams       The request parameters used to determine the selection.
     * @param serviceMetaInfoList The list of available service instances.
     * @return The selected ServiceMetaInfo instance.
     */
    ServiceMetaInfo select(Map<String, Object> requestParams,
                           List<ServiceMetaInfo> serviceMetaInfoList);
}
