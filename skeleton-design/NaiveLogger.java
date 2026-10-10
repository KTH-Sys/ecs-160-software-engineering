class Logger {
    public Logger() {
    }

    public void log(String message) {
        System.out.println(message);
    }
}

public class NaiveLogger {
    public static void main(String[] args) {
        Logger logger = new Logger();
        checkout(logger);
    }

    static void checkout(Logger logger) {
        logger.log("Checkout started");
        saveOrder(logger);
    }

    static void saveOrder(Logger logger) {
        logger.log("Order saved");
    }

    // Problem: a function can create another Logger instead of using the supplied one.
    static void saveOrderWithProblem(Logger logger) {
        Logger logger2 = new Logger(); // Creates a second object.
        logger2.log("Order saved");
    }
}
