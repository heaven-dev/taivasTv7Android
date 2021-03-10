package fi.tv7.taivastv7.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import fi.tv7.taivastv7.BuildConfig;
import fi.tv7.taivastv7.R;
import fi.tv7.taivastv7.TaivasTv7;
import fi.tv7.taivastv7.helpers.Utils;
import static fi.tv7.taivastv7.helpers.Constants.LOG_TAG;

/**
 * About fragment. Shows app error page.
 */
public class ErrorFragment extends Fragment {

    private View root = null;
    private TextView closeButton = null;

    /**
     * Default constructor.
     */
    public ErrorFragment() {

    }

    /**
     * Creates and returns a new instance of this error fragment.
     * @return
     */
    public static ErrorFragment newInstance() {
        return new ErrorFragment();
    }

    /**
     * onCreateView() - Android lifecycle method.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            root = inflater.inflate(R.layout.fragment_error, container, false);

            RelativeLayout errorContentContainer = root.findViewById(R.id.errorContentContainer);
            if (errorContentContainer != null) {
                Utils.fadePageAnimation(errorContentContainer);
            }

            boolean isConnected = TaivasTv7.getInstance().getConnectedToNet();
            if (!isConnected) {
                TextView tv = root.findViewById(R.id.errorText);
                if (tv != null) {
                    tv.setText(getText(R.string.no_network_connection));
                }
            }

            closeButton = root.findViewById(R.id.closeButton);
            if (closeButton != null) {
                closeButton.requestFocus();
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ErrorFragment.onCreateView(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
        return root;
    }

    /**
     * Handles key down events - remote control events.
     * @param keyCode
     * @param events
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent events) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): keyCode: " + keyCode);
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                View focusedView = root.findFocus();
                if (focusedView == closeButton) {
                    this.exitFromApplication();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }

        return true;
    }

    /**
     * Exits from application. User has selected 'Yes' button on exit fragment.
     */
    private void exitFromApplication() {
        getActivity().finishAffinity();
    }
}
