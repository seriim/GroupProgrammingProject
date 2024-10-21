CustomShell - A Java-based Command-Line Shell
==========================


Overview
---------
The `CustomShell` is a Java-based command-line shell that allows users to interact with their file system, environment variables, and processes. It supports various commands for file and directory management, text manipulation, environment variable management, command history, and more. Additionally, it provides support for command I/O redirection and piping.

This `README` serves as a user guide, providing examples and explanations for each command supported by `CustomShell`.

Created by : `Raheem Gordon`, `Serena Morris` and `Dontray Blackwood`

Command Guide 
--------

File Operations commands were developed to do the following: `create`, `delete`, `rename`

Examples

* `create myfile.txt`: Creates a file called myfile.txt.
* `delete myfile.txt`: Deletes myfile.txt.
* `rename myfile.txt newfile.txt`: Renames myfile.txt to newfile.txt.

Directory Management were developed to do the following: `make`, `remove`, `change`

Examples

* `make newdir`: Creates a directory called newdir.
* `remove newdir`: Deletes newdir.
* `change /path/to/directory`: Changes to directory newdir.

File Access and Permissions: `modify`, `setenv`, `printenv`

Examples

* `modify rwxr-xr-x myfile.txt`: Sets the permissions for myfile.txt.
* `setenv PATH /usr/bin`: Sets an environment variable PATH.
* `printenv PATH`: Prints the value of PATH.

Other Commands: `help`, `location`, `list`, `print`,`tree`,`insert`,`append`, `history`, `fresh`, `sort <name|date>`, `exit`, `!!`

Examples

* `help`: Displays a list of available commands with brief descriptions.
* `location`: Displays the current working directory (similar to `pwd` in Unix-like systems).
* `list` : Lists the contents of the current directory.
* `print Hello, ${USER}`: Prints the specified text. Supports environment variable substitution (e.g., ${VAR})
* `tree`: Displays the directory structure in a tree-like format.
* `insert file.txt` : Inserts (overwrites) text into a file. Enter the text line by line, and press Ctrl+D (Unix) or Ctrl+Z (Windows) to save.
* `append file.txt`:Appends text to a file. Enter the text line by line, and press Ctrl+D (Unix) or Ctrl+Z (Windows) to save.
* `read file.txt`: Reads and prints the contents of a file.
* `history` :Displays the last 20 commands entered.
* `fresh` : Clears the console.
* `sort name` or `sort date`: Sorts the files in the current directory by name or modification date.
* `!!`: Repeats the last command.
* `exit`: Used to exit the shell

Handling External Commands
---------------------------
You can also execute system commands like listing files or launching external processes. For example: `ls -la`

I/O Redirection and Piping (Not Yet Fully Implemented)
-------------------------------------------------------
This shell includes partial support for I/O redirection (`>`, `<`) and piping (`|`), similar to Unix-based shells. More features will be added in future releases.






