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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Utility class that manages context cache. Default realization of {@link Contextual} interface
 * uses this class to handle context.
 *
 * @see Contextual
 */
class ContextCache {
    private record CacheEntry(Object commonContext, Map<Contextual, Object> map) {}

    private static final Map<Thread, CacheEntry> cache = new HashMap<>();

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
    static void setContext(Contextual contextual, Object context) {
        Thread thread = Thread.currentThread();
        synchronized (cache) {
            CacheEntry entry = cache.getOrDefault(thread, null);
            if (entry == null) {
                cache.put(thread, new CacheEntry(context, new HashMap<>(Map.of(contextual, context))));
            } else {
                if (!Objects.equals(entry.commonContext(), context)) {
                    entry.map().put(contextual, context);
                }
            }
        }
    }

    /**
     * Returns context for the given contextual object as an {@link Optional}.
     * @param contextual object to retrieve context
     * @return context
     * @see Contextual#context()
     */
    static synchronized Optional<?> getContext(Contextual contextual) {
        Thread thread = Thread.currentThread();
        synchronized (cache) {
            return Optional.ofNullable(cache.getOrDefault(thread, null))
                    .map(entry -> entry.map().getOrDefault(contextual, entry.commonContext()));
        }
    }

    /**
     * Removes context of the given contextual object from the cache.
     * @param contextual object to remove context
     * @see Contextual#withContext(Object, Function)
     */
    static synchronized void resetContext(Contextual contextual) {
        Thread thread = Thread.currentThread();
        synchronized (cache) {
            CacheEntry entry = cache.getOrDefault(thread, null);
            if (entry != null) {
                entry.map().remove(contextual);
                if (entry.map().isEmpty()) {
                    cache.remove(thread);
                }
            }
        }
    }
}
