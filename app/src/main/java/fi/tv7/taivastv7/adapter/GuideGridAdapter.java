package fi.tv7.taivastv7.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import fi.tv7.taivastv7.R;
import fi.tv7.taivastv7.helpers.Utils;

import static fi.tv7.taivastv7.helpers.Constants.CAPTION;
import static fi.tv7.taivastv7.helpers.Constants.EMPTY;
import static fi.tv7.taivastv7.helpers.Constants.ID_NULL;
import static fi.tv7.taivastv7.helpers.Constants.IMAGE_PATH;
import static fi.tv7.taivastv7.helpers.Constants.NULL_VALUE;
import static fi.tv7.taivastv7.helpers.Constants.ONE_STR;
import static fi.tv7.taivastv7.helpers.Constants.ONGOING_PROGRAM;
import static fi.tv7.taivastv7.helpers.Constants.ONGOING_PROGRAM_ANIMATION_DURATION;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_AND_NAME;
import static fi.tv7.taivastv7.helpers.Constants.START_END_TIME;

/**
 * Grid adapter for guide items.
 */
public class GuideGridAdapter extends RecyclerView.Adapter<GuideGridAdapter.SimpleViewHolder> {

    private FragmentActivity activity = null;
    private Context context = null;
    private Resources resources = null;
    private JSONArray elements = null;

    public GuideGridAdapter(FragmentActivity activity, Context context, Resources resources, JSONArray jsonArray) {
        this.activity = activity;
        this.context = context;
        this.resources = resources;
        this.elements = jsonArray;
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
        public RelativeLayout guideContainer = null;
        public ImageView guideImage = null;
        public TextView startEndTime = null;
        public TextView seriesAndName = null;
        public TextView caption = null;
        public ImageView ongoingProgram = null;

        public SimpleViewHolder(View view) {
            super(view);

            guideContainer = view.findViewById(R.id.guideContainer);
            guideImage = view.findViewById(R.id.guideImage);
            startEndTime = view.findViewById(R.id.startEndTime);
            seriesAndName = view.findViewById(R.id.seriesAndName);
            caption = view.findViewById(R.id.caption);
            ongoingProgram = view.findViewById(R.id.ongoingProgram);

            // Calculate and set item height
            int itemHeight = Utils.dpToPx(calculateItemHeight());

            if (guideContainer != null) {
                ViewGroup.LayoutParams params = guideContainer.getLayoutParams();
                params.height = itemHeight;
                guideContainer.setLayoutParams(params);
            }

            // Calculate and set image width
            int imageWidth = Utils.dpToPx(calculateImageWidth());

            if (guideImage != null) {
                ViewGroup.LayoutParams params = guideImage.getLayoutParams();
                params.width = imageWidth;
                guideImage.setLayoutParams(params);
            }
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.guide_element, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        try {
            JSONObject obj = elements.getJSONObject(position);
            if (obj != null) {
                holder.ongoingProgram.setVisibility(View.GONE);

                String value = Utils.getJsonStringValue(obj, IMAGE_PATH);
                if (value != null && !value.equals(EMPTY) && !value.equals(NULL_VALUE) && !value.contains(ID_NULL)) {
                    Glide.with(context).asBitmap().load(value).into(holder.guideImage);
                }
                else {
                    Glide.with(context).asBitmap().load(R.drawable.fallback).into(holder.guideImage);
                }

                value = Utils.getJsonStringValue(obj, SERIES_AND_NAME);
                if (value != null) {
                    holder.seriesAndName.setText(value);
                }

                value = Utils.getJsonStringValue(obj, START_END_TIME);
                if (value != null) {
                    holder.startEndTime.setText(value);
                }

                value = Utils.getJsonStringValue(obj, ONGOING_PROGRAM);
                if (value != null && value.equals(ONE_STR)) {
                    holder.ongoingProgram.setVisibility(View.VISIBLE);

                    GradientDrawable gdb = (GradientDrawable) holder.ongoingProgram.getBackground();

                    int startColor = ResourcesCompat.getColor(resources, R.color.ongoing_program_icon_bg_start, null);
                    int endColor = ResourcesCompat.getColor(resources, R.color.ongoing_program_icon_bg_end, null);

                    ValueAnimator animation = ValueAnimator.ofArgb(startColor, endColor);
                    animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            if (gdb != null) {
                                gdb.setColor((int)valueAnimator.getAnimatedValue());
                            }
                        }
                    });

                    animation.setDuration(ONGOING_PROGRAM_ANIMATION_DURATION);
                    animation.setRepeatCount(Animation.INFINITE);
                    animation.setRepeatMode(ValueAnimator.REVERSE);
                    animation.start();
                }

                value = Utils.getJsonStringValue(obj, CAPTION);
                if (value != null && value.length() > 0) {
                    holder.caption.setText(value);
                }
                else {
                    holder.caption.setText(EMPTY);
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
        float width = Utils.getScreenHeightDp() - 150;
        return Math.floor(width / 3.5);
    }

    private static double calculateImageWidth() {
        double height = calculateItemHeight();
        return Math.round(height / 0.56);
    }
}
