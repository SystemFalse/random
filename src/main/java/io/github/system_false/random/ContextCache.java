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

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Utility class that manages context cache. Default realization of {@link Contextual} interface
 * uses this class to handle context.
 *
 * @see Contextual
 */
class ContextCache {
    private static final ConcurrentHashMap<Contextual<?>, Object> cache = new ConcurrentHashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private ContextCache() {}

    /**
     * Initializes context for the given contextual object.
     * @param contextual object to set context
     * @param context    context
     * @see Contextual#withContext(Object, Function)
     */
    static void setContext(Contextual<?> contextual, Object context) {
        cache.put(contextual, context);
    }

    /**
     * Returns context for the given contextual object as an {@link Optional}.
     * @param contextual object to retrieve context
     * @return context
     * @see Contextual#context()
     */
    static Optional<?> getContext(Contextual<?> contextual) {
        return Optional.ofNullable(cache.getOrDefault(contextual, null));
    }

    /**
     * Removes context of the given contextual object from the cache.
     * @param contextual object to remove context
     * @see Contextual#withContext(Object, Function)
     */
    static void resetContext(Contextual<?> contextual) {
        cache.remove(contextual);
    }
}
