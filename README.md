# Random
## Overview
This is small library for generating random things. It was designed to be used in 1 line of code. The main class is
`Generators`, which provides a lot of static methods for creating generators. All generators implement the `Generator`
interface. It contains only 1 method to implement: `<T> generate(java.util.Random)`.

## Installation
### Maven
```xml
<dependency>
    <groupId>io.github.systemfalse</groupId>
    <artifactId>random-lib</artifactId>
    <version>1.2.0</version>
</dependency>
```

## Usage
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
List<String> strings = ...
var randString = Generators.ofInt(strings.size()).map(strings::get);
Random rnd = new Random();
for(int i = 0; i < 10; i++){
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