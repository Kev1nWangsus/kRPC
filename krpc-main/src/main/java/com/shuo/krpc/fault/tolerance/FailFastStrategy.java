package com.shuo.krpc.fault.tolerance;

import com.shuo.krpc.model.RpcResponse;

import java.util.Map;

/**
 * Fail Fast Strategy
 * <p>
 * This strategy immediately fails and returns an error response as soon as an exception occurs.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class FailFastStrategy implements ToleranceStrategy {

    @Override
    public RpcResponse doTolerance(Map<String, Object> context, Exception e) {
        // Log the error and immediately
        throw new RuntimeException("Service fault", e);
    }
}

