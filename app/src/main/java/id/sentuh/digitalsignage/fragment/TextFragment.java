package id.sentuh.digitalsignage.fragment;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.app.AppUtils;
import id.sentuh.digitalsignage.app.EndPoints;
import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final
    String ARG_PARAM1 = "content";

    private static final
    String TAG = "Text Fragment";

    private static final
    String ARG_INDEX = "page_index";

    private static final
    String PAGER_INDEX = "index_pager";

    private
    String mParam1;

    private
    String textColor = "#ffffff";

    private
    String backColor ="#cccccc";

    private
    String textContent;

    private
    String multiLine = "single";

    private
    int backgroundColor;

    private
    int foreColor;

    Handler mHandler;
//    OnPageFinishListener mListener;
    private int DELAY_TIME = 5000;

    private
    TextView textView;

    private
    int textSpeed = 3000;

    Typeface fontDefault;

    public TextFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment TextFragment.
     */
    public static TextFragment newInstance(String param1,int index,int indexPager) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_INDEX, index);
        args.putInt(PAGER_INDEX, indexPager);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //        textView.setTextColor(foreColor);
//        textView.setBackgroundColor(backgroundColor);



//        textView.setTextColor(Color.parseColor(textColor));
        if(multiLine.equals("single")){

        } else {

//            textView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//            textView.setSingleLine(false);
        }
        //textView.setCharacterList(TickerUtils.getDefaultNumberList());

//        textView.startScroll();
    }
    public void startScroll(){
        if(textView!=null){
//            textView.startScroll();
            textView.setSelected(true);

        }

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            int mIndexPage = getArguments().getInt(ARG_INDEX);
            int mPagerIndex = getArguments().getInt(PAGER_INDEX);
        }
        fontDefault = AppUtils.getFont(Objects.requireNonNull(getContext()),"OpenSans-Regular");
//        mListener = (OnPageFinishListener) getContext();
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        LinearLayout layout = view.findViewById(R.id.layoutText);
        textView = view.findViewById(R.id.txtContent);
        textView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
//        textView.setAnimationInterpolator(new OvershootInterpolator());
//        textView.setCharacterList(TickerUtils.getDefaultNumberList());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScroll();
            }
        });
        textView.setGravity(Gravity.START);
        textView.requestFocus();
//        textView.setRndDuration(scrollSpeed);
//        setMarqueeSpeed(textView,1000.0f);
//        textView.setAnimationDuration(1000);
//        String json_string =  String.format("{\"result\": %s }",mParam1);
        //Log.d(TAG,"Content Text : "+json_string);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSelected(true);
        textView.setSingleLine();
        try {
            JSONObject json = new JSONObject(mParam1);
            textContent = json.getString("text");
            if(!json.isNull("background_color")){
                backColor = json.getString("background_color");
                textView.setBackgroundColor(Color.parseColor(backColor));
                layout.setBackgroundColor(Color.parseColor(backColor));
            }
            if(!json.isNull("color")){
                textColor = json.getString("color");
                textView.setTextColor(Color.parseColor(textColor));
            }
            if(!json.isNull("line")){
                multiLine = json.getString("line");
            }

            if(!json.isNull("speed")){
                int scrollSpeed = (int) Math.round(json.getDouble("speed"));
//                textView.setAnimationDuration(scrollSpeed * 1000);
//                textView.setRndDuration(scrollSpeed);
//                setMarqueeSpeed(textView,scrollSpeed);
            }
            if(!json.isNull("font_name")){
                String fontName =json.getString("font_name");
                File fontFile = new File(EndPoints.SYSTEM_PATH, fontName);
                if(fontFile.exists()){
                    Typeface typeface = Typeface.createFromFile(fontFile);
                    textView.setTypeface(typeface);
//                    textView.setTypeface(fontDefault);
                    Log.d(TAG,"Font File exist!");
                } else {
                    Log.d(TAG,"Font File not exist!");
                }

            }
            if(!json.isNull("rss")){
                String urlRSS = json.getString("rss");
                if(urlRSS.length()>1){
                    loadRSSContent(urlRSS);
                } else {
                    textView.setText(textContent);
                }

            }
            if(!json.isNull("size")){
                float font_size = (float)json.getInt("size");
                textView.setTextSize(font_size);
            }

//            if(!json.isNull("mMarquee")){
//                float text_speed = (float)json.getInt("mMarqueee");
//                textView.setText((int) text_speed);
//            }
            textView.setText(textContent);

//            backgroundColor = Color.parseColor(backColor);
//            foreColor = Color.parseColor(textColor);
//            Log.d(TAG,"Content Text : "+textContent);
        } catch (Exception ex){
            Log.e(TAG,"Error : "+ex.getMessage());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                startScroll();
            }
        }).start();

        return view;
    }

    public static void setMarqueeSpeed(TextView tv, float speed) {
        if (tv != null) {
            try {
                Field f = null;
                if (tv instanceof AppCompatTextView) {
                    f = tv.getClass().getSuperclass().getDeclaredField("mMarquee");
                } else {
                    f = tv.getClass().getDeclaredField("mMarquee");
                }
                if (f != null) {
                    f.setAccessible(true);
                    Object marquee = f.get(tv);
                    if (marquee != null) {
                        String scrollSpeedFieldName = "mScrollUnit";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            scrollSpeedFieldName = "mPixelsPerSecond";
                        }
                        Field mf = marquee.getClass().getDeclaredField(scrollSpeedFieldName);
                        mf.setAccessible(true);
                        mf.setFloat(marquee, speed);
                    }
                } else {
                    Log.e("Marquee", "mMarquee object is null.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void loadRSSContent(String urlString){
        //String urlString = "http://www.androidcentral.com/feed";
        Log.d(TAG,"rss url : "+urlString);
        AsyncHttpClient client = new AsyncHttpClient();

        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.get(urlString, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String rss_content = new String(responseBody);
                    RssFeed feed = RssReader.read(rss_content);

                    ArrayList<RssItem> rssItems = feed.getRssItems();
                    StringBuilder strBuilder = new StringBuilder();
                    int i=0;
                    for(RssItem rssItem : rssItems) {
//                        Log.i(TAG, "rss content : "+rssItem.getContent());
                        if(i<2){
                            strBuilder.append(rssItem.getTitle()).append(" ");
                        }

                        i++;
                    }
                    textView.setText(strBuilder.toString());
//                    textView.setCharacterList(strBuilder.toString().toCharArray());
                    textView.setSelected(true);
                } catch (Exception ex){
                    Log.d(TAG,"rss error : "+ex.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
