package com.shuo.krpc.loadbalancer;

import com.shuo.krpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Random Load Balancer
 * <p>
 * This class implements a random load balancing strategy, which selects a service instance at
 * random from a list of available services. It ensures that the load is distributed across all
 * instances without following a specific order.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class RandomLoadBalancer implements LoadBalancer {

    /**
     * Random instance used for selecting service instances.
     */
    private final Random random = new Random();

    /**
     * Selects a service instance using a random algorithm.
     *
     * @param requestParams       The request parameters used to determine the selection (not
     *                            used in this implementation).
     * @param serviceMetaInfoList The list of available service instances.
     * @return The selected ServiceMetaInfo instance, or null if the list is empty.
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        int size = serviceMetaInfoList.size();
        if (size == 0) {
            return null;
        }
        // If only one service is available, return it directly.
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        // Select a service instance at random.
        return serviceMetaInfoList.get(random.nextInt(size));
    }
}
