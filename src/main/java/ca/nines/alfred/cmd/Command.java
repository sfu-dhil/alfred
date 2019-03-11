/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.cmd;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.atteo.classindex.ClassIndex;
import org.atteo.classindex.IndexSubclasses;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains methods that handle the terminal interface of the
 * application.
 *
 * @author mjoyce
 */
@IndexSubclasses
abstract public class Command {

    public static final int TICK_SIZE = 100;

    protected final Logger logger;

    private static final Map<String, Class<? extends Command>> commandList = new TreeMap<>();

    private long count;

    protected PrintStream out;

    protected PrintStream err;

    protected NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    abstract public void execute(CommandLine cmd) throws Exception;

    public Command() {
        out = System.out;
        err = System.err;
        logger = LoggerFactory.getLogger(this.getClass());
        count = 0;
    }

    protected void reset() {
        out.println("\r" + formatter.format(count));
        count = 0;
    }

    protected void tick() {
        count++;
        if (count % TICK_SIZE == 0) {
            out.print("\r" + formatter.format(count));
        }
    }

    public static final Map<String, Class<? extends Command>> getCommandList() {
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

    public Options getOptions() {
        Options opts = new Options();
        opts.addOption("h", "help", false, "Command description.");
        return opts;
    }

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

    public String[] getArgList(CommandLine commandLine) {
        List<String> argList = commandLine.getArgList();
        argList = argList.subList(1, argList.size());
        String[] args = argList.toArray(new String[argList.size()]);
        return args;
    }

    public void usage() {
        Class<? extends Command> cls = getClass();
        CommandInfo props = cls.getAnnotation(CommandInfo.class);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar alfred.jar " + props.name(), props.description(), getOptions(), "", true);
    }

    public String description() {
        Class<? extends Command> cls = getClass();
        CommandInfo props = cls.getAnnotation(CommandInfo.class);
        return props.description();
    }

    public void setOutput(PrintStream out) {
        this.out = out;
    }

    public void setError(PrintStream err) {
        this.err = err;
    }
}
