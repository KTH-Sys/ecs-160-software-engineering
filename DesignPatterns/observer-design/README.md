# Observer design pattern

Study example based on **Lecture 4 and DesignPatterns.pdf, slides 78–85**. This simplified code and explanation were created for studying; they are not the professor's original code. Charts print their data instead of drawing a graphical view.

## Problem statement

A DataModel stores three values: `a`, `b`, and `c`. A bar chart and a pie chart display the same data. Whenever the application updates the model, all registered charts should receive the current values and update their own views.

We also want to register and remove charts without changing the DataModel's notification code.

## Analogy: channel subscriptions

A channel maintains a subscriber list. When it publishes an update, it notifies its subscribers. Someone who unsubscribes stops receiving future notifications.

| Analogy | Code |
| --- | --- |
| Channel publishing updates | DataModel |
| Subscribers | BarChart and PieChart |
| Subscribe | `addObserver(chart)` |
| Unsubscribe | `removeObserver(chart)` |
| Receive a notification | `chart.update(a, b, c)` |

## Pattern roles

Observer defines a one-to-many dependency: when the subject changes state, it notifies its registered observers.

| Role | Responsibility | Example |
| --- | --- | --- |
| Subject | Store state, track observers, and send notifications | DataModel |
| Observer interface | Define the notification method | Observer |
| Concrete observer | Respond to a notification | BarChart, PieChart |

Observer is a behavioral pattern because it coordinates how objects respond to changes.

## How the code works

[ObserverExample.java](ObserverExample.java) contains the subject, observer interface, charts, and demonstration in one file.

### 1. Give observers a shared contract

```java
interface Observer {
    void update(int a, int b, int c);
}
```

Each chart implements this interface. Its update method handles rendering that particular chart; the simplified example prints its values.

### 2. Register existing chart objects

```java
model.addObserver(barChart);
model.addObserver(pieChart);
```

Each call passes a reference to an existing chart. DataModel stores the reference in its `List<Observer>`; it does not create or copy the chart.

```text
DataModel's observer list → [barChart, pieChart]
```

### 3. Update the model and notify the list

```java
model.setData(10, 20, 30);
```

The setData method stores all three values and then calls notifyObservers:

```java
private void notifyObservers() {
    for (Observer observer : observers) {
        observer.update(a, b, c);
    }
}
```

For each reference in the list, the loop calls update with the current values. Runtime polymorphism selects that observer's implementation.

```text
setData(10, 20, 30)
    → store the data
    → notifyObservers()
        → BarChart.update(10, 20, 30)
        → PieChart.update(10, 20, 30)
```

The chart updates are automatic because setData explicitly triggers this loop. These are ordinary method calls performed sequentially; there is no background watcher. In this example, every setData call notifies the list, even if the supplied values match the previous values.

### 4. Remove an observer

```java
model.removeObserver(pieChart);
model.setData(15, 25, 35);
```

The list now contains only the bar chart, so only its update method is called. Removing an observer does not destroy the chart object; it stops future notifications from this model.

## Why use it?

The model works with the Observer interface and registered references. It does not need chart-specific methods or a separate branch for every chart type.

To add another view, implement Observer and register the object. The model's notification loop stays unchanged. This supports broadcasting one state update to many interested objects.

The example sends data along with the notification, often called a push approach. The lecture also describes notifying observers that a change occurred and letting them request the data afterward, a pull approach.

## Trade-off: cascading updates

An observer's response may update another subject and trigger further observers. One model change can therefore cause a chain of notifications that is harder to trace. For example, a chart redraw might notify other objects observing its layout.

## Run the example

With Java 11 or later, run from this folder:

```sh
java ObserverExample.java
```

Expected output:

```text
Bar chart: 10, 20, 30
Pie chart: 10, 20, 30
Bar chart: 15, 25, 35
```

Both charts receive the first update. The pie chart is removed before the second update, so it produces no second line.

## Check your understanding

1. Which object calls each chart's update method?
2. Why does registering a chart not create another chart object?
3. How would you add a new chart type without editing DataModel?
4. Why does removing the pie chart stop its notifications without deleting the object?
