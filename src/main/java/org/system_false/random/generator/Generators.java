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
import java.util.*;
import java.util.function.BiFunction;

/**
 * Utility class for creating instances of {@link org.system_false.random.generator.Generator} interface.
 */
public final class Generators {
    /**
     * Private constructor to prevent instantiation.
     */
    private Generators() {}

    /**
     * Creates a {@link org.system_false.random.generator.Generator} that generates random {@code boolean} values.
     * @return a {@link org.system_false.random.generator.Generator} that generates random {@code boolean} values
     */
    public static Generator<Boolean> ofBoolean() {
        return Random::nextBoolean;
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code byte} values.
     * @return a {@link RangeGenerator} that generates all possible {@code byte} values
     */
    public static RangeGenerator<Byte> ofByte() {
        return ofByte(Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code byte} values up to the given bound.
     * @param bound the upper bound of the range, exclusive
     * @return a {@link RangeGenerator} that generates all possible {@code byte} values up to the given bound
     */
    public static RangeGenerator<Byte> ofByte(byte bound) {
        return ofByte((byte) 0, (byte) (bound - 1));
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code byte} values in the given range.
     * @param minValue the lower bound of the range, inclusive
     * @param maxValue the upper bound of the range, inclusive
     * @return a {@link RangeGenerator} that generates all possible {@code byte} values in the given range
     */
    public static RangeGenerator<Byte> ofByte(byte minValue, byte maxValue) {
        return new ByteRangeGenerator(minValue, maxValue);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code byte} elements of the given array.
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code byte} elements of the given array
     */
    public static PoolGenerator<Byte> ofBytePool(byte... values) {
        return ofBytePool(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code byte} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code byte} elements of the given array
     */
    public static PoolGenerator<Byte> ofBytePool(boolean useBundle, byte... values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code byte} elements of the given list.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the list. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the list of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code byte} elements of the given list
     */
    public static PoolGenerator<Byte> ofBytePool(boolean useBundle, List<Byte> values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code char} values.
     * @return a {@link RangeGenerator} that generates all possible {@code char} values
     */
    public static RangeGenerator<Character> ofChar() {
        return ofChar(Character.MIN_VALUE, Character.MAX_VALUE);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible ASCII {@code char} values (\u0000 - \u007f).
     * @return a {@link RangeGenerator} that generates all possible ASCII {@code char} values (\u0000 - \u007f)
     */
    public static RangeGenerator<Character> ofASCIIChar() {
        return ofChar('\0', '\u007f');
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code char} values up to the given bound.
     * @param bound the upper bound of the range, exclusive
     * @return a {@link RangeGenerator} that generates all possible {@code char} values up to the given bound
     */
    public static RangeGenerator<Character> ofChar(char bound) {
        return ofChar((char) 0, (char) (bound - 1));
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code char} values in the given range.
     * @param minValue the lower bound of the range, inclusive
     * @param maxValue the upper bound of the range, inclusive
     * @return a {@link RangeGenerator} that generates all possible {@code char} values in the given range
     */
    public static RangeGenerator<Character> ofChar(char minValue, char maxValue) {
        return new CharRangeGenerator(minValue, maxValue);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code char} elements of the given array.
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code char} elements of the given array
     */
    public static PoolGenerator<Character> ofCharPool(char... values) {
        return ofCharPool(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code char} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code char} elements of the given array
     */
    public static PoolGenerator<Character> ofCharPool(boolean useBundle, char... values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code char} elements of the given list.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the list. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the list of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code char} elements of the given list
     */
    public static PoolGenerator<Character> ofCharPool(boolean useBundle, List<Character> values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code short} values.
     * @return a {@link RangeGenerator} that generates all possible {@code short} values
     */
    public static RangeGenerator<Short> ofShort() {
        return ofShort(Short.MIN_VALUE, Short.MAX_VALUE);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code short} values up to the given bound.
     * @param bound the upper bound of the range, exclusive
     * @return a {@link RangeGenerator} that generates all possible {@code short} values up to the given bound
     */
    public static RangeGenerator<Short> ofShort(short bound) {
        return ofShort((short) 0, (short) (bound - 1));
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code short} values in the given range.
     * @param minValue the lower bound of the range, inclusive
     * @param maxValue the upper bound of the range, inclusive
     * @return a {@link RangeGenerator} that generates all possible {@code short} values in the given range
     */
    public static RangeGenerator<Short> ofShort(short minValue, short maxValue) {
        return new ShortRangeGenerator(minValue, maxValue);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code short} elements of the given array.
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code short} elements of the given array
     */
    public static PoolGenerator<Short> ofShortPool(short... values) {
        return ofShortPool(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code short} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code short} elements of the given array
     */
    public static PoolGenerator<Short> ofShortPool(boolean useBundle, short... values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code short} elements of the given list.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the list. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the list of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code short} elements of the given list
     */
    public static PoolGenerator<Short> ofShortPool(boolean useBundle, List<Short> values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code int} values.
     * @return a {@link RangeGenerator} that generates all possible {@code int} values
     */
    public static RangeGenerator<Integer> ofInt() {
        return ofInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code int} values up to the given bound.
     * @param bound the upper bound of the range, exclusive
     * @return a {@link RangeGenerator} that generates all possible {@code int} values up to the given bound
     */
    public static RangeGenerator<Integer> ofInt(int bound) {
        return ofInt(0, bound - 1);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code int} values in the given range.
     * @param minValue the lower bound of the range, inclusive
     * @param maxValue the upper bound of the range, inclusive
     * @return a {@link RangeGenerator} that generates all possible {@code int} values in the given range
     */
    public static RangeGenerator<Integer> ofInt(int minValue, int maxValue) {
        return new IntRangeGenerator(minValue, maxValue);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code int} elements of the given array.
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code int} elements of the given array
     */
    public static PoolGenerator<Integer> ofIntPool(int... values) {
        return ofIntPool(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code int} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code int} elements of the given array
     */
    public static PoolGenerator<Integer> ofIntPool(boolean useBundle, int... values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code int} elements of the given list.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the list. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the list of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code int} elements of the given list
     */
    public static PoolGenerator<Integer> ofIntPool(boolean useBundle, List<Integer> values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code long} values.
     * @return a {@link RangeGenerator} that generates all possible {@code long} values
     */
    public static RangeGenerator<Long> ofLong() {
        return ofLong(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code long} values up to the given bound.
     * @param bound the upper bound of the range, exclusive
     * @return a {@link RangeGenerator} that generates all possible {@code long} values up to the given bound
     */
    public static RangeGenerator<Long> ofLong(long bound) {
        return ofLong(0L, bound - 1);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code long} values in the given range.
     * @param minValue the lower bound of the range, inclusive
     * @param maxValue the upper bound of the range, inclusive
     * @return a {@link RangeGenerator} that generates all possible {@code long} values in the given range
     */
    public static RangeGenerator<Long> ofLong(long minValue, long maxValue) {
        return new LongRangeGenerator(minValue, maxValue);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code long} elements of the given array.
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code long} elements of the given array
     */
    public static PoolGenerator<Long> ofLongPool(long... values) {
        return ofLongPool(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code long} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code long} elements of the given array
     */
    public static PoolGenerator<Long> ofLongPool(boolean useBundle, long... values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code long} elements of the given list.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the list. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the list of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code long} elements of the given list
     */
    public static PoolGenerator<Long> ofLongPool(boolean useBundle, List<Long> values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code float} values from 0 to 1.
     * @return a {@link RangeGenerator} that generates all possible {@code float} values from 0 to 1
     */
    public static RangeGenerator<Float> ofFloat() {
        return ofFloat(0F, 1F);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code float} values from 0 up to the given bound.
     * @param bound the upper bound of the range, exclusive
     * @return a {@link RangeGenerator} that generates all possible {@code float} values from 0 up to the given bound
     */
    public static RangeGenerator<Float> ofFloat(float bound) {
        return ofFloat(0F, Math.nextDown(bound));
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code float} values in the given range.
     * @param minValue the lower bound of the range, inclusive
     * @param maxValue the upper bound of the range, inclusive
     * @return a {@link RangeGenerator} that generates all possible {@code float} values in the given range
     */
    public static RangeGenerator<Float> ofFloat(float minValue, float maxValue) {
        return new FloatRangeGenerator(minValue, maxValue);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code float} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code float} elements of the given array
     */
    public static PoolGenerator<Float> ofFloatPool(float... values) {
        return ofFloatPool(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code float} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code float} elements of the given array
     */
    public static PoolGenerator<Float> ofFloatPool(boolean useBundle, float... values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code float} elements of the given list.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the list. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the list of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code float} elements of the given list
     */
    public static PoolGenerator<Float> ofFloatPool(boolean useBundle, List<Float> values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code double} values from 0 to 1.
     * @return a {@link RangeGenerator} that generates all possible {@code double} values from 0 to 1
     */
    public static RangeGenerator<Double> ofDouble() {
        return ofDouble(0D, 1D);
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code double} values up to the given bound.
     * <p>
     * The given bound is used to generate a random double value.
     * </p>
     *
     * @param bound the upper bound of the range, exclusive
     * @return a {@link RangeGenerator} that generates all possible {@code double} values up to the given bound
     */
    public static RangeGenerator<Double> ofDouble(double bound) {
        return ofDouble(0D, Math.nextDown(bound));
    }

    /**
     * Creates a {@link RangeGenerator} that generates all possible {@code double} values in the given range.
     *
     * @param minValue the lower bound of the range, inclusive
     * @param maxValue the upper bound of the range, inclusive
     * @return a {@link RangeGenerator} that generates all possible {@code double} values in the given range
     */
    public static RangeGenerator<Double> ofDouble(double minValue, double maxValue) {
        return new DoubleRangeGenerator(minValue, maxValue);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code double} elements of the given array.
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code double} elements of the given array
     */
    public static PoolGenerator<Double> ofDoublePool(double... values) {
        return ofDoublePool(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code double} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     *
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code double} elements of the given array
     */
    public static PoolGenerator<Double> ofDoublePool(boolean useBundle, double... values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code double} elements of the given list.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the list. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the list of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code double} elements of the given list
     */
    public static PoolGenerator<Double> ofDoublePool(boolean useBundle, List<Double> values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link Generator} that generates all possible strings of the given length.
     * <p>
     * The generator will generate all possible strings of the given length consisting of ASCII characters.
     * </p>
     *
     * @param length the length of the strings to generate
     * @return a {@link Generator} that generates all possible strings of the given length
     */
    public static Generator<String> ofString(int length) {
        return ofString(length, length, ofASCIIChar());
    }

    /**
     * Creates a {@link Generator} that generates all possible strings of the given length.
     * <p>
     * The given length is used to generate a random string.
     * The given {@link Generator} is used to generate characters.
     * </p>
     *
     * @param length the length of the strings to generate
     * @param generator the generator of characters
     * @return a {@link Generator} that generates all possible strings of the given length
     */
    public static Generator<String> ofString(int length, Generator<Character> generator) {
        return ofString(length, length, generator);
    }

    /**
     * Creates a {@link Generator} that generates all possible strings.
     * <p>
     * The generator will generate all possible strings of the given length consisting of ASCII characters.
     * </p>
     *
     * @param minLength the smallest possible length, inclusive
     * @param maxLength the largest possible length, inclusive
     * @return a {@link Generator} that generates all possible strings of the given length
     */
    public static Generator<String> ofString(int minLength, int maxLength) {
        return ofString(minLength, maxLength, ofASCIIChar());
    }

    /**
     * Creates a {@link Generator} that generates all possible strings of the given length.
     * <p>
     * The generator will generate all possible strings of the given length consisting of characters
     * generated by the given character generator.
     * </p>
     *
     * @param minLength the minimum length of the strings to generate
     * @param maxLength the maximum length of the strings to generate
     * @param generator the generator of characters
     * @return a {@link Generator} that generates all possible strings of the given length
     */
    public static Generator<String> ofString(int minLength, int maxLength, Generator<Character> generator) {
        return random -> Generator.generateString(random, minLength, maxLength, generator);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random strings from the given values.
     * @param values the list of possible values to generate from
     * @return a {@link PoolGenerator} that generates random strings from the given values
     */
    public static PoolGenerator<String> ofStringPool(String... values) {
        return ofStringPool(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random strings from the given values.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * strings without repeats until it reaches the end of the array. Then it will generate
     * all strings again.
     * </p>
     * @param useBundle whether to generate strings in a bundle or not
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random strings from the given values
     */
    public static PoolGenerator<String> ofStringPool(boolean useBundle, String... values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random strings from the given values.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * strings without repeats until it reaches the end of the list. Then it will generate
     * all strings again.
     * </p>
     * @param useBundle whether to generate strings in a bundle or not
     * @param values the list of values to generate from
     * @return a {@link PoolGenerator} that generates random strings from the given values
     */
    public static PoolGenerator<String> ofStringPool(boolean useBundle, List<String> values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link Generator} that generates random enum constants of the given enum class.
     * <p>
     * The generator will generate random enum constants of the given enum class.
     * </p>
     *
     * @param enumClass the enum class
     * @return a {@link Generator} that generates random enum constants of the given enum class
     */
    public static <T extends Enum<T>> Generator<T> ofEnum(Class<T> enumClass) {
        return ofEnum(enumClass, enumClass.getEnumConstants() != null ? ofInt(enumClass.getEnumConstants().length) : null);
    }

    /**
     * Creates a {@link Generator} that generates random enum constants of the given enum class.
     * <p>
     * The generator will generate random enum constants of the given enum class. The given
     * {@link Generator} is used to generate a random index of the enum constant.
     * </p>
     *
     * @param enumClass the enum class
     * @param intGenerator the generator of indices
     * @return a {@link Generator} that generates random enum constants of the given enum class
     */
    public static <T extends Enum<T>> Generator<T> ofEnum(Class<T> enumClass, Generator<Integer> intGenerator) {
        return random -> Generator.generateEnum(random, enumClass, intGenerator);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random enum constants of the given enum values.
     * @param values the enum values to generate from
     * @return a {@link PoolGenerator} that generates random enum constants of the given enum values
     */
    @SafeVarargs
    public static <T extends Enum<T>> PoolGenerator<T> ofEnumPool(T... values) {
        return ofEnumPool(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random enum constants of the given enum values.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * enum constants without repeats until it reaches the end of the array. Then it will generate
     * all enum constants again.
     * </p>
     * @param useBundle whether to generate enum constants in a bundle or not
     * @param values the array of enum values to generate from
     * @return a {@link PoolGenerator} that generates random enum constants of the given enum values
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> PoolGenerator<T> ofEnumPool(boolean useBundle, T... values) {
        if (Objects.requireNonNull(values, "values").length == 0) {
            throw new IllegalArgumentException("Empty values array");
        }
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random enum constants of the given enum values.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * enum constants without repeats until it reaches the end of the list. Then it will generate
     * all enum constants again.
     * </p>
     * @param useBundle whether to generate enum constants in a bundle or not
     * @param values the list of enum values to generate from
     * @return a {@link PoolGenerator} that generates random enum constants of the given enum values
     */
    public static <T extends Enum<T>> PoolGenerator<T> ofEnumPool(boolean useBundle, List<T> values) {
        if (Objects.requireNonNull(values).isEmpty()) {
            throw new IllegalArgumentException("Empty values list");
        }
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link Generator} that generates random arrays of the given element class with the given length.
     * <p>
     * The generator will generate random arrays of the given element class with the given length. Each element
     * of the array will be generated by the given element generator.
     * </p>
     *
     * @param elementClass the class of the elements of the arrays to generate
     * @param generator the generator of elements
     * @param length the length of the arrays to generate
     * @return a {@link Generator} that generates random arrays of the given element class with the given length
     */
    public static <T, A> Generator<A> ofArray(Class<T> elementClass, Generator<T> generator, int length) {
        return random -> Generator.generateArray(random, elementClass, generator, length);
    }

    /**
     * Creates a {@link Generator} that generates random lists of the given element type with the given length.
     * <p>
     * The generator will generate random lists of the given element type with the given length. Each element of the
     * list will be generated by the given element generator.
     * </p>
     *
     * @param elementType the class of the elements of the lists to generate
     * @param generator the generator of elements
     * @param length the length of the lists to generate
     * @return a {@link Generator} that generates random lists of the given element type with the given length
     */
    public static <T> Generator<List<T>> ofList(Class<T> elementType, Generator<T> generator, int length) {
        return random -> Generator.generateList(random, elementType, generator, length);
    }

    /**
     * Creates a {@link Generator} that generates random sets of the given element type with the given length.
     * <p>
     * The generator will generate random sets of the given element type with the given length. Each element of the
     * set will be generated by the given element generator.
     * </p>
     *
     * @param elementType the class of the elements of the sets to generate
     * @param generator the generator of elements
     * @param length the maximum length of the sets to generate
     * @return a {@link Generator} that generates random sets of the given element type with the given length
     */
    public static <T> Generator<Set<T>> ofSet(Class<T> elementType, Generator<T> generator, int length) {
        return random -> Generator.generateSet(random, elementType, generator, length);
    }

    /**
     * Creates a {@link Generator} that generates random maps of the given key and value types.
     * <p>
     * The generator will generate random maps of the given key and value types. The given
     * {@link Generator}s are used to generate the keys and values of the map. If a key is already
     * present in the map, the given {@link BiFunction} is used to resolve the duplicate.
     * </p>
     *
     * @param keyType the class of the keys of the maps to generate
     * @param valueType the class of the values of the maps to generate
     * @param keyGenerator the generator of keys
     * @param valueGenerator the generator of values
     * @param duplicateResolver the function to resolve duplicate keys
     * @param length the maximum length of the maps to generate
     * @return a {@link Generator} that generates random maps of the given key and value types
     */
    public static <K, V> Generator<Map<K, V>> ofMap(Class<K> keyType, Class<V> valueType, Generator<K> keyGenerator,
                                                    Generator<V> valueGenerator, BiFunction<V, V, V> duplicateResolver,
                                                    int length) {
        return random -> Generator.generateMap(random, keyType, valueType, keyGenerator, valueGenerator,
                duplicateResolver, length);
    }

    /**
     * Creates a {@link Generator} that generates random objects of the given class.
     * <p>
     * The generator will generate random objects of the given class..
     * </p>
     *
     * @param clazz the class to generate
     * @return a {@link Generator} that generates random objects of the given class
     *
     * @see ObjectGenerator
     */
    public static <T> ObjectGenerator<T> of(Class<T> clazz) {
        return new ObjectGenerator<>(clazz);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random elements of the given array.
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random elements of the given array
     */
    @SafeVarargs
    public static <T> PoolGenerator<T> ofPool(T... values) {
        return ofPool(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values the array of values to generate from
     * @return a {@link PoolGenerator} that generates random elements of the given array
     */
    @SuppressWarnings("unchecked")
    public static <T> PoolGenerator<T> ofPool(boolean useBundle, T... values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random elements of the given list.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the list. Then it will generate
     * all elements again.
     * </p>
     *
     * @param useBundle whether to generate elements in a bundle or not
     * @param list the list of values to generate from
     * @return a {@link PoolGenerator} that generates random elements of the given list
     */
    public static <T> PoolGenerator<T> ofPool(boolean useBundle, List<T> list) {
        return new DefaultPoolGenerator<>(list, useBundle);
    }

    /**
     * Creates a generator that returns a constant value.
     * <p>
     * The given value is returned every time the generated value is requested.
     * </p>
     *
     * @param value the value to generate
     * @return a generator that generates the given value
     */
    public static <E> Generator<E> ofValue(E value) {
        return random -> value;
    }

    static class ByteRangeGenerator implements RangeGenerator<Byte> {
        private final byte minValue;
        private final byte maxValue;

        public ByteRangeGenerator(byte minValue, byte maxValue) {
            if (minValue > maxValue) {
                throw new IllegalArgumentException("min value must be less than max value");
            }
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public Byte minValue() {
            return minValue;
        }

        @Override
        public Byte maxValue() {
            return maxValue;
        }

        @Override
        public boolean test(Byte aByte) {
            return minValue <= aByte && aByte <= maxValue;
        }

        @Override
        public Byte generate(Random random) {
            return Generator.generateByte(random, minValue, maxValue);
        }
    }

    static class CharRangeGenerator implements RangeGenerator<Character> {
        private final char minValue;
        private final char maxValue;

        public CharRangeGenerator(char minValue, char maxValue) {
            if (minValue > maxValue) {
                throw new IllegalArgumentException("min value must be less than max value");
            }
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public Character minValue() {
            return minValue;
        }

        @Override
        public Character maxValue() {
            return maxValue;
        }

        @Override
        public boolean test(Character character) {
            return minValue <= character && character <= maxValue;
        }

        @Override
        public Character generate(Random random) {
            return Generator.generateChar(random, minValue, maxValue);
        }
    }

    static class ShortRangeGenerator implements RangeGenerator<Short> {
        private final short minValue;
        private final short maxValue;

        public ShortRangeGenerator(short minValue, short maxValue) {
            if (minValue > maxValue) {
                throw new IllegalArgumentException("min value must be less than max value");
            }
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public Short minValue() {
            return minValue;
        }

        @Override
        public Short maxValue() {
            return maxValue;
        }

        @Override
        public boolean test(Short aShort) {
            return minValue <= aShort && aShort <= maxValue;
        }

        @Override
        public Short generate(Random random) {
            return Generator.generateShort(random, minValue, maxValue);
        }
    }

    static class IntRangeGenerator implements RangeGenerator<Integer> {
        private final int minValue;
        private final int maxValue;

        public IntRangeGenerator(int minValue, int maxValue) {
            if (minValue > maxValue) {
                throw new IllegalArgumentException("min value must be less than max value");
            }
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public Integer minValue() {
            return minValue;
        }

        @Override
        public Integer maxValue() {
            return maxValue;
        }

        @Override
        public boolean test(Integer anInt) {
            return minValue <= anInt && anInt <= maxValue;
        }

        @Override
        public Integer generate(Random random) {
            return Generator.generateInt(random, minValue, maxValue);
        }
    }

    static class LongRangeGenerator implements RangeGenerator<Long> {
        private final long minValue;
        private final long maxValue;

        public LongRangeGenerator(long minValue, long maxValue) {
            if (minValue > maxValue) {
                throw new IllegalArgumentException("min value must be less than max value");
            }
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public Long minValue() {
            return minValue;
        }

        @Override
        public Long maxValue() {
            return maxValue;
        }

        @Override
        public boolean test(Long aLong) {
            return minValue <= aLong && aLong <= maxValue;
        }

        @Override
        public Long generate(Random random) {
            return Generator.generateLong(random, minValue, maxValue);
        }
    }

    static class FloatRangeGenerator implements RangeGenerator<Float> {
        private final float minValue;
        private final float maxValue;

        public FloatRangeGenerator(float minValue, float maxValue) {
            if (Float.isInfinite(minValue) || Float.isInfinite(maxValue)) {
                throw new IllegalArgumentException("min and max values must be finite");
            }
            if (Float.isNaN(minValue) || Float.isNaN(maxValue)) {
                throw new IllegalArgumentException("min and max values must not be NaN");
            }
            if (minValue > maxValue) {
                throw new IllegalArgumentException("min value must be less than max value");
            }
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public Float minValue() {
            return minValue;
        }

        @Override
        public Float maxValue() {
            return maxValue;
        }

        @Override
        public boolean test(Float aFloat) {
            return minValue <= aFloat && aFloat <= maxValue;
        }

        @Override
        public Float generate(Random random) {
            return Generator.generateFloat(random, minValue, maxValue);
        }
    }

    static class DoubleRangeGenerator implements RangeGenerator<Double> {
        private final double minValue;
        private final double maxValue;

        public DoubleRangeGenerator(double minValue, double maxValue) {
            if (Double.isInfinite(minValue) || Double.isInfinite(maxValue)) {
                throw new IllegalArgumentException("min and max values must be finite");
            }
            if (Double.isNaN(minValue) || Double.isNaN(maxValue)) {
                throw new IllegalArgumentException("min and max values must not be NaN");
            }
            if (minValue > maxValue) {
                throw new IllegalArgumentException("min value must be less than max value");
            }
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public Double minValue() {
            return minValue;
        }

        @Override
        public Double maxValue() {
            return maxValue;
        }

        @Override
        public boolean test(Double aDouble) {
            return minValue <= aDouble && aDouble <= maxValue;
        }

        @Override
        public Double generate(Random random) {
            return Generator.generateDouble(random, minValue, maxValue);
        }
    }

    static class DefaultPoolGenerator<T> implements PoolGenerator<T> {
        private final int length;
        private final Object pool;
        private int bundleIndex;

        public DefaultPoolGenerator(List<T> list, boolean useBundle) {
            Objects.requireNonNull(list);
            length = list.size();
            if (length < 2) {
                throw new IllegalArgumentException("pool must have at least 2 elements");
            }
            Class<?> elementType = null;
            for (T element : list) {
                if (element != null) {
                    elementType = commonSuperClass(elementType, element.getClass());
                }
            }
            if (elementType == null) {
                throw new IllegalArgumentException("list must contain at least one non-null element");
            }
            this.pool = Array.newInstance(elementType, list.size());
            for (int i = 0; i < list.size(); i++) {
                Array.set(pool, i, list.get(i));
            }
            bundleIndex = useBundle ? 0 : -1;
        }

        @SuppressWarnings("SuspiciousSystemArraycopy")
        public DefaultPoolGenerator(Object pool, boolean useBundle) {
            Objects.requireNonNull(pool);
            length = Array.getLength(pool);
            if (length < 2) {
                throw new IllegalArgumentException("pool must have at least 2 elements");
            }
            this.pool = Array.newInstance(pool.getClass().getComponentType(), length);
            System.arraycopy(pool, 0, this.pool, 0, length);
            bundleIndex = useBundle ? 0 : -1;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<T> getPool() {
            Object[] array = new Object[Array.getLength(pool)];
            for (int i = 0; i < length; i++) {
                array[i] = Array.get(pool, i);
            }
            return (List<T>) List.of(array);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T generate(Random random) {
            if (bundleIndex != -1) {
                int nextIndex = random.nextInt(length - bundleIndex) + bundleIndex;
                T next = (T) Array.get(pool, nextIndex);
                T current = (T) Array.get(pool, bundleIndex);
                Array.set(pool, bundleIndex, next);
                Array.set(pool, nextIndex, current);
                bundleIndex = (bundleIndex + 1) % length;
                return next;
            }
            return (T) Array.get(pool, random.nextInt(length));
        }

        private static Class<?> commonSuperClass(Class<?> a, Class<?> b) {
            if (a == null) return b;
            while (!a.isAssignableFrom(b)) {
                a = a.getSuperclass();
            }
            return a;
        }
    }
}
