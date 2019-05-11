package com.bsuir.ftpclient.connection.ftp.data.file.parser.impl;

import com.bsuir.ftpclient.connection.ftp.data.file.parser.FileNameParser;

public abstract class AbstractParser implements FileNameParser {

    /**
     * Determine whether the file is a directory.<br>
     * Определить, является ли файл каталогом.
     *
     * @param fileInfo
     *        specific information about file that contains sign
     *        of belonging to directory class<br>
     *        некоторая информация о файле, содержащая признак
     *        принадлежности к классу каталогов
     * @return <b>true</b> if the file with this information
     *         is a directory, otherwise <b>false</b><br>
     *         <b>true</b> если информация о файле говорит
     *         о том, что это каталог, в ином случае - <b>false</b>
     */
    protected boolean isDirectory(String fileInfo) {
        return fileInfo.matches(getDirectoryRegexp());
    }

    protected abstract String getDirectoryRegexp();
}
