/*
 * The MIT License
 *
 * Copyright 2019 Michael Joyce
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
