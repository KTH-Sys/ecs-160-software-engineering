# Abstract Factory: naïve approach and factory design

Study examples based on **DesignPatterns.pdf, slides 29–35**. This simplified code and explanation were created for studying; they are not the professor's original code.

| File | Design |
| --- | --- |
| [NaiveUI.java](NaiveUI.java) | Check the platform separately for each component; includes a separate problem method |
| [AbstractFactoryUI.java](AbstractFactoryUI.java) | Choose a platform-specific factory once, then ask it for matching components |

## Problem statement

Build an application that supports Windows and macOS UI components. Each platform supplies a matching family of components:

| Platform | Button | Textbox |
| --- | --- | --- |
| Windows | `WinButton` | `WinTextBox` |
| macOS | `MacOSButton` | `MacOSTextBox` |

On Windows, create Windows components. On macOS, create macOS components. The application should use a consistent family and should be easy to extend to another platform.

The lecture also includes a dropdown. This example uses only a button and textbox to keep the code small. The `draw()` methods print component names instead of opening an actual GUI.

## Naïve approach

In [NaiveUI.java](NaiveUI.java), `drawGUI` checks the platform separately for each component:

```java
Button button;
if (platform.equals("win")) {
    button = new WinButton();
} else {
    button = new MacOSButton();
}

Textbox textbox;
if (platform.equals("win")) {
    textbox = new WinTextBox();
} else {
    textbox = new MacOSTextBox();
}
```

This normal method assumes the platform is either `"win"` or `"macos"`. In Java, `equals` compares string contents; the lecture's sample syntax has been corrected here.

`Button` and `Textbox` are abstract parent types. The platform-specific subclasses provide their own `draw()` implementations. For example, a `Button` variable holding a `WinButton` runs `WinButton.draw()` when `button.draw()` is called.

The normal code works for the two supported platforms. It is called naïve because platform decisions are repeated for every component. Each new component adds another check, and adding a platform requires changing all those checks.

## The problem: a platform is handled incompletely

The separate `drawGUIWithProblem` method demonstrates the omission shown in slide 34:

1. Linux support is added to the button branches, creating a `GnomeButton`.
2. Linux support is forgotten in the textbox branches.
3. On Linux, neither textbox branch runs, so `textbox` remains `null`.
4. The Linux button draws successfully.
5. `textbox.draw()` throws a `NullPointerException` because there is no textbox object.

The code compiles: `textbox` has been initialized to `null`. The failure appears when the Linux path runs.

A different omission is also possible: if you leave the original textbox's plain `else` unchanged when adding Linux elsewhere, it creates a macOS textbox on Linux. That produces a mixed family instead of the missing textbox shown here.

**Repeated platform checks make it easy to update one component and forget another.** Abstract Factory is the next design introduced by the course to organize the creation of related components.

## Abstract Factory design

An Abstract Factory provides methods for creating a family of related objects without making the application name each concrete component class.

The abstract factory specifies what every concrete factory must provide:

```java
abstract class UIFactory {
    public abstract Button createButton();
    public abstract Textbox createTextbox();
}
```

`Button` and `Textbox` are the return types. The slides show `void` in some creation methods, but a method returning a component must declare its component return type, as corrected here.

Each platform implements these methods for its own family:

| Factory | `createButton()` returns | `createTextbox()` returns |
| --- | --- | --- |
| `WinFactory` | A new `WinButton` | A new `WinTextBox` |
| `MacOSFactory` | A new `MacOSButton` | A new `MacOSTextBox` |

For example:

```java
class WinFactory extends UIFactory {
    public Button createButton() {
        return new WinButton();
    }

    public Textbox createTextbox() {
        return new WinTextBox();
    }
}
```

The Application constructor chooses a factory once and stores it in a `UIFactory` field. Its `drawGUI()` method then uses that field:

```java
Button button = uiFactory.createButton();
Textbox textbox = uiFactory.createTextbox();

button.draw();
textbox.draw();
```

These lines do not check the platform. Runtime polymorphism selects the creation methods belonging to the actual factory object.

For Windows, the execution is:

1. `new Application("win")` creates an Application whose factory field refers to a `WinFactory`.
2. Creating the factory does not yet create a button or textbox.
3. `uiFactory.createButton()` calls the Windows factory method, creating a `WinButton`.
4. `uiFactory.createTextbox()` creates a `WinTextBox`.
5. Both components draw using their Windows implementations.

The application uses the parent types `UIFactory`, `Button`, and `Textbox`. Platform-specific construction happens inside the concrete factories. Each creation call makes a new component; Abstract Factory does not make these objects singletons.

## Adding Linux

With the factory design, add:

1. Linux button and textbox classes.
2. A `LinuxFactory` implementing both creation methods.
3. A Linux branch in the Application's factory-selection code.

The component-creation calls in `drawGUI()` stay the same. If a concrete `LinuxFactory` forgets `createTextbox()`, Java rejects it at compile time because it has not implemented all abstract methods. This catches the missing operation earlier than the naïve example's runtime failure.

The current factory example supports Windows and macOS only. Its constructor rejects an unsupported platform with `IllegalArgumentException` rather than leaving its factory uninitialized.

## Compare the approaches

| Concern | Naïve approach | Abstract Factory |
| --- | --- | --- |
| Platform checks | Repeated for every component | One selection of the application's factory |
| Component creation | Application names concrete component classes | Concrete factories name their component classes |
| Matching families | Maintained across separate branches | Grouped inside each platform's factory |
| Adding a platform | Update every component's creation branches | Add a complete factory and update factory selection |
| Missing textbox support | May leave a `null` value during execution | A concrete factory missing the abstract method cannot compile |

## Run the examples

With Java 11 or later, run each file separately from this folder. Each standalone example defines its own component classes.

### Naïve approach

```sh
java NaiveUI.java
```

The default `main` calls `drawGUI("win")`, producing:

```text
Windows button
Windows textbox
```

To try macOS, change the call in `main` to `drawGUI("macos")`. The output becomes:

```text
macOS button
macOS textbox
```

To try the failure case, replace the call in `main` with `drawGUIWithProblem("linux")`. The program prints `Linux button`, then throws a `NullPointerException`. The problem method is not called by default.

### Abstract Factory

```sh
java AbstractFactoryUI.java
```

The default `main` creates `new Application("win")`, producing:

```text
Windows button
Windows textbox
```

Change that constructor argument to `"macos"` to produce:

```text
macOS button
macOS textbox
```

## Check your understanding

1. Why must both creation branches be updated when adding Linux?
2. Why does the problem method compile even though it fails on Linux?
3. If the original textbox's plain `else` were left unchanged, what object would it create on Linux?
4. Why does `uiFactory.createButton()` create a Windows button when the field's declared type is `UIFactory`?
5. Why does a concrete factory missing `createTextbox()` fail at compile time?
