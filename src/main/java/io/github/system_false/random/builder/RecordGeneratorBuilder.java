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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

/**
 * Class for creating object generators. This class provided builder-like methods
 * to configure generator. This builder supports only record classes.
 * @param <T> object type
 */
public class RecordGeneratorBuilder<T> extends GeneratorBuilder<T> {
    private final Class<T> recordClass;
    private final Map<String, Generator<?>> fieldGenerators;

    /**
     * Public constructor that creates new builder for specified record class.
     * @param recordClass record class
     */
    public RecordGeneratorBuilder(Class<T> recordClass) {
        if (!Objects.requireNonNull(recordClass, "class").isRecord()) {
            throw new IllegalArgumentException("class must be a record");
        }
        this.recordClass = recordClass;
        fieldGenerators = new HashMap<>(recordClass.getDeclaredFields().length);
    }

    /**
     * Specifies field value. This field will have only this value in generated objects.
     * @param name field name
     * @param value field value
     *
     * @return this builder
     */
    public RecordGeneratorBuilder<T> field(String name, Object value) {
        checkInstance();
        Field field = checkField(recordClass, name);
        checkCapability(field, value);
        fieldGenerators.put(name, rg -> value);
        return this;
    }

    /**
     * Specifies field supplier. This field will have value generated by supplier in generated objects.
     * @param name field name
     * @param value field supplier
     *
     * @return this builder
     * @throws NullPointerException if value is {@code null}
     */
    public RecordGeneratorBuilder<T> field(String name, Supplier<?> value) {
        checkInstance();
        Objects.requireNonNull(value, "value");
        checkField(recordClass, name);
        fieldGenerators.put(name, rg -> value.get());
        return this;
    }

    /**
     * Specifies field generator. This field will have value generated by generator in generated objects.
     * @param name field name
     * @param value field generator
     *
     * @return this builder
     * @throws NullPointerException if value is {@code null}
     */
    public RecordGeneratorBuilder<T> field(String name, Generator<?> value) {
        checkInstance();
        Objects.requireNonNull(value, "value");
        checkField(recordClass, name);
        fieldGenerators.put(name, value);
        return this;
    }

    @Override
    protected Generator<T> build0() {
        RecordComponent[] components = recordClass.getRecordComponents();
        if (components.length != fieldGenerators.size()) {
            throw new IllegalStateException("not all record components have a generator");
        }
        Constructor<T> constructor;
        try {
            constructor = recordClass.getDeclaredConstructor(Stream.of(components)
                    .map(RecordComponent::getType)
                    .toArray(Class[]::new));
        } catch (NoSuchMethodException e) {
            //should never happen
            throw new RuntimeException(e);
        }
        return new RecordGenerator<>(constructor, Stream.of(components)
                .map(rc -> fieldGenerators.get(rc.getName()))
                .toList());
    }
}

class RecordGenerator<T> implements Generator<T> {
    private final Constructor<T> constructor;
    private final List<? extends Generator<?>> fieldGenerators;

    RecordGenerator(Constructor<T> constructor, List<? extends Generator<?>> fieldGenerators) {
        this.constructor = constructor;
        constructor.setAccessible(true);
        this.fieldGenerators = fieldGenerators;
    }

    @Override
    public T generate(RandomGenerator random) {
        Object[] args = new Object[fieldGenerators.size()];
        for (int i = 0; i < args.length; i++) {
            args[i] = fieldGenerators.get(i).generate(random);
        }
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
