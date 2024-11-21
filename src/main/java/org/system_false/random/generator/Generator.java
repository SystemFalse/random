/*
 * Copyright (C) 2024 SystemFalse.
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

package org.system_false.random.generator;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This interface is used to generate random values of any type.
 * <p>
 * It has realizations of base types generation:
 * <ul>
 *     <li>{@link #generateByte(Random, byte, byte)}</li>
 *     <li>{@link #generateChar(Random, char, char)}</li>
 *     <li>{@link #generateShort(Random, short, short)}</li>
 *     <li>{@link #generateInt(Random, int, int)}</li>
 *     <li>{@link #generateLong(Random, long, long)}</li>
 *     <li>{@link #generateFloat(Random, float, float)}</li>
 *     <li>{@link #generateDouble(Random, double, double)}</li>
 *     <li>{@link #generateString(Random, int, int, Generator)}</li>
 *     <li>{@link #generateEnum(Random, Class, Generator)}</li>
 *     <li>{@link #generateArray(Random, Class, Generator, int)}</li>
 *     <li>{@link #generateList(Random, Class, Generator, int)}</li>
 *     <li>{@link #generateSet(Random, Class, Generator, int)}</li>
 *     <li>{@link #generateMap(Random, Class, Class, Generator, Generator, BiFunction, int)}</li>
 * </ul>
 * </p>
 *
 * @param <T> the type of the generated value
 */
@FunctionalInterface
public interface Generator<T> {
    /**
     * Generates a random value of the type represented by this generator.
     *
     * @param random PRNG or RNG to use
     * @return a random value of the type represented by this generator
     */
    T generate(Random random);

    /**
     * Generates a random value of the type represented by this generator using a newly created PRNG.
     *
     * @return a random value of the type represented by this generator
     */
    default T generate() {
        return generate(new Random());
    }

    /**
     * Maps the generated value to a new value of type {@code R} using the given function.
     *
     * @param mapper the function to use for mapping
     * @param <R>    the type of the new value
     * @return a new {@link Generator} that maps the generated value to a new value of type {@code R}
     */
    default <R> Generator<R> map(Function<T, R> mapper) {
        Objects.requireNonNull(mapper);
        return random -> mapper.apply(generate(random));
    }

    /**
     * Maps the generated value to a new generator of type {@code R} using the given function and
     * then generates a value from the new generator.
     *
     * @param mapper the function to use for mapping
     * @param <R>    the type of the new value
     * @return a new {@link Generator} that maps the generated value to a new generator and then
     * generates a value from the new generator
     */
    default <R> Generator<R> flatMap(Function<T, Generator<R>> mapper) {
        Objects.requireNonNull(mapper);
        return random -> mapper.apply(generate(random)).generate(random);
    }

    /**
     * Generates a random byte.
     * <p>
     * The given min and max values are used to generate a random byte value.
     * </p>
     *
     * @param random the PRNG or RNG to use
     * @param minValue the smallest possible value, inclusive
     * @param maxValue the largest possible value, inclusive
     * @return a random byte
     *
     * @throws NullPointerException if {@code random} is {@code null}
     * @throws GenerationException if {@code minValue} is greater than {@code maxValue}
     */
    static byte generateByte(Random random, byte minValue, byte maxValue) {
        Objects.requireNonNull(random, "random");
        if (minValue > maxValue) {
            throw new GenerationException("min value must be less than or equal to max value");
        }
        return (byte) (random.nextInt((short) maxValue - minValue + 1) + minValue);
    }

    /**
     * Generates a random character.
     * <p>
     * The given min and max values are used to generate a random character value.
     * </p>
     *
     * @param random the PRNG or RNG to use
     * @param minValue the smallest possible value, inclusive
     * @param maxValue the largest possible value, inclusive
     * @return a random character
     *
     * @throws NullPointerException if {@code random} is {@code null}
     * @throws GenerationException if {@code minValue} is greater than {@code maxValue}
     */
    static char generateChar(Random random, char minValue, char maxValue) {
        Objects.requireNonNull(random, "random");
        if (minValue > maxValue) {
            throw new GenerationException("min value must be less than or equal to max value");
        }
        return (char) (random.nextInt((int) maxValue - minValue + 1) + minValue);
    }

    /**
     * Generates a random short value.
     * <p>
     * The given min and max values are used to generate a random short value.
     * </p>
     *
     * @param random the PRNG or RNG to use
     * @param minValue the smallest possible value, inclusive
     * @param maxValue the largest possible value, inclusive
     * @return a random short value
     *
     * @throws NullPointerException if {@code random} is {@code null}
     * @throws GenerationException if {@code minValue} is greater than {@code maxValue}
     */
    static short generateShort(Random random, short minValue, short maxValue) {
        Objects.requireNonNull(random, "random");
        if (minValue > maxValue) {
            throw new GenerationException("min value must be less than or equal to max value");
        }
        return (short) (random.nextInt((int) maxValue - minValue + 1) + minValue);
    }

    /**
     * Generates a random int value.
     * <p>
     * The given min and max values are used to generate a random int value.
     * </p>
     *
     * @param random the PRNG or RNG to use
     * @param minValue the smallest possible value, inclusive
     * @param maxValue the largest possible value, inclusive
     * @return a random int value
     *
     * @throws NullPointerException if {@code random} is {@code null}
     * @throws GenerationException if {@code minValue} is greater than {@code maxValue}
     */
    static int generateInt(Random random, int minValue, int maxValue) {
        Objects.requireNonNull(random, "random");
        if (minValue > maxValue) {
            throw new GenerationException("min value must be less than or equal to max value");
        }
        return (int) (random.nextLong((long) maxValue - minValue + 1) + minValue);
    }
    
    /**
     * Generates a random long value.
     * <p>
     * The given min and max values are used to generate a random long value.
     * </p>
     *
     * @param random the PRNG or RNG to use
     * @param minValue the smallest possible value, inclusive
     * @param maxValue the largest possible value, inclusive
     * @return a random long value
     *
     * @throws NullPointerException if {@code random} is {@code null}
     * @throws GenerationException if {@code minValue} is greater than {@code maxValue}
     */
    static long generateLong(Random random, long minValue, long maxValue) {
        Objects.requireNonNull(random, "random");
        if (minValue > maxValue) {
            throw new GenerationException("min value must be less than or equal to max value");
        }
        long nextLong = random.nextLong();
        BigInteger bigMinValue = BigInteger.valueOf(minValue), bigMaxValue = BigInteger.valueOf(maxValue),
                randomValue = BigInteger.valueOf(nextLong > 0 ? nextLong : nextLong & Long.MAX_VALUE)
                .subtract(BigInteger.valueOf(Long.MIN_VALUE));
        return randomValue.mod(bigMaxValue.subtract(bigMinValue).add(BigInteger.ONE)).add(bigMinValue).longValue();
    }

    /**
     * Generates a random float value in range.
     * <p>
     * The given minimum and maximum values are used to generate a random float value.
     * They must be in range of 0 and 1.
     * </p>
     *
     * @param random the PRNG or RNG to use
     * @param minValue the smallest possible value, inclusive
     * @param maxValue the largest possible value, inclusive
     * @return a random float value
     *
     * @throws NullPointerException if {@code random} is {@code null}
     * @throws GenerationException if {@code minValue} is greater that {@code maxValue} or any of them is NaN or infinite
     */
    static float generateFloat(Random random, float minValue, float maxValue) {
        Objects.requireNonNull(random, "random");
        if (Float.isInfinite(minValue) || Float.isInfinite(maxValue)) {
            throw new GenerationException("min and max values must be finite");
        }
        if (Float.isNaN(minValue) || Float.isNaN(maxValue)) {
            throw new GenerationException("min and max values must not be NaN");
        }
        if (minValue > maxValue) {
            throw new GenerationException("min value must be less than or equal to max value");
        }
        return random.nextFloat() * (maxValue - minValue) + minValue;
    }
    
    /**
     * Generates a random double value in range.
     * <p>
     * The given minimum and maximum values are used to generate a random double value.
     * They must be in range of 0 and 1.
     * </p>
     *
     * @param random the PRNG or RNG to use
     * @param minValue the smallest possible value, inclusive
     * @param maxValue the largest possible value, inclusive
     * @return a random double value
     *
     * @throws NullPointerException if {@code random} is {@code null}
     * @throws GenerationException if {@code minValue} is greater that {@code maxValue} or any of them is NaN or infinite
     */
    static double generateDouble(Random random, double minValue, double maxValue) {
        Objects.requireNonNull(random, "random");
        if (Double.isInfinite(minValue) || Double.isInfinite(maxValue)) {
            throw new GenerationException("min and max values must be finite");
        }
        if (Double.isNaN(minValue) || Double.isNaN(maxValue)) {
            throw new GenerationException("min and max values must not be NaN");
        }
        if (minValue > maxValue) {
            throw new GenerationException("min value must be less than or equal to max value");
        }
        return random.nextDouble() * (maxValue - minValue) + minValue;
    }
    
    /**
     * Generates a random string value.
     * <p>
     * The given minimum and maximum lengths are used to generate a random string.
     * The given {@link Generator} is used to generate characters.
     * </p>
     *
     * @param random the PRNG or RNG to use
     * @param minLength the smallest possible length, inclusive
     * @param maxLength the largest possible length, inclusive
     * @param charGenerator the generator of characters
     * @return a random string
     *
     * @throws NullPointerException if {@code random} or {@code generator} is {@code null}
     * @throws GenerationException if {@code minValue} is greater than {@code maxValue}
     */
    static String generateString(Random random, int minLength, int maxLength, Generator<Character> charGenerator) {
        Objects.requireNonNull(random, "random");
        Objects.requireNonNull(charGenerator, "generator");
        if (minLength > maxLength) {
            throw new GenerationException("min length must be less than or equal to max length");
        }
        int length = (int) (random.nextLong((long) maxLength - minLength + 1) + minLength);
        char[] array = new char[length];
        for (int i = 0; i < length; i++) {
            array[i] = charGenerator.generate(random);
        }
        return new String(array);
    }

    /**
     * Generates a random enum constant.
     * <p>
     * The given {@link Random} is used to generate a random index of the enum constant.
     * The given {@link Class} is used to get the enum constants. The given
     * {@link Generator} is used to generate the index.
     * </p>
     *
     * @param random the PRNG or RNG to use
     * @param enumClass the enum class
     * @param intGenerator the generator of indices
     * @return a random enum constant
     *
     * @throws NullPointerException if {@code random}, {@code enumClass} or {@code generator} is {@code null}
     * @throws GenerationException if generated integer is less than 0 or greater than or equal to the number of
     *     enum constants
     */
    static <T extends Enum<T>> T generateEnum(Random random, Class<T> enumClass, Generator<Integer> intGenerator) {
        Objects.requireNonNull(random, "random");
        Objects.requireNonNull(enumClass, "enumClass");
        Objects.requireNonNull(intGenerator, "generator");
        //Enum class is forbidden
        if (enumClass.equals(Enum.class)) {
            throw new GenerationException("enumClass must not be " + enumClass.getCanonicalName());
        }
        int index = intGenerator.generate(random);
        T[] constants = enumClass.getEnumConstants();
        if (index < 0 || index >= constants.length) {
            throw new GenerationException("index must be between 0 and " + (constants.length - 1));
        }
        return constants[index];
    }

    /**
     * Generates a random array of the given element type.
     * <p>
     * The given {@link Generator} is used to generate a random element for each element in the array.
     * </p>
     *
     * @param random the PRNG or RNG to use
     * @param elementType the element class
     * @param generator the generator of elements
     * @param length the length of the array
     * @return a random array
     *
     * @throws NullPointerException if {@code random}, {@code elementType} or {@code generator} is {@code null}
     * @throws GenerationException if {@code length} is negative
     */
    @SuppressWarnings("unchecked")
    static <T, A> A generateArray(Random random, Class<T> elementType, Generator<T> generator, int length) {
        Objects.requireNonNull(random, "random");
        Objects.requireNonNull(elementType, "elementType");
        Objects.requireNonNull(generator, "generator");
        if (length < 0) {
            throw new GenerationException("length must not be negative");
        }
        Object array = Array.newInstance(elementType, length);
        for (int i = 0; i < length; i++) {
            Array.set(array, i, generator.generate(random));
        }
        return (A) array;
    }

    /**
     * Generates a random list of the given element type.
     * <p>
     * The given {@link Generator} is used to generate a random element for each element in the list.
     * </p>
     * This method has the following logic:
     * <pre>
     * {@code
     * ArrayList<T> list = new ArrayList<>(length);
     * for (int i = 0; i < length; i++) {
     *     list.add(generator.generate(random));
     * }
     * }
     * </pre>
     *
     * @param random the PRNG or RNG to use
     * @param elementType the element class
     * @param generator the generator of elements
     * @param length the length of the list
     * @return a random list
     *
     * @throws NullPointerException if {@code random}, {@code elementType} or {@code generator} is {@code null}
     * @throws GenerationException if {@code length} is negative
     */
    static <T> List<T> generateList(Random random, Class<T> elementType, Generator<T> generator, int length) {
        Objects.requireNonNull(random, "random");
        Objects.requireNonNull(elementType, "elementType");
        Objects.requireNonNull(generator, "generator");
        if (length < 0) {
            throw new GenerationException("length must not be negative");
        }
        ArrayList<T> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(generator.generate(random));
        }
        return list;
    }

    /**
     * Generates a random set of the given element type.
     * <p>
     * The given {@link Generator} is used to generate a random element for each element in the set.
     * </p>
     * This method has the following logic:
     * <pre>
     * {@code
     * HashSet<T> set = new HashSet<>(length);
     * for (int i = 0; i < length; i++) {
     *     set.add(generator.generate(random));
     * }
     * }
     * </pre>
     *
     * @param random the PRNG or RNG to use
     * @param elementType the element class
     * @param generator the generator of elements
     * @param length the maximum length of the set
     * @return a random set
     *
     * @throws NullPointerException if {@code random}, {@code elementType} or {@code generator} is {@code null}
     * @throws GenerationException if {@code length} is negative
     */
    static <T> Set<T> generateSet(Random random, Class<T> elementType, Generator<T> generator, int length) {
        Objects.requireNonNull(random, "random");
        Objects.requireNonNull(elementType, "elementType");
        Objects.requireNonNull(generator, "generator");
        if (length < 0) {
            throw new GenerationException("length must not be negative");
        }
        HashSet<T> set = new HashSet<>(length);
        for (int i = 0; i < length; i++) {
            set.add(generator.generate(random));
        }
        return set;
    }

    /**
     * Generates a random map of the given key and value types.
     * <p>
     * The given {@link Generator}s are used to generate a random key and value for each entry in the map.
     * </p>
     * <p>
     * If the given key is already present in the map, the given {@link BiFunction} is called to resolve the duplicate.
     * The first argument of the {@link BiFunction} is the value generated by the given {@link Generator} and the second
     * argument is the value currently in the map.
     * </p>
     * This method has the following logic:
     * <pre>
     * {@code
     * HashMap<K, V> map = new HashMap<>(length);
     * for (int i = 0; i < length; i++) {
     *     K key = keyGenerator.generate(random);
     *     V value = valueGenerator.generate(random);
     *     map.merge(key, value, duplicateResolver);
     * }
     * }
     * </pre>
     *
     * @param random            the PRNG or RNG to use
     * @param keyType           the key class
     * @param valueType         the value class
     * @param keyGenerator      the generator of keys
     * @param valueGenerator    the generator of values
     * @param duplicateResolver the function to resolve duplicate keys
     * @param length            the maximum length of the map
     * @return a random map
     *
     * @throws NullPointerException if {@code random}, {@code keyType}, {@code valueType}, {@code keyGenerator}, {@code valueGenerator}
     *                              or {@code duplicateResolver} is {@code null}
     * @throws GenerationException  if {@code length} is negative
     *
     * @see Map#merge(Object, Object, BiFunction)
     */
    static <K, V> Map<K, V> generateMap(Random random, Class<K> keyType, Class<V> valueType, Generator<K> keyGenerator,
                                        Generator<V> valueGenerator, BiFunction<V, V, V> duplicateResolver, int length) {
        Objects.requireNonNull(random, "random");
        Objects.requireNonNull(keyType, "keyType");
        Objects.requireNonNull(valueType, "valueType");
        Objects.requireNonNull(keyGenerator, "keyGenerator");
        Objects.requireNonNull(valueGenerator, "valueGenerator");
        Objects.requireNonNull(duplicateResolver, "duplicateResolver");
        if (length < 0) {
            throw new GenerationException("length must not be negative");
        }
        HashMap<K, V> map = new HashMap<>(length);
        for (int i = 0; i < length; i++) {
            K key = keyGenerator.generate(random);
            V value = valueGenerator.generate(random);
            map.merge(key, value, duplicateResolver);
        }
        return map;
    }
}
