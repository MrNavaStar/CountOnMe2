package me.mrnavastar.countonme2;

import me.mrnavastar.countonme2.listeners.CountListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

import java.util.Collections;

public class Main {

    private static JDA jda;

    public static void main(String[] args) {
        String BOT_TOKEN = System.getenv().get("BOT_TOKEN");

        if (BOT_TOKEN == null) {
            System.out.println("Missing Bot Token!");
            System.exit(1);
        }

        Scoreboard.init();

        jda = JDABuilder.createDefault(BOT_TOKEN)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGE_REACTIONS)
            .addEventListeners(new CountListener(0))
            .setActivity(Activity.watching("People Fail At Math"))
            .build();

        jda.updateCommands().addCommands(
            Commands.slash("count", "Sets the channel for the bot to roast people in")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL)),
            Commands.slash("highscore", "Shows the highest count record"),
            Commands.slash("scoreboard", "Shows the count scoreboard")
        ).queue();
    }

    public static User getUser(String userId) {
        return jda.getUserById(userId);
    }
}