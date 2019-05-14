package com.bsuir.ftpclient.logic.tree;

import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;

public class TreeUpdaterController {

    public String controlGettingName(ServerFile file) {
        return file.getName();
    }

    public boolean controlIsDirectory(ServerFile file) {
        return file.isDirectory();
    }
}
