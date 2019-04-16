package com.bsuir.ftpclient.connection.ftp.data.file.parser;

public abstract class AbstractParser implements FileNamesParser {

    protected boolean isDirectory(String fileInformation) {
        return fileInformation.matches(getDirectoryRegexp());
    }

    protected abstract String getDirectoryRegexp();
}
