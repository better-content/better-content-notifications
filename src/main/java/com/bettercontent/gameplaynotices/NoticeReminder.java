package com.bettercontent.gameplaynotices;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import net.minecraft.Util;

/** Client-thread-only, ephemeral reminder scheduling for actionable guidance. */
public final class NoticeReminder {
    private static final long REPEAT_MS = 60_000L;
    private static final long RETRY_MS = 5_000L;
    interface Transport {
        void submit(String key, GameplayNotice notice, BooleanSupplier valid, Runnable shown);
        void cancel(String key);
    }

    private final LongSupplier clock;
    private final Transport transport;
    private String key;
    private long nextAttempt;

    public NoticeReminder() {
        this(Util::getMillis, new Transport() {
            public void submit(String key, GameplayNotice notice, BooleanSupplier valid, Runnable shown) {
                NoticeClient.submitTransient(key, notice, valid, shown);
            }
            public void cancel(String key) { NoticeClient.cancelTransient(key); }
        });
    }

    NoticeReminder(LongSupplier clock, Transport transport) {
        this.clock = Objects.requireNonNull(clock);
        this.transport = Objects.requireNonNull(transport);
    }

    /** A null key or invalid predicate cancels. A changed key starts a new prompt immediately. */
    public void tick(String stateKey, GameplayNotice notice, BooleanSupplier valid, boolean canSubmit) {
        if (stateKey == null || !valid.getAsBoolean()) {
            clear();
            return;
        }
        if (!stateKey.equals(key)) {
            clear();
            key = stateKey;
            nextAttempt = 0;
        }
        long now = clock.getAsLong();
        if (!canSubmit || now < nextAttempt) return;
        nextAttempt = now + RETRY_MS;
        transport.submit(key, Objects.requireNonNull(notice), valid, () -> {
            if (stateKey.equals(key) && valid.getAsBoolean()) nextAttempt = clock.getAsLong() + REPEAT_MS;
        });
    }

    public void clear() {
        if (key != null) transport.cancel(key);
        key = null;
        nextAttempt = 0;
    }
}
