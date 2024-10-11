package com.shuo.provider;

import com.shuo.demo.common.service.UserService;
import com.shuo.krpc.bootstrap.ProviderBootstrap;
import com.shuo.krpc.model.ServiceRegistrationInfo;

import java.util.List;
import java.util.ArrayList;

public class ProviderBootstrapExample {
    public static void main(String[] args) {
        List<ServiceRegistrationInfo<?>> serviceRegistrationInfoList = new ArrayList<>();
        ServiceRegistrationInfo serviceRegistrationInfo =
                new ServiceRegistrationInfo(UserService.class.getName(), UserServiceImpl.class);

        serviceRegistrationInfoList.add(serviceRegistrationInfo);

        ProviderBootstrap.init(serviceRegistrationInfoList);
    }
}
