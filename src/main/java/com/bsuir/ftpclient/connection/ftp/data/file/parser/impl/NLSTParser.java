package com.bsuir.ftpclient.connection.ftp.data.file.parser.impl;

import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;
import com.bsuir.ftpclient.connection.ftp.data.file.parser.FileNameParser;

public class NLSTParser extends AbstractParser implements FileNameParser {
    private static final String DIRECTORY_REGEXP = "\\w+";
    private static final String SPLITTER_REGEXP = "/";

    /**
     * Parse information about server file to
     * {@link ServerFile} object.<br>
     * Разобрать информацию о файле сервера и
     * создать на её основе объект {@link ServerFile}.
     *
     * @param filePath
     *        file path at a server<br>
     *        путь к файлу на сервере
     * @return parsing {@link ServerFile} object<br>
     *         полученный объект {@link ServerFile}
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
