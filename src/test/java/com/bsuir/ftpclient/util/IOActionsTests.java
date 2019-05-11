package com.bsuir.ftpclient.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

public class IOActionsTests {
    private static final int DATA_END = -1;
    private static final int SOME_DIGIT = 1;

    private static final String LINE_END = null;
    private static final String SOME_LINE = "1/some/some";

    private static final int ONE_ELEMENT = 1;

    private static final int FIRST = 0;

    @Test
    public void testWriteToStreamShouldWriteDataToOutputStreamWhenInputStreamHasData() throws IOException {
        BufferedInputStream inputStream = mock(BufferedInputStream.class);
        when(inputStream.read()).thenReturn(SOME_DIGIT, DATA_END);

        BufferedOutputStream outputStream = mock(BufferedOutputStream.class);

        IOActions actions = new IOActions();

        actions.writeToStream(inputStream, outputStream);

        verify(outputStream, atLeastOnce()).write(SOME_DIGIT);
        verify(outputStream, atLeastOnce()).flush();
    }

    @Test
    public void testWriteToStreamShouldDontWriteDataToOutputStreamWhenInputStreamDoesntHaveData() throws IOException {
        BufferedInputStream inputStream = mock(BufferedInputStream.class);
        when(inputStream.read()).thenReturn(DATA_END);

        BufferedOutputStream outputStream = mock(BufferedOutputStream.class);

        IOActions actions = new IOActions();

        actions.writeToStream(inputStream, outputStream);

        verify(outputStream, never()).write(anyInt());
        verify(outputStream, atLeastOnce()).flush();
    }

    @Test
    public void testReadListByReaderShouldReturnEmptyListWhenReaderDoesntReadLines() throws IOException {
        BufferedReader reader = mock(BufferedReader.class);
        when(reader.readLine()).thenReturn(LINE_END);

        IOActions actions = new IOActions();

        List<String> actual = actions.readListByReader(reader);

        Assert.assertTrue(actual.isEmpty());
    }

    @Test
    public void testReadListByReaderShouldReturnListWithOneLineWhenReaderReadOneLine() throws IOException {
        BufferedReader reader = mock(BufferedReader.class);
        when(reader.readLine()).thenReturn(SOME_LINE, LINE_END);

        IOActions actions = new IOActions();

        List<String> actual = actions.readListByReader(reader);

        Assert.assertEquals(actual.size(), ONE_ELEMENT);
        Assert.assertEquals(actual.get(FIRST), SOME_LINE);
    }
}
