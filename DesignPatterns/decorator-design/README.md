# Decorator design pattern

Study example based on **DesignPatterns.pdf, slides 65–75**. The simplified code and explanation were created for studying; they are not the professor's original code. The fraud check and payment print their actions, and currency conversion uses a fixed example multiplier of 1.25.

## Problem statement

The application already uses a PaymentProcessor to make payments. We want to add optional features such as fraud checking and currency conversion without modifying PaypalProcessor or adding those operations wherever the application calls `pay()`.

We also want to combine features as needed while keeping the same PaymentProcessor interface.

## Analogy: customizing a coffee order

Start with a basic coffee and choose optional extras such as milk or syrup. You can combine the extras without defining a separate class for every possible combination.

Decorator applies this idea to objects. A wrapper adds a responsibility while keeping the interface expected by callers. Another wrapper can add a further responsibility.

## The complete example

[DecoratorExample.java](DecoratorExample.java) contains the interface, the PayPal processor, two decorators, and the client function in one file.

| Role | Code |
| --- | --- |
| Component interface | `PaymentProcessor` |
| Base component performing the payment | `PaypalProcessor` |
| Decorator adding fraud checking | `FraudDetectionDecorator` |
| Decorator adding currency conversion | `CurrencyConversionDecorator` |
| Client using the component interface | `handlePurchase` |

The slides factor common wrapping code into an abstract PaymentProcessorDecorator class. This simplified example implements PaymentProcessor directly in each decorator to keep the wrapping and delegation visible. The shared base class is a way to reuse that common code.

## How a decorator works

The fraud decorator holds a reference to a PaymentProcessor:

```java
private PaymentProcessor processor;

public FraudDetectionDecorator(PaymentProcessor processor) {
    this.processor = processor;
}
```

The constructor saves the supplied object's reference. It does not create or copy the wrapped object.

Its `pay()` method adds a check before forwarding the request:

```java
@Override
public void pay(double amount) {
    detectFraud(amount);
    processor.pay(amount);
}
```

The placeholder check prints the amount and allows delegation to continue. In an actual implementation, the check would decide whether the payment should proceed.

The decorator implements PaymentProcessor so it can be used in place of the base processor. Its field also has type PaymentProcessor, allowing it to wrap either a base processor or another decorator.

## Stack the features

```java
PaymentProcessor paypal = new PaypalProcessor();
PaymentProcessor checkedPayment = new FraudDetectionDecorator(paypal);
PaymentProcessor convertedPayment = new CurrencyConversionDecorator(checkedPayment);

handlePurchase(convertedPayment);
```

This creates three objects. The constructors connect them through references:

```text
CurrencyConversionDecorator
    → FraudDetectionDecorator
        → PaypalProcessor
```

The existing client function is unchanged:

```java
static void handlePurchase(PaymentProcessor p) {
    p.pay(1000.0);
}
```

Here, `p` refers to the outer currency decorator. Passing the reference does not create another object.

## Trace the execution

1. The currency decorator receives `1000.0`.
2. It multiplies the amount by the illustrative conversion rate, 1.25, producing `1250.0`.
3. It calls the wrapped fraud decorator with `1250.0`.
4. The fraud decorator checks that amount and delegates to PayPal.
5. PayPal receives `1250.0` and performs the placeholder payment.

```text
handlePurchase(convertedPayment)
    → CurrencyConversionDecorator.pay(1000.0)
        → FraudDetectionDecorator.pay(1250.0)
            → PaypalProcessor.pay(1250.0)
```

These decorators perform their added work before delegation. Decorators can also perform work after delegation, depending on the feature.

## Why does order matter?

The current arrangement converts before checking, so fraud checking sees the converted amount. Reversing the wrappers checks the original amount before conversion:

```java
PaymentProcessor convertedPayment = new CurrencyConversionDecorator(paypal);
PaymentProcessor checkedPayment = new FraudDetectionDecorator(convertedPayment);
```

Passing that `checkedPayment` to handlePurchase would check `1000.0`, then convert and pass `1250.0` to PayPal. The appropriate order depends on the application's requirements.

## Benefits and trade-off

- Add optional features without changing the base processor or client function.
- Combine features through wrapping at runtime.
- Keep each class focused on its own responsibility.
- Apply a decorator more than once, such as two fraud-checking layers.
- Many layers can make execution harder to trace and debug.

## Decorator compared with Proxy

| Pattern | Main intent |
| --- | --- |
| Proxy | Stand in for a subject and control access to it |
| Decorator | Add responsibilities that can be combined around a component |

Their implementations may look identical. Intent matters; logging, delegation, or stacking alone does not uniquely identify the pattern.

The course also combines Adapter with Decorator:

```text
Currency conversion → Fraud detection → StripeAdapter → StripeProcessor
```

The adapter translates the interface, while the decorators add features. The runnable file here uses PayPal to keep the example small.

## Run the example

With Java 11 or later, run from this folder:

```sh
java DecoratorExample.java
```

Expected output:

```text
Converted amount: 1250.0
Checking for fraud: 1250.0
PayPal payment: 1250.0
```

## Check your understanding

1. Why is each decorator's field typed PaymentProcessor instead of PaypalProcessor?
2. Which object does the parameter `p` refer to in the default example?
3. Why does reversing the wrappers change the amount seen by fraud checking?
