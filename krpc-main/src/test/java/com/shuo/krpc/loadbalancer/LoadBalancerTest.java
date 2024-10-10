package com.shuo.krpc.loadbalancer;

import com.shuo.krpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

@RunWith(Parameterized.class)
public class LoadBalancerTest {

    @Parameterized.Parameters
    public static Collection<Object[]> loadBalancers() {
        return Arrays.asList(new Object[][]{
                {new ConsistentHashingLoadBalancer(), "apple"},
                {new RandomLoadBalancer(), "banana"},
                {new RoundRobinLoadBalancer(), "cherry"}
        });
    }

    private final LoadBalancer loadBalancer;
    private final String methodName;

    public LoadBalancerTest(LoadBalancer loadBalancer, String methodName) {
        this.loadBalancer = loadBalancer;
        this.methodName = methodName;
    }

    @Test
    public void testLoadBalancer() {
        // Request parameters
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", methodName);
        // Service list
        List<ServiceMetaInfo> serviceMetaInfoList = Arrays.asList(
                createServiceMetaInfo("myService", "1.0", "localhost", 1234),
                createServiceMetaInfo("myService", "1.0", "localhost", 5678)
        );

        // Call select method 3 times
        for (int i = 0; i < 3; i++) {
            ServiceMetaInfo serviceMetaInfo = loadBalancer.select(requestParams,
                    serviceMetaInfoList);
            System.out.println(serviceMetaInfo);
            Assert.assertNotNull(serviceMetaInfo);
        }
    }

    /**
     * Helper method to create a ServiceMetaInfo instance.
     *
     * @param serviceName   The name of the service.
     * @param serviceVersion The version of the service.
     * @param serviceHost   The host of the service.
     * @param servicePort   The port of the service.
     * @return A new ServiceMetaInfo instance with the specified parameters.
     */
    private ServiceMetaInfo createServiceMetaInfo(String serviceName, String serviceVersion,
                                                  String serviceHost, int servicePort) {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(serviceVersion);
        serviceMetaInfo.setServiceHost(serviceHost);
        serviceMetaInfo.setServicePort(servicePort);
        return serviceMetaInfo;
    }
}