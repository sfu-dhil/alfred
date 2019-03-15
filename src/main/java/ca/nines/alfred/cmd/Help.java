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

import org.apache.commons.cli.CommandLine;

import java.util.Formatter;
import java.util.Map;

/**
 * Show some useful info to help a user out.
 */
@CommandInfo(name="help", description="Describe the application and its usage.")
public class Help extends Command {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandLine cmd) {
        out.println("General usage: java -jar oscar.jar [command] [options]");
        out.println("Specific command: java -jar oscar.jar [command] -h");
        out.println();
        out.println("[command] is one of the following: ");
        listCommands();
    }

    /**
     * Print out a list of commands and descriptions.
     */
    public void listCommands() {
        Formatter formatter = new Formatter(out);
        formatter.format("%n%16s   %s%n", "Command", "Description");

        Map<String, Class<? extends Command>> list = Command.getCommandList();
        for (String name : list.keySet()) {
            Class<? extends Command> cls = list.get(name);
            CommandInfo props = cls.getAnnotation(CommandInfo.class);
            formatter.format("%16s   %s%n", name, props.description());
        }
    }
}
