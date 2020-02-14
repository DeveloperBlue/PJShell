# PJShell

Linux based Java shell "simulator". It internally implements the cd and pwd commands along with the ; (semicolon) operator for running multiple commands in sucession on one line. Other commands are spawned into new processes with stdout and stderr hooks back the main Java program.

Project was completed as a group effort for CS 371 - Operating Systems - at Pace University.

---

### POINTS ADDRESSED:

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
