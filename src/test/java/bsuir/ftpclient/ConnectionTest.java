package test.java.bsuir.ftpclient;

import main.java.bsuir.ftpclient.connection.Connection;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionTest {

    @org.junit.jupiter.api.Test
    void connectPositive() {
        Connection connection = new Connection();
        String expected = "220";
        String actual = null;
        try {
            actual = connection.connect("91.122.30.115").substring(0, 3);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assertEquals(expected, actual, "Positive test failed!");
        }
    }

    /*@org.junit.jupiter.api.Test
    void connect*/

    @org.junit.jupiter.api.Test
    void disconnect() {
        fail();
    }

    @org.junit.jupiter.api.Test
    void isClosed() {
        fail();
    }
}