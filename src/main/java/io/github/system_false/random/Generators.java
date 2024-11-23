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

package io.github.system_false.random;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;

/**
 * Utility class for creating instances of {@link Generator} interface.
 */
public final class Generators {
    /**
     * Private constructor to prevent instantiation.
     */
    private Generators() {}

    /**
     * Creates a {@link Generator} that generates random {@code boolean} values.
     * @return a {@link Generator} that generates random {@code boolean} values
     */
    public static Generator<Boolean> ofBoolean() {
        return Random::nextBoolean;
    }

    /**
     * Creates a {@link Generator} that generates a random {@code boolean} value based on the given chance.
     * <p>
     * The chance is a double value in the range [0, 1]. For example, if the chance is 0.25, then the generator
     * will return true for 25% of the generated values.
     * </p>
     * @param chance the chance of the generator returning true
     * @return a {@link Generator} that generates a random boolean value based on the given chance
     */
    public static Generator<Boolean> ofBoolean(double chance) {
        if (Double.isInfinite(chance)) {
            throw new IllegalArgumentException("chance must be finite");
        }
        if (Double.isNaN(chance)) {
            throw new IllegalArgumentException("chance must not be NaN");
        }
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("chance must be in the range [0, 1]");
        }
        return random -> random.nextDouble() <= chance;
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
    public static PoolGenerator<Byte> ofBytes(byte... values) {
        return ofBytes(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code byte} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values    the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code byte} elements of the given array
     */
    public static PoolGenerator<Byte> ofBytes(boolean useBundle, byte... values) {
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
     * @param values    the collection of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code byte} elements of the given list
     */
    public static PoolGenerator<Byte> ofBytes(boolean useBundle, Collection<Byte> values) {
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
    public static PoolGenerator<Character> ofChars(char... values) {
        return ofChars(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code char} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values    the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code char} elements of the given array
     */
    public static PoolGenerator<Character> ofChars(boolean useBundle, char... values) {
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
     * @param values    the collection of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code char} elements of the given list
     */
    public static PoolGenerator<Character> ofChars(boolean useBundle, Collection<Character> values) {
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
    public static PoolGenerator<Short> ofShorts(short... values) {
        return ofShorts(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code short} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values    the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code short} elements of the given array
     */
    public static PoolGenerator<Short> ofShorts(boolean useBundle, short... values) {
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
     * @param values    the collection of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code short} elements of the given list
     */
    public static PoolGenerator<Short> ofShorts(boolean useBundle, Collection<Short> values) {
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
    public static PoolGenerator<Integer> ofInts(int... values) {
        return ofInts(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code int} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values    the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code int} elements of the given array
     */
    public static PoolGenerator<Integer> ofInts(boolean useBundle, int... values) {
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
     * @param values    the collection of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code int} elements of the given list
     */
    public static PoolGenerator<Integer> ofInts(boolean useBundle, Collection<Integer> values) {
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
    public static PoolGenerator<Long> ofLongs(long... values) {
        return ofLongs(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code long} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values    the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code long} elements of the given array
     */
    public static PoolGenerator<Long> ofLongs(boolean useBundle, long... values) {
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
     * @param values    the collection of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code long} elements of the given list
     */
    public static PoolGenerator<Long> ofLongs(boolean useBundle, Collection<Long> values) {
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
    public static PoolGenerator<Float> ofFloats(float... values) {
        return ofFloats(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random {@code float} elements of the given array.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate elements in a bundle or not
     * @param values    the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code float} elements of the given array
     */
    public static PoolGenerator<Float> ofFloats(boolean useBundle, float... values) {
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
     * @param values    the collection of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code float} elements of the given list
     */
    public static PoolGenerator<Float> ofFloats(boolean useBundle, Collection<Float> values) {
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
    public static PoolGenerator<Double> ofDoubles(double... values) {
        return ofDoubles(false, values);
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
     * @param values    the array of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code double} elements of the given array
     */
    public static PoolGenerator<Double> ofDoubles(boolean useBundle, double... values) {
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
     * @param values    the collection of values to generate from
     * @return a {@link PoolGenerator} that generates random {@code double} elements of the given list
     */
    public static PoolGenerator<Double> ofDoubles(boolean useBundle, Collection<Double> values) {
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
     * @param length    the length of the strings to generate
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
    public static PoolGenerator<String> ofStrings(String... values) {
        return ofStrings(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random strings from the given values.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * strings without repeats until it reaches the end of the array. Then it will generate
     * all strings again.
     * </p>
     * @param useBundle whether to generate strings in a bundle or not
     * @param values    the array of values to generate from
     * @return a {@link PoolGenerator} that generates random strings from the given values
     */
    public static PoolGenerator<String> ofStrings(boolean useBundle, String... values) {
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
     * @param values    the collection of values to generate from
     * @return a {@link PoolGenerator} that generates random strings from the given values
     */
    public static PoolGenerator<String> ofStrings(boolean useBundle, Collection<String> values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link Generator} that generates random enum constants of the given enum class.
     * <p>
     * The generator will generate random enum constants of the given enum class.
     * </p>
     *
     * @param enumClass the enum class
     * @param <T>       the enum type
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
     * @param enumClass    the enum class
     * @param intGenerator the generator of indices
     * @param <T>          the enum type
     * @return a {@link Generator} that generates random enum constants of the given enum class
     */
    public static <T extends Enum<T>> Generator<T> ofEnum(Class<T> enumClass, Generator<Integer> intGenerator) {
        return random -> Generator.generateEnum(random, enumClass, intGenerator);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random enum constants of the given enum values.
     * @param values the enum values to generate from
     * @param <T>    the enum type
     * @return a {@link PoolGenerator} that generates random enum constants of the given enum values
     */
    @SafeVarargs
    public static <T extends Enum<T>> PoolGenerator<T> ofEnums(T... values) {
        return ofEnums(false, values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random enum constants of the given enum values.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * enum constants without repeats until it reaches the end of the array. Then it will generate
     * all enum constants again.
     * </p>
     * @param useBundle whether to generate enum constants in a bundle or not
     * @param values    the array of enum values to generate from
     * @param <T>       the enum type
     * @return a {@link PoolGenerator} that generates random enum constants of the given enum values
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> PoolGenerator<T> ofEnums(boolean useBundle, T... values) {
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
     * @param values    the collection of enum values to generate from
     * @param <T>       the enum type
     * @return a {@link PoolGenerator} that generates random enum constants of the given enum values
     */
    public static <T extends Enum<T>> PoolGenerator<T> ofEnums(boolean useBundle, Collection<T> values) {
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
     * @param generator    the generator of elements
     * @param length       the length of the arrays to generate
     * @param <T>          the element type
     * @param <A>          the array type
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
     * @param generator   the generator of elements
     * @param length      the length of the lists to generate
     * @param <T>         the element type
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
     * @param generator   the generator of elements
     * @param length      the maximum length of the sets to generate
     * @param <T>         the element type
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
     * @param keyType           the class of the keys of the maps to generate
     * @param valueType         the class of the values of the maps to generate
     * @param keyGenerator      the generator of keys
     * @param valueGenerator    the generator of values
     * @param duplicateResolver the function to resolve duplicate keys
     * @param length            the maximum length of the maps to generate
     * @param <K>               the key type
     * @param <V>               the value type
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
     * @param <T>   the class type
     * @return a {@link Generator} that generates random objects of the given class
     *
     * @see ObjectGenerator
     */
    public static <T> ObjectGenerator<T> of(Class<T> clazz) {
        return new ObjectGenerator<>(clazz);
    }

    /**
     * Constantly returns {@code true}.
     */
    public static final BooleanSupplier ALWAYS = () -> true;

    /**
     * Creates a {@link PoolItem} that generates random values of the given generator.
     * <p>
     * The returned {@link PoolItem} has a weight of 1 and a condition that always returns {@code true}.
     * </p>
     *
     * @param generator the generator to generate random values with
     * @param <T>       the generator type
     * @return a {@link PoolItem} that generates random values of the given generator
     */
    public static <T> PoolItem<T> item(Generator<T> generator) {
        return new PoolItemImpl<>(1L, generator, ALWAYS);
    }

    /**
     * Creates a {@link PoolItem} that generates random values of the given generator.
     * <p>
     * The returned {@link PoolItem} has a weight of the given weight and a condition that always returns {@code true}.
     * </p>
     *
     * @param generator the generator to generate random values with
     * @param weight    the weight of this item in the pool, must be in range {@code [0; 0xffffffffL]}
     * @param <T>       the generator type
     * @return a {@link PoolItem} that generates random values of the given generator
     */
    public static <T> PoolItem<T> item(Generator<T> generator, long weight) {
        return new PoolItemImpl<>(weight, generator, ALWAYS);
    }

    /**
     * Creates a {@link PoolItem} that generates random values of the given generator.
     * <p>
     * The returned {@link PoolItem} has a weight of 1 and a condition of the given condition.
     * </p>
     *
     * @param generator the generator to generate random values with
     * @param condition the condition that must be satisfied in order for this item to be used
     * @param <T>       the generator type
     * @return a {@link PoolItem} that generates random values of the given generator
     */
    public static <T> PoolItem<T> item(Generator<T> generator, BooleanSupplier condition) {
        return new PoolItemImpl<>(1L, generator, condition);
    }

    /**
     * Creates a {@link PoolItem} that generates random values of the given generator.
     * <p>
     * The returned {@link PoolItem} has a weight of the given weight and a condition of the given condition.
     * </p>
     *
     * @param generator the generator to generate random values with
     * @param weight    the weight of this item in the pool, must be in range {@code [0; 0xffffffffL]}
     * @param condition the condition that must be satisfied in order for this item to be used
     * @param <T>       the generator type
     * @return a {@link PoolItem} that generates random values of the given generator
     */
    public static <T> PoolItem<T> item(Generator<T> generator, long weight, BooleanSupplier condition) {
        return new PoolItemImpl<>(weight, generator, condition);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random elements from the given values.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the array. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate strings in a bundle or not
     * @param values    the array of values to generate from
     * @param <T>       the generator type
     * @return a {@link PoolGenerator} that generates random strings from the given values
     */
    @SafeVarargs
    public static <T> PoolGenerator<T> ofPool(boolean useBundle, T... values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random elements from the given values.
     * <p>
     * If parameter {@code useBundle} is {@code true}, then created generator will generate
     * elements without repeats until it reaches the end of the list. Then it will generate
     * all elements again.
     * </p>
     * @param useBundle whether to generate strings in a bundle or not
     * @param values    the collection of values to generate from
     * @param <T>       the generator type
     * @return a {@link PoolGenerator} that generates random strings from the given values
     */
    public static <T> PoolGenerator<T> ofPool(boolean useBundle, Collection<T> values) {
        return new DefaultPoolGenerator<>(values, useBundle);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random values from the given values.
     * <p>
     * The returned {@link PoolGenerator} will generate random values from the given ones in strict order.
     * </p>
     *
     * @param values the values to generate random values from
     * @param <T>    the generator type
     * @return a {@link PoolGenerator} that generates random values from the given values
     */
    @SafeVarargs
    public static <T> PoolGenerator<T> ofOrderedPool(T... values) {
        return new OrderedPoolGenerator<>(values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random values from the given values.
     * <p>
     * The returned {@link PoolGenerator} will generate random values from the given ones in strict order.
     * </p>
     *
     * @param values the values to generate random values from
     * @param <T>    the generator type
     * @return a {@link PoolGenerator} that generates random values from the given values
     */
    public static <T> PoolGenerator<T> ofOrderedPool(Collection<T> values) {
        return new OrderedPoolGenerator<>(values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random values from the given values.
     * <p>
     * The returned {@link PoolGenerator} will generate random values from the given ones in a weighted manner.
     * The weight of each value is taken from the {@link PoolItem} it is wrapped in. This pool returns 1 on 0
     * values at a time.
     * </p>
     *
     * @param values the values to generate random values from
     * @param <T>    the generator type
     * @return a {@link PoolGenerator} that generates random values from the given values
     */
    @SafeVarargs
    public static <T> PoolGenerator<Optional<T>> ofWeightedPool(Generator<T>... values) {
        return new WeightedPoolGenerator<>(values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random values from the given values.
     * <p>
     * The returned {@link PoolGenerator} will generate random values from the given ones in a weighted manner.
     * The weight of each value is taken from the {@link PoolItem} it is wrapped in. This pool returns 1 on 0
     * values at a time.
     * </p>
     *
     * @param values the values to generate random values from
     * @param <T>    the generator type
     * @return a {@link PoolGenerator} that generates random values from the given values
     */
    public static <T> PoolGenerator<Optional<T>> ofWeightedPool(Collection<Generator<T>> values) {
        return new WeightedPoolGenerator<>(values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random values from the given values.
     * <p>
     * The returned {@link PoolGenerator} will generate random values from the given values in a weighted manner.
     * The weight of each value is taken from the {@link PoolItem} it is wrapped in. This pool returns a list of all
     * values that were generated. If values contains collections then their elements will be extracted.
     * </p>
     *
     * @param values the values to generate random values from
     * @return a {@link PoolGenerator} that generates random values from the given values
     */
    @SuppressWarnings("unchecked")
    public static PoolGenerator<List<Object>> ofMultiplePool(Generator<?>... values) {
        return new MultiplePoolGenerator((Generator<Object>[]) values);
    }

    /**
     * Creates a {@link PoolGenerator} that generates random values from the given values.
     * <p>
     * The returned {@link PoolGenerator} will generate random values from the given values in a weighted manner.
     * The weight of each value is taken from the {@link PoolItem} it is wrapped in. This pool returns a list of all
     * values that were generated. If values contains collections then their elements will be extracted.
     * </p>
     *
     * @param values the values to generate random values from
     * @return a {@link PoolGenerator} that generates random values from the given values
     */
    public static PoolGenerator<List<Object>> ofMultiplePool(Collection<Generator<?>> values) {
        return new MultiplePoolGenerator(values);
    }

    /**
     * Creates a generator that returns a constant value.
     * <p>
     * The given value is returned every time the generated value is requested.
     * </p>
     *
     * @param value the value to generate
     * @param <E>   the type of the value
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

    private static Class<?> commonSuperClass(Class<?> a, Class<?> b) {
        if (a == null) return b;
        while (!a.isAssignableFrom(b)) {
            a = a.getSuperclass();
        }
        return a;
    }

    private static Class<?> getCollectionElementType(Collection<?> collection) {
        Objects.requireNonNull(collection);
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("collection must have at least 1 element");
        }
        Class<?> elementType = null;
        for (Object element : collection) {
            if (element != null) {
                elementType = commonSuperClass(elementType, element.getClass());
            }
        }
        if (elementType == null) {
            throw new IllegalArgumentException("collection must contain at least one non-null element");
        }
        return elementType;
    }

    static class DefaultPoolGenerator<T> implements PoolGenerator<T> {
        private final int length;
        private final Object pool;
        private int bundleIndex;

        public DefaultPoolGenerator(Collection<T> values, boolean useBundle) {
            Objects.requireNonNull(values);
            length = values.size();
            if (length < 2) {
                throw new IllegalArgumentException("pool must have at least 2 elements");
            }
            Class<?> elementType = getCollectionElementType(values);
            this.pool = Array.newInstance(elementType, values.size());
            var itr = values.iterator();
            for (int i = 0; itr.hasNext(); i++) {
                Array.set(pool, i, itr.next());
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

        @Override
        public int size() {
            return length;
        }

        @Override
        public PoolItem<?> get(int index) {
            Objects.checkIndex(index, length);
            return item(ofValue(Array.get(pool, index)));
        }

        @Override
        public Iterator<PoolItem<?>> iterator() {
            return new Iterator<>() {
                int current;

                @Override
                public boolean hasNext() {
                    return current < length;
                }

                @Override
                public PoolItem<?> next() {
                    return get(current++);
                }
            };
        }
    }

    static class OrderedPoolGenerator<T> implements PoolGenerator<T> {
        private final int length;
        private final Object pool;
        private int current;

        public OrderedPoolGenerator(Collection<T> values) {
            Objects.requireNonNull(values);
            length = values.size();
            Class<?> elementType = getCollectionElementType(values);
            this.pool = Array.newInstance(elementType, length);
            var itr = values.iterator();
            for (int i = 0; itr.hasNext(); i++) {
                Array.set(pool, i, itr.next());
            }
        }

        @SuppressWarnings("SuspiciousSystemArraycopy")
        public OrderedPoolGenerator(Object pool) {
            Objects.requireNonNull(pool, "values");
            length = Array.getLength(pool);
            if (length == 0) {
                throw new IllegalArgumentException("pool must have at least 1 element");
            }
            this.pool = Array.newInstance(pool.getClass().getComponentType(), length);
            System.arraycopy(pool, 0, this.pool, 0, length);
        }

        @Override
        public int size() {
            return length;
        }

        @Override
        public PoolItem<?> get(int index) {
            return item(ofValue(Array.get(pool, index)));
        }

        @SuppressWarnings("unchecked")
        @Override
        public T generate(Random random) {
            Object next = Array.get(pool, current);
            current = ++current % length;
            return (T) next;
        }

        @Override
        public Iterator<PoolItem<?>> iterator() {
            return new Iterator<>() {
                int current;

                @Override
                public boolean hasNext() {
                    return current < length;
                }

                @Override
                public PoolItem<?> next() {
                    return get(current++);
                }
            };
        }
    }

    static class PoolItemImpl<T> implements PoolItem<T> {
        private static final long MAX_WEIGHT = 0xffffffffL;

        private final long weight;
        private final Generator<T> generator;
        private final BooleanSupplier condition;

        public PoolItemImpl(long weight, Generator<T> generator, BooleanSupplier condition) {
            if (weight < 0) {
                throw new IllegalArgumentException("weight must not be negative");
            }
            if (weight > MAX_WEIGHT) {
                throw new IllegalArgumentException("weight must not be greater than " + MAX_WEIGHT);
            }
            this.weight = weight;
            this.generator = Objects.requireNonNull(generator);
            this.condition = Objects.requireNonNull(condition);
        }

        @Override
        public long weight() {
            return weight;
        }

        @Override
        public boolean test() {
            return condition.getAsBoolean();
        }

        @Override
        public T generate(Random random) {
            return generator.generate(random);
        }
    }

    static abstract class AbstractPoolGenerator<T, R> implements PoolGenerator<R> {
        protected final List<PoolItem<T>> pool;

        protected AbstractPoolGenerator(Generator<T>[] values) {
            Objects.requireNonNull(values);
            for (int i = 0; i < values.length; i++) {
                Generator<T> item = values[i];
                Objects.requireNonNull(item, "item at index " + i);
            }
            pool = Arrays.stream(values).map(g -> g instanceof PoolItem<T> i ? i : item(g)).toList();
        }

        protected AbstractPoolGenerator(Collection<Generator<T>> values) {
            Objects.requireNonNull(values);
            int index = 0;
            for (Generator<T> item : values) {
                Objects.requireNonNull(item, "item at index " + index);
                index++;
            }
            pool = values.stream().map(g -> g instanceof PoolItem<T> i ? i : item(g)).toList();
        }

        @Override
        public Iterator<PoolItem<?>> iterator() {
            return new Iterator<>() {
                int current;

                @Override
                public boolean hasNext() {
                    return current < pool.size();
                }

                @Override
                public PoolItem<?> next() {
                    return pool.get(current++);
                }
            };
        }

        @Override
        public int size() {
            return pool.size();
        }

        @Override
        public PoolItem<T> get(int index) {
            return pool.get(index);
        }
    }

    static class WeightedPoolGenerator<T> extends AbstractPoolGenerator<T, Optional<T>> {
        public WeightedPoolGenerator(Generator<T>[] values) {
            super(values);
        }
        
        public WeightedPoolGenerator(Collection<Generator<T>> values) {
            super(values);
        }

        @Override
        public Optional<T> generate(Random random) {
            AtomicLong totalWeight = new AtomicLong();
            List<PoolItem<T>> items = new ArrayList<>(pool.size());
            pool.parallelStream().filter(PoolItem::test).forEachOrdered(item -> {
                        totalWeight.addAndGet(item.weight());
                        items.add(item);
                    });
            AtomicLong next = new AtomicLong(random.nextLong(totalWeight.get()));
            return items.stream().dropWhile(item -> {
                long difference = next.get() - item.weight();
                long cmp = item.weight() - next.getAndSet(difference);
                return cmp > 0;
            }).findFirst().map(item -> item.generate(random));
        }
    }

    static class MultiplePoolGenerator extends AbstractPoolGenerator<Object, List<Object>> {
        public MultiplePoolGenerator(Generator<Object>[] values) {
            super(values);
        }

        public MultiplePoolGenerator(Collection<Generator<?>> values) {
            super(cast(values));
        }

        @Override
        public List<Object> generate(Random random) {
            List<Object> list = new ArrayList<>(pool.size());
            for (PoolItem<?> item : pool) {
                if (item.test()) {
                    Object next = item.generate(random);
                    if (next instanceof Collection<?> c) {
                        list.addAll(c);
                    } else {
                        list.add(next);
                    }
                }
            }
            return list;
        }

        @SuppressWarnings("unchecked")
        private static Collection<Generator<Object>> cast(Collection<Generator<?>> values) {
            ArrayList<Generator<Object>> list = new ArrayList<>(values.size());
            for (Generator<?> item : values) {
                list.add((Generator<Object>) item);
            }
            return list;
        }
    }
}
