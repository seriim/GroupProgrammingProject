// Raheem Gordon (2208501)
// Serena Morris (2208659)
// Dontray Blackwood (2205745)
// Class: UE2
// Tutor: Phillip Smith

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
import java.text.Collator;
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
                 commandLine = new String(console.readLine());
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
        try {
            // Check if the command involves piping '|'
            if (commandLine.contains("|")) {
                handleIoAndPipes(commandLine);
                return;
            }
            // Check if command involves logical OR (||), redirection (<>), or piping (|)
            if (commandLine.contains("||")) {
                handleLogicalOr(commandLine);
                return;
            } else if (commandLine.contains("<>")) {
                handleRedirection(commandLine);
                return;
            }
    
            String[] commands = commandLine.trim().split("\\s+");
            if (commands.length == 0 || commands[0].isBlank()) {
                System.out.println("Error: No command entered.");
                return;
            }
    
            // Process commands based on the first word (command)
            switch (commands[0]) {
                case "help":
                    printHelp();
                    break;
                case "location":
                    System.out.println(System.getProperty("user.dir"));
                    break;
                case "create":
                    if (commands.length < 2) {
                        System.out.println("Error: Missing filename for 'create' command.");
                    } else {
                        createFile(commands[1]);
                    }
                    break;
                case "delete":
                    if (commands.length < 2) {
                        System.out.println("Error: Missing filename for 'delete' command.");
                    } else {
                        deleteFile(commands[1]);
                    }
                    break;
                case "rename":
                    if (commands.length < 3) {
                        System.out.println("Error: Missing arguments for 'rename' command. Usage: rename <oldName> <newName>");
                    } else {
                        renameFile(commands[1], commands[2]);
                    }
                    break;
                case "make":
                    if (commands.length < 2) {
                        System.out.println("Error: Missing directory name for 'make' command.");
                    } else {
                        createDirectory(commands[1], null);
                    }
                    break;
                case "createDirectory":
                    if (commands.length < 2) {
                        System.out.println("Error: Missing directory name for 'make' command.");
                    } else {
                        createDirectory(commands[1], null);
                    }
                    break;
                case "remove":
                    if (commands.length < 2) {
                        System.out.println("Error: Missing directory name for 'remove' command.");
                    } else {
                        removeDirectory(commands[1]);
                    }
                    break;
                case "change":
                    if (commands.length < 2) {
                        System.out.println("Error: Missing directory path for 'change' command.");
                    } else {
                        changeDirectory(commands[1]);
                    }
                    break;
                case "modify":
                    if (commands.length < 3) {
                        System.out.println("Error: Missing arguments for 'modify' command. Usage: modify <file> <permissions>");
                    } else {
                        modifyPermissions(commands[1], commands[2]);
                    }
                    break;
                case "list":
                    listDirectoryContents(false);
                    break;
                case "setenv":
                    if (commands.length < 3) {
                        System.out.println("Error: Missing arguments for 'setenv' command. Usage: setenv <key> <value>");
                    } else {
                        setEnvironmentVariable(commands[1], commands[2]);
                    }
                    break;
                case "printenv":
                    if (commands.length < 2) {
                        System.out.println("Error: Missing environment variable name for 'printenv' command.");
                    } else {
                        printEnvironmentVariable(commands[1]);
                    }
                    break;
                case "print":
                    if (commands.length < 2) {
                        System.out.println("Error: Missing text for 'print' command.");
                    } else {
                        printWithVariables(Arrays.copyOfRange(commands, 1, commands.length), null, false);
                    }
                    break;
                case "tree":
                    displayTree(System.getProperty("user.dir"), 0);
                    break;
                case "insert":
                    if (commands.length < 2) {
                        System.out.println("Error: Missing filename for 'insert' command.");
                    } else {
                        writeFile(commands[1], false);
                    }
                    break;
                case "append":
                    if (commands.length < 2) {
                        System.out.println("Error: Missing filename for 'append' command.");
                    } else {
                        writeFile(commands[1], true);
                    }
                    break;
                case "read":
                    if (commands.length < 2) {
                        System.out.println("Error: Missing filename for 'read' command.");
                    } else {
                        readFile(commands[1]);
                    }
                    break;
                case "history":
                    printHistory();
                    break;
                case "fresh":
                    clearConsole();
                    break;
                case "sort":
                    if (commands.length < 2) {
                        System.out.println("Error: Missing filename for 'sort' command.");
                    } else {
                        sortFiles(commands[1], null);
                    }
                    break;
                case "search":
                    if (commands.length < 2) {
                        System.out.println("Error: Missing search term for 'search' command.");
                    } else {
                        searchFile(commands[1], null);  // Call the search method with the search term
                    }
                    break;
                default:
                    System.out.println("Error: Unrecognized command '" + commands[0] + "'. Type 'help' for a list of available commands.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found. " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error: An I/O error occurred. " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: Missing arguments for the command. " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: An unexpected error occurred. " + e.getMessage());
        }
    }

    // Main method to process the entire command line
    public static void handleIoAndPipes(String commandLine) {
        String[] commands = commandLine.split("\\|");
        InputStream inputStream = null;  // Initialize the input stream

        // Iterate through all the commands and process them
        for (int i = 0; i < commands.length; i++) {
            String command = commands[i].trim();

            // Command that doesn't need an InputStream (listDirectoryContents, print, createDirectory)
            if (command.startsWith("list")) {
                if (inputStream != null) {
                    System.out.println("Error: 'list' should come first and not take an InputStream.");
                    return;
                }
                try {
                    inputStream = listDirectoryContents(true);  // list directory contents
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
            }
            // Command that doesn't need an InputStream (printWithVariables)
            else if (command.startsWith("print")) {
                try {
                    inputStream = printWithVariables(command.split("\\s+"), inputStream, true);  // Print variables
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
            }
            else if (command.startsWith("createDirectory")) {
                try {
                    inputStream = createDirectory(command.split("\\s+")[1], inputStream);  // Create directory
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
            }
            // Command that needs an InputStream (sortFiles)
            else if (command.startsWith("sort")) {
                if (inputStream == null) {
                    System.out.println("Error: 'sort' must come after a command that generates an InputStream.");
                    return;
                }
                try {
                    inputStream = sortFiles(command.split("\\s+")[1], inputStream);  // Sort files by name or date
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
            }
            // Command that needs an InputStream (searchFile)
            else if (command.startsWith("search")) {
                if (inputStream == null) {
                    System.out.println("Error: 'search' must come after a command that generates an InputStream.");
                    return;
                }
                try {
                    inputStream = searchFile(command.split("\\s+")[1], inputStream);  // Search files by term
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
            }
            // Invalid command
            else {
                System.out.println("Error: Unknown command: " + command);
                return;
            }
        }

        // If there was any input stream, print its content or do nothing
        if (inputStream != null) {
            try {
                printWithVariables(new String[0], inputStream, true);  // Print the final result
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
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
         // Get the current working directory
         String currentDir = System.getProperty("user.dir");
 
         // Create the full path to the file in the current directory
         Path path = Paths.get(currentDir, fileName);
 
         // Create the file at the specified path
         Files.createFile(path);
 
         // Inform the user that the file was created
         System.out.println("File created: " + path.toAbsolutePath());
     }
 
     private static void deleteFile(String fileName) throws IOException {
     // Use the current directory from the user.dir property
     File currentDirectory = new File(System.getProperty("user.dir"));
     Path filePath = currentDirectory.toPath().resolve(fileName);
 
     // Attempt to delete the file if it exists
     if (Files.deleteIfExists(filePath)) {
         System.out.println("File deleted: " + fileName);
     } else {
         System.out.println("File not found: " + fileName);
     }
 }
 
     /**
      * Deletes an existing file from the current directory.
      *
      * @param fileName the name of the file to delete
      * @throws IOException if an I/O error occur   
 
     /**
      * Renames a file in the current directory.
      *
      * @param oldName the current name of the file
      * @param newName the new name for the file
      * @throws IOException if an I/O error occurs
      */
       private static void renameFile(String oldName, String newName) throws IOException {
     File currentDirectory = new File(System.getProperty("user.dir"));
     Path oldFilePath = currentDirectory.toPath().resolve(oldName);
     Path newFilePath = currentDirectory.toPath().resolve(newName);
 
     if (Files.exists(oldFilePath)) {
         Files.move(oldFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
         System.out.println("File renamed from " + oldName + " to " + newName);
     } else {
         System.out.println("File not found: " + oldName);
     }
 }
 
 
     /**
      * Creates a new directory in the current directory.
      *
      * @param dirName the name of the directory to create
      * @throws IOException if an I/O error occurs
      */
      private static InputStream createDirectory(String dirName, InputStream inputStream) throws IOException {
        // Use the current directory from the user.dir property
        File currentDirectory = new File(System.getProperty("user.dir"));
        Path dirPath = currentDirectory.toPath().resolve(dirName);
        
        // Create the directory
        Files.createDirectories(dirPath);  // createDirectories ensures that all necessary parent directories are created
        System.out.println("Directory created: " + dirPath.toAbsolutePath());
    
        // If an InputStream is provided, take its content and save it to a file in the created directory
        if (inputStream != null) {
            // Read the content from the InputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
    
            // Ask the user for a file name
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the file name (without extension) to save the content: ");
            String fileName = scanner.nextLine().trim();
            File outputFile = new File(dirPath.toFile(), fileName + ".txt");
    
            // Save the content to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write(content.toString());
                System.out.println("Content saved to: " + outputFile.getAbsolutePath());
            }
    
            // Return the content as ByteArrayInputStream for further pipeline processing
            return new ByteArrayInputStream(content.toString().getBytes());
        }
    
        // If no InputStream is passed, return an empty ByteArrayInputStream
        return new ByteArrayInputStream(new byte[0]);
    }
    
 
     /**
      * Removes a directory from the current directory.
      *
      * @param dirName the name of the directory to remove
      * @throws IOException if an I/O error occurs
      */
     private static void removeDirectory(String dirName) throws IOException {
     // Use the current directory from the user.dir property
     File currentDirectory = new File(System.getProperty("user.dir"));
     Path dirPath = currentDirectory.toPath().resolve(dirName);
 
     // Attempt to delete the directory if it exists
     if (Files.deleteIfExists(dirPath)) {
         System.out.println("Directory removed: " + dirPath.toAbsolutePath());
     } else {
         System.out.println("Directory not found: " + dirName);
     }
 }
     /**
      * Changes the current working directory to the specified directory.
      *
      * @param dirName the path of the directory to change to
      */
     private static void changeDirectory(String dirName) {
         // Remove any leading or trailing quotes
         dirName = dirName.replaceAll("^\"|\"$", "");
 
         // If the directory name is "..", go to the parent directory
         if (dirName.equals("..")) {
             File directory = new File(System.getProperty("user.dir")).getParentFile();
             if (directory != null && directory.exists() && directory.isDirectory()) {
                 System.setProperty("user.dir", directory.getAbsolutePath());
                 System.out.println("Changed directory to: " + directory.getAbsolutePath());
             } else {
                 System.out.println("Unable to move up to the parent directory.");
             }
             return;
         }
 
         // Check if it's an absolute path (starts with a drive letter on Windows or '/' on Unix-like systems)
         File directory;
         if (dirName.matches("^[a-zA-Z]:\\\\.*") || dirName.startsWith("/")) {
             // Absolute path
             directory = new File(dirName);
         } else {
             // Relative path
             directory = new File(System.getProperty("user.dir"), dirName);
         }
 
         // Check if the directory exists and is a directory
         if (directory.exists() && directory.isDirectory()) {
             System.setProperty("user.dir", directory.getAbsolutePath());
             System.out.println("Changed directory to: " + directory.getAbsolutePath());
         } else {
             System.out.println("Directory not found or is not a directory: " + dirName);
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
     try {
         // Get the current working directory
         String currentDir = System.getProperty("user.dir");
         
         // Build the full path of the file
         Path path = Paths.get(currentDir, fileName);
 
         // Check if the file exists
         if (!Files.exists(path)) {
             System.err.println("Error: File does not exist at " + path.toAbsolutePath());
             return;
         }
 
         // Convert the permissions string to POSIX permissions
         Set<PosixFilePermission> perms = PosixFilePermissions.fromString(permissions);
 
         // Apply the permissions to the file
         Files.setPosixFilePermissions(path, perms);
         System.out.println("Permissions set for " + fileName + " to " + permissions);
     } catch (IllegalArgumentException e) {
         System.err.println("Error: Invalid permission mode '" + permissions + "'. Ensure it matches POSIX format, e.g., 'r--r--r--'.");
     } catch (UnsupportedOperationException e) {
         System.err.println("Error: POSIX file permissions are not supported on this file system.");
     }
 }
 
 
     /**
      * Lists the contents of the current directory, sorted alphabetically by name.
      */
      private static InputStream listDirectoryContents(Boolean isPiping) throws IOException {
        // Use the current directory from the user.dir property
        File dir = new File(System.getProperty("user.dir"));
        String[] contents = dir.list();
    
        // StringBuilder to accumulate the directory contents
        StringBuilder directoryContents = new StringBuilder();
    
        if (contents != null && contents.length > 0) {
            Arrays.sort(contents);
            // Print and accumulate the directory contents
            for (String file : contents) {
                if(!isPiping) {System.out.println(file);}  // Print to console
                directoryContents.append(file).append("\n");
            }
        } else {
            // Handle case where directory is empty
            System.out.println("Directory is empty.");
            directoryContents.append("Directory is empty.\n");
        }
    
        // Convert the accumulated directory contents to a ByteArrayInputStream for further processing
        return new ByteArrayInputStream(directoryContents.toString().getBytes());
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
      private static InputStream printWithVariables(String[] parts, InputStream inputStream, Boolean isPiping) throws IOException {
        StringBuilder output = new StringBuilder();
    
        // If no InputStream is passed, perform the normal operations with environment variable substitution
        if (inputStream == null) {
            // Process the string array with environment variable substitution
            for (String part : parts) {
                if (part.startsWith("${") && part.endsWith("}")) {
                    String var = part.substring(2, part.length() - 1);  // Extract the variable name
                    String value = environmentVariables.get(var);  // Get the value from the environment map
                    if (value != null) {
                        output.append(value).append(" ");  // Append the resolved value
                    } else {
                        output.append(part).append(" ");  // If no value found, append the original part
                    }
                } else {
                    output.append(part).append(" ");  // Append the part if no variable substitution is needed
                }
            }
            // If not piping, print the result
            if (!isPiping) {
                System.out.println(output.toString().trim());
            }
        } else {
            // If an InputStream is passed, read and process it
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Append the content of the InputStream to the output
                output.append(line).append("\n");
            }
        }
    
        // Return the accumulated content as an InputStream for further processing in the pipeline
        return new ByteArrayInputStream(output.toString().getBytes());
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
     // Use the current directory from the user.dir property
     File currentDirectory = new File(System.getProperty("user.dir"));
     Path filePath = currentDirectory.toPath().resolve(fileName);
 
     // Check if the file exists before attempting to write to it
     if (!Files.exists(filePath)) {
         System.out.println("File does not exist: " + filePath.toAbsolutePath());
         return;
     }
 
     // Open a BufferedWriter with the specified file and append mode
     try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), append))) {
         Scanner scanner = new Scanner(System.in);
         System.out.println("Enter text to append to the file (Press Enter on an empty line to finish):");
 
         // Write each line entered by the user to the file
         String line = scanner.nextLine();
         if (line.isEmpty()) { // Pressing Enter on an empty line ends input
             System.out.println("No changes made. Exiting...");
             return;
         }
 
         writer.write(line);
         writer.newLine(); // Add a new line after the input
         System.out.println("Changes appended to " + filePath.toAbsolutePath());
     }
 }
 
  /**    * Reads and prints the contents of the specified file.
      *
      * @param fileName the name of the file to read
      * @throws IOException if an I/O error occurs
      */
     private static void readFile(String fileName) throws IOException {
     // Get the current working directory
     String currentDir = System.getProperty("user.dir");
     
     // Build the full path to the file
     Path path = Paths.get(currentDir, fileName);
     
     // Check if the file exists before reading
     if (!Files.exists(path)) {
         System.err.println("Error: File does not exist at " + path.toAbsolutePath());
         return;
     }
     
     // Read and print the file's lines
     Files.lines(path).forEach(System.out::println);
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
      private static InputStream sortFiles(String criteria, InputStream inputStream) throws IOException {
    // If no InputStream is provided, perform the original operation (sort files in the current directory)
    if (inputStream == null) {
        File dir = new File(System.getProperty("user.dir"));
        File[] files = dir.listFiles();

        if (files != null) {
            // Sort files based on criteria
            if (criteria.equals("name")) {
                // Use Collator for natural sorting (handles numbers and letters properly)
                Arrays.sort(files, (file1, file2) -> Collator.getInstance().compare(file1.getName(), file2.getName()));
            } else if (criteria.equals("date")) {
                Arrays.sort(files, Comparator.comparing(File::lastModified));
            } else {
                System.out.println(ConsoleColors.RED_BRIGHT + "Error: Please enter 'sort name' or 'sort date'");
                return new ByteArrayInputStream(new byte[0]);  // Return empty InputStream
            }

            // Format date for display
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Collect the sorted file names and dates
            StringBuilder sortedFileDetails = new StringBuilder();
            for (File file : files) {
                String formattedDate = sdf.format(file.lastModified());
                sortedFileDetails.append(file.getName()).append(" \t\t").append(formattedDate).append("\n");
            }

            // Print the sorted files to the console
            System.out.print(sortedFileDetails.toString());

            // Return the sorted files as InputStream
            return new ByteArrayInputStream(sortedFileDetails.toString().getBytes());
        } else {
            System.out.println("Directory is empty.");
            return new ByteArrayInputStream(new byte[0]);  // Return empty InputStream
        }
    } else {
        // If InputStream is provided, read its content and process it as if it were file names

        // Read the content from the InputStream
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        // Sort the content based on criteria
        if (criteria.equals("name")) {
            // Use Collator for natural sorting (handles numbers and letters properly)
            lines.sort(Collator.getInstance());
        } else if (criteria.equals("date")) {
            // If sorting by date, we assume the content has a date (e.g., formatted like "yyyy-MM-dd HH:mm:ss")
            // In a real scenario, we would need actual file metadata, but here we just sort lexicographically.
            lines.sort(Comparator.naturalOrder());  // Simplified as we don't have file metadata
        } else {
            System.out.println(ConsoleColors.RED_BRIGHT + "Error: Please enter 'sort name' or 'sort date'");
            return new ByteArrayInputStream(new byte[0]);  // Return empty InputStream if error
        }

        // Convert the sorted list back to InputStream
        String sortedContent = String.join("\n", lines);
        return new ByteArrayInputStream(sortedContent.getBytes());
    }
}
    
     
    //  private static void handleExternalCommand(String[] commands) throws IOException, InterruptedException {
    //       ProcessBuilder processBuilder = new ProcessBuilder(commands);
    //       processBuilder.inheritIO();
    //       Process process = processBuilder.start();
    //       process.waitFor();
    //   }


      private static void handleLogicalOr(String input) {
        String[] commands = input.split("\\s*\\|\\|\\s*");

        if (commands.length == 2) {
            boolean firstCommandFailed = !executeCommand(commands[0]);

            if (firstCommandFailed) {
                System.out.println("First command failed. Executing second command.");
                executeCommand(commands[1]);
            }
        }
    }

    // Method to handle input/output redirection with '<>' operator
    private static void handleRedirection(String input) {
        String[] parts = input.split("\\s*<>\\s*");

        if (parts.length == 2) {
            String command = parts[0].trim();
            String file = parts[1].trim();

            if (command.isEmpty() || file.isEmpty()) {
                System.out.println("Invalid command or file for redirection.");
                return;
            }

            executeCommandWithRedirection(command, file);
        }
    }

    // Method to execute a command
    private static boolean executeCommand(String command) {
        try {
            ProcessBuilder builder = new ProcessBuilder(command.split("\\s+"));
            Process process = builder.start();
            process.waitFor();
            return process.exitValue() == 0;
        } catch (Exception e) {
            System.out.println("Error executing command: " + e.getMessage());
            return false;
        }
    }

    // Method to handle commands with redirection (input or output)
    private static void executeCommandWithRedirection(String command, String file) {
        try {
            File redirectFile = new File(file);
            ProcessBuilder builder = new ProcessBuilder(command.split("\\s+"));
            builder.redirectOutput(redirectFile); // Redirect output to file
            Process process = builder.start();
            process.waitFor();
        } catch (Exception e) {
            System.out.println("Error with redirection: " + e.getMessage());
        }
    }

    // searchFile method to search for files in the current directory
    public static InputStream searchFile(String searchTerm, InputStream inputStream) throws IOException {
        // If no InputStream is provided, perform the original operation (search files in the current directory)
        if (inputStream == null) {
            Path startDir = Paths.get(System.getProperty("user.dir"));  // Start search in current directory
            List<String> foundFiles = new ArrayList<>();
            try {
                // Perform file search in the current directory and subdirectories
                Files.walk(startDir)
                    .filter(path -> path.getFileName().toString().contains(searchTerm))  // Filter by search term
                    .forEach(path -> foundFiles.add(path.toString()));
        
                // If no files are found, print a message, otherwise add results to foundFiles list
                if (foundFiles.isEmpty()) {
                    foundFiles.add("No files found matching '" + searchTerm + "'");
                }
            } catch (IOException e) {
                foundFiles.add("Error during search: " + e.getMessage());
            }
    
            // Print the found files to the console
            foundFiles.forEach(System.out::println);
    
            // Convert the list of results to a ByteArrayInputStream and return it
            return new ByteArrayInputStream(String.join("\n", foundFiles).getBytes());
        } else {
            // If an InputStream is provided, we will search through the stream's content for the search term
    
            // Read the InputStream content
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
    
            // Filter lines that contain the search term
            List<String> filteredResults = new ArrayList<>();
            for (String lineContent : lines) {
                if (lineContent.contains(searchTerm)) {
                    filteredResults.add(lineContent);
                }
            }
    
            // If no matches found, return a message indicating no matches
            if (filteredResults.isEmpty()) {
                filteredResults.add("No matching content found in the provided stream.");
            }
    
            // Print the results to the console
            filteredResults.forEach(System.out::println);
    
            // Return the results as a ByteArrayInputStream for further processing
            return new ByteArrayInputStream(String.join("\n", filteredResults).getBytes());
        }
    }
    

 
    /**
    * Handles execution of external commands by the operating system.
    *
    * @param commands an array of strings representing the command and its arguments
    * @throws IOException if an I/O error occurs
    * @throws InterruptedException if the process is interrupted
    */

    
    // Method to handle piping '|' operator
    // private static void handleIoAndPipes(String input) throws IOException, InterruptedException {
    //     // Split the input by the pipe character '|'
    //     String[] commands = input.split("\\|");

    //     if (commands.length >= 2) { // For more than one command in the pipe
    //         try {
    //             Process[] previousProcessHolder = new Process[1]; // Array to hold the reference to previous process

    //             for (int i = 0; i < commands.length; i++) {
    //                 String command = commands[i].trim();

    //                 // Create the process builder for the current command
    //                 ProcessBuilder builder = new ProcessBuilder(command.split("\\s+"));

    //                 // For all but the first command, set the input from the previous process's output
    //                 if (previousProcessHolder[0] != null) {
    //                     // Set input stream of current process to be from the previous process's output
    //                     builder.redirectInput(ProcessBuilder.Redirect.PIPE);
    //                 }

    //                 // For all but the last command, redirect the output to a pipe (to the next command)
    //                 if (i < commands.length - 1) {
    //                     builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
    //                 }

    //                 // Start the current process
    //                 Process currentProcess = builder.start();

    //                 // If this is not the first command, connect the output of the previous process to the input of the current process
    //                 if (previousProcessHolder[0] != null) {
    //                     // Create a thread to handle the piping of data between the processes
    //                     Thread outputThread = new Thread(() -> {
    //                         try {
    //                             // Create a buffer to read the output from the previous process and send it to the current one
    //                             InputStream inputStream = previousProcessHolder[0].getInputStream();
    //                             OutputStream outputStream = currentProcess.getOutputStream();
    //                             byte[] buffer = new byte[1024];
    //                             int bytesRead;

    //                             // Copy data from inputStream of previous process to outputStream of current process
    //                             while ((bytesRead = inputStream.read(buffer)) != -1) {
    //                                 outputStream.write(buffer, 0, bytesRead);
    //                             }
    //                             outputStream.flush();
    //                         } catch (IOException e) {
    //                             System.out.println("Error in piping output: " + e.getMessage());
    //                         }
    //                     });
    //                     outputThread.start();
    //                 }

    //                 // Wait for the previous process to finish before proceeding
    //                 if (previousProcessHolder[0] != null) {
    //                     previousProcessHolder[0].waitFor();
    //                 }

    //                 // Update the previous process for the next iteration
    //                 previousProcessHolder[0] = currentProcess;
    //             }

    //             // Wait for the last process to finish
    //             if (previousProcessHolder[0] != null) {
    //                 previousProcessHolder[0].waitFor();
    //             }

    //         } catch (Exception e) {
    //             System.out.println("Error executing pipe command: " + e.getMessage());
    //         }
    //     } else {
    //         System.out.println("Error: Invalid pipe command.");
    //     }
    // }


 

}


 

 
