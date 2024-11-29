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

import java.lang.reflect.*;
import java.util.*;
import java.util.random.RandomGenerator;

/**
 * Generates random objects of the given class.
 * <p>
 * To configure randomization of each field, they should be annotated with {@link RandomValue}.
 * It contains parameters for all primitive types, String, enum, record, array, List, Set and
 * Map.
 * </p>
 * <p>
 * For configuration of randomization numbers in a range annotation parameters {@code <type>MinValue}
 * and {@code <type>MaxValue} should be used. Range parameters will be ignored is parameter
 * <i>type</i>Values contains non-zero count of values. Example:
 * <pre>
 * {@code
 * public class Money {
 *     //generates random int in range [100, 1000]
 *     @RandomValue(intMinValue = 100, intMaxValue = 1000)
 *     private int amount;
 *
 *     public int getAmount() {
 *         return amount;
 *     }
 * }}
 * </pre>
 * <p>
 * For configuration of randomization from a limited number of values annotation parameters
 * {@code <type>Values} should be used. If this parameters contains non-zero count of values
 * it will be used instead if range parameters. Example:
 * <pre>
 * {@code
 * public class Color {
 *     //generates random value from "red", "green", "blue"
 *     @RandomValue(stringValues = {"red", "green", "blue"})
 *     private String name;
 *
 *     public String String getName() {
 *         return name;
 *     }
 * }}
 * </pre>
 * <p>
 * For configuration of randomization of arrays parameters of array type,
 * {@code arrayMinLength} and {@code arrayMaxLength} should be used. Example:
 * <pre>
 * {@code
 * public class ID {
 *     //generates random array of bytes with size between 5 and 10
 *     @RandomValue(arrayMinLength = 5, arrayMaxLength = 10)
 *     private byte[] bytes;
 *
 *     public byte[] getBytes() {
 *         return bytes;
 *     }
 * }}
 * </pre>
 * <p>
 * For configuration of randomization of lists, sets and maps parameters of collection
 * type, {@code containerMinSize} and {@code containerMaxSize} should be used. Example:
 * <pre>
 * {@code
 * public class Student {
 *     //generates random name
 *     @RandomValue(stringMinLength = 5, stringMaxLength = 10, charMinValue = 'a', charMaxValue = 'z')
 *     private String name;
 *     //generates random age between 18 and 24
 *     @RandomValue(intMinValue = 18, intMaxValue = 24)
 *     private int age;
 *
 *     public String getName() {
 *         return name;
 *     }
 *
 *     public int getAge() {
 *         return age;
 *     }
 * }
 *
 * public class Group {
 *     //generates list of students with size between 10 and 15
 *     @RandomValue(containerMinSize = 10, containerMaxSize = 15)
 *     private List<Student> students;
 *
 *     public List<Student> getStudents() {
 *         return students;
 *     }
 * }
 * }
 * </pre>
 * <p>
 * If some fields should not be generated randomly parameter {@code random} should be set to false.
 * To customize default values of primitive types and enums parameter {@code default<type>}. Example:
 * <pre>
 * {@code
 * public class Color {
 *     private byte red;
 *     private byte green;
 *     //blue will always be 0
 *     @RandomValue(random=false)
 *     private byte blue;
 *
 *     public void setRed(int red) {
 *         this.red = (byte) red;
 *     }
 *
 *     public int getRed() {
 *         return red & 0xff;
 *     }
 *
 *     public void setGreen(int green) {
 *         this.green = (byte) green;
 *     }
 *
 *     public int getGreen() {
 *         return green & 0xff;
 *     }
 *
 *     public void setBlue(int blue) {
 *         this.blue = (byte) blue;
 *     }
 *
 *     public int getBlue() {
 *         return blue & 0xff;
 *     }
 * }}
 * </pre>
 * <p>
 * In addition to fields, this generator allows you to generate arguments for constructors
 * and methods. To use method or constructor for creating objects, they must be annotated
 * with {@link RandomCreator}. This annotation has two parameters: {@code weight} and
 * {@code random}. Weight is used to determine what method or constructor annotated
 * with {@link RandomCreator} should be used (default is 1). Random parameter is used to
 * determine whether method's or constructor's parameters should be randomized (default is true).
 * Example:
 * <pre>
 * {@code
 * public class Entity {
 *     public enum Type {
 *         PLAYER, ENTITY
 *     }
 *
 *     private final Type type;
 *     private final UUID id;
 *
 *     @RandomCreator
 *     private Entity(Type type, @RandomValue(arrayMinLength = 2, arrayMaxLength = 2) long[] idParts) {
 *         this.type = type;
 *         this.id = new UUID(idParts[0], idParts[1]);
 *     }
 *
 *     public Type getType() {
 *         return type;
 *     }
 *
 *     public UUID getId() {
 *         return id;
 *     }
 * }}
 * </pre>
 * @param <T> the type of objects that this generator generates
 */
public class ObjectGenerator<T> implements Generator<T> {
    /**
     * Class of generating object.
     */
    protected final Class<T> type;

    /**
     * Default constructor that creates new instance of {@code ObjectGenerator}.
     * This generator supports normal classes, enums and records. Given class
     * must not be interface, abstract or anonymous class or instance member class.
     *
     * @param type class of generating object
     *
     * @throws NullPointerException if {@code type} is null
     * @throws IllegalArgumentException if {@code type} is interface, abstract,
     * instance member or anonymous class.
     */
    public ObjectGenerator(Class<T> type) {
        checkClass(type);
        this.type = type;
    }

    /**
     * Returns the class of objects that this generator generates.
     *
     * @return the class of objects that this generator generates
     */
    public Class<T> getType() {
        return type;
    }

    public T generate(RandomGenerator random) {
        return random(type, null, 1, random);
    }

    /**
     * Decides whether to generate a value for a field or method or not based on
     * the value of the {@link RandomValue} annotation.
     * <p>
     * If the annotation is null, then the value is going to be generated. If the
     * annotation is not null, then it depends on the value of its {@link RandomValue#random()}
     * attribute. If it is true, then the value is going to be generated. If it is
     * false, then the value is not going to be generated.
     * </p>
     *
     * @param value the value of the annotation
     * @return true if the value is going to be generated, false otherwise
     */
    protected static boolean shouldGenerate(RandomValue value) {
        return value == null || value.random();
    }

    /**
     * Method returns the class that given type represents including raw types.
     * <p>
     * If the type is a {@link Class}, then it is returned as is. If the type
     * is a {@link TypeVariable}, then the {@link Class} representing the
     * declaring class is returned. If the type is a {@link ParameterizedType},
     * then the {@link Class} representing the raw type is returned. If the
     * type is a {@link WildcardType}, then the {@link Class} representing the
     * lower bound is returned. If there is no lower bound, then
     * {@link Object} is returned. If the type is a {@link GenericArrayType},
     * then the {@link Class} representing the array of objects is returned.
     * </p>
     *
     * @param type the type
     * @param <T>  the type of the class
     * @return the class that the given type represents
     *
     * @throws GenerationException if the type is not a class
     */
    @SuppressWarnings("unchecked")
    protected static <T> Class<T> extractClass(Type type) {
        if (type instanceof Class<?> c) {
            return (Class<T>) c;
        } else if (type instanceof TypeVariable<?> t) {
            return (Class<T>) t.getGenericDeclaration();
        } else if (type instanceof ParameterizedType p) {
            Type rawType = p.getRawType();
            return extractClass(rawType);
        } else if (type instanceof WildcardType w) {
            Type[] bounds = w.getLowerBounds();
            if (bounds.length == 0) {
                return (Class<T>) Object.class;
            } else {
                return extractClass(bounds[0]);
            }
        } else if (type instanceof GenericArrayType) {
            return (Class<T>) Object[].class;
        } else {
            throw new GenerationException(type + " is a class");
        }
    }

    /**
     * Method returns an array of types that are "inside" the given type.
     * <p>
     * If the type is an array, then the component type of the array is returned.
     * If the type is a parameterized type, then the actual type arguments are returned.
     * If the type is a generic array type, then the generic component type of the array is returned.
     * </p>
     * <p>
     * If the type is not an array, parameterized type or generic array type,
     * then a {@link GenerationException} is thrown.
     * </p>
     *
     * @param type the type
     * @return the types that are inside the given type
     */
    protected static Type[] extractInnerTypes(Type type) {
        if (type instanceof Class<?> c && c.isArray()) {
            return new Type[] {c.getComponentType()};
        } else if (type instanceof ParameterizedType p) {
            return p.getActualTypeArguments();
        } else if (type instanceof GenericArrayType g) {
            return new Type[] {g.getGenericComponentType()};
        } else {
            throw new GenerationException(type + " does not have a class as raw type");
        }
    }

    /**
     * Method that extracts all constructors and static methods annotated with
     * {@link RandomCreator} from the given class and adds them to the given list.
     * <p>
     * The given list is not cleared before adding the constructors and methods.
     * </p>
     *
     * @param creators the list that constructors and methods will be added to
     * @param clazz    the class to extract the constructors and methods from
     */
    protected static void extractCreators(List<Executable> creators, Class<?> clazz) {
        List<Constructor<?>> constructorsList = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.isAnnotationPresent(RandomCreator.class))
                .toList();
        creators.addAll(constructorsList);
        List<Method> methodsArray = Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> Modifier.isStatic(m.getModifiers()) && m.isAnnotationPresent(RandomCreator.class) &&
                        clazz.isAssignableFrom(m.getReturnType()))
                .toList();
        creators.addAll(methodsArray);
    }

    /**
     * Returns a random constructor or static method from the given list of creators.
     * <p>
     * The given list must not be empty.
     * </p>
     * <p>
     * The weight of each constructor or method is determined by the
     * {@link RandomCreator#weight()} parameter of the {@link RandomCreator}
     * annotation. The weight must be greater than or equal to 1.
     * </p>
     * <p>
     * The given random object is used to generate the random number that
     * determines which constructor or method to return.
     * </p>
     * <p>
     * If the given list is empty, then this method throws a
     * {@link GenerationException}.
     * </p>
     *
     * @param creators the list of constructors and methods to return a random
     *                 element from
     * @param random   the random object to use to generate the random number
     * @return a random constructor or static method from the given list
     *
     * @throws GenerationException if the given list is empty
     */
    protected static Executable getRandomCreator(List<Executable> creators, RandomGenerator random) {
        long weightSum = 0;
        for (Executable creator : creators) {
            int weight = creator.getAnnotation(RandomCreator.class).weight();
            if (weight < 1) {
                throw new GenerationException("constructor weight must be greater than or equal to 1");
            }
            weightSum += weight;
        }
        long nextLong = random.nextLong(weightSum);
        for (Executable creator : creators) {
            int weight = creator.getAnnotation(RandomCreator.class).weight();
            if (nextLong < weight) {
                return creator;
            } else {
                nextLong -= weight;
            }
        }
        throw new GenerationException("constructor not found");
    }

    /**
     * Extracts all non-static and non-final fields from the given class and all its
     * superclasses to the given list.
     * <p>
     * The given class must not be null.
     * </p>
     *
     * @param fields the list to add the fields to
     * @param type   the class to extract the fields from
     * @throws NullPointerException if the given list or class is null
     */
    protected static void extractFields(List<Field> fields, Class<?> type) {
        while (!type.equals(Object.class)) {
            List<Field> fieldsList = Arrays.stream(type.getDeclaredFields())
                    .filter(f -> !Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
                    .toList();
            fields.addAll(fieldsList);
            type = type.getSuperclass();
        }
    }

    /**
     * Generates a random object of the given type using the given generator class.
     * <p>
     * The generator class must implement the {@link Generator} interface. It must
     * also have an empty constructor which is accessible.
     * </p>
     *
     * @param generator the class to use to generate the random object
     * @param random    the random object to use to generate the random object
     * @param <T>       the type of the random object
     * @return a random object of the given type
     */
    @SuppressWarnings("unchecked")
    protected static <T> T generate(Class<?> generator, RandomGenerator random) {
        if (!Generator.class.isAssignableFrom(generator)) {
            throw new GenerationException(generator + " does not implement " + Generator.class);
        }
        try {
            Constructor<? extends Generator<T>> constructor = (Constructor<? extends Generator<T>>)
                    generator.getDeclaredConstructor();
            if (!constructor.trySetAccessible()) {
                throw new GenerationException("cannot access empty constructor of " + generator.getTypeName());
            }
            return constructor.newInstance().generate(random);
        } catch (Exception e) {
            throw new GenerationException(e);
        }
    }

    /**
     * Generates a random object of the given type, using the given random value
     * and generator class if specified.
     * <p>
     * If the given random value is not null and its {@link RandomValue#random()}
     * method returns true, then the given generator class is used to generate the
     * random object. Otherwise, this method returns default value of given type.
     * </p>
     * <p>
     * If the given generator class is used, then it must implement the
     * {@link Generator} interface. The generator must also have an empty
     * constructor which is accessible.
     * </p>
     *
     * @param type   the type of the random object to generate
     * @param value  the random value to use to generate the random object
     * @param depth  the maximum depth of object generation
     * @param random the random object to use to generate the random object
     * @param <T>    the type of the random object
     * @return a random object of the given type
     *
     * @throws GenerationException if the given generator class does not implement
     *                              the {@link Generator} interface, or if the
     *                              given generator class does not have an empty
     *                              constructor
     */
    protected static <T> T generate(Type type, RandomValue value, int depth, RandomGenerator random) {
        if (value != null && value.random() && !value.objectGenerator().equals(ObjectGenerator.class)) {
            return generate(value.objectGenerator(), random);
        }
        if (value == null || value.random()) {
            return random(type, value, depth - 1, random);
        } else {
            return defaultValue(type, value);
        }
    }

    /**
     * Generates a random object of the given type using the given random value
     * and generator class if specified.
     * <p>
     * If the given random value is not null and its {@link RandomValue#random()}
     * method returns true, then the given generator class is used to generate the
     * random object. Otherwise, this method returns default value of given type.
     * </p>
     * <p>
     * If the given generator class is used, then it must implement the
     * {@link Generator} interface. The generator must also have an empty
     * constructor which is accessible.
     * </p>
     * <p>
     * If the given type is an array type, then the length of the array is
     * determined by the given random value. If the given random value is null,
     * then the length of the array is determined by the given random object.
     * </p>
     * <p>
     * If the given generator class is not used and the given type is not an array
     * type and the given type does not have at least one constructor or static
     * method that is annotated with the {@link RandomCreator} annotation, then
     * this method returns null.
     * </p>
     *
     * @param type   the type of the random object to generate
     * @param value  the random value to use to generate the random object
     * @param depth  the maximum depth of object generation
     * @param random the random object to use to generate the random object
     * @param <T>    the type of the random object
     * @return a random object of the given type
     *
     * @throws GenerationException if the given generator class does not implement
     *                              the {@link Generator} interface, or if the
     *                              given generator class does not have an empty
     *                              constructor, or if the given type does not
     *                              have at least one constructor or static method
     */
    @SuppressWarnings("unchecked")
    protected static <T> T random(Type type, RandomValue value, int depth, RandomGenerator random) {
        T randomValue = randomValue(type, value, random);
        if (randomValue != null) {
            return randomValue;
        }
        if (depth <= 0) {
            return null;
        }
        Class<?> clazz = extractClass(type);

        ArrayList<Executable> creators = new ArrayList<>();
        extractCreators(creators, clazz);
        if (!creators.isEmpty()) {
            Executable executable = getRandomCreator(creators, random);
            RandomCreator creator = executable.getAnnotation(RandomCreator.class);
            if (executable.trySetAccessible()) {
                try {
                    Object[] arguments;
                    if (creator == null || creator.random()) {
                        arguments = random(executable.getParameters(), value, random);
                    } else {
                        Parameter[] parameters = executable.getParameters();
                        arguments = new Object[parameters.length];
                        for (int i = 0; i < parameters.length; i++) {
                            arguments[i] = defaultValue(parameters[i].getParameterizedType(), value);
                        }
                    }
                    if (executable instanceof Constructor<?> cons) {
                        return (T) cons.newInstance(arguments);
                    } else if (executable instanceof Method method) {
                        return (T) method.invoke(null, arguments);
                    } else {
                        throw new GenerationException("unreachable exception");
                    }
                } catch (Exception e) {
                    throw new GenerationException(e);
                }
            } else {
                throw new GenerationException(executable.toGenericString() + " is not accessible in " + type);
            }
        }

        Object obj;
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> cons = constructors[random.nextInt(constructors.length)];
        if (cons.trySetAccessible()) {
            try {
                Object[] arguments = random(cons.getParameters(), value, random);
                obj = cons.newInstance(arguments);
            } catch (Exception e) {
                throw new GenerationException(e);
            }
        } else {
            throw new GenerationException(cons.toGenericString() + " is not accessible in " + type);
        }

        ArrayList<Field> fields = new ArrayList<>();
        extractFields(fields, clazz);
        for (Field field : fields) {
            if (field.trySetAccessible()) {
                try {
                    RandomValue annotation = field.getAnnotation(RandomValue.class);
                    if (shouldGenerate(value)) {
                        field.set(obj, generate(field.getGenericType(), annotation, depth - 1, random));
                    } else {
                        field.set(obj, defaultValue(field.getGenericType(), annotation));
                    }
                } catch (Exception e) {
                    throw new GenerationException("Exception in generating field '%s' in '%s'"
                            .formatted(field.getName(), type), e);
                }
            }
        }

        return (T) obj;
    }

    /**
     * Returns an array of objects, each of which is a random value of the corresponding type,
     * as specified by the annotations on the given parameters.
     *
     * @param params the parameters for which to generate random values
     * @param value  the annotation that specifies the depth of the generated values
     * @param random a source of randomness
     * @return an array of randomly generated values, one for each parameter
     */
    protected static Object[] random(Parameter[] params, RandomValue value, RandomGenerator random) {
        Object[] objects = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            RandomValue annotation = params[i].getAnnotation(RandomValue.class);
            if (shouldGenerate(value)) {
                objects[i] = generate(params[i].getParameterizedType(), annotation, getDepth(annotation), random);
            } else {
                objects[i] = defaultValue(params[i].getParameterizedType(), annotation);
            }
        }
        return objects;
    }

    /**
     * Generates a random byte. If the given {@link RandomValue} is present and specifies
     * {@code byteValues}, then one of those values is chosen at random. Otherwise, a random byte
     * is chosen from the range specified by the {@link RandomValue}, or from the whole range
     * of possible byte values if the annotation is not present.
     *
     * @param value  the annotation that specifies the range of values, or {@code null}
     * @param random a source of randomness
     * @return a randomly generated byte
     */
    private static byte randomByte(RandomValue value, RandomGenerator random) {
        if (value == null || value.byteValues().length == 0) {
            byte minValue = value != null ? value.byteMinValue() : Byte.MIN_VALUE;
            byte maxValue = value != null ? value.byteMaxValue() : Byte.MAX_VALUE;
            return Generator.generateByte(random, minValue, maxValue);
        } else {
            return value.byteValues()[random.nextInt(value.byteValues().length)];
        }
    }

    /**
     * Generates a random character. If the given {@link RandomValue} is present and specifies
     * {@code charValues}, then one of those values is chosen at random. Otherwise, a random
     * character is chosen from the range specified by the {@link RandomValue}, or from the
     * whole range of possible character values if the annotation is not present.
     *
     * @param value  the annotation that specifies the range of values, or {@code null}
     * @param random a source of randomness
     * @return a randomly generated character
     */
    private static char randomChar(RandomValue value, RandomGenerator random) {
        if (value == null || value.charValues().length == 0) {
            char minValue = value != null ? value.charMinValue() : Character.MIN_VALUE;
            char maxValue = value != null ? value.charMaxValue() : Character.MAX_VALUE;
            return Generator.generateChar(random, minValue, maxValue);
        } else {
            return value.charValues()[random.nextInt(value.charValues().length)];
        }
    }

    /**
     * Generates a random short. If the given {@link RandomValue} is present and specifies
     * {@code shortValues}, then one of those values is chosen at random. Otherwise, a random
     * short is chosen from the range specified by the {@link RandomValue}, or from the
     * whole range of possible short values if the annotation is not present.
     *
     * @param value  the annotation that specifies the range of values, or {@code null}
     * @param random a source of randomness
     * @return a randomly generated short
     */
    private static short randomShort(RandomValue value, RandomGenerator random) {
        if (value == null || value.shortValues().length == 0) {
            short minValue = value != null ? value.shortMinValue() : Short.MIN_VALUE;
            short maxValue = value != null ? value.shortMaxValue() : Short.MAX_VALUE;
            return Generator.generateShort(random, minValue, maxValue);
        } else {
            return value.shortValues()[random.nextInt(value.shortValues().length)];
        }
    }

    /**
     * Generates a random int. If the given {@link RandomValue} is present and specifies
     * {@code intValues}, then one of those values is chosen at random. Otherwise, a random
     * int is chosen from the range specified by the {@link RandomValue}, or from the
     * whole range of possible int values if the annotation is not present.
     *
     * @param value  the annotation that specifies the range of values, or {@code null}
     * @param random a source of randomness
     * @return a randomly generated int
     */
    private static int randomInt(RandomValue value, RandomGenerator random) {
        if (value == null || value.intValues().length == 0) {
            int minValue = value != null ? value.intMinValue() : Integer.MIN_VALUE;
            int maxValue = value != null ? value.intMaxValue() : Integer.MAX_VALUE;
            return Generator.generateInt(random, minValue, maxValue);
        } else {
            return value.intValues()[random.nextInt(value.intValues().length)];
        }
    }

    /**
     * Generates a random long. If the given {@link RandomValue} is present and specifies
     * {@code longValues}, then one of those values is chosen at random. Otherwise, a random
     * long is chosen from the range specified by the {@link RandomValue}, or from the
     * whole range of possible long values if the annotation is not present.
     *
     * @param value  the annotation that specifies the range of values, or {@code null}
     * @param random a source of randomness
     * @return a randomly generated long
     */
    private static long randomLong(RandomValue value, RandomGenerator random) {
        if (value == null || value.longValues().length == 0) {
            long minValue = value != null ? value.longMinValue() : Long.MIN_VALUE;
            long maxValue = value != null ? value.longMaxValue() : Long.MAX_VALUE;
            return Generator.generateLong(random, minValue, maxValue);
        } else {
            return value.longValues()[random.nextInt(value.longValues().length)];
        }
    }

    /**
     * Generates a random float. If the given {@link RandomValue} is present and specifies
     * {@code floatValues}, then one of those values is chosen at random. Otherwise, a random
     * float is chosen from the range specified by the {@link RandomValue}, or from the
     * whole range of possible float values if the annotation is not present.
     * <p>
     * The range parameters are validated to ensure that the minimum value is less than or
     * equal to the maximum value, and that both values are finite and not NaN.
     * </p>
     * @param value  the annotation that specifies the range of values, or {@code null}
     * @param random a source of randomness
     * @return a randomly generated float
     */
    private static float randomFloat(RandomValue value, RandomGenerator random) {
        if (value == null || value.floatValues().length == 0) {
            float minValue = value != null ? value.floatMinValue() : 0F;
            float maxValue = value != null ? value.floatMaxValue() : 1F;
            return Generator.generateFloat(random, minValue, maxValue);
        } else {
            return value.floatValues()[random.nextInt(value.floatValues().length)];
        }
    }

    /**
     * Generates a random double. If the given {@link RandomValue} is present and specifies
     * {@code doubleValues}, then one of those values is chosen at random. Otherwise, a random
     * double is chosen from the range specified by the {@link RandomValue}, or from the
     * whole range of possible double values if the annotation is not present.
     * <p>
     * The range parameters are validated to ensure that the minimum value is less than or
     * equal to the maximum value, and that both values are finite and not NaN.
     * </p>
     * @param value  the annotation that specifies the range of values, or {@code null}
     * @param random a source of randomness
     * @return a randomly generated double
     */
    private static double randomDouble(RandomValue value, RandomGenerator random) {
        if (value == null || value.doubleValues().length == 0) {
            double minValue = value != null ? value.doubleMinValue() : 0D;
            double maxValue = value != null ? value.doubleMaxValue() : 1D;
            return Generator.generateDouble(random, minValue, maxValue);
        } else {
            return value.doubleValues()[random.nextInt(value.doubleValues().length)];
        }
    }

    /**
     * Generates a random string. If the given {@link RandomValue} is present and specifies
     * {@code stringValues}, then one of those values is chosen at random. Otherwise, a random
     * string is chosen from the range specified by the {@link RandomValue}, or from the
     * whole range of possible string values if the annotation is not present.
     * {@code charValue}, {@code charMinValue} and {@code charMaxValue} are used for generating
     * characters.
     * <p>
     * The range parameters are validated to ensure that the minimum length is less than
     * or equal to the maximum length, and that both lengths are non-negative.
     * </p>
     * @param value  the annotation that specifies the range of values, or {@code null}
     * @param random a source of randomness
     * @return a randomly generated string
     */
    private static String randomString(RandomValue value, RandomGenerator random) {
        if (value == null || value.stringValues().length == 0) {
            int minLength = value != null ? value.stringMinLength() : 1;
            int maxLength = value != null ? value.stringMaxLength() : 10;
            return Generator.generateString(random, minLength, maxLength, r -> randomChar(value, r));
        } else {
            return value.stringValues()[random.nextInt(value.stringValues().length)];
        }
    }

    /**
     * Generates a random enum value from the given enum class. If the given
     * {@link RandomValue} is present and specifies {@code enumValues}, then one of
     * those values is chosen at random. Otherwise, a random enum value is chosen
     * from the range of possible enum values.
     * <p>
     * If the given {@link RandomValue} is present and specifies {@code enumValues},
     * it is used to generate a random enum value. Otherwise, all the enum values
     * of the given enum class are used.
     * </p>
     * @param clazz  the enum class to generate the enum value from
     * @param value  the annotation that specifies the range of values, or {@code null}
     * @param random a source of randomness
     * @return a randomly generated enum value
     */
    private static <T extends Enum<T>> T randomEnum(Class<T> clazz, RandomValue value, RandomGenerator random) {
        if (value == null || value.enumValues().length == 0) {
            T[] enumValues = clazz.getEnumConstants();
            return Generator.generateEnum(random, clazz, Generators.ofInt(enumValues.length));
        } else {
            try {
                return Enum.valueOf(clazz, value.enumValues()[random.nextInt(value.enumValues().length)]);
            } catch (IllegalArgumentException e) {
                throw new GenerationException(e);
            }
        }
    }

    /**
     * Generates a random record from the given record class.
     * <p>
     * Method generates randomValues for all record components and gets
     * constructor that takes all record components as parameters.
     * </p>
     * <p>
     * The generated record is then constructed using the default constructor
     * of the record class.
     * </p>
     * @param clazz  the record class to generate the record from
     * @param value  the annotation that specifies the range of values, or {@code null}
     * @param random a source of randomness
     * @return a randomly generated record
     */
    private static <T extends Record> T randomRecord(Class<T> clazz, RandomValue value, RandomGenerator random) {
        RecordComponent[] components = clazz.getRecordComponents();
        Object[] values = new Object[components.length];
        for (int i = 0; i < components.length; i++) {
            RecordComponent component = components[i];
            RandomValue annotation = component.getAnnotation(RandomValue.class);
            if (shouldGenerate(value)) {
                values[i] = generate(component.getGenericType(), annotation, getDepth(annotation), random);
            } else {
                values[i] = defaultValue(component.getGenericType(), annotation);
            }
        }
        Constructor<T> cons;
        try {
            Class<?>[] parameters = Arrays.stream(components)
                    .map(RecordComponent::getType)
                    .toArray(Class[]::new);
            cons = clazz.getDeclaredConstructor(parameters);
        } catch (NoSuchMethodException e) {
            return null;
        }
        try {
            if (!cons.trySetAccessible()) {
                throw new GenerationException(cons.toGenericString() + " is not accessible in record " +
                        clazz.getCanonicalName());
            }
            return cons.newInstance(values);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Generates a random array from the given array type.
     * <p>
     * Method generates randomValues for all elements in array and then
     * constructs the array using the default constructor of the array class.
     * Elements in arrays are generated using {@code <type>Values},
     * {@code <type>MinValues} and {@code <type>MaxValues} parameters.
     * </p>
     * @param type     the type of the array
     * @param value    the annotation that specifies the range of values, or {@code null}
     * @param random   a source of randomness
     * @param minCount the minimum size of the array
     * @param maxCount the maximum size of the array
     * @param depth    the object depth of the random generation
     * @return a randomly generated array
     */
    private static Object randomArray(Type type, RandomValue value, RandomGenerator random, int minCount, int maxCount,
                                      int depth) {
        int size = Generator.generateInt(random, minCount, maxCount);
        return Generator.generateArray(random, extractClass(type), r -> random(type, value, depth - 1, r), size);
    }

    /**
     * Generates a random list from the given list type.
     * <p>
     * Method generates random values for all elements in list and then
     * constructs the list using the default constructor of the list class.
     * If the given list class is {@link List}, just a list is returned.
     * If the given list class has a constructor with one parameter of type
     * {@link Collection}, this constructor is used to generate the list.
     * If the given list class has a default constructor and method
     * {@code addAll(Collection<?>)}, the default constructor is used to
     * generate the list and then the method is called to add all elements
     * to the generated list.
     * </p>
     * @param listType    the type of the list
     * @param value       the annotation that specifies the range of values, or {@code null}
     * @param random      a source of randomness
     * @param minSize     the minimum size of the list
     * @param maxSize     the maximum size of the list
     * @param elementType the type of the elements in the list
     * @param depth       the object depth of the random generation
     * @return a randomly generated list
     */
    @SuppressWarnings("unchecked")
    private static List<?> randomList(Type listType, RandomValue value, RandomGenerator random, int minSize,
                                      int maxSize, Type elementType, int depth) {
        int size = Generator.generateInt(random, minSize, maxSize);
        List<Object> baseList = Generator.generateList(random, extractClass(elementType),
                r -> random(elementType, value, depth - 1, r), size);
        Class<?> listClass = extractClass(listType);
        if (listClass.equals(List.class)) {
            return baseList;
        }
        try {
            //trying to get constructor with initial values in collection
            Constructor<List<?>> defaultConstructor = (Constructor<List<?>>) listClass.getDeclaredConstructor(Collection.class);
            if (defaultConstructor.trySetAccessible()) {
                return defaultConstructor.newInstance(baseList);
            }
        } catch (Exception e) {
            //ignore
        }
        try {
            //trying to get empty constructor and then use addAll method
            Constructor<List<?>> emptyConstructor = (Constructor<List<?>>) listClass.getDeclaredConstructor();
            if (emptyConstructor.trySetAccessible()) {
                List<?> list = emptyConstructor.newInstance();
                Method addAll = listClass.getMethod("addAll", Collection.class);
                addAll.invoke(list, baseList);
                return list;
            }
        } catch (Exception e) {
            //ignore
        }
        return null;
    }

    /**
     * Generates a random set from the given set type.
     * <p>
     * Method generates random values for all elements in set and then
     * constructs the set using the default constructor of the set class.
     * If the given set class is {@link Set}, just a set is returned.
     * If the given set class has a constructor with one parameter of type
     * {@link Collection}, this constructor is used to generate the set.
     * If the given set class has a default constructor and method
     * {@code addAll(Collection<?>)}, the default constructor is used to
     * generate the set and then the method is called to add all elements
     * to the generated set.
     * </p>
     * @param setType     the type of the set
     * @param value       the annotation that specifies the range of values, or {@code null}
     * @param random      a source of randomness
     * @param minSize     the minimum size of the set
     * @param maxSize     the maximum size of the set
     * @param elementType the type of the elements in the set
     * @param depth       the object depth of the random generation
     * @return a randomly generated set
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Set<?> randomSet(Type setType, RandomValue value, RandomGenerator random, int minSize, int maxSize,
                                      Type elementType, int depth) {
        int size = Generator.generateInt(random, minSize, maxSize);
        Set baseSet = Generator.generateSet(random, extractClass(elementType),
                r -> random(elementType, value, depth - 1, r), size);
        Class<?> setClass = extractClass(setType);
        if (setClass.equals(Set.class)) {
            return baseSet;
        }
        try {
            //trying to get constructor with initial values in collection
            Constructor<Set<?>> defaultConstructor = (Constructor<Set<?>>) setClass.getDeclaredConstructor(Collection.class);
            if (defaultConstructor.trySetAccessible()) {
                return defaultConstructor.newInstance(baseSet);
            }
        } catch (Exception e) {
            //ignore
        }
        try {
            //trying to get empty constructor and then use addAll method
            Constructor<Set<?>> emptyConstructor = (Constructor<Set<?>>) setClass.getDeclaredConstructor();
            if (emptyConstructor.trySetAccessible()) {
                Set<?> set = emptyConstructor.newInstance();
                Method addAll = setClass.getMethod("addAll", Collection.class);
                addAll.invoke(set, baseSet);
                return set;
            }
        } catch (Exception e) {
            //ignore
        }
        return null;
    }

    /**
     * Generates a random map from the given map type.
     * <p>
     * Method generates random values for all elements in map and then
     * constructs the map using the default constructor of the map class.
     * If the given map class is {@link Map}, just a map is returned.
     * If the given map class has a constructor with one parameter of type
     * {@link Map}, this constructor is used to generate the map.
     * If the given map class has a default constructor and method
     * {@code putAll(Map)}, the default constructor is used to
     * generate the map and then the method is called to add all elements
     * to the generated map.
     * </p>
     * @param mapType   the type of the map
     * @param value     the annotation that specifies the range of values, or {@code null}
     * @param random    a source of randomness
     * @param minSize   the minimum size of the map
     * @param maxSize   the maximum size of the map
     * @param keyType   the type of the keys in the map
     * @param valueType the type of the values in the map
     * @param depth     the object depth of the random generation
     * @return a randomly generated map
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Map<?, ?> randomMap(Type mapType, RandomValue value, RandomGenerator random, int minSize, int maxSize,
                                         Type keyType, Type valueType, int depth) {
        int size = Generator.generateInt(random, minSize, maxSize);
        Map baseMap = Generator.generateMap(random, extractClass(keyType), extractClass(valueType),
                r -> random(keyType, value, depth - 1, r), r -> random(valueType, value, depth - 1, r),
                (o, n) -> o, size
        );
        Class<?> mapClass = extractClass(mapType);
        if (mapClass.equals(Map.class)) {
            return baseMap;
        }
        try {
            Constructor<Map<?, ?>> defaultConstructor = (Constructor<Map<?, ?>>) mapClass.getDeclaredConstructor(Map.class);
            if (defaultConstructor.trySetAccessible()) {
                return defaultConstructor.newInstance(baseMap);
            }
        } catch (Exception e) {
            //ignore
        }
        try {
            Constructor<Map<?, ?>> emptyConstructor = (Constructor<Map<?, ?>>) mapClass.getDeclaredConstructor();
            if (emptyConstructor.trySetAccessible()) {
                Map<?, ?> map = emptyConstructor.newInstance();
                Method putAll = mapClass.getMethod("putAll", Map.class);
                putAll.invoke(map, baseMap);
                return map;
            }
        } catch (Exception e) {
            //ignore
        }
        return null;
    }

    /**
     * Generates a random value of the given type.
     * <p>
     * The given {@link RandomValue} and the given {@link Random} object are used to generate
     * the random value. The type of the generated value is then converted to the given
     * {@code type} using the constructor with the least number of parameters.
     * </p>
     * @param type   the type of the value
     * @param value  the random value parameter
     * @param random the random object
     * @param <T>    the type of the generated value
     * @return a randomly generated value
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static <T> T randomValue(Type type, RandomValue value, RandomGenerator random) {
        if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return (T) (Boolean) random.nextBoolean();
        } else if (type.equals(byte.class) || type.equals(Byte.class)) {
            return (T) (Byte) randomByte(value, random);
        } else if (type.equals(char.class) || type.equals(Character.class)) {
            return (T) (Character) randomChar(value, random);
        } else if (type.equals(short.class) || type.equals(Short.class)) {
            return (T) (Short) randomShort(value, random);
        } else if (type.equals(int.class) || type.equals(Integer.class)) {
            return (T) (Integer) randomInt(value, random);
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            return (T) (Long) randomLong(value, random);
        } else if (type.equals(float.class) || type.equals(Float.class)) {
            return (T) (Float) randomFloat(value, random);
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            return (T) (Double) randomDouble(value, random);
        } else if (type.equals(String.class)) {
            return (T) randomString(value, random);
        }
        Class<?> clazz = extractClass(type);
        if (clazz.isEnum()) {
            return (T) randomEnum((Class<? extends Enum>) clazz, value, random);
        } else if (clazz.isRecord()) {
            return (T) randomRecord((Class<? extends Record>) clazz, value, random);
        } else if (clazz.isArray()) {
            Type[] rawTypes = extractInnerTypes(type);
            return (T) randomArray(rawTypes[0], value, random, getArrayMinLength(value),
                    getArrayMaxLength(value), getDepth(value));
        } else if (List.class.isAssignableFrom(clazz)) {
            Type[] rawTypes = extractInnerTypes(type);
            return (T) randomList(type, value, random, getContainerMinSize(value), getContainerMaxSize(value), rawTypes[0],
                    getDepth(value));
        } else if (Set.class.isAssignableFrom(clazz)) {
            Type[] rawTypes = extractInnerTypes(type);
            return (T) randomSet(type, value, random, getContainerMinSize(value), getContainerMaxSize(value), rawTypes[0],
                    getDepth(value));
        } else if (Map.class.isAssignableFrom(clazz)) {
            Type[] rawTypes = extractInnerTypes(type);
            return (T) randomMap(type, value, random, getContainerMinSize(value),
                    getContainerMaxSize(value), rawTypes[0], rawTypes[1], getDepth(value));
        } else {
            return null;
        }
    }

    /**
     * Returns the depth of the given {@link RandomValue}.
     * <p>
     * The object depth is the maximum depth of the object graph that is generated
     * by the random generator. If the given {@link RandomValue} is null, the
     * default depth is 3.
     * </p>
     * @param value the random value parameter
     * @return the object depth
     */
    private static int getDepth(RandomValue value) {
        return value != null ? value.objectDepth() : 3;
    }

    /**
     * Returns the default value for the given type and random value.
     * <p>
     * The default value is determined by the given random value. If the
     * given random value is null, the default value is determined by the
     * class of the given type.
     * </p>
     * @param type  the type
     * @param value the random value parameter
     * @param <T>   the type
     * @return the default value for the given type and random value
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected static <T> T defaultValue(Type type, RandomValue value) {
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return (T) (Boolean) (value != null && value.defaultBoolean());
        } else if (type.equals(Byte.class) || type.equals(byte.class)) {
            return (T) (Byte) (value != null ? value.defaultByte() : (byte) 0);
        } else if (type.equals(Character.class) || type.equals(char.class)) {
            return (T) (Character) (value != null ? value.defaultChar() : '\0');
        } else if (type.equals(Short.class) || type.equals(short.class)) {
            return (T) (Short) (value != null ? value.defaultShort() : (short) 0);
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return (T) (Integer) (value != null ? value.defaultInt() : 0);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return (T) (Long) (value != null ? value.defaultLong() : 0L);
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            return (T) (Float) (value != null ? value.defaultFloat() : 0F);
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return (T) (Double) (value != null ? value.defaultDouble() : 0D);
        } else if (type.equals(String.class)) {
            return (T) (value != null ? value.defaultString() : "");
        }
        Class<?> clazz = extractClass(type);
        if (clazz.equals(List.class)) {
            return (T) new ArrayList<>();
        } else if (clazz.equals(Set.class)) {
            return (T) new HashSet<>();
        } else if (clazz.equals(Map.class)) {
            return (T) new HashMap<>();
        } else if (clazz.isEnum()) {
            if (value == null || value.defaultEnum().isBlank()) {
                var enumConstants = clazz.getEnumConstants();
                if (enumConstants.length > 0) {
                    return (T) enumConstants[0];
                }
            } else {
                try {
                    return (T) Enum.valueOf((Class<? extends Enum>) clazz, value.defaultEnum());
                } catch (Exception e) {
                    //ignore
                }
            }
        } else if (clazz.isArray()) {
            int dimensions = 0;
            do {
                dimensions++;
                clazz = clazz.getComponentType();
            } while (clazz.isArray());
            return (T) Array.newInstance(clazz, new int[dimensions]);
        } else if (Collection.class.isAssignableFrom(clazz) ||
                Map.class.isAssignableFrom(clazz)) {
            try {
                return (T) clazz.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Gets the minimum length of the array. If the given {@link RandomValue} is
     * null, then 1 is returned.
     * @param value the given {@link RandomValue}
     * @return the minimum length
     */
    private static int getArrayMinLength(RandomValue value) {
        return value != null ? value.arrayMinLength() : 1;
    }

    /**
     * Gets the maximum length of the array. If the given {@link RandomValue} is
     * null, then 1 is returned.
     * @param value the given {@link RandomValue}
     * @return the maximum length
     */
    private static int getArrayMaxLength(RandomValue value) {
        return value != null ? value.arrayMaxLength() : 1;
    }

    /**
     * Gets the minimum size of the container. If the given {@link RandomValue} is
     * null, then 1 is returned.
     * @param value the given {@link RandomValue}
     * @return the minimum size
     */
    private static int getContainerMinSize(RandomValue value) {
        return value != null ? value.containerMinSize() : 1;
    }

    /**
     * Gets the maximum size of the container. If the given {@link RandomValue} is
     * null, then 1 is returned.
     * @param value the given {@link RandomValue}
     * @return the maximum size
     */
    private static int getContainerMaxSize(RandomValue value) {
        return value != null ? value.containerMaxSize() : 1;
    }

    /**
     * Checks if the given class is valid for random generation. If the class is not valid,
     * an {@link IllegalArgumentException} is thrown.
     * <p>
     * The following classes are not supported for random generation:
     * <ul>
     *     <li>interfaces</li>
     *     <li>anonymous classes</li>
     *     <li>abstract classes</li>
     *     <li>instance member classes</li>
     *     <li>annotations</li>
     *     <li>arrays</li>
     * </ul>
     * @param type the class to check
     * @throws IllegalArgumentException if the class is not supported for random generation
     */
    protected static void checkClass(Class<?> type) {
        Objects.requireNonNull(type);
        if (type.isInterface()) {
            throw new IllegalArgumentException("interfaces are not supported for random generation");
        }
        if (type.isAnonymousClass()) {
            throw new IllegalArgumentException("anonymous classes are not supported for random generation");
        }
        if (Modifier.isAbstract(type.getModifiers())) {
            throw new IllegalArgumentException("abstract classes are not supported for random generation");
        }
        if (type.isMemberClass() && !Modifier.isStatic(type.getModifiers())) {
            throw new IllegalArgumentException("instance member classes are not supported for random generation");
        }
        if (type.isAnnotation()) {
            throw new IllegalArgumentException("annotations are not supported for random generation");
        }
        if (type.isArray()) {
            throw new IllegalArgumentException("arrays are not supported for random generation");
        }
    }
}
