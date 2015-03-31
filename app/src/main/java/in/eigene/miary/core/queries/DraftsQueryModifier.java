package in.eigene.miary.core.queries;

import com.parse.*;
import in.eigene.miary.core.persistence.Note;

public class DraftsQueryModifier implements QueryModifier {

    public static final DraftsQueryModifier INSTANCE = new DraftsQueryModifier();

    @Override
    public ParseQuery apply(final ParseQuery query) {
        return query;
    }
}
