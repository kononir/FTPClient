package com.bsuir.ftpclient.connection.ftp.data.file.parser;

import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;

public interface FileNameParser {
    ServerFile parse(String fileInfo);
}
