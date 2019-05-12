package com.bsuir.ftpclient.connection.ftp.data;

public enum DataType {
    ASCII("A N"),
    L8("L 8"),
    BINARY("I N");

    public String code;

    DataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
