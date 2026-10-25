class Logger {
    private static Logger logger = null;

    private Logger() {
    }

    public static Logger getLogger() {
        if (logger == null) {
            logger = new Logger(); // Create the object on the first request.
        }
        return logger; // Later requests receive the same object.
    }

    public void log(String message) {
        System.out.println(message);
    }
}

public class singletonLogger {
    public static void main(String[] args) {
        checkout();
    }

    static void checkout() {
        Logger logger = Logger.getLogger();
        logger.log("Checkout started");
        saveOrder();
    }

    static void saveOrder() {
        Logger logger = Logger.getLogger(); // Receives the existing object.
        logger.log("Order saved");

        // new Logger() is not allowed here: the constructor is private.
        // Logger.logger = null is not allowed here: the field is private.
    }
}
