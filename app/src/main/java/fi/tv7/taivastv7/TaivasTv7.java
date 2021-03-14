package fi.tv7.taivastv7;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import static fi.tv7.taivastv7.helpers.Constants.LOG_TAG;
import static fi.tv7.taivastv7.helpers.Constants.NETWORK_REQUEST_FAILED_ERROR;
import static fi.tv7.taivastv7.helpers.Constants.NETWORK_REQUEST_TIMEOUT_ERROR;
import static fi.tv7.taivastv7.helpers.Constants.NO_NETWORK_CONNECTION_ERROR;
import static fi.tv7.taivastv7.helpers.Constants.VOLLEY_CACHE;

/**
 * Application class. Implements volley request queue functionality.
 */
public class TaivasTv7 extends Application {

    private static final String TAG = TaivasTv7.class.getSimpleName();

    private RequestQueue requestQueue = null;
    private static TaivasTv7 taivasTv7 = null;
    private int errorCode = 0;
    private Activity activity = null;

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

        req.setShouldCache(VOLLEY_CACHE);
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

    /**
     * Returns error string.
     * @return
     */
    public String getErrorString() {
        if (errorCode == NO_NETWORK_CONNECTION_ERROR) {
            return getResources().getString(R.string.no_network_connection);
        }
        else if (errorCode == NETWORK_REQUEST_FAILED_ERROR) {
            return getResources().getString(R.string.network_request_failed);
        }
        else if (errorCode == NETWORK_REQUEST_TIMEOUT_ERROR) {
            return getResources().getString(R.string.network_request_timeout);
        }
        else {
            return getResources().getString(R.string.something_went_wrong);
        }
    }

    /**
     * Sets error code.
     * @param errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Returns activity.
     * @return
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets activity.
     * @param value
     */
    public void setActivity(Activity value) {
        activity = value;
    }
}
