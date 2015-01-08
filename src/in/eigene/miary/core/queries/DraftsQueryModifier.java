package in.eigene.miary.core.queries;

import com.parse.*;
import in.eigene.miary.core.classes.*;

public class DraftsQueryModifier implements QueryModifier {

    public static final DraftsQueryModifier INSTANCE = new DraftsQueryModifier();

    @Override
    public ParseQuery apply(final ParseQuery query) {
        return query.whereEqualTo(Note.KEY_DRAFT, true);
    }
}
