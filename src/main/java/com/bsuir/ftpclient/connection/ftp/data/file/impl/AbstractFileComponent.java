package com.bsuir.ftpclient.connection.ftp.data.file.impl;

import com.bsuir.ftpclient.connection.ftp.data.file.FileComponent;

public abstract class AbstractFileComponent implements FileComponent {
    private String name;

    public AbstractFileComponent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
