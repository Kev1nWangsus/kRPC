package com.shuo.krpc.fault.retry;

import com.shuo.krpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * Retry Strategy Interface
 * <p>
 * This interface defines a contract for implementing retry strategies. It allows for retrying an
 * operation that may fail, such as a remote service call, with customizable retry logic.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public interface RetryStrategy {

    /**
     * Retry
     * <p>
     * This method attempts to execute the given callable operation, retrying if necessary based on the
     * implemented strategy.
     *
     * @param callable The operation to be retried.
     * @return The response of the retried operation.
     * @throws Exception If the operation fails after all retries.
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}