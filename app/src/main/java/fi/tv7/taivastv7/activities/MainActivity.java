package fi.tv7.taivastv7.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fi.tv7.taivastv7.BuildConfig;
import fi.tv7.taivastv7.R;
import fi.tv7.taivastv7.TaivasTv7;
import fi.tv7.taivastv7.fragments.AboutFragment;
import fi.tv7.taivastv7.fragments.ArchiveMainFragment;
import fi.tv7.taivastv7.fragments.ArchivePlayerFragment;
import fi.tv7.taivastv7.fragments.CategoriesFragment;
import fi.tv7.taivastv7.fragments.ChannelInfoFragment;
import fi.tv7.taivastv7.fragments.ErrorFragment;
import fi.tv7.taivastv7.fragments.ExitFragment;
import fi.tv7.taivastv7.fragments.FavoritesFragment;
import fi.tv7.taivastv7.fragments.GuideFragment;
import fi.tv7.taivastv7.fragments.ProgramInfoFragment;
import fi.tv7.taivastv7.fragments.SearchFragment;
import fi.tv7.taivastv7.fragments.SearchResultFragment;
import fi.tv7.taivastv7.fragments.SeriesFragment;
import fi.tv7.taivastv7.fragments.SeriesInfoFragment;
import fi.tv7.taivastv7.fragments.TvMainFragment;
import fi.tv7.taivastv7.fragments.TvPlayerFragment;
import fi.tv7.taivastv7.helpers.Utils;
import fi.tv7.taivastv7.interfaces.ArchiveDataLoadedListener;
import fi.tv7.taivastv7.model.ArchiveViewModel;
import fi.tv7.taivastv7.model.GuideViewModel;

import static fi.tv7.taivastv7.helpers.Constants.DATE_INDEX;
import static fi.tv7.taivastv7.helpers.Constants.EXIT_OVERLAY_FRAGMENT;
import static fi.tv7.taivastv7.helpers.Constants.GUIDE_DATA;
import static fi.tv7.taivastv7.helpers.Constants.LOG_TAG;
import static fi.tv7.taivastv7.helpers.Constants.TV_MAIN_FRAGMENT;
import static fi.tv7.taivastv7.helpers.Constants.PROGRESS_BAR_SIZE;

/**
 * Main activity.
 *  - Load epg data, shows logo and progressbar.
 *  - Opens main fragment.
 *  - Handles keydown events.
 */
public class MainActivity extends AppCompatActivity implements ArchiveDataLoadedListener {

    private FragmentManager fragmentManager = null;
    private ArchiveViewModel archiveViewModel = null;
    private GuideViewModel guideViewModel = null;

    private ArchiveDataLoadedListener guideDataLoadedListener = null;

    private View fragmentContainer = null;
    private ImageView startupLogo = null;
    private ProgressBar progressBar = null;

    /**
     * onCreate() - Android lifecycle method.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onCreate() called.");
            }

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            fragmentManager = Utils.getFragmentManager(this);

            archiveViewModel = ViewModelProviders.of(this).get(ArchiveViewModel.class);
            guideViewModel = ViewModelProviders.of(this).get(GuideViewModel.class);

            this.setGuideByDateDataLoadedListener(this);

            setContentView(R.layout.activity_main);

            fragmentContainer = findViewById(R.id.fragment_container);
            startupLogo = findViewById(R.id.startupLogo);

            progressBar = findViewById(R.id.startupProgressBar);
            progressBar.setScaleY(PROGRESS_BAR_SIZE);
            progressBar.setScaleX(PROGRESS_BAR_SIZE);

            TaivasTv7.getInstance().setActivity(this);

            archiveViewModel.getGuideByDate(Utils.getTodayUtcFormattedLocalDate(), 0, this);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onCreate(): Exception: " + e);
            }
            Utils.toErrorPage(this);
        }
    }

    /**
     * Handles key down events and sends events to visible fragment.
     * @param keyCode
     * @param events
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent events) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onKeyDown(): keyCode: " + keyCode);
            }

            Fragment fragment = this.getVisibleFragment();
            if (fragment != null) {
                if (fragment instanceof TvMainFragment) {
                    // TV main fragment visible
                    return ((TvMainFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof ArchiveMainFragment) {
                    // Archive main fragment visible
                    return ((ArchiveMainFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof TvPlayerFragment) {
                    // Video player fragment visible
                    return ((TvPlayerFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof ArchivePlayerFragment) {
                    // Archive player fragment visible
                    return ((ArchivePlayerFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof ProgramInfoFragment) {
                    // Program info fragment visible
                    return ((ProgramInfoFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof SeriesInfoFragment) {
                    // Series info fragment visible
                    return ((SeriesInfoFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof CategoriesFragment) {
                    // Categories fragment visible
                    return ((CategoriesFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof SeriesFragment) {
                    // Series fragment visible
                    return ((SeriesFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof GuideFragment) {
                    // Guide fragment visible
                    return ((GuideFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof SearchFragment) {
                    // Search fragment visible
                    return ((SearchFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof SearchResultFragment) {
                    // Search result fragment visible
                    return ((SearchResultFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof FavoritesFragment) {
                    // Favorites fragment visible
                    return ((FavoritesFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof ChannelInfoFragment) {
                    // Channel info fragment visible
                    return ((ChannelInfoFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof AboutFragment) {
                    // About fragment visible
                    return ((AboutFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof ErrorFragment) {
                    // Error fragment visible
                    return ((ErrorFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof ExitFragment) {
                    // Exit overlay fragment visible
                    return ((ExitFragment) fragment).onKeyDown(keyCode, events);
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onKeyDown(): Exception: " + e);
            }
            Utils.toErrorPage(this);
        }

        return super.onKeyDown(keyCode, events);
    }

    /**
     * Remote home button pressed. If playback ongoing - stop it.
     */
    @Override
    public void onUserLeaveHint()
    {
        super.onUserLeaveHint();

        Fragment fragment = this.getVisibleFragment();
        if (fragment != null) {
            if (fragment instanceof TvPlayerFragment) {
                ((TvPlayerFragment) fragment).onHomeButtonPressed();
            }
            else if (fragment instanceof ArchivePlayerFragment) {
                // Archive player fragment visible
                ((ArchivePlayerFragment) fragment).onHomeButtonPressed();
            }
        }
    }

    /**
     * Callback to success guide by date load.
     */
    @Override
    public void onArchiveDataLoaded(JSONArray jsonArray, String type) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onArchiveDataLoaded(): EpgData load/parse ok.");
            }

            if (jsonArray != null && jsonArray.length() == 1) {
                JSONObject obj = jsonArray.getJSONObject(0);
                if (obj != null) {
                    JSONArray guideData = obj.getJSONArray(GUIDE_DATA);
                    if (obj.getInt(DATE_INDEX) == 0) {
                        this.addGuideData(guideData);

                        archiveViewModel.getGuideByDate(Utils.getTomorrowUtcFormattedLocalDate(), 1, this);
                    }
                    else {
                        this.addGuideData(guideData);

                        archiveViewModel.addGuideData(guideViewModel.getGuide(), true);

                        this.prepareUi();
                        Utils.toPage(TV_MAIN_FRAGMENT, this, false, false, null);
                    }
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onArchiveDataLoaded(): Exception: " + e);
            }

            Utils.toErrorPage(this);
        }
    }

    /**
     * Callback to error guide by date load.
     */
    @Override
    public void onArchiveDataLoadError(String message, String type) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "MainActivity.onArchiveDataLoadError(): EpgData load/parse error: " + message);
        }

        this.prepareUi();

        Utils.toErrorPage(this);
    }

    /**
     * Callback to network error guide by date load.
     */
    @Override
    public void onNetworkError(String type) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "MainActivity.onNetworkError(): ***Network error!***");
        }

        this.prepareUi();

        Utils.toErrorPage(this);
    }

    /**
     * Adds guide data to view model.
     * @param guideData
     * @throws Exception
     */
    private void addGuideData(JSONArray guideData) throws  Exception {
        if (guideData == null) {
            Utils.toErrorPage(this);
        }

        for (int i = 0; i < guideData.length(); i++) {
            JSONObject obj = guideData.getJSONObject(i);
            if (obj != null) {
                guideViewModel.addItemToGuide(Utils.getGuideItemByJsonObj(obj));
            }
        }
    }

    /**
     * Hides logo and progressbar. Shows fragment container.
     */
    private void prepareUi() {
        startupLogo.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Returns visible fragment.
     * @return
     */
    private Fragment getVisibleFragment(){
        // Possible multiple fragments visible because exit overlay fragment
        List<Fragment> visibleFragments = new ArrayList<>();

        if (fragmentManager != null) {
            List<Fragment> fragments = fragmentManager.getFragments();
            if(fragments != null) {
                for(Fragment fragment : fragments){
                    if(fragment != null && fragment.isVisible())
                        visibleFragments.add(fragment);
                }
            }

            Fragment visibleFragment = null;
            if (visibleFragments.size() > 1) {
                visibleFragment = fragmentManager.findFragmentByTag(EXIT_OVERLAY_FRAGMENT);
            }

            if (visibleFragment != null) {
                // return exit overlay fragment
                return visibleFragment;
            }
        }

        return visibleFragments.size() > 0 ? visibleFragments.get(0) : null;
    }

    /**
     * Creates epg data load listener.
     * @param guideDataLoadedListener
     */
    private void setGuideByDateDataLoadedListener(ArchiveDataLoadedListener guideDataLoadedListener) {
        this.guideDataLoadedListener = guideDataLoadedListener;
    }
}
