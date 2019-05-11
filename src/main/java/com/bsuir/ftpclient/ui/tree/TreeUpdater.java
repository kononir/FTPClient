package com.bsuir.ftpclient.ui.tree;

import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
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

            if (isDirectory) {
                childNode.setExpanded(true);
            }

            parentNode.getChildren().add(childNode);
        }
    }

    public void clearTree() {
        TreeItem<String> root = new TypedTreeItem<>("/", true);
        root.setExpanded(true);
        tree.setRoot(root);
    }

    public String getAbsolutePath(TreeItem<String> node) {
        TreeItem<String> parent = node.getParent();

        if ((parent != null)) {
            return getParentsPath(parent) + "/" + node.getValue();
        } else {
            return "/";
        }
    }

    public String getParentsPath(TreeItem<String> node) {
        TreeItem<String> parent = node.getParent();

        if ((parent != null)) {
            return getParentsPath(parent) + "/" + node.getValue();
        } else {
            return "";
        }
    }
}
