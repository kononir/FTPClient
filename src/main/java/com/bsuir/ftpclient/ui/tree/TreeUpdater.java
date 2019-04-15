package com.bsuir.ftpclient.ui.tree;

import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;
import com.bsuir.ftpclient.ui.tree.controller.TreeUpdaterController;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.List;

public class TreeUpdater {
    private TreeView<String> tree;

    private TreeUpdaterController controller = new TreeUpdaterController();

    public TreeUpdater(TreeView<String> tree) {
        this.tree = tree;
    }

    public TreeView<String> getTree() {
        return tree;
    }

    public void addAllComponents(List<ServerFile> files, TreeItem<String> parentNode) {
        for (ServerFile file : files) {
            String fileName = controller.controlGettingName(file);
            boolean isDirectory = controller.controlIsDirectory(file);
            TreeItem<String> childNode = new TypedTreeItem<>(fileName, isDirectory);

            parentNode.getChildren().add(childNode);
        }
    }

    public void clearTree() {
        TreeItem<String> rootNode = tree.getRoot();
        ObservableList<TreeItem<String>> list = rootNode.getChildren();
        list.clear();
    }
}
