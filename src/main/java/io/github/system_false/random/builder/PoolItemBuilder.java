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
import io.github.system_false.random.Generators;
import io.github.system_false.random.PoolItem;

import java.util.Objects;
import java.util.function.*;
import java.util.random.RandomGenerator;

/**
 * Builder class for {@link PoolItem} objects.
 * <p>
 * This class has configuration methods named as {@code value}, {@code weight}, {@code condition},
 * {@code pick} and {@code ignore}.
 * </p>
 * @param <T> the type of the generated values
 */
public class PoolItemBuilder<T> extends AbstractBuilder<PoolItem<T>> {
    /**
     * Maximum value of item weight;
     */
    public static final long MAX_WEIGHT = 0xffffffffL;

    /**
     * Default value of item weight.
     */
    public static final LongBinaryOperator DEFAULT_WEIGHT = (pick, ignore) -> 1L;
    /**
     * Default value of item condition.
     */
    public static final BiPredicate<Long, Long> DEFAULT_CONDITION = (pick, ignore) -> true;
    /**
     * Default value of item pick combiner.
     */
    public static final LongUnaryOperator DEFAULT_PICK_COMBINER = LongUnaryOperator.identity();
    /**
     * Default value of item ignore combiner.
     */
    public static final LongUnaryOperator DEFAULT_IGNORE_COMBINER = LongUnaryOperator.identity();

    private BiFunction<Long, Long, Generator<T>> value;
    private LongBinaryOperator weight;
    private BiPredicate<Long, Long> condition;
    private LongUnaryOperator pickCombiner;
    private LongUnaryOperator ignoreCombiner;
    private ContextualImpl<T> contextual;

    /**
     * Public constructor that creates new {@code PoolItemBuilder} object.
     */
    public PoolItemBuilder() {
        weight = DEFAULT_WEIGHT;
        condition = DEFAULT_CONDITION;
        pickCombiner = DEFAULT_PICK_COMBINER;
        ignoreCombiner = DEFAULT_IGNORE_COMBINER;
    }

    /**
     * Method sets constant value of pool item.
     * <p>
     * This method changes value set by other {@code value(...)} methods.
     * </p>
     * @param value item value
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     */
    public PoolItemBuilder<T> value(T value) {
        checkInstance();
        Generator<T> generator = Generators.ofValue(value);
        this.value = (pick, ignore) -> generator;
        return this;
    }

    /**
     * Method sets supplier as value of pool item.
     * <p>
     * This method changes value set by other {@code value(...)} methods.
     * </p>
     * @param supplier item value supplier
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     */
    public PoolItemBuilder<T> value(Supplier<T> supplier) {
        checkInstance();
        Objects.requireNonNull(supplier, "supplier");
        Generator<T> generator = rg -> supplier.get();
        this.value = (pick, ignore) -> generator;
        return this;
    }

    /**
     * Method sets generator as value of pool item.
     * <p>
     * This method changes value set by other {@code value(...)} methods.
     * </p>
     * @param generator item value generator
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     */
    public PoolItemBuilder<T> value(Generator<T> generator) {
        checkInstance();
        Objects.requireNonNull(generator, "generator");
        this.value = (pick, ignore) -> generator;
        return this;
    }

    /**
     * Method sets fully configured item value as function that takes 2 {@code long} arguments
     * and returns {@code Generator} object.
     * <p>
     * First parameter in {@code value} is number of times this item was picked.
     * Second parameter in {@code value} is number of times this item was ignored.
     * </p>
     * <p>
     * This method changes value set by other {@code value(...)} methods.
     * </p>
     * @param value item value value
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     */
    public PoolItemBuilder<T> value(BiFunction<Long, Long, Generator<T>> value) {
        checkInstance();
        Objects.requireNonNull(value, "value");
        this.value = value;
        return this;
    }

    /**
     * Method sets weight of pool item.
     * <p>
     * This method changes value set by other {@code weight(...)} methods.
     * </p>
     * @param weight item weight
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     * @throws IllegalArgumentException if weight is negative or more that {@value MAX_WEIGHT}
     */
    public PoolItemBuilder<T> weight(long weight) {
        checkInstance();
        if (weight < 0) {
            throw new IllegalArgumentException("weight must not be negative");
        }
        if (weight > MAX_WEIGHT) {
            throw new IllegalArgumentException("weight must not be greater than " + MAX_WEIGHT);
        }
        this.weight = (pick, ignore) -> weight;
        return this;
    }

    /**
     * Method sets supplier that returns weight of pool item.
     * <p>
     * This method changes value set by other {@code weight(...)} methods.
     * </p>
     * @param supplier item weight supplier
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     */
    public PoolItemBuilder<T> weight(LongSupplier supplier) {
        checkInstance();
        Objects.requireNonNull(supplier, "supplier");
        this.weight = (pick, ignore) -> supplier.getAsLong();
        return this;
    }

    /**
     * Method sets fully configured item weight as function that takes 2 {@code long} arguments
     * and returns {@code long} value.
     * <p>
     * First parameter in {@code weight} is number of times this item was picked.
     * Second parameter in {@code weight} is number of times this item was ignored.
     * </p>
     * <p>
     * This method changes value set by other {@code weight(...)} methods.
     * </p>
     * @param weight item weight
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     */
    public PoolItemBuilder<T> weight(LongBinaryOperator weight) {
        checkInstance();
        Objects.requireNonNull(weight, "weight");
        this.weight = weight;
        return this;
    }

    /**
     * Method sets condition for pool item. This is called by pools to check whether this item
     * should be used.
     * <p>
     * This method changes value set by other {@code condition(...)} methods.
     * </p>
     * @param supplier item condition
     * @return this builder
     */
    public PoolItemBuilder<T> condition(BooleanSupplier supplier) {
        checkInstance();
        Objects.requireNonNull(supplier, "supplier");
        this.condition = (pick, ignore) -> supplier.getAsBoolean();
        return this;
    }

    /**
     * Method sets fully configured item condition.
     * <p>
     * First parameter in {@code condition} is number of times this item was picked.
     * Second parameter in {@code condition} is number of times this item was ignored.
     * </p>
     * <p>
     * This method changes value set by other {@code condition(...)} methods.
     * </p>
     * @param condition item condition
     * @return this builder
     */
    public PoolItemBuilder<T> condition(BiPredicate<Long, Long> condition) {
        checkInstance();
        Objects.requireNonNull(condition, "condition");
        this.condition = condition;
        return this;
    }

    /**
     * Method sets action that will be executed when this item is picked.
     * <p>
     * This method changes value set by other {@code pick(...)} methods.
     * </p>
     * @param action action to execute
     * @return this builder
     */
    public PoolItemBuilder<T> pick(Runnable action) {
        checkInstance();
        Objects.requireNonNull(action, "action");
        this.pickCombiner = p -> {
            action.run();
            return p;
        };
        return this;
    }

    /**
     * Method sets consumer that will be executed when this item is picked.
     * <p>
     * {@code consumer} will receive number of times this item was picked.
     * </p>
     * <p>
     * This method changes value set by other {@code pick(...)} methods.
     * </p>
     * @param consumer consumer
     * @return this builder
     */
    public PoolItemBuilder<T> pick(LongConsumer consumer) {
        checkInstance();
        Objects.requireNonNull(consumer, "consumer");
        this.pickCombiner = p -> {
            consumer.accept(p);
            return p;
        };
        return this;
    }

    /**
     * Method sets combiner that takes current number of times this item was picked
     * and returns new number of times this item was picked.
     * <p>
     * This method changes value set by other {@code pick(...)} methods.
     * </p>
     * @param combiner combiner
     * @return this builder
     */
    public PoolItemBuilder<T> pick(LongUnaryOperator combiner) {
        checkInstance();
        Objects.requireNonNull(combiner, "combiner");
        this.pickCombiner = combiner;
        return this;
    }

    /**
     * Method sets action that will be executed when this item is ignored.
     * <p>
     * This method changes value set by other {@code ignore(...)} methods.
     * </p>
     * @param action action to execute
     * @return this builder
     */
    public PoolItemBuilder<T> ignore(Runnable action) {
        checkInstance();
        Objects.requireNonNull(action, "action");
        this.ignoreCombiner = p -> {
            action.run();
            return p;
        };
        return this;
    }

    /**
     * Method sets consumer that will be executed when this item is ignored.
     * <p>
     * {@code consumer} will receive number of times this item was ignored.
     * </p>
     * <p>
     * This method changes value set by other {@code ignore(...)} methods.
     * </p>
     * @param consumer consumer
     * @return this builder
     */
    public PoolItemBuilder<T> ignore(LongConsumer consumer) {
        checkInstance();
        Objects.requireNonNull(consumer, "consumer");
        this.ignoreCombiner = p -> {
            consumer.accept(p);
            return p;
        };
        return this;
    }

    /**
     * Method sets combiner that takes current number of times this item was ignored
     * and returns new number of times this item was ignored.
     * <p>
     * This method changes value set by other {@code ignore(...)} methods.
     * </p>
     * @param combiner combiner
     * @return this builder
     */
    public PoolItemBuilder<T> ignore(LongUnaryOperator combiner) {
        checkInstance();
        Objects.requireNonNull(combiner, "combiner");
        this.ignoreCombiner = combiner;
        return this;
    }

    /**
     * Method takes {@code BiConsumer} that continues configuration of this builder.
     * <p>
     * First argument is this builder, second one is contextual object that is linked to building object.
     * Invoking methods {@code context()} and {@code withContext(...)} on this contextual object in given
     * action will not have any effect. Contextual object can be used only in lambdas expressions.
     * </p>
     * @param action configuration action
     * @return this builder
     * @throws NullPointerException if {@code action} is {@code null}
     */
    public PoolItemBuilder<T> withContext(BiConsumer<PoolItemBuilder<T>, Contextual<Generator<T>>> action) {
        checkInstance();
        Objects.requireNonNull(action, "action");
        if (contextual == null) {
            contextual = new ContextualImpl<>();
        }
        action.accept(this, contextual);
        return this;
    }

    @Override
    protected PoolItem<T> build0() {
        if (value == null) {
            value = (pick, ignore) -> null;
        }
        PoolItemImpl<T> item = new PoolItemImpl<>(value, weight, condition, pickCombiner, ignoreCombiner);
        if (contextual != null) {
            contextual.setBase(item);
        }
        return item;
    }

    @Override
    public PoolItemBuilder<T> clone() {
        return (PoolItemBuilder<T>) super.clone();
    }
}

class PoolItemImpl<G> implements PoolItem<G> {
    private final BiFunction<Long, Long, Generator<G>> generator;
    private final LongBinaryOperator weight;
    private final BiPredicate<Long, Long> condition;
    private final LongUnaryOperator pickCombiner;
    private final LongUnaryOperator ignoreCombiner;

    private long picked, ignored;

    public PoolItemImpl(BiFunction<Long, Long, Generator<G>> generator, LongBinaryOperator weight,
                        BiPredicate<Long, Long> condition, LongUnaryOperator pickCombiner,
                        LongUnaryOperator ignoreCombiner) {
        this.generator = generator;
        this.weight = weight;
        this.condition = condition;
        this.pickCombiner = pickCombiner;
        this.ignoreCombiner = ignoreCombiner;
    }

    @Override
    public long weight() {
        return weight.applyAsLong(picked, ignored);
    }

    @Override
    public boolean test() {
        return condition.test(picked, ignored);
    }

    @Override
    public G generate(RandomGenerator random) {
        return generator.apply(picked, ignored).generate(random, this);
    }

    @Override
    public void picked() {
        picked = pickCombiner.applyAsLong(picked + 1);
    }

    @Override
    public void ignored() {
        ignored = ignoreCombiner.applyAsLong(ignored + 1);
    }
}
