package com.bsuir.ftpclient.connection.ftp.data.file;

import java.util.List;

public interface FileComponent {
    void add(FileComponent fileComponent);
    List<FileComponent> getChildren();
}
