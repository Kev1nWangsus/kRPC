package com.shuo.krpc.fault.retry;

import org.junit.Test;
import com.shuo.krpc.model.RpcResponse;

public class RetryStrategyTest {

    RetryStrategy retryStrategy = new FixedIntervalRetryStrategy();

    @Test
    public void doRetry() {
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("Testing retry");
                throw new RuntimeException("Simulated retry failure");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("Failed after multiple retries");
            e.printStackTrace();
        }
    }
}
