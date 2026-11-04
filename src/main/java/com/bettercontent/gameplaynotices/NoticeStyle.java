package com.bettercontent.gameplaynotices;

/** Central presentation data; senders only choose a theme and supply text. */
final class NoticeStyle {
    record Definition(int accent, int secondary, int title, int hint, int outline,
                      float pitch, float volume, long durationMs, int[][] glyph) {}

    private static final int[][] THREAD = {{3, 4}, {2, 5}, {1, 3, 4, 6}, {0, 2, 5, 7},
            {0, 2, 5, 7}, {1, 3, 4, 6}, {2, 5}, {3, 4}};
    private static final int[][] COMBAT = {{0, 7}, {1, 6}, {2, 5}, {3, 4}, {3, 4}, {2, 5}, {1, 6}, {0, 7}};
    private static final int[][] TRACES = {{1, 2}, {0, 1, 2}, {0, 1, 2}, {1, 2, 5, 6},
            {5, 6, 7}, {5, 6, 7}, {1, 2, 5, 6}, {}};
    private static final int[][] SPAWN = {{3, 4}, {2, 3, 4, 5}, {1, 3, 4, 6}, {0, 1, 3, 4, 6, 7},
            {0, 1, 3, 4, 6, 7}, {1, 3, 4, 6}, {2, 3, 4, 5}, {3, 4}};
    private static final int[][] LIFE = {{1, 2, 5, 6}, {0, 3, 4, 7}, {0, 7}, {1, 6},
            {2, 5}, {3, 4}, {3, 4}, {}};

    private static final Definition[] DEFINITIONS = {
            new Definition(0xC6A15B, 0xC6A15B, 0xFFFFFF, 0xFFFFFF, 0x000000, 0.72f, 0.38f, 8_000L, THREAD),
            new Definition(0xC73E42, 0x2A080D, 0xF5B7B7, 0xE98185, 0x110307, 0.60f, 0.64f, 3_200L, COMBAT),
            new Definition(0xB8D4DD, 0xB8D4DD, 0xE4EFF2, 0xB8D4DD, 0x071014, 0.82f, 0.24f, 3_200L, TRACES),
            new Definition(0x71C7B3, 0x346D61, 0xD8FFF3, 0xAAEAD9, 0x071A17, 0.76f, 0.38f, 8_000L, SPAWN),
            new Definition(0xB699E8, 0x5C4189, 0xF4E8FF, 0xDEC9FF, 0x170C26, 0.86f, 0.38f, 8_000L, LIFE)
    };

    private NoticeStyle() {}

    static Definition forTheme(NoticeTheme theme) { return DEFINITIONS[theme.ordinal()]; }
}
