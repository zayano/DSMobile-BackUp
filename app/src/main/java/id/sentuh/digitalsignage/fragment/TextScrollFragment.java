package id.sentuh.digitalsignage.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;


import java.io.File;

import id.sentuh.digitalsignage.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextScrollFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_INDEX = "page_index";
    private static final String PAGER_INDEX = "index_pager";
    static final String TAG ="HTML";

    private String mParam1;
    private int mIndexPage;
    private int mPagerIndex;
    private int DELAY_TIME = 5000;
    Handler mHandler;
    //    OnPageFinishListener mListener;
    private String webUrl;
    WebView webView;
    String textContent;
    private String textColor = "#ffffff";
    private String backColor ="#cccccc";
    private String fontSize = "28";
    public TextScrollFragment() {
        // Required empty public constructor
    }
    public static TextScrollFragment newInstance(String param1, int index, int indexPager) {
        TextScrollFragment fragment = new TextScrollFragment();
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
//        mListener = (OnPageFinishListener) getContext();
        mHandler = new Handler();
        Log.d(TAG,"scroller json : "+mParam1);
        try {
            JSONObject json = new JSONObject(mParam1);
            textContent = json.getString("text").replace("\n","");

            if(!json.isNull("background_color")){
                backColor = json.getString("background_color");

            }
            if(!json.isNull("color")){
                textColor = json.getString("color");

            }
            if(!json.isNull("size")){
                fontSize = json.getString("size");
                Log.d(TAG,"text size : "+fontSize);
            }
        } catch (Exception ex){
            Log.e(TAG,"error load text style : "+ex.getMessage());
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
        webView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        WebSettings settings = webView.getSettings();
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);

        settings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        File file = getActivity().getExternalCacheDir();
        settings.setAppCachePath(file.getAbsolutePath());
        settings.setLoadsImagesAutomatically(true);
        settings.setAppCacheEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading (WebView view, String url){
                //True if the host application wants to leave the current WebView and handle the url itself, otherwise return false.
                return false;
            }
        });
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        String htmlContent = generateHTML(textContent);
        webView.loadData(htmlContent,"text/html",null);

        return view;
    }
    public void reloadWeb(){
        if(webView!=null){
            //webView.reload();
        }

    }
    private String generateHTML (String content ){
        int cwidth = content.length()*8;
        String html = "<!DOCTYPE html>\n<html>" +
                "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />" +
                "<title></title><style>" +
                "body { background-color:"+backColor+";color:"+textColor+";}\n" +
                "marquee { height: 100%; margin:10px; font-size:"+fontSize+"px;color:"+textColor+";}\n" +
                "</style></head>" +
                "<body><marquee> " +content+"</marquee></body></html>";
        String html2 = "<!DOCTYPE html>\n<html>" +
                "<head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" /><title></title><style>" +
                "body { background-color:"+backColor+";color:"+textColor+";}\n" +
                ".marquee {\n" +
                "    width: 450px;\n" +
                "    margin: 0 auto;\n" +
                "    white-space: nowrap;\n" +
                "    overflow: hidden;\n" +
                "    box-sizing: border-box;\n" +
                "}\n" +
                "\n" +
                ".marquee span {\n" +
                "    display: inline-block;\n" +
                "    padding-left: 100%;  /* show the marquee just outside the paragraph */\n" +
                "    animation: marquee 15s linear infinite;\n" +
                "}\n" +
                "\n" +
                ".marquee span:hover {\n" +
                "    animation-play-state: paused\n" +
                "}\n" +
                "\n" +
                "/* Make it move */\n" +
                "@keyframes marquee {\n" +
                "    from   { text-indent: 100%}\n" +
                "    to { text-indent: -100%}\n" +
                "}" +
                "</style></head>" +
                "<body><div class=\"marquee\" " +
                content+"</div></body></html>";

        String htmlBy = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />" +
                "<style>\n" +
                "body { background-color:"+backColor+";}" +
                ".example1 {\n" +
                " display: table; height: 50px;\t\n" +
                " overflow: hidden;\n" +
                " position: relative;\n" +
                "}\n" +
                ".example1 #texting {\n" +
                " " +
                "font-size: "+fontSize+"px;\n" +
                " color: "+textColor+";\n" +
                " position: absolute;\n" +
                " width: 100%;\n" +
                " height: 100%;\n" +
                " margin: 0;\n" +
                " line-height: 50px;\n" +
                " text-align: center;\n" +
                " /* Starting position */\n" +
                " -moz-transform:translateX(100%);\n" +
                " -webkit-transform:translateX(100%);\t\n" +
                " transform:translateX(100%);\n" +
                " /* Apply animation to this element */\t\n" +
                " -moz-animation: example1 15s linear infinite;\n" +
                " -webkit-animation: example1 15s linear infinite;\n" +
                " animation: example1 15s linear infinite;\n" +
                "}\n" +
                "/* Move it (define the animation) */\n" +
                "@-moz-keyframes example1 {\n" +
                " 0%   { -moz-transform: translateX(100%); }\n" +
                " 100% { -moz-transform: translateX(-100%); }\n" +
                "}\n" +
                "@-webkit-keyframes example1 {\n" +
                " 0%   { -webkit-transform: translateX(100%); }\n" +
                " 100% { -webkit-transform: translateX(-100%); }\n" +
                "}\n" +
                "@keyframes example1 {\n" +
                " 0%   { \n" +
                " -moz-transform: translateX(100%); /* Firefox bug fix */\n" +
                " -webkit-transform: translateX(100%); /* Firefox bug fix */\n" +
                " transform: translateX(100%); \t\t\n" +
                " }\n" +
                " 100% { \n" +
                " -moz-transform: translateX(-100%); /* Firefox bug fix */\n" +
                " -webkit-transform: translateX(-100%); /* Firefox bug fix */\n" +
                " transform: translateX(-100%); \n" +
                " }\n" +
                "}\n" +
                "</style>\n" +
                "\n" +
                "<body>\t\n" +
                "<div class=\"example1\">\n" +
                "<div id=\"texting\">"+content+"</div>\n" +
                "</div></body></html>";
//        Log.d(TAG,"html:"+html);
        return html;
    }

}
