package com.bsuir.ftpclient.connection.ftp.data.file;

public class ServerFile {
    private String name;
    private boolean isDirectory;

    public ServerFile(String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }
}
