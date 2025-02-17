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

package io.github.system_false.random.builder;

import io.github.system_false.random.Contextual;
import io.github.system_false.random.Generator;

import java.util.Optional;
import java.util.function.Function;

/**
 * Class that implements {@link Contextual} interface and delegates all methods from {@link Generator}.
 * @param <T> the type of implementation
 */
class ContextualImpl<T> implements Contextual<Generator<T>> {
    private Generator<T> base;

    /**
     * Sets generator that will be used as base.
     * @param base generator
     */
    public void setBase(Generator<T> base) {
        this.base = base;
    }

    @Override
    public Optional<?> context() {
        return base != null ? base.context() : Optional.empty();
    }

    @Override
    public <R> R withContext(Object context, Function<Generator<T>, R> function) {
        if (base == null) {
            throw new IllegalStateException("Item was not built yet");
        }
        return base.withContext(context, function);
    }
}
