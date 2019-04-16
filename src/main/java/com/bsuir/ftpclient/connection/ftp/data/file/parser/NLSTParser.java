package com.bsuir.ftpclient.connection.ftp.data.file.parser;

import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NLSTParser extends AbstractParser implements FileNamesParser {
    private static final String DIRECTORY_REGEXP = "\\w+";
    private static final String SPLITTER_REGEXP = "/";

    @Override
    public List<ServerFile> parse(List<String> filesInformation) {

        List<ServerFile> serverFiles = new ArrayList<>();

        for (String filePath : filesInformation) {
            String[] fileNames = filePath.split(SPLITTER_REGEXP);
            int last = fileNames.length - 1;

            serverFiles.add(new ServerFile(fileNames[last], isDirectory(fileNames[last])));
        }

        return serverFiles;
    }

    @Override
    protected String getDirectoryRegexp() {
        return DIRECTORY_REGEXP;
    }
}
