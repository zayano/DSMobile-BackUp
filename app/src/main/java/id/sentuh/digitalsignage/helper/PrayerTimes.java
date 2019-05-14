package id.sentuh.digitalsignage.helper;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
public class PrayerTimes {
   private float LatLocation; 
   private float LongLocation; 
   private float TimeZones;//UTC+n
   private float ElevHigh; //meter
   private int curDay;
   private int curMonth;
   private int curYear;
   private float FajarAngle;
   private float IsyaAngle;
   private float Sunrise;
   private float ZuhurTime;
   private String fTitle;
//   private String Hijriah;
   private long FajrTime;
   private long IsyaTime;
   private long MaghribTime;
   private long DzuhurTime;
   private long IsyraqTime;
   private long AsharTime;
   private Calendar cal;
   public void setTitle(String title){
	   this.fTitle=title;
   }
//   public PrayerTimes(String CurrentDate){
//
//      // this.TimeZones = Z;
//
//       String[] datepart = CurrentDate.split("-");
//       this.curDay = Integer.parseInt(datepart[0]);
//       this.curMonth = Integer.parseInt(datepart[1]);
//       this.curYear = Integer.parseInt(datepart[2]);
//       //this.fTitle=getLocation(L, B);
//       //Hijriah=MasehiToHijriah(this.curDay,this.curMonth,this.curYear);
//       initlong();
//   }
   private void initlong(){
	   FajrTime=0;
	   DzuhurTime=0;
	   MaghribTime=0;
	   AsharTime=0;
	   IsyaTime=0;
	   IsyraqTime=0;
   }
   public PrayerTimes (Calendar CurrentDate){
	   this.curDay=CurrentDate.get(Calendar.DAY_OF_MONTH);
	   this.curMonth=CurrentDate.get(Calendar.MONTH)+1;
	   this.curYear=CurrentDate.get(Calendar.YEAR);
	   //this.Hijriah=MasehiToHijriah(this.curDay,this.curMonth,this.curYear);
	   initlong();
   }
   public void setLocation(float Latitude,float Longitude){
	   this.LatLocation = Latitude;
       this.LongLocation = Longitude;
   }
   public void setTimeZone(String TZ){
	   if (TZ.contains("UTC")){
		   String[] dg=TZ.substring(3).split(":");
                   
		   if (dg[0]!=null){
                   this.TimeZones=(float)Double.parseDouble(dg[0])+(float)Double.parseDouble(dg[1])/60;
                  // System.out.println("Tz double :"+this.TimeZones);
                   } else this.TimeZones=0f;
	   } else {
		  
		   this.TimeZones=7f;
	   }
   }
   public void setElevationAngle (float elevation,float a_Fajar,float a_Isya){
	   this.ElevHigh = elevation;
       this.FajarAngle=a_Fajar;
       this.IsyaAngle=a_Isya;
   }
   
   
   public String getLocation(float latitude,float longitude){
		String strUrl="http://mesinit.com/getlocation.php?lat="+
		Double.toString(latitude)+"&long="+Double.toString(longitude);
		try {
		URL geoUrl=new URL(strUrl);
		InputStream is=geoUrl.openConnection().getInputStream();
		
		BufferedReader br
    	= new BufferedReader(
    		new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		 
    	String line;
    	while ((line = br.readLine()) != null) {
    		sb.append(line);
    	} 
    	
    	return sb.toString();
		} catch (IOException ex){
			return ex.getMessage();
		}
		
	}
   public float JulianDay(int Day,int Month, int Year) {
       float JD;
       int A =(int)Math.floor(Year/100);
       float B = 2 + A/4 -A;
       if (Month==1||Month==2){
           Month+=12;
           Year--;
       }
       JD= (float)(1720994.5f + Math.floor(365.25f*Year) + Math.floor(30.6001f*(Month + 1f)) + B + Day);
       return JD;

   }
   public float AngleDate(float JulianDay){
       float T = (float) (2 * Math.PI * (JulianDay - 2451545f))/365.25f;
       return T;
   }
   public float getDelta(float T){
       float fDelta;
       fDelta = (float)(0.37877f + 23.264*Math.sin(Math.toRadians(57.297* T - 79.547f)) +
               0.3812f * Math.sin(Math.toRadians(2 * 57.297f* T - 82.682f)) +
               0.17132f* Math.sin(Math.toRadians(3 * 57.297f * T - 59.722f)));
       return (float)Math.toRadians(fDelta);
   }
   public float getHourAngle(float Altitude,float fDelta){
       
       float CosHA= (float)((Math.sin(Altitude)-(Math.sin(fDelta)*Math.sin(Math.toRadians(LatLocation))))/
               (Math.cos(fDelta)*Math.cos(Math.toRadians(LatLocation))));
      
       float HA=(float)Math.acos(CosHA);
  
       float HAngle = (float)Math.toDegrees(HA);
      
  
       return HAngle;
   }
   
   public float getAltitudeAshar(float fDelta){
       fDelta=(float)Math.toDegrees(fDelta);
       float F=1 + (float)Math.tan(Math.toRadians(Math.abs(fDelta-this.LatLocation)));
       float Alt=(float)(Math.atan((1/F)));
       float AltN=(float)Math.toDegrees(Alt);
      
       float Koreksi=(float)(1/(60*Math.tan((AltN + 7.31d/(AltN+4.4d))*Math.PI/180)));
       float AltS=(float)Math.toRadians(AltN-Koreksi);
       return AltS;
   }
   private float getMaghribAltitude(){
       float MA=-0.833333f - (float)( 0.03470f * Math.sqrt(this.ElevHigh));
       return (float)Math.toRadians(MA);
   }
   private float EquationTime(float JD){ //satuan menit
       
       float U = (JD - 2451545f)/36525f;
       float L0 = (float)Math.toRadians(280.46607f + 36000.7698 * U);
       float ET = (float)(-1*(1789 + 237 * U)* Math.sin(L0) -
               (7146 - 62*U) * Math.cos(L0) +
               (9934 - 14*U) * Math.sin(2*L0) -
               (29 + 5 * U) * Math.cos( 2 * L0) +
               (74 + 10 * U) * Math.sin (3*L0) +
               (320 - 4* U) * Math.cos(3*L0) -
               212 * Math.sin(4*L0))/1000;
       return ET;
   }
   private String LeadZero(int n,int digits){
       char[] zeros = new char[digits];
    Arrays.fill(zeros, '0');
    DecimalFormat df = new DecimalFormat(String.valueOf(zeros));

    return df.format(n);
   }
   public String floatToTime(float sTime){
	   int dHours=(int)Math.floor(sTime);
	   if (dHours>23){
    	   dHours=dHours-24;
       }
	   String sHours  = LeadZero(dHours,2);
       float fMinutes= (float)((sTime - Math.floor(sTime))*60);
       String sMinutes = LeadZero((int)Math.floor(fMinutes),2);
       //float fSeconds = (float) ((fMinutes-Math.floor(fMinutes))*60);
       //String sSeconds = LeadZero((int)fSeconds,2);
       
       return " "+sHours+":"+sMinutes;
   }
   public long floatToTimeMillis(float sTime){
	   int dHours=(int)Math.floor(sTime);
	   if (dHours>23){
    	   dHours=dHours-24;
       }
	   cal=Calendar.getInstance();
       int minutes= (int)((sTime - Math.floor(sTime))*60);
       cal.set(curYear, curMonth, curDay, dHours, minutes);
       return cal.getTimeInMillis();
       
   }
   public float getZuhurTime(){
       float JD = JulianDay(curDay, curMonth, curYear);
       float ET=EquationTime(JD);
       this.ZuhurTime= 12 + TimeZones - LongLocation / 15 - ET/60;
       return this.ZuhurTime;
   }
   public float getAsharTime(){
       
       float JD = JulianDay(curDay, curMonth, curYear)-TimeZones/24;
       float fDelta=getDelta(AngleDate(JD));
       float Altitude=getAltitudeAshar(fDelta);
       float Ashar=this.ZuhurTime + getHourAngle(Altitude,fDelta)/15;
       return Ashar;
   }
   public float getMaghribTime(){
     
       float JD = JulianDay(curDay, curMonth, curYear)-TimeZones/24;
       float fDelta=getDelta(AngleDate(JD));
       float Altitude=getMaghribAltitude();
      
       float Maghrib=this.ZuhurTime + getHourAngle(Altitude, fDelta)/15;
       this.Sunrise=this.ZuhurTime - getHourAngle(Altitude, fDelta)/15;
       return Maghrib;
   }
   public float getSyuruqTime(){
	     
       float JD = JulianDay(curDay, curMonth, curYear)-TimeZones/24;
       float fDelta=getDelta(AngleDate(JD));
       float Altitude=getMaghribAltitude();
      
       float Maghrib=this.ZuhurTime + getHourAngle(Altitude, fDelta)/15;
       this.Sunrise=this.ZuhurTime - getHourAngle(Altitude, fDelta)/15;
       return this.Sunrise;
   }
   public float getIsyaTime(){
       float JD = JulianDay(curDay, curMonth, curYear)-TimeZones/24;
       float fDelta=getDelta(AngleDate(JD));
       float Altitude=(float)Math.toRadians(-IsyaAngle);
      
       float Isya=this.ZuhurTime+getHourAngle(Altitude, fDelta)/15;
       return Isya;
   }
   public void LoadAllTime(){
	         
       this.DzuhurTime=floatToTimeMillis(getZuhurTime());
       this.AsharTime=floatToTimeMillis(getAsharTime());
       this.MaghribTime=floatToTimeMillis(getMaghribTime());
       this.IsyaTime=floatToTimeMillis(getIsyaTime());
       this.IsyraqTime=floatToTimeMillis(getSyuruqTime());
       this.FajrTime=floatToTimeMillis(getFajarTime());
   }
   public float getFajarTime(){
       float JD = JulianDay(curDay, curMonth, curYear)-TimeZones/24;
       float fDelta=getDelta(AngleDate(JD));
       float Altitude=(float)Math.toRadians(-FajarAngle);

       float Fajar=this.ZuhurTime-getHourAngle(Altitude, fDelta)/15;
       return Fajar;
   }
   public String strZuhurTime(){
       this.getZuhurTime();
       return floatToTime(this.ZuhurTime);
   }
   public String strAsharTime(){
       float Ashar=getAsharTime();
       return floatToTime(Ashar);
   }
   public String strIsyaTime(){
       float Isya=getIsyaTime();
       return floatToTime(Isya);
   }
   public String strFajarTime(){
       float fajar=getFajarTime();
       return floatToTime(fajar);
   }
   public String strMagribTime(){
       float Maghrib=getMaghribTime();
       return floatToTime(Maghrib);
   }
   public String strSunriseTime(){
       return floatToTime(getSyuruqTime());
   }
   public long getDzuhurCal(){
	   return this.DzuhurTime;
   }
   public long getAsharCal(){
	   return this.AsharTime;
   }
   public long getMaghribCal(){
	   return this.MaghribTime;
   }
   public long getIsyaCal(){
	   return this.IsyaTime;
   }
   public long getFajrCal(){
	   return this.FajrTime;
   }
   public long getSyuruqCal(){
	   return this.IsyraqTime;
   }
   public ArrayList<String> getTimeArray(){
       ArrayList<String> atime=new ArrayList<String>();
       atime.add(fTitle);
       atime.add("Zuhur    : "+this.strZuhurTime());
       atime.add("Ashar    : "+this.strAsharTime());
       atime.add("Maghrib  : "+this.strMagribTime());
       atime.add("Isya'    : "+this.strIsyaTime());
       atime.add("Fajr     : "+this.strFajarTime());
       atime.add("Sunrise  : "+this.strSunriseTime());
       return atime;
   }
   public String[] getArrayString(){
       String[] sholat=new String[7];
       sholat[0]=this.fTitle;
       sholat[1]="Zuhur    : "+this.strZuhurTime();
       sholat[2]="Ashar    : "+this.strAsharTime();
       sholat[3]="Maghrib  : "+this.strMagribTime();
       sholat[4]="Isya'    : "+this.strIsyaTime();
       sholat[5]="Fajr     : "+this.strFajarTime();
       sholat[6]="Sunrise  : "+this.strSunriseTime();
       return sholat;
   }
}
