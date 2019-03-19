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

import ca.nines.alfred.comparator.Comparator;
import ca.nines.alfred.comparator.ComparatorInfo;
import org.apache.commons.cli.CommandLine;

import java.util.Formatter;
import java.util.Map;

/**
 * Show some useful info to help a user out.
 */
@CommandInfo(name="lc", description="List the comparators available.")
public class Comparators extends Command {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandLine cmd) {
        Formatter formatter = new Formatter(out);
        formatter.format("%16s   %s%n", "Algorithm", "Description");
        Map<String, Class<? extends Comparator>> list = Comparator.getComparatorList();
        for(String name : list.keySet()) {
            Class<? extends Comparator> cls = list.get(name);
            ComparatorInfo props = cls.getAnnotation(ComparatorInfo.class);
            formatter.format("%16s   %s%n", name, props.description());
        }
    }

}
