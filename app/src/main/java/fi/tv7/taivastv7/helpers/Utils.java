package fi.tv7.taivastv7.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import java.util.Locale;

import static fi.tv7.taivastv7.helpers.Constants.FADE_ANIMATION_DURATION;
import static fi.tv7.taivastv7.helpers.Constants.FADE_ANIMATION_END;
import static fi.tv7.taivastv7.helpers.Constants.FADE_ANIMATION_START;
import static fi.tv7.taivastv7.helpers.Constants.SHOW_ANIMATIONS;
import static fi.tv7.taivastv7.helpers.Constants.ZERO_STR;

/**
 * Util methods.
 */
public abstract class Utils {

    public static void showErrorToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void fadePageAnimation(ViewGroup viewGroup) {
        if (SHOW_ANIMATIONS) {
            viewGroup.startAnimation(createAnimation());
        }
    }

    public static String prependZero(int value) {
        if (value < 10) {
            return ZERO_STR + String.valueOf(value);
        }
        return String.valueOf(value);
    }

    private static Animation createAnimation() {
        Animation animation = new AlphaAnimation(FADE_ANIMATION_START, FADE_ANIMATION_END);
        animation.setDuration(FADE_ANIMATION_DURATION);
        return animation;
    }
}
