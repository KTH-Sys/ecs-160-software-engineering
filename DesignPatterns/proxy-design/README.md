# Proxy design pattern

Study example based on **DesignPatterns.pdf, slides 46–51**. This simplified code and explanation were created for studying; they are not the professor's original code. The example uses consistent `void walk()` declarations to correct the slide's mismatched return types.

## Problem statement

The application already calls `walk()` and `talk()` on Person objects. We want to log calls to selected objects, but:

- The Person class comes from a library we cannot modify.
- We want existing functions such as `animate(Person p)` to keep their method signatures and calls.
- We want to use the particular original Person object, which may already exist.

A Proxy stands in for the original object. It preserves the interface callers expect, intercepts calls, and forwards them to the original object after performing the logging.

## Analogy: a campus printer

Normally, a laptop sends a print request directly to a printer:

```text
Laptop → Printer
```

The university wants to record requests without changing the printer's software or every application that prints. It places a logging box in front of the printer:

```text
Laptop → Logging box → Printer
```

The laptop still requests printing. The box records the request, then forwards it. The original printer still performs the printing.

| Analogy | Code |
| --- | --- |
| Laptop requests an operation | `animate()` calls `walk()` and `talk()` |
| Logging box | `PersonProxy` |
| Recording a request | `log()` |
| Actual printer | Original `Person` object |

## How the code works

[ProxyExample.java](ProxyExample.java) contains the entire example in one file. Person simulates the library class; its methods simply print their actions.

First, create the original object:

```java
Person realPerson = new Person();
```

Then create the proxy and give it a reference to that object:

```java
Person proxy = new PersonProxy(realPerson);
```

The proxy constructor saves that reference in its `person` field. It does not create or copy the wrapped Person.

```text
proxy → PersonProxy object
            person → original Person object
```

There are two objects. Two parts of the proxy serve different purposes:

| Part | Purpose |
| --- | --- |
| `extends Person` | Allows the proxy to be used wherever a Person is expected |
| `private Person person` | Holds the original object to which calls are forwarded |

Call the existing function with the proxy:

```java
animate(proxy);

static void animate(Person p) {
    p.walk();
    p.talk();
}
```

The parameter `p` receives a reference to the proxy. Passing it does not create another object. Runtime polymorphism selects the proxy's overridden methods.

The call to `p.walk()` follows this route:

```text
animate(proxy)
    → PersonProxy.walk()
        → log()
        → original Person.walk()
```

`talk()` follows the same structure. The proxy receives the call first, while the original object performs the action. Wrapping lets the proxy use an existing object's behavior and state without copying its fields.

## What do we gain?

Logging is centralized in the proxy. The original class and existing caller functions retain their implementations. During setup, we choose whether to supply the original object or a proxy around it.

Only calls through the proxy receive its logging. Calling `animate(realPerson)` directly bypasses the proxy.

## Proxy compared with Adapter

| Pattern | Intent | Example call flow |
| --- | --- | --- |
| Adapter | Translate an incompatible interface into the interface the client expects | `pay()` → `makePayment()` |
| Proxy | Control access while preserving the subject's interface | `walk()` → logging → original `walk()` |

Both examples wrap an object and delegate to it. Their purpose and interface relationships differ.

## Run the example

With Java 11 or later, run from this folder:

```sh
java ProxyExample.java
```

Expected output:

```text
Logging call
Walking
Logging call
Talking
```

To try the original object directly, replace `animate(proxy)` in `main` with `animate(realPerson)`. The output becomes:

```text
Walking
Talking
```

## Check your understanding

1. Why can a function expecting a Person accept a PersonProxy?
2. Why does the proxy hold a reference to the original Person?
3. Why does calling `animate(realPerson)` bypass logging?
