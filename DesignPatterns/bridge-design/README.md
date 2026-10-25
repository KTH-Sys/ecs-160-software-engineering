# Bridge design pattern

Study example based on **DesignPatterns.pdf, slides 52–59**. This simplified code and explanation were created for studying; they are not the professor's original code. The `move()` and `supplyPower()` methods add printed actions so you can trace the relationship shown in the lecture diagrams.

## Problem statement

Vehicles vary in two independent ways:

- Vehicle type: Car, Motorcycle, Bicycle, and so on.
- Energy source: Electric, Gas, Diesel, Manual, and so on.

We want to add vehicle types and energy sources without creating a new vehicle class for every combination. This runnable example uses two vehicle types and two energy sources.

## Naïve design: a class for every combination

```text
Vehicle
├── ElectricCar
├── GasCar
├── ElectricMotorcycle
└── GasMotorcycle
```

Adding diesel requires `DieselCar` and `DieselMotorcycle`. Adding another vehicle type creates more combination classes. This growth is the class explosion shown in slide 55.

## Analogy: choose a vehicle body and a power system

Think of assembling a vehicle from two separately chosen parts. A car body remains a car body whether its power system is electric or gas.

Bridge models the two choices separately:

```text
Vehicle hierarchy                  EnergySource hierarchy
Car                                Electric
Motorcycle        ── has-a ──→      Gas
```

The vehicle holds a reference to an energy-source object and delegates power-related operations to it.

## How the code works

[BridgeExample.java](BridgeExample.java) contains the complete example in one file.

### 1. Store an energy-source reference in Vehicle

```java
class Vehicle {
    private EnergySource energySource;

    public Vehicle(EnergySource source) {
        this.energySource = source;
    }

    public void move() {
        energySource.supplyPower();
    }
}
```

`source` is a constructor parameter. The constructor saves a reference to the supplied object; it does not create or copy an energy source. The field accepts any object that is an EnergySource, including Electric and Gas instances.

### 2. Keep vehicle types in their own hierarchy

Car and Motorcycle extend Vehicle. Their constructors use `super(source)` to pass the selected energy source to the Vehicle constructor.

Their overridden `move()` methods first call `super.move()`, which supplies power through the selected source, then print the vehicle's own action.

`super(source)` calls a parent constructor. `super.move()` calls a parent method.

### 3. Keep energy sources in a separate hierarchy

The abstract EnergySource class declares `supplyPower()`. Electric and Gas each override it with their own implementation.

The role names are:

| Bridge role | Code |
| --- | --- |
| Abstraction: higher-level operations | Vehicle and its subclasses |
| Implementation: the mechanism those operations use | EnergySource and its subclasses |
| Connection between the hierarchies | Vehicle's `energySource` field |

Here, "abstraction" names a role in the pattern. It does not require the Vehicle class to use Java's `abstract` keyword.

### 4. Combine objects when constructing a vehicle

```java
Vehicle electricCar = new Car(new Electric());
Vehicle gasCar = new Car(new Gas());
```

These use the same Car class. Each Car object holds its own selected energy-source reference:

```text
electricCar → Car object → Electric object
gasCar      → Car object → Gas object
```

Calling `electricCar.move()` runs:

```text
Car.move()
    → Vehicle.move()
        → Electric.supplyPower()
    → print "Car moves"
```

Runtime polymorphism selects Car's `move()` through the Vehicle reference and Electric's `supplyPower()` through the EnergySource reference.

## Why use Bridge?

- Add a new energy source without creating a vehicle subclass for every combination.
- Add a new vehicle type that can use existing energy sources.
- Keep the vehicle and energy-source implementations separate so each can evolve independently.

For example, adding this energy-source class lets existing Car and Motorcycle classes use diesel:

```java
class Diesel extends EnergySource {
    @Override
    public void supplyPower() {
        System.out.println("Using diesel");
    }
}
```

You can then construct `new Car(new Diesel())` without adding a DieselCar class. This example selects the energy source at construction time.

The flexible relationship does not automatically reject unwanted vehicle–energy combinations. If the application needs restrictions, it must enforce them separately.

## Connection to Proxy

Both designs hold references to other objects and delegate work. Proxy wraps a subject and preserves its interface to control access. Bridge separates two independently varying parts: a vehicle and its energy source.

## Run the example

With Java 11 or later, run from this folder:

```sh
java BridgeExample.java
```

Expected output:

```text
Using electricity
Car moves
Using gas
Car moves
Using electricity
Motorcycle moves
Using gas
Motorcycle moves
```

## Check your understanding

1. Why can the same Car class represent both electric and gas cars?
2. What does `super(source)` pass to the Vehicle constructor?
3. Which class would you add to support hydrogen power without creating HydrogenCar and HydrogenMotorcycle classes?
