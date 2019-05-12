package com.bsuir.ftpclient.ui.dialog.choose;

import com.bsuir.ftpclient.connection.ftp.data.DataType;
import javafx.scene.control.ChoiceDialog;

import java.util.Arrays;
import java.util.Optional;

public class ChoiceDataTypeDialog extends ChoiceDialog<String> {

    public ChoiceDataTypeDialog() {
        super("ASCII", Arrays.asList(
                DataType.ASCII.name(),
                DataType.L8.name(),
                DataType.BINARY.name()
        ));

        setTitle("Choice data type");
        setHeaderText("Choice data type");
        setContentText("Choose data type:");
    }

    public Optional<String> showDialog() {
        return this.showAndWait();
    }
}
