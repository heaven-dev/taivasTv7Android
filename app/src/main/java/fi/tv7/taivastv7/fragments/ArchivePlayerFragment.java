package fi.tv7.taivastv7.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fi.tv7.taivastv7.BuildConfig;
import fi.tv7.taivastv7.R;
import fi.tv7.taivastv7.adapter.NewestProgramsGridAdapter;
import fi.tv7.taivastv7.helpers.Utils;
import fi.tv7.taivastv7.interfaces.ArchiveDataLoadedListener;
import fi.tv7.taivastv7.model.ArchiveViewModel;
import fi.tv7.taivastv7.model.SharedCacheViewModel;

import static fi.tv7.taivastv7.helpers.Constants.AMPERSAND;
import static fi.tv7.taivastv7.helpers.Constants.ARCHIVE_LANGUAGE;
import static fi.tv7.taivastv7.helpers.Constants.ARCHIVE_VIDEO_URL;
import static fi.tv7.taivastv7.helpers.Constants.AUDIO_INDEX_ENABLE_LANG;
import static fi.tv7.taivastv7.helpers.Constants.AUDIO_INDEX_PARAM;
import static fi.tv7.taivastv7.helpers.Constants.CAPTION;
import static fi.tv7.taivastv7.helpers.Constants.COLON_WITH_SPACE;
import static fi.tv7.taivastv7.helpers.Constants.CUE_LINE_POSITION;
import static fi.tv7.taivastv7.helpers.Constants.DASH;
import static fi.tv7.taivastv7.helpers.Constants.EMPTY;
import static fi.tv7.taivastv7.helpers.Constants.EPISODE_NUMBER;
import static fi.tv7.taivastv7.helpers.Constants.EQUAL;
import static fi.tv7.taivastv7.helpers.Constants.ID;
import static fi.tv7.taivastv7.helpers.Constants.IS_SUBTITLE;
import static fi.tv7.taivastv7.helpers.Constants.LANG_ID;
import static fi.tv7.taivastv7.helpers.Constants.LINK_PATH;
import static fi.tv7.taivastv7.helpers.Constants.LOG_TAG;
import static fi.tv7.taivastv7.helpers.Constants.NEWEST_LIMIT;
import static fi.tv7.taivastv7.helpers.Constants.NEWEST_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.ONE_STR;
import static fi.tv7.taivastv7.helpers.Constants.PATH;
import static fi.tv7.taivastv7.helpers.Constants.PAUSE_START_ICON_ANIMATION_DURATION;
import static fi.tv7.taivastv7.helpers.Constants.PAUSE_START_ICON_ANIMATION_END;
import static fi.tv7.taivastv7.helpers.Constants.PAUSE_START_ICON_ANIMATION_START;
import static fi.tv7.taivastv7.helpers.Constants.PAUSE_START_ICON_ANIMATION_START_OFFSET;
import static fi.tv7.taivastv7.helpers.Constants.PERCENT;
import static fi.tv7.taivastv7.helpers.Constants.PNID_PARAM;
import static fi.tv7.taivastv7.helpers.Constants.POSITION;
import static fi.tv7.taivastv7.helpers.Constants.PROGRAM_INFO_FRAGMENT;
import static fi.tv7.taivastv7.helpers.Constants.QUESTION_MARK;
import static fi.tv7.taivastv7.helpers.Constants.SERIES_AND_NAME;
import static fi.tv7.taivastv7.helpers.Constants.SLASH_WITH_SPACES;
import static fi.tv7.taivastv7.helpers.Constants.STREAM_ERROR_RETRY_DELAY;
import static fi.tv7.taivastv7.helpers.Constants.SUBTITLES_URL;
import static fi.tv7.taivastv7.helpers.Constants.SUBTITLE_BACKGROUND;
import static fi.tv7.taivastv7.helpers.Constants.TRANSLATION_LANG_ID;
import static fi.tv7.taivastv7.helpers.Constants.TRANSLATION_METHOD;
import static fi.tv7.taivastv7.helpers.Constants.TV_BRAND;
import static fi.tv7.taivastv7.helpers.Constants.VIDEO_CONTROLS_TIMEOUT;
import static fi.tv7.taivastv7.helpers.Constants.VIDEO_POSITION_TIMEOUT;
import static fi.tv7.taivastv7.helpers.Constants.VIDEO_SEEK_STEP_SECONDS;
import static fi.tv7.taivastv7.helpers.Constants.VIDEO_STATUSES_SP_DEFAULT;
import static fi.tv7.taivastv7.helpers.Constants.VIDEO_STATUSES_SP_TAG;
import static fi.tv7.taivastv7.helpers.Constants.VOD_PARAM;
import static fi.tv7.taivastv7.helpers.Constants.ZERO_STR;
import static fi.tv7.taivastv7.helpers.Constants._LINK_PATH_;

/**
 * Archive player fragment. Uses the ExoPlayer to show HLS stream.
 * Home of ExoPlayer: https://github.com/google/ExoPlayer
 */
public class ArchivePlayerFragment extends Fragment implements Player.EventListener, ArchiveDataLoadedListener {

    private View root = null;

    private ArchiveViewModel archiveViewModel = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private HorizontalGridView newestProgramsScroll = null;
    private NewestProgramsGridAdapter adapter = null;
    private boolean newestProgramsLoaded = false;

    private JSONObject selectedProgram = null;

    private SimpleExoPlayer exoPlayer = null;
    private int controlsVisible = 0;
    private boolean paused = false;
    private boolean seeking = false;

    private long videoDuration = 0;
    private long videoPosition = 0;

    private RelativeLayout controls = null;
    private RelativeLayout otherVideos = null;
    private ProgressBar videoProgressBar = null;
    private TextView positionAndDurationTimes = null;

    private Timer videoProgressTimer = null;
    private Timer videoControlsTimer = null;

    /**
     * Default constructor.
     */
    public ArchivePlayerFragment() { }

    /**
     * Creates and return new instance of this fragment.
     * @return
     */
    public static ArchivePlayerFragment newInstance() {
        return new ArchivePlayerFragment();
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
                Log.d(LOG_TAG, "ArchivePlayerFragment.onCreate() called.");
            }

            archiveViewModel = ViewModelProviders.of(requireActivity()).get(ArchiveViewModel.class);
            sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.onCreate(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * onCreateView lifecycle method.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.onCreateView(): Called.");
            }

            root = inflater.inflate(R.layout.fragment_archive_player, container, false);

            controls = root.findViewById(R.id.controls);
            otherVideos = root.findViewById(R.id.otherVideos);
            videoProgressBar = root.findViewById(R.id.videoProgressBar);
            positionAndDurationTimes = root.findViewById(R.id.positionAndDurationTimes);

            this.prepareView(false);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.onCreateView(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }

        return root;
    }

    /**
     * Prepares view.
     * @param fromOtherVideo
     * @throws Exception
     */
    private void prepareView(boolean fromOtherVideo) throws Exception {
        selectedProgram = sharedCacheViewModel.getSelectedProgram();
        if (selectedProgram == null) {
            throw new Exception("Archive player page. Not selected program passed to this fragment!");
        }

        String programId = Utils.getValue(selectedProgram, ID);
        if (programId == null) {
            throw new Exception("Archive player page. Not selected program id passed to this fragment!");
        }

        Resources resources = getResources();

        TextView episode = root.findViewById(R.id.episode);
        if (episode != null) {
            String episodeNbr = Utils.getValue(selectedProgram, EPISODE_NUMBER);
            if (episodeNbr != null) {
                String text = resources.getString(R.string.episode) + COLON_WITH_SPACE + episodeNbr;
                episode.setText(text);
                episode.setVisibility(View.VISIBLE);
            }
            else {
                episode.setText(EMPTY);
                episode.setVisibility(View.GONE);
            }
        }

        TextView caption = root.findViewById(R.id.caption);
        if (caption != null) {
            String captionText = Utils.getValue(selectedProgram, CAPTION);
            if (captionText != null && captionText.length() > 0) {
                caption.setText(captionText);
                caption.setVisibility(View.VISIBLE);
            }
            else {
                caption.setText(EMPTY);
                caption.setVisibility(View.GONE);
            }
        }

        TextView seriesAndName = root.findViewById(R.id.seriesAndName);
        if (seriesAndName != null) {
            String name = Utils.getValue(selectedProgram, SERIES_AND_NAME);
            if (name != null && name.length() > 0) {
                seriesAndName.setText(name);
                seriesAndName.setVisibility(View.VISIBLE);
            }
            else {
                seriesAndName.setText(EMPTY);
                seriesAndName.setVisibility(View.GONE);
            }
        }

        if (!fromOtherVideo) {
            if (otherVideos != null) {
                ViewGroup.LayoutParams params = otherVideos.getLayoutParams();
                params.height = Utils.dpToPx(this.calculateOtherVideoItemHeight());
                otherVideos.setLayoutParams(params);
            }

            this.loadNewestPrograms();
        }

        archiveViewModel.getTranslation(programId, this);
    }

    /**
     * Starts video on exoplayer.
     * @param subtitlesUrl - subtitles URL
     * @param langId
     */
    private void startVideo(String subtitlesUrl, String langId) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.startVideo(): Called.");
            }

            this.disableScreenSaver();

            String videoUrl = getVideoUrl();

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.startVideo(): Parameters. URL: " + videoUrl + " - subtitles path: " + (subtitlesUrl != null ? subtitlesUrl : "-"));
            }

            PlayerView playerView = root.findViewById(R.id.exoPlayer);
            Context context = getContext();

            DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(context, getString(R.string.app_name)));
            HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).setLoadErrorHandlingPolicy(getErrorHandlingPolicy()).setAllowChunklessPreparation(true).createMediaSource(Uri.parse(videoUrl));
            exoPlayer = new SimpleExoPlayer.Builder(context).build();
            playerView.setPlayer(exoPlayer);

            boolean hasSubtitles = subtitlesUrl != null;

            MergingMediaSource mergedMediaSource = null;
            if (hasSubtitles) {
                Format subtitleFormat = Format.createTextSampleFormat(null, MimeTypes.TEXT_VTT, Format.NO_VALUE, langId);

                MediaSource subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(subtitlesUrl), subtitleFormat, C.TIME_UNSET);
                mergedMediaSource = new MergingMediaSource(hlsMediaSource, subtitleSource);

                SubtitleView subtitleView = playerView.getSubtitleView();
                if (subtitleView != null) {
                    CaptionStyleCompat captionStyleCompat = new CaptionStyleCompat(Color.WHITE, SUBTITLE_BACKGROUND,
                            Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_NONE, Color.TRANSPARENT, Typeface.DEFAULT);
                    subtitleView.setStyle(captionStyleCompat);

                    exoPlayer.addTextOutput(new TextOutput() {
                        @Override
                        public void onCues(List<Cue> cues) {
                            // Move subtitles little up
                            if (cues.size() > 0) {
                                List<Cue> resultCues = new ArrayList<>();
                                for (Cue cue: cues) {
                                    Cue newCue = new Cue(cue.text, cue.textAlignment, CUE_LINE_POSITION, cue.lineType,
                                            cue.lineAnchor, cue.position, cue.positionAnchor, cue.size);
                                    resultCues.add(newCue);
                                }

                                if (resultCues.size() > 0) {
                                    subtitleView.setCues(resultCues);
                                }
                            }
                        }
                    });
                }
            }

            exoPlayer.addListener(this);
            exoPlayer.prepare(hasSubtitles ? mergedMediaSource : hlsMediaSource);
            exoPlayer.setPlayWhenReady(true);

            JSONObject videoStatus = this.getVideoStatus();
            if (videoStatus != null && videoStatus.getInt(PERCENT) < 100) {
                // Start video from position
                videoPosition = videoStatus.getInt(POSITION);
                this.seekTo();
            }

            this.hideProgressBar();
            this.addVideoProgressTimer();
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.startVideo(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Stream error handling policy.
     * @return
     */
    private static LoadErrorHandlingPolicy getErrorHandlingPolicy() {
        return new LoadErrorHandlingPolicy() {

            @Override
            public long getBlacklistDurationMsFor(int dataType, long loadDurationMs, IOException exception, int errorCount) {
                return 0;
            }

            @Override
            public long getRetryDelayMsFor(int dataType, long loadDurationMs, IOException exception, int errorCount) {
                if (exception instanceof HttpDataSource.HttpDataSourceException) {
                    return STREAM_ERROR_RETRY_DELAY;
                }
                else {
                    return C.TIME_UNSET;
                }
            }

            @Override
            public int getMinimumLoadableRetryCount(int dataType) {
                return Integer.MAX_VALUE;
            }
        };
    }

    /**
     * Handles video end event.
     * @param playWhenReady
     * @param state
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int state) {
        if (state == ExoPlayer.STATE_ENDED) {
            this.toPreviousPage();
        }
    }

    /**
     * Home button pressed => to program info page.
     */
    public void onHomeButtonPressed() {
        if (controlsVisible == 1) {
            this.hideControls();
        }
        else if (controlsVisible == 2) {
            this.hideOtherVideos();
            this.hideControls();
        }

        if (seeking) {
            this.seekTo();
        }

        this.toPreviousPage();
    }

    /**
     * Handles keydown events.
     * @param keyCode
     * @param events
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent events) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): keyCode: " + keyCode);
            }

            if (exoPlayer == null) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (controlsVisible == 1) {
                    this.cancelVideoControlsTimer();

                    if (seeking) {
                        this.seekTo();
                        this.play();
                    }
                    else {
                        if (paused) {
                            this.play();
                        }
                        else {
                            this.pause();
                        }
                    }
                }
                else if (controlsVisible == 2) {
                    this.startOtherVideo();
                }
                else if (controlsVisible == 0) {
                    this.showControls();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                if (controlsVisible == 1) {
                    this.cancelVideoControlsTimer();

                    seeking = true;
                    this.pause();

                    if (videoPosition > 0) {
                        videoPosition -= VIDEO_SEEK_STEP_SECONDS;
                    }

                    if (videoPosition < 0) {
                        videoPosition = 0;
                    }

                    this.updateControls(videoPosition);
                }
                else if (controlsVisible == 2) { }
                else if (controlsVisible == 0) {
                    this.showControls();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (controlsVisible == 1) {
                    this.cancelVideoControlsTimer();

                    seeking = true;
                    this.pause();

                    if (videoPosition < videoDuration) {
                        videoPosition += VIDEO_SEEK_STEP_SECONDS;
                    }

                    if (videoPosition > videoDuration) {
                        videoPosition = videoDuration;
                    }

                    this.updateControls(videoPosition);
                }
                else if (controlsVisible == 2) { }
                else if (controlsVisible == 0) {
                    this.showControls();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (controlsVisible == 0) {
                    this.showControls();
                }
                else if (controlsVisible == 1 && newestProgramsLoaded) {
                    this.hideControls();
                    this.showOtherVideos();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (controlsVisible == 0) {
                    this.showControls();
                }
                else if (controlsVisible == 2) {
                    this.hideOtherVideos();
                    this.showControls();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_MEDIA_PAUSE: keyCode: " + keyCode);
                }

                if (!paused) {
                    if (controlsVisible == 2) {
                        this.hideOtherVideos();
                    }

                    this.showControls();
                    this.cancelVideoControlsTimer();

                    this.pause();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_MEDIA_PLAY: keyCode: " + keyCode);
                }

                if (paused) {
                    if (controlsVisible == 2) {
                        this.hideOtherVideos();
                    }

                    if (seeking) {
                        this.seekTo();
                    }

                    this.play();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_MEDIA_PLAY_PAUSE: keyCode: " + keyCode);
                }

                if (controlsVisible == 2) {
                    this.hideOtherVideos();
                }

                if (paused) {
                    if (seeking) {
                        this.seekTo();
                    }

                    this.play();
                }
                else {
                    this.showControls();
                    this.cancelVideoControlsTimer();

                    this.pause();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (controlsVisible == 1 || controlsVisible == 2) {
                    if (controlsVisible == 2) {
                        this.hideOtherVideos();
                    }

                    if (seeking) {
                        this.seekTo();
                    }

                    if (paused) {
                        this.play();
                    }
                    else {
                        this.resetFlags();
                        this.hideControls();
                    }
                }
                else if (controlsVisible == 0) {
                    this.toPreviousPage();
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }

        return false;
    }

    /**
     * onDestroyView lifecycle method.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.releasePlayer();
    }

    /**
     * onPlayerError overridden exoplayer method.
     * @param error
     */
    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (error.type == ExoPlaybackException.TYPE_SOURCE) {
            IOException cause = error.getSourceException();

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.onPlayerError(): Exception cause: " + cause);
            }

            if (cause instanceof HttpDataSource.HttpDataSourceException) {
                HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onPlayerError(): Exception message: " + httpError.getMessage());
                }

                /*
                if (httpError.type == HttpDataSource.HttpDataSourceException.TYPE_OPEN || httpError.type == HttpDataSource.HttpDataSourceException.TYPE_READ) {
                    TaivasTv7.getInstance().setErrorCode(NO_NETWORK_CONNECTION_ERROR);
                    Utils.toErrorPage(getActivity());
                }
                */
            }
        }
    }

    /**
     * Data loaded callback for subtitles of video.
     * @param jsonArray
     * @param type
     */
    @Override
    public void onArchiveDataLoaded(JSONArray jsonArray, String type) {
        String subtitlesUrl = null;
        String langId = null;

        try {
            if (type.equals(TRANSLATION_METHOD) && jsonArray != null) {
                // Check is subtitles available
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (obj == null) {
                        continue;
                    }

                    if (obj.has(LANG_ID) && obj.has(PATH) && obj.has(IS_SUBTITLE)
                            && obj.getString(IS_SUBTITLE).equals(ONE_STR)
                            && obj.getString(LANG_ID).equals(TRANSLATION_LANG_ID)) {
                        subtitlesUrl = obj.getString(PATH);
                        langId = obj.getString(LANG_ID);
                        break;
                    }
                }

                if (subtitlesUrl != null) {
                    subtitlesUrl = SUBTITLES_URL + subtitlesUrl;
                }

                this.startVideo(subtitlesUrl, langId);
            }
            else if (type.equals(NEWEST_METHOD) && jsonArray != null) {
                newestProgramsScroll = root.findViewById(R.id.newestProgramsScroll);
                newestProgramsScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (newestProgramsScroll != null) {
                            newestProgramsScroll.invalidate();
                            newestProgramsScroll.requestLayout();
                        }
                    }
                });

                newestProgramsScroll.addOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
                    @Override
                    public void onChildViewHolderSelectedAndPositioned(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                        super.onChildViewHolderSelectedAndPositioned(parent, child, position, subposition);

                        if (parent != null && child != null) {
                            int count = parent.getChildCount();

                            for(int i = 0; i < count; i++) {
                                View item = parent.getChildAt(i);
                                if (item != null) {
                                    ImageView playButton = item.findViewById(R.id.programPlayButton);
                                    if (playButton != null) {
                                        playButton.setVisibility(View.GONE);
                                    }
                                }
                            }

                            // Set play button visible on the selected item view
                            ImageView playButton = child.itemView.findViewById(R.id.programPlayButton);
                            if (playButton != null) {
                                playButton.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

                adapter = new NewestProgramsGridAdapter(getActivity(), getContext(), jsonArray);
                newestProgramsScroll.setAdapter(adapter);

                newestProgramsLoaded = jsonArray.length() > 0;
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.onArchiveDataLoaded(): Exception: " + e);
            }

            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Data loaded callback error method.
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
     * Opens selected video.
     * @throws Exception
     */
    private void startOtherVideo() throws Exception {
        this.saveVideoStatus();

        int pos = this.getSelectedPosition();
        JSONObject obj = adapter.getElementByIndex(pos);
        if (obj != null) {
            sharedCacheViewModel.resetAll();
            sharedCacheViewModel.clearPageHistory();
            sharedCacheViewModel.setSelectedProgram(obj);

            videoDuration = 0;
            videoPosition = 0;

            this.hideOtherVideos();
            this.resetFlags();
            this.hideControls();
            this.releasePlayer();

            this.prepareView(true);
        }
    }

    /**
     * Returns video status object from shared preferences.
     * @return JSONObject
     */
    private JSONObject getVideoStatus() {
        JSONObject statusItem = null;
        try {
            String value = Utils.getValue(selectedProgram, ID);
            if (value != null) {
                statusItem = Utils.getVideoStatus(Utils.stringToInt(value), getContext());
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.getVideoStatus(): Exception: " + e);
            }
        }

        return statusItem;
    }

    /**
     * Saves video watch status to shared preferences.
     */
    private void saveVideoStatus() {
        try {
            JSONArray jsonArray = Utils.getSavedPrefs(VIDEO_STATUSES_SP_TAG, VIDEO_STATUSES_SP_DEFAULT, getContext());
            if (jsonArray != null) {
                int programId = 0;
                String value = Utils.getValue(selectedProgram, ID);
                if (value != null) {
                    programId = Utils.stringToInt(value);
                }

                // Remove existing value
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (obj == null) {
                        continue;
                    }

                    value = Utils.getValue(obj, ID);
                    if (value == null) {
                        continue;
                    }

                    int id = Utils.stringToInt(value);
                    if (id == programId) {
                        jsonArray.remove(i);
                        break;
                    }
                }

                long position = exoPlayer.getContentPosition() / 1000;
                long duration = exoPlayer.getDuration() / 1000;

                int percent = 0;
                if (duration - position <= 60) {
                    percent = 100;
                }
                else {
                    percent = Math.round((float)position / (float)duration * (float)100);
                    if (percent < 0) {
                        percent = 0;
                    }
                    if (percent > 100) {
                        percent = 100;
                    }
                }

                // Add new value
                JSONObject obj = new JSONObject();
                obj.put(ID, programId);
                obj.put(POSITION, position);
                obj.put(PERCENT, percent);

                // add new object to the begin of array
                JSONArray newArray = new JSONArray();
                newArray.put(obj);

                for (int i = 0; i < jsonArray.length(); i++) {
                    newArray.put(jsonArray.get(i));
                }

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.saveVideoStatus(): Save status: " + obj.toString());
                }

                Utils.savePrefs(VIDEO_STATUSES_SP_TAG, getContext(), newArray);
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.saveVideoStatus(): Exception: " + e);
            }

            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Release the resources and opens the previous page.
     */
    private synchronized void toPreviousPage() {
        this.saveVideoStatus();

        this.releasePlayer();
        this.cancelVideoProgressTimer();
        this.cancelVideoControlsTimer();

        String page = sharedCacheViewModel.getPageFromHistory();
        if (page == null) {
            page = PROGRAM_INFO_FRAGMENT;
        }

        this.enableScreenSaver();

        Utils.toPage(page, getActivity(), true, false, null);
    }

    /**
     * Creates video progress time.
     */
    private void addVideoProgressTimer() {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchivePlayerFragment.videoProgressTimer() called.");
        }

        videoProgressTimer = new Timer();
        videoProgressTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (controlsVisible == 1 && !paused) {
                    updateControlsInMainThread();
                }
            }
        }, VIDEO_POSITION_TIMEOUT, VIDEO_POSITION_TIMEOUT);
    }

    /**
     * Cancels video progress timer.
     */
    private void cancelVideoProgressTimer() {
        if (videoProgressTimer != null) {
            videoProgressTimer.cancel();
            videoProgressTimer = null;
        }
    }

    /**
     * Runs on main thread.
     */
    private void updateControlsInMainThread() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    long pos = exoPlayer.getContentPosition() / 1000;
                    updateControls(pos);
                }
                catch(Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "Runnable.run(): Exception: " + e);
                    }
                }
            }
        });
    }

    /**
     * Creates video controls timer.
     */
    private void addVideoControlsTimer() {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchivePlayerFragment.addVideoControlsTimer() called.");
        }

        this.cancelVideoControlsTimer();

        videoControlsTimer = new Timer();
        videoControlsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (controlsVisible == 1) {
                    hideControlsInMainThread();
                }
            }
        }, VIDEO_CONTROLS_TIMEOUT, VIDEO_CONTROLS_TIMEOUT);
    }

    /**
     * Cancels video controls timer.
     */
    private void cancelVideoControlsTimer() {
        if (videoControlsTimer != null) {
            videoControlsTimer.cancel();
            videoControlsTimer = null;
        }
    }

    /**
     * Runs on main thread.
     */
    private void hideControlsInMainThread() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    resetFlags();
                    hideControls();
                }
                catch(Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "Runnable.run(): Exception: " + e);
                    }
                }
            }
        });
    }

    /**
     * Updates controls (time and progress bar).
     * @param pos
     */
    private void updateControls(long pos) {
        if (exoPlayer != null) {
            if (videoDuration == 0) {
                videoDuration = exoPlayer.getDuration() / 1000;
            }

            videoPosition = pos;

            if (videoPosition > videoDuration) {
                videoPosition = videoDuration;
            }

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.updateControls(): Video position: " + pos + " second(s)");
            }

            StringBuilder times = new StringBuilder(Utils.getTimeStampByDurationMs(String.valueOf(pos * 1000)));
            times.append(SLASH_WITH_SPACES);
            times.append(Utils.getTimeStampByDurationMs(String.valueOf(videoDuration * 1000)));

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.updateControls(): Video position/duration string: " + times.toString());
            }

            pos = Math.round((((float) pos * 100.0f) / (float) videoDuration));
            if (pos < 0) {
                pos = 0;
            }

            if (pos > 100) {
                pos = 100;
            }

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.updateControls(): Video progress bar position: " + pos);
            }

            if (videoProgressBar != null) {
                videoProgressBar.setProgress((int) pos);
            }

            if (positionAndDurationTimes != null) {
                positionAndDurationTimes.setText(times.toString());
            }
        }
    }

    /**
     * Releases exo player resources.
     */
    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    /**
     Archive video URL:
     - https://vod.tv7.fi:443/vod/_definst_/mp4:" + link_path + "/playlist.m3u8
     - query string:
     - pnid = program id
     - vod = app id (samsung, lg, android) + channel id (FI1, ET1, SV1, RU1), for example lg-FI1 or samsung-ET1
     - audioindex is 0, nebesa channel videos audioindex is always 1
     **/
    private String getVideoUrl() throws Exception {
        if (!selectedProgram.has(ID)) {
            throw new Exception("Program id value missing in archive player!");
        }

        StringBuilder url = new StringBuilder();
        url.append(ARCHIVE_VIDEO_URL.replace(_LINK_PATH_, this.getPath()));

        url.append(QUESTION_MARK);
        url.append(PNID_PARAM);
        url.append(EQUAL);
        url.append(selectedProgram.getString(ID));

        url.append(AMPERSAND);
        url.append(VOD_PARAM);
        url.append(EQUAL);
        url.append(TV_BRAND);
        url.append(DASH);
        url.append(ARCHIVE_LANGUAGE);

        url.append(AMPERSAND);
        url.append(AUDIO_INDEX_PARAM);
        url.append(EQUAL);
        url.append(ARCHIVE_LANGUAGE.equals(AUDIO_INDEX_ENABLE_LANG) ? ONE_STR : ZERO_STR);

        String urlStr = url.toString();

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchivePlayerFragment.getVideoUrl(): URL: " + urlStr);
        }

        return urlStr;
    }

    /**
     * Returns video path.
     * @return
     * @throws Exception
     */
    private String getPath() throws Exception {
        String path = null;

        if (selectedProgram.has(LINK_PATH)) {
            path = selectedProgram.getString(LINK_PATH);
        }
        else if (selectedProgram.has(PATH)) {
            path = selectedProgram.getString(PATH);
        }

        if (path == null) {
            throw new Exception("Video link missing in archive player!");
        }

        return path;
    }

    /**
     * Hides progress bar.
     */
    private void hideProgressBar() {
        ProgressBar progressBar = root.findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Shows video controls.
     */
    private void showControls() {
        if (exoPlayer != null) {
            long pos = exoPlayer.getContentPosition() / 1000;
            this.updateControls(pos);

            controlsVisible = 1;
            controls.setVisibility(View.VISIBLE);

            if (!paused) {
                this.addVideoControlsTimer();
            }
        }
    }

    /**
     * Resets seeking and paused flags.
     */
    private void resetFlags() {
        seeking = false;
        paused = false;
    }

    /**
     * Hides video controls.
     */
    private void hideControls() {
        controlsVisible = 0;

        controls.setVisibility(View.GONE);
        this.cancelVideoControlsTimer();
    }

    /**
     * Shows other videos.
     */
    private void showOtherVideos() {
        controlsVisible = 2;

        controls.setVisibility(View.GONE);
        otherVideos.setVisibility(View.VISIBLE);

        View gradientOtherVideos = root.findViewById(R.id.gradientOtherVideos);
        if (gradientOtherVideos != null) {
            gradientOtherVideos.setVisibility(View.VISIBLE);
        }

        ImageView arrowUpIcon = root.findViewById(R.id.arrowUpIcon);
        if (arrowUpIcon != null) {
            arrowUpIcon.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides other videos.
     */
    private void hideOtherVideos() {
        controlsVisible = 1;

        controls.setVisibility(View.VISIBLE);
        otherVideos.setVisibility(View.GONE);

        View gradientOtherVideos = root.findViewById(R.id.gradientOtherVideos);
        if (gradientOtherVideos != null) {
            gradientOtherVideos.setVisibility(View.GONE);
        }

        ImageView arrowUpIcon = root.findViewById(R.id.arrowUpIcon);
        if (arrowUpIcon != null) {
            arrowUpIcon.setVisibility(View.GONE);
        }
    }

    /**
     * Starts video.
     */
    private void play() {
        this.resetFlags();
        this.hideControls();
        exoPlayer.setPlayWhenReady(true);

        this.animatePauseStartIcon(false);
    }

    /**
     * Pause video.
     */
    private void pause() {
        if (!paused) {
            paused = true;
            exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());

            this.animatePauseStartIcon(true);
        }
    }

    /**
     * Seeks video to position.
     */
    private void seekTo() {
        exoPlayer.seekTo(videoPosition * 1000);
    }

    /**
     * Show, hides and animates pause/start icon.
     * @param pause
     */
    private void animatePauseStartIcon(boolean pause) {
        ImageView pauseStartIcon = root.findViewById(R.id.pauseStartIcon);
        if (pauseStartIcon != null) {
            pauseStartIcon.setImageResource(pause ? R.drawable.pause : R.drawable.start);
            pauseStartIcon.setVisibility(View.VISIBLE);

            Animation animation = new AlphaAnimation(PAUSE_START_ICON_ANIMATION_START, PAUSE_START_ICON_ANIMATION_END);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setStartOffset(PAUSE_START_ICON_ANIMATION_START_OFFSET);
            animation.setDuration(PAUSE_START_ICON_ANIMATION_DURATION);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    pauseStartIcon.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            pauseStartIcon.startAnimation(animation);
        }
    }

    /**
     * Calls load newest programs.
     */
    private void loadNewestPrograms() {
        archiveViewModel.getNewestPrograms(Utils.getTodayUtcFormattedLocalDate(), NEWEST_LIMIT, 0, this);
    }

    /**
     * Get scroll view item position.
     */
    private int getSelectedPosition() {
        if (newestProgramsScroll != null) {
            int pos = newestProgramsScroll.getSelectedPosition();
            if (pos < 0) {
                pos = 0;
            }
            return pos;
        }

        return 0;
    }

    /**
     * Calculates other video item height.
     *  - 3.2 items visible
     *  - 16:9 aspect ratio
     * @return item height in dp
     */
    private int calculateOtherVideoItemHeight() {
        float itemWidth = Utils.getScreenWidthDp() / (float)3.2;
        return Math.round((float)9 / (float)16 * (float)itemWidth);
    }

    /**
     * Enables screen saver.
     */
    private void enableScreenSaver() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Disables screen saver.
     */
    private void disableScreenSaver() {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
