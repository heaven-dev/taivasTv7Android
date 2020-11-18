package fi.tv7.taivastv7.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import fi.tv7.taivastv7.R;
import fi.tv7.taivastv7.helpers.Utils;

import static fi.tv7.taivastv7.helpers.Constants.BROADCAST_DATE_TIME;
import static fi.tv7.taivastv7.helpers.Constants.DURATION;
import static fi.tv7.taivastv7.helpers.Constants.IMAGE_PATH;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_AND_NAME;

/**
 * Grid adapter for archive main programs.
 */
public class ArchiveMainProgramGridAdapter extends RecyclerView.Adapter<ArchiveMainProgramGridAdapter.SimpleViewHolder> {

    private Context context = null;
    private JSONArray elements = null;

    public ArchiveMainProgramGridAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.elements = jsonArray;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout programContainer = null;
        public ImageView programImage = null;
        public TextView programText = null;
        public TextView programDateTimeText = null;
        public TextView programDurationText = null;

        public SimpleViewHolder(View view) {
            super(view);

            programContainer = view.findViewById(R.id.mainArchiveProgramContainer);
            programImage = view.findViewById(R.id.mainArchiveProgramImage);
            programText = view.findViewById(R.id.mainArchiveProgramText);
            programDateTimeText = view.findViewById(R.id.mainArchiveProgramDateTime);
            programDurationText = view.findViewById(R.id.mainArchiveProgramDuration);

            // Calculate and set item width
            int itemWidth = Utils.dpToPx(calculateItemWidth());

            if (programContainer != null) {
                ViewGroup.LayoutParams params = programContainer.getLayoutParams();
                params.width = itemWidth;
                programContainer.setLayoutParams(params);
            }
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.archive_main_grid_program_element, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        try {
            JSONObject obj = elements.getJSONObject(position);
            if (obj != null) {
                String imagePath = Utils.getValue(obj, IMAGE_PATH);
                if (imagePath != null) {
                    Glide.with(context).asBitmap().load(imagePath).into(holder.programImage);
                }
                else {
                    Glide.with(context).asBitmap().load(R.drawable.tv7_app_icon).into(holder.programImage);
                }

                String dateTime = Utils.getValue(obj, BROADCAST_DATE_TIME);
                if (dateTime != null) {
                    holder.programDateTimeText.setText(dateTime);
                }

                String duration = Utils.getValue(obj, DURATION);
                if (duration != null) {
                    holder.programDurationText.setText(duration);
                }

                String seriesAndName = Utils.getValue(obj, SERIES_AND_NAME);
                if (seriesAndName != null) {
                    holder.programText.setText(seriesAndName);
                }
            }
        }
        catch (Exception e) {
            Utils.showErrorToast(context, context.getResources().getString(R.string.toast_something_went_wrong));
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

    private static double calculateItemWidth() {
        float width = Utils.getScreenWidthDp() - 82;
        return Math.floor(width / 3.2);
    }
}