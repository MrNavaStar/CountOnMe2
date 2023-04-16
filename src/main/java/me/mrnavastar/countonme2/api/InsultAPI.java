package me.mrnavastar.countonme2.api;

import net.dv8tion.jda.api.entities.User;

import java.util.Random;

public class InsultAPI {

    private static final String[] insultUrls = {
            "https://evilinsult.com/generate_insult.php?lang=en"
    };

    public static String getRoast(User user) {
        return user.getAsMention() + " " + APIHandler.getRaw(insultUrls[0]);
    }
}