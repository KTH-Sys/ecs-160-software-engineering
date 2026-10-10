class Logger {
    public static Logger logger = null;

    public Logger() {
    }

    public void log(String message) {
        System.out.println(message);
    }
}

public class StaticFieldLogger {
    public static void main(String[] args) {
        Logger.logger = new Logger();
        checkout();
    }

    static void checkout() {
        Logger.logger.log("Checkout started");
        saveOrder();
    }

    static void saveOrder() {
        Logger.logger.log("Order saved");
    }

    // Problem: a function can create another Logger and replace the shared reference.
    static void saveOrderWithProblem() {
        Logger logger2 = new Logger(); // Creates a second object.
        Logger.logger = logger2;       // Replaces the shared reference.
        Logger.logger.log("Order saved");
    }
}
