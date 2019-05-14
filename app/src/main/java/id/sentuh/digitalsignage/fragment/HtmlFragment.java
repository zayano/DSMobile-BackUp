package id.sentuh.digitalsignage.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import org.json.JSONObject;

import java.net.MalformedURLException;

import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.app.AppUtils;
import id.sentuh.digitalsignage.helper.OnPageFinishListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class HtmlFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_INDEX = "page_index";
    private static final String PAGER_INDEX = "index_pager";
    static final String TAG ="HTML";

    private String mParam1;
    private int mIndexPage;
    private int mPagerIndex;
    private int DELAY_TIME = 5000;
    //    Handler mHandler;
    OnPageFinishListener mListener;
    private String webUrl;
    WebView webView;
    public HtmlFragment() {
        // Required empty public constructor
    }
    public static HtmlFragment newInstance(String param1,int index,int indexPager) {
        HtmlFragment fragment = new HtmlFragment();
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
        mListener = (OnPageFinishListener) getContext();
//        mHandler = new Handler();
        try {
            JSONObject json = new JSONObject(mParam1);
            if(!json.isNull("url")){
                webUrl = json.getString("url");
            }
        } catch (Exception ex){

        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // mHandler.postDelayed(mRunnable,DELAY_TIME);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_html, container, false);
        webView = view.findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLoadsImagesAutomatically(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading (WebView view, String url){
                //True if the host application wants to leave the current WebView and handle the url itself, otherwise return false.
                return false;
            }
        });
        if(webUrl.contains("youtube.com")){
            String youtubeid = null;
            settings.setJavaScriptEnabled(true);
            settings.setAllowContentAccess(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            try {
                youtubeid = AppUtils.extractYoutubeId(webUrl);
                String strContent = generateHTML(youtubeid);
                webView.loadData(strContent,"text/html",null);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        } else {
            settings.setJavaScriptCanOpenWindowsAutomatically(false);
            settings.setJavaScriptEnabled(true);
            webView.loadUrl(webUrl);
        }

//
        return view;
    }
    public void reloadWeb(){
        if(webView!=null){
            //webView.reload();
        }

    }
    private String generateHTML (String youtubeid ){
        String html = "<!DOCTYPE html>\n<html><body><iframe width=\"900\" height=\"600\" src=\"https://www.youtube.com/embed/"+
                youtubeid+"\" frameborder=\"0\" allow=\"autoplay; encrypted-media\" allowfullscreen></iframe></body></html>";
        return html;
    }
    //    <iframe width="560" height="315" src="https://www.youtube.com/embed/yh7J8UBkx8E" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
    Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            mListener.OnPageFinish(mPagerIndex,true);
            // mHandler.postDelayed(mRunnable,DELAY_TIME);
        }
    };
}
