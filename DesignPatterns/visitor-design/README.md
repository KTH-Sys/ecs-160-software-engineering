# Visitor design pattern

Study example based on **DesignPatterns.pdf, slides 104-125**. This simplified code and explanation were created for studying; they are not the professor's original code.

## Problem statement

A picture contains circles, rectangles, and groups of shapes. A group can contain another group. We want to apply an operation to every individual shape, including shapes inside nested groups.

We also want each operation's logic together: translation logic in one class, rotation logic in another, and so on.

## Naive approach

Put every operation in every shape class:

```java
class Circle {
    void translate() { /* ... */ }
    void rotate() { /* ... */ }
    void export() { /* ... */ }
}

class Rectangle {
    void translate() { /* ... */ }
    void rotate() { /* ... */ }
    void export() { /* ... */ }
}
```

This can work, but adding another operation requires changing every shape class. The code for one operation is spread across several classes.

## Visitor solution

Visitor represents an operation as a separate object. It defines a method for each concrete element type, so the operation can handle each shape appropriately.

```text
TranslationVisitor -> moving circles and rectangles
RotationVisitor    -> rotating circles and rectangles
ExportVisitor      -> exporting circles and rectangles
```

Only TranslationVisitor is implemented in [VisitorExample.java](VisitorExample.java).

| Part | Responsibility in this example |
| --- | --- |
| Shape | Declare accept(Visitor) |
| Circle / Rectangle | Forward to the matching visit method |
| CompoundShape | Store child shapes and accept a visitor |
| Visitor | Declare shape-specific overloads and provide group traversal |
| TranslationVisitor | Implement the translation operation for individual shapes |

The code stores a circle's center and a rectangle's top-left position. It actually updates those coordinates. Sizes and drawing are omitted to keep the example small.

## Start with one circle

```java
Shape shape = new Circle(2, 3);
Visitor visitor = new TranslationVisitor(5, 2);

shape.accept(visitor);
```

The variable shape has declared type Shape, but references a Circle object. The variable visitor has declared type Visitor, but references a TranslationVisitor object.

1. Runtime polymorphism selects Circle.accept(), because the actual shape is a Circle.
2. Inside Circle.accept(), this has compile-time type Circle. The compiler selects the visit(Circle) signature for visitor.visit(this).
3. Runtime polymorphism selects TranslationVisitor's implementation of visit(Circle), because the actual visitor is a TranslationVisitor.
4. That method changes the existing circle's center from (2, 3) to (7, 5).

No circle is copied or created by accept() or visit(). Their parameters receive references to the existing objects.

## Why not call visitor.visit(shape) directly?

```java
Shape shape = new Circle(2, 3);
// visitor.visit(shape); // Does not compile with this Visitor contract.
```

The available overloads accept Circle, Rectangle, and CompoundShape. There is no visit(Shape). The compiler sees the argument's declared type Shape, so it cannot select any of those more specific overloads.

Even if we added a generic visit(Shape), this call would select that signature rather than dynamically selecting visit(Circle).

Calling accept() first gets us inside Circle's implementation, where this has type Circle:

```java
@Override
public void accept(Visitor visitor) {
    visitor.visit(this);
}
```

Although every shape's accept() has the same-looking body, this has a different compile-time type in each class.

## Overloading, overriding, and double dispatch

| Mechanism | What determines the choice? |
| --- | --- |
| Overloading | Declared argument types determine the signature at compile time |
| Overriding | The actual receiver type determines the implementation at runtime |

In receiver.method(argument), the receiver is the object before the dot.

Visitor achieves double dispatch through two runtime dispatches: the shape selects its accept() implementation, then the visitor selects its implementation of the chosen visit signature. A compile-time overload selection occurs between those calls.

The resulting operation depends on both the actual shape type and the actual visitor type. Java does not dynamically select overloaded signatures from argument object types.

## Why does this refer to different objects?

| Location | Expression | Meaning of this |
| --- | --- | --- |
| Circle.accept() | visitor.visit(this) | The circle receiving accept() |
| Visitor.visit(CompoundShape) | shape.accept(this) | The visitor receiving visit() |

The group method is defined in Visitor, but inherited by TranslationVisitor. Its this still refers to the executing TranslationVisitor object. There is no separate base Visitor object.

## Trace the nested example

The main method builds:

```text
picture
├── Rectangle at (10, 20)
├── Circle centered at (2, 3)
└── innerGroup
    └── Circle centered at (0, 0)
```

It starts with:

```java
Visitor visitor = new TranslationVisitor(5, 2);
picture.accept(visitor);
```

The sequence is:

```text
CompoundShape.accept(visitor)
    -> Visitor.visit(picture)
        -> Rectangle.accept(visitor)
            -> TranslationVisitor.visit(Rectangle)
        -> Circle.accept(visitor)
            -> TranslationVisitor.visit(Circle)
        -> CompoundShape.accept(visitor), for innerGroup
            -> Visitor.visit(innerGroup)
                -> Circle.accept(visitor)
                    -> TranslationVisitor.visit(Circle)
```

The shared traversal method is:

```java
public void visit(CompoundShape group) {
    for (Shape shape : group.getShapeList()) {
        shape.accept(this);
    }
}
```

Read the loop body as: "Child shape, accept this same visitor."

A child group leads back into the group traversal method. This recursion reaches shapes inside nested groups. TranslationVisitor inherits this concrete method and only implements the two abstract methods for individual shapes.

## Run the example

With Java 11 or later, run from this folder:

```sh
java VisitorExample.java
```

Expected output:

```text
Moved rectangle to (15, 22)
Moved circle to (7, 5)
Moved circle to (5, 2)
```

Each line comes from an individual shape's visit method after updating its coordinates. Traversal follows the order in which children were added.

## Adding operations and shape types

- **New operation:** create another Visitor subclass and implement visit(Circle) and visit(Rectangle). The existing shape classes already have accept(). Group traversal can be inherited when it suits the operation.
- **New shape type:** add its accept(), add an overload to Visitor, and implement the new required method in concrete visitors.

Visitor is useful when element types are relatively stable and operations change frequently. Adding element types requires maintaining the visitor contract and its implementations.

For example, a compiler can use separate visitors to evaluate expressions, count constants, or generate code over the same syntax-tree structure.

## Common mistakes

- Expecting Java to select visit(Circle) from a Shape-typed argument automatically.
- Assuming this always refers to a shape; inside a visitor method, it refers to the visitor.
- Thinking an abstract class can contain only abstract methods. Visitor provides a concrete group traversal method.
- Treating translation as a radius change. Slide 117 uses setRadius with coordinates; translation should change the circle's center, leaving its radius unchanged.
- Assuming Visitor removes the need to update code when a new shape type is introduced.

## What to remember

**accept() routes the call; visit() performs the operation.** In this example, the group visit method traverses children, and the individual-shape visit methods move them.

## Check your understanding

1. Why does a Shape reference holding a Circle execute Circle.accept()?
2. Inside Circle.accept(), why does visitor.visit(this) select the visit(Circle) signature?
3. Inside the group traversal loop, what object does this refer to?
4. If picture.accept(visitor) runs twice, what coordinates will the nested circle have?
5. Which classes must change when you add a Triangle type, compared with adding an export operation?
