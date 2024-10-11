package com.shuo.krpc.fault.tolerance;

import com.shuo.krpc.model.RpcResponse;

import java.util.Map;

/**
 * Fail Over Strategy
 * <p>
 * This strategy attempts to recover by trying an alternative solution or another available
 * service instance.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class FailOverStrategy implements ToleranceStrategy {

    @Override
    public RpcResponse doTolerance(Map<String, Object> context, Exception e) {
        // Log the error and attempt to switch to an alternative solution
        System.err.println("Failing over due to exception: " + e.getMessage() + ". Trying " +
                "alternative instance.");
        // todo: Simulate switching to an alternative (implementation-specific)
        return null;
    }
}