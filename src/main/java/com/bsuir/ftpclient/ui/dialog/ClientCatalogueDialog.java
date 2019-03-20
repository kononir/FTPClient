package com.bsuir.ftpclient.ui.dialog;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class ClientCatalogueDialog {
    private DirectoryChooser chooser = new DirectoryChooser();

    public ClientCatalogueDialog() {
        chooser.setTitle("Choosing directory");
    }

    public Optional<String> chooseCataloguePath() {
        Stage stage = new Stage();
        File choosingCatalogue = chooser.showDialog(stage);

        Optional<String> path;

        if (choosingCatalogue == null) {
            path = Optional.empty();
        } else {
            path = Optional.of(choosingCatalogue.getAbsolutePath());
        }

        return path;
    }
}
