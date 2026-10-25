/*
 * Study example based on DesignPatterns.pdf, slides 52–59.
 * Separate vehicle types from energy sources instead of defining ElectricCar,
 * GasCar, ElectricMotorcycle, and GasMotorcycle as separate classes.
 * Run: java BridgeExample.java
 */
public class BridgeExample {
    public static void main(String[] args) {
        // Choose the vehicle and energy source independently.
        Vehicle electricCar = new Car(new Electric());
        Vehicle gasCar = new Car(new Gas());
        Vehicle electricMotorcycle = new Motorcycle(new Electric());
        Vehicle gasMotorcycle = new Motorcycle(new Gas());

        electricCar.move();
        gasCar.move();
        electricMotorcycle.move();
        gasMotorcycle.move();
    }
}

// ABSTRACTION: the vehicle side of the design.
// "Abstraction" is a pattern role; Vehicle does not have to be an abstract class.
class Vehicle {
    // This reference connects the two hierarchies: a Vehicle HAS an EnergySource.
    private EnergySource energySource;

    public Vehicle(EnergySource source) {
        // Save the supplied object's reference, without copying it.
        this.energySource = source;
    }

    public void move() {
        // Delegate power-related work to the selected energy-source object.
        energySource.supplyPower();
    }
}

class Car extends Vehicle {
    public Car(EnergySource source) {
        super(source); // Pass the energy source to the Vehicle constructor.
    }

    @Override
    public void move() {
        super.move(); // Run Vehicle.move(), which uses the selected energy source.
        System.out.println("Car moves");
    }
}

class Motorcycle extends Vehicle {
    public Motorcycle(EnergySource source) {
        super(source);
    }

    @Override
    public void move() {
        super.move();
        System.out.println("Motorcycle moves");
    }
}

// IMPLEMENTATION: a separate hierarchy for how power is supplied.
abstract class EnergySource {
    public abstract void supplyPower();
}

class Electric extends EnergySource {
    @Override
    public void supplyPower() {
        System.out.println("Using electricity");
    }
}

class Gas extends EnergySource {
    @Override
    public void supplyPower() {
        System.out.println("Using gas");
    }
}

/*
 * electricCar.move()
 *     -> Car.move()
 *         -> Vehicle.move()
 *             -> Electric.supplyPower()
 *         -> print "Car moves"
 *
 * The electric car uses TWO objects: one Car and one Electric.
 * A gas car uses the SAME Car class with a different energy-source object.
 * Adding a Diesel energy source does not require a DieselCar class.
 */
