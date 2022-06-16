package emu.grasscutter.server.packet.send;

import com.google.protobuf.ByteString;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.GetPlayerTokenRspOuterClass;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.utils.Crypto;

public class PacketBannedRsp extends BasePacket {
    public PacketBannedRsp(GameSession session) {
        super(PacketOpcodes.GetPlayerTokenRsp, true);

        this.setUseDispatchKey(true);

        GetPlayerTokenRspOuterClass.GetPlayerTokenRsp p = GetPlayerTokenRspOuterClass.GetPlayerTokenRsp.newBuilder()
                .setUid(session.getPlayer().getUid())
                .setToken(session.getAccount().getToken())
                .setRetcode(21)
                .setAccountType(1)
                .setIsProficientPlayer(session.getPlayer().getAvatars().getAvatarCount() > 0) // Not sure where this goes
                .setSecretKeySeed(Crypto.ENCRYPT_SEED)
                .setSecurityCmdBuffer(ByteString.copyFrom(Crypto.ENCRYPT_SEED_BUFFER))
                .setPlatformType(3)
                .setChannelId(1)
                .setCountryCode("US")
                .setClientVersionRandomKey("c25-314dd05b0b5f")
                .setRegPlatform(3)
                .setClientIpStr(session.getAddress().getAddress().getHostAddress())
                .build();

        this.setData(p.toByteArray());
    }
}
