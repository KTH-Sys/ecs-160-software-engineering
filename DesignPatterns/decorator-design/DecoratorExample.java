/*
 * Study example based on DesignPatterns.pdf, slides 65–75.
 * The fraud check and payment are placeholders that print their actions.
 * Currency conversion uses a fixed example multiplier of 1.25.
 * Run: java DecoratorExample.java
 */
public class DecoratorExample {
    public static void main(String[] args) {
        PaymentProcessor paypal = new PaypalProcessor();

        // Wrap PayPal to add fraud checking.
        PaymentProcessor checkedPayment = new FraudDetectionDecorator(paypal);

        // Wrap that decorator to add currency conversion as the outer layer.
        PaymentProcessor convertedPayment = new CurrencyConversionDecorator(checkedPayment);
        handlePurchase(convertedPayment);
    }

    // Existing client code still accepts the same interface.
    // p refers to the outer decorator; passing it creates no additional object.
    static void handlePurchase(PaymentProcessor p) {
        p.pay(1000.0);
    }
}

// COMPONENT INTERFACE: implemented by the base processor and every decorator.
interface PaymentProcessor {
    void pay(double amount);
}

// BASE COMPONENT: performs the payment without additional features.
class PaypalProcessor implements PaymentProcessor {
    @Override
    public void pay(double amount) {
        System.out.println("PayPal payment: " + amount);
    }
}

// DECORATOR: adds fraud checking, then delegates to the wrapped processor.
class FraudDetectionDecorator implements PaymentProcessor {
    // This can refer to PayPal OR another decorator implementing PaymentProcessor.
    private PaymentProcessor processor;

    public FraudDetectionDecorator(PaymentProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void pay(double amount) {
        detectFraud(amount);
        processor.pay(amount);
    }

    private void detectFraud(double amount) {
        // Placeholder: a real check would decide whether payment should proceed.
        System.out.println("Checking for fraud: " + amount);
    }
}

// DECORATOR: converts the amount, then delegates with the converted value.
class CurrencyConversionDecorator implements PaymentProcessor {
    private PaymentProcessor processor;

    public CurrencyConversionDecorator(PaymentProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void pay(double amount) {
        double convertedAmount = amount * 1.25;
        System.out.println("Converted amount: " + convertedAmount);
        processor.pay(convertedAmount);
    }
}

/*
 * Object references:
 * convertedPayment -> CurrencyConversionDecorator
 *                         processor -> FraudDetectionDecorator
 *                                          processor -> PaypalProcessor
 *
 * handlePurchase(convertedPayment)
 *     -> CurrencyConversionDecorator.pay(1000.0)
 *         -> convert to 1250.0
 *         -> FraudDetectionDecorator.pay(1250.0)
 *             -> detectFraud(1250.0)
 *             -> PaypalProcessor.pay(1250.0)
 *
 * The outer decorator receives the call first. Each layer adds its own feature
 * and forwards the call. PayPal and handlePurchase keep their implementations.
 */
