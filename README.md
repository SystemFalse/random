# Random Library v1.2.2
## Overview
This is small library for generating random things. It was designed to be used in 1 line of code. The main class is
`Generators`, which provides a lot of static methods for creating generators. All generators implement the `Generator`
interface. It contains only 1 method to implement: `<T> generate(java.util.RandomGenerator)`.

## Installation
### Maven
```xml
<dependency>
    <groupId>io.github.systemfalse</groupId>
    <artifactId>random-lib</artifactId>
    <version>1.2.2</version>
</dependency>
```

## Usage
### Primitives
Class `io.github.system_false.random.Generators` provides static methods for creating generators of all primitives,
strings, arrays, lists, sets and maps. To create any instance of the `Generator`, you need to invoke one of methods
named as `of<type>()`.
```java
//printing 10 random integers
var randInt = Generators.ofInt();
for (int i = 0; i < 10; i++) {
    System.out.println(randInt.generate());
}
```
Class `Generator` also has mapping methods. They can be used to generate random values from other generators.
```java
//selecting random string from list
List<String> strings = List.of(/*your data*/);
var randString = Generators.ofInt(strings.size()).map(strings::get);
Random rnd = new Random();
for(int i = 0; i < 10; i++) {
    System.out.println(randString.generate(rnd));
}
```
Besides simple types, this library also provides generators for any `class` via reflection. Annotation `@RandomValue`
sets random parameters of the object. It can also be applied to `record` components and parameters of constructors and
methods. Annotation `@RandomCreator` specifies how to create random object.
```java
//person describing class
class Person {
    @RandomValue(stringValues = {"John", "Jane"})
    private String name;
    @RandomValue(intMinValue = 24, intMaxValue = 36)
    private int age;

    @RandomCreator
    public Person() {
    }
}

//person generator
var randPerson = Generators.of(Person.class);
System.out.println(randPerson.generate());
```
Weighted pools are also supported. They can be created using `ofPool` and `ofMultiplePool` methods. First one returns
only 1 value at 1 call, second one randomizes all values and returns all of them at 1 call. Pool can contain another
pools.
```java
//loot table
Random rnd = new Random();
var loot = Generators.ofMultiplePool(Generators.ofInt(100, 200),
        Generators.item(Generators.ofValue("sword"), () -> rnd.nextBoolean() < 0.1));
System.out.println(loot.generate(rnd));
```

### Builders
#### Overview
Library also provides builders for creating generators. They can be used to create `ObjectGenerator`, `RecordGenerator`,
`PoolGenerator` and `PoolItem` objects.

#### Object and record generators
To create an object or record generator builder, use `Generators.builder()` or `Generators.recordBuilder()` methods.
They can create generators of objects of normal classes and records, respectively. The difference between them is that
the usual constructor of object generators cannot initialize the final fields, and in the constructor of record
generators all fields must be initialized before calling the `build()` method.

Object class example:
```java
class UserInfo {
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

var generator = Generators.builder(UserInfo.class)
        .field("id", UUID::randomUUID)
        .field("name", Generators.ofString(5, Generators.ofASCIIChar()))
        .field("age", Generators.ofShort((short) 100))
        .build();
```
Here `generator` will produce `UserInfo` objects with random `id`, `name` and `age`.

Record example:
```java
record Color(int red, int green, int blue) {}

var generator = Generators.recordBuilder(Color.class)
        .field("red", Generators.ofInt(0, 255))
        .field("green", Generators.ofInt(0, 255))
        .field("blue", Generators.ofInt(0, 255))
        .build();
```
Here `generator` will produce `Color` objects with random `red`, `green` and `blue`.

#### Pools
To create a pool generator, use `Generators.bundlePoolBuilder()`, `Generators.orderedPoolBuilder()`,
`Generators.weightedPoolBuilder()` or `Generators.multiplePoolBuilder()` methods.

##### Bundle pool
A bundle pool is a default pool that returns values in random order. However, the values cannot be selected twice until
all the values from the list are selected once. This version of the generator ignores the conditions and the weights of
the elements in it. This pool will call the `Police.picked()` method only for the selected item and the
`PoolItem.ignored()` method for all other items.

Example:
```java
var evenDigits = Generators.<Integer>bundlePoolBuilder(true)
        .add(0, 2, 4, 6, 8)
        .build();
Random random = new Random();
int sum = evenDigits.stream(random).limit(10).mapToInt(n -> n).sum();
assertEquals(40, sum, "sum of 10 even digits is not 40");
```
Here assertion will never fail because all digits will be selected twice.

##### Ordered pool
An ordered pool is a pool that always returns values in the order in which they were added to it. This can be useful if
the values stored in it are also randomized. This version of the generator ignores the conditions and weights of the
elements in it. This pool will call the `Police.picked()` method only for the selected item and the
`PoolItem.ignored()` method for all other items.

Example:
```java
Integer[] pool = new Integer[] { 5, 3, 8, 5, 2, 9 };
var poolGenerator = Generators.ofOrderedPool(pool);
Random random = new Random();
for (int i = 0; i < 1000; i++) {
    Integer next = assertDoesNotThrow(() -> poolGenerator.generate(random), "failed to generate integer");
    assertEquals(pool[i % pool.length], next, "value " + next + " is not in right order");
}
```
Here assertion will never fail because all digits will be returned as they were added.

##### Weighted pool
A weighted pool is a pool that returns a random element based on the weight of each element and the conditions that are
set in them. If, when calculating the weight, it turns out that it is equal to or less than 0, then `Optional.empty()`
will be returned. This pool will call the `Police.picked()` method only for the selected item and the
`PoolItem.ignored()` method for all other items.

Example:
```java
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
```
Here, the `numbers` generator will return two 3 times more often than zero, and one 2 times more often than zero.

##### Multiple pool
A multiple pool is a pool that returns a list of generated objects. Each element in the pool will be added to the output
list if its condition is satisfied. Also, if the `unwrapCollection` flag is true, then all the elements of the
collections generated by the list items will be added to the output list separately. This feature allows you to create
complex pools based on other pools.

Example:

```java
record Money(long amount) {}
enum Rarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY
}
record Weapon(Rarity rarity) {}

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
                        .build().map(Optional::orElseThrow).map(Weapon::new))
                .condition(() -> Math.random() < 0.3))
        .build();
```
Here the `lootTable` will generate list of 1 or 2 elements. First element will be `Money` with random amount between
100 and 200. Second element can be `Weapon` with 30% chance. It can be common (40% chance), uncommon (30% chance),
rare (20% chance), epic (9% chance) or legendary (1% chance).

#### Items
To create `PoolItem` objects, use `PoolItemBuilder` class. It can be instantiated using `PoolItem.builder()` method.
This builder can configure value, weight, condition, pick action and ignore action. Item is picked when it is selected
in the pool and ignored when it is not selected.

Value of the item can be set using constant value, `Supplier<T>`, `Generator<T>` or
`BiFunction<Long, Long, Generator<T>>`. Last one is function that returns generator that depends on the number of times
the item was picked and ignored.

Weight of the item can be set using constant value, `LongSupplier`, `LongBinaryOperator` or
`BiFunction<Long, Long, Long>`. Last one is function that returns weight that depends on the number of times the item
was picked and ignored.

Condition of the item can be set using `BooleanSupplier` or `BiFunction<Long, Long, Boolean>`. Last one is function that
returns condition that depends on the number of times the item was picked and ignored.

Pick action of the item can be set using `Runnable`, `LongConsumer` or `LongUnaryOperator`. Last one is function that
returns pick action that depends on the number of times the item was picked and returns new number of times.

Ignore action of the item can be set using `Runnable`, `LongConsumer` or `LongUnaryOperator`. Last one is function that
returns ignore action that depends on the number of times the item was ignored and returns new number of times.

Example:
```java
var item = PoolItem.<Integer>builder()
        .value(5)
        .weight(10)
        .condition(() -> Math.random() < 0.3)
        .pick(() -> System.out.println("item was picked"))
        .ignore(() -> System.out.println("item was ignored"))
        .build();
```
Here `item` will always return 5, it has weight 10 and will be picked with 30% probability.