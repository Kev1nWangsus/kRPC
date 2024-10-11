package com.shuo.krpc.fault.tolerance;

import com.shuo.krpc.model.RpcResponse;

import java.util.Map;

/**
 * Tolerance Strategy Interface
 * <p>
 * This interface defines the contract for implementing tolerant strategies. It is used to handle exceptions
 * and provide fault tolerance during the execution of a process.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public interface ToleranceStrategy {

    /**
     * Fault Tolerance
     * <p>
     * This method is responsible for handling exceptions and implementing fault tolerance logic based on the
     * provided context and exception.
     *
     * @param context The context used to pass data during the process.
     * @param e       The exception that occurred.
     * @return The response after performing fault tolerance.
     */
    RpcResponse doTolerance(Map<String, Object> context, Exception e);
}
