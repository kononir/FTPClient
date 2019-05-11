package com.bsuir.ftpclient.connection.ftp.data.file.parser.impl;

import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;
import com.bsuir.ftpclient.connection.ftp.data.file.parser.FileNameParser;

public class NLSTParser extends AbstractParser implements FileNameParser {
    private static final String DIRECTORY_REGEXP = "\\w+";
    private static final String SPLITTER_REGEXP = "/";

    /**
     * Parse information about server file to
     * {@link ServerFile} object.
     *
     * @param filePath
     *        file path at a server
     * @return parsing {@link ServerFile} object
     */
    @Override
    public ServerFile parse(String filePath) {
        String[] fileNames = filePath.split(SPLITTER_REGEXP);
        int last = fileNames.length - 1;

        return new ServerFile(fileNames[last], isDirectory(fileNames[last]));
    }

    @Override
    protected String getDirectoryRegexp() {
        return DIRECTORY_REGEXP;
    }
}
