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
import io.github.system_false.random.PoolGenerator;
import io.github.system_false.random.PoolItem;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

/**
 * Builder class for {@link PoolGenerator} objects.
 * <p>
 * This class has configuration methods named as {@code add} taking arrays of values, suppliers,
 * generators or {@link PoolItem} objects.
 * </p>
 * <p>
 * This class also has methods for removing and clearing items. They can be used for modifying
 * cloned builders.
 * </p>
 * @param <T> the type of the items
 * @param <R> the type of the generated value
 */
public class PoolBuilder<T, R> extends AbstractBuilder<PoolGenerator<R>> {
    private final Function<List<PoolItem<T>>, PoolGenerator<R>> poolBuilder;
    /**
     * Pool containing items.
     */
    protected ArrayList<PoolItem<T>> items;

    /**
     * Public constructor that creates new instance of {@link PoolBuilder}. It takes a function
     * that takes a list of {@link PoolItem} objects and returns a {@link PoolGenerator} object.
     * @param poolBuilder a function that takes a list of {@link PoolItem} objects and returns
     *                    a {@link PoolGenerator} object
     * @throws NullPointerException if {@code poolBuilder} is {@code null}
     */
    public PoolBuilder(Function<List<PoolItem<T>>, PoolGenerator<R>> poolBuilder) {
        this.poolBuilder = Objects.requireNonNull(poolBuilder);
        items = new ArrayList<>();
    }

    /**
     * Method adds the given values to the pool. All values will have weight 1, and the condition
     * that always returns {@code true}.
     * @param values the values to add
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     * @throws NullPointerException if {@code values} are {@code null}
     */
    @SafeVarargs
    public final PoolBuilder<T, R> add(T... values) {
        checkInstance();
        Objects.requireNonNull(values, "values");
        for (T value : values) {
            items.add(PoolItem.item(rg -> value));
        }
        return this;
    }

    /**
     * Method adds the given suppliers to the pool. All suppliers will have weight 1, and the
     * condition that always returns {@code true}.
     * @param suppliers the suppliers to add
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     * @throws NullPointerException if {@code suppliers} or one of them are {@code null}
     */
    @SafeVarargs
    public final PoolBuilder<T, R> add(Supplier<T>... suppliers) {
        checkInstance();
        Objects.requireNonNull(suppliers, "suppliers");
        for (int i = 0; i < suppliers.length; i++) {
            Supplier<T> supplier = suppliers[i];
            Objects.requireNonNull(supplier, "supplier at index " + i);
            items.add(PoolItem.item(rg -> supplier.get()));
        }
        return this;
    }

    /**
     * Method adds the given generators to the pool. All generators will have weight 1, and the
     * condition that always returns {@code true}.
     * @param generators the generators to add
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     * @throws NullPointerException if {@code generators} or one of them are {@code null}
     */
    @SafeVarargs
    public final PoolBuilder<T, R> add(Generator<T>... generators) {
        checkInstance();
        Objects.requireNonNull(generators, "generators");
        for (int i = 0; i < generators.length; i++) {
            Generator<T> generator = generators[i];
            Objects.requireNonNull(generator, "generator at index " + i);
            items.add(PoolItem.item(generator));
        }
        return this;
    }

    /**
     * Method adds the given items to the pool. All items will have weight 1, and the condition
     * that always returns {@code true}.
     * @param items the items to add
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     * @throws NullPointerException if {@code items} or one of them are {@code null}
     */
    @SafeVarargs
    public final PoolBuilder<T, R> add(PoolItem<T>... items) {
        checkInstance();
        Objects.requireNonNull(items, "items");
        for (int i = 0; i < items.length; i++) {
            PoolItem<T> item = items[i];
            Objects.requireNonNull(item, "item at index " + i);
            this.items.add(item);
        }
        return this;
    }

    /**
     * Method invokes consumer with new builder and adds built item to the pool.
     * @param itemBuilder the builder consumer
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     * @throws NullPointerException if {@code itemBuilder} is {@code null}
     */
    public PoolBuilder<T, R> add(Consumer<PoolItemBuilder<T>> itemBuilder) {
        checkInstance();
        Objects.requireNonNull(itemBuilder, "itemBuilder");
        PoolItemBuilder<T> builder = new PoolItemBuilder<>();
        itemBuilder.accept(builder);
        items.add(builder.build());
        return this;
    }

    /**
     * Method removes item at given index from the pool. If {@code index} is incorrect,
     * an exception will be thrown.
     * @param index the index of the item to remove
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     * @throws IndexOutOfBoundsException if the index is out of range ({@code index < 0 || index >= size()})
     */
    public PoolBuilder<T, R> remove(int index) {
        checkInstance();
        items.remove(index);
        return this;
    }

    /**
     * Method removes the given item from the pool.
     * @param item the item to remove
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     */
    public PoolBuilder<T, R> remove(PoolItem<?> item) {
        checkInstance();
        items.remove(item);
        return this;
    }

    /**
     * Method removes all items that match the given predicate from the pool.
     * @param item the predicate
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     */
    public PoolBuilder<T, R> remove(Predicate<PoolItem<?>> item) {
        checkInstance();
        items.removeIf(item);
        return this;
    }

    /**
     * Method removes all items from the pool.
     * @return this builder
     * @throws IllegalStateException if {@link #build()} method was already called
     */
    public PoolBuilder<T, R> clear() {
        checkInstance();
        items.clear();
        return this;
    }

    @Override
    protected PoolGenerator<R> build0() {
        return poolBuilder.apply(List.copyOf(items));
    }

    @Override
    public PoolBuilder<T, R> clone() {
        PoolBuilder<T, R> clone = (PoolBuilder<T, R>) super.clone();
        clone.items = new ArrayList<>(items);
        return (PoolBuilder<T, R>) super.clone();
    }

    /**
     * Method creates new builder for bundle pool generator object.
     * @return new builder
     * @param <T> the type of the building object
     */
    public static <T> PoolBuilder<T, T> bundled() {
        return new PoolBuilder<>(BundlePoolGenerator::new);
    }

    /**
     * Method creates new builder for bundle pool generator object.
     * @param useBundle whether pool must use bundle or not
     * @return new builder
     * @param <T> the type of the building object
     */
    public static <T> PoolBuilder<T, T> bundled(boolean useBundle) {
        return new PoolBuilder<>(items -> new BundlePoolGenerator<>(items, useBundle));
    }

    /**
     * Method creates new builder for ordered pool generator object.
     * @return new builder
     * @param <T> the type of the building object
     */
    public static <T> PoolBuilder<T, T> ordered() {
        return new PoolBuilder<>(OrderedPoolGenerator::new);
    }

    /**
     * Method creates new builder for weighted pool generator object.
     * @return new builder
     * @param <T> the type of the building object
     */
    public static <T> PoolBuilder<T, Optional<T>> weighted() {
        return new PoolBuilder<>(WeightedPoolGenerator::new);
    }

    /**
     * Method creates new builder for multiple pool generator object.
     * @return new builder
     */
    public static PoolBuilder<Object, List<Object>> multiple() {
        return new PoolBuilder<>(MultiplePoolGenerator::new);
    }
}

abstract class AbstractPoolGenerator<T, R> implements PoolGenerator<R> {
    protected final List<PoolItem<T>> items;

    protected AbstractPoolGenerator(List<PoolItem<T>> items) {
        this.items = items;
    }

    @Override
    public Iterator<PoolItem<?>> iterator() {
        return new Iterator<>() {
            int current;

            @Override
            public boolean hasNext() {
                return current < items.size();
            }

            @Override
            public PoolItem<?> next() {
                return items.get(current++);
            }
        };
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public PoolItem<T> get(int index) {
        return items.get(index);
    }

    /**
     * Method invokes {@link PoolItem#picked()} and {@link PoolItem#ignored()} methods for all items.
     * @param selected the selected item
     */
    protected void notifyItems(PoolItem<?> selected) {
        items.parallelStream().forEach(item -> {
            if (item == selected) {
                item.picked();
            } else {
                item.ignored();
            }
        });
    }
}

class BundlePoolGenerator<T> extends AbstractPoolGenerator<T, T> {
    private int bundleIndex;

    public BundlePoolGenerator(List<PoolItem<T>> items) {
        this(items, false);
    }

    public BundlePoolGenerator(List<PoolItem<T>> items, boolean useBundle) {
        super(new ArrayList<>(items));
        if (items.size() <= 1) {
            throw new IllegalArgumentException("At least 2 items are required to use bundle.");
        }
        bundleIndex = useBundle ? 0 : -1;
    }

    @Override
    public T generate(RandomGenerator random) {
        if (bundleIndex == -1) {
            PoolItem<T> nextItem = items.get(random.nextInt(items.size()));
            notifyItems(nextItem);
            return nextItem.generate(random);
        }
        int selected = random.nextInt(items.size() - bundleIndex) + bundleIndex;
        PoolItem<T> next = items.get(selected);
        PoolItem<T> current = items.get(bundleIndex);
        items.set(bundleIndex, next);
        items.set(selected, current);
        bundleIndex = (bundleIndex + 1) % items.size();
        notifyItems(next);
        return next.generate(random);
    }
}

class OrderedPoolGenerator<T> extends AbstractPoolGenerator<T, T> {
    private int current;

    public OrderedPoolGenerator(List<PoolItem<T>> items) {
        super(items);
    }

    @Override
    public T generate(RandomGenerator random) {
        PoolItem<T> next = items.get(current);
        current = ++current % items.size();
        notifyItems(next);
        return next.generate(random);
    }
}

class WeightedPoolGenerator<T> extends AbstractPoolGenerator<T, Optional<T>> {
    public WeightedPoolGenerator(List<PoolItem<T>> values) {
        super(values);
    }

    @Override
    public Optional<T> generate(RandomGenerator random) {
        AtomicLong totalWeight = new AtomicLong();
        List<PoolItem<T>> items = new ArrayList<>(this.items.size());
        this.items.parallelStream().filter(PoolItem::test).forEachOrdered(item -> {
            totalWeight.addAndGet(item.weight());
            items.add(item);
        });
        AtomicLong next = new AtomicLong(random.nextLong(totalWeight.get()));
        var select = items.stream().dropWhile(item -> {
            long difference = next.get() - item.weight();
            long cmp = item.weight() - next.getAndSet(difference);
            return cmp <= 0;
        }).findFirst();
        select.ifPresent(this::notifyItems);
        return select.map(item -> item.generate(random));
    }
}

class MultiplePoolGenerator extends AbstractPoolGenerator<Object, List<Object>> {
    public MultiplePoolGenerator(List<PoolItem<Object>> values) {
        super(values);
    }

    @Override
    public List<Object> generate(RandomGenerator random) {
        ArrayList<Object> list = new ArrayList<>(items.size());
        items.forEach(item -> {
            if (item.test()) {
                Object next = item.generate(random);
                if (next instanceof Collection<?> c) {
                    list.addAll(c);
                } else {
                    list.add(next);
                }
                item.picked();
            } else {
                item.ignored();
            }
        });
        return list;
    }
}
