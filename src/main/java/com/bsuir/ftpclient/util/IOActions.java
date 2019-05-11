package com.bsuir.ftpclient.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IOActions {

    public void writeToStream(BufferedInputStream inputStream, BufferedOutputStream outputStream)
            throws IOException {
        int currDigit;
        while ((currDigit = inputStream.read()) != -1) {
            outputStream.write(currDigit);
        }

        outputStream.flush();
    }

    public List<String> readListByReader(BufferedReader inputStream) throws IOException {
        List<String> list = new ArrayList<>();

        String fileListLine;
        while ((fileListLine = inputStream.readLine()) != null) {
            list.add(fileListLine);
        }

        return list;
    }
}
