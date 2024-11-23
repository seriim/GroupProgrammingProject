CustomShell - A Java-based Command-Line Shell
==========================


Overview
---------
The `CustomShell` is a Java-based command-line shell that allows users to interact with their file system, environment variables, and processes. It supports various commands for file and directory management, text manipulation, environment variable management, command history, and more. Additionally, it provides support for command I/O redirection and piping.

This `README` serves as a user guide, providing examples and explanations for each command supported by `CustomShell`.

Created by: `Raheem Gordon`, `Serena Morris` and `Dontray Blackwood`

Command Guide 
--------

File Operations commands were developed to do the following:
* `create myfile.txt`: Creates a file called myfile.txt.
* `delete myfile.txt`: Deletes myfile.txt.
* `rename myfile.txt newfile.txt`: Renames myfile.txt to newfile.txt.

Directory Management were developed to do the following:
* `make newdir`: Creates a directory called newdir.
* `remove newdir`: Deletes newdir.
* `change /path/to/directory`: Changes to directory newdir.

File Access and Permissions: 
* `modify rwxr-xr-x myfile.txt`: Sets the permissions for myfile.txt.
* `setenv PATH /usr/bin`: Sets an environment variable PATH.
* `printenv PATH`: Prints the value of PATH.
* `print Hello, ${USER}`: Prints the specified text. Supports environment variable substitution (e.g., ${VAR})

Text Manipulation
* `insert file.txt`: Inserts (overwrites) text into a file. Enter the text line by line, and press Ctrl+D (Unix) or Ctrl+Z (Windows) to save.
* `append file.txt`:Appends text to a file. Enter the text line by line, and press Ctrl+D (Unix) or Ctrl+Z (Windows) to save.
* `read file.txt`: Reads and prints the contents of a file.


Other Commands
* `help`: Displays a list of available commands with brief descriptions.
* `location`: Displays the current working directory (similar to `pwd` in Unix-like systems).
* `list`: Lists the contents of the current directory.
* `tree`: Displays the directory structure in a tree-like format.
* `history`: Displays the last 20 commands entered.
* `fresh`: Clears the console.
* `sort name` or `sort date`: Sorts the files in the current directory by name or modification date.
* `search` <pattern>: Searches for a specified pattern (text) within all files in the current directory. Like the grep command, it returns all pattern occurrences and the file names.
* `!!`: Repeats the last command.
* `exit`: Used to exit the shell

I/O Redirection and Piping (Not Yet Fully Implemented)
------------------------------------------------------
This shell includes partial support for I/O redirection (`>`, `<`) and piping (`|`), similar to Unix-based shells. More features will be added in future releases.


System Requirements
-------------------
* Java 11 or later
* Operating System: Windows, macOS, or Linux

### Example Usage
-----------------

#### File Operations
```shell
create notes.txt
append notes.txt
    (Enter text line by line and press Ctrl+D or Ctrl+Z to save)
read notes.txt
rename notes.txt journal.txt
list
delete journal.txt
```
#### Directory and Environment Variables
```shell
make projects
change projects
location
setenv PROJECT_PATH /home/user/projects
printenv PROJECT_PATH
list
remove projects
```
#### Searching Files
```shell
search .java
search file.txt
```
#### System Commands and Redirection
```shell
### Key Points:

- Use triple backticks (\`\`\`) before and after the code blocks to specify a block of code or commands. Optionally, you can specify the language (`shell` in this case) after the first set of backticks for syntax highlighting.
- GitHub will automatically render these properly formatted commands as code blocks.

When you put this in your **README.md** file on GitHub, it will display the example usage clearly with the proper formatting.

```



