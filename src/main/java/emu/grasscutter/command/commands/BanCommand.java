package emu.grasscutter.command.commands;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;

import java.util.List;
import java.util.Objects;

import static emu.grasscutter.utils.Language.translate;

@Command(label = "ban", usage = "ban <uid>", aliases = {"sus"}, permission = "server.ban", description = "commands.ban.description", permissionLevel = 2, targetRequirement = Command.TargetRequirement.NONE)
public class BanCommand implements CommandHandler {

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.size() < 1) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.ban.command_usage"));
            return;
        }

        String uid = args.get(0);
        try {
            Objects.requireNonNull(Grasscutter.getGameServer().getPlayerByUid
                    (Integer.parseInt(uid), true)).getAccount().setBanned();
        } catch (Exception e) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.ban.error"));
        }
    }
}
