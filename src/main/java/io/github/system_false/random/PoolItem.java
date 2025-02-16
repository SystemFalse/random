/*
 * Copyright (C) 2025 SystemFalse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.github.system_false.random;

import io.github.system_false.random.builder.PoolItemBuilder;

import java.util.function.BooleanSupplier;

/**
 * A marker interface for pool items. A pool item is a generator which assigns a non-negative weight to each value it
 * generates. The weight is used to control the probability of each value being generated.
 *
 * @param <G> the type of the generated values
 */
public interface PoolItem<G> extends Generator<G> {
    /**
     * The weight of the generator. The weight is used to control the probability of this generator
     * being selected. The probability of this generator being selected is the weight divided by the
     * sum of the weights of all generators with the same type.
     *
     * @return the weight of the generator
     */
    long weight();

    /**
     * Test if some condition is met. If this method returns {@code false}, this generator is ignored.
     *
     * @return {@code true} if the generator is valid, {@code false} otherwise
     */
    boolean test();

    /**
     * Method is invoked when this item was picked from the pool. Implementations of this interface can
     * use this information as they choose to.
     */
    default void picked() {

    }

    /**
     * Method is invoked when this item was not picked from the pool. Implementations of this interface can
     * use this information as they choose to.
     */
    default void ignored() {

    }

    /**
     * Creates a {@link PoolItem} that generates random values of the given generator.
     * <p>
     * The returned {@link PoolItem} has a weight of 1 and a condition that always returns {@code true}.
     * </p>
     *
     * @param generator the generator to generate random values with
     * @param <T>       the generator type
     * @return a {@link PoolItem} that generates random values of the given generator
     */
    static <T> PoolItem<T> item(Generator<T> generator) {
        return PoolItem.<T>builder()
                .value(generator)
                .build();
    }

    /**
     * Creates a {@link PoolItem} that generates random values of the given generator.
     * <p>
     * The returned {@link PoolItem} has a weight of the given weight and a condition that always returns {@code true}.
     * </p>
     *
     * @param generator the generator to generate random values with
     * @param weight    the weight of this item in the pool, must be in range {@code [0; 0xffffffffL]}
     * @param <T>       the generator type
     * @return a {@link PoolItem} that generates random values of the given generator
     */
    static <T> PoolItem<T> item(Generator<T> generator, long weight) {
        return PoolItem.<T>builder()
                .value(generator)
                .weight(weight)
                .build();
    }

    /**
     * Creates a {@link PoolItem} that generates random values of the given generator.
     * <p>
     * The returned {@link PoolItem} has a weight of 1 and a condition of the given condition.
     * </p>
     *
     * @param generator the generator to generate random values with
     * @param condition the condition that must be satisfied in order for this item to be used
     * @param <T>       the generator type
     * @return a {@link PoolItem} that generates random values of the given generator
     */
    static <T> PoolItem<T> item(Generator<T> generator, BooleanSupplier condition) {
        return PoolItem.<T>builder()
                .value(generator)
                .condition(condition)
                .build();
    }

    /**
     * Creates a {@link PoolItem} that generates random values of the given generator.
     * <p>
     * The returned {@link PoolItem} has a weight of the given weight and a condition of the given condition.
     * </p>
     *
     * @param generator the generator to generate random values with
     * @param weight    the weight of this item in the pool, must be in range {@code [0; 0xffffffffL]}
     * @param condition the condition that must be satisfied in order for this item to be used
     * @param <T>       the generator type
     * @return a {@link PoolItem} that generates random values of the given generator
     */
    static <T> PoolItem<T> item(Generator<T> generator, long weight, BooleanSupplier condition) {
        return PoolItem.<T>builder()
                .value(generator)
                .weight(weight)
                .condition(condition)
                .build();
    }

    /**
     * Returns new pool item builder for configuring pool item.
     * @param <T> the generator type
     * @return new pool item builder
     */
    static <T> PoolItemBuilder<T> builder() {
        return new PoolItemBuilder<>();
    }
}
