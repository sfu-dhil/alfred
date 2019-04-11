/*
 * Copyright (C) 2019 Michael Joyce
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package ca.nines.alfred.main;

import ca.nines.alfred.cmd.Command;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;

import ca.nines.alfred.util.Settings;
import org.apache.commons.cli.CommandLine;
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
        if (args.length > 0 && commandList.containsKey(commandName)) {
            commandName = args[0];
        }

        Command cmd = commandList.get(commandName).newInstance();
        cmd.setOutput(this.out);
        cmd.setError(this.err);

        Options opts = cmd.getOptions();
        CommandLine commandLine;
        commandLine = cmd.getCommandLine(opts, args);
        if(commandLine == null || commandLine.hasOption("help")) {
            cmd.usage();
            return;
        }

        Settings settings = Settings.getInstance();
        Properties props = commandLine.getOptionProperties("d");
        for(String name : props.stringPropertyNames()) {
            settings.set(name, props.getProperty(name));
        }

        cmd.execute(commandLine);
    }

    public static void main(String[] args) {
        Main m = new Main();
        try {
            m.run(args);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

}
