package id.sentuh.digitalsignage.fragment;


import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.net.MalformedURLException;

import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.app.AppUtils;
import id.sentuh.digitalsignage.helper.OnDialogListener;


public class HtmlDialogFragment extends DialogFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_INDEX = "page_index";
    private static final String PAGER_INDEX = "index_pager";
    static final String TAG ="HTML";

    //
    private String webUrl;

    private int DELAY_TIME = 15000;
    Handler mHandler;
    OnDialogListener mListener;
    public HtmlDialogFragment() {
        // Required empty public constructor
    }
    public static HtmlDialogFragment newInstance(String param1) {
        HtmlDialogFragment fragment = new HtmlDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            webUrl = getArguments().getString(ARG_PARAM1);

        }
        mListener = (OnDialogListener) getActivity();
        mHandler = new Handler();
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHandler.postDelayed(mRunnable,DELAY_TIME);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view= inflater.inflate(R.layout.fragment_html, container, false);
        WebView webView = view.findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        if(webUrl.contains("youtube.com")){
            String youtubeid = null;
            settings.setJavaScriptEnabled(true);
            try {
                youtubeid = AppUtils.extractYoutubeId(webUrl);
                String strContent = generateHTML(youtubeid);
                webView.loadData(strContent,"text/html",null);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        } else {
            settings.setJavaScriptCanOpenWindowsAutomatically(false);
            settings.setJavaScriptEnabled(false);
            webView.loadUrl(webUrl);
        }
        return view;
    }
    private String generateHTML (String youtubeid ){
        String html = "<!DOCTYPE html>\n<html><body><iframe width=\"900\" height=\"600\" src=\"https://www.youtube.com/embed/"+
                youtubeid+"\" frameborder=\"0\" allow=\"autoplay; encrypted-media\"></iframe></body></html>";
        return html;
    }
    Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            //
            mListener.OnDialogClose(true);
           dismiss();
        }
    };
}
