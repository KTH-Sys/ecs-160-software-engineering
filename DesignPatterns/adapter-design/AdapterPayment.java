/*
 * Study example based on DesignPatterns.pdf, slides 37–45.
 * These simplified processors print messages instead of making real payments.
 * Run: java AdapterPayment.java
 */
public class AdapterPayment {
    public static void main(String[] args) {
        // 1. PayPal already implements the interface our application expects.
        PaymentProcessor paypal = new PaypalProcessor();
        handlePurchase(paypal);

        // 2. Stripe can pay, but its method is named makePayment(), not pay().
        StripeProcessor stripe = new StripeProcessor();

        // handlePurchase(stripe); // Compiler error: Stripe is not a PaymentProcessor.

        // 3. Create an adapter that holds a reference to the existing Stripe object.
        // The adapter implements PaymentProcessor, so handlePurchase accepts it.
        PaymentProcessor adaptedStripe = new StripeAdapter(stripe);
        handlePurchase(adaptedStripe);
    }

    // The parameter p receives a reference to the object supplied by the caller.
    // Passing the reference does not create or copy a payment processor.
    static void handlePurchase(PaymentProcessor p) {
        p.pay(1000); // Each call asks the supplied processor to pay 1000.
    }
}

// TARGET INTERFACE: the contract the application expects.
interface PaymentProcessor {
    void pay(int amount);
}

// PayPal already satisfies that contract and needs no adapter.
class PaypalProcessor implements PaymentProcessor {
    @Override
    public void pay(int amount) {
        System.out.println("PayPal payment: " + amount);
    }
}

// ADAPTEE: an existing class with a different interface.
// Imagine this class comes from a third-party library that we do not modify.
class StripeProcessor {
    public void makePayment(int amount) {
        System.out.println("Stripe payment: " + amount);
    }
}

// ADAPTER: presents pay() to the application and delegates to Stripe's makePayment().
class StripeAdapter implements PaymentProcessor {
    private StripeProcessor stripe;

    public StripeAdapter(StripeProcessor stripe) {
        // Save the supplied object's reference; do not create another Stripe object.
        this.stripe = stripe;
    }

    @Override
    public void pay(int amount) {
        stripe.makePayment(amount); // Stripe performs the payment.
    }
}

/*
 * Stripe call flow:
 * handlePurchase(adaptedStripe)
 *     -> p.pay(1000), where p refers to the StripeAdapter object
 *     -> StripeAdapter.pay(1000)
 *     -> stripe.makePayment(1000) on the wrapped StripeProcessor object
 *
 * The Stripe path uses TWO objects: one StripeAdapter and one StripeProcessor.
 * handlePurchase stays unchanged for both PayPal and adapted Stripe.
 *
 * Output:
 * PayPal payment: 1000
 * Stripe payment: 1000
 *
 * Check: if handlePurchase(adaptedStripe) is called twice, does that create
 * another StripeProcessor object, or use the existing one again?
 */
