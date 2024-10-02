package com.shuo.krpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC Request
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {

    /**
     * request RPC service name
     */
    private String serviceName;

    /**
     * request method name
     */
    private String methodName;

    /**
     * request parameter types
     */
    private Class<?>[] parameterTypes;

    /**
     * request arguments
     */
    private Object[] args;
}
