package id.sentuh.digitalsignage.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.snatik.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import id.sentuh.digitalsignage.ScreenActivity;
import id.sentuh.digitalsignage.app.AppUtils;
import id.sentuh.digitalsignage.app.EndPoints;
import id.sentuh.digitalsignage.models.FrameResources;
import id.sentuh.digitalsignage.models.Popups;

/**
 * Created by sony on 31-May-18.
 */

public class InsertFileToDB extends AsyncTask<Void,Void,Void> {
    private static final String TAG = "Insert Data";
    Activity mContext;
//    ProgressDialog dialogProgress;
    Storage storage;
    Handler mHandler = new Handler();
    public InsertFileToDB(Activity mContext) {
        this.mContext = mContext;
//        showProgress(true);
        storage = new Storage(mContext);
    }
//    private void showProgress(boolean value){
//        if(dialogProgress==null){
//            dialogProgress = new ProgressDialog(mContext);
//            dialogProgress.setIndeterminate(true);
//            dialogProgress.setCancelable(false);
//            dialogProgress.setMessage("Loading Content ...");
//        }
//        if(value){
//            dialogProgress.show();
//        } else {
//            dialogProgress.dismiss();
//        }
//    }
    private void insertResources(){
//        caption.setText("Inserting Resource ...");
        List<File> files = storage.getFiles(EndPoints.RESOURCE_PATH, null);
//        progressBar.setMax(files.size());
        if(files.size()>0){
//            Log.d(TAG,"page insert resources ");
            int i=0;
            for(File file:files){
                if(!file.isDirectory()){
                    String mime = AppUtils.getMimeType(file);
                    if(!DBHelper.isResourceExist(file.getName())){
                        id.sentuh.digitalsignage.models.Resources resources = new id.sentuh.digitalsignage.models.Resources();
                        resources.setFile_name(file.getName());
                        resources.setMime(mime);
                        if(file.getName().contains(".txt")){
                            String content = storage.readTextFile(file.getAbsolutePath());
                            resources.setContent(content);
                        }
                        resources.save();
                        Log.d(TAG,"file name:"+file.getName()+" mime : "+mime+" saved!");
                    } else {
//                            Toast.makeText(context,"File Resource tidak Ada!",
//                                    Toast.LENGTH_SHORT).show();
                    }

                }
                i++;
//                progressBar.setProgress(i);
            }
//            insertPopup(context,progressBar);

        }
    }

    private void insertPopup(Context context){
        // caption.setText("Inserting PopUP ...");
        // SQLite.delete().from(Popups.class);
        File file = new File(EndPoints.POPUP_PATH);
        if(file.exists()){
            String content = storage.readTextFile(EndPoints.POPUP_PATH);
            String json_string = String.format("{\"result\": %s }",content);
            try {
                JSONObject json = new JSONObject(json_string);
                JSONArray result = json.getJSONArray("result");
//            progressBar.setMax(result.length());
                for(int i=0;i<result.length();i++){
                    JSONObject item = result.getJSONObject(i);
                    String filename=item.getString("resource");
                    String waktu = item.getString("time");
                    Popups popups = new Popups();
                    popups.setFile_name(filename);
                    popups.setTime(waktu);
                    popups.save();
//                progressBar.setProgress(i);
                }
//            progressBar.setProgress(result.length());
//            loadScreen();

            } catch (Exception ex){
//                    Toast.makeText(context,"Kesalahan Format Data Popup!",
//                            Toast.LENGTH_SHORT).show();
                Log.e(TAG,"error:"+ex.getMessage());
            }
        } else {
            Toast.makeText(context,"Popup Not Found! Load Screen Without PopUp",
                    Toast.LENGTH_SHORT).show();

        }
    }
    private void loadScreen(){
//        config.setKeyInt("view_count",1);

        Intent intent = new Intent(mContext,ScreenActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(android.R.anim.fade_out,android.R.anim.fade_in);
    }
    @Override
    protected Void doInBackground(Void... voids) {
        insertResources();
        Log.d(TAG,"page insert frame ");
        File dir = new File(EndPoints.VIEW_PATH);
        if(dir.exists()){
            int last_view_id = 0;
            for(File file : dir.listFiles()){
//                Log.d(TAG,"file name: "+file.getName());
                String content=storage.readTextFile(file.getAbsolutePath());
//                Log.d(TAG,"file content:"+content);
                try {
                    JSONObject json = new JSONObject(content);
                    int view_id = Integer.parseInt(json.getString("id"));
                    if(view_id<last_view_id){
                        view_id = last_view_id++;
                    }
                    String name = json.getString("name");
                    String description = json.getString("description");

                    String bg_color = "";
                    String bg_image = "";
                    String showTime = "00:05";
                    int delayTime = 1000;
                    int order=0;
                    if(!json.isNull("background-image")){
                        bg_image = json.getString("background-image");
                    }
                    if(!json.isNull("background-color")){
                        bg_color = json.getString("background-color");
                    }
                    if(!json.isNull("time")){
                        showTime = json.getString("time");
                    }
                    if(!json.isNull("delay")){
                        delayTime = json.getInt("delay");
                    }
                    if(!json.isNull("order")){
                        delayTime = json.getInt("order");
                    }
                    if(!json.isNull("name")){
                        DBHelper.insertViews(view_id,name,bg_image,bg_color,showTime,delayTime,order);
                        JSONArray jarray = json.getJSONArray("data");
                        int jlength = jarray.length();
                        //int id_frame = 1;
                        for(int i=0;i<jlength;i++){
                            JSONObject child = jarray.getJSONObject(i);
                            String frame_name = child.getString("name");

                            String frame_desc = child.getString("description");
                            int x = (int)Math.round(child.getDouble("x"));
                            int y = (int)Math.round(child.getDouble("y"));
                            int width = (int)Math.round(child.getDouble("width"));
                            int height = (int)Math.round(child.getDouble("height"));
                            int id_frame=DBHelper.insertFrame(view_id,frame_name,frame_desc,x,y,width,height);
//                            Log.d(TAG,"frame name:"+frame_name + "inserted...");
//                    Frames dframe = DBHelper.getFrameDetail(frame_name,view_id);
                            JSONArray resources = child.getJSONArray("resources");
                            int rlength = resources.length();
                            for(int j=0;j<rlength;j++){
                                JSONObject item_res = resources.getJSONObject(j);
                                String filename = item_res.getString("resource");
                                String waktu = item_res.getString("waktu");
                                FrameResources frameResources = new FrameResources();
                                frameResources.setFrame_id(id_frame);
                                frameResources.setResource_name(filename);
                                frameResources.setWaktu(Integer.parseInt(waktu));
                                frameResources.save();

                            }
                            id_frame++;
                        }
                    }

//                    Log.d(TAG,"insert view : "+Integer.toString(view_id));
//                int view_id = DBHelper.getViewRow(name).getId();

//                    last_view_id = view_id;
                } catch (JSONException e) {
//                        Toast.makeText(context,"Kesalahan Format Data Konten!",
//                                Toast.LENGTH_SHORT).show();
//                        new DestroyInvalidData(MainActivity.this,EndPoints.ZIP_OLD_FILE).execute();
//                e.printStackTrace();
                }

//            progressBar.setProgress(ifile);

            }
            insertPopup(mContext);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        showProgress(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadScreen();
            }
        },1600);

    }
}
