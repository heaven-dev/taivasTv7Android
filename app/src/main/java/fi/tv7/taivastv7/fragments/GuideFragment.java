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
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.VerticalGridView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fi.tv7.taivastv7.BuildConfig;
import fi.tv7.taivastv7.R;
import fi.tv7.taivastv7.adapter.GuideGridAdapter;
import fi.tv7.taivastv7.helpers.GuideDate;
import fi.tv7.taivastv7.helpers.PageStateItem;
import fi.tv7.taivastv7.helpers.Sidebar;
import fi.tv7.taivastv7.helpers.Utils;
import fi.tv7.taivastv7.interfaces.ArchiveDataLoadedListener;
import fi.tv7.taivastv7.model.ArchiveViewModel;
import fi.tv7.taivastv7.model.SharedCacheViewModel;

import static fi.tv7.taivastv7.helpers.Constants.ARCHIVE_MAIN_FRAGMENT;
import static fi.tv7.taivastv7.helpers.Constants.DATES_COUNT;
import static fi.tv7.taivastv7.helpers.Constants.DATE_INDEX;
import static fi.tv7.taivastv7.helpers.Constants.GUIDE_DATA;
import static fi.tv7.taivastv7.helpers.Constants.GUIDE_DATE_IDS;
import static fi.tv7.taivastv7.helpers.Constants.GUIDE_FRAGMENT;
import static fi.tv7.taivastv7.helpers.Constants.ID;
import static fi.tv7.taivastv7.helpers.Constants.LOG_TAG;
import static fi.tv7.taivastv7.helpers.Constants.ONGOING_PROGRAM_INDEX;
import static fi.tv7.taivastv7.helpers.Constants.PROGRAM_INFO_FRAGMENT;
import static fi.tv7.taivastv7.helpers.Constants.PROGRAM_INFO_METHOD;
import static fi.tv7.taivastv7.helpers.PageStateItem.DATA;
import static fi.tv7.taivastv7.helpers.PageStateItem.SELECTED_DATE_ID;
import static fi.tv7.taivastv7.helpers.PageStateItem.SELECTED_POS;

/**
 * Guide fragment.
 */
public class GuideFragment extends Fragment implements ArchiveDataLoadedListener {

    private View root = null;
    private ArchiveViewModel archiveViewModel = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private List<TextView> menuTexts = null;

    private VerticalGridView guideScroll = null;
    private GuideGridAdapter guideGridAdapter = null;

    private List<GuideDate> dates = new ArrayList<>();

    private int selectedDateId = R.id.date_0;
    private int toSideMenuItemId = R.id.date_0;
    private int ongoingProgramIndex = 0;

    /**
     * Default constructor.
     */
    public GuideFragment() {

    }

    /**
     * Creates and returns a new instance of this guide fragment.
     * @return
     */
    public static GuideFragment newInstance() {
        return new GuideFragment();
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
                Log.d(LOG_TAG, "GuideFragment.onCreate() called.");
            }

            archiveViewModel = ViewModelProviders.of(requireActivity()).get(ArchiveViewModel.class);
            sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideFragment.onCreate(): Exception: " + e);
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
            root = inflater.inflate(R.layout.fragment_guide, container, false);

            RelativeLayout contentContainer = root.findViewById(R.id.contentContainer);
            if (contentContainer != null) {
                Utils.fadePageAnimation(contentContainer);
            }

            menuTexts = Sidebar.getMenuTextItems(root);
            Sidebar.setSelectedMenuItem(root, R.id.guideMenuContainer);

            this.createDates();

            for (int i = 0; i  < GUIDE_DATE_IDS.size(); i++) {
                int id = GUIDE_DATE_IDS.get(i);

                TextView dateItem = root.findViewById(id);
                if (dateItem != null) {
                    GuideDate gd = dates.get(i);
                    dateItem.setText(gd.getLabel());
                }
            }

            PageStateItem pageStateItem = sharedCacheViewModel.getGuidePageStateItem();
            if (pageStateItem != null) {
                Utils.showProgressBar(root, R.id.guideProgress);

                selectedDateId = (Integer)pageStateItem.getValue(SELECTED_DATE_ID);
                this.addElements((JSONArray)pageStateItem.getValue(DATA), false);
                this.scrollToPosition((Integer)pageStateItem.getValue(SELECTED_POS));
            }
            else {
                this.loadGuideByDate(Utils.getTodayUtcFormattedLocalDate(), 0);
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideFragment.onCreateView(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
        return root;
    }

    /**
     * Creates grid and adds data to it.
     * @param jsonArray
     */
    private void addElements(JSONArray jsonArray, boolean isPageLoad) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideFragment.addElements(): Guide data loaded. Data length: " + jsonArray.length());
            }

            if (jsonArray != null && jsonArray.length() > 0) {
                JSONObject obj = jsonArray.getJSONObject(0);

                JSONArray guideData = obj.getJSONArray(GUIDE_DATA);
                ongoingProgramIndex = obj.getInt(ONGOING_PROGRAM_INDEX);

                guideScroll = root.findViewById(R.id.guideScroll);
                guideScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (guideScroll != null) {
                            guideScroll.invalidate();
                            guideScroll.requestLayout();
                        }
                    }
                });

                guideGridAdapter = new GuideGridAdapter(getActivity(), getContext(), getResources(), guideData);
                guideScroll.setAdapter(guideGridAdapter);

                if (isPageLoad && obj.getInt(DATE_INDEX) == 0) {
                    this.scrollToPosition(ongoingProgramIndex);
                }

                int length = guideData.length();

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "GuideFragment.addElements(): Items loaded: " + length);
                }

                this.setDateSelection();

                if (isPageLoad) {
                    Utils.requestFocusById(root, selectedDateId);
                }
                else {
                    Utils.requestFocus(guideScroll);
                }
            }

            Utils.hideProgressBar(root, R.id.guideProgress);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideFragment.addElements(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
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
                Log.d(LOG_TAG, "GuideFragment.onArchiveDataLoaded(): Archive data loaded. Type: " + type);
            }

            if (type.equals(PROGRAM_INFO_METHOD)) {
                Utils.hideProgressBar(root, R.id.guideProgress);

                if (jsonArray != null && jsonArray.length() == 1) {
                    JSONObject obj = jsonArray.getJSONObject(0);
                    if (obj != null) {
                        sharedCacheViewModel.setSelectedProgram(obj);

                        Utils.toPage(PROGRAM_INFO_FRAGMENT, getActivity(), true, false, null);
                    }
                }
            }
            else {
                this.addElements(jsonArray, true);
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideFragment.onArchiveDataLoaded(): Exception: " + e);
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
     * Handles keydown events - remote control events.
     * @param keyCode
     * @param events
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent events) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideFragment.onKeyDown(): keyCode: " + keyCode);
            }

            if (guideScroll == null || guideGridAdapter == null) {
                return false;
            }

            if (Sidebar.isSideMenuOpen(menuTexts) && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                return false;
            }

            View focusedDate = this.isDateFocused();

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "GuideFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    int focusedMenu = Sidebar.getFocusedMenuItem(root);
                    if (focusedMenu == R.id.guideMenuContainer) {
                        this.focusOutFromSideMenu();
                    }
                    else {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "GuideFragment.onKeyDown(): Selected sidebar menu: " + focusedMenu);
                        }

                        Sidebar.menuItemSelected(focusedMenu, getActivity(), sharedCacheViewModel);
                    }
                }
                else if (focusedDate != null) {
                    int index = GUIDE_DATE_IDS.indexOf(focusedDate.getId());
                    if (index != -1) {
                        GuideDate gd = dates.get(index);
                        if (gd != null) {
                            selectedDateId = GUIDE_DATE_IDS.get(index);
                            this.loadGuideByDate(gd.getDate(), index);
                        }
                    }
                }
                else {
                    int pos = this.getSelectedPosition();

                    JSONObject obj = guideGridAdapter.getElementByIndex(pos);
                    if (obj != null) {
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put(ONGOING_PROGRAM_INDEX, ongoingProgramIndex);
                        jsonObj.put(GUIDE_DATA, guideGridAdapter.getElements());

                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(jsonObj);

                        sharedCacheViewModel.setGuidePageStateItem(new PageStateItem(
                                jsonArray,
                                pos,
                                selectedDateId));

                        sharedCacheViewModel.setPageToHistory(GUIDE_FRAGMENT);

                        this.loadProgramInfo(obj);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "GuideFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                toSideMenuItemId = Utils.getFocusedView(getActivity()).getId();

                if (focusedDate != null) {
                    int id = focusedDate.getId();
                    int previousId = this.getPreviousDateItemId(id);
                    if (id > previousId) {
                        Utils.requestFocusById(root, previousId);
                    }
                    else {
                        Sidebar.showMenuTexts(menuTexts, root);
                        Sidebar.setFocusToMenu(root, R.id.guideMenuContainer);
                    }
                }
                else {
                    Sidebar.showMenuTexts(menuTexts, root);
                    Sidebar.setFocusToMenu(root, R.id.guideMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "GuideFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    if (focusedDate != null) {
                        int id = focusedDate.getId();
                        int nextId = this.getNextDateItemId(id);
                        if (nextId > id) {
                            Utils.requestFocusById(root, nextId);
                        }
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "GuideFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusDown(root, R.id.guideMenuContainer);
                }
                else {
                    if (focusedDate != null) {
                        Utils.requestFocusById(root, R.id.guideScroll);
                    }
                    else {
                        int pos = this.getSelectedPosition();
                        this.setSelectedPosition(++pos);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "GuideFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusUp(root, R.id.guideMenuContainer);
                }
                else {
                    int pos = this.getSelectedPosition();

                    if (focusedDate == null && pos == 0 || ongoingProgramIndex == pos) {
                        Utils.requestFocusById(root, selectedDateId);
                    }
                    else {
                        this.setSelectedPosition(--pos);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "GuideFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    sharedCacheViewModel.resetGuidePageStateItem();

                    Utils.toPage(ARCHIVE_MAIN_FRAGMENT, getActivity(), true, false,null);
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideFragment.onKeyDown(): Exception: " + e);
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
        Sidebar.setSelectedMenuItem(root, R.id.guideMenuContainer);

        int index = GUIDE_DATE_IDS.indexOf(toSideMenuItemId);
        if (index != -1) {
            Utils.requestFocusById(root, toSideMenuItemId);
        }
        else {
            Utils.requestFocus(guideScroll);
        }
    }

    /**
     * Calls load guide by date method.
     * @param date
     */
    private void loadGuideByDate(String date, Integer dateIndex) {
        Utils.showProgressBar(root, R.id.guideProgress);
        archiveViewModel.getGuideByDate(date, dateIndex, this);
    }

    /**
     * Calls get program info method.
     * @param obj
     * @throws Exception
     */
    private void loadProgramInfo(JSONObject obj) throws Exception {
        Utils.showProgressBar(root, R.id.guideProgress);
        String programId = Utils.getJsonStringValue(obj, ID);
        if (programId != null) {
            archiveViewModel.getProgramInfo(programId, this);
        }
    }

    /**
     * Returns selected position from grid.
     * @return
     */
    private int getSelectedPosition() {
        if (guideScroll != null) {
            int pos = guideScroll.getSelectedPosition();
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
        if (guideScroll != null) {
            guideScroll.setSelectedPositionSmooth(position);
        }
    }

    /**
     * Scrolls to given position on grid.
     * @param position
     */
    private void scrollToPosition(int position) {
        if (guideScroll != null) {
            guideScroll.scrollToPosition(position);
        }
    }

    /**
     * Create dates (label, date used in guide search).
     */
    private void createDates() {
        Calendar calendar = Utils.getLocalCalendar();
        calendar.setTime(new Date());

        for(int i = 0; i < DATES_COUNT; i++) {
            String date = Utils.getDateByCalendar(calendar);
            String label = i == 0 ? getString(R.string.today) : Utils.getLocalDateByCalendar(calendar);
            dates.add(new GuideDate(date, label));

            calendar.add(Calendar.DATE, 1);
        }
    }

    /**
     * Is date item focused or not.
     * @return
     */
    private View isDateFocused() {
        View view = Utils.getFocusedView(getActivity());
        if (view != null) {
            if (GUIDE_DATE_IDS.contains(view.getId())) {
                return view;
            }
        }
        return null;
    }

    /**
     * Returns previous date item id from date list.
     * @param id
     * @return
     */
    private int getPreviousDateItemId(int id) {
        int index = GUIDE_DATE_IDS.indexOf(id);
        if (index > 0) {
            return GUIDE_DATE_IDS.get(--index);
        }
        return id;
    }

    /**
     * Returns next date item id from date list.
     * @param id
     * @return
     */
    private int getNextDateItemId(int id) {
        int index = GUIDE_DATE_IDS.indexOf(id);
        if (index < GUIDE_DATE_IDS.size() - 1) {
            return GUIDE_DATE_IDS.get(++index);
        }
        return id;
    }

    /**
     * Sets date item focused.
     */
    private void setDateSelection() {
        Resources resources = getResources();

        for (int i = 0; i < GUIDE_DATE_IDS.size(); i++) {
            int id = GUIDE_DATE_IDS.get(i);
            this.setSelectedById(root, id, resources, id == selectedDateId);
        }
    }

    /**
     * Sets date item text color.
     * @param root
     * @param id
     * @param resources
     * @param selected
     */
    public void setSelectedById(View root, int id, Resources resources, boolean selected) {
        if (root != null) {
            TextView tv = root.findViewById(id);
            if (tv != null) {
                tv.setTextColor(ResourcesCompat.getColor(resources, selected ? R.color.tv7_default : R.color.toolbar_and_title_text, null));
            }
        }
    }
}
