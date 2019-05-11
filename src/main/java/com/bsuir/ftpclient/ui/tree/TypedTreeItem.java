package com.bsuir.ftpclient.ui.tree;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class TypedTreeItem<T> extends TreeItem<T> {
    private static final String FOLDER_IMAGE_PATH = "images/folder.png";
    private static final String FILE_IMAGE_PATH = "images/file.png";

    private boolean isPackage;

    public TypedTreeItem(T value, boolean isPackage) {
        super(value);
        this.isPackage = isPackage;

        setNodeGraphic();
    }

    public boolean isPackage() {
        return isPackage;
    }

    public void setNodeGraphic() {
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream inputStream;
        if (isPackage) {
            inputStream = classLoader.getResourceAsStream(FOLDER_IMAGE_PATH);
        } else {
            inputStream = classLoader.getResourceAsStream(FILE_IMAGE_PATH);
        }

        setGraphic(new ImageView(new Image(inputStream)));
    }
}
