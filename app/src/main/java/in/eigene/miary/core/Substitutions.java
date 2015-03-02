package in.eigene.miary.core;

/**
 * Automatic substitutions helper.
 * https://github.com/eigenein/miary-android/issues/73
 */
@Deprecated
public class Substitutions {

    private static class TableEntry {

        public final String target;
        public final String replacement;

        public TableEntry(final String target, final String replacement) {
            this.target = target;
            this.replacement = replacement;
        }

        @Override
        public String toString() {
            return String.format("%s \u2192 %s", target, replacement);
        }
    }

    public static final String[] REPRS;

    /**
     * Replacement table.
     */
    private static final TableEntry[] TABLE = new TableEntry[] {
            new TableEntry("--", "\u2014"),
            new TableEntry(" - ", " \u2014 "),
            new TableEntry(">>", "\u00BB"),
            new TableEntry("<<", "\u00AB"),
            new TableEntry("...", "\u2026")
    };

    /**
     * Generates string representation of table entries.
     */
    static {
        REPRS = new String[TABLE.length];
        for (int i = 0; i < TABLE.length; i += 1) {
            REPRS[i] = TABLE[i].toString();
        }
    }

    public static String replace(final String string) {
        String result = string;
        for (final TableEntry entry : TABLE) {
            result = result.replace(entry.target, entry.replacement);
        }
        return result;
    }
}
