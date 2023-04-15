package me.mrnavastar.countonme2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;

public class Scoreboard {

    private static HashMap<Long, PlayerData> scoreboard = new HashMap<>();
    private static final File scoreboardData = new File("data/scoreboard.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static class PlayerData {
        public int fails = 0;
        public int counts = 0;
    }

    public static void init() {
        if (!scoreboardData.exists()) save();

        try {
            FileReader reader = new FileReader(scoreboardData);
            scoreboard = gson.fromJson(reader, HashMap.class);
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save() {
        try {
            scoreboardData.getParentFile().mkdir();
            scoreboardData.createNewFile();
            FileWriter writer = new FileWriter(scoreboardData);
            gson.toJson(scoreboard, writer);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MessageEmbed getEmbed() {
        List<Map.Entry<Long, PlayerData>> players = new ArrayList<>(scoreboard.entrySet());
        players.sort(Comparator.comparingInt(e -> e.getValue().fails));

        int count = 1;
        StringBuilder message = new StringBuilder();
        message.append("#  Name       Fails\n");
        for (Map.Entry<Long, PlayerData> entry : players) {

            message.append(String.format("%d. %s | %d\n", count, "test", entry.getValue().fails));
            count++;
        }

        return new MessageEmbed(null, "Scoreboard", message.toString(), EmbedType.UNKNOWN,
                OffsetDateTime.MAX, 3, null, null, null, null,
                null, null, null);
    }

    private static PlayerData getPlayerData(long userId) {
        PlayerData data = scoreboard.get(userId);
        return  (data != null) ? data : new PlayerData();
    }

    public static void addFail(User author) {
        getPlayerData(author.getIdLong()).fails++;
        save();
    }

    public static void addCount(User author) {
        getPlayerData(author.getIdLong()).counts++;
        save();
    }
}