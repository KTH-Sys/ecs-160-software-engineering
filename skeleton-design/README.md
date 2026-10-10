# Logger design skeleton

Study examples based on **DesignPatterns.pdf, slides 16–22**. These explanations and simplified examples were created for studying; they are not the professor's original code.

| File | Course slides | Design |
| --- | --- | --- |
| [NaiveLogger.java](NaiveLogger.java) | 16–19 | Pass the Logger through function parameters |
| [StaticFieldLogger.java](StaticFieldLogger.java) | 20–22 | Store the Logger reference in a public static field |

## Problem statement

An application has hundreds of classes that need to record events in a log file. Design a Logger so that:

1. At most one Logger object exists, shared throughout the application.
2. The Logger is created when the first message needs to be logged (lazy initialization).
3. Any part of the application can access the shared Logger.

The course example names the log file using the application's date and launch time. These simplified examples print messages to the console so you can focus on object creation and access.

## Naïve design

Create one Logger in `main`, then pass its reference to functions that need it:

```text
main → checkout(logger) → saveOrder(logger)
```

In `checkout(Logger logger)`, `Logger` is the parameter's type and `logger` is its local name. Calling `checkout(logger)` passes a reference to the existing object; it does not create another object.

The normal call chain successfully shares one Logger. The design has two remaining problems:

- The reference must be passed manually through parameters and callers. In a large application, adding logging can require many method changes.
- The public constructor still allows someone to create another Logger.

The file's `saveOrderWithProblem` method demonstrates the second problem:

```java
static void saveOrderWithProblem(Logger logger) {
    Logger logger2 = new Logger();
    logger2.log("Order saved");
}
```

The parameter refers to the original object. `new Logger()` creates a second object, and the message goes through that second object instead. Java accepts this code even though the application requires only one Logger.

**Using one object in the normal call chain does not enforce the one-object requirement.**

## Static field design

Store a reference in a field belonging to the Logger class:

```java
public static Logger logger = null;
```

- `public` allows other code to access and assign the field.
- `static` makes it a class field shared across instances.
- `Logger` is the field's type; `logger` is its name.
- `null` means it initially refers to no object. The slides use `nullptr`; Java uses `null`.

The declaration creates no Logger object. Creation happens here:

```java
Logger.logger = new Logger();
```

Any function can then use `Logger.logger.log(...)`. Read this as: get the reference from the Logger class's `logger` field, then call `log` on that object. The `log` method is an instance method.

The call chain no longer needs Logger parameters:

```text
main → checkout() → saveOrder()
```

However, `saveOrderWithProblem` shows that the shared reference can be replaced:

```java
static void saveOrderWithProblem() {
    Logger logger2 = new Logger();
    Logger.logger = logger2;
    Logger.logger.log("Order saved");
}
```

The first line creates another object. The second makes the shared field point to it:

```text
Before: Logger.logger → original Logger object
After:  Logger.logger → new Logger object
```

If other code saved a reference to the original object, that reference still points to the original. Assigning the static field does not change every reference or destroy the old object.

**One static field does not enforce one Logger object.** The constructor remains public, and the shared field can be reassigned.

## What remains unresolved?

| Concern | Naïve design | Static field design |
| --- | --- | --- |
| Access to the logger | Pass a reference through parameters | Use `Logger.logger` after initialization |
| Prevent extra Logger objects | Not enforced | Not enforced |
| Prevent replacement of the shared field | No shared field | Not enforced |
| Lazy initialization | Not enforced; created directly in `main` | Not enforced; created directly in `main` |

For a Logger that opens a file in its constructor, creating another object opens another file stream and can split messages across files. These console examples show the object operations in the code rather than printing diagnostic labels.

Slide 22 emphasizes avoiding hidden rules. The static field design still relies on callers to initialize the field first, avoid extra objects, and avoid replacing the reference. Calling `Logger.logger.log(...)` while the field is `null` throws a `NullPointerException`.

## Run the examples

With Java 11 or later, run each file separately from this folder. Each standalone example defines its own `Logger` class.

```sh
java NaiveLogger.java
```

```sh
java StaticFieldLogger.java
```

Both normal examples print:

```text
Checkout started
Order saved
```

The problem methods are shown separately and are not called by default. To try one, change the call inside `checkout`:

- Naïve design: replace `saveOrder(logger)` with `saveOrderWithProblem(logger)`.
- Static field design: replace `saveOrder()` with `saveOrderWithProblem()`.

The message text stays the same even though the code creates another Logger. Printed messages alone do not prove that one object is being shared.

## Check your understanding

1. Why does passing a Logger parameter not create another object?
2. Which statement in each problem method creates a second object?
3. Why does the static field design no longer need Logger parameters?
4. What does `Logger.logger = logger2` change?
5. Why do both designs still permit violations of the one-object requirement?
