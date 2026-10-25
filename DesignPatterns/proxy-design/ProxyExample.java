/*
 * Study example based on DesignPatterns.pdf, slides 46–51.
 * Person simulates a library class that we cannot modify.
 * Run: java ProxyExample.java
 */
public class ProxyExample {
    public static void main(String[] args) {
        Person realPerson = new Person();

        // Like putting a logging box in front of an existing printer.
        Person proxy = new PersonProxy(realPerson);
        animate(proxy);

        // To try without logging, replace animate(proxy) with animate(realPerson).
    }

    // p receives a reference to the supplied object. No object is created here.
    // This function works with either the original Person or its proxy.
    static void animate(Person p) {
        p.walk();
        p.talk();
    }
}

// ORIGINAL OBJECT: performs the actual work, like the physical printer.
class Person {
    public void walk() {
        System.out.println("Walking");
    }

    public void talk() {
        System.out.println("Talking");
    }
}

// PROXY: receives calls first, logs them, then forwards them to the original object.
// Extending Person lets existing code accept this proxy as a Person.
class PersonProxy extends Person {
    private Person person;

    public PersonProxy(Person person) {
        // Save the existing object's reference; do not copy it or create another Person.
        this.person = person;
    }

    @Override
    public void walk() {
        log();
        person.walk();
    }

    @Override
    public void talk() {
        log();
        person.talk();
    }

    private void log() {
        System.out.println("Logging call");
    }
}

/*
 * Call flow for walking:
 * animate(proxy)
 *     -> p.walk(), where p refers to the PersonProxy object
 *     -> PersonProxy.walk()
 *         -> log()
 *         -> person.walk() on the original Person object
 *
 * Two objects exist: the original Person and its PersonProxy.
 * Only calls through the proxy receive this logging.
 */
