/*
 * Copyright (C) 2024 SystemFalse.
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

package org.system_false.random.generator;

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
     * @apiNote weight should not be negative and more that {@code 0xffffffffL}
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
}
