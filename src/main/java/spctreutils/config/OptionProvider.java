package spctreutils.config;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.List;

public interface OptionProvider
{
    String getName();
    String getDescription();
    boolean getEnabled();
    void setEnabled(boolean value);
    List<Setting<?>> getSettings();

    default Option<Boolean> createOption()
    {
        return Option.<Boolean>createBuilder()
            .name(Component.literal(getName()))
            .description(OptionDescription.of(Component.literal(getDescription())))
            .binding(false,
                this::getEnabled,
                v -> { setEnabled(v); ConfigManager.save(); })
            .controller(TickBoxControllerBuilder::create)
            .build();
    }

    default OptionGroup createGroup()
    {
        OptionGroup.Builder group = OptionGroup.createBuilder()
                .name(Component.literal(getName()))
                .description(OptionDescription.of(Component.literal(getDescription())))
                .option(createOption());

        for (Setting<?> setting : getSettings())
            group.option(createSettingOption(setting));

        return group.build();
    }

    @SuppressWarnings("unchecked")
    default <T> Option<T> createSettingOption(Setting<T> setting)
    {
        var builder = Option.<T>createBuilder()
            .name(Component.literal(setting.getName()))
            .description(OptionDescription.of(Component.literal(setting.getDescription())))
            .binding(setting.getDefault(),
                setting::getValue,
                setting::setValue);

        if (setting.getType() == Boolean.class)
            ((Option.Builder<Boolean>)(Object)builder)
                .controller(TickBoxControllerBuilder::create);
        else if (setting.getType() == Float.class)
            ((Option.Builder<Float>)(Object)builder)
                .controller(opt -> FloatSliderControllerBuilder.create((Option<Float>)(Object)opt)
                    .range(0f, 1f).step(0.01f));
        else if (setting.getType() == Color.class)
            ((Option.Builder<Color>)(Object)builder)
                .controller(ColorControllerBuilder::create);

        return builder.build();
    }
}
