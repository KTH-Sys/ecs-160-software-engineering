public class AbstractFactoryUI {
    public static void main(String[] args) {
        Application app = new Application("win");
        app.drawGUI();
    }
}

class Application {
    private UIFactory uiFactory;

    public Application(String platform) {
        // Choose the platform's factory once.
        if (platform.equals("win")) {
            uiFactory = new WinFactory();
        } else if (platform.equals("macos")) {
            uiFactory = new MacOSFactory();
        } else {
            throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }

    public void drawGUI() {
        // The factory creates a matching family, without more platform checks.
        Button button = uiFactory.createButton();
        Textbox textbox = uiFactory.createTextbox();

        button.draw();
        textbox.draw();
    }
}

abstract class UIFactory {
    public abstract Button createButton();
    public abstract Textbox createTextbox();
}

class WinFactory extends UIFactory {
    public Button createButton() {
        return new WinButton();
    }

    public Textbox createTextbox() {
        return new WinTextBox();
    }
}

class MacOSFactory extends UIFactory {
    public Button createButton() {
        return new MacOSButton();
    }

    public Textbox createTextbox() {
        return new MacOSTextBox();
    }
}

abstract class Button {
    public abstract void draw();
}

class WinButton extends Button {
    public void draw() {
        System.out.println("Windows button");
    }
}

class MacOSButton extends Button {
    public void draw() {
        System.out.println("macOS button");
    }
}

abstract class Textbox {
    public abstract void draw();
}

class WinTextBox extends Textbox {
    public void draw() {
        System.out.println("Windows textbox");
    }
}

class MacOSTextBox extends Textbox {
    public void draw() {
        System.out.println("macOS textbox");
    }
}
