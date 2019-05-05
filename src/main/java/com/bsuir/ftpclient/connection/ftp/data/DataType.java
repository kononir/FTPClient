package com.bsuir.ftpclient.connection.ftp.data;

public enum DataType {
    ASCII("A"),
    EBCDIC("E"),
    IMAGE("I");

    public String code;

    DataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
