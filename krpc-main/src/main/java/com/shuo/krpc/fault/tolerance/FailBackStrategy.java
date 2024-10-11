package com.shuo.krpc.fault.tolerance;

import com.shuo.krpc.model.RpcResponse;

import java.util.Map;

/**
 * Fail Back Strategy
 * <p>
 * This strategy attempts to recover from the failure by retrying the operation later or
 * delegating the task to a backup.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class FailBackStrategy implements ToleranceStrategy {

    @Override
    public RpcResponse doTolerance(Map<String, Object> context, Exception e) {
        System.err.println("Failing back due to exception: " + e.getMessage() + ". Scheduling " +
                "retry or fallback.");
        // todo: Simulate scheduling a retry or notifying a backup (implementation-specific)
        return null;
    }
}