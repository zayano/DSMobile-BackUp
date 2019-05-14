package id.sentuh.digitalsignage.fragment;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.File;

import id.sentuh.digitalsignage.PlayerManager;
import id.sentuh.digitalsignage.R;
import wseemann.media.FFmpegMediaMetadataRetriever;

import static android.content.Context.AUDIO_SERVICE;

public class VideoFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_INDEX = "page_index";
    private static final String PAGER_INDEX = "index_pager";
    private static final String VIDEO_LOOPING = "video_looping";
    private static final String TAG = "Player";

    private final Paint restorePaint = new Paint();
    private final Paint maskXferPaint = new Paint();
    private final Paint canvasPaint = new Paint();

    private final Rect bounds = new Rect();
    private final RectF boundsf = new RectF();

    private String mParam1;
    private PlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private Context mContext;
    private int mIndexPage;
    private int mPagerIndex;
    private long mDuration;
    private boolean isLooping;
    private float radius;
    private Path path = new Path();
    private RectF rect = new RectF();
    //    OnPageFinishListener mListener;
    PlayerManager playerManager;
    //    Handler mHandler;
    float currentVol;
    long realDurationMillis;
    boolean durationSet = false;
    boolean readyStatus = false;
    AudioManager am;
    CardView cv;

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance(String param1,int index,int pagerIndex,boolean isLoop) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_INDEX,index);
        args.putInt(PAGER_INDEX,pagerIndex);
        args.putBoolean(VIDEO_LOOPING,isLoop);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mIndexPage = getArguments().getInt(ARG_INDEX);
            mPagerIndex = getArguments().getInt(PAGER_INDEX);
            isLooping = getArguments().getBoolean(VIDEO_LOOPING);

        }

        mContext = getContext();
//        mListener = (OnPageFinishListener) getContext();
//        mHandler=new Handler();
        playerManager = new PlayerManager(mContext);
        setUserVisibleHint(false);
        am = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        currentVol= am.getStreamVolume(AudioManager.STREAM_MUSIC);
    }



    public long getMediaDuration(){
        if(mParam1!=null){
            File file = new File(mParam1);
            return getMediaDuration(file);
        } else return 0;

    }

    public String getMediaFile(){
        return mParam1;
    }

    private long getMediaDuration(File file){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        if(file.exists()){
            retriever.setDataSource(mContext, Uri.fromFile(file));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//
            long timeInMillisec = Long.parseLong(time);
            return timeInMillisec;
        }
        return 0;

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //videoView.setVideoURI(Uri.parse(mParam1));
//        player.setPlayWhenReady(true); //run file/link when ready to play.

    }

    public long getDuration(){
        return getMediaDuration();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_video, container, false);
        simpleExoPlayerView = view.findViewById(R.id.video_view);
        simpleExoPlayerView.setUseController(false);
//        ShadowLayout shadowLayout = view.findViewById(R.id.shadow_video);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        playerManager.init(mContext,simpleExoPlayerView,mParam1);
        

//        shadowLayout.setInvalidateShadowOnSizeChanged(true);

//        RoundedBitmapDrawable roundedCorners = RoundedBitmapDrawableFactory.create(
//                simpleExoPlayerView
//        );



        return view;
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
//        Log.d(TAG, "Rotation:"+rotation+" dimension :"+Integer.toString(vwidth)+","+Integer.toString(vheight));
//        float fRotation = Float.parseFloat(rotation);
        return rotation;
    }
    public void rotateVideo(File file){
        String rotation = getRotation(file);
        if(rotation!=null){
            float fRotation = Float.parseFloat(rotation);
            simpleExoPlayerView.setRotation(fRotation);
            int MyVersion = Build.VERSION.SDK_INT;
            if(MyVersion>=Build.VERSION_CODES.JELLY_BEAN){
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
    public void volumeRaise(){

    }

    @Override
    public void onResume() {
        super.onResume();
//
        playerManager.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        playerManager.release();
    }
    public void StartVideo(){

        if(playerManager!=null){
//            playerManager.init(mContext,simpleExoPlayerView,mParam1);
            playerManager.start();
        }

    }

    public void StopVideo(){
        if(playerManager!=null){
            playerManager.stop();
//            playerManager=null;
        }


    }

    public boolean getReady(){
        return readyStatus;
    }


    @Override
    public void onDestroyView() {
        playerManager.release();
        super.onDestroyView();
    }
    public void setRadius(float radius) {
        this.radius = radius;
    }

    //    Runnable mRunnable = new Runnable()
//    {
//        @Override
//        public void run()
//        {
//            mListener.OnPageFinish(mPagerIndex,true);
////            mHandler.postDelayed(mRunnable,mDuration);
//        }
//    };

}
