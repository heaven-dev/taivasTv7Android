package fi.tv7.taivastv7;

import android.app.Application;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import static fi.tv7.taivastv7.helpers.Constants.LOG_TAG;

/**
 * Application class. Implements volley request queue functionality.
 */
public class TaivasTv7 extends Application {

    private static final String TAG = TaivasTv7.class.getSimpleName();

    private RequestQueue requestQueue = null;
    private static TaivasTv7 taivasTv7 = null;

    /**
     * onCreate() - Android lifecycle method.
     */
    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "TaivasTv7.onCreate() called.");
        }

        super.onCreate();
        taivasTv7 = this;
    }

    /**
     * onTerminate() - Android lifecycle method.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        if (requestQueue != null) {
            requestQueue.stop();
        }
    }

    /**
     * Returns instance of this class.
     * @return
     */
    public static synchronized TaivasTv7 getInstance() {
        return taivasTv7;
    }

    /**
     * Returns volley request queue.
     * @return
     */
    public RequestQueue getRequestQueue() {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "TaivasTv7.getRequestQueue() called.");
        }

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Adds request to volley request queue.
     * @param req
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "TaivasTv7.addToRequestQueue() called.");
        }

        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    /**
     * Cancels volley pending request.
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
