package com.bsuir.ftpclient.logic;

import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.FtpConnectionException;
import com.bsuir.ftpclient.connection.ftp.control.manager.SendingManager;
import com.bsuir.ftpclient.connection.ftp.data.DataType;
import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;
import com.bsuir.ftpclient.connection.ftp.data.file.parser.FileNameParser;
import com.bsuir.ftpclient.connection.ftp.data.file.parser.impl.NLSTParser;
import com.bsuir.ftpclient.connection.ftp.data.manager.DataManager;
import com.bsuir.ftpclient.connection.ftp.data.manager.work.FileListReceiving;
import com.bsuir.ftpclient.connection.ftp.data.manager.work.FileReceiving;
import com.bsuir.ftpclient.connection.ftp.data.manager.work.FileSending;
import com.bsuir.ftpclient.ui.window.exception.MainControllerException;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mockito.Mockito.*;

public class MainController {
    private static final Logger LOGGER = Logger.getRootLogger();

    private static final int CONTROL_PORT = 21;
    private Connection controlConnection = new Connection();

    private static final int TIMEOUT = 5000;
    private Exchanger<String> responseExchanger = new Exchanger<>();
    private Exchanger<List<ServerFile>> listExchanger = new Exchanger<>();

    private SendingManager sendingManager = new SendingManager(controlConnection, responseExchanger);
    private DataManager dataManager = new DataManager();

    //----- For tests -----
    private static final String SLASH_PATH = "/";
    private static final String SOME_PATH = "some";
    private static final String SLASH_SOME_PATH = "/some";
    private static final String SOME_SOME_PATH = "some/some";

    private static final String ANY = "any";

    private static final String CONNECTING_TEST_COMMAND = "";
    private static final String DISCONNECTING_TEST_COMMAND = "QUIT";
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

    private static final String CATALOGUE_WITH_EMPTY_NESTED_PATH = "src/test/resources/1/some";
    private static final String CATALOGUE_WITH_FILE_PATH = "src/test/resources/2/some";

    private static final String TEST_CONNECTION_INFO = "127,0,0,1,0,21";
    private static final String TEST_IP = "127.0.0.1";
    private static final int TEST_PORT = 21;

    @Before
    public void setUp() throws TimeoutException, InterruptedException {
        controlConnection = mock(Connection.class);
        dataManager = mock(DataManager.class);
        sendingManager = mock(SendingManager.class);
        responseExchanger = (Exchanger<String>) mock(Exchanger.class);
        listExchanger = (Exchanger<List<ServerFile>>) mock(Exchanger.class);

        when(responseExchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS))
                .thenReturn(TEST_CONNECTION_INFO);
    }
    //---------------------

    public void controlConnecting(String hostname) throws MainControllerException {
        try {
            controlConnection.connect(hostname, CONTROL_PORT);
            String connectCommand = "";
            sendingManager.send(connectCommand);
        } catch (FtpConnectionException e) {
            LOGGER.error("Connecting error.", e);
            throw new MainControllerException("Connecting error.", e);
        }
    }

    @Test
    public void testControlConnecting() throws MainControllerException, FtpConnectionException {
        controlConnecting(TEST_IP);

        verify(controlConnection, atLeastOnce()).connect(TEST_IP, CONTROL_PORT);
        verify(sendingManager, atLeastOnce()).send(CONNECTING_TEST_COMMAND);
    }

    @Test(expected = MainControllerException.class)
    public void testControlConnectingShouldThrowMainControllerExceptionWhenConnectThrowException()
            throws MainControllerException, FtpConnectionException {
        when(controlConnection.connect(TEST_IP, CONTROL_PORT))
                .thenThrow(new FtpConnectionException("Test exception"));

        controlConnecting(TEST_IP);

        Assert.fail();
    }

    public void controlDisconnecting() {
        String exitCommand = "QUIT";
        sendingManager.send(exitCommand);
    }

    @Test
    public void testControlDisconnecting() {
        controlDisconnecting();

        verify(sendingManager, atLeastOnce()).send(DISCONNECTING_TEST_COMMAND);
    }

    public void controlAuthenticating(Pair<String, String> authenticationPair) {
        String loginCommand = "USER " + authenticationPair.getKey();
        sendingManager.send(loginCommand);

        String passwordCommand = "PASS " + authenticationPair.getValue();
        sendingManager.send(passwordCommand);
    }

    @Test
    public void testControlAuthenticating() {
        Pair<String, String> authenticationPair = new Pair<>(ANY, ANY);
        controlAuthenticating(authenticationPair);

        verify(sendingManager, atLeastOnce()).send(LOGIN_TEST_COMMAND);
        verify(sendingManager, atLeastOnce()).send(PASSWORD_TEST_COMMAND);
    }

    public void controlCreatingCatalogue(String catalogueName) {
        String catalogueCreatingCommand = "MKD " + catalogueName;
        sendingManager.send(catalogueCreatingCommand);
    }

    @Test
    public void testControlCreatingCatalogue() {
        controlCreatingCatalogue(SLASH_SOME_PATH);

        verify(sendingManager, atLeastOnce()).send(CREATING_CATALOGUE_TEST_COMMAND);
    }

    public void controlDeletingCatalogue(String catalogueName) {
        String catalogueDeletingCommand = "RMD " + catalogueName;
        sendingManager.send(catalogueDeletingCommand);
    }

    @Test
    public void testControlDeletingCatalogue() {
        controlDeletingCatalogue(SLASH_SOME_PATH);

        verify(sendingManager, atLeastOnce()).send(DELETING_CATALOGUE_TEST_COMMAND);
    }

    public void controlLoadingCatalogue(String fromDirectoryPath, String toDirectoryPath)
            throws MainControllerException {
        File directory = new File(toDirectoryPath);
        if (!directory.exists()) {
            directory.mkdir();
        }

        List<ServerFile> files = controlLoadingFileList(fromDirectoryPath);
        for (ServerFile serverFile : files) {
            String name = serverFile.getName();
            String newFromPath = addToServerPath(fromDirectoryPath, name);
            String newToPath = addToClientPath(toDirectoryPath, name);

            if (serverFile.isDirectory()) {
                controlLoadingCatalogue(newFromPath, newToPath);
            } else {
                controlLoadingFile(newFromPath, newToPath);
            }
        }
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

        controlLoadingCatalogue(SLASH_SOME_PATH, CATALOGUE_WITH_EMPTY_NESTED_PATH);

        verify(sendingManager, atLeastOnce()).send(LOADING_FILE_LIST_TEST_COMMAND);
        verify(sendingManager, atLeastOnce()).send(LOADING_NESTED_CATALOGUE_FILE_LIST_TEST_COMMAND);
    }

    @Test
    public void testControlLoadingCatalogueShouldLoadFileListAndLoadFileWhenGivenCataloguePathWithFile()
            throws MainControllerException, TimeoutException, InterruptedException {
        List<ServerFile> catalogueFileList = Collections.singletonList(
                new ServerFile(ANY, false));

        doReturn(catalogueFileList).when(listExchanger).exchange(null, TIMEOUT, TimeUnit.MILLISECONDS);

        controlLoadingCatalogue(SLASH_SOME_PATH, CATALOGUE_WITH_EMPTY_NESTED_PATH);

        verify(sendingManager, atLeastOnce()).send(LOADING_FILE_LIST_TEST_COMMAND);
        verify(sendingManager, atLeastOnce()).send(LOADING_NESTED_FILE_TEST_COMMAND);
    }

    public void controlSavingCatalogue(String fromDirectoryPath, String toDirectoryPath)
            throws MainControllerException {
        controlCreatingCatalogue(toDirectoryPath);

        File directory = new File(fromDirectoryPath);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                String name = file.getName();
                String newFromPath = addToClientPath(fromDirectoryPath, name);
                String newToPath = addToServerPath(toDirectoryPath, name);

                if (file.isDirectory()) {
                    controlSavingCatalogue(newFromPath, newToPath);
                } else {
                    controlSavingFile(newFromPath, newToPath);
                }
            }
        }
    }

    @Test
    public void testControlSavingCatalogueShouldCreateCatalogueTwoTimesWhenGivenCataloguePathWithNested()
            throws MainControllerException {
        controlSavingCatalogue(CATALOGUE_WITH_EMPTY_NESTED_PATH, SLASH_SOME_PATH);

        verify(sendingManager, atLeastOnce()).send(CREATING_CATALOGUE_TEST_COMMAND);
        verify(sendingManager, atLeastOnce()).send(CREATING_NESTED_CATALOGUE_TEST_COMMAND);
    }

    @Test
    public void testControlSavingCatalogueShouldCreateCatalogueAndSaveFileWhenGivenCataloguePathWithFile()
            throws MainControllerException {
        controlSavingCatalogue(CATALOGUE_WITH_FILE_PATH, SLASH_SOME_PATH);

        verify(sendingManager, atLeastOnce()).send(CREATING_CATALOGUE_TEST_COMMAND);
        verify(sendingManager, atLeastOnce()).send(SAVING_NESTED_FILE_TEST_COMMAND);
    }

    private String addToClientPath(String oldPath, String name) {
        return oldPath + "/" + name;
    }

    @Test
    public void testAddToClientPathShouldReturnNewPathWithSeparatorSlashWhenGivenSomePath() {
        String actual = addToClientPath(SOME_PATH, SOME_PATH);

        Assert.assertEquals(SOME_SOME_PATH, actual);
    }

    /**
     * Add new level to old path on the server.<br>
     * Добавить новый уровень к старому пути на сервере
     *
     * @param oldPath old path, root directory to file or catalogue
     *                with specified name<br>
     *                старый путь, корневая директория для файла или
     *                каталога с указанным именем
     * @param name    name of file or catalogue that must be added<br>
     *                имя файла или каталога, которое будет добавлено
     * @return new path on the server<br>
     *         новый путь на сервере
     */
    private String addToServerPath(String oldPath, String name) {
        String newPath;

        if ("/".equals(oldPath)) {
            newPath = oldPath + name;
        } else {
            newPath = oldPath + "/" + name;
        }

        return newPath;
    }

    @Test
    public void testAddToServerPathShouldReturnNewPathWithSlashWhenGivenSlash() {
        String actual = addToServerPath(SLASH_PATH, SOME_PATH);

        Assert.assertEquals(SLASH_SOME_PATH, actual);
    }

    @Test
    public void testAddToServerPathShouldReturnNewPathWithSeparatorSlashWhenGivenSomePath() {
        String actual = addToServerPath(SOME_PATH, SOME_PATH);

        Assert.assertEquals(SOME_SOME_PATH, actual);
    }

    public void controlDeletingFile(String fileName) {
        String fileDeletingCommand = "DELE " + fileName;
        sendingManager.send(fileDeletingCommand);
    }

    public void controlLoadingFile(String fileName, String toDirectoryPath) throws MainControllerException {
        Connection dataConnection = establishDataConnection();

        String catalogueLoadingCommand = "RETR " + fileName;
        sendingManager.send(catalogueLoadingCommand);

        FileReceiving fileReceiving = new FileReceiving(dataConnection, toDirectoryPath);
        dataManager.manageWork(fileReceiving);
    }

    @Test
    public void testControlLoadingFile() throws MainControllerException {
        controlLoadingFile(ANY, SOME_PATH);

        verify(sendingManager, atLeastOnce()).send(LOADING_FILE_TEST_COMMAND);
        verify(dataManager, atLeastOnce()).manageWork(any());
    }

    public void controlSavingFile(String fromFile, String toFile) throws MainControllerException {
        Connection dataConnection = establishDataConnection();

        String fileSavingCommand = "STOR " + toFile;
        sendingManager.send(fileSavingCommand);

        FileSending fileSending = new FileSending(dataConnection, fromFile);
        dataManager.manageWork(fileSending);
    }

    @Test
    public void testControlSavingFile() throws MainControllerException {
        controlSavingFile(ANY, ANY);

        verify(sendingManager, atLeastOnce()).send(SAVING_FILE_TEST_COMMAND);
        verify(dataManager, atLeastOnce()).manageWork(any());
    }

    private Connection establishDataConnection() throws MainControllerException {
        String passiveModeCommand = "PASV";
        sendingManager.send(passiveModeCommand);

        Connection dataConnection = new Connection();
        try {
            String response = responseExchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS);

            Pattern pattern = Pattern.compile("(\\d)+(,(\\d)+){5}");
            Matcher matcher = pattern.matcher(response);

            matcher.find();
            String addressDigitsStr = matcher.group();

            String[] addressDigits = addressDigitsStr.split(",");

            String ipAddress = addressDigits[0] + '.' + addressDigits[1] + '.' + addressDigits[2] + '.' + addressDigits[3];
            int port = Integer.parseInt(addressDigits[4]) * 256 + Integer.parseInt(addressDigits[5]);

            dataConnection.connect(ipAddress, port);
        } catch (InterruptedException | TimeoutException | FtpConnectionException e) {
            LOGGER.error("Error when establish data connection.", e);
            throw new MainControllerException("Error when establish data connection.", e);
        }

        return dataConnection;
    }

    @Test
    public void testEstablishDataConnectionShouldReturnFtpConnectionWhenGetValidResponseToPASV()
            throws MainControllerException {
        Connection connection = establishDataConnection();

        Assert.assertEquals(TEST_IP, connection.getHostAddress());
        Assert.assertEquals(TEST_PORT, connection.getPort());
    }

    @Test(expected = MainControllerException.class)
    public void testEstablishDataConnectionShouldThrowMainControllerExceptionWhenExchangerThrowException()
            throws TimeoutException, InterruptedException, MainControllerException {
        when(responseExchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS))
                .thenThrow(new TimeoutException("Test exception"));

        establishDataConnection();

        Assert.fail();
    }

    public void controlClose() {
        sendingManager.killAllSenders();
        dataManager.shutdown();
    }

    public List<ServerFile> controlLoadingFileList(String directoryName)
            throws MainControllerException {
        List<ServerFile> serverFiles;
        try {
            Connection dataConnection = establishDataConnection();
            String fileListLoadingCommand = "NLST " + directoryName; // MLSD/LIST/NLST (another format of request - another parser)
            sendingManager.send(fileListLoadingCommand);

            FileNameParser parser = new NLSTParser();
            FileListReceiving fileListReceiving = new FileListReceiving(dataConnection, parser, listExchanger);
            dataManager.manageWork(fileListReceiving);

            serverFiles = listExchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | TimeoutException e) {
            LOGGER.error("Error when getting file list.", e);
            throw new MainControllerException("Error when getting file list.", e);
        }
        return serverFiles;
    }

    @Test
    public void testControlLoadingFileListShouldReturnListOfServerFilesWhenReceivedIt()
            throws TimeoutException, InterruptedException, MainControllerException {
        List<ServerFile> expected = Collections.emptyList();
        when(listExchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS)).thenReturn(expected);

        List<ServerFile> actual = controlLoadingFileList(SLASH_SOME_PATH);

        Assert.assertEquals(expected, actual);

        verify(sendingManager, atLeastOnce()).send(LOADING_FILE_LIST_TEST_COMMAND);
        verify(dataManager, atLeastOnce()).manageWork(any());
    }

    @Test(expected = MainControllerException.class)
    public void testControlLoadingFileListShouldThrowMainControllerExceptionWhenExchangeThrowException()
            throws TimeoutException, InterruptedException, MainControllerException {
        when(listExchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS))
                .thenThrow(new InterruptedException());

        controlLoadingFileList(ANY);

        Assert.fail();
    }

    public void controlChangeWorkingDirectory(String directoryName) {
        String changingDirectoryCommand = "CWD " + directoryName;
        sendingManager.send(changingDirectoryCommand);
    }

    public void controlChangeDataType(DataType dataType) {
        String changeDataTypeCommand = "TYPE " + dataType.getCode();
        sendingManager.send(changeDataTypeCommand);
    }
}
