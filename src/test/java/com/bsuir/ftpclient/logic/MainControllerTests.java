package com.bsuir.ftpclient.logic;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.FtpConnectionException;
import com.bsuir.ftpclient.connection.ftp.control.manager.SendingManager;
import com.bsuir.ftpclient.connection.ftp.data.DataType;
import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;
import com.bsuir.ftpclient.connection.ftp.data.manager.DataManager;
import com.bsuir.ftpclient.ui.window.exception.MainControllerException;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

public class MainControllerTests {
    private static final String SOME_PATH = "some";
    private static final String SLASH_SOME_PATH = "/some";

    private static final String ANY = "any";

    private static final String CONNECTING_TEST_COMMAND = "";
    private static final String DISCONNECTING_TEST_COMMAND = "QUIT";
    private static final String DELETING_FILE_TEST_COMMAND = "DELE any";
    private static final String LOADING_FILE_TEST_COMMAND = "RETR any";
    private static final String LOADING_NESTED_FILE_TEST_COMMAND = "RETR /some/any";
    private static final String SAVING_FILE_TEST_COMMAND = "STOR any";
    private static final String SAVING_NESTED_FILE_TEST_COMMAND = "STOR /some/any";
    private static final String LOADING_FILE_LIST_TEST_COMMAND = "NLST /some";
    private static final String LOADING_NESTED_CATALOGUE_FILE_LIST_TEST_COMMAND = "NLST /some/some";
    private static final String LOGIN_TEST_COMMAND = "USER any";
    private static final String PASSWORD_TEST_COMMAND = "PASS any";
    private static final String CREATING_CATALOGUE_TEST_COMMAND = "MKD /some";
    private static final String CREATING_NESTED_CATALOGUE_TEST_COMMAND = "MKD /some/some";
    private static final String DELETING_CATALOGUE_TEST_COMMAND = "RMD /some";
    private static final String CHANGE_DIRECTORY_TEST_COMMAND = "CWD /some";
    private static final String CHANGE_DATA_TYPE_TEST_COMMAND = "TYPE A N";

    private static final String CATALOGUE_WITH_EMPTY_NESTED_PATH = "src/test/resources/1/some";
    private static final String CATALOGUE_WITH_FILE_PATH = "src/test/resources/2/some";

    private static final String TEST_CONNECTION_INFO = "127,0,0,1,0,21";
    private static final String TEST_IP = "127.0.0.1";
    private static final int TEST_PORT = 21;

    private static final int TIMEOUT = 5000;

    private MainController controller;

    private Connection controlConnection;
    private Exchanger<String> responseExchanger;
    private Exchanger<List<ServerFile>> listExchanger;
    private SendingManager sendingManager;
    private DataManager dataManager;

    @Before
    public void setUp() throws TimeoutException, InterruptedException {
        controlConnection = mock(Connection.class);
        responseExchanger = (Exchanger<String>) mock(Exchanger.class);
        listExchanger = (Exchanger<List<ServerFile>>) mock(Exchanger.class);
        dataManager = mock(DataManager.class);
        sendingManager = mock(SendingManager.class);

        controller = new MainController(
                controlConnection,
                responseExchanger,
                listExchanger,
                sendingManager,
                dataManager
        );

        when(responseExchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS))
                .thenReturn(TEST_CONNECTION_INFO);
    }

    @Test
    public void testControlConnecting() throws MainControllerException, FtpConnectionException {
        controller.controlConnecting(TEST_IP);

        verify(controlConnection, atLeastOnce()).connect(TEST_IP, TEST_PORT);
        verify(sendingManager, atLeastOnce()).send(CONNECTING_TEST_COMMAND);
    }

    @Test(expected = MainControllerException.class)
    public void testControlConnectingShouldThrowMainControllerExceptionWhenConnectThrowException()
            throws MainControllerException, FtpConnectionException {
        when(controlConnection.connect(TEST_IP, TEST_PORT))
                .thenThrow(new FtpConnectionException("Test exception"));

        controller.controlConnecting(TEST_IP);

        Assert.fail();
    }

    @Test
    public void testControlDisconnecting() {
        controller.controlDisconnecting();

        verify(sendingManager, atLeastOnce()).send(DISCONNECTING_TEST_COMMAND);
    }

    @Test
    public void testControlAuthenticating() {
        Pair<String, String> authenticationPair = new Pair<>(ANY, ANY);
        controller.controlAuthenticating(authenticationPair);

        verify(sendingManager, atLeastOnce()).send(LOGIN_TEST_COMMAND);
        verify(sendingManager, atLeastOnce()).send(PASSWORD_TEST_COMMAND);
    }

    @Test
    public void testControlCreatingCatalogue() {
        controller.controlCreatingCatalogue(SLASH_SOME_PATH);

        verify(sendingManager, atLeastOnce()).send(CREATING_CATALOGUE_TEST_COMMAND);
    }

    @Test
    public void testControlDeletingCatalogue() {
        controller.controlDeletingCatalogue(SLASH_SOME_PATH);

        verify(sendingManager, atLeastOnce()).send(DELETING_CATALOGUE_TEST_COMMAND);
    }

    @Test
    public void testControlLoadingCatalogueShouldLoadFileListTwoTimesWhenGivenCataloguePathWithNested()
            throws MainControllerException, TimeoutException, InterruptedException {
        List<ServerFile> catalogueFileList = Collections.singletonList(
                new ServerFile(SOME_PATH, true));
        List<ServerFile> nestedCatalogueFileList = Collections.emptyList();

        doReturn(catalogueFileList)
                .doReturn(nestedCatalogueFileList)
                .when(listExchanger).exchange(null, TIMEOUT, TimeUnit.MILLISECONDS);

        controller.controlLoadingCatalogue(SLASH_SOME_PATH, CATALOGUE_WITH_EMPTY_NESTED_PATH);

        verify(sendingManager, atLeastOnce()).send(LOADING_FILE_LIST_TEST_COMMAND);
        verify(sendingManager, atLeastOnce()).send(LOADING_NESTED_CATALOGUE_FILE_LIST_TEST_COMMAND);
    }

    @Test
    public void testControlLoadingCatalogueShouldLoadFileListAndLoadFileWhenGivenCataloguePathWithFile()
            throws MainControllerException, TimeoutException, InterruptedException {
        List<ServerFile> catalogueFileList = Collections.singletonList(
                new ServerFile(ANY, false));

        doReturn(catalogueFileList).when(listExchanger).exchange(null, TIMEOUT, TimeUnit.MILLISECONDS);

        controller.controlLoadingCatalogue(SLASH_SOME_PATH, CATALOGUE_WITH_EMPTY_NESTED_PATH);

        verify(sendingManager, atLeastOnce()).send(LOADING_FILE_LIST_TEST_COMMAND);
        verify(sendingManager, atLeastOnce()).send(LOADING_NESTED_FILE_TEST_COMMAND);
    }

    @Test
    public void testControlSavingCatalogueShouldCreateCatalogueTwoTimesWhenGivenCataloguePathWithNested()
            throws MainControllerException {
        controller.controlSavingCatalogue(CATALOGUE_WITH_EMPTY_NESTED_PATH, SLASH_SOME_PATH);

        verify(sendingManager, atLeastOnce()).send(CREATING_CATALOGUE_TEST_COMMAND);
        verify(sendingManager, atLeastOnce()).send(CREATING_NESTED_CATALOGUE_TEST_COMMAND);
    }

    @Test
    public void testControlSavingCatalogueShouldCreateCatalogueAndSaveFileWhenGivenCataloguePathWithFile()
            throws MainControllerException {
        controller.controlSavingCatalogue(CATALOGUE_WITH_FILE_PATH, SLASH_SOME_PATH);

        verify(sendingManager, atLeastOnce()).send(CREATING_CATALOGUE_TEST_COMMAND);
        verify(sendingManager, atLeastOnce()).send(SAVING_NESTED_FILE_TEST_COMMAND);
    }

    @Test
    public void testControlDeletingFile() {
        controller.controlDeletingFile(ANY);

        verify(sendingManager, atLeastOnce()).send(DELETING_FILE_TEST_COMMAND);
    }

    @Test
    public void testControlLoadingFile() throws MainControllerException {
        controller.controlLoadingFile(ANY, SOME_PATH);

        verify(sendingManager, atLeastOnce()).send(LOADING_FILE_TEST_COMMAND);
        verify(dataManager, atLeastOnce()).manageWork(any());
    }

    @Test
    public void testControlSavingFile() throws MainControllerException {
        controller.controlSavingFile(ANY, ANY);

        verify(sendingManager, atLeastOnce()).send(SAVING_FILE_TEST_COMMAND);
        verify(dataManager, atLeastOnce()).manageWork(any());
    }

    @Test
    public void testEstablishDataConnectionShouldReturnFtpConnectionWhenGetValidResponseToPASV()
            throws MainControllerException {
        Connection connection = controller.establishDataConnection();

        Assert.assertEquals(TEST_IP, connection.getHostAddress());
        Assert.assertEquals(TEST_PORT, connection.getPort());
    }

    @Test(expected = MainControllerException.class)
    public void testEstablishDataConnectionShouldThrowMainControllerExceptionWhenExchangerThrowException()
            throws TimeoutException, InterruptedException, MainControllerException {
        when(responseExchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS))
                .thenThrow(new TimeoutException("Test exception"));

        controller.establishDataConnection();

        Assert.fail();
    }

    @Test
    public void testControlClose() {
        controller.controlClose();

        verify(sendingManager, atLeastOnce()).killAllSenders();
        verify(dataManager, atLeastOnce()).shutdown();
    }

    @Test
    public void testControlLoadingFileListShouldReturnListOfServerFilesWhenReceivedIt()
            throws TimeoutException, InterruptedException, MainControllerException {
        List<ServerFile> expected = Collections.emptyList();
        when(listExchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS)).thenReturn(expected);

        List<ServerFile> actual = controller.controlLoadingFileList(SLASH_SOME_PATH);

        Assert.assertEquals(expected, actual);

        verify(sendingManager, atLeastOnce()).send(LOADING_FILE_LIST_TEST_COMMAND);
        verify(dataManager, atLeastOnce()).manageWork(any());
    }

    @Test(expected = MainControllerException.class)
    public void testControlLoadingFileListShouldThrowMainControllerExceptionWhenExchangeThrowException()
            throws TimeoutException, InterruptedException, MainControllerException {
        when(listExchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS))
                .thenThrow(new InterruptedException());

        controller.controlLoadingFileList(ANY);

        Assert.fail();
    }

    @Test
    public void testControlChangeWorkingDirectory() {
        controller.controlChangeWorkingDirectory(SLASH_SOME_PATH);

        verify(sendingManager, atLeastOnce()).send(CHANGE_DIRECTORY_TEST_COMMAND);
    }

    @Test
    public void testControlChangeDataType() {
        controller.controlChangeDataType(DataType.ASCII);

        verify(sendingManager, atLeastOnce()).send(CHANGE_DATA_TYPE_TEST_COMMAND);
    }
}
