package ca.nines.alfred.cmd;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.WordUtils;
import sun.misc.Launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@ca.nines.alfred.cmd.CommandInfo(name = "sw", description = "Show info about stop word lists")
public class StopWords extends Command {

    public static final String PATH = "stopwords";

    private List<String> listStopWordFiles() throws IOException, URISyntaxException {
        List<String> filenames = new ArrayList<>();
        File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        if (file.isFile()) {  // Run with JAR file
            JarFile jar = new JarFile(file);
            Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(PATH + "/")) { //filter according to the path
                    filenames.add(name.substring(PATH.length() + 1));
                }
            }
            jar.close();
        } else {
            URL url = Launcher.class.getResource("/" + PATH);
            if (url != null) {
                final File apps = new File(url.toURI());
                for (File app : apps.listFiles()) {
                    System.out.println(app);
                }
            }
        }
        return filenames;
    }

    @Override
    public void execute(org.apache.commons.cli.CommandLine cmd) throws Exception {
        String[] arguments = getArgList(cmd);
        if(arguments.length > 0) {
            for(String name : arguments) {
                out.println("Word list " + name + " contents:");
                InputStream in = this.getClass().getResourceAsStream("/" + PATH + "/" + name);
                for(String line : IOUtils.readLines(in, StandardCharsets.UTF_8)) {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    out.println(line);
                }
            }
        } else {
            out.println("Stop word lists available:");
            for(String name : listStopWordFiles()) {
                if(name.trim().isEmpty()) {
                    continue;
                }
                out.println("  " + name);
            }
        }
    }
}
