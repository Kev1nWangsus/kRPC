package com.shuo.krpc.fault.retry;

import com.shuo.krpc.spi.SpiLoader;

/**
 * Retry Strategy Factory (Used for obtaining retry strategy instances)
 * <p>
 * This class provides a factory for retrieving instances of retry strategies. It uses the factory pattern
 * to create and manage retry strategy objects based on a specified key. A default retry strategy is also
 * available for general use.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * Default retry strategy instance.
     */
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    /**
     * Get an instance of the retry strategy.
     *
     * @param key The key used to identify the retry strategy instance.
     * @return The retry strategy instance corresponding to the key.
     */
    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }

}
