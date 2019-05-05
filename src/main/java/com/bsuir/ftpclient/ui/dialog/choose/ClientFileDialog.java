package com.bsuir.ftpclient.ui.dialog.choose;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class ClientFileDialog extends ChoiceFileDialog {
    private FileChooser chooser = new FileChooser();

    public ClientFileDialog() {
        chooser.setTitle("Choosing file");
    }

    public Optional<String> chooseFilePath() {
        Stage stage = new Stage();
        File choosingFile = chooser.showOpenDialog(stage);

        return getOptionalPath(choosingFile);
    }
}
