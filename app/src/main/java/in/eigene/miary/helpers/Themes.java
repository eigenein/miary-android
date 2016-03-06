package in.eigene.miary.helpers;

import java.util.HashMap;

import in.eigene.miary.R;

public class Themes {

    private static final HashMap<String, Integer> THEMES = new HashMap<>();

    static {
        THEMES.put("Miary.Theme", R.style.Miary_Theme);
        THEMES.put("Miary.Theme.Dark", R.style.Miary_Theme_Dark);
    }

    public static int getThemeResourceId(final String themeName) {
        return THEMES.get(themeName);
    }
}
