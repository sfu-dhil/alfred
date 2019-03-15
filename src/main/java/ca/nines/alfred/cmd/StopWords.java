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

import org.apache.commons.io.IOUtils;
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
