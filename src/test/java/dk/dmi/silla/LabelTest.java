package dk.dmi.silla;

import dk.dmi.silla.model.Label;
import org.junit.Test;

import static org.junit.Assert.*;

public class LabelTest {

    @Test
    public void emptyConstructor() {
        Label label = new Label();
        assertEquals((Long) 0L, label.get());
    }

    @Test
    public void labelConstructor() {
        Label label = new Label(123456789L);
        assertEquals((Long) 123456789L, label.get());
    }

    @Test
    public void setExclude() {
        Label label = new Label();
        assertEquals((Long) 0L, label.get());
        label.setExclude();
        assertEquals((Long) 300000000L, label.get());
    }

    @Test
    public void clearExclude() {
        Label label = new Label();
        label.setExclude();
        assertEquals((Long) 300000000L, label.get());
        label.clearExclude();
        assertEquals((Long) 0L, label.get());
    }

    @Test
    public void isExcluded() {
        Label label = new Label();
        assertFalse(label.isExcluded());
        label.setExclude();
        assertTrue(label.isExcluded());
    }
}
