public class NaiveUI {
    public static void main(String[] args) {
        drawGUI("win");
    }

    static void drawGUI(String platform) {
        Button button;
        if (platform.equals("win")) {
            button = new WinButton();
        } else {
            button = new MacOSButton();
        }

        Textbox textbox;
        if (platform.equals("win")) {
            textbox = new WinTextBox();
        } else {
            textbox = new MacOSTextBox();
        }

        button.draw();
        textbox.draw();
    }

    // Problem: adding a platform requires updating every component's branches.
    static void drawGUIWithProblem(String platform) {
        Button button;
        if (platform.equals("win")) {
            button = new WinButton();
        } else if (platform.equals("macos")) {
            button = new MacOSButton();
        } else {
            button = new GnomeButton(); // Linux support added here.
        }

        Textbox textbox = null;
        if (platform.equals("win")) {
            textbox = new WinTextBox();
        } else if (platform.equals("macos")) {
            textbox = new MacOSTextBox();
        }
        // Linux support forgotten: textbox stays null.

        button.draw();
        textbox.draw(); // On Linux, this throws a NullPointerException.
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

class GnomeButton extends Button {
    public void draw() {
        System.out.println("Linux button");
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
