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

/**
 * Base class for all builders.
 * @param <T> the type of the building object
 */
public abstract class AbstractBuilder<T> implements Cloneable {
    /**
     * Built object instance.
     */
    private T instance;

    /**
     * Protected constructor that is called only by subclasses.
     */
    protected AbstractBuilder() {}

    /**
     * Method checks whether the instance was already built and if so throws an exception.
     * This method should be used in subclasses to check whether the instance was already built.
     */
    protected void checkInstance() {
        if (instance != null) {
            throw new IllegalStateException("Instance already built");
        }
    }

    /**
     * Method returns new building object instance. This method is called only by
     * {@link #build()} method and only if the instance was not built yet.
     * @return building object instance
     */
    protected abstract T build0();

    /**
     * Method returns new building object instance if it was not built yet. If
     * this method is called multiple times, the same instance will be returned.
     * @return building object instance
     */
    public T build() {
        if (instance != null) {
            return instance;
        }
        return instance = build0();
    }

    /**
     * Method creates a copy of this builder that can be modified again. The copy will be
     * modifiable evan if {@link #build()} method was already called.
     * @return copy of this builder
     */
    @SuppressWarnings("unchecked")
    @Override
    public AbstractBuilder<T> clone() {
        AbstractBuilder<T> clone;
        try {
            clone = (AbstractBuilder<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            //should never happen
            throw new RuntimeException(e);
        }
        //reset instance to reuse clone object
        clone.instance = null;
        return clone;
    }
}
