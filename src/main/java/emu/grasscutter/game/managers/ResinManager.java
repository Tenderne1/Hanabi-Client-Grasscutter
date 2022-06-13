package emu.grasscutter.game.managers;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.server.packet.send.PacketPlayerPropNotify;
import emu.grasscutter.server.packet.send.PacketResinChangeNotify;
import emu.grasscutter.utils.Utils;

import static emu.grasscutter.Configuration.GAME_OPTIONS;

public class ResinManager {
    private final Player player;

    public ResinManager(Player player) {
        this.player = player;
    }

    /********************
     * Use resin.
     ********************/
    public synchronized boolean useResin(int amount) {
        int currentResin = this.player.getProperty(PlayerProperty.PROP_PLAYER_RESIN);
        
        // Check if the player has sufficient resin.
        if (currentResin < amount) {
            return false;
        }

        // Deduct the resin from the player.
        int newResin = currentResin - amount;
        this.player.setProperty(PlayerProperty.PROP_PLAYER_RESIN, newResin);

        // Check if this has taken the player under the recharge cap,
        // starting the recharging process.
        if (newResin < GAME_OPTIONS.resinOptions.cap) {
		    int currentTime = Utils.getCurrentSeconds();
            this.player.setNextResinRefresh(currentTime);
        }

        // Send packets.
        this.player.sendPacket(new PacketResinChangeNotify(this.player));
        this.player.sendPacket(new PacketPlayerPropNotify(this.player, PlayerProperty.PROP_PLAYER_RESIN));

        return true;
    }

    /********************
     * Recharge resin.
     ********************/
    public synchronized void rechargeResin() {
        int currentResin = this.player.getProperty(PlayerProperty.PROP_PLAYER_RESIN);
        int currentTime = Utils.getCurrentSeconds();

        // In case server administrators change the resin cap while players are capped,
        // we need to restart recharging here.
        if (currentResin < GAME_OPTIONS.resinOptions.cap && this.player.getNextResinRefresh() == 0) {
            this.player.setNextResinRefresh(currentTime + GAME_OPTIONS.resinOptions.rechargeTime);

            this.player.sendPacket(new PacketPlayerPropNotify(this.player, PlayerProperty.PROP_PLAYER_RESIN));
            this.player.sendPacket(new PacketResinChangeNotify(this.player));

            return;
        }

        // Make sure we are currently in "recharging mode".
        // This is denoted by Player.nextResinRefresh being greater than 0.
        if (this.player.getNextResinRefresh() <= 0) {
            return;
        }

        // Determine if we actually need to recharge yet.
        if (currentTime < this.player.getNextResinRefresh()) {
            return;
        }

        // Calculate how much resin we need to refill and update player.
        // Note that this can be more than one in case the player
        // logged off with uncapped resin and is now logging in again.
        int recharge = 1 + (int)((currentTime - this.player.getNextResinRefresh()) / GAME_OPTIONS.resinOptions.rechargeTime);
        int newResin = Math.min(GAME_OPTIONS.resinOptions.cap, currentResin + recharge);
        int resinChange = newResin - currentResin;

        this.player.setProperty(PlayerProperty.PROP_PLAYER_RESIN, newResin);

        // Calculate next recharge time.
        // Set to zero to disable recharge (because on/over cap.)
        if (newResin >= GAME_OPTIONS.resinOptions.cap) {
            this.player.setNextResinRefresh(0);
        }
        else {
            int nextRecharge = this.player.getNextResinRefresh() + resinChange * GAME_OPTIONS.resinOptions.rechargeTime;
            this.player.setNextResinRefresh(nextRecharge);
        }

        // Send packets.
        this.player.sendPacket(new PacketPlayerPropNotify(this.player, PlayerProperty.PROP_PLAYER_RESIN));
        this.player.sendPacket(new PacketResinChangeNotify(this.player));
    }
}
