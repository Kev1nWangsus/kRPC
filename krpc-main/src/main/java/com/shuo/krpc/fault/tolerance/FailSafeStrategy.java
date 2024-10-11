package com.shuo.krpc.fault.tolerance;

import com.shuo.krpc.model.RpcResponse;

import java.util.Map;

/**
 * Fail Safe Strategy
 * <p>
 * This strategy ignores the failure and returns a default success response, allowing the process
 * to continue.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class FailSafeStrategy implements ToleranceStrategy {

    @Override
    public RpcResponse doTolerance(Map<String, Object> context, Exception e) {
        // Log the error but continue by returning a default success response
        System.err.println("Ignoring exception and continuing: " + e.getMessage());
        return new RpcResponse();
    }
}