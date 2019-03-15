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

package ca.nines.alfred.util;

import java.text.Normalizer;

public class Text {

    public static String normalize(String text) {
        if(text == null) {
            return "";
        }
        return Normalizer
                .normalize(text, Normalizer.Form.NFKD)
                .toLowerCase()
                .replaceAll("(\\p{Graph})\\p{Punct}+(?=\\p{Graph})", "$1")
                .replaceAll("\\p{Punct}+", " ")
                .replaceAll("\\s+", " ")
                .replaceAll("[^a-z0-9 -]", "")
                .trim();
    }

}
