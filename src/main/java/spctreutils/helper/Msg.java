package spctreutils.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import spctreutils.component.TextComp;

import java.awt.*;
import java.util.Arrays;

public class Msg
{
    public static void sendChat(Component component)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.gui.getChat().addMessage(Component.literal("[SpctreUtils]: ")
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x55FFFF)))
            .append(component));
    }

    public static void sendChat(String msg, Color color)
    {
        sendChat(Component.literal(msg)
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorHelper.rgbToHex(color)))));
    }

    public static void sendChat(String msg)
    {
        sendChat(msg, Color.WHITE);
    }

    public static void sendMulticoloredChat(int spacing, TextComp... comps)
    {
        MutableComponent text = Component.empty();
        for (TextComp comp : comps)
        {
            text.append(comp.getText() + " ".repeat(spacing)).withColor(ColorHelper.rgbToHex(comp.getColor()));
        }

        sendChat(text);
    }

    public static void sendMulticoloredChat(TextComp... comps)
    {
        sendMulticoloredChat(1, comps);
    }

    public static void sendBulletedListChat(Color color, String prefixMsg, String... msgs)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(prefixMsg);

        Arrays.stream(msgs)
            .skip(0)
            .map(msg -> "\n• " + msg)
            .forEach(sb::append);

        sendChat(sb.toString(), color);
    }

    public static void sendBulletedListChat(String prefixMsg, String... msgs)
    {
        sendBulletedListChat(Color.WHITE, prefixMsg, msgs);
    }

    public static void sendHud(Component component)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        player.displayClientMessage(component, true);
    }

    public static void sendHud(TextComp... comps)
    {
        MutableComponent text = Component.empty();
        for (TextComp comp : comps)
        {
            text.append(comp.getText()).withColor(ColorHelper.rgbToHex(comp.getColor()));
        }

        sendHud(text);
    }

    public static void sendHud(String msg, Color color)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        Component component = Component.literal(msg)
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorHelper.rgbToHex(color))));
        sendHud(component);
    }

    public static void sendHud(String msg)
    {
        sendHud(msg, Color.WHITE);
    }
}
