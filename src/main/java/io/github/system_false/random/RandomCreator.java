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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation configures randomization of constructors and methods. It can be used
 * only to constructors and static methods returning same Class as Class they are declared in
 * or its children. Example:
 * <pre>
 * {@code
 * public class MyClass {
 *     private final int field1;
 *     private final String field2;
 *     private final Map<String, String> field3;
 *
 *     private MyClass(int field1, String field2, Map<String, String> field3) {
 *         this.field1 = field1;
 *         this.field2 = field2;
 *         this.field3 = field3;
 *     }
 *
 *     @RandomCreator
 *     public static MyClass newInstance(@RandomValue(intMinValue = 0) int field1, String field2, Map<String, String> field3) {
 *         if (field1 < 0) {
 *             throw new IllegalArgumentException("field1 must be greater than 0");
 *         }
 *         Objects.requireNonNull(field2, "field2");
 *         Objects.requireNonNull(field3, "field3");
 *         return new MyClass(field1, field2, field3);
 *     }
 * }}
 * </pre>
 * <p>
 * Parameter {@code weight} specifies chance annotated constructor/method to be used
 * in creation of random object. Parameter {@code random} specifies whether constructor/method
 * parameters should be randomized or not. If this {@code random} is false, then even if the
 * {@code random} value in the parameter annotation {@link RandomValue} is true, the default
 * value will be used.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface RandomCreator {
    /**
     * Chance that annotated constructor/method will be used in creation of random object.
     * @return the chance weight
     */
    int weight() default 1;

    /**
     * Whether constructor/method parameters should be randomized or not.
     * @return {@code true} if constructor/method parameters should be randomized
     */
    boolean random() default true;
}
