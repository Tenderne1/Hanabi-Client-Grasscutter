package emu.grasscutter.data.binout;

import emu.grasscutter.utils.Position;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SceneNpcBornEntry {
    int _id;
    int _configId;
    Position _pos;
    Position _rot;
    int _groupId;
    List<Integer> _suiteIdList;
}
