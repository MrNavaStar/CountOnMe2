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

    private static HashMap<String, PlayerData> scoreboard = new HashMap<>();
    private static final File scoreboardData = new File("data/scoreboard.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static class PlayerData {
        public int fails = 0;
        public int counts = 0;
        public int streak = 0;
        public int longestStreak = 0;
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
            scoreboardData.delete();
            scoreboardData.createNewFile();
            FileWriter writer = new FileWriter(scoreboardData, false);
            gson.toJson(scoreboard, writer);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MessageEmbed getEmbed() {
        List<Map.Entry<String, PlayerData>> players = new ArrayList<>(scoreboard.entrySet());
        players.sort(Comparator.comparingInt(e -> e.getValue().fails));

        int count = 1;
        StringBuilder message = new StringBuilder();
        message.append("# Name Fails\n");
        for (Map.Entry<String, PlayerData> entry : players) {
            PlayerData playerData = entry.getValue();

            message.append(String.format("%d. %s | %d\n", count, Main.getUser(entry.getKey()), playerData.fails));
            count++;
        }

        return new MessageEmbed(null, "Scoreboard", message.toString(), EmbedType.UNKNOWN,
                OffsetDateTime.now(), 5, null, null, null, null,
                null, null, null);
    }

    private static PlayerData getPlayerData(String userId) {
        PlayerData data = scoreboard.get(userId);
        if (data == null) {
            data = new PlayerData();
            scoreboard.put(userId, data);
        }
        return data;
    }

    public static void addFail(User author) {
        PlayerData playerData = getPlayerData(author.getId());
        playerData.fails++;
        playerData.streak = 0;
        save();
    }

    public static void addCount(User author) {
        PlayerData playerData = getPlayerData(author.getId());
        playerData.counts++;
        playerData.streak++;
        if (playerData.streak > playerData.longestStreak) playerData.longestStreak = playerData.streak;
        save();
    }
}