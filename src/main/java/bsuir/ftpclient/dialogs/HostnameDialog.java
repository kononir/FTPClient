package main.java.bsuir.ftpclient.dialogs;

import javafx.scene.control.TextInputDialog;
import main.java.bsuir.ftpclient.managers.ViewManager;

public class HostnameDialog extends TextInputDialog {
    private ViewManager viewManager;

    public HostnameDialog() {
        super("localhost");

        this.setTitle("Text input");
        this.setHeaderText("Text input");
        this.setContentText("Please enter domain name or ip");
    }
}
