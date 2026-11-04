package com.bettercontent.gameplaynotices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

final class NoticeReminderTest {
    private static final class Transport implements NoticeReminder.Transport {
        int submitted;
        int cancelled;
        String key;
        Runnable shown;
        BooleanSupplier valid;
        public void submit(String key, GameplayNotice notice, BooleanSupplier valid, Runnable shown) {
            this.key = key;
            this.valid = valid;
            this.shown = shown;
            submitted++;
        }
        public void cancel(String key) {
            if (key.equals(this.key)) this.key = null;
            cancelled++;
        }
    }
    private static GameplayNotice notice(String key) {
        return new GameplayNotice(key, NoticeTheme.SPAWN, Component.literal("Title"), Component.literal("Hint"));
    }

    @Test
    void visiblePromptRepeatsOnlyOneMinuteAfterActuallyShown() {
        var time = new AtomicLong(1_000);
        var transport = new Transport();
        var reminder = new NoticeReminder(time::get, transport);
        var valid = new AtomicBoolean(true);
        reminder.tick("spawn", notice("spawn"), valid::get, false);
        assertEquals(0, transport.submitted);
        reminder.tick("spawn", notice("spawn"), valid::get, true);
        assertEquals(1, transport.submitted);
        time.set(4_999);
        transport.shown.run();
        time.set(64_998);
        reminder.tick("spawn", notice("spawn"), valid::get, true);
        assertEquals(1, transport.submitted);
        time.set(64_999);
        reminder.tick("spawn", notice("spawn"), valid::get, true);
        assertEquals(2, transport.submitted);
        valid.set(false);
        reminder.tick(null, null, valid::get, true);
        assertEquals(1, transport.cancelled);
        assertNull(transport.key);
    }

    @Test
    void unseenNoticeRetriesBoundedlyAndModeChangesInvalidateOldCallbacks() {
        var time = new AtomicLong(1_000);
        var transport = new Transport();
        var reminder = new NoticeReminder(time::get, transport);
        reminder.tick("spawn", notice("spawn"), () -> true, true);
        var oldShown = transport.shown;
        time.set(5_999);
        reminder.tick("spawn", notice("spawn"), () -> true, true);
        assertEquals(1, transport.submitted);
        time.set(6_000);
        reminder.tick("spawn", notice("spawn"), () -> true, true);
        assertEquals(2, transport.submitted);
        reminder.tick("class", notice("class"), () -> true, true);
        assertEquals(3, transport.submitted);
        assertEquals(1, transport.cancelled);
        oldShown.run();
        time.set(10_999);
        reminder.tick("class", notice("class"), () -> true, true);
        assertEquals(3, transport.submitted);
        time.set(11_000);
        reminder.tick("class", notice("class"), () -> true, true);
        assertEquals(4, transport.submitted);
        reminder.clear();
        assertEquals(2, transport.cancelled);
    }
}
