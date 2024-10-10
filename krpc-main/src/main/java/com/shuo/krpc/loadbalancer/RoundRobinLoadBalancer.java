package com.shuo.krpc.loadbalancer;

import com.shuo.krpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round Robin Load Balancer
 * <p>
 * This class implements a round-robin load balancing strategy, which distributes requests evenly
 * across multiple service instances by cycling through them in order. The load balancer keeps
 * track of the current index of the service instance and increments it for each new request,
 * ensuring that the load is distributed fairly among available services.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    /**
     * The current index for round-robin selection.
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    /**
     * Selects a service instance using the round-robin algorithm.
     *
     * @param requestParams       The request parameters used to determine the selection (not
     *                            used in this implementation).
     * @param serviceMetaInfoList The list of available service instances.
     * @return The selected ServiceMetaInfo instance, or null if the list is empty.
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams,
                                  List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        // If only one service is available, return it directly.
        int size = serviceMetaInfoList.size();
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }

        // Get the next index using round-robin and return the corresponding service instance.
        int index = currentIndex.getAndIncrement() % size;
        return serviceMetaInfoList.get(index);
    }
}
