/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.main;

import ca.nines.alfred.cmd.Command;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * This class handles the main command prompt interface of the application. It
 * accepts a set of commands, and, if input does not match to any element in the
 * set, throws an exception and redirects to correct usage.
 *
 * @author mjoyce
 */
public class Main {

    private PrintStream out;

    private PrintStream err;

    public Main() {
        this.out = System.out;
        this.err = System.err;
    }

    public OutputStream getOutStream() {
        return out;
    }

    public void setOutStream(PrintStream out) {
        this.out = out;
    }

    public OutputStream getErrStream() {
        return err;
    }

    public void setErrStream(PrintStream err) {
        this.err = err;
    }

    public void run(String[] args) throws Exception {
        Map<String, Class<? extends Command>> commandList;
        commandList = Command.getCommandList();

        String commandName = "help";
        if (args.length > 0) {
            commandName = args[0];
        }

        if (!commandList.containsKey(commandName)) {
            this.err.println("Unknown command: " + commandName);
        }

        Command cmd = commandList.get(commandName).newInstance();
        cmd.setOutput(this.out);
        cmd.setError(this.err);

        Options opts = cmd.getOptions();
        CommandLine commandLine;
        commandLine = cmd.getCommandLine(opts, args);
        if(commandLine == null || commandLine.hasOption("help")) {
            cmd.help();
            out.println(cmd.usage());
            out.println(cmd.description());
            return;
        }

        cmd.execute(commandLine);
    }

    public static void main(String[] args) {
        Main m = new Main();
        try {
            m.run(args);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }

}
