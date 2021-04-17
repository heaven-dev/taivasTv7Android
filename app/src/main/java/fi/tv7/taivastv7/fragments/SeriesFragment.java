package fi.tv7.taivastv7.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.VerticalGridView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import fi.tv7.taivastv7.BuildConfig;
import fi.tv7.taivastv7.R;
import fi.tv7.taivastv7.adapter.SeriesGridAdapter;
import fi.tv7.taivastv7.helpers.PageStateItem;
import fi.tv7.taivastv7.helpers.Sidebar;
import fi.tv7.taivastv7.helpers.Utils;
import fi.tv7.taivastv7.interfaces.ArchiveDataLoadedListener;
import fi.tv7.taivastv7.model.ArchiveViewModel;
import fi.tv7.taivastv7.model.SharedCacheViewModel;

import static fi.tv7.taivastv7.helpers.Constants.COLON_WITH_SPACE;
import static fi.tv7.taivastv7.helpers.Constants.ID;
import static fi.tv7.taivastv7.helpers.Constants.LOG_TAG;
import static fi.tv7.taivastv7.helpers.Constants.NO_MORE_PAGING_DATA;
import static fi.tv7.taivastv7.helpers.Constants.PROGRAM_INFO_FRAGMENT;
import static fi.tv7.taivastv7.helpers.Constants.PROGRAM_INFO_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_FRAGMENT;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_ID;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_NAME;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_PROGRAMS_SEARCH_LIMIT;
import static fi.tv7.taivastv7.helpers.Constants.SID;
import static fi.tv7.taivastv7.helpers.PageStateItem.DATA;
import static fi.tv7.taivastv7.helpers.PageStateItem.DATA_LENGTH;
import static fi.tv7.taivastv7.helpers.PageStateItem.OFFSET;
import static fi.tv7.taivastv7.helpers.PageStateItem.SELECTED_POS;

/**
 * Series fragment.
 */
public class SeriesFragment extends Fragment implements ArchiveDataLoadedListener {

    private View root = null;
    private ArchiveViewModel archiveViewModel = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private List<TextView> menuTexts = null;

    private int offset = 0;
    private int dataLength = 0;

    private JSONObject selectedProgram = null;

    private boolean loadingData = false;

    private SeriesGridAdapter seriesGridAdapter = null;
    private VerticalGridView seriesScroll = null;

    /**
     * Default constructor.
     */
    public SeriesFragment() {

    }

    /**
     * Creates and returns a new instance of this series fragment.
     * @return
     */
    public static SeriesFragment newInstance() {
        return new SeriesFragment();
    }

    /**
     * onCreate() - Android lifecycle method.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SeriesFragment.onCreate() called.");
            }

            archiveViewModel = ViewModelProviders.of(requireActivity()).get(ArchiveViewModel.class);
            sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SeriesFragment.onCreate(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
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
            root = inflater.inflate(R.layout.fragment_series, container, false);

            RelativeLayout contentContainer = root.findViewById(R.id.contentContainer);
            if (contentContainer != null) {
                Utils.fadePageAnimation(contentContainer);
            }

            menuTexts = Sidebar.getMenuTextItems(root);
            Sidebar.setSelectedMenuItem(root, R.id.archiveMenuContainer);

            dataLength = 0;
            offset = 0;

            selectedProgram = sharedCacheViewModel.getSelectedProgram();
            if (selectedProgram != null) {
                PageStateItem pageStateItem = sharedCacheViewModel.getSeriesPageStateItem();
                if (pageStateItem != null) {
                    Utils.showProgressBar(root, R.id.seriesProgress);

                    this.addElements((JSONArray)pageStateItem.getValue(DATA));
                    this.scrollToPosition((Integer)pageStateItem.getValue(SELECTED_POS));
                    dataLength = (Integer)pageStateItem.getValue(DATA_LENGTH);
                    offset = (Integer)pageStateItem.getValue(OFFSET);
                }
                else {
                    int seriesId = this.getSeriesId();
                    if (seriesId > 0) {
                        this.loadSeriesPrograms(seriesId);
                    }
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SeriesFragment.onCreateView(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
        return root;
    }

    /**
     * Creates grid and adds data to it.
     * @param jsonArray
     */
    private void addElements(JSONArray jsonArray) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SeriesFragment.addElements(): Archive data loaded. Data length: " + jsonArray.length());
            }

            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }

            if (dataLength == 0) {
                // first load
                this.addTitleText(jsonArray.getJSONObject(0));

                seriesScroll = root.findViewById(R.id.seriesScroll);
                seriesScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (seriesScroll != null) {
                            seriesScroll.invalidate();
                            seriesScroll.requestLayout();
                        }
                    }
                });

                seriesGridAdapter = new SeriesGridAdapter(getActivity(), getContext(), jsonArray);
                seriesScroll.setAdapter(seriesGridAdapter);
            }
            else {
                // next loads
                seriesGridAdapter.addElements(jsonArray);
                seriesGridAdapter.notifyDataSetChanged();
            }

            int length = jsonArray.length();
            dataLength += length;
            offset += length;

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SeriesFragment.addElements(): Items loaded: " + dataLength);
            }

            if (length < SERIES_PROGRAMS_SEARCH_LIMIT) {
                // no more data
                offset = NO_MORE_PAGING_DATA;
            }

            loadingData = false;

            Utils.hideProgressBar(root, R.id.seriesProgress);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SeriesFragment.addElements(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Date loaded callback method.
     * @param jsonArray
     * @param type
     */
    @Override
    public void onArchiveDataLoaded(JSONArray jsonArray, String type) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SeriesFragment.onArchiveDataLoaded(): Series programs data loaded. Type: " + type);
            }

            if (type.equals(PROGRAM_INFO_METHOD)) {
                Utils.hideProgressBar(root, R.id.seriesProgress);

                if (jsonArray != null && jsonArray.length() == 1) {
                    JSONObject obj = jsonArray.getJSONObject(0);
                    if (obj != null) {
                        sharedCacheViewModel.setSelectedProgram(obj);

                        Utils.toPage(PROGRAM_INFO_FRAGMENT, getActivity(), true, false, null);
                    }
                }
            }
            else {
                this.addElements(jsonArray);
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SeriesFragment.onArchiveDataLoaded(): Exception: " + e);
            }

            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Data loaded error callback method.
     * @param message
     * @param type
     */
    @Override
    public void onArchiveDataLoadError(String message, String type) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "Archive data load error. Type: " + type + " - Error message: " + message);
        }

        Utils.toErrorPage(getActivity());
    }

    /**
     * Archive data load network error response.
     * @param type
     */
    @Override
    public void onNetworkError(String type) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "Archive data load error. Type: " + type + " - ***Network error!***");
        }

        Utils.toErrorPage(getActivity());
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
                Log.d(LOG_TAG, "SeriesFragment.onKeyDown(): keyCode: " + keyCode);
            }

            if (seriesScroll == null || seriesGridAdapter == null) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SeriesFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuItemSelected(Sidebar.getFocusedMenuItem(root), getActivity(), sharedCacheViewModel);
                }
                else {
                    int pos = this.getSelectedPosition();

                    JSONObject obj = seriesGridAdapter.getElementByIndex(pos);
                    if (obj != null) {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "SeriesFragment.onKeyDown(): Selected element: " + obj.toString());
                        }

                        sharedCacheViewModel.setPageToHistory(SERIES_FRAGMENT);

                        sharedCacheViewModel.setSeriesPageStateItem(new PageStateItem(
                                seriesGridAdapter.getElements(),
                                pos,
                                dataLength,
                                offset));

                        this.loadProgramInfo(obj);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SeriesFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                if (!Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.showMenuTexts(menuTexts, root);
                    Sidebar.setFocusToMenu(root, R.id.archiveMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SeriesFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SeriesFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusDown(root, R.id.archiveMenuContainer);
                }
                else {
                    if (!loadingData) {
                        int pos = this.getSelectedPosition();

                        this.setSelectedPosition(++pos);

                        // paging
                        if (pos > 0 && offset != NO_MORE_PAGING_DATA && pos + SERIES_PROGRAMS_SEARCH_LIMIT / 2 == dataLength) {
                            loadingData = true;

                            int seriesId = this.getSeriesId();
                            if (seriesId > 0) {
                                this.loadSeriesPrograms(seriesId);
                            }
                        }
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SeriesFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusUp(root, R.id.archiveMenuContainer);
                }
                else {
                    int pos = this.getSelectedPosition();

                    this.setSelectedPosition(--pos);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SeriesFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    sharedCacheViewModel.resetSeriesPageStateItem();

                    String toPage = sharedCacheViewModel.getPageFromHistory();
                    if (toPage != null) {
                        Utils.toPage(toPage, getActivity(), true, false,null);
                    }
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SeriesFragment.onKeyDown(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }

        return true;
    }

    /**
     * Handles focus out from side menu.
     */
    private void focusOutFromSideMenu() {
        Sidebar.hideMenuTexts(menuTexts, root);
        Sidebar.setSelectedMenuItem(root, R.id.archiveMenuContainer);

        Utils.requestFocus(seriesScroll);
    }

    /**
     * Calls load series programs method.
     * @param seriesId
     */
    private void loadSeriesPrograms(int seriesId) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "SeriesFragment.loadSeriesPrograms(): Load more series programs data: " + seriesId);
        }

        Utils.showProgressBar(root, R.id.seriesProgress);
        archiveViewModel.getSeriesPrograms(seriesId, SERIES_PROGRAMS_SEARCH_LIMIT, offset, this);
    }

    /**
     * Calls get program info method.
     * @param obj
     * @throws Exception
     */
    private void loadProgramInfo(JSONObject obj) throws Exception {
        Utils.showProgressBar(root, R.id.seriesProgress);
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
        if (seriesScroll != null) {
            int pos = seriesScroll.getSelectedPosition();
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
        if (seriesScroll != null) {
            seriesScroll.setSelectedPositionSmooth(position);
        }
    }

    /**
     * Scrolls to given position on grid.
     * @param position
     */
    private void scrollToPosition(int position) {
        if (seriesScroll != null) {
            seriesScroll.scrollToPosition(position);
        }
    }

    /**
     * Adds page title text.
     * @param jsonObject
     * @throws Exception
     */
    private void addTitleText(JSONObject jsonObject) throws Exception {
        if (jsonObject != null) {
            String seriesName = Utils.getValue(jsonObject, SERIES_NAME);
            if (seriesName != null) {
                Resources resources = getResources();
                String text = resources.getString(R.string.series) + COLON_WITH_SPACE + seriesName;

                TextView seriesTitle = root.findViewById(R.id.seriesTitle);
                if (seriesTitle != null) {
                    seriesTitle.setText(text);
                }
            }
        }
    }

    /**
     * Returns series id from the item.
     * @return
     * @throws Exception
     */
    private int getSeriesId() throws Exception {
        int seriesId = 0;
        String seriesIdStr = Utils.getValue(selectedProgram, SID);
        if (seriesIdStr == null) {
            seriesIdStr = Utils.getValue(selectedProgram, SERIES_ID);
        }

        if (seriesIdStr != null) {
            seriesId = Utils.stringToInt(seriesIdStr);
        }
        return seriesId;
    }
}
