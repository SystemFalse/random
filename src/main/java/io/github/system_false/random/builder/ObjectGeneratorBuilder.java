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

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

/**
 * Class for creating object generators. This class provided builder-like methods
 * to configure the generator. This builder supports only normal classes.
 * @param <T> object type
 */
public class ObjectGeneratorBuilder<T> extends GeneratorBuilder<T> {
    private final Class<T> objectClass;
    private Generator<T> constructor;
    private final Map<String, Generator<?>> fieldGenerators;

    /**
     * Public constructor that creates new builder for specified object class
     * @param objectClass object class
     */
    public ObjectGeneratorBuilder(Class<T> objectClass) {
        this.objectClass = checkClass(objectClass);
        fieldGenerators = new HashMap<>(objectClass.getDeclaredFields().length);
    }

    /**
     * Specifies object constructor. Constructor should return object with evaluated final
     * fields only.
     * @param constructor {@link Supplier} that returns generating object
     *
     * @return this builder
     */
    public ObjectGeneratorBuilder<T> constructor(Supplier<T> constructor) {
        checkInstance();
        Objects.requireNonNull(constructor, "constructor");
        this.constructor = rg -> constructor.get();
        return this;
    }

    /**
     * Specifies object constructor. Constructor should return object with evaluated final
     * fields only.
     * @param constructor {@link Function} that returns generating object
     *
     * @return this builder
     */
    public ObjectGeneratorBuilder<T> constructor(Function<RandomGenerator, T> constructor) {
        checkInstance();
        Objects.requireNonNull(constructor, "constructor");
        this.constructor = constructor::apply;
        return this;
    }

    /**
     * Specifies object constructor. Constructor should return object with evaluated final
     * fields only.
     * @param constructor {@link MethodHandle} that returns generating object
     * @param parameters parameters
     *
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    public ObjectGeneratorBuilder<T> constructor(MethodHandle constructor, Generator<?>... parameters) {
        checkInstance();
        Objects.requireNonNull(constructor, "constructor");
        if (!objectClass.isAssignableFrom(constructor.type().returnType())) {
            throw new IllegalArgumentException("Constructor must return " + objectClass);
        }
        Objects.requireNonNull(parameters, "parameters");
        for (int i = 0; i < parameters.length; i++) {
            Objects.requireNonNull(parameters[i], "parameters[" + i + "]");
        }
        if (constructor.type().parameterCount() != parameters.length) {
            throw new IllegalArgumentException("Invalid count of parameters");
        }
        this.constructor = new Generator<>() {
            @Override
            public T generate(RandomGenerator rg) {
                Object[] args = new Object[parameters.length];
                for (int i = 0; i < args.length; i++) {
                    args[i] = parameters[i].generate(rg, this);
                }
                try {
                    return (T) constructor.invoke(args);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return this;
    }

    /**
     * Specifies field value. This field will have only this value in generated objects.
     * @param name field name
     * @param value field value
     *
     * @return this builder
     * @throws IllegalArgumentException if field is final
     */
    public ObjectGeneratorBuilder<T> field(String name, Object value) {
        checkInstance();
        Field field = checkField(objectClass, name);
        if (Modifier.isFinal(field.getModifiers())) {
            throw new IllegalArgumentException("Field " + field.getName() + " is final");
        }
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
     * @throws IllegalArgumentException if field is final
     */
    public ObjectGeneratorBuilder<T> field(String name, Supplier<?> value) {
        checkInstance();
        Objects.requireNonNull(value, "value");
        Field field = checkField(objectClass, name);
        if (Modifier.isFinal(field.getModifiers())) {
            throw new IllegalArgumentException("Field " + field.getName() + " is final");
        }
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
     * @throws IllegalArgumentException if field is final
     */
    public ObjectGeneratorBuilder<T> field(String name, Generator<?> value) {
        checkInstance();
        Objects.requireNonNull(value, "value");
        Field field = checkField(objectClass, name);
        if (Modifier.isFinal(field.getModifiers())) {
            throw new IllegalArgumentException("Field " + field.getName() + " is final");
        }
        fieldGenerators.put(name, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Generator<T> build0() {
        if (constructor == null) {
            Constructor<?> cons;
            try {
                cons = objectClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("no constructor specified");
            }
            constructor = rg -> {
                try {
                    cons.setAccessible(true);
                    return (T) cons.newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        }
        return new ObjectGenerator<>(objectClass, constructor, fieldGenerators);
    }

    private static <T> Class<T> checkClass(Class<T> clazz) {
        Objects.requireNonNull(clazz, "class");
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("Interfaces are not supported");
        }
        if (clazz.isRecord()) {
            throw new IllegalArgumentException("Records are not supported");
        }
        if (clazz.isArray()) {
            throw new IllegalArgumentException("Arrays are not supported");
        }
        if (clazz.isEnum()) {
            throw new IllegalArgumentException("Enums are not supported");
        }
        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("Primitives are not supported");
        }
        if (clazz.isSynthetic()) {
            throw new IllegalArgumentException("Synthetic classes are not supported");
        }
        return clazz;
    }
}

class ObjectGenerator<T> implements Generator<T> {
    private final Class<T> clazz;
    private final Generator<T> constructor;
    private final Map<String, Generator<?>> fieldGenerators;

    ObjectGenerator(Class<T> clazz, Generator<T> constructor, Map<String, Generator<?>> fieldGenerators) {
        this.clazz = clazz;
        this.constructor = constructor;
        this.fieldGenerators = fieldGenerators;
    }

    @Override
    public T generate(RandomGenerator random) {
        T obj = constructor.generate(random, this);
        for (Map.Entry<String, Generator<?>> entry : fieldGenerators.entrySet()) {
            try {
                Field field = clazz.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                field.set(obj, entry.getValue().generate(random, this));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                //ignore
            }
        }
        return obj;
    }
}
