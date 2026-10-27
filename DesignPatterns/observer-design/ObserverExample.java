import java.util.ArrayList;
import java.util.List;

/*
 * Study example based on DesignPatterns.pdf, slides 78–85.
 * Charts print their data instead of drawing a real graphical view.
 * Run: java ObserverExample.java
 */
public class ObserverExample {
    public static void main(String[] args) {
        DataModel model = new DataModel();
        BarChart barChart = new BarChart();
        PieChart pieChart = new PieChart();

        // Subscribe both existing charts. These calls do not create more charts.
        model.addObserver(barChart);
        model.addObserver(pieChart);

        // Store the data, then notify both registered charts.
        model.setData(10, 20, 30);

        // Unsubscribe the pie chart. The object still exists, but is no longer notified.
        model.removeObserver(pieChart);
        model.setData(15, 25, 35); // Only the bar chart receives this update.
    }
}

// OBSERVER CONTRACT: every registered chart must provide an update method.
interface Observer {
    void update(int a, int b, int c);
}

// SUBJECT: owns the data and maintains a list of interested observers.
class DataModel {
    private int a, b, c;
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer); // Save a reference to the supplied observer.
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void setData(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
        notifyObservers(); // This explicit call triggers the automatic chart updates.
    }

    private void notifyObservers() {
        for (Observer observer : observers) {
            // Runtime polymorphism selects the current chart's update implementation.
            observer.update(a, b, c);
        }
    }
}

// CONCRETE OBSERVERS: each responds in its own way to the same data.
class BarChart implements Observer {
    @Override
    public void update(int a, int b, int c) {
        System.out.println("Bar chart: " + a + ", " + b + ", " + c);
    }
}

class PieChart implements Observer {
    @Override
    public void update(int a, int b, int c) {
        System.out.println("Pie chart: " + a + ", " + b + ", " + c);
    }
}

/*
 * First update:
 * model.setData(10, 20, 30)
 *     -> store the values
 *     -> notifyObservers()
 *         -> barChart.update(10, 20, 30)
 *         -> pieChart.update(10, 20, 30)
 *
 * After removeObserver(pieChart), the next notification reaches only barChart.
 * "Automatic" updating comes from setData calling the notification loop.
 */
