package id.sentuh.digitalsignage.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.app.AppUtils;

public class ClockFragment extends Fragment {
    private static final String ARG_PARAM1 = "content";
    private static final String TAG = "Clock";
    private static final String ARG_INDEX = "page_index";
    private static final String PAGER_INDEX = "index_pager";
    private String textColor = "#ffffff";
    private String backColor ="#cccccc";
//    private String textContent;
    private int fontSize = 40;
//    private int backgroundColor;
//    private int foreColor;
    private String mParam1;
    private int mIndexPage;
    private int mPagerIndex;
    Typeface fontDefault;
    Handler mHandler;
    TextView textTime;
    private static int DELAY = 1000;
//    TextView textDate;
    public static ClockFragment newInstance(String param1,int index,int indexPager) {
        ClockFragment fragment = new ClockFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_INDEX, index);
        args.putInt(PAGER_INDEX, indexPager);
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
        }
        fontDefault = AppUtils.getFont(getContext(),"OpenSans-Regular");

        mHandler = new Handler();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.clock_fragment, container, false);
        LinearLayout layout = view.findViewById(R.id.layout_clock);
        textTime = view.findViewById(R.id.text_time);
        textTime.setTypeface(fontDefault);

//        String json_string =  String.format("{\"result\": %s }",mParam1);
        //Log.d(TAG,"Content Text : "+json_string);
        try {
            JSONObject json = new JSONObject(mParam1);
//            textContent = json.getString("text");
            if(!json.isNull("background_color")){
                backColor = json.getString("background_color");
                //view.setBackgroundColor(Color.parseColor(backColor));
                if(backColor.length()>1){
                    layout.setBackgroundColor(Color.parseColor(backColor));
                } else {
                    layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }

            }
            if(!json.isNull("color")){
                textColor = json.getString("color");
                textTime.setTextColor(Color.parseColor(textColor));
            }
            if(!json.isNull("size")){
                String strSize = json.getString("size");
                fontSize = Integer.parseInt(strSize);

                textTime.setTextSize(fontSize);
                Log.d(TAG,"Size exist");
            } else {
                Log.d(TAG,"Size not exist");
            }

            //textView.setText(textContent);

            if(!json.isNull("size")){
                float font_size = (float)json.getInt("size");
                textTime.setTextSize(font_size);
            }



        } catch (Exception ex){
            Log.e(TAG,"Error : "+ex.getMessage());
        }
        Date tanggal = new Date(System.currentTimeMillis());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        if(textTime!=null){
            textTime.setText(timeFormat.format(tanggal));
        }
        mHandler.postDelayed(mRunnableUpdateTime,DELAY);
        return view;
    }
    private Runnable mRunnableUpdateTime = new Runnable(){

        @Override
        public void run() {
            Date tanggal = new Date(System.currentTimeMillis());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

            if(textTime!=null){
                textTime.setText(timeFormat.format(tanggal));
            }

            mHandler.postDelayed(mRunnableUpdateTime,DELAY);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnableUpdateTime);
    }
}
