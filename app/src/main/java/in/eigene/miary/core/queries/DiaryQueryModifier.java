package in.eigene.miary.core.queries;

import com.parse.*;

import in.eigene.miary.core.persistence.Note;

public class DiaryQueryModifier implements QueryModifier {

    public static final DiaryQueryModifier INSTANCE = new DiaryQueryModifier();

    @Override
    public ParseQuery apply(final ParseQuery query) {
        return query;
    }
}
