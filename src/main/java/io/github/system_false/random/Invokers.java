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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.random.RandomGenerator;

/**
 * Utility class for creating function for invoking methods with randomized arguments.
 */
public final class Invokers {
    /**
     * Private constructor to prevent instantiation.
     */
    private Invokers() {}

    /**
     * Method creates a consumer that invokes the given consumer with randomized argument.
     *
     * @param consumer  base consumer to invoke
     * @param generator generator of the argument
     * @param <T>       the type of the argument
     *
     * @return consumer invoker
     * @throws NullPointerException if either {@code consumer} or {@code generator} is null
     */
    public static <T> Consumer<RandomGenerator> consumer(Consumer<T> consumer, Generator<T> generator) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(generator);
        return random -> consumer.accept(generator.generate(random));
    }

    /**
     * Method creates a function that invokes the given function with randomized argument.
     * @param function  base function to invoke
     * @param generator generator of the argument
     * @param <T>       the type of the argument
     * @param <R>       the type of the return value
     *
     * @return function invoker
     * @throws NullPointerException if either {@code function} or {@code generator} is null
     */
    public static <T, R> Function<RandomGenerator, R> function(Function<T, R> function, Generator<T> generator) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(generator);
        return random -> function.apply(generator.generate(random));
    }

    /**
     * Method creates a bi-consumer that invokes the given bi-consumer with randomized arguments.
     * @param consumer   base bi-consumer to invoke
     * @param generator1 generator of the first argument
     * @param generator2 generator of the second argument
     * @param <T1>       the type of the first argument
     * @param <T2>       the type of the second argument
     *
     * @return bi-consumer invoker
     * @throws NullPointerException if either {@code consumer}, {@code generator1} or {@code generator2} is null
     */
    public static <T1, T2> Consumer<RandomGenerator> biConsumer(BiConsumer<T1, T2> consumer, Generator<T1> generator1,
                                                         Generator<T2> generator2) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(generator1);
        Objects.requireNonNull(generator2);
        return random -> consumer.accept(generator1.generate(random), generator2.generate(random));
    }

    /**
     * Method creates a bi-function that invokes the given bi-function with randomized arguments.
     * @param function   base bi-function to invoke
     * @param generator1 generator of the first argument
     * @param generator2 generator of the second argument
     * @param <T1>       the type of the first argument
     * @param <T2>       the type of the second argument
     * @param <R>        the type of the return value
     *
     * @return bi-function invoker
     * @throws NullPointerException if either {@code function}, {@code generator1} or {@code generator2} is null
     */
    public static <T1, T2, R> Function<RandomGenerator, R> biFunction(BiFunction<T1, T2, R> function, Generator<T1> generator1,
                                                               Generator<T2> generator2) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(generator1);
        Objects.requireNonNull(generator2);
        return random -> function.apply(generator1.generate(random), generator2.generate(random));
    }

    /**
     * Method creates a method handle that invokes the given method handle with randomized arguments.
     * @param handle     base method handle to invoke
     * @param generators generators of the arguments
     *
     * @return method handle invoker
     * @throws NullPointerException if either {@code handle} or {@code generators} is null
     */
    public static MethodHandle handle(MethodHandle handle, Generator<?>... generators) {
        Objects.requireNonNull(handle);
        Objects.requireNonNull(generators);
        for (int i = 0; i < generators.length; i++) {
            Objects.requireNonNull(generators[i], "generator at index " + i);
        }
        MethodHandle converted = new Handle(handle, generators).applyHandle();
        converted.type().changeReturnType(handle.type().returnType());
        return converted;
    }

    private record Handle(MethodHandle handle, Generator<?>[] generators) {
        public Object apply(RandomGenerator random) {
            Object[] args = new Object[generators.length];
            for (int i = 0; i < generators.length; i++) {
                args[i] = generators[i].generate(random);
            }
            try {
                return handle.invoke(args);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private MethodHandle applyHandle() {
            try {
                return MethodHandles.lookup().findVirtual(Handle.class, "apply",
                        MethodType.methodType(Object.class, RandomGenerator.class))
                        .bindTo(this);
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
