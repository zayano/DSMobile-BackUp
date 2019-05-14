package id.sentuh.digitalsignage.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.List;

import id.sentuh.digitalsignage.app.Configurate;
import id.sentuh.digitalsignage.app.EndPoints;
import id.sentuh.digitalsignage.fragment.HtmlFragment;
import id.sentuh.digitalsignage.fragment.TextFragment;
import id.sentuh.digitalsignage.fragment.VideoFragment;
import id.sentuh.digitalsignage.fragment.VideoPlayListFragment;
import id.sentuh.digitalsignage.fragment.YTPlayer;

/**
 * Created by sony on 2/16/2018.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    public static String TAG = "Pager Adapter";
    public static int pos = 0;
    private List<Fragment> myFragments;
    private Context context;
    Configurate config;
    YouTubePlayer youTubePlayer1;
    public MyFragmentPagerAdapter(Context c, FragmentManager fragmentManager, List<Fragment> myFrags) {
        super(fragmentManager);
        myFragments = myFrags;
        this.context = c;
        config=new Configurate(context);
    }

    @Override
    public Fragment getItem(int position) {

        return myFragments.get(position);

    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (object instanceof VideoPlayListFragment) {
            VideoPlayListFragment fragment = (VideoPlayListFragment) object;
            if(fragment!=null){
                fragment.StartVideo();
            }
            Log.v(TAG,"video show Playlist");
        }
        if (object instanceof VideoFragment) {
            VideoFragment fragment = (VideoFragment) object;
            if(fragment!=null){
                fragment.StartVideo();
            }
            Log.v(TAG,"video show single");
        }
        if(object instanceof TextFragment){
            TextFragment fragment = (TextFragment) object;
            if(fragment!=null){
                fragment.startScroll();
            }
        }
        if(object instanceof HtmlFragment){
            HtmlFragment fragment = (HtmlFragment) object;
            if(fragment!=null){
                fragment.reloadWeb();
            }
        }
        if(object instanceof YTPlayer){
            YTPlayer fragment = (YTPlayer) object;
            if(fragment!=null){

            }
        }
        if(object instanceof YouTubePlayerSupportFragment) {
            YouTubePlayerSupportFragment fragment = (YouTubePlayerSupportFragment) object;
            fragment.initialize(EndPoints.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    final String youtubeId = config.getKeyString("youtube_id");
                    Log.d(TAG,"Youtube ID : "+youtubeId);
                    youTubePlayer1 = youTubePlayer;
                    youTubePlayer1.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                        @Override
                        public void onLoading() {

                        }

                        @Override
                        public void onLoaded(String s) {
                            if(!youTubePlayer1.isPlaying()){
                                youTubePlayer1.play();
                            }
                        }

                        @Override
                        public void onAdStarted() {

                        }

                        @Override
                        public void onVideoStarted() {

                        }

                        @Override
                        public void onVideoEnded() {
                            youTubePlayer1.play();
                        }

                        @Override
                        public void onError(YouTubePlayer.ErrorReason errorReason) {

                        }
                    });
                    if(!youTubePlayer.isPlaying()){

                        youTubePlayer.loadVideo(youtubeId);
//                        youTubePlayer.play();
                    }
                    if(b){
                        youTubePlayer.cueVideo(youtubeId);
                    }
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        if (object instanceof VideoPlayListFragment) {
            Log.d(TAG,"destroy video playlist");

            VideoPlayListFragment fragment = (VideoPlayListFragment) object;
            if(fragment!=null){
                fragment.StopVideo();
            }

        }

        if (object instanceof VideoFragment) {
            Log.d(TAG,"destroy video");

            VideoFragment fragment = (VideoFragment) object;
            if(fragment!=null){
//                fragment.StopVideo();
                fragment.StopVideo();
            }

        }
    }

    @Override
    public int getCount() {

        return myFragments.size();
    }
    public static void setPos(int pos) {
        MyFragmentPagerAdapter.pos = pos;
    }

}
