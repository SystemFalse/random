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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Interface that provides way to use object with specified context. Default realization
 * uses external cache to store contexts of all objects. It's thread-safe.
 * @param <T> the type of implementation
 */
public interface Contextual<T extends Contextual<T>> {
    /**
     * Method returns an optional context value for this object.
     * @return context
     */
    default Optional<?> context() {
        return ContextCache.getContext(this);
    }

    /**
     *Method returns and casts to the given class an optional context value for this object.
     * @param castTo class to cast context
     * @param <R>    the type of the context
     * @return context of given type
     */
    default <R> Optional<R> context(Class<R> castTo) {
        Objects.requireNonNull(castTo);
        return context().map(castTo::cast);
    }

    /**
     * Initializes this generator with the given context and returns the result of the given function.
     * Scope of the given context is limited to the execution of the given function.
     *
     * @param context  context to set
     * @param function function to execute
     * @param <R>      the type of the result
     * @return result of the given function
     * @throws NullPointerException if {@code context} or {@code function} is {@code null}
     */
    @SuppressWarnings("unchecked")
    default <R> R withContext(Object context, Function<T, R> function) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(function, "function");
        R result;
        synchronized (this) {
            ContextCache.setContext(this, context);
            try {
                result = function.apply((T) this);
            } finally {
                ContextCache.resetContext(this);
            }
        }
        return result;
    }
}
