package com.shuo.krpc.fault.tolerance;

import com.shuo.krpc.spi.SpiLoader;

/**
 * Tolerance Strategy Factory (Used for obtaining tolerance strategy instances)
 * <p>
 * This class provides a factory for creating and retrieving instances of tolerance strategies.
 * It uses the factory pattern to encapsulate the creation logic and allows clients to obtain
 * different implementations of tolerance strategies.
 *
 * @author <a href="https://github.com/Kev1nWangsus">shuo</a>
 */
public class ToleranceStrategyFactory {

    static {
        SpiLoader.load(ToleranceStrategy.class);
    }

    /**
     * Default tolerance strategy
     */
    private static final ToleranceStrategy DEFAULT_TOLERANCE_STRATEGY = new FailFastStrategy();

    /**
     * Get an instance of a tolerance strategy
     *
     * @param key The key used to identify the tolerance strategy instance.
     * @return The tolerance strategy instance corresponding to the key.
     */
    public static ToleranceStrategy getInstance(String key) {
        return SpiLoader.getInstance(ToleranceStrategy.class, key);
    }

}

