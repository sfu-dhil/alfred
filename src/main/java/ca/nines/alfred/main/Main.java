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

    public void run(String[] args) {
        Map<String, Command> commandList;
        try {
            commandList = Command.getCommandList();
        } catch (InstantiationException | IllegalAccessException ex) {
            this.err.println("Internal error: " + ex.getMessage());
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        String commandName = "help";
        if (args.length > 0) {
            commandName = args[0];
        }

        if (!commandList.containsKey(commandName)) {
            this.err.println("Unknown command: " + commandName);
            return;
        }

        Command cmd = commandList.get(commandName);
        Options opts = cmd.getOptions();
        CommandLine commandLine;

        commandLine = cmd.getCommandLine(opts, args);
        if(commandLine == null) {
            cmd.help();
            return;
        }

        if (commandLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar oscar.jar " + cmd.getCommandName() + " [options]", opts);
            System.out.println(cmd.getDescription());
            return;
        }

        try {
            cmd.execute(commandLine);
        } catch (Exception ex) {
            this.err.println("Internal error: " + ex.getMessage());
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.run(args);
    }

}
