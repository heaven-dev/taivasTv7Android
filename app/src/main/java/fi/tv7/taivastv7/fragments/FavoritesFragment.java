package fi.tv7.taivastv7.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.leanback.widget.VerticalGridView;
import androidx.lifecycle.ViewModelProviders;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import fi.tv7.taivastv7.BuildConfig;
import fi.tv7.taivastv7.R;
import fi.tv7.taivastv7.adapter.FavoritesGridAdapter;
import fi.tv7.taivastv7.helpers.PageStateItem;
import fi.tv7.taivastv7.helpers.Sidebar;
import fi.tv7.taivastv7.helpers.Utils;
import fi.tv7.taivastv7.interfaces.ArchiveDataLoadedListener;
import fi.tv7.taivastv7.model.ArchiveViewModel;
import fi.tv7.taivastv7.model.SharedCacheViewModel;

import static fi.tv7.taivastv7.helpers.Constants.ARCHIVE_MAIN_FRAGMENT;
import static fi.tv7.taivastv7.helpers.Constants.FAVORITES_FRAGMENT;
import static fi.tv7.taivastv7.helpers.Constants.FAVORITES_SP_DEFAULT;
import static fi.tv7.taivastv7.helpers.Constants.FAVORITES_SP_TAG;
import static fi.tv7.taivastv7.helpers.Constants.ID;
import static fi.tv7.taivastv7.helpers.Constants.LOG_TAG;
import static fi.tv7.taivastv7.helpers.Constants.PROGRAM_INFO_FRAGMENT;
import static fi.tv7.taivastv7.helpers.Constants.PROGRAM_INFO_METHOD;
import static fi.tv7.taivastv7.helpers.PageStateItem.SELECTED_POS;

/**
 * Favorites fragment. Shows info of program and possible play video button.
 */
public class FavoritesFragment extends Fragment implements ArchiveDataLoadedListener {

    private View root = null;
    private ArchiveViewModel archiveViewModel = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private FavoritesGridAdapter favoritesGridAdapter = null;
    private VerticalGridView favoritesScroll = null;

    private int hitCount = 0;

    private List<TextView> menuTexts = null;

    /**
     * Default constructor.
     */
    public FavoritesFragment() {

    }

    /**
     * Creates and returns a new instance of this favorites fragment.
     * @return
     */
    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    /**
     * onCreate() - Android lifecycle method.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "FavoritesFragment.onCreate() called.");
        }

        archiveViewModel = ViewModelProviders.of(requireActivity()).get(ArchiveViewModel.class);
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
            root = inflater.inflate(R.layout.fragment_favorites, container, false);

            RelativeLayout contentContainer = root.findViewById(R.id.contentContainer);
            if (contentContainer != null) {
                Utils.fadePageAnimation(contentContainer);
            }

            menuTexts = Sidebar.getMenuTextItems(root);
            Sidebar.setSelectedMenuItem(root, R.id.favoritesMenuContainer);

            JSONArray jsonArray = Utils.getSavedPrefs(FAVORITES_SP_TAG, FAVORITES_SP_DEFAULT, getContext());
            if (jsonArray != null) {
                PageStateItem pageStateItem = sharedCacheViewModel.getFavoritesPageStateItem();
                if (pageStateItem != null) {
                    Utils.showProgressBar(root, R.id.favoritesProgress);

                    this.addElements(jsonArray);
                    this.scrollToPosition((Integer)pageStateItem.getValue(SELECTED_POS));
                }
                else {
                    this.addElements(jsonArray);
                }
            }

            if (jsonArray == null || jsonArray.length() == 0) {
                TextView noFavorites = root.findViewById(R.id.noFavorites);
                if (noFavorites != null) {
                    noFavorites.setVisibility(View.VISIBLE);
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "FavoritesFragment.onCreateView(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }
        return root;
    }

    /**
     * Creates grid and add data to it.
     * @param jsonArray
     */
    private void addElements(JSONArray jsonArray) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "FavoritesFragment.addElements(): Archive data loaded. Data length: " + jsonArray.length());
            }

            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }

            hitCount = jsonArray.length();

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "FavoritesFragment.addElements(): Favorites item count: " + hitCount);
            }

            favoritesScroll = root.findViewById(R.id.favoritesScroll);
            favoritesGridAdapter = new FavoritesGridAdapter(getContext(), jsonArray);
            favoritesScroll.setAdapter(favoritesGridAdapter);

            if (jsonArray.length() == 0) {
                Utils.requestFocusById(root, R.id.favoritesTitle);

                TextView noFavorites = root.findViewById(R.id.noFavorites);
                if (noFavorites != null) {
                    noFavorites.setVisibility(View.VISIBLE);
                }
            }

            Utils.hideProgressBar(root, R.id.favoritesProgress);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "FavoritesFragment.addElements(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }
    }

    /**
     * Data loaded callback method.
     * @param jsonArray
     * @param type
     */
    @Override
    public void onArchiveDataLoaded(JSONArray jsonArray, String type) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "FavoritesFragment.onArchiveDataLoaded(): Archive data loaded. Type: " + type);
            }

            if (type.equals(PROGRAM_INFO_METHOD)) {
                Utils.hideProgressBar(root, R.id.favoritesProgress);

                if (jsonArray != null && jsonArray.length() == 1) {
                    JSONObject obj = jsonArray.getJSONObject(0);
                    if (obj != null) {
                        sharedCacheViewModel.setSelectedProgram(obj);

                        Utils.toPage(PROGRAM_INFO_FRAGMENT, getActivity(), true, false, null);
                    }
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "FavoritesFragment.onArchiveDataLoaded(): Exception: " + e);
            }

            Context context = getContext();
            if (context != null) {
                String message = context.getString(R.string.toast_something_went_wrong);
                Utils.showErrorToast(context, message);
            }
        }
    }

    /**
     * Data error loaded method.
     * @param message
     * @param type
     */
    @Override
    public void onArchiveDataLoadError(String message, String type) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "Archive data load error. Type: " + type + " - Error message: " + message);
            }

            Context context = getContext();
            if (context != null) {
                message = context.getString(R.string.toast_something_went_wrong);

                Utils.showErrorToast(context, message);
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "FavoritesFragment.onArchiveDataLoadError(): Exception: " + e);
            }
        }
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
                Log.d(LOG_TAG, "FavoritesFragment.onKeyDown(): keyCode: " + keyCode);
            }

            if (favoritesScroll == null || favoritesGridAdapter == null) {
                return false;
            }

            View focusedView = Utils.getFocusedView(getActivity());
            if (focusedView == null) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "FavoritesFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    int focusedMenu = Sidebar.getFocusedMenuItem(root);
                    if (focusedMenu == R.id.favoritesMenuContainer) {
                        this.focusOutFromSideMenu();
                    }
                    else {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "FavoritesFragment.onKeyDown(): Selected sidebar menu: " + focusedMenu);
                        }

                        Sidebar.menuItemSelected(focusedMenu, getActivity(), sharedCacheViewModel);
                    }
                }
                else {
                    if (hitCount == 0) {
                        return false;
                    }

                    sharedCacheViewModel.setPageToHistory(FAVORITES_FRAGMENT);

                    int pos = this.getSelectedPosition();

                    JSONObject obj = favoritesGridAdapter.getElementByIndex(pos);
                    if (obj != null) {
                        sharedCacheViewModel.setFavoritesPageStateItem(new PageStateItem(
                                null,
                                pos));

                        this.loadProgramInfo(obj);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "FavoritesFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                int focusedId = focusedView.getId();

                if (focusedId == R.id.favoriteContainer || focusedId == R.id.favoritesTitle) {
                    Sidebar.showMenuTexts(menuTexts, root);
                    Sidebar.setFocusToMenu(root, R.id.favoritesMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "FavoritesFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "FavoritesFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusDown(root, R.id.favoritesMenuContainer);
                }
                else {
                    int pos = this.getSelectedPosition();

                    this.setSelectedPosition(++pos);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "FavoritesFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusUp(root, R.id.favoritesMenuContainer);
                }
                else {
                    int pos = this.getSelectedPosition();

                    this.setSelectedPosition(--pos);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "FavoritesFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    sharedCacheViewModel.resetFavoritesPageStateItem();

                    Utils.toPage(ARCHIVE_MAIN_FRAGMENT, getActivity(), true, false,null);
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "FavoritesFragment.onKeyDown(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }

        return true;
    }

    /**
     * Handles focus out from side menu.
     */
    private void focusOutFromSideMenu() {
        Sidebar.hideMenuTexts(menuTexts, root);
        Sidebar.setSelectedMenuItem(root, R.id.favoritesMenuContainer);

        if (hitCount > 0)  {
            Utils.requestFocusById(root, R.id.favoritesScroll);
        }
        else {
            Utils.requestFocusById(root, R.id.favoritesTitle);
        }
    }

    /**
     * Calls get program info method.
     * @param obj
     * @throws Exception
     */
    private void loadProgramInfo(JSONObject obj) throws Exception {
        Utils.showProgressBar(root, R.id.favoritesProgress);

        String programId = Utils.getValue(obj, ID);
        if (programId != null) {
            archiveViewModel.getProgramInfo(programId, this);
        }
    }

    /**
     * Returns selected position from grid.
     * @return
     */
    private int getSelectedPosition() {
        if (favoritesScroll != null) {
            int pos = favoritesScroll.getSelectedPosition();
            if (pos < 0) {
                pos = 0;
            }
            return pos;
        }
        return 0;
    }

    /**
     * Sets selected position to grid.
     * @param position
     */
    private void setSelectedPosition(int position) {
        if (favoritesScroll != null) {
            favoritesScroll.setSelectedPositionSmooth(position);
        }
    }

    /**
     * Scrolls to given position on grid.
     * @param position
     */
    private void scrollToPosition(int position) {
        if (favoritesScroll != null) {
            favoritesScroll.scrollToPosition(position);
        }
    }
}
