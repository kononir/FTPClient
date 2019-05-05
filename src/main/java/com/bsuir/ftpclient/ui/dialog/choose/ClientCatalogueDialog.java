package com.bsuir.ftpclient.ui.dialog.choose;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class ClientCatalogueDialog extends ChoiceFileDialog {
    private DirectoryChooser chooser = new DirectoryChooser();

    public ClientCatalogueDialog() {
        chooser.setTitle("Choosing directory");
    }

    public Optional<String> chooseCataloguePath() {
        Stage stage = new Stage();
        File choosingCatalogue = chooser.showDialog(stage);

        return getOptionalPath(choosingCatalogue);
    }
}
