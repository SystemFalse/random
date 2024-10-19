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

import java.util.List;

/**
 * Common interface for all pool generators. Implementations of this interface generates
 * random values from specified pool of values.
 *
 * @param <T> the type of the generated values
 */
public interface PoolGenerator<T> extends Generator<T> {
    /**
     * Returns the pool of values that this generator generates from.
     *
     * @return the pool of values
     */
    List<T> getPool();
}
