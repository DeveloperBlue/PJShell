
import java.io.BufferedReader;
import java.io.IOException; 
import java.io.InputStreamReader; 

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.File;
import java.lang.ProcessBuilder; // https://docs.oracle.com/javase/7/docs/api/java/lang/ProcessBuilder.html

public class PJShell {


	private static String current_working_directory;
		// This will be changed constantly as the user runs the 'cd <args>' command.
		// Running 'cd' with no args will set the directory back to the default directory of the main PJShell process, which can be obtained with System.getProperty("user.dir");

	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLUE = "\u001B[34m";
		// Used for recoloring the terminal

	private static Pattern whitespace_or_wrapped_regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
		// Regex for splitting strings by whitespace, unless they are in a phrase wrapped in quotation marks.

	public static void main(String[] args) throws IOException {

		// Save the current_working_directory to the default processes' working directory.

		current_working_directory = System.getProperty("user.dir"); // returns the working directory of PJShell.

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // User input reader

		System.out.println(ANSI_WHITE_BACKGROUND + ANSI_BLUE + "Welcome to PJShell. Press CTRL + D to exit." + ANSI_RESET + "\n");

		while(true){

			System.out.print(ANSI_BLUE + "PJShell> " + ANSI_RESET);

			/*

				ROUGH IDEA:

				Check if the input string is empty (remove all spaces and tabs from input string, check if that is equal to "") (Point 1)
				The input string is split into an array of commands (separated by ;). (Point 7)
				eg. command_stack = input_string.split(";") // => Returns an array of commands in order. (Point 8)
				We then loop through this command stack and fire each command using the process builder (Point 3), or handle it internally, like in the case of 'cd' (Point 6)

			*/

			String input_string = reader.readLine();

			// Trigger for user hitting CTRL + D
			if (input_string == null){
				System.out.println("Terminating . . .");
				System.exit(0);
			}

			String[] command_stack = input_string.trim().split(";");

			// Loop through each command in the stack . . .
			for (int c_i = 0; c_i < command_stack.length; c_i++){

				String command_str = command_stack[c_i]; // The command + it's parameters

				String[] command_split = command_str.trim().split(" ", 2); // Returns an array where [0] is the command and [1] is a single string of all the arguments after the command.

				// IF command_split.length == 1, a command was passed with no arguments
				// IF command_split.length == 2, a command was passed (index 0), with arguments (index 1).

				String command = command_split[0].trim();

				if (command.isEmpty()){
					// User entered nothing . . .
					continue; // Move to next command in stack
				}

				if (command.equals("exit")){
					System.out.println("Terminating . . .");
					System.exit(0);
				}

				if (command.equals("cd")){

					// As per Point 6, we must implement the cd command internally
					
					if (command_split.length == 1){

						// cd with no argument for the path should reset the current working directory;

						current_working_directory = System.getProperty("user.dir");

					} else {

						String cd_args_string = command_split[1];

						// The following splits the arguments string by spaces UNLESS they are wrapped in quotations.
						ArrayList<String> cd_args = new ArrayList<String>();
						Matcher regexMatcher = whitespace_or_wrapped_regex.matcher(cd_args_string.trim());

						while (regexMatcher.find()) {
							if (regexMatcher.group(1) != null) {
								// Add double-quoted string without the quotes
								cd_args.add(regexMatcher.group(1));
							} else if (regexMatcher.group(2) != null) {
								// Add single-quoted string without the quotes
								cd_args.add(regexMatcher.group(2));
							} else {
								// Add unquoted word
								cd_args.add(regexMatcher.group());
							}
						} 

						if (cd_args.size() > 1){

							System.out.println("cd: called with too many arguments.\nDirectories with spaces must be wrapped in quotations.");

						} else {

							// The getCanonicalPath() method of the File class is extremely convenient (so that you can easily deal with things like "cd ./././dir3/../dir2/../dir1//////../somedir", which is essentially "cd somedir").

							// If user-input path begins with '/', change directory relative to root. Otherwise, append to cwd.
							File new_working_directory = new File((cd_args.get(0).charAt(0) !== '/') ? current_working_directory : "") + "/" + cd_args.get(0);

							if (new_working_directory.exists() == false){
								System.out.println("Invalid directory '" + cd_args.get(0) + "'");
							} else {
								current_working_directory = new_working_directory.getCanonicalPath();
							}

						}

						// ProcessBuilder then runs with this as the directory.

					}

				} else {

					// Otherwise, send it off to the process builder . . .

					// Taken from ProcessBuilderDemo.java provided by the professor:

					try {

						// Send our command + args to the Process Builder
						ProcessBuilder process_builder;

						if (command_split.length > 1){
							process_builder = new ProcessBuilder(command, command_split[1].trim());
						} else {
							process_builder = new ProcessBuilder(command);
						}

						// Change working directory of process_builder to current_working_directory

						process_builder.directory(new File(current_working_directory));

						// Start the process builder, running a process
						final Process process = process_builder.start();

						// Pipe INPUTSTREAM and ERRORSTREAM from the process builder's process to this main process

						BufferedReader process_buffer_input = new BufferedReader(
							new InputStreamReader(
								process.getInputStream()
							)
						);

						BufferedReader process_buffer_error = new BufferedReader(
							new InputStreamReader(
								process.getErrorStream()
							)
						);

						String process_line;

						while ((process_line = process_buffer_input.readLine()) != null){
							System.out.println(process_line);
						}

						while ((process_line = process_buffer_error.readLine()) != null){
							System.out.println(process_line);
						}

					} catch (Exception e){
						System.out.println("No such command '" + command + "' exists.");
						// e.printStackTrace();
					}

				}

			}

		}

	}

}

/*

POINTS TO ADDRESS:

Implement PJShell so that it prints a prompt and accepts commands from users. These commands correspond to available user programs (e.g., ls, rm, cp, mkdir, rmdir) as well as some built-in commands (e.g., cd, or ;(semicolon operator)). Basically,

1. When the user types ↵ (a carriage return), then nothing should happen and the prompt should be printed again. 
2. The user can type additional spaces/tabs. 
3. When the user types a command, PJShell should run that command via the ProcessBuilder class, as described in the lecture. 
4. If there is an error (e.g., the command does not exist) then an appropriate error message should be displayed. 
5. The stdout and stderr streams of the created process should be captured and printed to the screen (we won't be using the stdin stream for now, meaning that interactive commands cannot be executed).
6. PJShell internally implements the cd command (that is, you can’t directly pass cd command down to Unix shell using  ProcessBuilder). Each process keeps track of its working directory and cd changes it (when PJShell runs a command via ProcessBuilder it can specify the working directory for the command). If the user simply types cd, then the current directory is changed to the working directory at the time PJShell was started (which PJShell must therefore "remember"). Finally, cd should print appropriate error messages. 
7. PJShell internally implements the ;(semicolon operator). The semicolon (;) operator allows you to execute multiple commands in succession, regardless of whether each previous command succeeds. 
8. Your code can’t directly pass commands chained by ; down to Unix shell. Instead your code must parse the command string, extract the commands and launch multiple  ProcessBuilder instances in succession,one for each command).
9. PJShell terminates when it meets EOF on standard input, i.e., when the user types ^D (Ctrl+D).

Important: It's ok to display all the command's stdout and then all the command's stderr (doing anything else will require more complex techniques that we'll learn later).

*/

/*

Michael Rooplall, Aleksandar Kamenev, Naglis Bukauskas

*/