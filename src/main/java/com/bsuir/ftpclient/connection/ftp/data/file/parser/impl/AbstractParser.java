package com.bsuir.ftpclient.connection.ftp.data.file.parser.impl;

import com.bsuir.ftpclient.connection.ftp.data.file.parser.FileNameParser;

public abstract class AbstractParser implements FileNameParser {

    /**
     * Determine whether the file is a directory.
     *
     * @param fileInfo
     *        specific information about file that contains
     *        sign of belonging to directory class
     * @return {@code <b>true</b>} if the file with this information
     *         is a directory, otherwise {@code <b>false</b>}
     */
    protected boolean isDirectory(String fileInfo) {
        return fileInfo.matches(getDirectoryRegexp());
    }

    protected abstract String getDirectoryRegexp();
}
