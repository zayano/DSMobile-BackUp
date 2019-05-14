package id.sentuh.digitalsignage.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import id.sentuh.digitalsignage.app.EndPoints;
import id.sentuh.digitalsignage.helper.OnPageFinishListener;

/**
 * Created by sony on 2/22/2018.
 */

public class YTPlayer extends YouTubePlayerSupportFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_INDEX = "page_index";
    private static final String PAGER_INDEX = "index_pager";
    private static final String VIDEO_LOOPING = "video_looping";
    private static final String TAG = "Player";
    private Context mContext;
    private int mIndexPage;
    private int mPagerIndex;
    private long mDuration;
    private boolean isLooping;
    private String mParam1;
    OnPageFinishListener mListener;
    Handler mHandler;
    public static YTPlayer newInstance(String param1,int index,int pagerIndex,boolean isLoop) {
        YTPlayer fragment = new YTPlayer();
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
        mListener = (OnPageFinishListener) getContext();
        mHandler=new Handler();
        setUserVisibleHint(false);
        this.initialize(EndPoints.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
                if(b){
                    youTubePlayer.cueVideo(mParam1);
                    youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                        @Override
                        public void onLoading() {

                        }

                        @Override
                        public void onLoaded(String s) {

                        }

                        @Override
                        public void onAdStarted() {

                        }

                        @Override
                        public void onVideoStarted() {

                        }

                        @Override
                        public void onVideoEnded() {
                            if(isLooping){
                                youTubePlayer.play();
                            } else {
                                mListener.OnPageFinish(mPagerIndex,true);
                            }

                        }

                        @Override
                        public void onError(YouTubePlayer.ErrorReason errorReason) {

                        }
                    });
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }


}
