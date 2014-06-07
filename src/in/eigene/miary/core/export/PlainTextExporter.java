package in.eigene.miary.core.export;

import in.eigene.miary.*;
import in.eigene.miary.core.*;

import java.io.*;

public class PlainTextExporter extends Exporter {

    @Override
    public int getWriterTitle() {
        return R.string.export_writer_title_txt;
    }

    @Override
    public String getExtension() {
        return "txt";
    }

    @Override
    public void putNote(final OutputStream stream, final Note note) {
        if (!note.getTitle().isEmpty()) {
            print(stream, "Title: " + note.getTitle());
        }
        print(stream, "Date: " + note.getCreationDate());
        if (note.isDraft()) {
            print(stream, "Tag: draft");
        }
        if (note.isStarred()) {
            print(stream, "Tag: starred");
        }
        if (!note.getText().isEmpty()) {
            print(stream, note.getText());
        }
        print(stream, "--");
    }
}
