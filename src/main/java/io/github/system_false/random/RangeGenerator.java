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

package io.github.system_false.random;

import java.util.function.Predicate;

/**
 * Common interface for all range generators. Implementations of this interface generates
 * random values within a specified range.
 *
 * @param <T> the type of the range
 */
public interface RangeGenerator<T> extends Predicate<T>, Generator<T> {
    /**
     * Method returns the minimum value of the range.
     * @return the minimum value of the range.
     */
    T minValue();

    /**
     * Method returns the maximum value of the range.
     * @return the maximum value of the range.
     */
    T maxValue();

    /**
     * Checks if the given value is within the range.
     * @param value the value to check.
     * @return {@code true} if the given value is within the range, {@code false} otherwise.
     */
    @Override
    boolean test(T value);
}
