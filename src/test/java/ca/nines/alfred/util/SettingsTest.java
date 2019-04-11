package ca.nines.alfred.util;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SettingsTest {

    private Settings settings;

    @Before
    public void setUp() throws IOException {
        settings = Settings.getInstance();
        settings.reset();
    }

    @Test
    public void getInt() {
        assertEquals(64, settings.getInt("iv"));
        assertEquals(0, settings.getInt("cheese"));
    }

    @Test
    public void getDouble() {
        assertEquals(5.4, settings.getDouble("dv"), 0.01);
        assertEquals(0, settings.getDouble("cheese"), 0.001);
    }

    @Test
    public void getString() {
        assertEquals("Cheese", settings.getString("sv"));
        assertEquals(0, settings.getInt("cheese"));
        assertNull(settings.getString("cheese"));
    }

    @Test
    public void list() {
        assertArrayEquals(new String[]{"dv", "iv", "sv"}, settings.list());
    }

    @Test
    public void set() {
        settings.set("asparagus", "123.4");
        assertEquals(123.4, settings.getDouble("asparagus"), 0.01);
    }

    @Test
    public void reset() {
        settings.set("asparagus", "123.4");
        settings.reset();
        assertArrayEquals(new String[]{"dv", "iv", "sv"}, settings.list());
    }
}
