package id.sentuh.digitalsignage.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eftimoff.viewpagertransformers.AccordionTransformer;
import com.eftimoff.viewpagertransformers.BackgroundToForegroundTransformer;
import com.eftimoff.viewpagertransformers.CubeInTransformer;
import com.eftimoff.viewpagertransformers.DrawFromBackTransformer;
import com.eftimoff.viewpagertransformers.FlipHorizontalTransformer;
import com.eftimoff.viewpagertransformers.ZoomOutSlideTransformer;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.snatik.storage.Storage;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.app.AppUtils;
import id.sentuh.digitalsignage.app.Configurate;
import id.sentuh.digitalsignage.app.EndPoints;
import id.sentuh.digitalsignage.helper.DBHelper;
import id.sentuh.digitalsignage.helper.OnPageFinishListener;
import id.sentuh.digitalsignage.models.FrameResources;
import id.sentuh.digitalsignage.models.Frames;
import id.sentuh.digitalsignage.models.Views;
import id.sentuh.digitalsignage.views.MultiPager;

public class ScreenFragment extends Fragment {
    private static String TAG="Screen";
    private OnPageFinishListener mListener;
    public static String VIEW_ID="view_Id";
    //    private boolean isVersionChecked=false;
//    private AddressResultReceiver mResultReceiver;
    Storage storage;
    int MAX_PAGER = 200;
    //    ViewPager[] mPagers=new ViewPager[MAX_PAGER];
    List<MultiPager> mPagers;
    private Handler mHandler;
    public static final int DELAY = 1000;
    public static final int MINUTE = 10000;
    RelativeLayout screenLayout;
    int[] mFrameId = new int[MAX_PAGER];
    Context mContext;
    public static int screen_height;
    public static int screen_width;
    boolean showQrCode = false;
    ImageView qrCode;
    int delayTime;
    public static int indexView;
    public static int maxWidth = 1080;
    public static int maxHeight = 1920;
    private float scaleX ;
    private float scaleY;
    public static String frame_name;
    public static Views currentView = new Views();
    public static String page_title = currentView.getPage_name();
    public static int width;
    public static int height;
    Dialog myDialog;
    int nextIndex;
    List<Views> views=null;
    int maxViewIndex;
    public static ScreenFragment newInstance(int index) {
        // Required empty public constructor
        ScreenFragment fragment = new ScreenFragment();
        Bundle args = new Bundle();
        args.putInt(VIEW_ID,index);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            indexView = getArguments().getInt(VIEW_ID);
            nextIndex = indexView+1;
        }
        mContext = getContext();
        mListener = (OnPageFinishListener)mContext;
        mHandler = new Handler();
        views = DBHelper.getViews();
        maxViewIndex = views.size()-1;
        storage = new Storage(mContext);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_screen, container, false);
        screenLayout = view.findViewById(R.id.screenFragment);
        DecimalFormat dformat = new DecimalFormat("#,###.###");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_height = displayMetrics.heightPixels;
        screen_width = displayMetrics.widthPixels;
        scaleX = (float) screen_width / maxWidth;
        scaleY = (float) screen_height / maxHeight;


        if(indexView<views.size()) {
            Views currentView = views.get(indexView);
            if (currentView != null) {
                Log.d(TAG, "pager name : " + currentView.getPage_name() + ": " +
                        dformat.format(scaleX) + "," + dformat.format(scaleY));
                loadView(currentView);
            } else {
                Log.e(TAG, "pager view not found");
                Toast.makeText(mContext, "View Not Found!", Toast.LENGTH_SHORT).show();
            }
        }
         else {
            views.size();
            loadView(currentView);
        }

        return view;
    }
    @SuppressLint("HardwareIds")
    private void loadView(Views currentView){
        RelativeLayout.LayoutParams matchParent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        String page_title = currentView.getPage_name();
        //Toast.makeText(mContext,"view load : "+page_title,Toast.LENGTH_SHORT).show();
//        String file_path = EndPoints.STORAGE_DATA_PATH +"/Views/"+page_title+".txt";
        //this.setTitle(page_title);

        int viewid = currentView.getId();
        String background_image = currentView.getBackground_image();
        String background_path = EndPoints.RESOURCE_PATH + File.separator + background_image;
        File file_bg = new File(background_path);
//        if(screenLayout.getChildCount()>0){
//            screenLayout.removeAllViewsInLayout();
//        }
        if (file_bg.exists()) {
            Drawable bgdrawable = Drawable.createFromPath(background_path);
            screenLayout.setBackground(bgdrawable);

        }
        List<Frames> frames = DBHelper.getFrames(viewid);
        int length = frames.size();
        if (length == 0) {
//            currentView = views.get(1);
            frames = DBHelper.getFrames(currentView.getId());
        }
//        Log.d(TAG,"load page : "+page_title+" index size : "+Integer.toString(length));
        if (length > 0) {
            mPagers = new ArrayList<MultiPager>();
            MAX_PAGER = length;
//                max_page = new int[length];
//                current_page = new int[length];
//                int ending = length - 1;
//                pagerAdapters = new ArrayList<MyFragmentPagerAdapter>();
            for (int i = 0; i < length; i++) {//loading frame
                Frames frame = frames.get(i);
                frame_name = frame.getFrame_name();
//                    current_page[i]=0;
                 width = (int) (frame.getWidth() * scaleX);
                 height = (int) (frame.getHeight() * scaleY);
                int left = (int) (frame.getX() * scaleX);
                int top = (int) (frame.getY() * scaleY);
                Log.d(TAG,"frame size :"+Integer.toString(width)+","+Integer.toString(height));


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    mFrameId[i] = View.generateViewId();
                }
                FrameLayout frameLayout = new FrameLayout(mContext);
                RelativeLayout.LayoutParams frameParam = new RelativeLayout.LayoutParams(width, height);
                if(width>=screen_width&&height>=screen_height){
                    frameParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                }
                if (i == 0) {
                    frameParam.addRule(RelativeLayout.ALIGN_PARENT_START);
                    frameParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    frameParam.setMargins(0, top, 0, 0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        frameParam.setMarginStart(left);
                    }
                } else {

                    frameParam.addRule(RelativeLayout.BELOW, mFrameId[i - 1]);
                    frameParam.addRule(RelativeLayout.END_OF, mFrameId[i - 1]);
                    //top = top - mPagers[i-1].getHeight();
                    frameParam.setMargins(0, top, 0, 0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        frameParam.setMarginStart(left);
                    }
                }


                frameLayout.setLayoutParams(frameParam);

                screenLayout.addView(frameLayout);
                List<FrameResources> resources = DBHelper.getFrameResource(frame.getId());
                int rec_length = resources.size();
//                Log.d(TAG,"add new frame : "+frame_name+" resource size : "+Integer.toString(rec_length));
//                    max_page[i]=rec_length;
                final int lindx = rec_length - 1;
                if (rec_length > 0) {
                    List<Fragment> pages = new ArrayList<Fragment>();
                    ArrayList<String> sources = new ArrayList<String>();
                    List<Integer> durations = new ArrayList<Integer>();
                    int vid_count =0;
                    int widget_index=0;
                    int htmlindex=0;
                    int running_text_index=0;
                    int videoIndex=0;
                    boolean lasVideo = false;
                    for (int j = 0; j < rec_length; j++) {
                        FrameResources resss = resources.get(j);
                        String file_name = resss.getResource_name();
                        int duration = resss.getWaktu();
                        Log.d(TAG, "file name :" + file_name);
                        if (file_name.contains("http:") || file_name.contains("rtmp:")) {
                            durations.add(duration * 1000);
//                            if (file_name.contains(".m3u8")) {
//                                Log.d(TAG,"file :"+file_name+" video");
                            //pages.add(VideoFragment.newInstance(file_path,j,i,rec_length==1));
                            sources.add(file_name);
                            if (j == (rec_length - 1)) {
                                durations.add(duration * 1000);
                                pages.add(VideoPlayListFragment.newInstance(sources, j, i, true));
//                                        max_page[i]=1;
                            }
//                            }
                        } else {
                            String file_path = EndPoints.RESOURCE_PATH + File.separator + file_name;
                            File source_file = new File(file_path);
                            if (source_file.exists()) {
                                if (file_name.contains(".website")) {
                                    String url_json = storage.readTextFile(source_file.getAbsolutePath());
                                    try {
                                        JSONObject json = new JSONObject(url_json);
                                        String url_web = json.getString("url");
                                        if (url_web.contains("youtube.com")) {
                                            String Ytid = AppUtils.extractYoutubeId(url_web);
//                                            YTPlayer ytFragment =YTPlayer.newInstance(Ytid,j,i,rec_length==1);
//                                            pages.add(ytFragment);
                                            YouTubePlayerSupportFragment ytfragment = createYoutubeFragment(Ytid);
                                            durations.add(duration * 900000000);
                                            pages.add(ytfragment);
                                        } else {
                                            HtmlFragment htmlFragment = HtmlFragment.newInstance(url_json, htmlindex, i);
                                            durations.add(duration * 900000000);
                                            pages.add(htmlFragment);
                                        }
                                    } catch (Exception ex) {

                                    }
//                                Log.d(TAG,"file :"+file_name+" url:"+url_json);

                                }
                                if (file_name.contains(".txt")) {
                                    String content = storage.readTextFile(source_file.getAbsolutePath());
                                    if(content!=null){
                                        TextScrollFragment textFragment = TextScrollFragment.newInstance(content, running_text_index, i);
                                        durations.add(duration * 900000000);
                                        pages.add(textFragment);
                                    }
//                                Log.d(TAG,"file :"+file_name+" content:"+content);

                                }
                                if (file_name.contains(".clock")) {
                                    String content = storage.readTextFile(source_file.getAbsolutePath());
                                    Log.d(TAG,"file :"+file_name+" content:"+content);
                                    if(content!=null){
                                        ClockFragment timeFragment = ClockFragment.newInstance(content, widget_index, i);
                                        durations.add(duration * 900000000);
                                        pages.add(timeFragment);
                                    }

                                }
                                if (file_name.contains(".date")) {
                                    String content = storage.readTextFile(source_file.getAbsolutePath());
                                    if(content!=null){
                                        DateFragment dateFragment = DateFragment.newInstance(content, widget_index, i);
                                        durations.add(duration * 900000000);
                                        pages.add(dateFragment);
                                    }

                                }
                                if (file_name.contains(".weather")){
                                    String content = storage.readTextFile(source_file.getAbsolutePath());
                                    if(content!=null){
                                        WeatherFragment weatherFragment = WeatherFragment.newInstance(content, widget_index, i);
                                        durations.add(duration * 900000000);
                                        pages.add(weatherFragment);
                                    }
                                }
                                if (file_name.contains(".png") || file_name.contains(".jpeg") ||
                                        file_name.contains(".jpg") || file_name.contains(".bmp")) {
//                                Log.d(TAG,"file :"+file_name+" image");
                                    ImageFragment imageFragment = ImageFragment.newInstance(file_path, j, i);
                                    if (resources.size()== 1){
                                        durations.add(duration * 999999999);
                                    }
                                    durations.add(duration * 1000);
                                    pages.add(imageFragment);

                                }
                                if (file_name.contains(".mp4") || file_name.contains(".m3u8")) {

//                                    int lindx = pages.size()-1;
                                    int video_durasi = MediaPlayer.create(getContext(), Uri.fromFile(new File(file_path))).getDuration();
                                    durations.add(video_durasi);
                                    sources.add(file_path);
//                                    if(j==lindx){
////                                        pages.remove(lindx);
//
//                                    } else {
//                                        VideoFragment videoFragment = VideoFragment.newInstance(file_path,j,i,rec_length==1);
//                                        pages.add(videoFragment);
//                                        Log.d(TAG,"video single :"+file_name+"");
//                                    }

//                                    videoIndex = j;
                                    //pages.add(VideoFragment.newInstance(file_path,j,i,rec_length==1));

//                                    vid_count++;

//                                    VideoFragment videoFragment = VideoFragment.newInstance(file_path,j,i,false);
//                                    pages.add(videoFragment);
                                }

                            }// End source file exist

                        }

                    }
                    if(sources.size()>0){
                        VideoPlayListFragment playListFragment = VideoPlayListFragment.newInstance(sources,videoIndex,i,true);
                        pages.add(playListFragment);
                        Log.d(TAG,"video playlist added");
                    } else if (height >= maxHeight && width >= maxWidth){
                        VideoPlayListFragment playListFragment = VideoPlayListFragment.newInstance(sources,videoIndex,i,false);
                        pages.add(playListFragment);
                    } else {
                        Log.d(TAG,"frame count : "+Integer.toString(pages.size()));
                    }

//                        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(this,getSupportFragmentManager(),pages);
//                        pagerAdapters.add(pagerAdapter);
                    MultiPager mPager = new MultiPager(mContext,frame_name, getFragmentManager(), pages, durations);
//                    mPager.setLayerType(View.LAYER_TYPE_HARDWARE,null);
                    switch (i) {
                        case 0:
                            mPager.setPageTransformer(true, new BackgroundToForegroundTransformer());
                            break;
                        case 1:
                            mPager.setPageTransformer(true, new CubeInTransformer());
                            break;
                        case 2:
                            mPager.setPageTransformer(true, new ZoomOutSlideTransformer());
                            break;
                        case 3:
                            mPager.setPageTransformer(true, new DrawFromBackTransformer());
                            break;
                        case 4:
                            mPager.setPageTransformer(true, new AccordionTransformer());
                            break;
                        default:
                            mPager.setPageTransformer(true, new FlipHorizontalTransformer());
                    }

                    mPager.setLayoutParams(matchParent);
//                mPagers[i].setBackgroundColor(Color.parseColor(background));
//                mPagers[i].setPageMargin(left);
//                    mPagers[i].setPadding(left,top,left,top);
                    int newId = 0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        newId = View.generateViewId();
                    }
                    mPager.setId(newId);
                    mPager.setFocusable(true);
//                        mPager.setAdapter(pagerAdapter);
                    mPager.setOnPageChangeListener(pageChangeListener);
                    Log.d(TAG, " add page to frame : " + frame_name + ", child count : " +
                            Integer.toString(pages.size()));
                    frameLayout.addView(mPager);
                    mPager.startAnimation();
                    mPagers.add(mPager);

                }
                qrCode = new ImageView(mContext);
                @SuppressLint("HardwareIds")
                String android_id = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    android_id = Settings.Secure.getString(Objects.requireNonNull(getActivity()).getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }
                Bitmap filebmp = QRCode.from(android_id).to(ImageType.PNG).withSize(250, 250).bitmap();
                qrCode.setImageBitmap(filebmp);
                RelativeLayout.LayoutParams widgetParam = new RelativeLayout.LayoutParams(200,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    widgetParam.addRule(RelativeLayout.ALIGN_PARENT_START);
                }
                widgetParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                widgetParam.setMargins(0,screen_height-250,0,0);
                int marginstart = (screen_width /2) - 125;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    widgetParam.setMarginStart(marginstart);
                }
                qrCode.setLayoutParams(widgetParam);
//                dateTimeWidget.setText("TODAY DATE");
                screenLayout.addView(qrCode);
                qrCode.setVisibility(View.GONE);
//                    addSnowLayout();
            }
        }//end file view exist;


//        screenLayout = screenLayout;
        if(views.size()>1){
            String[] times = currentView.getShowTime().split(":");
            delayTime = Integer.parseInt(times[1])*60000;
            mHandler.postDelayed(mRunnableUpdateTime, delayTime);
        }



    }
    public void setShowQrCode(boolean value){
        if(value){
            qrCode.setVisibility(View.VISIBLE);
        } else {
            qrCode.setVisibility(View.GONE);
        }
    }
    private Runnable mRunnableUpdateTime = new Runnable(){

        @Override
        public void run() {

            if(nextIndex>maxViewIndex){
                nextIndex=0;
            }
            //Log.d(TAG,"page view : "+Integer.toString(indexView));
            mListener.OnPageFinish(nextIndex,true);

        }
    };
    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacks(mRunnableUpdateTime);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        int currentPosition = 0;

        @Override
        public void onPageSelected(int newPosition) {


            currentPosition = newPosition;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) { }

        public void onPageScrollStateChanged(int arg0) { }
    };
    private YouTubePlayerSupportFragment createYoutubeFragment(final String youtubeid){
        Configurate config =new Configurate(mContext);
        config.setKeyString("youtube_id",youtubeid);
        YouTubePlayerSupportFragment youtubeFragment = new YouTubePlayerSupportFragment();

        youtubeFragment.initialize(EndPoints.DEVELOPER_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        // do any work here to cue video, play video, etc.
                        if(b){
                            youTubePlayer.cueVideo(youtubeid);
//                            youTubePlayer.loadVideo(youtubeid);
//                            youTubePlayer.play();
                            // youtubeStart = System.currentTimeMillis();


                        }

                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        //youTubeInitializationResult.getErrorDialog(ScreenRelativeActivity.this,0);
//                        youTubeInitializationResult.
                    }
                });
        return youtubeFragment;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPageFinishListener) {
            mListener = (OnPageFinishListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mHandler.removeCallbacks(mRunnableUpdateTime);
    }



}
