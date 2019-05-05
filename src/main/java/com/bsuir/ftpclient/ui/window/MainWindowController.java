package com.bsuir.ftpclient.ui.window;

import com.bsuir.ftpclient.connection.ftp.data.DataType;
import com.bsuir.ftpclient.connection.ftp.Connection;
import com.bsuir.ftpclient.connection.ftp.control.exception.ControlConnectionException;
import com.bsuir.ftpclient.connection.ftp.control.manager.SendingManager;
import com.bsuir.ftpclient.connection.ftp.data.DataConnectionActions;
import com.bsuir.ftpclient.connection.ftp.data.file.ServerFile;
import com.bsuir.ftpclient.connection.ftp.data.file.parser.FileNamesParser;
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

public class MainWindowController {
    private static final Logger LOGGER = Logger.getRootLogger();

    private static final int CONTROL_PORT = 21;

    private Connection controlConnection = new Connection();

    private static final int TIMEOUT = 1000;
    private Exchanger<String> responseExchanger = new Exchanger<>();

    private SendingManager sendingManager = new SendingManager(controlConnection, responseExchanger);
    private DataManager dataManager = new DataManager();

    public void controlConnecting(String connectInformation) throws MainControllerException {
        try {
            controlConnection.connect(connectInformation, CONTROL_PORT);
            String connectCommand = "";
            sendingManager.send(connectCommand);
        } catch (ControlConnectionException e) {
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
        File file = new File(toDirectoryPath);
        if (!file.exists()) {
            file.mkdir();
        }

        List<ServerFile> files = controlLoadingFileList(fromDirectoryPath);
        for (ServerFile serverFile : files) {
            String fileName = serverFile.getName();
            String newFromPath = getNewServerDirectoryPath(fromDirectoryPath, fileName);
            String newToPath = getNewClientDirectoryPath(toDirectoryPath, fileName);

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
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String dirName = file.getName();
                String newFromPath = getNewClientDirectoryPath(fromDirectoryPath, dirName);
                String newToPath = getNewServerDirectoryPath(toDirectoryPath, dirName);

                if (file.isDirectory()) {
                    controlSavingCatalogue(newFromPath, newToPath);
                } else {
                    controlSavingFile(newFromPath, newToPath);
                }
            }
        }
    }

    private String getNewClientDirectoryPath(String oldPath, String name) {
        return oldPath + "/" + name;
    }

    private String getNewServerDirectoryPath(String oldPath, String dirName) {
        String newToPath;

        if ("/".equals(oldPath)) {
            newToPath = oldPath + dirName;
        } else {
            newToPath = oldPath + "/" + dirName;
        }

        return newToPath;
    }

    public void controlDeletingFile(String fileName) {
        String fileDeletingCommand = "DELE " + fileName;
        sendingManager.send(fileDeletingCommand);
    }

    public void controlLoadingFile(String fileName, String toDirectoryPath) throws MainControllerException {
        Connection dataConnection = establishDataConnection();
        DataConnectionActions actions = new DataConnectionActions(dataConnection);

        String catalogueLoadingCommand = "RETR " + fileName;
        sendingManager.send(catalogueLoadingCommand);

        FileReceiving fileReceiving = new FileReceiving(actions, toDirectoryPath);
        dataManager.manageWork(fileReceiving);
    }

    public void controlSavingFile(String fromFile, String toFile) throws MainControllerException {
        Connection dataConnection = establishDataConnection();
        DataConnectionActions actions = new DataConnectionActions(dataConnection);

        String catalogueSavingCommand = "STOR " + toFile;
        sendingManager.send(catalogueSavingCommand);

        FileSending fileSending = new FileSending(actions, fromFile);
        dataManager.manageWork(fileSending);
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
        } catch (InterruptedException | TimeoutException | ControlConnectionException e) {
            LOGGER.error("Error when establish data connection.", e);
            throw new MainControllerException("Error when establish data connection.", e);
        }

        return dataConnection;
    }

    public void controlClose() {
        sendingManager.killAllSenders();
        dataManager.shutdown();
    }

    public void controlRestartSendingMessages() {
        sendingManager.restart();
    }

    public List<ServerFile> controlLoadingFileList(String directoryName)
            throws MainControllerException {
        List<ServerFile> fileComponents;
        try {
            Connection dataConnection = establishDataConnection();
            String fileListLoadingCommand = "NLST " + directoryName; // MLSD/LIST/NLST (another format of request - another parser)
            sendingManager.send(fileListLoadingCommand);

            Exchanger<List<ServerFile>> listExchanger = new Exchanger<>();
            FileNamesParser parser = new NLSTParser();
            FileListReceiving fileListReceiving = new FileListReceiving(dataConnection, parser, listExchanger);
            dataManager.manageWork(fileListReceiving);

            fileComponents = listExchanger.exchange(null, TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | TimeoutException e) {
            LOGGER.error("Error when getting file list.", e);
            throw new MainControllerException("Error when getting file list.", e);
        }
        return fileComponents;
    }

    public void controlChangeWorkingDirectory(String directoryName) throws MainControllerException {
        if (directoryName == null) {
            throw new MainControllerException("Null directory name.");
        }

        String changingDirectoryCommand = "CWD " + directoryName;
        sendingManager.send(changingDirectoryCommand);
    }

    public void controlChangeDataType(DataType dataType) {
        String changeDataTypeCommand = "TYPE " + dataType.getCode() + " " + "N";
        sendingManager.send(changeDataTypeCommand);
    }
}
