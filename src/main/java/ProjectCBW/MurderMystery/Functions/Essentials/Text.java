package ProjectCBW.MurderMystery.Functions.Essentials;

import org.bukkit.ChatColor;

public class Text {
    public static String[] createAnimatedText(final String text, final String BeforeColor, final String AfterColor, final String ShadowColor) {
        final String[] after = new String[text.length() + 4];
        StringBuilder builder = new StringBuilder();
        builder.append(text);
        after[after.length - 4] = AfterColor + text;
        after[after.length - 3] = BeforeColor + text;
        after[after.length - 2] = AfterColor + text;
        after[after.length - 1] = BeforeColor + text;
        for (int i = 1; i < text.length() + 1; ++i) {
            builder = new StringBuilder();
            builder.append(text);
            builder.insert(i, BeforeColor);
            builder.insert(i - 1, ShadowColor);
            builder.insert(0, AfterColor);
            after[i - 1] = builder.toString();
        }
        return after;
    }

    public static String getColoredText(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
