/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nines.alfred.cmd;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Map;
import org.apache.commons.cli.CommandLine;

/**
 * This class contains methods that provides help functionality for the command prompt interface.
 * @author mjoyce
 */
public class Help extends Command {

    @Override
    public String getDescription() {
        return "Dsiaply useful help messages.";
    }

    @Override
    public void execute(CommandLine cmd) throws Exception {
        System.out.println("General usage: java -jar oscar.jar [command] [options]");
        System.out.println("Specific command: java -jar oscar.jar [command] -h");
        System.out.println();
        System.out.println("[command] is one of the following: ");
        listCommands();
    }

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "java -jar oscar.jar help";
    }

    public void listCommands() throws InstantiationException, IllegalAccessException {
        Formatter formatter = new Formatter(System.out);
        formatter.format("%n%16s   %s%n", "Command", "Description");

        Map<String, Command> list = Command.getCommandList();
        String names[] = list.keySet().toArray(new String[list.size()]);
        Arrays.sort(names);
        for (String name : names) {
            formatter.format("%16s   %s%n", name, list.get(name).getDescription());
        }
    }
}
