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

import java.io.File;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController {
    private static final Logger LOGGER = Logger.getRootLogger();

    private static final int CONTROL_PORT = 21;
    private Connection controlConnection = new Connection();

    private static final int TIMEOUT = 5000;
    private Exchanger<String> responseExchanger = new Exchanger<>();
    private Exchanger<List<ServerFile>> listExchanger = new Exchanger<>();

    private SendingManager sendingManager = new SendingManager(controlConnection, responseExchanger);
    private DataManager dataManager = new DataManager();

    public MainController() {
    }

    public MainController(Connection controlConnection, Exchanger<String> responseExchanger,
                          Exchanger<List<ServerFile>> listExchanger, SendingManager sendingManager,
                          DataManager dataManager) {
        this.controlConnection = controlConnection;
        this.responseExchanger = responseExchanger;
        this.listExchanger = listExchanger;
        this.sendingManager = sendingManager;
        this.dataManager = dataManager;
    }

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

    public void controlDisconnecting() {
        String exitCommand = "QUIT";
        sendingManager.send(exitCommand);
    }

    public void controlAuthenticating(Pair<String, String> authenticationPair) {
        String loginCommand = "USER " + authenticationPair.getKey();
        sendingManager.send(loginCommand);

        String passwordCommand = "PASS " + authenticationPair.getValue();
        sendingManager.send(passwordCommand);
    }

    public void controlCreatingCatalogue(String catalogueName) {
        String catalogueCreatingCommand = "MKD " + catalogueName;
        sendingManager.send(catalogueCreatingCommand);
    }

    public void controlDeletingCatalogue(String catalogueName) {
        String catalogueDeletingCommand = "RMD " + catalogueName;
        sendingManager.send(catalogueDeletingCommand);
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

    private String addToClientPath(String oldPath, String name) {
        return oldPath + "/" + name;
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

    public void controlSavingFile(String fromFile, String toFile) throws MainControllerException {
        Connection dataConnection = establishDataConnection();

        String fileSavingCommand = "STOR " + toFile;
        sendingManager.send(fileSavingCommand);

        FileSending fileSending = new FileSending(dataConnection, fromFile);
        dataManager.manageWork(fileSending);
    }

    // public for tests
    public Connection establishDataConnection() throws MainControllerException {
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

    public void controlChangeWorkingDirectory(String directoryName) {
        String changingDirectoryCommand = "CWD " + directoryName;
        sendingManager.send(changingDirectoryCommand);
    }

    public void controlChangeDataType(DataType dataType) {
        String changeDataTypeCommand = "TYPE " + dataType.getCode();
        sendingManager.send(changeDataTypeCommand);
    }
}
