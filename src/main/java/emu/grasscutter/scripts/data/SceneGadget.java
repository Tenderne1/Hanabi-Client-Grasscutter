package emu.grasscutter.scripts.data;

import emu.grasscutter.utils.Position;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
public class SceneGadget extends SceneObject{
	public int level;
	public int config_id;
	public int gadget_id;
	public int state;
	public Position pos;
	public Position rot;
	public int point_type;
	public SceneBossChest boss_chest;
	public int interact_id;
}