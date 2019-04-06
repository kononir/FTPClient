package com.bsuir.ftpclient.ui.updater;

import com.bsuir.ftpclient.connection.ftp.data.file.FileComponent;
import com.bsuir.ftpclient.ui.updater.controller.TreeUpdaterController;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.List;

public class TreeUpdater {
    private TreeView<String> tree;

    private TreeUpdaterController controller = new TreeUpdaterController();

    public TreeUpdater(TreeView<String> tree) {
        this.tree = tree;
    }

    public void addAllToTree(List<FileComponent> fileComponents) {
        TreeItem<String> parentNode = tree.getRoot();
        addAllComponents(fileComponents, parentNode);
    }

    private void addAllComponents(List<FileComponent> fileComponents, TreeItem<String> parentNode) {
        for (FileComponent fileComponent : fileComponents) {
            String fileName = controller.controlGettingName(fileComponent);
            TreeItem<String> childNode = new TreeItem<>(fileName);

            if (controller.controlIsDirectory(fileComponent)) {
                addAllComponents(fileComponent.getChildren(), childNode);
            }

            parentNode.getChildren().add(childNode);
        }
    }
}
