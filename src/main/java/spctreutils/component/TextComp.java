package spctreutils.component;

import java.awt.*;

public record TextComp(String text, Color color) {

    public TextComp(String text) {
        this(text, Color.WHITE);
    }
}
