package in.eigene.miary.core.queries;

import com.parse.*;
import in.eigene.miary.core.persistence.Note;

public class StarredQueryModifier implements QueryModifier {

    public static final StarredQueryModifier INSTANCE = new StarredQueryModifier();

    @Override
    public ParseQuery apply(final ParseQuery query) {
        return query;
    }
}
