package emu.grasscutter.game.dungeons.challenge;

import emu.grasscutter.data.common.ItemParamData;
import emu.grasscutter.data.excels.DungeonData;
import emu.grasscutter.game.dungeons.challenge.trigger.ChallengeTrigger;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.inventory.ItemType;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.net.proto.GadgetInteractReqOuterClass;
import emu.grasscutter.scripts.constants.EventType;
import emu.grasscutter.scripts.data.SceneGroup;
import emu.grasscutter.scripts.data.ScriptArgs;
import emu.grasscutter.server.packet.send.PacketGadgetAutoPickDropInfoNotify;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.List;

public class DungeonChallenge extends WorldChallenge {

	/**
	 * has more challenge
	 */
	private boolean stage;
	private IntSet rewardedPlayers;

	public DungeonChallenge(Scene scene, SceneGroup group,
							int challengeId, int challengeIndex,
							List<Integer> paramList,
							int timeLimit, int goal,
							List<ChallengeTrigger> challengeTriggers) {
		super(scene, group, challengeId, challengeIndex, paramList, timeLimit, goal, challengeTriggers);
		this.setRewardedPlayers(new IntOpenHashSet());
	}

	public boolean isStage() {
		return stage;
	}

	public void setStage(boolean stage) {
		this.stage = stage;
	}

	public IntSet getRewardedPlayers() {
		return rewardedPlayers;
	}

	public void setRewardedPlayers(IntSet rewardedPlayers) {
		this.rewardedPlayers = rewardedPlayers;
	}

	@Override
	public void done() {
		super.done();
		if (this.isSuccess()) {
			// Settle
			settle();
		}
	}
	
	private void settle() {
		if(!stage){
			getScene().getDungeonSettleObservers().forEach(o -> o.onDungeonSettle(getScene()));
			getScene().getScriptManager().callEvent(EventType.EVENT_DUNGEON_SETTLE,
					new ScriptArgs(this.isSuccess() ? 1 : 0));
		}
	}

	private List<GameItem> rollRewards() {
		List<GameItem> rewards = new ArrayList<>();

		for (ItemParamData param : getScene().getDungeonData().getRewardPreview().getPreviewItems()) {
			rewards.add(new GameItem(param.getId(), Math.max(param.getCount(), 1)));
		}

		return rewards;
	}

	public void getStatueDrops(Player player, GadgetInteractReqOuterClass.GadgetInteractReq request) {
		DungeonData dungeonData = getScene().getDungeonData();
		int resinCost = dungeonData.getStatueCostCount() != 0 ? dungeonData.getStatueCostCount() : 20;

		if (!isSuccess() || dungeonData == null || dungeonData.getRewardPreview() == null || dungeonData.getRewardPreview().getPreviewItems().length == 0) {
			return;
		}

		// Already rewarded
		if (getRewardedPlayers().contains(player.getUid())) {
			return;
		}

		// Get rewards.
		List<GameItem> rewards = new ArrayList<>();

		if (request.getIsUseCondenseResin()) {
			// Check if condensed resin is usable here.
			// For this, we use the following logic for now:
			// The normal resin cost of the dungeon has to be 20.
			if (resinCost != 20) {
				return;
			}

			// Make sure the player has condensed resin.
			GameItem condensedResin = player.getInventory().getInventoryTab(ItemType.ITEM_MATERIAL).getItemById(220007);
			if (condensedResin == null || condensedResin.getCount() <= 0) {
				return;
			}

			// Deduct.
			player.getInventory().removeItem(condensedResin, 1);

			// Roll rewards, twice (because condensed).
			rewards.addAll(this.rollRewards());
			rewards.addAll(this.rollRewards());
		}
		else {
			// If the player used regular resin, try to deduct.
			// Stop if insufficient resin.
			boolean success = player.getResinManager().useResin(resinCost);
			if (!success) {
				return;
			}

			// Roll rewards.
			rewards.addAll(this.rollRewards());
		}

		// Add rewards to player and send notification.
		player.getInventory().addItems(rewards, ActionReason.DungeonStatueDrop);
		player.sendPacket(new PacketGadgetAutoPickDropInfoNotify(rewards));

		getRewardedPlayers().add(player.getUid());
	}

}
