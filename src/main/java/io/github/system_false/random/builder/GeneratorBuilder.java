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

import io.github.system_false.random.Generator;

import java.lang.reflect.Field;

/**
 * Base class for all builders of {@link Generator} instances.
 * @param <T> the type of the generated value
 */
public abstract class GeneratorBuilder<T> {
    private Generator<T> instance;

    protected GeneratorBuilder() {}

    protected void checkInstance() {
        if (instance != null) {
            throw new IllegalStateException("Instance already built");
        }
    }

    protected abstract Generator<T> build0();

    /**
     * Method returns new {@link Generator} instance if it was not built yet. If
     * this method is called multiple times, the same instance will be returned.
     * @return new {@link Generator} instance
     */
    public Generator<T> build() {
        if (instance != null) {
            return instance;
        }
        return instance = build0();
    }

    protected static Field checkField(Class<?> clazz, String name) {
        Field field;
        try {
            field = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
        return field;
    }

    protected static void checkCapability(Field field, Object value) {
        Class<?> fieldClass = field.getDeclaringClass();
        if (value != null) {
            if (!fieldClass.isInstance(value)) {
                throw new IllegalArgumentException("value must be an instance of " + fieldClass.getCanonicalName());
            }
        } else {
            if (fieldClass.isPrimitive()) {
                throw new IllegalArgumentException("value must not be null for primitive field " + field.getName());
            }
        }
    }
}
