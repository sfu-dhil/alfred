package ca.nines.alfred.cmd;

import java.util.Formatter;
import java.util.Map;
import org.apache.commons.cli.CommandLine;

@CommandInfo(name="help", description="Describe the application and its usage.")
public class Help extends Command {

    @Override
    public void execute(CommandLine cmd) throws Exception {
        out.println("General usage: java -jar oscar.jar [command] [options]");
        out.println("Specific command: java -jar oscar.jar [command] -h");
        out.println();
        out.println("[command] is one of the following: ");
        listCommands();
    }

    public void listCommands() throws InstantiationException, IllegalAccessException {
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
