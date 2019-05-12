package com.bsuir.ftpclient.connection.ftp.control;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.FtpConnectionException;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControlConnectionActionsTests {
    private static final String ONE_LINE = "220 Hello!\n";
    private static final String TWO_LINES = "220-Hello\n220 World!\n";
    private static final String THREE_LINES = "220-Hello\nSOME TEXT...\n220 World!\n";
    private static final String TWO_LINES_WITH_WAITING_COMMAND = "100 Wait another command\n220 Hello!\n";

    private static final String REQUEST = "User anonymous";
    private static final String REQUEST_WITH_NL = "User anonymous\r\n";

    @Test
    public void testReceiveResponseShouldReturnOneLineWhenReceiveOneLine() throws FtpConnectionException {
        String actual = positiveTestReceive(ONE_LINE);

        Assert.assertEquals(ONE_LINE, actual);
    }

    @Test
    public void testReceiveResponseShouldReturnTwoLinesWhenReceiveTwoLine() throws FtpConnectionException {
        String actual = positiveTestReceive(TWO_LINES);

        Assert.assertEquals(TWO_LINES, actual);
    }

    @Test
    public void testReceiveResponseShouldReturnThreeLinesWhenReceiveThreeLine() throws FtpConnectionException {
        String actual = positiveTestReceive(THREE_LINES);

        Assert.assertEquals(THREE_LINES, actual);
    }

    @Test
    public void testReceiveResponseShouldReturnTwoLinesWithWaitingCommandWhenReceiveTwoLines()
            throws FtpConnectionException {
        String actual = positiveTestReceive(TWO_LINES_WITH_WAITING_COMMAND);

        Assert.assertEquals(TWO_LINES_WITH_WAITING_COMMAND, actual);
    }

    @Test(expected = FtpConnectionException.class)
    public void testReceiveResponseShouldThrowFtpConnectionExceptionWhenConnectionNotExist()
            throws FtpConnectionException {
        Connection connection = mock(Connection.class);
        when(connection.getInputStream()).thenThrow(new FtpConnectionException("Test exception"));

        ControlConnectionActions actions = new ControlConnectionActions(connection);

        actions.receiveResponse();

        Assert.fail();
    }

    private String positiveTestReceive(String expected) throws FtpConnectionException {
        Connection connection = mock(Connection.class);

        InputStream inputStream = new ByteArrayInputStream(expected.getBytes());
        when(connection.getInputStream()).thenReturn(inputStream);

        ControlConnectionActions actions = new ControlConnectionActions(connection);

        return actions.receiveResponse();
    }

    @Test
    public void testSendRequestShouldReturnRequestWhenRetrieveStringFromStream() throws FtpConnectionException {
        Connection connection = mock(Connection.class);

        OutputStream outputStream = new ByteArrayOutputStream();
        when(connection.getOutputStream()).thenReturn(outputStream);

        ControlConnectionActions actions = new ControlConnectionActions(connection);

        actions.sendRequest(REQUEST);

        String actual = outputStream.toString();

        Assert.assertEquals(REQUEST_WITH_NL, actual);
    }

    @Test(expected = FtpConnectionException.class)
    public void testSendRequestShouldThrowFtpConnectionExceptionWhenConnectionNotExist()
            throws FtpConnectionException {
        Connection connection = mock(Connection.class);
        when(connection.getOutputStream()).thenThrow(new FtpConnectionException("Test exception"));

        ControlConnectionActions actions = new ControlConnectionActions(connection);

        actions.sendRequest(REQUEST);

        Assert.fail();
    }
}
