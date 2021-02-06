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

import static fi.tv7.taivastv7.helpers.Constants.CAPTION;
import static fi.tv7.taivastv7.helpers.Constants.EMPTY;
import static fi.tv7.taivastv7.helpers.Constants.IMAGE_PATH;
import static fi.tv7.taivastv7.helpers.Constants.SERIES;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_AND_NAME;
import static fi.tv7.taivastv7.helpers.Constants.TYPE;

/**
 * Grid adapter for favorite items.
 */
public class FavoritesGridAdapter extends RecyclerView.Adapter<FavoritesGridAdapter.SimpleViewHolder> {

    private Context context = null;
    private JSONArray elements = null;

    public FavoritesGridAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
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
        public RelativeLayout favoriteContainer = null;
        public ImageView favoriteImage = null;
        public TextView seriesAndName = null;
        public TextView caption = null;

        public SimpleViewHolder(View view) {
            super(view);

            favoriteContainer = view.findViewById(R.id.favoriteContainer);
            favoriteImage = view.findViewById(R.id.favoriteImage);
            seriesAndName = view.findViewById(R.id.seriesAndName);
            caption = view.findViewById(R.id.caption);

            // Calculate and set item height
            int itemHeight = Utils.dpToPx(calculateItemHeight());

            if (favoriteContainer != null) {
                ViewGroup.LayoutParams params = favoriteContainer.getLayoutParams();
                params.height = itemHeight;
                favoriteContainer.setLayoutParams(params);
            }

            // Calculate and set image width
            int imageWidth = Utils.dpToPx(calculateImageWidth());

            if (favoriteImage != null) {
                ViewGroup.LayoutParams params = favoriteImage.getLayoutParams();
                params.width = imageWidth;
                favoriteImage.setLayoutParams(params);
            }
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.favorite_element, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        try {
            JSONObject obj = elements.getJSONObject(position);
            if (obj != null) {

                String value = Utils.getValue(obj, IMAGE_PATH);
                if (value != null) {
                    Glide.with(context).asBitmap().load(value).into(holder.favoriteImage);
                }
                else {
                    Glide.with(context).asBitmap().load(R.drawable.tv7_app_icon).into(holder.favoriteImage);
                }

                int imageSrc = 0;
                value = Utils.getValue(obj, TYPE);
                if (value != null) {
                    imageSrc = value.equals(SERIES) ? R.drawable.series : R.drawable.program;
                }

                value = Utils.getValue(obj, SERIES_AND_NAME);
                if (value != null) {
                    holder.seriesAndName.setText(value);
                }

                value = Utils.getValue(obj, CAPTION);
                if (value != null && value.length() > 0) {
                    holder.caption.setText(value);
                }
                else {
                    holder.caption.setText(EMPTY);
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

    private static double calculateItemHeight() {
        float width = Utils.getScreenHeightDp() - 124;
        return Math.floor(width / 3.5);
    }

    private static double calculateImageWidth() {
        double height = calculateItemHeight();
        return Math.round(height / 0.56);
    }
}
