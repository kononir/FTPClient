package com.bsuir.ftpclient.ui.updater.controller;

import com.bsuir.ftpclient.connection.ftp.data.file.FileComponent;
import com.bsuir.ftpclient.connection.ftp.data.file.impl.AbstractFileComponent;

public class TreeUpdaterController {

    public String controlGettingName(FileComponent fileComponent) {
        return ((AbstractFileComponent) fileComponent).getName();
    }

    public boolean controlIsDirectory(FileComponent fileComponent) {
        return (fileComponent.getChildren() != null);
    }
}
