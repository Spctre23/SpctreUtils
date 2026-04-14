package spctreutils.hud;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import spctreutils.config.ConfigManager;
import spctreutils.config.ModConfig;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class HudElement
{
    protected final Minecraft mc;
    protected int prefixColor = ConfigManager.config.hudPrefixColor;
    protected int textColor = ConfigManager.config.hudTextColor;
    private String name;
    private String description;
    private boolean enabled;
    private Component text = null;
    private final String prefix;
    private final Function<ModConfig, Boolean> configGetter;
    private final Consumer<Boolean> configSetter;

    protected HudElement(String name, String prefix, String description, Function<ModConfig, Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this.name = name;
        this.prefix = prefix + ": ";
        this.description = description;
        this.mc = Minecraft.getInstance();
        this.configGetter = configGetter;
        this.configSetter = configSetter;
        this.enabled = configGetter.apply(ConfigManager.config);
        initialize();
    }

    protected HudElement(String name, String description, Function<ModConfig, Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this(name, name, description, configGetter, configSetter);
    }

    protected void onEnabled() {}

    protected void onDisabled() { removeText(); }

    protected void onTick() {}

    public boolean isEnabled() { return enabled; }

    public Option<Boolean> createOption()
    {
        return Option.<Boolean>createBuilder()
            .name(Component.literal(name))
            .description(OptionDescription.of(Component.literal(description)))
            .binding(false,
                () -> configGetter.apply(ConfigManager.config),
                v -> { configSetter.accept(v); ConfigManager.save(); })
            .controller(TickBoxControllerBuilder::create)
            .build();
    }

    private void initialize()
    {
        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            syncFromConfig();
            if (enabled && client.player != null) onTick();
        });
    }

    private void syncFromConfig()
    {
        prefixColor = ConfigManager.config.hudPrefixColor;
        textColor = ConfigManager.config.hudTextColor;
        boolean configValue = configGetter.apply(ConfigManager.config);
        if (configValue == enabled) return;
        enabled = configValue;
        onStateChanged();
    }

    private void onStateChanged()
    {
        if (enabled) onEnabled();
        else onDisabled();
    }

    public int getPrefixColor() { return prefixColor; }

    public Component getText() { return text; }

    protected void setText(String text, int textColor)
    {
        this.text = Component.literal(prefix).withColor(prefixColor)
            .append(Component.literal(text).withColor(textColor));
    }

    protected void setText(String text) { setText(text, textColor); }

    protected void removeText() { text = null; }
}