package id.sentuh.digitalsignage.fragment;


import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.app.AppUtils;
import id.sentuh.digitalsignage.helper.OnDialogListener;

public class TextDialogFragment extends DialogFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "content";
    private static final String TAG = "Text Fragment";
    private String mParam1;
    private String textColor;
    private String backColor;
    private String textContent;
    private String multiLine;
    private int backgroundColor;
    private int foreColor;
    Handler mHandler;
    private int DELAY_TIME = 15000;
    private TextView textView;
    Typeface fontDefault;
    OnDialogListener mListener;
    public TextDialogFragment() {
        // Required empty public constructor
    }


    public static TextDialogFragment newInstance(String param1) {
        TextDialogFragment fragment = new TextDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHandler.postDelayed(mRunnable,DELAY_TIME);

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        fontDefault = AppUtils.getFont(getActivity(),"OpenSans-Regular");
        mHandler = new Handler();
        mListener = (OnDialogListener) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        int width = 1080;
        int height = 920;
        getDialog().getWindow().setLayout(width, height);
        View view = inflater.inflate(R.layout.dialog_text, container, false);
        LinearLayout layout = view.findViewById(R.id.layoutDialogText);
        textView = view.findViewById(R.id.text_content);
        textView.setTypeface(fontDefault);
        //String json_string =  String.format("{\"result\": %s }",mParam1);
        //Log.d(TAG,"Content Text : "+json_string);
        try {
            JSONObject json = new JSONObject(mParam1);
            textContent = json.getString("text");
            textView.setText(textContent);

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
              //  scrollSpeed = json.getDouble("speed")* 1000;

            }
            if(!json.isNull("size")){
                float font_size = (float)json.getInt("size");
                textView.setTextSize(font_size);
            }

            //textView.setSelected(true);
            Log.d(TAG,"Content Text : "+textContent);
        } catch (Exception ex){
            Log.e(TAG,"Error : "+ex.getMessage());
        }

//        if(multiLine.equals("multiline")){
//            textView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//            textView.setSingleLine(false);
//        } else {
//            textView.setSingleLine(true);
//        }

        return view;
    }
    Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            mListener.OnDialogClose(true);
           dismiss();
        }
    };
}
