package utils;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.osbot.rs07.script.API;
import org.osbot.rs07.script.Script;

public class BotFiles extends API {

    Script script;
    String scriptName;
    String scriptFolderDir;
    File scriptFolder;
    Component botCanvas;

    @Override
    public void initializeModule() {
        initialiseVariables();
        createScriptFolder();
    }

    private void initialiseVariables() {
        script = bot.getScriptExecutor().getCurrent();
        scriptName = script.getName();
        scriptFolderDir = (scriptName + File.separator);
        scriptFolder = new File(script.getDirectoryData(), scriptFolderDir);
        botCanvas = bot.getCanvas();
    }

    private void createScriptFolder() {
        if (!scriptFolder.exists()) {
            if (scriptFolder.mkdirs()) {
                logger.info("Successfully created new script folder for " + scriptName + ":");
                logger.info(scriptFolder.getAbsolutePath());
            } else {
                logger.error("Failed to create a new script folder for " + scriptName + ":");
                logger.error(scriptFolder.getAbsolutePath());
            }
        }
    }

    public boolean folderExists() {
        return scriptFolder.exists();
    }

    public boolean fileExists(String filePathString) {

        File file = new File(scriptFolder, filePathString);

        return file.exists() && !file.isDirectory();
    }

    protected synchronized String readFromFile(File file)
            throws FileNotFoundException, IOException {

        String fileContents = null;

        if (file == null || !file.exists() || file.isDirectory()) {
            throw new IllegalArgumentException("Invalid file");
        }

        try (
                FileReader in = new FileReader(file);
                BufferedReader br = new BufferedReader(in)
        ) {

            fileContents = br.lines().collect(Collectors.joining("\n"));
        }

        return fileContents;
    }

    public synchronized String readFromFile(String filename)
            throws FileNotFoundException, IOException {

        File file;

        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename must be valid");
        }

        file = new File(scriptFolder, filename);

        return readFromFile(file);
    }

    protected synchronized void writeToFile(File file, String fileContent)
            throws IOException {

        if (file == null) {
            throw new IllegalArgumentException("Invalid file");
        } else if (!file.exists() && !file.createNewFile()) {
            throw new IllegalArgumentException("Failed to create file: " + file);
        }

        try (
                FileWriter out = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(out)
        ) {

            bw.write(fileContent != null ? fileContent : "");
        }
    }

    public synchronized void writeToFile(String filename, String fileContent)
            throws IOException {

        File file;

        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename must be valid");
        }

        file = new File(scriptFolder, filename);

        writeToFile(file, fileContent);
    }

    public synchronized String open(FileFilter fileFilter)
            throws RuntimeException, FileNotFoundException, IOException {

        String fileContents = null;
        JFileChooser fileChooser;
        int selectionState;
        File selectedFile;

        if (scriptName == null) {
            throw new RuntimeException("Initialise the API module first!");
        }

        fileChooser = new JFileChooser(scriptFolder);
        fileChooser.setMultiSelectionEnabled(false);
        if (fileFilter != null) {
            fileChooser.setFileFilter(fileFilter);
        }
        selectionState = fileChooser.showOpenDialog(botCanvas);
        if (selectionState == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            fileContents = readFromFile(selectedFile);
        }

        return fileContents;
    }

    public synchronized String open() throws RuntimeException, FileNotFoundException, IOException {
        return open(null);
    }

    public synchronized void save(FileFilter fileFilter, String fileContent)
            throws IOException {

        JFileChooser fileChooser;
        int selectionState;
        File selectedFile;

        if (scriptName == null) {
            throw new RuntimeException("Initialise the API module first!");
        }

        fileChooser = new JFileChooser(scriptFolder);
        fileChooser.setMultiSelectionEnabled(false);
        if (fileFilter != null) {
            fileChooser.setFileFilter(fileFilter);
        }
        selectionState = fileChooser.showSaveDialog(botCanvas);
        if (selectionState == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            writeToFile(selectedFile, fileContent);
        }
    }

    public synchronized void save(String fileContent) throws IOException {
        save(null, fileContent);
    }

    public synchronized boolean deleteFile(String filename) {

        File file;

        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename must be valid");
        }

        file = new File(scriptFolder, filename);

        return !file.exists() || (file.isFile() && file.delete());
    }
}