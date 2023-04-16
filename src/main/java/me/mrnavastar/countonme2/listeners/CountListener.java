package me.mrnavastar.countonme2.listeners;

import com.expression.parser.exception.CalculatorException;
import com.expression.parser.function.FunctionX;
import me.mrnavastar.countonme2.Scoreboard;
import me.mrnavastar.countonme2.api.InsultAPI;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class CountListener extends ListenerAdapter {

    private int count = 0;
    private int highScore;
    private String lastSenderId = "";
    private long channelId = 0;

    public CountListener(int highScore) {
        this.highScore = highScore;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor();
        if (event.getChannel().getIdLong() != channelId || (author.isBot() && author.getName().equals("CountOnMe"))) return;

        try {
            FunctionX f_x = new FunctionX(event.getMessage().getContentRaw());
            if (count + 1 == f_x.getF_xo(0) && !lastSenderId.equals(author.getId())) {
                count++;
                lastSenderId = author.getId();
                Scoreboard.addCount(author);

                if (count > highScore) {
                    highScore = count;
                    event.getMessage().addReaction(Emoji.fromUnicode("\uD83C\uDF1F")).submit();
                } else event.getMessage().addReaction(Emoji.fromUnicode("✅")).submit();

            } else {
                count = 0;
                lastSenderId = "";
                event.getMessage().addReaction(Emoji.fromUnicode("❌")).submit();
                event.getChannel().sendMessage(InsultAPI.getRoast(author)).submit();
                Scoreboard.addFail(author);
            }
        } catch (CalculatorException ignore) {}
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        MessageChannelUnion channel = event.getChannel();

        if (event.getName().equals("count")) {
            this.channelId = channel.getIdLong();
            event.reply("Sounds good baby, I will only brutally roast people in " + channel.getAsMention()).setEphemeral(true).queue();
        }

        if (this.channelId != channel.getIdLong()) return;

        if (event.getName().equals("highscore")) event.reply("High Score : " + highScore).setEphemeral(false).queue();
        else if (event.getName().equals("scoreboard")) event.reply(MessageCreateData.fromEmbeds(Scoreboard.getEmbed())).setEphemeral(false).queue();
    }
}
