package id.sentuh.digitalsignage.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
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
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.adapter.PlayerControl;
import wseemann.media.FFmpegMediaMetadataRetriever;

public final class VideoPlayListFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_INDEX = "page_index";
    private static final String PAGER_INDEX = "index_pager";
    private static final String VIDEO_LOOPING = "video_looping";
    private static final String TAG = "Player";

    private List<String> mParam1;
    private SimpleExoPlayerView simpleExoPlayerView;
    private Context mContext;
    private int mIndexPage;
    private int mPagerIndex;
    private long mDuration;
    private boolean isLooping;
    //    OnPageFinishListener mListener;
    ExoPlayer player;
    //    Handler mHandler;
    PlayerControl playerControl;
    DataSource.Factory dataSourceFactory;
    private DataSource.Factory manifestDataSourceFactory;
    private DataSource.Factory mediaDataSourceFactory;
    //    List<MediaSource> videoSource;
    MediaSource[] mediaSources;
    MediaSource mediaSource;
    //    private List<Uri> mp4VideoUri;
//    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//    LoadControl loadControl ;
//    TrackSelection.Factory videoTrackSelectionFactory;
//    TrackSelector trackSelector;
//    RtmpDataSourceFactory rtmpDataSourceFactory;
    int currIndex;
    int maxIndex;
    long realDurationMillis;
    int dimWidth;
    int dimHeight;
    boolean isStop=false;
    public VideoPlayListFragment() {
        // Required empty public constructor
    }

    public static VideoPlayListFragment newInstance(ArrayList<String> param1, int index, int pagerIndex, boolean isLoop) {
        VideoPlayListFragment fragment = new VideoPlayListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM1, param1);
        args.putInt(ARG_INDEX,index);
        args.putInt(PAGER_INDEX,pagerIndex);
        args.putBoolean(VIDEO_LOOPING,isLoop);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        manifestDataSourceFactory =
                new DefaultDataSourceFactory(
                        mContext, Util.getUserAgent(mContext, mContext.getString(R.string.application_name)));
        mediaDataSourceFactory =
                new DefaultDataSourceFactory(
                        mContext,
                        Util.getUserAgent(mContext, mContext.getString(R.string.application_name)),
                        new DefaultBandwidthMeter());
        if (getArguments() != null) {
            mParam1 = getArguments().getStringArrayList(ARG_PARAM1);
            mIndexPage = getArguments().getInt(ARG_INDEX);
            mPagerIndex = getArguments().getInt(PAGER_INDEX);
            isLooping = getArguments().getBoolean(VIDEO_LOOPING);
            maxIndex = mParam1.size() - 1;
            mediaSources = new MediaSource[mParam1.size()];
            realDurationMillis =0;
            for (int i = 0; i < mParam1.size(); i++) {
                mediaSources[i] = buildMediaSource(Uri.parse(mParam1.get(i)));

            }

        }

        currIndex = 0;
        mContext = getContext();
//        mListener = (OnPageFinishListener) getContext();
//        mHandler=new Handler();

        setUserVisibleHint(false);
//        DefaultAllocator allocation = new DefaultAllocator(true,2048000);
//        loadControl = new CustomLoadControl(allocation);

    }
    private MediaSource buildMediaSource(Uri uri) {
        @C.ContentType int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        manifestDataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), manifestDataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }
    private void loadVideoFile(String filename){
        File video_source_file = new File(filename);
        Uri mp4VideoUri = Uri.fromFile(video_source_file);
        DataSpec dataSpec = new DataSpec(mp4VideoUri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }
        dataSourceFactory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //videoView.setVideoURI(Uri.parse(mParam1));
//        player.setPlayWhenReady(true); //run file/link when ready to play.

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_video, container, false);
        simpleExoPlayerView = view.findViewById(R.id.video_view);
        simpleExoPlayerView.setControllerAutoShow(false);
        simpleExoPlayerView.setShutterBackgroundColor(Color.TRANSPARENT);
        simpleExoPlayerView.setKeepScreenOn(true);
        simpleExoPlayerView.setKeepContentOnPlayerReset(true);
        simpleExoPlayerView.setUseController(false);
        simpleExoPlayerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                player.prepare(mediaSource);
                player.seekTo(currIndex++, C.TIME_UNSET);
                if(currIndex>maxIndex){
                    currIndex = 0;
                    player.seekTo(currIndex, C.TIME_UNSET);
                }
                player.setPlayWhenReady(true);
                return false;
            }
        });
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // Create a player instance.
        player = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        simpleExoPlayerView.setPlayer(player);
//        simpleExoPlayerView.setLayerType(View.LAYER_TYPE_HARDWARE,null);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                simpleExoPlayerView.post(new Runnable() {
                    public void run() {
                        dimHeight=view.getHeight(); //height is ready
                        dimWidth = view.getWidth();
                        Log.d(TAG,"dimension fragment "+Integer.toString(dimWidth)+","+Integer.toString(dimHeight));
                    }
                });
            }
        });
        mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(true,mediaSources);
        player.setPlayWhenReady(false);

        if(isLooping) {
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
        }
        player.prepare(mediaSource);
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case ExoPlayer.STATE_IDLE:
                        Log.d(TAG,"status video : IDLE , index : "+Integer.toString(currIndex));
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        Log.d(TAG,"status video : BUFFERING , index : "+Integer.toString(currIndex));
                        break;
                    case ExoPlayer.STATE_READY:
                        Log.d(TAG,"status video : READY , index : "+Integer.toString(currIndex));
//                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                        retriever.setDataSource(Integer.toString(currIndex));
//                        int vwidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
//                        int vheight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
//                        FFmpegMediaMetadataRetriever fmmr = new FFmpegMediaMetadataRetriever();
//                        fmmr.setDataSource(Integer.toString(currIndex));
                        break;

                    case ExoPlayer.STATE_ENDED:
                        currIndex++;
                        if(currIndex>maxIndex){
                            currIndex=0;
                        }
                        //player.seekTo(currIndex,C.TIME_UNSET);
                        Log.d(TAG,"status video : ENDED , index : "+Integer.toString(currIndex));
                        break;
                }


            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
//        player.setPlayWhenReady(true);
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        StopVideo();
//        mHandler.removeCallbacks(mUpdateTimer);
    }

    public void rotateVideo(File file){
        String rotation = getRotation(file);
        if(rotation!=null){
            float fRotation = Float.parseFloat(rotation);
            simpleExoPlayerView.setRotation(fRotation);
            int MyVersion = Build.VERSION.SDK_INT;
            if(MyVersion<=Build.VERSION_CODES.KITKAT){
                if(rotation.equals("90")){
                    //simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

//                        ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
//                        group.addView(simpleExoPlayerView,new ViewGroup.LayoutParams(
//                                dimWidth, dimHeight));
                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                } else {

                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                }
            }
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        StopVideo();

//        mHandler.removeCallbacks(mUpdateTimer);

    }
    public void StartVideo(){
        if(player!=null){

//            File file = new File(mParam1.get(currIndex));
//            long duration = getMediaDuration(file);
//            loadVideoFile(mParam1.get(currIndex));
            player.setPlayWhenReady(true);

        }

    }
    public long getDuration(){
        realDurationMillis=0;
        if(mParam1!=null){
            for(int i=0;i<mParam1.size();i++){
                realDurationMillis+=getMediaDuration(new File(mParam1.get(i)));
            }
        }

        return realDurationMillis;
    }
    public void StopVideo(){
        if(player!=null){
            player.setPlayWhenReady(false);
            player.release();
        }
        isStop=true;
//        mHandler.removeCallbacks(mUpdateTimer);
    }
//    Runnable mUpdateTimer = new Runnable() {
//        @Override
//        public void run() {
//            currIndex++;
//            if(currIndex>maxIndex){
//                currIndex=0;
//            }
//            File file = new File(mParam1.get(currIndex));
//            long duration = getMediaDuration(file);
//            loadVideoFile(mParam1.get(currIndex));
//
//            Log.d(TAG,"video index : "+Integer.toString(currIndex));
////            mHandler.postDelayed(this,duration);
//        }
//    };


    //String rotation="0.0";
    private long getMediaDuration(File file){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());
        long duration=Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        try {
            Thread.sleep(1000);
        } catch (Exception ex){

        }
        return duration;
    }
    private String getRotation(File file){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());
        int vwidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int vheight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
//        String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        FFmpegMediaMetadataRetriever fmmr = new FFmpegMediaMetadataRetriever();
        fmmr.setDataSource(file.getAbsolutePath());
        String rotation = fmmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        Log.d(TAG, "Rotation:"+rotation+" dimension :"+Integer.toString(vwidth)+","+Integer.toString(vheight));
//        float fRotation = Float.parseFloat(rotation);
        return rotation;
    }
    @Override
    public void onDestroyView() {

        StopVideo();

        super.onDestroyView();
    }


}
