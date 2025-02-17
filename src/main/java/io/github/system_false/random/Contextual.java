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
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Interface that provides way to use object with specified context. Default realization
 * uses external cache to store contexts of all objects. It's thread-safe.
 */
public interface Contextual {
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
     * Initializes this contextual object with the given context and returns the result of the given function.
     * Scope of the given context is limited to the execution of the given function.
     *
     * @param context  context to set
     * @param function function to execute
     * @param <C>      type of contextual object
     * @param <R>      the type of the result
     * @return result of the given function
     * @throws NullPointerException if {@code context} or {@code function} is {@code null}
     */
    @SuppressWarnings("unchecked")
    default <C extends Contextual, R> R withContext(Object context, Function<C, R> function) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(function, "function");
        R result;
        synchronized (this) {
            ContextCache.setContext(this, context);
            try {
                result = function.apply((C) this);
            } finally {
                ContextCache.resetContext(this);
            }
        }
        return result;
    }

    /**
     * Initializes this contextual object with the given context and executes the given consumer.
     * Scope of the given context is limited to the execution of the given consumer.
     *
     * @param context  context to set
     * @param consumer consumer to execute
     * @param <C>      type of contextual object
     * @throws NullPointerException if {@code context} or {@code consumer} is {@code null}
     */
    @SuppressWarnings("unchecked")
    default <C extends Contextual> void withContext(Object context, Consumer<C> consumer) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(consumer, "consumer");
        synchronized (this) {
            ContextCache.setContext(this, context);
            try {
                consumer.accept((C) this);
            } finally {
                ContextCache.resetContext(this);
            }
        }
    }

    /**
     * Method returns new contextual object that encapsulates given object.
     * @param object object to encapsulate
     * @param <E>    type of encapsulated object
     * @return new contextual object
     */
    static <E extends Contextual> Contextual encapsulate(E object) {
        Objects.requireNonNull(object, "object");
        return new Contextual() {
            @Override
            public Optional<?> context() {
                return object.context();
            }

            @Override
            public <R> Optional<R> context(Class<R> castTo) {
                return object.context(castTo);
            }

            @Override
            public <C extends Contextual, R> R withContext(Object context, Function<C, R> function) {
                return object.withContext(context, function);
            }
        };
    }
}
