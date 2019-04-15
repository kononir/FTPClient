package com.bsuir.ftpclient.ui.tree;

import javafx.scene.control.TreeItem;

public class TypedTreeItem<T> extends TreeItem<T> {
    private boolean isPackage;

    public TypedTreeItem(T value, boolean isPackage) {
        super(value);
        this.isPackage = isPackage;
    }

    public boolean isPackage() {
        return isPackage;
    }
}
