package com.db.plantlyf.Utilities;

import android.content.Context;
import android.content.res.Configuration;

public class DarkModeStatus {

    public static boolean isDarkModeEnabled(Context activity) {

        int currentNightMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                return false;
        }

        return false;

    }

}
