package id.sentuh.digitalsignage.views;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.List;

import id.sentuh.digitalsignage.adapter.MyFragmentPagerAdapter;
import id.sentuh.digitalsignage.fragment.TextFragment;
import id.sentuh.digitalsignage.fragment.VideoFragment;
import id.sentuh.digitalsignage.fragment.VideoPlayListFragment;

/**
 * Created by sony on 3/26/2018.
 */

public class MultiPager extends ViewPager {
    private static final String TAG = "Multipager";
    List<Fragment> mItems;
    Context mContext;
    Handler mHandler;
    int childCount;
    private int running_page;
    List<Integer> delay;
    MyFragmentPagerAdapter pagerAdapter;
    String frameName;
    public MultiPager(Context context,String name,FragmentManager fragmentManager,List<Fragment> pages,List<Integer> duration) {
        super(context);
        this.mContext = context;
        this.mItems = pages;
        mHandler = new Handler();
        this.childCount = pages.size();
        this.delay = duration;
        this.frameName = name;
//        this.setId(View.generateViewId());
        pagerAdapter = new MyFragmentPagerAdapter(mContext,fragmentManager,mItems);
        this.setAdapter(pagerAdapter);

    }

    @Override
    public int getCurrentItem() {
        return super.getCurrentItem();
    }
    VideoFragment videoFragment;
    TextFragment textFragment;
    public void startAnimation(){
        running_page = 0;
        childCount=mItems.size();
        if (childCount<1){
            return;
        }
        long durasi = delay.get(running_page);
        Log.v(TAG,"page duration "+Long.toString(durasi));
        Fragment item = mItems.get(running_page);
        if (item instanceof VideoPlayListFragment) {
            VideoPlayListFragment fragment = (VideoPlayListFragment) item;
            if(fragment!=null){
                fragment.StartVideo();
//                durasi =fragment.getDuration();
            }
        }


        if (item instanceof TextFragment){
            textFragment = (TextFragment) item;
            textFragment.startScroll();

            Context text = textFragment.getContext();
            if (text == null) {
                mHandler.postDelayed(mFindingDuration, 1000);
            }
        }

        if(item instanceof VideoFragment) {
//                mHandler.removeCallbacks(mRunnable);
            videoFragment = (VideoFragment) item;
            videoFragment.StartVideo();

            long duration = videoFragment.getMediaDuration();
            if (duration == 0) {
                mHandler.postDelayed(mFindingDuration, 1000);
            }

        }

        if(childCount>1){
            mHandler.postDelayed( mRunnable , durasi );
            Log.d(TAG,frameName+" start looping");
        } else {
            Log.d(TAG,frameName+" no need looping");
        }

    }
    Runnable mFindingDuration = new Runnable() {
        @Override
        public void run() {
            long duration = videoFragment.getMediaDuration();
            Context text = textFragment.getContext();

            Log.d(TAG,"duration : "+Long.toString(duration));
            if(duration>0){
                mHandler.postDelayed( mRunnable , duration );
                mHandler.removeCallbacks(this);
            } else {
                mHandler.postDelayed(this,1000);
            }

            if(text == null) {
                mHandler.postDelayed(mRunnable, Long.parseLong(null));
                mHandler.removeCallbacks(this);
            } else {
                mHandler.postDelayed(this,1000);
            }
        }
    };
    Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {

            childCount=(mItems.size()-1);

            if(running_page<childCount){
                running_page++;
            } else {
                running_page=0;
            }

            Fragment item = mItems.get(running_page);
            if(item instanceof VideoPlayListFragment){
//                mHandler.removeCallbacks(mRunnable);
                VideoPlayListFragment fragment = (VideoPlayListFragment) item;
                long duration = fragment.getDuration();

//                mHandler.postDelayed( mRunnable , duration );
            }
//            int seconds=0;
            long duration =0;
            DecimalFormat dformat = new DecimalFormat("#,###.###");
            if(item instanceof VideoFragment){
//                mHandler.removeCallbacks(mRunnable);
                VideoFragment fragment = (VideoFragment) item;
                duration = fragment.getDuration();
//                seconds = (int)duration/1000;
//                Log.d(TAG,"duration : "+Long.toString(duration));
                mHandler.postDelayed( mRunnable , duration );
                Log.d(TAG,"video loop index:"+Integer.toString(running_page)+" "+dformat.format(duration));
            } else {
                duration = delay.get(running_page);
                mHandler.postDelayed( mRunnable , duration );
                Log.d(TAG,"non video loop index:"+Integer.toString(running_page)+" "+dformat.format(duration));
            }

            if (item instanceof TextFragment){
                TextFragment fragment = (TextFragment) item;
                fragment.startScroll();

                duration = delay.get(running_page);
                mHandler.postDelayed(mRunnable, duration);
                Log.d(TAG, "non text loop index:"+Integer.toString(running_page)+" "+dformat.format(duration));
            }
            setCurrentItem(running_page,true);

        }
    };
    public void stopAnimation(){
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        for(int i=0;i<mItems.size();i++){
            if(mItems.get(i) instanceof VideoFragment){
                VideoFragment fragment = (VideoFragment) mItems.get(i);
                fragment.StartVideo();
                break;
            }
            if(mItems.get(i) instanceof VideoPlayListFragment){
                VideoPlayListFragment fragment = (VideoPlayListFragment) mItems.get(i);
                fragment.StartVideo();
                break;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {

        for(int i=0;i<mItems.size();i++){
            if(mItems.get(i) instanceof VideoFragment){
                VideoFragment fragment = (VideoFragment) mItems.get(i);
                fragment.StopVideo();
                break;
            }
            if(mItems.get(i) instanceof VideoPlayListFragment){
                VideoPlayListFragment fragment = (VideoPlayListFragment) mItems.get(i);
                fragment.StopVideo();
                break;
            }
        }

        mHandler.removeCallbacks(mRunnable);
        super.onDetachedFromWindow();
    }


}
