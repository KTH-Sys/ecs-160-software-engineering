# Adapter design pattern

Study example based on **DesignPatterns.pdf, slides 37–45**. The explanations and simplified code were created for studying; they are not the professor's original code. The processors print messages instead of making real payments.

## Problem statement

The application uses `PaymentProcessor.pay(int amount)` to process purchases. PayPal already implements this interface.

We want to support an existing Stripe library that provides `StripeProcessor.makePayment(int amount)` without changing the library or adding Stripe-specific checks throughout the application.

## The application already expects this interface

```java
interface PaymentProcessor {
    void pay(int amount);
}
```

Its purchase function accepts an object implementing that interface:

```java
static void handlePurchase(PaymentProcessor p) {
    p.pay(1000);
}
```

`PaymentProcessor` is the parameter's type, and `p` is its local name. The parameter receives a reference to the caller's object; it does not create or copy a processor.

Every call to this version of `handlePurchase` asks the supplied processor to pay `1000`, because that amount is hardcoded.

## Why Stripe cannot be passed directly

`StripeProcessor` provides `makePayment()`, but it does not implement `PaymentProcessor`. Therefore, this does not compile:

```java
handlePurchase(new StripeProcessor());
```

Similar functionality does not make Java types compatible. Even renaming the method would not establish the required `implements PaymentProcessor` relationship.

## The Adapter solution

`StripeAdapter` implements the interface the application expects and holds a reference to the existing Stripe object:

```java
class StripeAdapter implements PaymentProcessor {
    private StripeProcessor stripe;

    public StripeAdapter(StripeProcessor stripe) {
        this.stripe = stripe;
    }

    @Override
    public void pay(int amount) {
        stripe.makePayment(amount);
    }
}
```

- `implements PaymentProcessor` makes the adapter acceptable to `handlePurchase`.
- The constructor saves a reference to the supplied Stripe object.
- `pay()` delegates the payment to that object's `makePayment()` method.
- `@Override` lets the compiler check that `pay()` implements the interface method correctly. The example uses `int` consistently; some slide snippets mix `int` and `double`.

## Using it

```java
StripeProcessor stripe = new StripeProcessor();
PaymentProcessor adaptedStripe = new StripeAdapter(stripe);
handlePurchase(adaptedStripe);
```

The Stripe path uses two objects: one Stripe processor and one adapter. Passing `adaptedStripe` to the function creates no additional object.

```text
handlePurchase(adaptedStripe)
    → p.pay(1000), where p refers to the adapter
    → StripeAdapter.pay(1000)
    → stripe.makePayment(1000)
```

Runtime polymorphism selects `StripeAdapter.pay()` because the actual object referred to by `p` is a StripeAdapter. The wrapped StripeProcessor performs the payment.

## Pattern roles

| Role | Example |
| --- | --- |
| Client: code requesting the operation | `handlePurchase` |
| Target interface: the contract the client expects | `PaymentProcessor` |
| Adapter: translates calls into the existing object's interface | `StripeAdapter` |
| Adaptee: existing object being adapted | `StripeProcessor` |

The application keeps calling `pay()` for both PayPal and adapted Stripe. Provider-specific translation stays inside the adapter, so purchase functions need no Stripe flag or conditional branch.

## Run the code

With Java 11 or later, run from this folder:

```sh
java AdapterPayment.java
```

Expected output:

```text
PayPal payment: 1000
Stripe payment: 1000
```

[AdapterPayment.java](AdapterPayment.java) contains the complete runnable example, explanations in comments, and a commented-out call demonstrating the incompatible type.

## Check your understanding

1. Why can `handlePurchase` accept a StripeAdapter but not a StripeProcessor directly?
2. Which object does the parameter `p` refer to when using adapted Stripe?
3. If `handlePurchase(adaptedStripe)` runs twice, does it create another processor or reuse the existing objects?
