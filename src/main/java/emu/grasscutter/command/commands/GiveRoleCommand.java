package emu.grasscutter.command.commands;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.Account;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.permission.PermissionGroup;

import java.util.List;

import static emu.grasscutter.utils.Language.translate;

@Command(label = "giverole", usage = "giverole <uid> <role>", aliases = {"role"}, permission = "server.roles", description = "Give Role to somebody", permissionLevel = 3, targetRequirement = Command.TargetRequirement.NONE)
public class GiveRoleCommand implements CommandHandler {

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (args.size() < 1) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.giverole.command_usage"));
            return;
        }

        String uid = args.get(0).toLowerCase();
        int role = 1;
        try {
            role = PermissionGroup.getGroupByName(args.get(1)).getNumber();
        } catch (Exception e) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.giverole.role_not_found"));
        }

        try {
            Player player = Grasscutter.getGameServer().getPlayerByUid(Integer.parseInt(uid), true);
            Account account = player.getAccount();
            account.setPermission(role);
        } catch (Exception e) {
            CommandHandler.sendMessage(sender, translate(sender, "commands.giverole.error"));
        }
    }
}
