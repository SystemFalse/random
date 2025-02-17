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

class ContextCache {
    private static final ConcurrentHashMap<Contextual<?>, Object> cache = new ConcurrentHashMap<>();

    private ContextCache() {}

    static void setContext(Contextual<?> contextual, Object context) {
        cache.put(contextual, context);
    }

    static Optional<?> getContext(Contextual<?> contextual) {
        return Optional.ofNullable(cache.getOrDefault(contextual, null));
    }

    static void resetContext(Contextual<?> contextual) {
        cache.remove(contextual);
    }
}
