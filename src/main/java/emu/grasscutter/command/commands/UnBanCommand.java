package emu.grasscutter.command.commands;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.permission.PermissionGroup;

import java.util.List;
import java.util.Objects;

import static emu.grasscutter.utils.Language.translate;

@Command(label = "unban", usage = "unban <uid>", permission = "server.unban", description = "commands.unban.description", permissionLevel = 2, targetRequirement = Command.TargetRequirement.NONE)
public class UnBanCommand implements CommandHandler {

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.size() < 1) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.unban.command_usage"));
            return;
        }

        String uid = args.get(0);
        try {
            Player entity = Grasscutter.getGameServer().getPlayerByUid
                    (Integer.parseInt(uid), true);


            if (!Objects.requireNonNull(entity).getAccount().isBanned())
                CommandHandler.sendMessage(sender, translate(sender, "commands.unban.no_ban"));
            else {
                try {
                    Objects.requireNonNull(entity).getAccount().setPermission
                            (Objects.requireNonNull(PermissionGroup.getGroupByName(Grasscutter.getConfig().account.basicPermission)).getNumber());
                    CommandHandler.sendMessage(sender, translate(sender, "commands.unban.success"));
                } catch (Exception e) {
                    CommandHandler.sendMessage(sender, translate(sender, "commands.unban.error"));
                }
            }

            Objects.requireNonNull(entity).getAccount().save();
        } catch (Exception e) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.unban.invalid_player"));
        }

    }
}
