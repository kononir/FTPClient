package com.bsuir.ftpclient.connection.ftp.data.file.parser;

import com.bsuir.ftpclient.connection.ftp.data.file.FileComponent;
import com.bsuir.ftpclient.connection.ftp.data.file.impl.DirectoryComposite;
import com.bsuir.ftpclient.connection.ftp.data.file.impl.ServerFile;

import java.util.ArrayList;
import java.util.List;

public class FileNameParser {
    private static final String DIRECTORY_REGEXP = "d[rwx-]{9}.*";
    private static final String INFORMATION_SPLITTER_REGEXP = "(\\s|\\t)+";

    public List<FileComponent> parse(List<String> filesInformation) {
        List<FileComponent> fileComponents = new ArrayList<>();

        for (String fileInformation : filesInformation) {
            String[] informationMas = fileInformation.split(INFORMATION_SPLITTER_REGEXP);

            int nameIndex = informationMas.length - 1;
            String fileName = informationMas[nameIndex];

            FileComponent fileComponent;
            if (isDirectory(fileInformation)) {
                fileComponent = new DirectoryComposite(fileName);
            } else {
                fileComponent = new ServerFile(fileName);
            }

            fileComponents.add(fileComponent);
        }

        return fileComponents;
    }

    private boolean isDirectory(String fileInformation) {
        return fileInformation.matches(DIRECTORY_REGEXP);
    }
}
