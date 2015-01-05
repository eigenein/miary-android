package in.eigene.miary.core.queries;

import com.parse.*;
import in.eigene.miary.core.classes.*;

public class DiaryQueryModifier implements QueryModifier {

    public static final DiaryQueryModifier INSTANCE = new DiaryQueryModifier();

    @Override
    public ParseQuery apply(final ParseQuery query) {
        return query.whereEqualTo(Note.KEY_DRAFT, false);
    }
}
