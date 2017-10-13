package me.shadorc.discordbot.command.currency;

import java.time.temporal.ChronoUnit;

import me.shadorc.discordbot.command.AbstractCommand;
import me.shadorc.discordbot.command.CommandCategory;
import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.command.Role;
import me.shadorc.discordbot.data.Config;
import me.shadorc.discordbot.data.DBUser;
import me.shadorc.discordbot.data.Storage;
import me.shadorc.discordbot.utils.BotUtils;
import me.shadorc.discordbot.utils.StringUtils;
import me.shadorc.discordbot.utils.TextUtils;
import me.shadorc.discordbot.utils.Utils;
import me.shadorc.discordbot.utils.command.Emoji;
import me.shadorc.discordbot.utils.command.MissingArgumentException;
import me.shadorc.discordbot.utils.command.RateLimiter;
import sx.blah.discord.util.EmbedBuilder;

public class TransferCoinsCmd extends AbstractCommand {

	private final RateLimiter rateLimiter;

	public TransferCoinsCmd() {
		super(CommandCategory.CURRENCY, Role.USER, "transfer");
		this.rateLimiter = new RateLimiter(RateLimiter.COMMON_COOLDOWN, ChronoUnit.SECONDS);
	}

	@Override
	public void execute(Context context) throws MissingArgumentException {
		if(rateLimiter.isSpamming(context)) {
			return;
		}

		if(!context.hasArg()) {
			throw new MissingArgumentException();
		}

		String[] splitCmd = context.getArg().split(" ", 2);
		if(splitCmd.length != 2 || context.getMessage().getMentions().size() != 1) {
			throw new MissingArgumentException();
		}

		DBUser receiverPlayer = Storage.getUser(context.getGuild(), context.getMessage().getMentions().get(0));
		DBUser senderPlayer = context.getUser();
		if(senderPlayer.equals(receiverPlayer)) {
			BotUtils.sendMessage(Emoji.GREY_EXCLAMATION + " You cannot transfer coins to yourself.", context.getChannel());
			return;
		}

		String coinsStr = splitCmd[0];
		if(!StringUtils.isPositiveInt(coinsStr)) {
			BotUtils.sendMessage(Emoji.GREY_EXCLAMATION + " Invalid amount.", context.getChannel());
			return;
		}

		int coins = Integer.parseInt(coinsStr);
		if(senderPlayer.getCoins() < coins) {
			BotUtils.sendMessage(TextUtils.NOT_ENOUGH_COINS, context.getChannel());
			return;
		}

		if(receiverPlayer.getCoins() + coins >= Config.MAX_COINS) {
			BotUtils.sendMessage(Emoji.BANK + " This transfer cannot be done because " + receiverPlayer.getUser().getName()
					+ " would exceed the maximum coins cap.", context.getChannel());
			return;
		}

		senderPlayer.addCoins(-coins);
		receiverPlayer.addCoins(coins);

		BotUtils.sendMessage(Emoji.BANK + " " + senderPlayer.getUser().mention() + " has transfered **"
				+ coins + " coins** to " + receiverPlayer.getUser().mention(), context.getChannel());
	}

	@Override
	public void showHelp(Context context) {
		EmbedBuilder builder = Utils.getDefaultEmbed(this)
				.appendDescription("**Transfer coins to the mentioned user.**")
				.appendField("Usage", "`" + context.getPrefix() + "transfer <coins> <@user>`", false);
		BotUtils.sendMessage(builder.build(), context.getChannel());
	}
}
