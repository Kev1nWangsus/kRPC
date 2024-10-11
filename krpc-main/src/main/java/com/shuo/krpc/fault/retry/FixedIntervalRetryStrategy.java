package com.shuo.krpc.fault.retry;

import com.github.rholder.retry.*;
import com.shuo.krpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Fixed Interval Retry Strategy
 * <p>
 * This class implements a retry strategy with a fixed interval between retries. It retries a failed
 * operation at a constant time interval, and stops after a specified number of attempts.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {

    /**
     * Perform retry with a fixed interval.
     *
     * This method attempts to execute the given callable operation, retrying if it fails, with a fixed
     * wait time of 3 seconds between each retry. It stops retrying after 3 attempts.
     *
     * @param callable The operation to be retried.
     * @return The response of the retried operation.
     * @throws ExecutionException If the operation fails after all retries.
     * @throws RetryException If the retry process encounters an issue.
     */
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("Retry attempt number: {}", attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }
}