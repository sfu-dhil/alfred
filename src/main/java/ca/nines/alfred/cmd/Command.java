/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.cmd;

import java.io.IOException;
import java.io.PrintStream;
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
import org.atteo.classindex.ClassIndex;
import org.atteo.classindex.IndexSubclasses;
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

    protected final Logger logger;

    private static final Map<String, Class<? extends Command>> commandList = new TreeMap<>();

    private long count;

    protected PrintStream out;

    protected PrintStream err;

    abstract public void execute(CommandLine cmd) throws Exception;

    public Command() {
        out = System.out;
        err = System.err;
        logger = LoggerFactory.getLogger(this.getClass());
        count = 0;
    }

    protected void reset() {
        count = 0;
    }

    protected void tick() {
        count++;
        if (count % 1000 == 0) {
            out.print("\r" + NumberFormat.getNumberInstance(Locale.US).format(count));
        }
    }

    public static final Map<String, Class<? extends Command>> getCommandList() {
        if (commandList.isEmpty()) {
            for (Class<? extends Command> cls : ClassIndex.getSubclasses(Command.class)) {
                CommandInfo props = cls.getAnnotation(CommandInfo.class);
                commandList.put(props.name(), cls);
            }
        }
        return commandList;
    }

    public List<Path> findFiles(String root) throws IOException {
        return findFiles(Paths.get(root));
    }

    public List<Path> findFiles(Path root) throws IOException {
        Stream<Path> filePathStream = Files.find(root, 4, (path, attr) -> String.valueOf(path).endsWith(".xml"));
        ArrayList<Path> pathList = filePathStream.collect(Collectors.toCollection(ArrayList::new));
        return pathList;
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

    public String usage() {
        Class<? extends Command> cls = getClass();
        CommandInfo props = cls.getAnnotation(CommandInfo.class);
        return "java -jar alfred.jar " + props.name() + " [options]";
    }

    public String description() {
        Class<? extends Command> cls = getClass();
        CommandInfo props = cls.getAnnotation(CommandInfo.class);
        return props.description();
    }

    public void help() {
        HelpFormatter formatter = new HelpFormatter();
        Options opts = getOptions();
        Class<? extends Command> cls = getClass();
        CommandInfo props = cls.getAnnotation(CommandInfo.class);

        out.println(props.description());
        if (opts.getOptions().size() > 0) {
            formatter.printHelp(usage(), opts);
        } else {
            out.println(props.name() + " " + usage());
        }
    }

    public void setOutput(PrintStream out) {
        this.out = out;
    }

    public void setError(PrintStream err) {
        this.err = err;
    }
}
