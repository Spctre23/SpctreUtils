package spctreutils.config.yacl;

import dev.isxander.yacl3.api.ListOption;
import net.minecraft.network.chat.Component;
import spctreutils.SpctreUtils;
import spctreutils.config.ConfigManager;
import spctreutils.hud.HudElement;

import java.util.List;

public interface HudOptionProvider extends OptionProvider
{
    default ListOption<HudElement> createDrawOrderOption()
    {
        return ListOption.<HudElement>createBuilder()
            .name(Component.literal(getName()))
            .binding(
                List.of(),
                () -> SpctreUtils.instance != null && SpctreUtils.instance.hud != null
                    ? SpctreUtils.instance.hud.getElements() : List.of(),
                v -> ConfigManager.save())
            .controller(HudControllerBuilder::create)
            .build();
    }
}
