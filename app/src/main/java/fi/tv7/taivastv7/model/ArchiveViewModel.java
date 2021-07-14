package fi.tv7.taivastv7.model;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fi.tv7.taivastv7.BuildConfig;
import fi.tv7.taivastv7.TaivasTv7;
import fi.tv7.taivastv7.helpers.ArchiveDataCacheItem;
import fi.tv7.taivastv7.helpers.GuideItem;
import fi.tv7.taivastv7.helpers.Utils;
import fi.tv7.taivastv7.interfaces.ArchiveDataLoadedListener;

import static fi.tv7.taivastv7.helpers.Constants.AMPERSAND;
import static fi.tv7.taivastv7.helpers.Constants.ARCHIVE_BASE_URL;
import static fi.tv7.taivastv7.helpers.Constants.ARCHIVE_LANGUAGE;
import static fi.tv7.taivastv7.helpers.Constants.ASPECT_RATIO;
import static fi.tv7.taivastv7.helpers.Constants.BROADCAST_DATE;
import static fi.tv7.taivastv7.helpers.Constants.BROADCAST_DATE_TIME;
import static fi.tv7.taivastv7.helpers.Constants.BROADCAST_RECOMMENDATIONS_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.CAPTION;
import static fi.tv7.taivastv7.helpers.Constants.CATEGORY;
import static fi.tv7.taivastv7.helpers.Constants.CATEGORY_ID;
import static fi.tv7.taivastv7.helpers.Constants.CATEGORY_ID_PARAM;
import static fi.tv7.taivastv7.helpers.Constants.CATEGORY_NAME;
import static fi.tv7.taivastv7.helpers.Constants.CATEGORY_PROGRAMS_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.CID;
import static fi.tv7.taivastv7.helpers.Constants.COLON;
import static fi.tv7.taivastv7.helpers.Constants.DASH_WITH_SPACES;
import static fi.tv7.taivastv7.helpers.Constants.DATE_INDEX;
import static fi.tv7.taivastv7.helpers.Constants.DATE_PARAM;
import static fi.tv7.taivastv7.helpers.Constants.DOT;
import static fi.tv7.taivastv7.helpers.Constants.DURATION;
import static fi.tv7.taivastv7.helpers.Constants.DYNAMIC_ROW_FIVE;
import static fi.tv7.taivastv7.helpers.Constants.DYNAMIC_ROW_FOUR;
import static fi.tv7.taivastv7.helpers.Constants.DYNAMIC_ROW_MIN_PROGRAMS;
import static fi.tv7.taivastv7.helpers.Constants.DYNAMIC_ROW_ONE;
import static fi.tv7.taivastv7.helpers.Constants.DYNAMIC_ROW_THREE;
import static fi.tv7.taivastv7.helpers.Constants.DYNAMIC_ROW_TWO;
import static fi.tv7.taivastv7.helpers.Constants.END_DATE;
import static fi.tv7.taivastv7.helpers.Constants.END_TIME;
import static fi.tv7.taivastv7.helpers.Constants.EPISODE_NUMBER;
import static fi.tv7.taivastv7.helpers.Constants.EQUAL;
import static fi.tv7.taivastv7.helpers.Constants.FIRST_BROADCAST;
import static fi.tv7.taivastv7.helpers.Constants.FORMATTED_END_TIME;
import static fi.tv7.taivastv7.helpers.Constants.FORMATTED_START_TIME;
import static fi.tv7.taivastv7.helpers.Constants.GET_;
import static fi.tv7.taivastv7.helpers.Constants.GUIDE_DATA;
import static fi.tv7.taivastv7.helpers.Constants.GUIDE_DATE_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.ID;
import static fi.tv7.taivastv7.helpers.Constants.IMAGE_PATH;
import static fi.tv7.taivastv7.helpers.Constants.IS_SERIES;
import static fi.tv7.taivastv7.helpers.Constants.IS_VISIBLE_ON_VOD;
import static fi.tv7.taivastv7.helpers.Constants.LIMIT_PARAM;
import static fi.tv7.taivastv7.helpers.Constants.LINK_PATH;
import static fi.tv7.taivastv7.helpers.Constants.LOG_TAG;
import static fi.tv7.taivastv7.helpers.Constants.MOST_VIEWED_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.NAME;
import static fi.tv7.taivastv7.helpers.Constants.NEGATIVE_ONE_STR;
import static fi.tv7.taivastv7.helpers.Constants.NETWORK_REQUEST_FAILED_ERROR;
import static fi.tv7.taivastv7.helpers.Constants.NETWORK_REQUEST_TIMEOUT_ERROR;
import static fi.tv7.taivastv7.helpers.Constants.NEWEST_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.NO_NETWORK_CONNECTION_ERROR;
import static fi.tv7.taivastv7.helpers.Constants.NULL_VALUE;
import static fi.tv7.taivastv7.helpers.Constants.OFFSET_PARAM;
import static fi.tv7.taivastv7.helpers.Constants.ONE_STR;
import static fi.tv7.taivastv7.helpers.Constants.ONGOING_PROGRAM;
import static fi.tv7.taivastv7.helpers.Constants.ONGOING_PROGRAM_INDEX;
import static fi.tv7.taivastv7.helpers.Constants.PARENT_CATEGORIES_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.PARENT_ID;
import static fi.tv7.taivastv7.helpers.Constants.PARENT_NAME;
import static fi.tv7.taivastv7.helpers.Constants.PATH;
import static fi.tv7.taivastv7.helpers.Constants.PIPE_WITH_SPACES;
import static fi.tv7.taivastv7.helpers.Constants.PLAY;
import static fi.tv7.taivastv7.helpers.Constants.PROGRAM_ID_PARAM;
import static fi.tv7.taivastv7.helpers.Constants.PROGRAM_INFO_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.QUERY_PARAM;
import static fi.tv7.taivastv7.helpers.Constants.QUESTION_MARK;
import static fi.tv7.taivastv7.helpers.Constants.RECOMMENDATIONS_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.RESULTS;
import static fi.tv7.taivastv7.helpers.Constants.SEARCH_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.SEARCH_URL;
import static fi.tv7.taivastv7.helpers.Constants.SERIES;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_AND_NAME;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_ID;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_ID_PARAM;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_INFO_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_NAME;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_PROGRAMS_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.SID;
import static fi.tv7.taivastv7.helpers.Constants.SNAME;
import static fi.tv7.taivastv7.helpers.Constants.SPACE;
import static fi.tv7.taivastv7.helpers.Constants.START_DATE;
import static fi.tv7.taivastv7.helpers.Constants.START_END_TIME;
import static fi.tv7.taivastv7.helpers.Constants.SUB_CATEGORIES_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.TIME;
import static fi.tv7.taivastv7.helpers.Constants.TRANSLATION_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.TWO_STR;
import static fi.tv7.taivastv7.helpers.Constants.TYPE;
import static fi.tv7.taivastv7.helpers.Constants.UTF_8;
import static fi.tv7.taivastv7.helpers.Constants.VISIBLE_ON_VOD_SINCE;
import static fi.tv7.taivastv7.helpers.Constants.VOD_PARAM;
import static fi.tv7.taivastv7.helpers.Constants.VOLLEY_TIMEOUT_VALUE;
import static fi.tv7.taivastv7.helpers.Constants.ZERO_DATE;
import static fi.tv7.taivastv7.helpers.Constants.ZERO_DATE_TIME;
import static fi.tv7.taivastv7.helpers.Constants.ZERO_STR;

/**
 * Archive view model. Makes queries and cache some data.
 */
public class ArchiveViewModel extends ViewModel {

    private ArchiveDataCacheItem recommended = null;
    private ArchiveDataCacheItem broadcastRecommendations = null;
    private ArchiveDataCacheItem mostViewed = null;
    private ArchiveDataCacheItem newest = null;
    private ArchiveDataCacheItem series = null;
    private ArchiveDataCacheItem dynamicRowOne = null;
    private ArchiveDataCacheItem dynamicRowTwo = null;
    private ArchiveDataCacheItem dynamicRowThree = null;
    private ArchiveDataCacheItem dynamicRowFour = null;
    private ArchiveDataCacheItem dynamicRowFive = null;

    private ArchiveDataCacheItem parentCategories = null;
    private ArchiveDataCacheItem subCategories = null;

    private List<GuideItem> threeDaysGuide = null;
    private boolean dynamicRowsInitialized = false;

    public JSONObject getRecommendedByIndex(int index) throws Exception {
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        if (broadcastRecommendations != null) {
            if (!broadcastRecommendations.isDataInIndex(index)) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "*** ArchiveViewModel.getRecommendedByIndex(): No broadcast recommendations data in index!");
                }
                throw new Exception("Not broadcast recommendations data in index!");
            }

            jsonArray = broadcastRecommendations.getData();
        }
        else if (recommended != null) {
            if (!recommended.isDataInIndex(index)) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "*** ArchiveViewModel.getRecommendedByIndex(): No recommend data in index!");
                }
                throw new Exception("Not recommend data in index!");
            }

            jsonArray = recommended.getData();
        }

        if (jsonArray != null) {
            jsonObject = jsonArray.getJSONObject(index);
        }

        return jsonObject;
    }

    public void clearBroadcastRecommendations() {
        broadcastRecommendations = null;
    }

    public JSONObject getMostViewedByIndex(int index) throws Exception {
        JSONObject jsonObject = null;

        if (mostViewed != null) {
            if (!mostViewed.isDataInIndex(index)) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "*** ArchiveViewModel.getMostViewedByIndex(): No most viewed data in index!");
                }
                throw new Exception("No most viewed data in index!");
            }

            JSONArray jsonArray = mostViewed.getData();
            if (jsonArray != null) {
                jsonObject = jsonArray.getJSONObject(index);
            }
        }

        return jsonObject;
    }

    public JSONObject getNewestByIndex(int index) throws Exception {
        JSONObject jsonObject = null;

        if (newest != null) {
            if (!newest.isDataInIndex(index)) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "*** ArchiveViewModel.getNewestByIndex(): No newest data in index!");
                }
                throw new Exception("No newest data in index!");
            }

            JSONArray jsonArray = newest.getData();
            if (jsonArray != null) {
                jsonObject = jsonArray.getJSONObject(index);
            }
        }

        return jsonObject;
    }

    public JSONObject getSeriesByIndex(int index) throws Exception {
        JSONObject jsonObject = null;

        if (series != null) {
            if (!series.isDataInIndex(index)) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "*** ArchiveViewModel.getSeriesByIndex(): No series data in index!");
                }
                throw new Exception("No series data in index!");
            }

            JSONArray jsonArray = series.getData();
            if (jsonArray != null) {
                jsonObject = jsonArray.getJSONObject(index);
            }
        }

        return jsonObject;
    }

    public JSONObject getDynamicRowElementByIndex(int index, int row) throws Exception {
        JSONObject jsonObject = null;
        ArchiveDataCacheItem data = null;

        if (row == DYNAMIC_ROW_ONE) {
            data = new ArchiveDataCacheItem(dynamicRowOne.getData());
        }
        else if (row == DYNAMIC_ROW_TWO) {
            data = new ArchiveDataCacheItem(dynamicRowTwo.getData());
        }
        else if (row == DYNAMIC_ROW_THREE) {
            data = new ArchiveDataCacheItem(dynamicRowThree.getData());
        }
        else if (row == DYNAMIC_ROW_FOUR) {
            data = new ArchiveDataCacheItem(dynamicRowFour.getData());
        }
        else if (row == DYNAMIC_ROW_FIVE) {
            data = new ArchiveDataCacheItem(dynamicRowFive.getData());
        }

        if (data != null) {
            if (!data.isDataInIndex(index)) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "*** ArchiveViewModel.getDynamicRowElementByIndex(): No dynamic row data in index!");
                }
                throw new Exception("No dynamic row data in index!");
            }

            JSONArray jsonArray = data.getData();
            if (jsonArray != null) {
                jsonObject = jsonArray.getJSONObject(index);
            }
        }

        return jsonObject;
    }

    public JSONArray hasSubCategories(int index) throws Exception {
        JSONArray result = new JSONArray();

        if (parentCategories != null) {
            JSONObject obj = parentCategories.getData().getJSONObject(index);
            if (obj != null) {
                String id = Utils.getJsonStringValue(obj, ID);
                if (id != null) {
                    result = this.getSubCategoriesByParentId(id);
                }
            }
        }
        return result;
    }

    public JSONArray getSubCategoriesByParentId(String id) throws Exception {
        JSONArray result = new JSONArray();

        if (subCategories == null || id == null) {
            return result;
        }

        JSONArray sc = subCategories.getData();

        for( int i = 0; sc != null && i < sc.length(); i++) {
            JSONObject obj = sc.getJSONObject(i);
            if (obj != null) {
                String parentId = Utils.getJsonStringValue(obj, PARENT_ID);
                if (parentId != null && parentId.equals(id)) {
                    result.put(obj);
                }
            }
        }

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getSubCategoriesByParentId(): Parent id: " + id + " has " + result.length() + " sub categories.");
        }

        return result;
    }

    public JSONObject getParentCategoryByIndex(int index) throws Exception {
        JSONObject parentCategory = null;

        if (parentCategories != null) {
            if (!parentCategories.isDataInIndex(index)) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchiveViewModel.getParentCategoryByIndex(): Not parent categories data in index!");
                }
                throw new Exception("Not parent categories data in index!");
            }

            JSONArray pc = parentCategories.getData();
            if (pc != null) {
                parentCategory = pc.getJSONObject(index);
            }
        }
        return parentCategory;
    }

    public JSONArray getParentCategories() {
        if (parentCategories != null) {
            return parentCategories.getData();
        }
        return null;
    }

    public JSONArray getSeriesData() {
        if (series != null) {
            return series.getData();
        }
        return null;
    }

    public void initializeSeriesData(List<GuideItem> guideData) throws Exception {
        if (guideData != null) {
            JSONArray jsonArray = new JSONArray();
            List<Integer> seen = new ArrayList<>();

            for(int i = 0; i < guideData.size(); i++) {
                GuideItem g = guideData.get(i);
                if (g == null) {
                    continue;
                }

                Integer sid = g.getSid();
                Integer episodeNumber = g.getEpisodeNumber();
                Integer isVisibleOnVod = g.getIsVisibleOnVod();

                if (sid == null || episodeNumber == null || isVisibleOnVod == null) {
                    continue;
                }

                String visibleOnVod = String.valueOf(isVisibleOnVod);

                if (episodeNumber > 1 && visibleOnVod.length() > 0 && !visibleOnVod.equals(NEGATIVE_ONE_STR) && !seen.contains(sid)) {
                    JSONObject obj = new JSONObject();
                    obj.put(SID, g.getSid());
                    obj.put(SERIES_NAME, g.getSeries());
                    obj.put(IMAGE_PATH, g.getImagePath());
                    obj.put(SERIES_AND_NAME, g.getSeriesAndName());
                    obj.put(START_DATE, g.getStartDate());
                    obj.put(DURATION, g.getDuration());

                    jsonArray.put(obj);
                    seen.add(sid);
                }
            }

            series = new ArchiveDataCacheItem(jsonArray);
        }
    }

    public void initializeDynamicData() throws Exception {
        if (threeDaysGuide == null) {
            throw new Exception("Initialization of dynamic rows data failed because guide data is not initialized!");
        }

        Map<Integer, JSONArray> dynamicRows = new HashMap<>();
        List<Integer> seen = new ArrayList<>();

        for (int i = 0; i < threeDaysGuide.size(); i++) {
            GuideItem g = threeDaysGuide.get(i);
            if (g == null) {
                continue;
            }

            String visibleOnVod = String.valueOf(g.getIsVisibleOnVod());
            if (!visibleOnVod.equals(ONE_STR) && !visibleOnVod.equals(TWO_STR)) {
                continue;
            }

            Integer id = g.getId();
            Integer cid = g.getCid();
            if (id != null && cid != null && !seen.contains(id)) {
                seen.add(id);
                JSONObject obj = new JSONObject();
                obj.put(ID, g.getId());
                obj.put(IMAGE_PATH, g.getImagePath());
                obj.put(BROADCAST_DATE_TIME, g.getBroadcastDateTime());
                obj.put(DURATION, g.getDuration());
                obj.put(SERIES_AND_NAME, g.getSeriesAndName());
                obj.put(CATEGORY, g.getCategory());

                this.addToMap(cid, obj, dynamicRows);
            }
        }

        List<Integer> keys = this.finalizeMap(dynamicRows);
        Collections.shuffle(keys);

        for(int i = 0; i < keys.size(); i++) {
            Integer key = keys.get(i);

            if (i == DYNAMIC_ROW_ONE - 1) {
                dynamicRowOne = new ArchiveDataCacheItem(dynamicRows.get(key));
            }
            else if (i == DYNAMIC_ROW_TWO - 1) {
                dynamicRowTwo = new ArchiveDataCacheItem(dynamicRows.get(key));
            }
            else if (i == DYNAMIC_ROW_THREE - 1) {
                dynamicRowThree = new ArchiveDataCacheItem(dynamicRows.get(key));
            }
            else if (i == DYNAMIC_ROW_FOUR - 1) {
                dynamicRowFour = new ArchiveDataCacheItem(dynamicRows.get(key));
            }
            else if (i == DYNAMIC_ROW_FIVE - 1) {
                dynamicRowFive = new ArchiveDataCacheItem(dynamicRows.get(key));
            }
        }

        dynamicRows.clear();
        threeDaysGuide.clear();

        dynamicRowsInitialized = true;
    }

    public boolean isDynamicRowsInitialized() {
        return dynamicRowsInitialized;
    }

    public JSONArray getDynamicDataRow(int rowNumber) {
        if (rowNumber == DYNAMIC_ROW_ONE && dynamicRowOne != null) {
            return dynamicRowOne.getData();
        }
        else if (rowNumber == DYNAMIC_ROW_TWO && dynamicRowTwo != null) {
            return dynamicRowTwo.getData();
        }
        else if (rowNumber == DYNAMIC_ROW_THREE && dynamicRowThree != null) {
            return dynamicRowThree.getData();
        }
        else if (rowNumber == DYNAMIC_ROW_FOUR && dynamicRowFour != null) {
            return dynamicRowFour.getData();
        }
        else if (rowNumber == DYNAMIC_ROW_FIVE && dynamicRowFive != null) {
            return dynamicRowFive.getData();
        }
        return null;
    }

    public int getDynamicRowCount() {
        int size = 0;
        if (dynamicRowOne != null) {
            size++;
        }

        if (dynamicRowTwo != null) {
            size++;
        }

        if (dynamicRowThree != null) {
            size++;
        }

        if (dynamicRowFour != null) {
            size++;
        }

        if (dynamicRowFive != null) {
            size++;
        }
        return size;
    }

    private void addToMap(Integer cid, JSONObject jsonObject, Map<Integer, JSONArray> dynamicRows) {
        JSONArray jsonArray = dynamicRows.get(cid);
        if (jsonArray == null) {
            jsonArray = new JSONArray();
        }

        jsonArray.put(jsonObject);
        dynamicRows.put(cid, jsonArray);
    }

    private List<Integer> finalizeMap(Map<Integer, JSONArray> dynamicRows) {
        List<Integer> keys = new ArrayList<>();

        for(Iterator<Map.Entry<Integer, JSONArray>> it = dynamicRows.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Integer, JSONArray> entry = it.next();

            JSONArray jsonArray = entry.getValue();
            if (jsonArray == null) {
                continue;
            }

            if(jsonArray.length() < DYNAMIC_ROW_MIN_PROGRAMS) {
                it.remove();
            }
            else {
                keys.add(entry.getKey());
            }
        }

        return keys;
    }

    public List<GuideItem> getThreeDaysGuide() {
        if (threeDaysGuide != null) {
            return threeDaysGuide;
        }
        return new ArrayList<>();
    }

    public void addGuideData(List<GuideItem> guideData, boolean toEnd) {
        if (threeDaysGuide == null) {
            threeDaysGuide = new ArrayList<>();
        }

        if (guideData != null) {
            if (toEnd) {
                threeDaysGuide.addAll(guideData);
            }
            else {
                threeDaysGuide.addAll(0, guideData);
            }
        }
    }

    /**
     * View model clear lifecycle method.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
    }

    /**
     * Broadcast recommendations programs query.
     * @param date
     * @param limit
     * @param offset
     * @param archiveDataLoadedListener
     */
    public void getBroadcastRecommendationPrograms(final String date, int limit, int offset, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = BROADCAST_RECOMMENDATIONS_METHOD;

        if (broadcastRecommendations != null && broadcastRecommendations.isCacheValid()) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "*** ArchiveViewModel.getBroadcastRecommendationPrograms(): Return data from cache.");
            }

            archiveDataLoadedListener.onArchiveDataLoaded(broadcastRecommendations.getData(), type);
        }
        else {
            String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + DATE_PARAM + EQUAL + date + AMPERSAND + LIMIT_PARAM + EQUAL + limit + AMPERSAND + OFFSET_PARAM + EQUAL + offset;
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.getBroadcastRecommendationPrograms(): URL" + url);
            }

            this.runQuery(url, type, null, archiveDataLoadedListener);
        }
    }

    /**
     * Recommend programs query.
     * @param date
     * @param limit
     * @param offset
     * @param archiveDataLoadedListener
     */
    public void getRecommendPrograms(final String date, int limit, int offset, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = RECOMMENDATIONS_METHOD;

        if (recommended != null && recommended.isCacheValid()) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "*** ArchiveViewModel.getBroadcastRecommendationPrograms(): Return data from cache.");
            }

            archiveDataLoadedListener.onArchiveDataLoaded(recommended.getData(), type);
        }
        else {
            String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + DATE_PARAM + EQUAL + date + AMPERSAND + LIMIT_PARAM + EQUAL + limit + AMPERSAND + OFFSET_PARAM + EQUAL + offset;
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.getRecommendPrograms(): URL" + url);
            }

            this.runQuery(url, type, null, archiveDataLoadedListener);
        }
    }

    /**
     * Most viewed programs query.
     * @param archiveLang
     * @param archiveDataLoadedListener
     */
    public void getMostViewedPrograms(final String archiveLang, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = MOST_VIEWED_METHOD;

        if (mostViewed != null && mostViewed.isCacheValid()) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "*** ArchiveViewModel.getMostViewedPrograms(): Return data from cache.");
            }

            archiveDataLoadedListener.onArchiveDataLoaded(mostViewed.getData(), type);
        }
        else {
            String url = ARCHIVE_BASE_URL + type + QUESTION_MARK + VOD_PARAM + EQUAL + archiveLang;
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.getMostViewedPrograms(): URL" + url);
            }

            this.runQuery(url, type, null, archiveDataLoadedListener);
        }
    }

    /**
     * Newest programs query.
     * @param date
     * @param limit
     * @param offset
     * @param archiveDataLoadedListener
     */
    public void getNewestPrograms(final String date, int limit, int offset, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = NEWEST_METHOD;

        if (newest != null && newest.isCacheValid()) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "*** ArchiveViewModel.getNewestPrograms(): Return data from cache.");
            }

            archiveDataLoadedListener.onArchiveDataLoaded(newest.getData(), type);
        }
        else {
            String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + DATE_PARAM + EQUAL + date + AMPERSAND + LIMIT_PARAM + EQUAL + limit + AMPERSAND + OFFSET_PARAM + EQUAL + offset;
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.getNewestPrograms(): URL" + url);
            }

            this.runQuery(url, type, null, archiveDataLoadedListener);
        }
    }

    /**
     * Parent categories query.
     * @param archiveDataLoadedListener
     */
    public void getParentCategories(final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = PARENT_CATEGORIES_METHOD;

        if (parentCategories != null) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "*** ArchiveViewModel.getParentCategories(): Return data from cache.");
            }

            archiveDataLoadedListener.onArchiveDataLoaded(parentCategories.getData(), type);
        }
        else {
            String url = ARCHIVE_BASE_URL + GET_ + type;
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.getParentCategories(): URL" + url);
            }

            this.runQuery(url, type, null, archiveDataLoadedListener);
        }
    }

    /**
     * Sub categories query.
     * @param archiveDataLoadedListener
     */
    public void getSubCategories(final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = SUB_CATEGORIES_METHOD;

        if (subCategories != null) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "*** ArchiveViewModel.getSubCategories(): Return data from cache.");
            }

            archiveDataLoadedListener.onArchiveDataLoaded(subCategories.getData(), type);
        }
        else {
            String url = ARCHIVE_BASE_URL + GET_ + type;
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.getSubCategories(): URL" + url);
            }

            this.runQuery(url, type, null, archiveDataLoadedListener);
        }
    }

    /**
     * Translation query.
     * @param archiveDataLoadedListener
     */
    public void getTranslation(final String programId, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = TRANSLATION_METHOD;

        String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + PROGRAM_ID_PARAM + EQUAL + programId;
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getTranslation(): URL: " + url);
        }

        this.runQuery(url, type, null, archiveDataLoadedListener);
    }


    /**
     * Category programs query.
     * @param categoryId
     * @param limit
     * @param offset
     * @param archiveDataLoadedListener
     */
    public void getCategoryPrograms(final int categoryId, final int limit, final int offset, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = CATEGORY_PROGRAMS_METHOD;

        String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + CATEGORY_ID_PARAM + EQUAL + categoryId
                + AMPERSAND + LIMIT_PARAM + EQUAL + limit + AMPERSAND + OFFSET_PARAM + EQUAL + offset;
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getCategoryPrograms(): URL: " + url);
        }

        this.runQuery(url, type, null, archiveDataLoadedListener);
    }

    /**
     * Program info query.
     * @param programId
     * @param archiveDataLoadedListener
     */
    public void getProgramInfo(final String programId, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = PROGRAM_INFO_METHOD;

        String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + PROGRAM_ID_PARAM + EQUAL + programId;
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getProgramInfo(): URL: " + url);
        }

        this.runQuery(url, type, null, archiveDataLoadedListener);
    }

    /**
     * Series info query.
     * @param seriesId
     * @param archiveDataLoadedListener
     */
    public void getSeriesInfo(final String seriesId, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = SERIES_INFO_METHOD;

        String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + SERIES_ID_PARAM + EQUAL + seriesId;
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getSeriesInfo(): URL: " + url);
        }

        this.runQuery(url, type, null, archiveDataLoadedListener);
    }

    /**
     * Series programs query.
     * @param seriesId
     * @param limit
     * @param offset
     * @param archiveDataLoadedListener
     */
    public void getSeriesPrograms(final int seriesId, final int limit, final int offset, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = SERIES_PROGRAMS_METHOD;

        String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + SERIES_ID_PARAM + EQUAL + seriesId
                + AMPERSAND + LIMIT_PARAM + EQUAL + limit + AMPERSAND + OFFSET_PARAM + EQUAL + offset;
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getSeriesPrograms(): URL: " + url);
        }

        this.runQuery(url, type, null, archiveDataLoadedListener);
    }

    /**
     * Guide by date query.
     * @param date
     * @param archiveDataLoadedListener
     */
    public void getGuideByDate(final String date, final Integer dateIndex, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = GUIDE_DATE_METHOD;

        String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + DATE_PARAM + EQUAL + date;
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getGuideByDate(): URL: " + url);
        }

        this.runQuery(url, type, dateIndex, archiveDataLoadedListener);
    }

    /**
     * Search items by search string.
     * @param searchString
     * @param archiveDataLoadedListener
     */
    public void searchItemsByString(String searchString, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        try {
            String type = SEARCH_METHOD;

            searchString = URLEncoder.encode(searchString, UTF_8);

            String url = SEARCH_URL + QUESTION_MARK + VOD_PARAM + EQUAL + ARCHIVE_LANGUAGE + AMPERSAND + QUERY_PARAM + EQUAL + searchString;

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.searchItemsByString(): URL: " + url);
            }

            this.runQuery(url, type, null, archiveDataLoadedListener);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.searchItemsByString(): Exception: " + e);
            }

            archiveDataLoadedListener.onArchiveDataLoadError(e.getMessage(), SEARCH_METHOD);
        }
    }

    /**
     * Runs REST query.
     * @param url
     * @param type
     * @param archiveDataLoadedListener
     */
    private void runQuery(final String url, final String type, final Object data, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.runQuery(): called.");
            }

            TaivasTv7 app = TaivasTv7.getInstance();

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (BuildConfig.DEBUG) {
                                    Log.d(LOG_TAG, "ArchiveViewModel.runQuery(): onResponse(): " + response.toString());
                                }

                                if (archiveDataLoadedListener != null) {
                                    if (type.equals(PARENT_CATEGORIES_METHOD) || type.equals(SUB_CATEGORIES_METHOD)) {
                                        JSONArray filtered = filterCategoryResponse(response, type);
                                        if (type.equals(PARENT_CATEGORIES_METHOD)) {
                                            parentCategories = new ArchiveDataCacheItem(filtered);
                                        }
                                        else {
                                            subCategories = new ArchiveDataCacheItem(filtered);
                                        }

                                        archiveDataLoadedListener.onArchiveDataLoaded(filtered, type);
                                    }
                                    else if (type.equals(TRANSLATION_METHOD)) {
                                        JSONArray filtered = response.getJSONArray(type);

                                        archiveDataLoadedListener.onArchiveDataLoaded(filtered, type);
                                    }
                                    else if (type.equals(SERIES_INFO_METHOD)) {
                                        JSONArray filtered = filterSeriesResponse(response, type);

                                        archiveDataLoadedListener.onArchiveDataLoaded(filtered, type);
                                    }
                                    else if (type.equals(GUIDE_DATE_METHOD)) {
                                        JSONArray filtered = filterGuideByDateResponse(response, type, (Integer) data);

                                        archiveDataLoadedListener.onArchiveDataLoaded(filtered, type);
                                    }
                                    else if (type.equals(SEARCH_METHOD)) {
                                        JSONArray filtered = filterResponse(response, RESULTS);

                                        archiveDataLoadedListener.onArchiveDataLoaded(filtered, type);
                                    }
                                    else {
                                        JSONArray filtered = filterResponse(response, type);

                                        if (type.equals(RECOMMENDATIONS_METHOD)) {
                                            recommended = new ArchiveDataCacheItem(filtered);
                                        }
                                        else if (type.equals(BROADCAST_RECOMMENDATIONS_METHOD)) {
                                            broadcastRecommendations = new ArchiveDataCacheItem(filtered);
                                        }
                                        else if (type.equals(MOST_VIEWED_METHOD)) {
                                            mostViewed = new ArchiveDataCacheItem(filtered);
                                        }
                                        else if (type.equals(NEWEST_METHOD)) {
                                            newest = new ArchiveDataCacheItem(filtered);
                                        }

                                        archiveDataLoadedListener.onArchiveDataLoaded(filtered, type);
                                    }
                                }
                            }
                            catch (Exception e) {
                                if (BuildConfig.DEBUG) {
                                    Log.d(LOG_TAG, "ArchiveViewModel.onResponse(): Error fetching json: " + e.getMessage());
                                }

                                if (archiveDataLoadedListener != null) {
                                    archiveDataLoadedListener.onArchiveDataLoadError(e.getMessage(), type);
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                if (BuildConfig.DEBUG) {
                                    Log.d(LOG_TAG, "ArchiveViewModel.onErrorResponse(): Error fetching json: " + error.getMessage());
                                }

                                if (archiveDataLoadedListener != null) {
                                    if (error instanceof NoConnectionError) {
                                        app.setErrorCode(NO_NETWORK_CONNECTION_ERROR);
                                        archiveDataLoadedListener.onNetworkError(type);
                                    }
                                    else if (error instanceof TimeoutError) {
                                        app.setErrorCode(NETWORK_REQUEST_TIMEOUT_ERROR);
                                        archiveDataLoadedListener.onNetworkError(type);
                                    }
                                    else {
                                        app.setErrorCode(NETWORK_REQUEST_FAILED_ERROR);
                                        archiveDataLoadedListener.onNetworkError(type);
                                    }
                                }
                            }
                            catch (Exception e) {
                                if (BuildConfig.DEBUG) {
                                    Log.d(LOG_TAG, "ArchiveViewModel.onErrorResponse(): Error fetching json: " + e.getMessage());
                                }

                                if (archiveDataLoadedListener != null) {
                                    archiveDataLoadedListener.onArchiveDataLoadError(e.getMessage(), type);
                                }
                            }
                        }
                    }
            );

            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    VOLLEY_TIMEOUT_VALUE,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            app.addToRequestQueue(jsonRequest);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.runQuery(): Exception: " + e.getMessage());
            }

            archiveDataLoadedListener.onArchiveDataLoadError(e.getMessage(), type);
        }
    }

    /**
     * Filters JSON response.
     * @param jsonObject
     * @param objectName
     * @return
     * @throws Exception
     */
    private JSONArray filterResponse(JSONObject jsonObject, String objectName) throws Exception {
        JSONArray respArray = new JSONArray();

        JSONArray array = jsonObject.getJSONArray(objectName);

        for (int i = 0; i < array.length(); i++) {
            JSONObject respObj = new JSONObject();
            JSONObject sourceObj = array.getJSONObject(i);

            setValue(respObj, ID, this.getValue(sourceObj, ID), true);
            setValue(respObj, IMAGE_PATH, this.getValue(sourceObj, IMAGE_PATH), false);
            setValue(respObj, LINK_PATH, this.getValue(sourceObj, LINK_PATH), false);
            setValue(respObj, EPISODE_NUMBER, this.getValue(sourceObj, EPISODE_NUMBER), true);
            setValue(respObj, SID, this.getValue(sourceObj, SID), true);
            setValue(respObj, SERIES_ID, this.getValue(sourceObj, SERIES_ID), true);
            setValue(respObj, NAME, this.getValue(sourceObj, NAME), false);
            setValue(respObj, SERIES_NAME, this.getValue(sourceObj, SERIES_NAME), false);
            setValue(respObj, SNAME, this.getValue(sourceObj, SNAME), false);

            String firstBroadcast = this.getValue(sourceObj, FIRST_BROADCAST);
            if (firstBroadcast == null) {
                firstBroadcast = this.getValue(sourceObj, START_DATE);
            }

            setValue(respObj, BROADCAST_DATE_TIME, this.getDateTimeByTimeInMs(firstBroadcast), false);
            setValue(respObj, BROADCAST_DATE, this.getDateByTimeInMs(firstBroadcast), false);
            setValue(respObj, DURATION, Utils.getTimeStampByDurationMs(this.getValue(sourceObj, DURATION)), false);

            setValue(respObj, ASPECT_RATIO, this.getValue(sourceObj, ASPECT_RATIO), false);

            String seriesName = this.getValue(respObj, SERIES_NAME);
            if (seriesName == null) {
                seriesName = this.getValue(respObj, SNAME);
            }

            String seriesAndName = null;
            if (seriesName != null) {
                seriesAndName = seriesName;
            }

            String name = this.getValue(respObj, NAME);
            if (seriesName != null && name != null && name.length() > 0 && !seriesName.equals(name)) {
                seriesAndName += (PIPE_WITH_SPACES + name);
            }

            if (seriesAndName == null) {
                seriesAndName = name;
            }

            setValue(respObj, SERIES_AND_NAME, seriesAndName, false);

            setValue(respObj, CAPTION, this.getValue(sourceObj, CAPTION), false);
            setValue(respObj, TYPE, this.getValue(sourceObj, TYPE), false);
            setValue(respObj, PATH, this.getValue(sourceObj, PATH), false);

            if (objectName.equals(BROADCAST_RECOMMENDATIONS_METHOD) || objectName.equals(PROGRAM_INFO_METHOD)) {
                String isVisibleOnVod = this.getValue(sourceObj, IS_VISIBLE_ON_VOD);
                if (isVisibleOnVod != null) {
                    setValue(respObj, IS_VISIBLE_ON_VOD, isVisibleOnVod, true);

                    String visibleOnVodSince = this.getValue(sourceObj, VISIBLE_ON_VOD_SINCE);
                    if (visibleOnVodSince != null && isVisibleOnVod.equals(ONE_STR)) {
                        String isVisibleValue = isPastTime(visibleOnVodSince) ? ONE_STR : ZERO_STR;
                        setValue(respObj, IS_VISIBLE_ON_VOD, isVisibleValue, true);
                    }
                }
                else {
                    String visibleOnVodSince = this.getValue(sourceObj, VISIBLE_ON_VOD_SINCE);
                    if (visibleOnVodSince == null) {
                        setValue(respObj, IS_VISIBLE_ON_VOD, NEGATIVE_ONE_STR, true);
                    }
                }

                firstBroadcast = getValue(sourceObj, VISIBLE_ON_VOD_SINCE);
                if (firstBroadcast != null) {
                    String duration = getValue(sourceObj, DURATION);
                    if (duration != null) {
                        String startTime = String.valueOf(Long.parseLong(firstBroadcast) - Long.parseLong(duration));
                        setValue(respObj, BROADCAST_DATE_TIME, getDateTimeByTimeInMs(startTime), false);
                    }
                }
            }
            else {
                setValue(respObj, IS_VISIBLE_ON_VOD, ONE_STR, true);
            }

            if (objectName.equals(CATEGORY_PROGRAMS_METHOD)) {
                String play = this.getValue(sourceObj, PLAY);
                if (play != null) {
                    setValue(respObj, PLAY, Boolean.parseBoolean(play) ? ONE_STR : ZERO_STR, true);
                }
            }

            respArray.put(respObj);
        }

        return respArray;
    }

    /**
     * Filters parent category and sub category responses.
     * @param jsonObject
     * @param type
     * @return
     * @throws Exception
     */
    private JSONArray filterCategoryResponse(JSONObject jsonObject, String type) throws Exception {
        JSONArray respArray = new JSONArray();

        JSONArray array = jsonObject.getJSONArray(type);

        for (int i = 0; i < array.length(); i++) {
            JSONObject respObj = new JSONObject();
            JSONObject sourceObj = array.getJSONObject(i);

            if (type.equals(PARENT_CATEGORIES_METHOD)) {
                setValue(respObj, ID, this.getValue(sourceObj, ID), true);
                setValue(respObj, NAME, this.getValue(sourceObj, NAME), false);
            }
            else {
                setValue(respObj, PARENT_ID, this.getValue(sourceObj, PARENT_ID), true);
                setValue(respObj, PARENT_NAME, this.getValue(sourceObj, PARENT_NAME), false);
                setValue(respObj, CATEGORY_ID, this.getValue(sourceObj, CATEGORY_ID), true);
                setValue(respObj, CATEGORY_NAME, this.getValue(sourceObj, CATEGORY_NAME), false);
            }

            respArray.put(respObj);
        }

        return respArray;
    }

    private JSONArray filterSeriesResponse(JSONObject jsonObject, String type) throws Exception {
        JSONArray respArray = jsonObject.getJSONArray(type);

        JSONObject obj = respArray.getJSONObject(0);
        if (obj != null) {
            setValue(obj, IS_SERIES, ONE_STR, false);
            setValue(obj, SID, this.getValue(obj, ID), false);
        }

        return respArray;
    }

    private JSONArray filterGuideByDateResponse(JSONObject jsonObject, String type, Integer dateIndex) throws Exception {
        JSONArray respArray = new JSONArray();

        JSONArray array = jsonObject.getJSONArray(type);
        long currentTime = Utils.getTimeInMilliseconds();

        int ongoingProgramIndex = 0;
        for (int i = 0; i < array.length(); i++) {
            JSONObject respObj = new JSONObject();
            JSONObject sourceObj = array.getJSONObject(i);

            setValue(respObj, DATE_INDEX, String.valueOf(dateIndex), true);

            setValue(respObj, ID, this.getValue(sourceObj, ID), true);
            setValue(respObj, PATH, this.getValue(sourceObj, PATH), false);

            setValue(respObj, IMAGE_PATH, this.getValue(sourceObj, IMAGE_PATH), false);
            setValue(respObj, CAPTION, this.getValue(sourceObj, CAPTION), false);

            String start = this.getValue(sourceObj, TIME);
            String end = this.getValue(sourceObj, END_TIME);

            if (start == null || end == null) {
                throw new Exception("Invalid response in guide by date query!");
            }

            if (currentTime >= Long.parseLong(start) && currentTime <= Long.parseLong(end) || Long.parseLong(end) < currentTime) {
                ongoingProgramIndex = i;
            }

            setValue(respObj, TIME, start, false);
            setValue(respObj, END_TIME, end, false);

            // Start and end time (hh:mm - hh:mm)
            String startTime = this.createLocalTimeString(start);
            String endTime = this.createLocalTimeString(end);

            setValue(respObj, FORMATTED_START_TIME, startTime, false);
            setValue(respObj, FORMATTED_END_TIME, endTime, false);
            setValue(respObj, START_END_TIME, startTime + DASH_WITH_SPACES + endTime, false);

            // Start and end date (dd.mm.yyyy)
            String startDate = this.createLocalDateString(start);
            String endDate = this.createLocalDateString(end);

            setValue(respObj, START_DATE, startDate, false);
            setValue(respObj, END_DATE, endDate, false);

            setValue(respObj, BROADCAST_DATE_TIME, getDateTimeByTimeInMs(start), false);
            setValue(respObj, BROADCAST_DATE, getDateByTimeInMs(start), false);

            String duration = String.valueOf(Long.parseLong(end) - Long.parseLong(start));
            setValue(respObj, DURATION, Utils.getTimeStampByDurationMs(duration), false);

            setValue(respObj, SERIES, this.getValue(sourceObj, SERIES), false);
            setValue(respObj, NAME, this.getValue(sourceObj, NAME), false);

            setValue(respObj, SID, this.getValue(sourceObj, SID), true);
            setValue(respObj, EPISODE_NUMBER, this.getValue(sourceObj, EPISODE_NUMBER), true);

            setValue(respObj, CID, this.getValue(sourceObj, CID), true);
            setValue(respObj, CATEGORY, this.getValue(sourceObj, CATEGORY), false);

            String isVisibleOnVod = this.getValue(sourceObj, IS_VISIBLE_ON_VOD);
            String visibleOnVodSince = this.getValue(sourceObj, VISIBLE_ON_VOD_SINCE);
            if (isVisibleOnVod != null && visibleOnVodSince != null) {
                String isVisibleValue = isPastTime(visibleOnVodSince) ? TWO_STR : ZERO_STR;
                setValue(respObj, IS_VISIBLE_ON_VOD, isVisibleValue, true);
            }
            else {
                setValue(respObj, IS_VISIBLE_ON_VOD, NEGATIVE_ONE_STR, true);
            }

            // series and name
            String series = this.getValue(sourceObj, SERIES);

            String seriesName = null;
            if (series != null) {
                seriesName = series;
            }

            String name = this.getValue(sourceObj, NAME);
            if (series != null && name != null && name.length() > 0) {
                seriesName += (PIPE_WITH_SPACES + name);
            }

            if (seriesName == null) {
                seriesName = name;
            }

            setValue(respObj, SERIES_AND_NAME, seriesName, false);

            respArray.put(respObj);
        }

        JSONObject responseObj = new JSONObject();
        responseObj.put(ONGOING_PROGRAM_INDEX, ongoingProgramIndex);
        responseObj.put(DATE_INDEX, dateIndex);
        responseObj.put(GUIDE_DATA, dateIndex == 0 ? this.addOngoingProgramToIndex(respArray, ongoingProgramIndex) : respArray);

        return new JSONArray().put(responseObj);
    }

    /**
     * Adds ongoing program flag to index.
     * @param respArray
     * @param index
     * @return
     * @throws Exception
     */
    private JSONArray addOngoingProgramToIndex(JSONArray respArray, int index) throws Exception {
        JSONObject obj = respArray.getJSONObject(index);
        if (obj != null) {
            setValue(obj, ONGOING_PROGRAM, ONE_STR, true);
            respArray.put(index, obj);
        }

        return respArray;
    }

    /**
     * Checks and get value of property from the source object.
     * @param obj
     * @param id
     * @return
     * @throws Exception
     */
    private String getValue(JSONObject obj, String id) throws Exception {
        if (obj.has(id)) {
            String value = obj.getString(id);
            return value.equals(NULL_VALUE) ? null : value;
        }
        return null;
    }

    /**
     * Validates and sets property to response object.
     * @param obj
     * @param id
     * @param value
     * @param isInteger
     * @throws Exception
     */
    private void setValue(JSONObject obj, String id, String value, boolean isInteger) throws Exception {
        if (value != null && !value.equals(NULL_VALUE)) {
            if (isInteger) {
                obj.put(id, Long.valueOf(value));
            }
            else {
                obj.put(id, value);
            }
        }
    }

    /**
     * Returns date (dd.mm.yyyy hh:mm) by given time ms.
     * @param time
     * @return
     */
    private String getDateTimeByTimeInMs(String time) {
        if (time != null) {
            Calendar calendar = Utils.getLocalCalendar();
            calendar.setTimeInMillis(Long.valueOf(time));

            return calendar.get(Calendar.DATE) + DOT + (calendar.get(Calendar.MONTH) + 1) + DOT + calendar.get(Calendar.YEAR)
                    + SPACE + Utils.prependZero(calendar.get(Calendar.HOUR_OF_DAY)) + COLON + Utils.prependZero(calendar.get(Calendar.MINUTE));
        }
        else {
            return ZERO_DATE_TIME;
        }
    }

    /**
     * Returns date (dd.mm.yyyy) by given time ms.
     * @param time
     * @return
     */
    private String getDateByTimeInMs(String time) {
        if (time != null) {
            Calendar calendar = Utils.getLocalCalendar();
            calendar.setTimeInMillis(Long.valueOf(time));

            return calendar.get(Calendar.DATE) + DOT + (calendar.get(Calendar.MONTH) + 1) + DOT + calendar.get(Calendar.YEAR);
        }
        else {
            return ZERO_DATE;
        }

    }

    /**
     * Checks if given time is in the past.
     * @param time
     * @return
     */
    private boolean isPastTime(String time) {
        if (time != null) {
            Calendar now = Utils.getUtcCalendar();
            now.setTime(new Date());

            return now.getTimeInMillis() > Long.valueOf(time);
        }

        return false;
    }

    private String createLocalTimeString(String time) {
        Calendar calendar = Utils.getLocalCalendar();
        calendar.setTimeInMillis(Long.parseLong(time));

        return Utils.prependZero(calendar.get(Calendar.HOUR_OF_DAY)) + COLON + Utils.prependZero(calendar.get(Calendar.MINUTE));
    }

    private String createLocalDateString(String time) {
        Calendar calendar = Utils.getLocalCalendar();
        calendar.setTimeInMillis(Long.parseLong(time));

        return calendar.get(Calendar.DATE) + DOT + (calendar.get(Calendar.MONTH) + 1) + DOT + calendar.get(Calendar.YEAR);
    }
}
