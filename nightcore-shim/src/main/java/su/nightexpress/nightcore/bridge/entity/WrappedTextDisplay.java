package su.nightexpress.nightcore.bridge.entity;

import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.NullMarked;

import su.nightexpress.nightcore.util.bridge.Software;
import su.nightexpress.nightcore.util.bridge.wrapper.NightComponent;

@NullMarked
public interface WrappedTextDisplay {

    static WrappedTextDisplay wrap(TextDisplay display) {
        return Software.get().wrapTextDisplay(display);
    }

    TextDisplay getBukkit();

    String getText();

    void setText(String text);

    void setText(NightComponent component);
}
