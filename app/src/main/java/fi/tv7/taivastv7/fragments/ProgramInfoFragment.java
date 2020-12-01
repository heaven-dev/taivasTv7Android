package fi.tv7.taivastv7.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import fi.tv7.taivastv7.BuildConfig;
import fi.tv7.taivastv7.R;
import fi.tv7.taivastv7.helpers.Sidebar;
import fi.tv7.taivastv7.helpers.Utils;
import fi.tv7.taivastv7.model.SharedCacheViewModel;

import static fi.tv7.taivastv7.helpers.Constants.ARCHIVE_PLAYER_FRAGMENT;
import static fi.tv7.taivastv7.helpers.Constants.BROADCAST_DATE_TIME;
import static fi.tv7.taivastv7.helpers.Constants.CAPTION;
import static fi.tv7.taivastv7.helpers.Constants.COLON_WITH_SPACE;
import static fi.tv7.taivastv7.helpers.Constants.DURATION;
import static fi.tv7.taivastv7.helpers.Constants.EPISODE_NUMBER;
import static fi.tv7.taivastv7.helpers.Constants.FAVORITES_TEXT_ANIMATION_DURATION;
import static fi.tv7.taivastv7.helpers.Constants.FAVORITES_TEXT_ANIMATION_END;
import static fi.tv7.taivastv7.helpers.Constants.FAVORITES_TEXT_ANIMATION_START;
import static fi.tv7.taivastv7.helpers.Constants.FAVORITES_TEXT_ANIMATION_START_OFFSET;
import static fi.tv7.taivastv7.helpers.Constants.ID;
import static fi.tv7.taivastv7.helpers.Constants.IMAGE_PATH;
import static fi.tv7.taivastv7.helpers.Constants.IS_VISIBLE_ON_VOD;
import static fi.tv7.taivastv7.helpers.Constants.LOG_TAG;
import static fi.tv7.taivastv7.helpers.Constants.ONE_STR;
import static fi.tv7.taivastv7.helpers.Constants.PROGRAM_INFO_FRAGMENT;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_AND_NAME;

/**
 * Program info fragment. Shows info of program and possible play video button.
 */
public class ProgramInfoFragment extends Fragment {

    private View root = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private List<TextView> menuTexts = null;

    private boolean videoAvailable = false;

    private ImageView startButton = null;
    private ImageView favoriteButton = null;

    private JSONObject selectedProgram = null;
    private int programFavoritesIndex = -1;

    /**
     * Default constructor.
     */
    public ProgramInfoFragment() {

    }

    /**
     * Creates and returns a new instance of this program info fragment.
     * @return
     */
    public static ProgramInfoFragment newInstance() {
        return new ProgramInfoFragment();
    }

    /**
     * onCreate() - Android lifecycle method.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ProgramInfoFragment.onCreate() called.");
        }

        sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);
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
            root = inflater.inflate(R.layout.fragment_program_info, container, false);

            RelativeLayout contentContainer = root.findViewById(R.id.contentContainer);
            if (contentContainer != null) {
                Utils.fadePageAnimation(contentContainer);
            }

            selectedProgram = sharedCacheViewModel.getSelectedProgram();
            if (selectedProgram == null) {
                throw new Exception("Program info page. Not selected program passed to this fragment!");
            }

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramInfoFragment.onCreateView(): Selected program: " + selectedProgram.getString(SERIES_AND_NAME));
            }

            menuTexts = Sidebar.getMenuTextItems(root);
            Sidebar.setSelectedMenuItem(root, R.id.archiveMenuContainer);

            ImageView backgroundImage = root.findViewById(R.id.backgroundImage);
            if (backgroundImage != null) {
                String imagePath = Utils.getValue(selectedProgram, IMAGE_PATH);
                if (imagePath != null) {
                    Glide.with(this).asBitmap().load(imagePath).into(backgroundImage);
                }
                else {
                    Glide.with(this).asBitmap().load(R.drawable.tv7_app_icon).into(backgroundImage);
                }
            }

            videoAvailable = Utils.getValue(selectedProgram, IS_VISIBLE_ON_VOD).equals(ONE_STR);

            startButton = root.findViewById(R.id.startButton);
            if (startButton != null) {
                if (videoAvailable) {
                    // play button available
                    Utils.requestFocus(startButton);
                }
                else {
                    startButton.setVisibility(View.GONE);
                }
            }

            favoriteButton = root.findViewById(R.id.favoriteButton);
            if (favoriteButton != null) {
                String programId = Utils.getValue(selectedProgram, ID);
                if (programId != null) {
                    if (!videoAvailable) {
                        Utils.requestFocus(favoriteButton);
                    }

                    programFavoritesIndex = Utils.isProgramInFavorites(getContext(), programId);

                    if (programFavoritesIndex != -1) {
                        this.setFavoritesButtonImage(R.drawable.favorites);
                    }
                }
            }

            Resources resources = getResources();
            if (resources != null) {
                String titleText;
                String valueText;

                TextView firstBroadcast = root.findViewById(R.id.firstBroadcast);
                if (firstBroadcast != null) {
                    valueText = Utils.getValue(selectedProgram, BROADCAST_DATE_TIME);
                    if (valueText != null && valueText.length() > 0) {
                        titleText = resources.getString(videoAvailable ? R.string.first_broadcast : R.string.coming_on_channel);
                        valueText = titleText + COLON_WITH_SPACE + valueText;

                        firstBroadcast.setText(valueText);
                    }
                    else {
                        firstBroadcast.setVisibility(View.GONE);
                    }
                }

                TextView duration = root.findViewById(R.id.duration);
                if (duration != null) {
                    valueText = Utils.getValue(selectedProgram, DURATION);
                    if (valueText != null && valueText.length() > 0) {
                        titleText = resources.getString(R.string.duration);
                        valueText = titleText + COLON_WITH_SPACE + valueText;

                        duration.setText(valueText);
                    }
                    else {
                        duration.setVisibility(View.GONE);
                    }
                }

                TextView episodeNbr = root.findViewById(R.id.episode);
                if (episodeNbr != null) {
                    valueText = Utils.getValue(selectedProgram, EPISODE_NUMBER);
                    if (valueText != null && valueText.length() > 0) {
                        titleText = resources.getString(R.string.episode);
                        valueText = titleText + COLON_WITH_SPACE + valueText;

                        episodeNbr.setText(valueText);
                    }
                    else {
                        episodeNbr.setVisibility(View.GONE);
                    }
                }

                TextView caption = root.findViewById(R.id.caption);
                if (caption != null) {
                    valueText = Utils.getValue(selectedProgram, CAPTION);
                    if (valueText != null && valueText.length() > 0) {
                        caption.setText(valueText);
                    }
                    else {
                        caption.setVisibility(View.GONE);
                    }
                }

                TextView seriesAndName = root.findViewById(R.id.seriesAndName);
                if (seriesAndName != null) {
                    valueText = Utils.getValue(selectedProgram, SERIES_AND_NAME);
                    if (valueText != null) {
                        seriesAndName.setText(valueText);
                    }
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramInfoFragment.onCreateView(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }
        return root;
    }

    /**
     * Handles keydown events - remote control events.
     * @param keyCode
     * @param events
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent events) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): keyCode: " + keyCode);
            }

            View focusedView = Utils.getFocusedView(getActivity());
            if (focusedView == null) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuItemSelected(Sidebar.getFocusedMenuItem(root), getActivity(), sharedCacheViewModel);
                }
                else {
                    int focusedId = focusedView.getId();

                    if (focusedId == R.id.startButton) {
                        sharedCacheViewModel.setPageToHistory(PROGRAM_INFO_FRAGMENT);
                        Utils.toPage(ARCHIVE_PLAYER_FRAGMENT, getActivity(), true, false, null);
                    }
                    else if (focusedId == R.id.favoriteButton) {
                        if (programFavoritesIndex == -1) {
                            this.addToSavedFavorites();
                            this.setFavoritesButtonImage(R.drawable.favorites);
                            this.setFavoritesPopupText(R.string.added_to_favorites);
                        }
                        else {
                            this.removeFromSavedFavorites();
                            this.setFavoritesButtonImage(R.drawable.favorites_not_selected);
                            this.setFavoritesPopupText(R.string.removed_from_favorites);
                        }
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                int focusedId = focusedView.getId();

                if (focusedId == R.id.favoriteButton && videoAvailable) {
                    Utils.requestFocus(startButton);
                }
                else if (focusedId == R.id.startButton || focusedId == R.id.favoriteButton && !videoAvailable) {
                    Sidebar.showMenuTexts(menuTexts);
                    Sidebar.setFocusToMenu(root, R.id.archiveMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                int focusedId = focusedView.getId();

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else if (focusedId == R.id.startButton) {
                    Utils.requestFocus(favoriteButton);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusDown(root, R.id.archiveMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusUp(root, R.id.archiveMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    String toPage = sharedCacheViewModel.getPageFromHistory();
                    if (toPage != null) {
                        Utils.toPage(toPage, getActivity(), true, false,null);
                    }
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }

        return true;
    }

    /**
     * Handles focus out from side menu.
     */
    private void focusOutFromSideMenu() {
        Sidebar.hideMenuTexts(menuTexts);
        Sidebar.setSelectedMenuItem(root, R.id.archiveMenuContainer);

        if (videoAvailable) {
            Utils.requestFocus(startButton);
        }
        else {
            Utils.requestFocus(favoriteButton);
        }
    }

    /**
     * Adds program to favorites. Saves to shared prefs.
     * @throws Exception
     */
    private void addToSavedFavorites() throws Exception {
        JSONArray jsonArray = Utils.getSavedFavorites(getContext());
        if (jsonArray != null) {
            jsonArray.put(selectedProgram);

            Utils.saveFavorites(getContext(), jsonArray);

            programFavoritesIndex = jsonArray.length() - 1;
        }
    }

    /**
     * Removes program from favorites. Saves to shared prefs.
     * @throws Exception
     */
    private void removeFromSavedFavorites() throws Exception {
        JSONArray jsonArray = Utils.getSavedFavorites(getContext());
        if (jsonArray != null && programFavoritesIndex != -1) {
            jsonArray.remove(programFavoritesIndex);

            Utils.saveFavorites(getContext(), jsonArray);

            programFavoritesIndex = - 1;
        }
    }

    /**
     * Sets favorite button image.
     * @param drawableId
     */
    private void setFavoritesButtonImage(int drawableId) {
        favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), drawableId, null));
    }

    /**
     * Sets favorite added/removed popup text and start fade out animation.
     * @param textId
     */
    private void setFavoritesPopupText(int textId) {
        TextView addedRemovedFavorites = root.findViewById(R.id.addedRemovedFavorites);
        if (addedRemovedFavorites != null) {
            addedRemovedFavorites.setText(textId);
            addedRemovedFavorites.setVisibility(View.VISIBLE);

            Animation animation = new AlphaAnimation(FAVORITES_TEXT_ANIMATION_START, FAVORITES_TEXT_ANIMATION_END);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setStartOffset(FAVORITES_TEXT_ANIMATION_START_OFFSET);
            animation.setDuration(FAVORITES_TEXT_ANIMATION_DURATION);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    addedRemovedFavorites.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            addedRemovedFavorites.startAnimation(animation);
        }
    }
}
