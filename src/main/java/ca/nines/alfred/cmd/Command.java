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

package ca.nines.alfred.cmd;

import org.apache.commons.cli.*;
import org.atteo.classindex.ClassIndex;
import org.atteo.classindex.IndexSubclasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Parent class for all commands understood by the application. Classes must also be annotated with {@link CommandInfo}
 * to be picked up by the command dispatcher. Abstract subclassses are automatically ignored.
 */
@IndexSubclasses
abstract public class Command {

    /**
     * Print a number to out every tickSize calls to the {@link #tick()} method.
     */
    int tickSize = 100;

    /**
     * Number of calls to {@link #tick()}.
     */
    long count;

    /**
     * Logger for commands. See The simplelogger.properties file to configure it.
     */
    final Logger logger;

    /**
     * Collection of commands that can be called from the commandline.
     */
    static final Map<String, Class<? extends Command>> commandList = new TreeMap<>();

    /**
     * Redirectable output stream. Defaults to System.out.
     */
    PrintStream out;

    /**
     * Redirectable error stream. Defaults to System.err.
     */
    PrintStream err;

    /**
     * Format progress numbers in a nice way.
     */
    NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    /**
     * Entry point for all commands.
     *
     * @param cmd parsed command line.
     * @throws Exception for IO errors.
     */
    abstract public void execute(CommandLine cmd) throws Exception;

    /**
     * Create a new command object. Sets out to System.out, err to System.err, and configures a default logger.
     */
    public Command() {
        out = System.out;
        err = System.err;
        logger = LoggerFactory.getLogger(this.getClass());
        count = 0;
    }

    /**
     * Reset the progress counter.
     */
    protected void reset() {
        out.println("\r" + formatter.format(count) + "\n");
        count = 0;
    }

    /**
     * Progress counter. Updates the progress display after {@link #tickSize} calls.
     */
    protected void tick() {
        count++;
        if (count % tickSize == 0) {
            out.print("\r" + formatter.format(count));
        }
    }

    /**
     * Build a list of commands by searching for all subclasses of Command. Keys in the returned list are determined
     * by the {@link CommandInfo} annotation.
     *
     * @return mapping of command names to command classes.
     */
    public static Map<String, Class<? extends Command>> getCommandList() {
        if (commandList.isEmpty()) {
            for (Class<? extends Command> cls : ClassIndex.getSubclasses(Command.class)) {
                if(Modifier.isAbstract(cls.getModifiers())) {
                    continue;
                }
                CommandInfo props = cls.getAnnotation(CommandInfo.class);
                if(props == null) {
                    continue;
                }
                commandList.put(props.name(), cls);
            }
        }
        return commandList;
    }

    /**
     * Get the default options for command, which only include -h | --help
     *
     * @return configured options
     */
    public Options getOptions() {
        Options opts = new Options();
        opts.addOption("h", "help", false, "Command description.");
        Option settings = Option.builder("d").argName("settings").numberOfArgs(2).valueSeparator().desc("Override a default setting").build();
        opts.addOption(settings);
        return opts;
    }

    /**
     * Parse the command line call to the program and return it.
     *
     * @param opts options returned by {@link #getOptions()}
     * @param args arguments passed to the program
     * @return parsed arguments
     */
    public CommandLine getCommandLine(Options opts, String[] args) {
        CommandLine cmd;
        CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse(opts, args);
            return cmd;
        } catch (ParseException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * Get the arguments passed to the command line. Filters out the name of the command from the arguments.
     *
     * @param commandLine Command line parsed by {@link #getCommandLine(Options, String[])}
     * @return a string array of arguments.
     */
    public String[] getArgList(CommandLine commandLine) {
        List<String> argList = commandLine.getArgList();
        argList = argList.subList(1, argList.size());
        String[] args = argList.toArray(new String[argList.size()]);
        return args;
    }

    /**
     * Print a usage string to System.out.
     */
    public void usage() {
        Class<? extends Command> cls = getClass();
        CommandInfo props = cls.getAnnotation(CommandInfo.class);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar alfred.jar " + props.name(), props.description(), getOptions(), "", true);
    }

    /**
     * Get a description of the command from the {@link CommandInfo} annotation.
     *
     * @return the description from the annotation
     */
    public String description() {
        Class<? extends Command> cls = getClass();
        CommandInfo props = cls.getAnnotation(CommandInfo.class);
        return props.description();
    }

    /**
     * Set the output stream.
     *
     * @param out an output stream
     */
    public void setOutput(PrintStream out) {
        this.out = out;
    }

    /**
     * Set the error stream.
     *
     * @param err an output stream
     */
    public void setError(PrintStream err) {
        this.err = err;
    }
}
