package id.sentuh.digitalsignage.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.app.AppUtils;
import id.sentuh.digitalsignage.app.Configurate;
import id.sentuh.digitalsignage.app.EndPoints;

import static android.content.ContentValues.TAG;
import static id.sentuh.digitalsignage.app.AppUtils.registerDevice;

@SuppressLint("Registered")
public class WeatherFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_INDEX = "page_index";
    private static final String PAGER_INDEX = "index_pager";
    private Context mContext;
    private String mParam1;
    private int mIndexPage;
    private int mPagerIndex;
    TextView t1_date, t2_city, t3_celcius, t4_weather;
    ImageView weather_img;

    public static WeatherFragment newInstance(String param1, int index, int pagerIndex){
     WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_INDEX, index);
        args.putInt(PAGER_INDEX, pagerIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() !=null){
            mParam1 = getArguments().getString(ARG_PARAM1);
            mIndexPage = getArguments().getInt(ARG_INDEX);
            mPagerIndex = getArguments().getInt(PAGER_INDEX);
        }
        mContext = getContext();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //videoView.setVideoURI(Uri.parse(mParam1));
//        player.setPlayWhenReady(true); //run file/link when ready to play.
        getIpAddrLatLon();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        ConstraintLayout layout = view.findViewById(R.id.layoutWeather);
//        t1_date = view.findViewById(R.id.tv_date);
        t2_city = view.findViewById(R.id.tv_city);
        t3_celcius = view.findViewById(R.id.tv_celcius);
        t4_weather = view.findViewById(R.id.tv_weather);
        weather_img = view.findViewById(R.id.im_weather);

//        find_weather();

        return view;
    }

    public void getIpAddrLatLon(){
        final String mac_address = null;
        final String ip_local = null;
        final Configurate config = new Configurate(mContext);

        @SuppressLint("HardwareIds")
        final String android_id = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        try {
            Log.d(TAG,"getting ip address and location...");
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(EndPoints.GET_IPADRESS, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String json_string = new String(responseBody);
                        Log.d(TAG,"json ip : "+json_string);
                        JSONObject json = new JSONObject(json_string);
                        String status = json.getString("status");
                        if(status.equals("success")){
                            final String public_ip = json.getString("query");
                            final float latitude = (float)json.getDouble("lat");
                            final float longitude = (float)json.getDouble("lon");
                            config.setKeyString("ip_address",public_ip);
                            config.setKeyFloat("latitude",latitude);
                            config.setKeyFloat("longitude",longitude);
                            int templateid = config.getPageId();
                            registerDevice(mContext,android_id,latitude,longitude,public_ip,ip_local,mac_address,templateid);

                            String url = "https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&APPID=54b5df224b574bb5814c69bf77946e3a";
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
                            client.get(url, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    try {
                                        String json_string = new String(responseBody);
                                        JSONObject response = new JSONObject(json_string);
                                        JSONObject jsonObject = response.getJSONObject("main");
                                        JSONArray array = response.getJSONArray("weather");
                                        JSONObject object = array.getJSONObject(0);

                                        String temp = String.valueOf(jsonObject.getDouble("temp"));
                                        String description = object.getString("description");
                                        String city = response.getString("name");
                                        String icon = object.getString("icon");
                                        String imgUrl = "http://openweathermap.org/img/w/" + icon + ".png";

                                        Picasso.with(getContext())
                                                .load(imgUrl)
                                                .error(R.drawable.pagenotfound)
                                                .into(weather_img);

                                        double temp_int = Double.parseDouble(temp);
                                        double centi = (temp_int - 273.15);
                                        centi = Math.round(centi);
                                        int i = (int)centi;
                                        t3_celcius.setText(String.valueOf(i+"°C"));

                                        t2_city.setText(city);
                                        t4_weather.setText(description);

                                        Calendar calendar =  Calendar.getInstance();
                                        @SuppressLint("SimpleDateFormat")
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                                        String formatted_date = simpleDateFormat.format(calendar.getTime());

                                        Log.d(TAG,"json weather : "+json_string);
                                    } catch (Exception ex){
                                        Log.e(TAG,ex.getMessage());
                                    }

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                }
                            });
                        }

                    } catch (Exception ex){
                        Log.e(TAG,"error convert json : "+ex.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(mContext,"Kegagalan Mengambil Alamat IP!",
                            Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception ex){
            Log.e(TAG,"error get ip address : "+ex.getMessage());
        }
    }

//

}
