package spctreutils.command;

import com.mojang.brigadier.CommandDispatcher;
import de.maxhenkel.admiral.MinecraftAdmiral;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

public class CommandManager
{
    public void initialize()
    {
        CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, _) ->
            registerCommands(dispatcher, commandBuildContext));
    }

    private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext)
    {
        MinecraftAdmiral.builder(dispatcher, commandBuildContext).addCommandClasses(Commands.class).build();
    }
}
