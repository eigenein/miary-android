package in.eigene.miary.core;

/**
 * Automatic substitutions helper.
 */
public class Substitutions {

    public static class TableEntry {
        public final String regularExpression;
        public final String replacement;

        public TableEntry(final String regularExpression, final String replacement) {
            this.regularExpression = regularExpression;
            this.replacement = replacement;
        }

        @Override
        public String toString() {
            return String.format("%s → %s", regularExpression, replacement);
        }
    }

    public static final String[] REPRS;

    /**
     * Replacement table.
     */
    private static final TableEntry[] TABLE = new TableEntry[] {
            new TableEntry("--", "—"),
            new TableEntry(" - ", "—"),
            new TableEntry(">>", "»"),
            new TableEntry("<<", "«"),
            new TableEntry("...", "…")
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
            result = result.replace(entry.regularExpression, entry.replacement);
        }
        return result;
    }
}
