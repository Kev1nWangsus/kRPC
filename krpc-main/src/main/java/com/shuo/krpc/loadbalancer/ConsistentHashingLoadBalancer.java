package com.shuo.krpc.loadbalancer;

import com.shuo.krpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Consistent Hash Load Balancer
 * <p>
 * This class implements a load balancer using the consistent hashing algorithm. It distributes
 * requests to service instances in a manner that ensures minimal disruption when service
 * instances are added or removed. Virtual nodes are used to improve load distribution across
 * service instances.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ConsistentHashingLoadBalancer implements LoadBalancer {

    /**
     * Consistent hash ring to store virtual nodes.
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * Number of virtual nodes for each service instance.
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    /**
     * Selects a service instance using consistent hashing.
     * <p>
     * It builds a consistent hash ring with virtual nodes for each service instance and selects the
     * closest virtual node greater than or equal to the hash of the request.
     *
     * @param requestParams       The request parameters used to determine the selection.
     * @param serviceMetaInfoList The list of available service instances.
     * @return The selected ServiceMetaInfo instance, or null if the list is empty.
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams,
                                  List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // Build the virtual node ring
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        // Get the hash value of the request
        int hash = getHash(requestParams);

        // Select the closest virtual node greater than or equal to the request hash value
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            // If no virtual node is greater than or equal to the request hash, return the first node in the ring
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * Hash function to generate hash values.
     *
     * @param key The key to be hashed.
     * @return The hash value of the key.
     */
    private int getHash(Object key) {
        return key.hashCode();
    }
}