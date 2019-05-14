package id.sentuh.digitalsignage.fragment;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.io.File;
import java.text.DecimalFormat;

import id.sentuh.digitalsignage.R;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by sony on 2/18/2018.
 */

public class VideoDialog extends Dialog implements VideoRendererEventListener {
    private static final String TAG = "Video Dialog";
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    Handler mHandler;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
//    PlayerControl playerControl;
//    OnDialogListener mListener;
    private Uri mp4VideoUri;
    private Context mContext;
    double scaleWidth = 0.0f;
    int screen_width = 0, screen_height = 0;
    String rotation="0.0";
    int video_width;
    public VideoDialog(@NonNull Context context, String mParam1,int width,int height) {
        super(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.mContext = context;
        this.mParam1 = mParam1;
        File file = new File(mParam1);
        if(file.exists()){
            mp4VideoUri = Uri.fromFile(file);
            this.screen_width = width;

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(file.getAbsolutePath());
            int vwidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int vheight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            FFmpegMediaMetadataRetriever fmmr = new FFmpegMediaMetadataRetriever();
            fmmr.setDataSource(file.getAbsolutePath());
            rotation = fmmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            Log.d("Rotation", rotation);
            float fRotation = Float.parseFloat(rotation);

            if(rotation.equals("90")){
                scaleWidth = 1.777778d;
                double result = width * scaleWidth;
                video_width = (int)Math.round(result);
                this.screen_height = height;
            } else {
                scaleWidth = (double) width/vwidth;
                double result = vheight / scaleWidth;
                this.screen_height = (int)Math.round(result);
            }


//        DecimalFormat df = new DecimalFormat("#,###.###");

            retriever.release();
        } else {
            Log.d("Vd Dialog","File not found");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);


//        getWindow().setLayout(width, height);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.gravity = Gravity.CENTER;
//        getWindow().setAttributes(lp);
        setContentView(R.layout.video_dialog);
        getWindow().setLayout(screen_width, screen_height);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        LoadControl loadControl = new DefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
        //simpleExoPlayerView = new SimpleExoPlayerView(mContext);
        simpleExoPlayerView = findViewById(R.id.video_screen);
//
        //Set media controller
//
        simpleExoPlayerView.setUseController(false);
        simpleExoPlayerView.requestFocus();
//        if(rotation.equals("90")){
//            player.setVideoSurfaceView((SurfaceView)simpleExoPlayerView.getVideoSurfaceView());
//        }
        simpleExoPlayerView.setPlayer(player);
        if(rotation.equals("90")){
            float fRotation = Float.parseFloat(rotation);

            simpleExoPlayerView.setRotation(fRotation);
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        } else {
            ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
        this.addContentView(simpleExoPlayerView,new ViewGroup.LayoutParams(
                screen_width, screen_height));
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        }

        player.setRepeatMode(player.REPEAT_MODE_OFF);
        //prepareExoPlayerFromFileUri(mp4VideoUri,trackSelector);
        DataSpec dataSpec = new DataSpec(mp4VideoUri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }
        DataSource.Factory dataSourceFactory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mp4VideoUri);

        player.prepare(videoSource);
        player.addListener(exoPlayerListener);
        player.setPlayWhenReady(true);
        player.setVideoDebugListener(this);

    }


    private ExoPlayer.EventListener exoPlayerListener = new ExoPlayer.EventListener() {

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest,int reason) {
            Log.v(TAG, "Listener-onTimelineChanged...");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.v(TAG, "Listener-onTracksChanged...");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.v(TAG, "Listener-onLoadingChanged...isLoading:"+isLoading);

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if(playbackState==ExoPlayer.STATE_READY){

            }
            if(playbackState==ExoPlayer.STATE_ENDED){

                dismiss();
            }
            Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            Log.v(TAG, "Listener-onRepeatModeChanged...");
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.v(TAG, "Listener-onPlayerError...");
            player.stop();

        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }


        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            Log.v(TAG, "Listener-onPlaybackParametersChanged...");
        }

        @Override
        public void onSeekProcessed() {

        }
    };
    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        DecimalFormat df = new DecimalFormat("#,###.#####");
        Log.d(TAG,"video size : "+Integer.toString(width)+","+
                Integer.toString(height)+" ration : "+df.format(pixelWidthHeightRatio));

    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }

    @Override
    public void dismiss() {

        player.stop();
        player.release();
//        mListener.OnDialogClose(true);
        super.dismiss();
    }
}
