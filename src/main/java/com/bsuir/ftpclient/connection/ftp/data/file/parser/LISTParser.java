package com.bsuir.ftpclient.connection.ftp.data.file.parser;

import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;

import java.util.ArrayList;
import java.util.List;

public class LISTParser extends AbstractParser implements FileNamesParser {
    private static final String DIRECTORY_REGEXP = "d[rwx-]{9}.*";
    private static final String INFORMATION_SPLITTER_REGEXP = "(\\s|\\t)+";

    @Override
    public List<ServerFile> parse(List<String> filesInformation) {
        List<ServerFile> fileComponents = new ArrayList<>();

        for (String fileInformation : filesInformation) {
            String[] informationMas = fileInformation.split(INFORMATION_SPLITTER_REGEXP);

            int nameIndex = informationMas.length - 1;
            String fileName = informationMas[nameIndex];

            fileComponents.add(new ServerFile(fileName, isDirectory(fileInformation)));
        }

        return fileComponents;
    }

    @Override
    protected String getDirectoryRegexp() {
        return DIRECTORY_REGEXP;
    }
}
