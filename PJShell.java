
import java.io.BufferedReader;
import java.io.IOException; 
import java.io.InputStreamReader; 

import java.io.File;
import java.lang.ProcessBuilder; // https://docs.oracle.com/javase/7/docs/api/java/lang/ProcessBuilder.html

public class PJShell {


	private static String working_directory_at_launch;
		// Save the cwd that the program was launched from. 
		// Running the 'cd' command will reset the current working directory to 'cwd' that we saved on run.

	private static String current_working_directory;
		// This will be changed constantly as the user runs the 'cd <args>' command.
		// 'cd' alone with no args will set this back to the working_directory_at_launch.

	private static boolean isQuit = false;
		// <todo>
		// CTRL + D quits application (Point 9)
		// Maybe have a listener on a different thread that force closes the program when CTRL + D is hit?
		// 

	public static void main(String[] args) throws IOException {

		// Save the cwd to working_directory_at_launch
		working_directory_at_launch = System.getProperty("user.dir"); // returns the working directory of PJShell.
		current_working_directory = working_directory_at_launch;

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // User input reader

		System.out.println("Welcome to PJShell. Press CTRL + D to exit.\n");

		while(isQuit == false){

			/*

				ROUGH IDEA:

				Check if the input string is empty (remove all spaces and tabs from input string, check if that is equal to "") (Point 1)
				The input string is split into an array of commands (separated by ;). (Point 7)
				eg. command_stack = input_string.split(";") // => Returns an array of commands in order. (Point 8)
				We then loop through this command stack and fire each command using the process builder (Point 3), or handle it internally, like in the case of 'cd' (Point 6)

				We should have a list of accepted commands that each command is checked against. (Point 4)

			*/

			String input_string = reader.readLine();

			String[] command_stack = input_string.split(";");

			// Loop through each command in the stack . . .
			for (int c_i = 0; c_i < command_stack.length; c_i++){

				String command_str = command_stack[c_i]; // The command + it's parameters

				// <TODO>
				// 'sanitize' command before parsing it and sending it to the process builder.
				// eg. removing any leading/ending spaces/tabs.
				// In javascript this is STRING.trim()
				// I believe this meets part of Point 2

				String[] command_split = command_str.split(" ", 2); // Returns an array where [0] is the command and [1] is a single string of all the arguments after the command.

				// IF command_split.length == 1, a command was passed with no arguments
				// IF command_split.length == 2, a command was passed (index 0), with arguments (index 1).

				String command = command_split[0]; // <todo> Sanitize here

				if (command == "" || command_split.length == 0){
					// User entered nothing . . .
					continue; // Move to next command in stack
				}

				
				if (command == "cd"){

					// As per Point 6, we must implement the cd command internally
					
					if (command_split.length == 1){

						// cd with no argument for the path should reset the current working directory;

						current_working_directory = working_directory_at_launch;

					} else {

						// The getCanonicalPath() method of the File class is extremely convenient (so that you can easily deal with things like "cd ./././dir3/../dir2/../dir1//////../somedir", which is essentially "cd somedir").
					
						// It stands to reason that if . . .
						// The System.getProperty("user.dir") call returns the working directory of PJShell.
						// . . . exists, then . . .
						// there must be a System.setProperty("user.dir", OUR_NEW_PATH) to set the new current working directory

						// <TODO>
						// Set current_working_directory to wherever the arguments command_split[1] take us (relative to the current_working_directory already set!)

					}

				} else {

					// Otherwise, send it off to the process builder . . .

					// Taken from ProcessBuilderDemo.java provided by the professor:

					try {

						// Send our command + args to the Process Builder
						ProcessBuilder process_builder = new ProcessBuilder(command);

						// <todo>
						// Change working directory of process_builder to current_working_directory

						// process_builder.directory(new File(current_working_directory)); // Throws error, idk

						// Start the process builder, running a process
						final Process process = process_builder.start();

						// Pipe output from the process builder's process to this main process
						BufferedReader process_buffer = new BufferedReader(
							new InputStreamReader(
								process.getInputStream()
							)
						);

						String process_line;

						while ((process_line = process_buffer.readLine()) != null){
							System.out.println(process_line);
						}

					} catch (Exception e){
						e.printStackTrace();
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

<TODO>

Add your last names :)

Michael Rooplall, Alex, Naglis

*/