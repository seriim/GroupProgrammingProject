/**
 * CustomShell is a Java-based command-line shell that allows users to perform
 * various file and directory operations, manage environment variables, and interact
 * with external processes. The shell provides built-in commands, as well as support
 * for I/O redirection, piping, and command history.
 * <p>
 * This shell supports a variety of commands, including:
 * - File and directory management (create, delete, rename, list, etc.)
 * - Environment variable management (setenv, printenv)
 * - Text file manipulation (insert, append, read)
 * - Command history navigation
 * - Console clearing and file sorting
 * </p>
 * 
 * @author Raheem Gordon, Serena Morris, Dontray Blackwood
 */

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CustomShell {
	// Map to store custom environment variables
    private static Map<String, String> environmentVariables = new HashMap<>(System.getenv());
    
    // List to store command history
    private static List<String> commandHistory = new ArrayList<>();
    
    // Pointer for command history navigation
    private static int historyPointer = -1;

    /**
     * Main entry point for the custom shell. This method continuously accepts and processes
     * user input until the 'exit' command is given.
     *
     * @param args command-line arguments (not used)
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String commandLine;

        // Setup to read arrow keys for command history
        Console console = System.console();

        while (true) {
            System.out.print(ConsoleColors.CYAN_UNDERLINED +"my_shell>" + ConsoleColors.WHITE_BRIGHT +" ");
            if (console != null) {
                commandLine = new String(console.readPassword());
            } else {
                commandLine = scanner.nextLine().trim();
            }

            // Support for repeating last command using !!
            if (commandLine.equals("!!")) {
                if (commandHistory.size() > 0) {
                    commandLine = commandHistory.get(commandHistory.size() - 1);
                    System.out.println(commandLine);
                } else {
                    System.out.println(ConsoleColors.RED_BACKGROUND + "No commands in history.");
                    continue;
                }
            }

            if (commandLine.equals("exit")) break;
            if (commandLine.isEmpty()) continue;

            // Add to history
            commandHistory.add(commandLine);
            historyPointer = commandHistory.size(); // Reset pointer

            try {
                processCommand(commandLine);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * Processes the user input and determines which command to execute.
     * Handles both built-in commands and external processes.
     *
     * @param commandLine the full command entered by the user
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the external command process is interrupted
     */
    private static void processCommand(String commandLine) throws IOException, InterruptedException {
        // Check if command involves I/O redirection or piping
        if (commandLine.contains("|") || commandLine.contains(">") || commandLine.contains("<")) {
            handleIoAndPipes(commandLine);
            return;
        }

        String[] commands = commandLine.split("\\s+");

        switch (commands[0]) {
            case "help":
                printHelp();
                break;
            case "location":
                System.out.println(System.getProperty("user.dir"));
                break;
            case "create":
                createFile(commands[1]);
                break;
            case "delete":
                deleteFile(commands[1]);
                break;
            case "rename":
                renameFile(commands[1], commands[2]);
                break;
            case "make":
                createDirectory(commands[1]);
                break;
            case "remove":
                removeDirectory(commands[1]);
                break;
            case "change":
                changeDirectory(commands[1]);
                break;
            case "modify":
                modifyPermissions(commands[1], commands[2]);
                break;
            case "list":
                listDirectoryContents();
                break;
            case "setenv":
                setEnvironmentVariable(commands[1], commands[2]);
                break;
            case "printenv":
                printEnvironmentVariable(commands[1]);
                break;
            case "print":
                printWithVariables(Arrays.copyOfRange(commands, 1, commands.length));
                break;
            case "tree":
                displayTree(System.getProperty("user.dir"), 0);
                break;
            case "insert":
                writeFile(commands[1], false);
                break;
            case "append":
                writeFile(commands[1], true);
                break;
            case "read":
                readFile(commands[1]);
                break;
            case "history":
                printHistory();
                break;
            case "fresh":
                clearConsole();
                break;
            case "sort":
            	sortFiles(commands[1]);
                break;
            default:
                handleExternalCommand(commands);
        }
    }

    /**
     * Prints a list of available commands with descriptions for each.
     */
    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("help - Display this help message");
        System.out.println("location - Show the current working directory (like pwd)");
        System.out.println("create <filename> - Create a file");
        System.out.println("delete <filename> - Delete a file");
        System.out.println("rename <oldname> <newname> - Rename a file");
        System.out.println("make <directory> - Create a directory");
        System.out.println("remove <directory> - Remove a directory");
        System.out.println("change <directory> - Change the current working directory");
        System.out.println("modify <permissions> <filename> - Modify file permissions");
        System.out.println("list - List the contents of the current directory");
        System.out.println("setenv <var> <value> - Set an environment variable");
        System.out.println("printenv <var> - Print the value of an environment variable");
        System.out.println("print <text> - Print text, supports ${VARS}");
        System.out.println("tree - Display files and directories in a tree structure");
        System.out.println("insert <file> - Insert and overwrite text into a file");
        System.out.println("append <file> - Append text to a file");
        System.out.println("read <file> - Read and display the contents of a file");
        System.out.println("history - Show the last 20 commands");
        System.out.println("fresh - Clear the console");
        System.out.println("sort <name|date> - Sort files by name or date");
        System.out.println("!! - Repeat the last command");
    }

    /**
     * Creates a new file in the current directory.
     *
     * @param fileName the name of the file to create
     * @throws IOException if an I/O error occurs
     */
    private static void createFile(String fileName) throws IOException {
        Files.createFile(Paths.get(fileName));
        System.out.println("File created: " + fileName);
    }

    /**
     * Deletes an existing file from the current directory.
     *
     * @param fileName the name of the file to delete
     * @throws IOException if an I/O error occurs
     */
    private static void deleteFile(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(fileName));
        System.out.println("File deleted: " + fileName);
    }

    /**
     * Renames a file in the current directory.
     *
     * @param oldName the current name of the file
     * @param newName the new name for the file
     * @throws IOException if an I/O error occurs
     */
    private static void renameFile(String oldName, String newName) throws IOException {
        Files.move(Paths.get(oldName), Paths.get(newName), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("File renamed from " + oldName + " to " + newName);
    }

    /**
     * Creates a new directory in the current directory.
     *
     * @param dirName the name of the directory to create
     * @throws IOException if an I/O error occurs
     */
    private static void createDirectory(String dirName) throws IOException {
        Files.createDirectory(Paths.get(dirName));
        System.out.println("Directory created: " + dirName);
    }

    /**
     * Removes a directory from the current directory.
     *
     * @param dirName the name of the directory to remove
     * @throws IOException if an I/O error occurs
     */
    private static void removeDirectory(String dirName) throws IOException {
        Files.deleteIfExists(Paths.get(dirName));
        System.out.println("Directory removed: " + dirName);
    }

    /**
     * Changes the current working directory to the specified directory.
     *
     * @param dirName the path of the directory to change to
     */
    private static void changeDirectory(String dirName) {
        File directory = new File(dirName);
        if (directory.exists() && directory.isDirectory()) {
            System.setProperty("user.dir", directory.getAbsolutePath());
            System.out.println("Changed directory to: " + directory.getAbsolutePath());
        } else {
            System.out.println("Directory not found: " + dirName);
        }
    }

    /**
     * Modifies the permissions of a file based on the provided string representation.
     *
     * @param permissions a string representing the new permissions (e.g., "rwxr-xr-x")
     * @param fileName the name of the file whose permissions will be modified
     * @throws IOException if an I/O error occurs
     */
    private static void modifyPermissions(String permissions, String fileName) throws IOException {
        Path path = Paths.get(fileName);
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString(permissions);
        Files.setPosixFilePermissions(path, perms);
        System.out.println("Permissions set for " + fileName + " to " + permissions);
    }

    /**
     * Lists the contents of the current directory, sorted alphabetically by name.
     */
    private static void listDirectoryContents() {
        File dir = new File(System.getProperty("user.dir"));
        String[] contents = dir.list();
        if (contents != null) {
            Arrays.sort(contents);
            for (String file : contents) {
                System.out.println(file);
            }
        } else {
            System.out.println("Directory is empty.");
        }
    }

    /**
     * Sets an environment variable with the specified value.
     *
     * @param var the name of the environment variable
     * @param value the value to set for the variable
     */
    private static void setEnvironmentVariable(String var, String value) {
        environmentVariables.put(var, value);
        System.out.println("Set environment variable: " + var + "=" + value);
    }
    
    /**
     * Prints the value of the specified environment variable.
     *
     * @param var the name of the environment variable
     */
    private static void printEnvironmentVariable(String var) {
        String value = environmentVariables.get(var);
        if (value != null) {
            System.out.println(var + "=" + value);
        } else {
            System.out.println("Environment variable not set: " + var);
        }
    }

    /**
     * Prints the specified text, with support for substituting environment variables
     * using the format ${VAR}.
     *
     * @param parts an array of text parts to print
     */
    private static void printWithVariables(String[] parts) {
        for (String part : parts) {
            if (part.startsWith("${") && part.endsWith("}")) {
                String var = part.substring(2, part.length() - 1);
                String value = environmentVariables.get(var);
                if (value != null) {
                    System.out.print(value + " ");
                } else {
                    System.out.print(part + " ");
                }
            } else {
                System.out.print(part + " ");
            }
        }
        System.out.println();
    }

    /**
     * Displays a tree structure of files and directories from the current directory.
     *
     * @param directory the root directory to display
     * @param level the current level of the directory tree (used for indentation)
     */
    private static void displayTree(String directory, int level) {
        File dir = new File(directory);
        File[] files = dir.listFiles();

        if (files == null) return;

        for (File file : files) {
            for (int i = 0; i < level; i++) System.out.print("    ");
            System.out.println("|-- " + file.getName());

            if (file.isDirectory()) {
                displayTree(file.getAbsolutePath(), level + 1);
            }
        }
    }

    /**
     * Writes text to a file, either by inserting (overwriting) or appending to the file.
     *
     * @param fileName the name of the file to write to
     * @param append if true, text will be appended; if false, text will overwrite the file
     * @throws IOException if an I/O error occurs
     */
    private static void writeFile(String fileName, boolean append) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, append))) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter text to write to the file (Ctrl+D to finish):");
            while (scanner.hasNextLine()) {
                writer.write(scanner.nextLine());
                writer.newLine();
            }
        }
        System.out.println((append ? "Appended to " : "Inserted into ") + fileName);
    }

    /**
     * Reads and prints the contents of the specified file.
     *
     * @param fileName the name of the file to read
     * @throws IOException if an I/O error occurs
     */
    private static void readFile(String fileName) throws IOException {
        Files.lines(Paths.get(fileName)).forEach(System.out::println);
    }

    /**
     * Prints the last 20 commands entered by the user.
     */
    private static void printHistory() {
        int start = Math.max(commandHistory.size() - 20, 0);
        for (int i = start; i < commandHistory.size(); i++) {
            System.out.println((i + 1) + " " + commandHistory.get(i));
        }
    }

    /**
     * Clears the console screen. The command used varies based on the operating system.
     * On Windows, it uses 'cls'; on other systems, it uses 'clear'.
     */
    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Set the TERM environment variable if it's not already set
                ProcessBuilder pb = new ProcessBuilder("clear");
                Map<String, String> env = pb.environment();
                if (!env.containsKey("TERM")) {
                    env.put("TERM", "xterm-256color");
                }
                pb.inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.println("Error clearing console: " + e.getMessage());
        }
    }


    /**
     * Sorts the files in the current directory by the specified criteria (name or date).
     *
     * @param criteria the sorting criteria ("name" or "date")
     * @throws IOException if an I/O error occurs
     */
    private static void sortFiles(String criteria) throws IOException {
        File dir = new File(System.getProperty("user.dir"));
        File[] files = dir.listFiles();

        if (files != null) {
            if (criteria.equals("name")) {
                Arrays.sort(files, Comparator.comparing(File::getName));
            } else if (criteria.equals("date")) {
                Arrays.sort(files, Comparator.comparing(File::lastModified));
            } else {
            	System.out.println(ConsoleColors.RED_BRIGHT + "Error: Please enter sort date or sort name");
            	return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (File file : files) {
                String formattedDate = sdf.format(file.lastModified());
                System.out.println(file.getName() + " \t\t" + formattedDate);
            }
        } else {
            System.out.println("Directory is empty.");
        }
    }

    /**
     * Handles execution of external commands by the operating system.
     *
     * @param commands an array of strings representing the command and its arguments
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the process is interrupted
     */
    private static void handleExternalCommand(String[] commands) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        process.waitFor();
    }

    // Handle I/O Redirection and Piping
    private static void handleIoAndPipes(String commandLine) throws IOException {
        String[] pipeParts = commandLine.split("\\|");
        String commandPart = pipeParts[0].trim();
        String[] redirectionParts = commandPart.split(">");
        String inputPart = redirectionParts[0].trim();
        List<String> outputCommands = Arrays.asList(redirectionParts);

        // Handle file output redirection, input, and piping
        // This part would involve handling streams, similar to UNIX systems.
        System.out.println("I/O and piping not fully implemented yet.");
    }
}
