package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import emu.grasscutter.game.trigger.enums.Trigger;

@ResourceType(name = {"AchievementExcelConfigData.json"})
public class AchievementData extends GameResource {
    private int id;
    private ShowType isShow;
    private int finishRewardId;
    private AchievementTriggerConfig triggerConfig;
    private boolean isDeleteWatcherAfterFinish;
    private int progress;

    public int getId() {
        return id;
    }

    public ShowType getIsShow() {
        return isShow;
    }

    public int getFinishRewardId() {
        return finishRewardId;
    }

    public int getProgress() {
        return progress;
    }


    public AchievementTriggerConfig getTriggerConfig() {
        return triggerConfig;
    }

    public boolean getIsDeleteWatcherAfterFinish() {
        return isDeleteWatcherAfterFinish;
    }

    @Override
    public void onLoad() {

    }

    public static class AchievementTriggerConfig {
        Trigger triggerType;
        String[] paramList;

        public Trigger getTriggerType() {
            return triggerType;
        }

        public String[] getParamList() {
            return paramList;
        }
    }

    public enum ShowType{
        SHOWTYPE_SHOW,
        SHOWTYPE_HIDE
    }
}


