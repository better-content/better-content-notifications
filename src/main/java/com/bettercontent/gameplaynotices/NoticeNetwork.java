package com.bettercontent.gameplaynotices;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

final class NoticeNetwork {
    private static final String VERSION = "3";
    static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BetterGameplayNotices.MOD_ID, "notices"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    private NoticeNetwork() {}

    static void register() {
        CHANNEL.messageBuilder(GameplayNotice.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(NoticeCodec::encode)
                .decoder(NoticeCodec::decode)
                .consumerMainThread((notice, context) -> {
                    context.get().enqueueWork(() -> NoticeClient.receive(notice));
                    context.get().setPacketHandled(true);
                }).add();
    }

}
