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

import ca.nines.alfred.main.Settings;
import org.apache.commons.cli.CommandLine;

import java.io.IOException;
import java.util.Formatter;

/**
 * Show some useful info to help a user out.
 */
@CommandInfo(name="settings", description="Show configurable settings for the application.")
public class SettingsCmd extends Command {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandLine cmd) throws IOException {
        Settings settings = Settings.getInstance();
        Formatter formatter = new Formatter(out);

        out.println("Configurable settings: ");
        formatter.format("%32s   %s%n", "Name", "Value");
        for(String name : settings.list()) {
            formatter.format("%32s   %s%n", name, settings.getString(name));
        }
        out.println("Use -d name=value to modify.");
    }

}
