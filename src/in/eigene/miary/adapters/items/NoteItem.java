package in.eigene.miary.adapters.items;

import in.eigene.miary.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.core.classes.*;

public class NoteItem extends FeedAdapter.Item {

    public final Note note;

    public NoteItem(final Note note) {
        super(R.layout.note_feed_item);
        this.note = note;
    }
}
