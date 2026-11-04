package com.bettercontent.gameplaynotices;

import net.minecraft.network.FriendlyByteBuf;

/** Wire codec kept independent of channel initialization for isolated verification. */
final class NoticeCodec {
    private NoticeCodec() {}

    static void encode(GameplayNotice notice, FriendlyByteBuf buffer) {
        buffer.writeUtf(notice.identity(), 160);
        buffer.writeEnum(notice.theme());
        buffer.writeComponent(notice.title());
        buffer.writeComponent(notice.hint());
        buffer.writeInt(notice.accentRgb());
    }

    static GameplayNotice decode(FriendlyByteBuf buffer) {
        return new GameplayNotice(buffer.readUtf(160), buffer.readEnum(NoticeTheme.class),
                buffer.readComponent(), buffer.readComponent(), buffer.readInt());
    }
}
