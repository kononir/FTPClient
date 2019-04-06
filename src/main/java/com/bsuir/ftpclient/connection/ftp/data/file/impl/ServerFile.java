package com.bsuir.ftpclient.connection.ftp.data.file.impl;

import com.bsuir.ftpclient.connection.ftp.data.file.FileComponent;

import java.util.List;

public class ServerFile extends AbstractFileComponent implements FileComponent {

    public ServerFile(String name) {
        super(name);
    }

    @Override
    public void add(FileComponent fileComponent) {
    }

    @Override
    public List<FileComponent> getChildren() {
        return null;
    }
}
