package fi.tv7.taivastv7.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import fi.tv7.taivastv7.R;
import fi.tv7.taivastv7.helpers.Utils;

import static fi.tv7.taivastv7.helpers.Constants.BROADCAST_DATE_TIME;
import static fi.tv7.taivastv7.helpers.Constants.COLON_WITH_SPACE;
import static fi.tv7.taivastv7.helpers.Constants.DURATION;
import static fi.tv7.taivastv7.helpers.Constants.EMPTY;
import static fi.tv7.taivastv7.helpers.Constants.EPISODE_NUMBER;
import static fi.tv7.taivastv7.helpers.Constants.ID_NULL;
import static fi.tv7.taivastv7.helpers.Constants.IMAGE_PATH;
import static fi.tv7.taivastv7.helpers.Constants.NULL_VALUE;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_AND_NAME;

/**
 * Grid adapter for series programs.
 */
public class SeriesGridAdapter extends RecyclerView.Adapter<SeriesGridAdapter.SimpleViewHolder> {

    private FragmentActivity activity = null;
    private Context context = null;
    private JSONArray elements = null;

    public SeriesGridAdapter(FragmentActivity activity, Context context, JSONArray jsonArray) {
        this.activity = activity;
        this.context = context;
        this.elements = jsonArray;
    }

    public void addElements(JSONArray jsonArray) throws Exception {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if (obj != null && elements != null) {
                elements.put(obj);
            }
        }
    }

    public JSONObject getElementByIndex(int index) throws Exception {
        if (elements != null && elements.length() > index) {
            return elements.getJSONObject(index);
        }
        return null;
    }

    public JSONArray getElements() {
        return elements;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout seriesContainer = null;
        public ImageView seriesImage = null;
        public TextView seriesAndName = null;
        public TextView firstBroadcast = null;
        public TextView duration = null;
        public TextView episode = null;

        public SimpleViewHolder(View view) {
            super(view);

            seriesContainer = view.findViewById(R.id.seriesContainer);
            seriesImage = view.findViewById(R.id.seriesImage);
            seriesAndName = view.findViewById(R.id.seriesAndName);
            firstBroadcast = view.findViewById(R.id.firstBroadcast);
            duration = view.findViewById(R.id.duration);
            episode = view.findViewById(R.id.episode);

            // Calculate and set item height
            int itemHeight = Utils.dpToPx(calculateItemHeight());

            if (seriesContainer != null) {
                ViewGroup.LayoutParams params = seriesContainer.getLayoutParams();
                params.height = itemHeight;
                seriesContainer.setLayoutParams(params);
            }

            // Calculate and set image width
            int imageWidth = Utils.dpToPx(calculateImageWidth());

            if (seriesImage != null) {
                ViewGroup.LayoutParams params = seriesImage.getLayoutParams();
                params.width = imageWidth;
                seriesImage.setLayoutParams(params);
            }
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.series_element, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        try {
            JSONObject obj = elements.getJSONObject(position);
            if (obj != null) {

                String value = Utils.getJsonStringValue(obj, IMAGE_PATH);
                if (value != null && !value.equals(EMPTY) && !value.equals(NULL_VALUE) && !value.contains(ID_NULL)) {
                    Glide.with(context).asBitmap().load(value).into(holder.seriesImage);
                }
                else {
                    Glide.with(context).asBitmap().load(R.drawable.fallback).into(holder.seriesImage);
                }

                value = Utils.getJsonStringValue(obj, SERIES_AND_NAME);
                if (value != null) {
                    holder.seriesAndName.setText(value);
                }

                Resources resources = context.getResources();

                String text = null;
                value = Utils.getJsonStringValue(obj, BROADCAST_DATE_TIME);
                if (value != null) {
                    text = resources.getString(R.string.first_broadcast);
                    text += (COLON_WITH_SPACE + value);

                    holder.firstBroadcast.setText(text);
                }

                value = Utils.getJsonStringValue(obj, DURATION);
                if (value != null) {
                    text = resources.getString(R.string.duration);
                    text += (COLON_WITH_SPACE + value);

                    holder.duration.setText(text);
                }

                value = Utils.getJsonStringValue(obj, EPISODE_NUMBER);
                if (value != null) {
                    text = resources.getString(R.string.episode);
                    text += (COLON_WITH_SPACE + value);

                    holder.episode.setText(text);
                }
            }
        }
        catch (Exception e) {
            Utils.toErrorPage(activity);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.elements.length();
    }

    private static double calculateItemHeight() {
        float width = Utils.getScreenHeightDp() - 124;
        return Math.floor(width / 3.5);
    }

    private static double calculateImageWidth() {
        double height = calculateItemHeight();
        return Math.round(height / 0.56);
    }
}
