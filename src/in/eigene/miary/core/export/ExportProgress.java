package in.eigene.miary.core.export;

public class ExportProgress {

    private final int max;
    private int value;
    private String message;

    public ExportProgress(final int max, final int value, final String message) {
        this.max = max;
        this.value = value;
        this.message = message;
    }

    public int getMax() {
        return max;
    }

    public int getValue() {
        return value;
    }

    public void incrementValue(final int value) {
        this.value += value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
