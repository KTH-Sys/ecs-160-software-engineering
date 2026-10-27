# Facade design pattern

Study notes based on **DesignPatterns.pdf, slides 60–64**. The explanation, analogy, and simplified pseudocode were created for studying; they are not the professor's original code.

## Problem statement

A transpilation library converts code from one language into another, such as C to Rust. It contains multiple components: scanners, parsers, program-node builders, and output components.

Without a simpler entry point, every application using the library must understand which objects to create, how to connect them, and the order of operations.

We want the application to request transpilation through a simple API while the library handles the internal workflow.

## Analogy: ordering a meal at a restaurant

You tell the waiter what meal you want. The waiter coordinates with the kitchen, drinks station, and cashier. You do not have to visit each department or know their internal workflow.

The waiter acts as a facade: one convenient point of contact for several internal services.

| Restaurant analogy | Course example |
| --- | --- |
| Customer | Client application |
| Waiter | CompilerFacade |
| Kitchen, drinks station, cashier | Transpiler subsystem components |
| Ordering a meal | Calling `transpile(source)` |

## Without a facade

```text
Application → Scanner
Application → Parser
Application → Program-node builder
Application → Output components
```

The client must coordinate these components itself. This makes the library harder to use and couples the application to internal details. If a component's API changes, client code using it directly may also need to change.

## With a facade

The library provides a CompilerFacade with a high-level operation. An illustrative client call is:

```java
CompilerFacade compiler = new CompilerFacade();
String rustCode = compiler.transpile(cCode);
```

The client now interacts with the facade:

```text
Application → CompilerFacade.transpile(source)
                    → coordinates subsystem components
                    → returns the result
```

The original components still perform their jobs. The facade handles orchestration: calling them in the required way and order and passing results between them.

## What happens inside?

This simplified pseudocode illustrates the responsibility of the facade:

```text
transpile(source):
    tokens = scanner.scan(source)
    program = parser.parse(tokens)
    result = generator.generate(program)
    return result
```

The lecture diagram includes additional components such as ProgramNodeBuilder, ProgramNode, BytecodeStream, and ObjectWriter. The pseudocode condenses that diagram to show the central idea rather than implementing a real transpiler.

## Formal definition

Facade provides a unified interface to a set of interfaces in a subsystem, making the subsystem easier to use.

Here, "interface" means the operations available to callers; the pattern does not require Java's `interface` keyword. A facade can be an ordinary class with public methods. It is a structural pattern because it organizes how clients and subsystem objects interact.

## Why use it?

| Benefit | Meaning |
| --- | --- |
| Easier use | Clients request `transpile()` instead of managing many components |
| Fewer objects to manage | Clients work through a convenient entry point |
| Less dependency on internal details | The facade knows the subsystem workflow |
| Localized maintenance | Internal API changes can be handled inside the facade while its public contract stays stable |

For example, if the scanner changes its API, the library maintainer adjusts how CompilerFacade uses it. Client applications can keep calling `transpile()` if that public contract remains unchanged.

The facade offers a simpler route into the subsystem. It does not inherently make every subsystem class private; a library may still expose lower-level operations for advanced uses.

## Connection to Proxy

| Pattern | Main purpose | Example |
| --- | --- | --- |
| Proxy | Stand in for a subject and control access while preserving its interface | Log before forwarding `walk()` to the original Person |
| Facade | Simplify interaction with a subsystem | Coordinate scanning, parsing, and output through `transpile()` |

The purpose distinguishes these patterns. A facade is useful because callers no longer need to coordinate the subsystem themselves.

## What to remember

**One simple public operation can coordinate a complex internal workflow.** The facade reduces what the client must know while leaving the actual subsystem work in its component classes.

## Check your understanding

1. Why does a facade make a complex library easier to use?
2. If the scanner's API changes but `transpile()` keeps its contract, where should the adjustment happen?
3. Why is a CompilerFacade different from a PersonProxy?
