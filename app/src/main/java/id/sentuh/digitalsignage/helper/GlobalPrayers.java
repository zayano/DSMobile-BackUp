package id.sentuh.digitalsignage.helper;

import java.util.Calendar;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class GlobalPrayers {
	public static final String TAG = "Prayer Time";
	public static final String PREFS_NAME = "prayer_time_preference";
	private Context mContext;
	private SharedPreferences settings;
	private String timezone;
	private float fajr_angle;
	private float isya_angle;
	private float gslat;
	private float gslong;
	private float gselev = 50.0f;
	private PrayerTimes pt;
	private int NextPrayerIndex;
	private String[] PrayerLabels = {"Dhzuhur", "Ashar", "Maghrib", "Isya'", "Fajr", "Syuruq"};
	private String[] strPrayers;
	private Calendar ThisTime;
	private long[] TimePrayers;
	private int intCorrection;

	public GlobalPrayers(Context context,float latitude,float longitude) {
		this.mContext = context;
		NextPrayerIndex = 0;
		TimePrayers = new long[6];
		strPrayers = new String[6];
		this.gslat = latitude;
		this.gslong = longitude;
		try {
			ThisTime = Calendar.getInstance();

			//settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        	
        	settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        	timezone=settings.getString("location_timezone", "UTC+07:00");
        	fajr_angle=settings.getFloat("fajar_angle", 18f);
        	isya_angle=settings.getFloat("isya_angle", 20f);
        	intCorrection=settings.getInt("hijri_correction", +1);
			SharedPreferences.Editor editor=settings.edit();
			editor.putFloat("latitude", gslat);
			editor.putFloat("longitude", gslong);
			editor.putFloat("location_elev", gselev);
			editor.commit();
        	
        	intCorrection=settings.getInt("hijri_correction", 0);
        	pt=new PrayerTimes(ThisTime);
        	pt.setLocation(gslat, gslong);
        	pt.setTimeZone(timezone);
        	pt.setElevationAngle(gselev, fajr_angle, isya_angle);
        	pt.LoadAllTime();
        	TimePrayers[0]=pt.getDzuhurCal();
        	TimePrayers[1]=pt.getAsharCal();
        	TimePrayers[2]=pt.getMaghribCal();
        	TimePrayers[3]=pt.getIsyaCal();
        	TimePrayers[4]=pt.getFajrCal();
        	TimePrayers[5]=pt.getSyuruqCal();
        	strPrayers[0]=pt.strZuhurTime();
        	strPrayers[1]=pt.strAsharTime();
        	strPrayers[2]=pt.strMagribTime();
        	strPrayers[3]=pt.strIsyaTime();
        	strPrayers[4]=pt.strFajarTime();
        	strPrayers[5]=pt.strSunriseTime();
        	 
		} catch (Exception ex){
			Log.e(TAG, "Prayer Time Error on :"+ex.getMessage());
		}
	}
	public long getZuhurTime(){
		return pt.getDzuhurCal();
	}
	public long getAsharTime(){
		return pt.getAsharCal();
	}
	public long getMaghribTime(){
		return pt.getMaghribCal();
	}
	public long getIsyaTime(){
		return pt.getIsyaCal();
	}
	public long getFajrTime(){
		return pt.getFajrCal();
	}
	public long getSyuruqTime(){
		return pt.getSyuruqCal();
	}
	public int getNextPrayerIndex(){
		if (ThisTime.getTimeInMillis()<pt.getDzuhurCal()){
			NextPrayerIndex=0;
		} else if (ThisTime.getTimeInMillis()<pt.getAsharCal()){
			NextPrayerIndex=1;
		} else if (ThisTime.getTimeInMillis()<pt.getMaghribCal()){
			NextPrayerIndex=2;
		} else if (ThisTime.getTimeInMillis()<pt.getIsyaCal()){
			NextPrayerIndex=3;
		} else if (ThisTime.getTimeInMillis()<pt.getFajrCal()){
			NextPrayerIndex=4;
		} else {
			NextPrayerIndex=5;
		}
		return NextPrayerIndex;
	}
	public String getNextPrayer(){
		return PrayerLabels[getNextPrayerIndex()];
	}
	public long getNextPrayerTime(){
		return TimePrayers[getNextPrayerIndex()];
	}
	public long[] getAllPrayerTimes (){
		return TimePrayers;
	}
	public String[] getAllPrayerTimeString(){
		return strPrayers;
	}
	public String MasehiToHijriah(int d,int m, int y){
        String[] monthHijr={"Muharam","Safar","Rabi'ul Ula", "Rabi'ul Akhir", 
            "Jumadil Ula","Jumadil Akhir","Rajab", "Sya'ban","Ramadhan",
            "Syawal","Dzulqa'idah","Dzulhijjah"};
        int jd;
        if ((y>1582)||((y==1582)&&(m>10))||((y==1582)&&(m==10)&&(d>14))) {
            jd= (1461*(y+4800+ (m-14)/12))/4 + (367*(m-2-12* (m-14)/12))/12 -
                    (3* (y+4900+ (m-14)/12)/100) /4 +d-32075;

        } else {
            jd = 367*y- (7*(y+5001+ (m-9)/7))/4 + (275*m)/9 +d+1729777;

        }
        int l=jd-1948440+10632;
        int n= (l-1)/10631;
        l=l-10631*n+354;
        int j= (10985-l)/5316 * (50*l)/17719 + l/5670 * (43*l)/15238;
        l=l- (30-j)/15 * (17719*j)/50 - j/16 * (15238*j)/43 +29;
 	m= (24*l)/709;
 	int day=l- (709*m)/24;
 	int year=30*n+j-30;


    return (Integer.toString(day) + " " + monthHijr[m] + " " + Integer.toString(year));

        
    }
	
	public String getHijriahDate(){
		String dayH="";
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE, intCorrection);
		try {
			int day=cal.get(Calendar.DAY_OF_MONTH);
			int mnth=cal.get(Calendar.MONTH)+1;
			int yrs=cal.get(Calendar.YEAR);
			dayH=MasehiToHijriah(day,mnth, yrs);
			 
			Log.d("TANGGAL"," : "+Integer.toString(day)+":"+
					Integer.toString(mnth)+":"+Integer.toString(yrs));
		} catch (Exception ex){
			return ex.toString();
			
		}
		  
		return dayH;
	}
	public boolean CheckForPlay(){
		long timenow=getNextPrayerTime();
		Calendar cal=Calendar.getInstance();
		
		return cal.getTimeInMillis()>=timenow; 
	}
}
