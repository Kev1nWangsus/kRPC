package com.shuo.krpc.registry;

import com.shuo.krpc.config.RegistryConfig;
import com.shuo.krpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Optional;

/**
 * Registry interface
 * <p>
 * Defines the basic contract for a registry service that can be used to
 * register, unregister, discover services, handle heartbeat checks, and manage
 * watch mechanisms for service nodes. This interface should be implemented by
 * classes that interact with a service registry system like etcd, ZooKeeper,
 * etc.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public interface Registry {

    /**
     * Initialize the registry with the provided configuration.
     *
     * @param registryConfig The configuration settings for the registry, such
     *                       as connection details.
     */
    void init(RegistryConfig registryConfig);

    /**
     * Register a service in the registry (used by service providers).
     *
     * @param serviceMetaInfo Metadata information about the service to be
     *                        registered, including service key, host, and port.
     * @throws Exception If the registration fails due to connection issues or
     *                   other errors.
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * Unregister a service from the registry (used by service providers).
     *
     * @param serviceMetaInfo Metadata information about the service to be
     *                        unregistered.
     */
    void unregister(ServiceMetaInfo serviceMetaInfo);

    /**
     * Discover services based on the provided service key (used by service
     * consumers).
     *
     * @param serviceKey The key representing the target service for which nodes
     *                   should be discovered.
     * @return An {@link Optional} containing a list of {@link ServiceMetaInfo}
     *         objects representing all nodes of the target service, or an empty
     *         {@link Optional} if no services are found.
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * Deactivates the current node by unregistering all services that it has
     * registered. This should be called when the service is shutting down or
     * the registry is no longer needed.
     */
    void destroy();

    /**
     * Perform heartbeat detection to ensure the health of registered services
     * and renew leases if necessary.
     */
    void sendHeartBeat();

    /**
     * Watch a specific service node for changes (used by service consumers).
     *
     * @param serviceNodeKey The key representing the service node to watch for
     *                       changes, such as updates or deletions.
     */
    void watch(String serviceNodeKey);
}