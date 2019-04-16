package com.bsuir.ftpclient.connection.ftp.data.file.parser;

import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;

import java.util.List;

public interface FileNamesParser {
    List<ServerFile> parse(List<String> filesInformation);
}
