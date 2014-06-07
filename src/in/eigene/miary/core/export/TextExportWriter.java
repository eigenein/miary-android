package in.eigene.miary.core.export;

import in.eigene.miary.*;
import in.eigene.miary.core.*;

import java.io.*;

public class TextExportWriter extends ExportWriter {

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
            print(stream, note.getTitle());
        }
        print(stream, note.getCreationDate().toString());
        if (note.isDraft()) {
            print(stream, "[draft]");
        }
        if (note.isStarred()) {
            print(stream, "[starred]");
        }
        print(stream, note.getText());
        print(stream, "--");
    }
}
