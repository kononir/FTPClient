package com.bsuir.ftpclient.connection.ftp.control;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControlConnectionActionsTests {
    private static final String ONE_LINE = "220 Hello!";
    private static final String TWO_LINES = "220-Hello\n220 World!";
    private static final String THREE_LINES = "220-Hello\nSOME TEXT...\n220 World!";

    private static final String REQUEST = "User anonymous";
    private static final String REQUEST_WITH_NL = "User anonymous\r\n";

    private static final Socket NULL_SOCKET = null;

    private static final boolean CLOSED = true;
    private static final boolean NOT_CLOSED = false;

    @Test
    public void testReceiveResponseShouldReturnOneLineWhenReceiveOneLine()
            throws IOException, ControlConnectionException {
        String actual = positiveTestReceive(ONE_LINE);

        Assert.assertEquals(ONE_LINE, actual);
    }

    @Test
    public void testReceiveResponseShouldReturnTwoLinesWhenReceiveTwoLine()
            throws ControlConnectionException, IOException {
        String actual = positiveTestReceive(TWO_LINES);

        Assert.assertEquals(TWO_LINES, actual);
    }

    @Test
    public void testReceiveResponseShouldReturnThreeLinesWhenReceiveThreeLine()
            throws ControlConnectionException, IOException {
        String actual = positiveTestReceive(THREE_LINES);

        Assert.assertEquals(THREE_LINES, actual);
    }

    @Test(expected = ControlConnectionException.class)
    public void testReceiveResponseShouldThrowConnectionNotExistExceptionWhenConnectionNotExist()
            throws ControlConnectionException {
        Connection connection = mock(Connection.class);
        when(connection.isClosed()).thenReturn(CLOSED);
        when(connection.getSocket()).thenReturn(NULL_SOCKET);

        ControlConnectionActions actions = new ControlConnectionActions(connection);

        actions.receiveResponse();

        Assert.fail();
    }

    private String positiveTestReceive(String expected)
            throws IOException, ControlConnectionException {
        Connection connection = mock(Connection.class);
        when(connection.isClosed()).thenReturn(NOT_CLOSED);

        Socket socket = mock(Socket.class);
        when(connection.getSocket()).thenReturn(socket);

        InputStream inputStream = new ByteArrayInputStream(expected.getBytes());
        when(socket.getInputStream()).thenReturn(inputStream);

        ControlConnectionActions actions = new ControlConnectionActions(connection);

        return actions.receiveResponse();
    }

    @Test
    public void testSendRequestShouldReturnRequestWhenRetrieveStringFromStream()
            throws IOException, ControlConnectionException {
        Connection connection = mock(Connection.class);
        when(connection.isClosed()).thenReturn(NOT_CLOSED);

        Socket socket = mock(Socket.class);
        when(connection.getSocket()).thenReturn(socket);

        OutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        ControlConnectionActions actions = new ControlConnectionActions(connection);

        actions.sendRequest(REQUEST);

        String actual = outputStream.toString();

        Assert.assertEquals(REQUEST_WITH_NL, actual);
    }

    @Test(expected = ControlConnectionException.class)
    public void testSendRequestShouldThrowConnectionNotExistExceptionWhenConnectionNotExist()
            throws ControlConnectionException {
        Connection connection = mock(Connection.class);
        when(connection.getSocket()).thenReturn(NULL_SOCKET);
        when(connection.isClosed()).thenReturn(CLOSED);

        ControlConnectionActions actions = new ControlConnectionActions(connection);

        actions.sendRequest(REQUEST);

        Assert.fail();
    }
}
