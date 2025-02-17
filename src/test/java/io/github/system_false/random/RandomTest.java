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

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

public class RandomTest {
    @SuppressWarnings("unused")
    static class TestPrimitiveObject {
        @RandomValue(random = false)
        private boolean bool;

        @RandomValue(byteMinValue = 0, byteMaxValue = 10)
        private byte byte1;
        @RandomValue(byteValues = {1, -7, 4})
        private byte byte2;

        @RandomValue(charMinValue = 'a', charMaxValue = 'z')
        private char char1;
        @RandomValue(charValues = {'a', 'b', 'c'})
        private char char2;

        @RandomValue(shortMinValue = 0, shortMaxValue = 10)
        private short short1;
        @RandomValue(shortValues = {9, 0, -5})
        private short short2;

        @RandomValue(intMinValue = 0, intMaxValue = 10)
        private int int1;
        @RandomValue(intValues = {4, 7, 2})
        private int int2;

        @RandomValue(longMinValue = 0, longMaxValue = 10)
        private long long1;
        @RandomValue(longValues = {-5, -9, -1})
        private long long2;

        @RandomValue(floatMinValue = 0.3f, floatMaxValue = 0.4f)
        private float float1;
        @RandomValue(floatValues = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
        private float float2;

        @RandomValue(doubleMinValue = 0.3, doubleMaxValue = 0.4)
        private double double1;
        @RandomValue(doubleValues = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
        private double double2;

        @RandomValue(charValues = {'a', 'b', 'c'}, stringMinLength = 4, stringMaxLength = 4)
        private String string1;
        @RandomValue(stringValues = {"abc", "bac", "cba"})
        private String string2;

        public TestPrimitiveObject() {
        }
    }

    @Test
    void objectPrimitiveTest() {
        Generator<TestPrimitiveObject> generator = Generators.of(TestPrimitiveObject.class);
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            TestPrimitiveObject object = assertDoesNotThrow(() -> generator.generate(random), "failed to generate object");
            assertFalse(object.bool, "bool is true");
            assertTrue(object.byte1 >= 0 && object.byte1 <= 10, "byte1 is not between 0 and 10");
            assertTrue(object.byte2 == 1 || object.byte2 == -7 || object.byte2 == 4, "byte2 is not 1, -7 or 4");
            assertTrue(object.char1 >= 'a' && object.char1 <= 'z', "char1 is not between 'a' and 'z'");
            assertTrue(object.char2 == 'a' || object.char2 == 'b' || object.char2 == 'c', "char2 is not 'a', 'b' or 'c'");
            assertTrue(object.short1 >= 0 && object.short1 <= 10, "short1 is not between 0 and 10");
            assertTrue(object.short2 == 9 || object.short2 == 0 || object.short2 == -5, "short2 is not 9, 0 or -5");
            assertTrue(object.int1 >= 0 && object.int1 <= 10, "int1 is not between 0 and 10");
            assertTrue(object.int2 == 4 || object.int2 == 7 || object.int2 == 2, "int2 is not 4, 7 or 2");
            assertTrue(object.long1 >= 0 && object.long1 <= 10, "long1 is not between 0 and 10");
            assertTrue(object.long2 == -5 || object.long2 == -9 || object.long2 == -1, "long2 is not -5, -9 or -1");
            assertTrue(object.float1 >= 0.3 && object.float1 <= 0.4, "float1 is not between 0.3 and 0.4");
            assertTrue(object.float2 % 1 == 0 && object.float2 >= 1 && object.float2 <= 10, "float2 is not between 1 and 10");
            assertTrue(object.double1 >= 0.3 && object.double1 <= 0.4, "double1 is not between 0.3 and 0.4");
            assertTrue(object.double2 % 1 == 0 && object.double2 >= 1 && object.double2 <= 10, "double2 is not between 1 and 10");
            assertEquals(4, object.string1.length(), "string1 length is not 4");
            for (char c : object.string1.toCharArray()) {
                assertTrue(c == 'a' || c == 'b' || c == 'c', "string1 contains not 'a', 'b' or 'c'");
            }
            assertTrue(object.string2.equals("abc") || object.string2.equals("bac") || object.string2.equals("cba"),
                    "string2 is not 'abc', 'bac' or 'cba'");
        }
    }

    @SuppressWarnings("unused")
    static class TestCreatorObject {
        private final UUID id;
        private final List<String> list;

        private TestCreatorObject(UUID id, List<String> list) {
            this.id = id;
            this.list = list;
        }

        @RandomCreator
        public static TestCreatorObject of(@RandomValue(arrayMinLength = 2, arrayMaxLength = 2) long[] parts,
                                           @RandomValue(containerMinSize = 3, containerMaxSize = 3, stringValues = {"a", "b", "c"}) List<String> list) {
            return new TestCreatorObject(new UUID(parts[0], parts[1]), list);
        }
    }

    @Test
    void objectCreatorTest() {
        Generator<TestCreatorObject> generator = Generators.of(TestCreatorObject.class);
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            TestCreatorObject object = assertDoesNotThrow(() -> generator.generate(random), "failed to generate object");
            assertNotNull(object.id);
            assertNotNull(object.list);
            assertEquals(3, object.list.size(), "list size is not 3");
            for (String s : object.list) {
                assertTrue(s.equals("a") || s.equals("b") || s.equals("c"), "list contains not 'a', 'b' or 'c'");
            }
        }
    }

    @SuppressWarnings("unused")
    static class TestComplexObject {
        enum Number {
            ZERO, ONE, TWO, THREE, FOUR, FIVE
        }

        private Number number;
        @RandomValue(enumValues = {"TWO", "THREE", "FIVE"})
        private Number prime;

        record TestRecord(
                @RandomValue(containerMinSize = 3, containerMaxSize = 3, stringValues = {"a", "b", "c"})
                List<String> strings,
                @RandomValue(arrayMinLength = 3, arrayMaxLength = 3, intValues = {1, 2, 3})
                int[] numbers) {}

        private TestRecord record;

        @RandomValue(containerMaxSize = 3, containerMinSize = 3, stringValues = {"a", "b", "c"})
        private List<String> list;
        @RandomValue(containerMaxSize = 3, containerMinSize = 3, byteValues = {1, 2, 3})
        private Set<Byte> set;
        @RandomValue(containerMinSize = 2, containerMaxSize = 2, arrayMinLength = 3, arrayMaxLength = 3, stringValues = {"a", "b", "c"}, intValues = {1, 2, 3})
        private Map<String, int[]> map;
    }

    @Test
    void objectComplexTest() {
        Generator<TestComplexObject> generator = Generators.of(TestComplexObject.class);
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            TestComplexObject object = assertDoesNotThrow(() -> generator.generate(random), "failed to generate object");
            assertNotNull(object.number);
            assertTrue(object.prime == TestComplexObject.Number.TWO ||
                    object.prime == TestComplexObject.Number.THREE || object.prime == TestComplexObject.Number.FIVE,
                    "prime is not TWO, THREE or FIVE");
            assertNotNull(object.record);
            assertEquals(3, object.record.strings().size(), "record strings size is not 3");
            for (String string : object.record.strings()) {
                assertTrue(string.equals("a") || string.equals("b") || string.equals("c"),
                        "record strings contains not 'a', 'b' or 'c'");
            }
            assertEquals(3, object.record.numbers().length, "record numbers size is grater that 3");
            for (int number : object.record.numbers()) {
                assertTrue(number == 1 || number == 2 || number == 3, "record numbers contains not 1, 2 or 3");
            }
            assertNotNull(object.list);
            assertEquals(3, object.list.size(), "list size is not 3");
            for (String string : object.list) {
                assertTrue(string.equals("a") || string.equals("b") || string.equals("c"),
                        "list contains not 'a', 'b' or 'c'");
            }
            assertNotNull(object.set);
            assertTrue(3 >= object.set.size(), "set size is more than 3");
            for (Byte number : object.set) {
                assertTrue(number == 1 || number == 2 || number == 3, "set contains not 1, 2 or 3");
            }
            assertNotNull(object.map);
            assertTrue(2 >= object.map.size(), "map size is more than 2");
            for (var entry : object.map.entrySet()) {
                assertTrue(entry.getKey().equals("a") || entry.getKey().equals("b") || entry.getKey().equals("c"),
                        "map contains not 'a', 'b' or 'c'");
                assertEquals(3, entry.getValue().length, "map value size is not 3");
                for (int number : entry.getValue()) {
                    assertTrue(number == 1 || number == 2 || number == 3, "map value contains not 1, 2 or 3");
                }
            }
        }
    }

    @Test
    void listTest() {
        var booleanListGenerator = Generators.ofList(Boolean.class, Generators.ofBoolean(), 5);
        var byteListGenerator = Generators.ofList(Byte.class, Generators.ofByte(), 5);
        var charListGenerator = Generators.ofList(Character.class, Generators.ofChar(), 5);
        var shortListGenerator = Generators.ofList(Short.class, Generators.ofShort(), 5);
        var intListGenerator = Generators.ofList(Integer.class, Generators.ofInt(), 5);
        var longListGenerator = Generators.ofList(Long.class, Generators.ofLong(), 5);
        var floatListGenerator = Generators.ofList(Float.class, Generators.ofFloat(), 5);
        var doubleListGenerator = Generators.ofList(Double.class, Generators.ofDouble(), 5);
        var stringListGenerator = Generators.ofList(String.class, Generators.ofString(1), 5);
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            List<Boolean> booleanList = assertDoesNotThrow(() -> booleanListGenerator.generate(random), "failed to generate list of booleans");
            assertEquals(5, booleanList.size(), "booleans list size is not 5: " + booleanList);
            List<Byte> byteList = assertDoesNotThrow(() -> byteListGenerator.generate(random), "failed to generate list of bytes");
            assertEquals(5, byteList.size(), "bytes list size is not 5: " + byteList);
            List<Character> charList = assertDoesNotThrow(() -> charListGenerator.generate(random), "failed to generate list of chars");
            assertEquals(5, charList.size(), "chars list size is not 5: " + charList);
            List<Short> shortList = assertDoesNotThrow(() -> shortListGenerator.generate(random), "failed to generate list of shorts");
            assertEquals(5, shortList.size(), "shorts list size is not 5: " + shortList);
            List<Integer> intList = assertDoesNotThrow(() -> intListGenerator.generate(random), "failed to generate list of integers");
            assertEquals(5, intList.size(), "integers list size is not 5: " + intList);
            List<Long> longList = assertDoesNotThrow(() -> longListGenerator.generate(random), "failed to generate list of longs");
            assertEquals(5, longList.size(), "longs list size is not 5: " + longList);
            List<Float> floatList = assertDoesNotThrow(() -> floatListGenerator.generate(random), "failed to generate list of floats");
            assertEquals(5, floatList.size(), "floats list size is not 5: " + floatList);
            List<Double> doubleList = assertDoesNotThrow(() -> doubleListGenerator.generate(random), "failed to generate list of doubles");
            assertEquals(5, doubleList.size(), "doubles list size is not 5: " + doubleList);
            List<String> stringList = assertDoesNotThrow(() -> stringListGenerator.generate(random), "failed to generate list of strings");
            assertEquals(5, stringList.size(), "strings list size is not 5: " + stringList);
        }
    }

    @Test
    void setTest() {
        var booleanSetGenerator = Generators.ofSet(Boolean.class, Generators.ofBoolean(), 5);
        var byteSetGenerator = Generators.ofSet(Byte.class, Generators.ofByte((byte) 100), 5);
        var charSetGenerator = Generators.ofSet(Character.class, Generators.ofChar('d'), 5);
        var shortSetGenerator = Generators.ofSet(Short.class, Generators.ofShort((short) 100), 5);
        var intSetGenerator = Generators.ofSet(Integer.class, Generators.ofInt(100), 5);
        var longSetGenerator = Generators.ofSet(Long.class, Generators.ofLong(100), 5);
        var floatSetGenerator = Generators.ofSet(Float.class, Generators.ofFloat(100F), 5);
        var doubleSetGenerator = Generators.ofSet(Double.class, Generators.ofDouble(100D), 5);
        var stringSetGenerator = Generators.ofSet(String.class, Generators.ofString(1), 5);
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            Set<Boolean> booleanSet = assertDoesNotThrow(() -> booleanSetGenerator.generate(random), "failed to generate set of booleans");
            assertTrue(booleanSet.size() <= 5, "booleans set size is more than 5: " + booleanSet);
            Set<Byte> byteSet = assertDoesNotThrow(() -> byteSetGenerator.generate(random), "failed to generate set of bytes");
            assertTrue(byteSet.size() <= 5, "bytes set size is more than 5: " + byteSet);
            for (Byte b : byteSet) {
                assertTrue(b >= 0 && b < 100, "bytes set contains value not between 0 and 100: " + b);
            }
            Set<Character> charSet = assertDoesNotThrow(() -> charSetGenerator.generate(random), "failed to generate set of chars");
            assertTrue(charSet.size() <= 5, "chars set size is more than 5: " + charSet);
            for (Character c : charSet) {
                assertTrue(c <= 'd', "chars set contains value not between '\\0' and '\\u0064': " + c);
            }
            Set<Short> shortSet = assertDoesNotThrow(() -> shortSetGenerator.generate(random), "failed to generate set of shorts");
            assertTrue(shortSet.size() <= 5, "shorts set size is more than 5: " + shortSet);
            for (Short s : shortSet) {
                assertTrue(s >= 0 && s < 100, "shorts set contains value not between 0 and 100: " + s);
            }
            Set<Integer> intSet = assertDoesNotThrow(() -> intSetGenerator.generate(random), "failed to generate set of integers");
            assertTrue(intSet.size() <= 5, "integers set size is more than 5: " + intSet);
            for (Integer integer : intSet) {
                assertTrue(integer >= 0 && integer < 100, "integers set contains value not between 0 and 100: " + integer);
            }
            Set<Long> longSet = assertDoesNotThrow(() -> longSetGenerator.generate(random), "failed to generate set of longs");
            assertTrue(longSet.size() <= 5, "longs set size is more than 5: " + longSet);
            for (Long l : longSet) {
                assertTrue(l >= 0 && l < 100, "longs set contains value not between 0 and 100: " + l);
            }
            Set<Float> floatSet = assertDoesNotThrow(() -> floatSetGenerator.generate(random), "failed to generate set of floats");
            assertTrue(floatSet.size() <= 5, "floats set size is more than 5: " + floatSet);
            for (Float f : floatSet) {
                assertTrue(f >= 0 && f < 100, "floats set contains value not between 0 and 100: " + f);
            }
            Set<Double> doubleSet = assertDoesNotThrow(() -> doubleSetGenerator.generate(random), "failed to generate set of doubles");
            assertTrue(doubleSet.size() <= 5, "doubles set size is more than 5: " + doubleSet);
            for (Double d : doubleSet) {
                assertTrue(d >= 0 && d < 100, "doubles set contains value not between 0 and 100: " + d);
            }
            Set<String> stringSet = assertDoesNotThrow(() -> stringSetGenerator.generate(random), "failed to generate set of strings");
            assertTrue(stringSet.size() <= 5, "strings set size is more than 5: " + stringSet);
        }
    }

    @Test
    void mapTest() {
        var mapGenerator = Generators.ofMap(String.class, Integer.class, Generators.ofPool(true, "a", "b", "c", "d", "e"),
                Generators.ofInt(1, 10), Integer::sum, 5);
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            Map<String, Integer> map = assertDoesNotThrow(() -> mapGenerator.generate(random), "failed to generate map");
            assertEquals(5, map.size(), "map size is not 5");
            assertTrue(map.containsKey("a"), "map does not contain 'a'");
            assertTrue(map.containsKey("b"), "map does not contain 'b'");
            assertTrue(map.containsKey("c"), "map does not contain 'c'");
            assertTrue(map.containsKey("d"), "map does not contain 'd'");
            assertTrue(map.containsKey("e"), "map does not contain 'e'");
            for (var entry : map.entrySet()) {
                assertTrue(entry.getValue() >= 1 && entry.getValue() <= 10, "map value is not between 1 and 10: " + entry.getValue());
            }
        }
    }

    @Test
    void bundlePoolTest() {
        var evenDigits = Generators.<Integer>bundlePoolBuilder(true)
                .add(0, 2, 4, 6, 8)
                .build();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            int sum = evenDigits.stream(random).limit(10).mapToInt(n -> n).sum();
            assertEquals(40, sum, "sum of 10 even digits is not 40");
        }
    }

    @Test
    void orderedPoolTest() {
        Integer[] pool = new Integer[] { 5, 3, 8, 5, 2, 9 };
        var poolGenerator = Generators.ofOrderedPool(pool);
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            Integer next = assertDoesNotThrow(() -> poolGenerator.generate(random), "failed to generate integer");
            assertEquals(pool[i % pool.length], next, "value " + next + " is not in right order");
        }
    }

    record Money(long amount) {}

    enum Rarity {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC,
        LEGENDARY
    }

    record Weapon(Rarity rarity) {}

    @Test
    void multiplePoolTest() {
        var lootTable = Generators.multiplePoolBuilder()
                .add(Generators.ofInt(100, 200).map(Money::new))
                .add(pib -> pib
                        .value(Generators.<Rarity>weightedPoolBuilder()
                                .add(pib2 -> pib2
                                        .value(Rarity.COMMON)
                                        .weight(40))
                                .add(pib2 -> pib2
                                        .value(Rarity.UNCOMMON)
                                        .weight(30))
                                .add(pib2 -> pib2
                                        .value(Rarity.RARE)
                                        .weight(20))
                                .add(pib2 -> pib2
                                        .value(Rarity.EPIC)
                                        .weight(9))
                                .add(pib2 -> pib2
                                        .value(Rarity.LEGENDARY)
                                        .weight(1))
                                .build().map((Function<Optional<Rarity>, Rarity>) Optional::orElseThrow).map(Weapon::new))
                        .condition(() -> Math.random() < 0.3))
                .build();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            List<Object> next = assertDoesNotThrow(() -> lootTable.generate(random), "failed to generate object");
            assertInstanceOf(Money.class, next.get(0), "first element is not Money");
            Money money = (Money) next.get(0);
            assertTrue(money.amount >= 100 && money.amount <= 200, "money amount is not between 100 and 200");
            if (next.size() > 1) {
                assertInstanceOf(Weapon.class, next.get(1), "second element is not Weapon");
                Weapon weapon = (Weapon) next.get(1);
                assertTrue(weapon.rarity == Rarity.COMMON || weapon.rarity == Rarity.UNCOMMON || weapon.rarity == Rarity.RARE ||
                        weapon.rarity == Rarity.EPIC || weapon.rarity == Rarity.LEGENDARY, "rarity is not in expected range");
            }
        }
    }

    @Test
    void weightedPoolTest() {
        var numbers = Generators.<Integer>weightedPoolBuilder()
                .add(pb -> pb
                        .value(0)
                        .weight(1L))
                .add(pb -> pb
                        .value(1)
                        .weight(2L))
                .add(pb -> pb
                        .value(2)
                        .weight(3L))
                .build();
        Random random = new Random();
        int[] count = new int[3];
        for (int i = 0; i < 1000; i++) {
            Optional<Integer> next = assertDoesNotThrow(() -> numbers.generate(random), "failed to generate integer");
            assertTrue(next.isPresent(), "value is not present");
            int n = next.get();
            assertTrue(n >= 0 && n <= 2, "value " + n + " is not between 0 and 2");
            ++count[n];
        }
        System.out.printf("%d, %d, %d = %s%n", 0, 1, 2, Arrays.toString(count));
    }

    static class UserInfo {
        UUID id;
        String name;
        short age;

        @SuppressWarnings("unused")
        public void setName(String name) {
            this.name = name;
        }

        public void setAge(short age) {
            this.age = age;
        }
    }

    @Test
    void objectClassTest() {
        var generator = Generators.builder(UserInfo.class)
                .field("id", UUID::randomUUID)
                .field("name", Generators.ofString(5, Generators.ofASCIIChar()))
                .field("age", Generators.ofShort((short) 100))
                .build();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            UserInfo info = generator.generate(random);
            assertNotNull(info, "null object");
            assertNotNull(info.id, "null id");
            assertNotNull(info.name, "null name");
            assertEquals(5, info.name.length(), "wrong length of name: " + info.name.length());
            assertTrue(info.age >= 0 && info.age < 100, "wrong age: " + info.age);
        }
    }

    record Color(int red, int green, int blue) {}

    @Test
    void recordTest() {
        var generator = Generators.recordBuilder(Color.class)
                .field("red", Generators.ofInt(0, 255))
                .field("green", Generators.ofInt(0, 255))
                .field("blue", Generators.ofInt(0, 255))
                .build();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            Color color = generator.generate(random);
            assertNotNull(color, "null object");
            assertTrue(color.red >= 0 && color.red <= 255, "wrong red: " + color.red);
            assertTrue(color.green >= 0 && color.green <= 255, "wrong green: " + color.green);
            assertTrue(color.blue >= 0 && color.blue <= 255, "wrong blue: " + color.blue);
        }
    }

    @Test
    void itemBuilderTest() {
        AtomicLong pickCount = new AtomicLong(), ignoreCount = new AtomicLong();
        var item = PoolItem.<Integer>builder()
                .value(5)
                .weight(10)
                .condition(() -> Math.random() < 0.3)
                .pick(pickCount::setPlain)
                .ignore(ignoreCount::setPlain)
                .build();
        var generator = Generators.<Integer>weightedPoolBuilder()
                .add(item)
                .build();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            Optional<Integer> next = assertDoesNotThrow(() -> generator.generate(random), "failed to generate integer");
            next.ifPresent(n -> assertEquals(5, n, "value is not 5"));
        }
        System.out.printf("Picked: %d, Ignored: %d%n", pickCount.get(), ignoreCount.get());
    }

    @Test
    void generatorContextTest() {
        var generator = new Generator<Integer>() {
            @Override
            public Integer generate(RandomGenerator random) {
                double luck = context(Double.class).orElse(0D);
                int next = random.nextInt(100);
                return Math.max(0, Math.min(99, (int) (next + luck * next)));
            }
        };
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            final double luck = (i % 200 - 99) / 100d;
            int next = assertDoesNotThrow(() -> generator.generateWithContext(random, luck), "failed to generate integer");
            assertTrue(next >= 0 && next <= 99, "value is not between 0 and 99: " + next);
        }
    }

    @Test
    void complexPoolTest() {
        var lootTable = Generators.multiplePoolBuilder()
                .addGenerator(Generators.ofInt(100, 200)
                        .map((a, c) -> a + (int) (a * c.context(Double.class).orElse(0D)))
                        .map(a -> Math.min(100, a))
                        .map(Money::new))
                .<Weapon>addBuilder(pib -> pib
                        .value(Generators.<Rarity>weightedPoolBuilder()
                                .addBuilder(pib2 -> pib2
                                        .value(Rarity.COMMON)
                                        .withContext((pib3, ctl) -> pib3
                                                .weight(() -> Math.min(40, (long) (40 - ctl.context(Double.class).orElse(0D) * 40)))))
                                .addBuilder(pib2 -> pib2
                                        .value(Rarity.UNCOMMON)
                                        .withContext((pib3, ctl) -> pib3
                                                .weight(() -> Math.min(30, (long) (30 - ctl.context(Double.class).orElse(0D) * 30)))))
                                .addBuilder(pib2 -> pib2
                                        .value(Rarity.RARE)
                                        .withContext((pib3, ctl) -> pib3
                                                .weight(() -> Math.min(50, (long) (20 + ctl.context(Double.class).orElse(0D) * 30)))))
                                .addBuilder(pib2 -> pib2
                                        .value(Rarity.EPIC)
                                        .withContext((pib3, ctl) -> pib3
                                                .weight(() -> Math.min(30, (long) (9 + ctl.context(Double.class).orElse(0D) * 21)))))
                                .addBuilder(pib2 -> pib2
                                        .value(Rarity.LEGENDARY)
                                        .withContext((pib3, ctl) -> pib3
                                                .weight(() -> Math.min(20, (long) (1 + ctl.context(Double.class).orElse(0D) * 19)))))
                                .build().map(opt -> opt.orElseThrow()).map(Weapon::new))
                        .withContext((pib2, ctl) -> pib2
                                .condition(() -> Math.random() < 0.3 + ctl.context(Double.class).orElse(0D) * 0.3)))
                .build();
        Random random = new Random();
        long totalCount = 0;
        TreeMap<Double, HashMap<Rarity, Integer>> count = new TreeMap<>();
        for (int i = 0; i < 10000; i++) {
            final double luck = (i % 100 + 1) / 100d;
            List<Object> next = assertDoesNotThrow(() -> lootTable.generateWithContext(random, luck), "failed to generate object");
            assertInstanceOf(Money.class, next.get(0), "first element is not Money");
            Money money = (Money) next.get(0);
            assertTrue(money.amount >= 100 && money.amount <= 400, "money amount is not between 100 and 400");
            if (next.size() > 1) {
                assertInstanceOf(Weapon.class, next.get(1), "second element is not Weapon");
                Weapon weapon = (Weapon) next.get(1);
                assertTrue(weapon.rarity == Rarity.COMMON || weapon.rarity == Rarity.UNCOMMON || weapon.rarity == Rarity.RARE ||
                        weapon.rarity == Rarity.EPIC || weapon.rarity == Rarity.LEGENDARY, "rarity is not in expected range");
                ++totalCount;
                if (!count.containsKey(luck)) {
                    count.putIfAbsent(luck, new HashMap<>());
                }
                count.get(luck).merge(weapon.rarity, 1, Integer::sum);
            }
        }
        System.out.println("Total weapons: " + totalCount);
        System.out.println("Average chance:");
        count.forEach((luck, chance) -> {
            double luckCount = chance.values().stream().mapToInt(i -> i).sum();
            System.out.printf("Luck %.2f: COMMON - %.0f%%,\tUNCOMMON - %.0f%%,\tRARE - %.0f%%,\tEPIC - %.0f%%,\tLEGENDARY - %.0f%%%n",
                    luck, chance.getOrDefault(Rarity.COMMON, 0) / luckCount * 100,
                    chance.getOrDefault(Rarity.UNCOMMON, 0) / luckCount * 100,
                    chance.getOrDefault(Rarity.RARE, 0) / luckCount * 100,
                    chance.getOrDefault(Rarity.EPIC, 0) / luckCount * 100,
                    chance.getOrDefault(Rarity.LEGENDARY, 0) / luckCount * 100);
        });
    }
}
