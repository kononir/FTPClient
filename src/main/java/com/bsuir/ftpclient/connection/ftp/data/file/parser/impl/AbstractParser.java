package com.bsuir.ftpclient.connection.ftp.data.file.parser.impl;

import com.bsuir.ftpclient.connection.ftp.data.file.parser.FileNamesParser;

public abstract class AbstractParser implements FileNamesParser {

    protected boolean isDirectory(String fileInformation) {
        return fileInformation.matches(getDirectoryRegexp());
    }

    protected abstract String getDirectoryRegexp();
}
