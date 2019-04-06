package com.bsuir.ftpclient.connection.ftp.data.file.impl;

import com.bsuir.ftpclient.connection.ftp.data.file.FileComponent;

import java.util.ArrayList;
import java.util.List;

public class DirectoryComposite extends AbstractFileComponent implements FileComponent {
    private List<FileComponent> children = new ArrayList<>();

    public DirectoryComposite(String name) {
        super(name);
    }

    @Override
    public void add(FileComponent fileComponent) {
        children.add(fileComponent);
    }

    @Override
    public List<FileComponent> getChildren() {
        return children;
    }
}
