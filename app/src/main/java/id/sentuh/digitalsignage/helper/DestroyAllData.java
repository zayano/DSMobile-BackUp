package id.sentuh.digitalsignage.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import id.sentuh.digitalsignage.UpdateUSBActivity2;
import id.sentuh.digitalsignage.app.EndPoints;

/**
 * Created by sony on 31-May-18.
 */

public class DestroyAllData extends AsyncTask<Void,Void,Void> {
    private static final String TAG = "Destroy";
    Activity mContext;
    String filePath;
    Handler mHandler;
    ProgressDialog dialogProgress;
    public DestroyAllData(Activity context, String file_path){
        this.mContext = context;
        this.filePath = file_path;
        this.mHandler = new Handler();
//        showProgress(true);
    }
    private void showProgress(boolean value){
        if(dialogProgress==null){
            dialogProgress = new ProgressDialog(mContext);
            dialogProgress.setIndeterminate(true);
            dialogProgress.setCancelable(false);
            dialogProgress.setMessage("Cleaning directory...");
        }
        if(value){
            dialogProgress.show();
        } else {
            dialogProgress.dismiss();
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles()) {

                deleteRecursive(child);
            }
        Log.d(TAG,"delete file : "+fileOrDirectory.getName());
        if(fileOrDirectory.exists()){
            fileOrDirectory.delete();
        }


    }
    @Override
    protected Void doInBackground(Void... objects) {
        //DBHelper.clearDB(mContext);
        File dirs = new File(EndPoints.STORAGE_DATA_PATH);
        if(dirs.exists()){
            for(File dir:dirs.listFiles()) {
                if (dir.getName().equals("Views") ||
                        dir.getName().equals("Resources") ||
                        dir.getName().equals("Events") ||
                        dir.getName().equals("Models")||
                        dir.getName().equals("Config")) {
                    Log.d(TAG, "delete dir : " + dir.getName());
                    deleteRecursive(dir);
                } else {
                    Toast.makeText(mContext,"Extracting data!", Toast.LENGTH_SHORT).show();
                    new ExtractDataFile(mContext,filePath,EndPoints.STORAGE_DATA_PATH).execute();
                }
            }
        }else if (dirs.getName().isEmpty()){
            Toast.makeText(mContext,"Extracting data!", Toast.LENGTH_SHORT).show();
            new ExtractDataFile(mContext,filePath,EndPoints.STORAGE_DATA_PATH).execute();
        } else {
            new CopyUsbToLocal(mContext,UpdateUSBActivity2.progressBar,UpdateUSBActivity2.usbFile,
                    EndPoints.ZIP_DEST_FILE,true).execute();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        showProgress(false);
        Toast.makeText(mContext,"Extracting data!", Toast.LENGTH_SHORT).show();
        new ExtractDataFile(mContext,filePath,EndPoints.STORAGE_DATA_PATH).execute();

//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                AppUtils.loadMain(mContext);
//            }
//        },2000);
    }
}
