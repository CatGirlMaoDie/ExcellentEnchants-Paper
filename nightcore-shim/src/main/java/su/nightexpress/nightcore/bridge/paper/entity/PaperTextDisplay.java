package su.nightexpress.nightcore.bridge.paper.entity;

import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.NullMarked;

import su.nightexpress.nightcore.bridge.entity.WrappedTextDisplay;
import su.nightexpress.nightcore.bridge.paper.PaperBridge;
import su.nightexpress.nightcore.bridge.paper.text.PaperTextComponentAdapter;
import su.nightexpress.nightcore.util.bridge.wrapper.NightComponent;
import su.nightexpress.nightcore.util.text.night.NightMessage;

@NullMarked
public class PaperTextDisplay implements WrappedTextDisplay {

    private final TextDisplay               bukkit;
    private final PaperTextComponentAdapter componentAdapter;

    public PaperTextDisplay(TextDisplay bukkit, PaperTextComponentAdapter componentAdapter) {
        this.bukkit = bukkit;
        this.componentAdapter = componentAdapter;
    }

    @Override
    public TextDisplay getBukkit() {
        return this.bukkit;
    }

    @Override
    public String getText() {
        return PaperBridge.serializeComponent(this.bukkit.text());
    }

    @Override
    public void setText(String text) {
        this.setText(NightMessage.parse(text));
    }

    @Override
    public void setText(NightComponent component) {
        this.bukkit.text(this.componentAdapter.adaptComponent(component));
    }
}
