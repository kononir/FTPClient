package com.bsuir.ftpclient.ui.dialog.choose;

import java.io.File;
import java.util.Optional;

public abstract class ChoiceFileDialog {
    protected Optional<String> getOptionalPath(File choosingCatalogue) {
        Optional<String> path;
        if (choosingCatalogue == null) {
            path = Optional.empty();
        } else {
            path = Optional.of(choosingCatalogue.getAbsolutePath());
        }

        return path;
    }
}
