import java.util.ArrayList;
import java.util.List;

/*
 * Study example based on DesignPatterns.pdf, slides 104-125.
 * This simplified example stores positions and actually moves them.
 * Circle coordinates represent its center; rectangle coordinates its top-left.
 * Sizes, drawing, rotation, and scaling are omitted.
 * Run: java VisitorExample.java
 */
public class VisitorExample {
    public static void main(String[] args) {
        Shape rectangle = new Rectangle(10, 20);
        Shape circle = new Circle(2, 3);

        CompoundShape innerGroup = new CompoundShape();
        innerGroup.add(new Circle(0, 0));

        CompoundShape picture = new CompoundShape();
        picture.add(rectangle);
        picture.add(circle);
        picture.add(innerGroup);

        Visitor visitor = new TranslationVisitor(5, 2);
        picture.accept(visitor); // Move every individual shape, including nested ones.
    }
}

// ELEMENT CONTRACT: every concrete shape can receive a visitor.
abstract class Shape {
    public abstract void accept(Visitor visitor);
}

class Circle extends Shape {
    int centerX;
    int centerY;

    public Circle(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override
    public void accept(Visitor visitor) {
        // Here, this is the Circle. Overloading selects the visit(Circle) signature.
        visitor.visit(this);
    }
}

class Rectangle extends Shape {
    int x;
    int y;

    public Rectangle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void accept(Visitor visitor) {
        // Here, this is the Rectangle. The visitor receives this same object.
        visitor.visit(this);
    }
}

// A group contains individual shapes, other groups, or both.
class CompoundShape extends Shape {
    private final List<Shape> shapes = new ArrayList<>();

    public void add(Shape shape) {
        shapes.add(shape);
    }

    public List<Shape> getShapeList() {
        return shapes;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this); // Selects the visit(CompoundShape) signature.
    }
}

// VISITOR CONTRACT: overloads for each shape type, with shared group traversal.
abstract class Visitor {
    public abstract void visit(Circle circle);

    public abstract void visit(Rectangle rectangle);

    public void visit(CompoundShape group) {
        for (Shape shape : group.getShapeList()) {
            // Here, this is the visitor (a TranslationVisitor in this example).
            // Runtime polymorphism selects each child's accept() implementation.
            shape.accept(this);
        }
    }
}

// ONE OPERATION: keep translation logic for all individual shape types together.
class TranslationVisitor extends Visitor {
    private final int xOffset;
    private final int yOffset;

    public TranslationVisitor(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public void visit(Circle circle) {
        circle.centerX += xOffset;
        circle.centerY += yOffset;
        System.out.println("Moved circle to (" + circle.centerX + ", " + circle.centerY + ")");
    }

    @Override
    public void visit(Rectangle rectangle) {
        rectangle.x += xOffset;
        rectangle.y += yOffset;
        System.out.println("Moved rectangle to (" + rectangle.x + ", " + rectangle.y + ")");
    }

    // Inherits visit(CompoundShape): the existing loop handles nested groups too.
}

/*
 * picture.accept(visitor)
 *     -> CompoundShape.accept(visitor)
 *     -> Visitor.visit(picture), inherited by the TranslationVisitor object
 *         -> Rectangle.accept(visitor)
 *             -> TranslationVisitor.visit(Rectangle)
 *         -> Circle.accept(visitor)
 *             -> TranslationVisitor.visit(Circle)
 *         -> CompoundShape.accept(visitor), for innerGroup
 *             -> Visitor.visit(innerGroup)
 *                 -> Circle.accept(visitor)
 *                     -> TranslationVisitor.visit(Circle)
 *
 * accept() routes the call. The individual-shape visit() methods move the shapes.
 * All accept/visit calls pass references to existing objects, without copying them.
 */
