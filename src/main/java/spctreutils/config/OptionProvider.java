package spctreutils.config;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.*;
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
                this::getEnabled, v ->
                {
                    setEnabled(v);
                    ConfigManager.save();
                })
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
        Option.Builder<T> builder = Option.<T>createBuilder()
            .name(Component.literal(setting.getName()).withColor(Color.lightGray.getRGB()))
            .description(OptionDescription.of(Component.literal(setting.getDescription())))
            .binding(setting.getDefault(),
                setting::getValue,
                setting::setValue);

        if (setting.getType() == String.class)
            ((Option.Builder<String>) builder).controller(StringControllerBuilder::create);
        else if (setting.getType() == Boolean.class)
            ((Option.Builder<Boolean>) builder).controller(TickBoxControllerBuilder::create);
        else if (setting.getType() == Integer.class)
            ((Option.Builder<Integer>) builder).controller(IntegerFieldControllerBuilder::create);
        else if (setting.getType() == Float.class)
            ((Option.Builder<Float>) builder).controller(FloatFieldControllerBuilder::create);
        else if (setting.getType() == Double.class)
            ((Option.Builder<Double>) builder).controller(DoubleFieldControllerBuilder::create);
        else if (setting.getType() == Long.class)
            ((Option.Builder<Long>) builder).controller(LongFieldControllerBuilder::create);
        else if (setting.getType() == Color.class)
            ((Option.Builder<Color>) builder).controller(ColorControllerBuilder::create);

        return builder.build();
    }
}
