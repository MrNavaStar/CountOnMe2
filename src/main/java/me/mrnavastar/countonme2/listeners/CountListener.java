package me.mrnavastar.countonme2.listeners;

import com.expression.parser.exception.CalculatorException;
import com.expression.parser.function.FunctionX;
import me.mrnavastar.countonme2.Scoreboard;
import me.mrnavastar.countonme2.api.InsultAPI;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CountListener extends ListenerAdapter {

    private int count = 0;
    private int highScore;
    private User lastSender = null;
    private long channelId = 0;

    public CountListener(int highScore) {
        this.highScore = highScore;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != channelId) return;
        User author = event.getAuthor();

        try {
            FunctionX f_x = new FunctionX(event.getMessage().getContentRaw());
            if (count + 1 != f_x.getF_xo(0) || author == lastSender) {
                count = 0;
                event.getMessage().addReaction(Emoji.fromUnicode("❌")).submit();
                event.getChannel().sendMessage(InsultAPI.getRoast(author)).submit();
                Scoreboard.addFail(author);
                return;
            }
        } catch (CalculatorException ignore) {}

        count++;
            lastSender = author;
            Scoreboard.addCount(author);

            if (count > highScore) {
                highScore = count;
                event.getMessage().addReaction(Emoji.fromUnicode("\uD83C\uDF1F")).submit();
            } else {
                event.getMessage().addReaction(Emoji.fromUnicode("✅")).submit();
            }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        MessageChannelUnion channel = event.getChannel();

        if (event.getName().equals("count")) {
            this.channelId = channel.getIdLong();
            event.reply("Sounds good baby, I will only brutally roast people in " + channel.getAsMention()).setEphemeral(true).queue();
        }

        if (this.channelId != channel.getIdLong()) return;

        switch (event.getName()) {
            case "highscore": {
                channel.sendMessage("High Score : " + highScore);
            }

            case "scoreboard": {
                channel.sendMessageEmbeds(Scoreboard.getEmbed());
            }
        }
    }
}
