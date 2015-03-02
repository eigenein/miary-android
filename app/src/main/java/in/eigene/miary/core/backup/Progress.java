package in.eigene.miary.core.backup;

public class Progress {

    public enum State {
        PROGRESS,
        FINISHING,
    }

    public static final Progress FINISHING = new Progress(
            State.FINISHING, 0);

    private final State state;
    private int progress;

    public Progress(final State state, final int progress) {
        this.state = state;
        this.progress = progress;
    }

    public State getState() {
        return state;
    }

    public int getProgress() {
        return progress;
    }

    public void incrementProgress() {
        progress += 1;
    }
}
