package ProjectCBW.MurderMystery.Functions.Essentials;

import ProjectCBW.MurderMystery.DataStorage.Userdata.Data;
import ProjectCBW.MurderMystery.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Text {

    public static String[] createAnimatedText(String text, ChatColor BeforeColor, ChatColor AfterColor, ChatColor ShadowColor) {
        String[] after = new String[text.length() + 4];
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

    public static String convert(String text, Player player) {
        Data data = Main.getPlayer(player).getData();
        return text
                .replace("%rank", String.valueOf(data.getRank()))
                .replace("%level", String.valueOf(data.getLevel()))
                .replace("%experience", String.valueOf(data.getExperience()))
                .replace("%interval", String.valueOf(data.getInterval()))
                .replace("%position", data.getPositionName());
    }

}
