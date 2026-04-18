package spctreutils.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import spctreutils.SpctreUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Msg
{
    public static void sendChat(String msg, Color color)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        mc.gui.getChat()
            .addMessage(Component.literal("[SpctreUtils]: ")
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x55FFFF)))
                .append(Component.literal(msg)
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorHelper.rgbToHex(color))))));
    }

    public static void sendChat(String msg)
    {
        sendChat(msg, Color.WHITE);
    }

    public static void sendChat(Color color, String prefixMsg, String... msgs)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(prefixMsg);

        Arrays.stream(msgs)
            .skip(0)
            .map(msg -> "\n• " + msg)
            .forEach(sb::append);

        sendChat(sb.toString(), color);
    }

    public static void sendChat(String prefixMsg, String... msgs)
    {
        sendChat(Color.WHITE, prefixMsg, msgs);
    }

    public static <T extends List> void sendChat(T msgs, String prefixMsg, Color color)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(prefixMsg);

        for (Object msg : msgs)
        {
            try
            {
                sb.append("\n• " + msg.toString());
            }
            catch (Exception e) {}
        }
        sendChat(sb.toString(), color);
    }

    public static <T extends List> void sendChat(T msgs, String prefixMsg)
    {
        sendChat(msgs, prefixMsg, Color.WHITE);
    }

    public static void sendHud(String msg, Color color)
    {
        if (SpctreUtils.localPlayer == null) return;
        SpctreUtils.localPlayer.displayClientMessage(Component.literal(msg)
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorHelper.rgbToHex(color)))), true);
    }

    public static void sendHud(String msg)
    {
        sendHud(msg, Color.WHITE);
    }
}
