package id.sentuh.digitalsignage.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import id.sentuh.digitalsignage.app.EndPoints;

/**
 * Created by sony on 31-May-18.
 */

public class DestroyTempDir extends AsyncTask<Void,Void,Void> {
    private static final String TAG = "Destroy";
    Activity mContext;
    String filePath;
    String tempPath;
    Handler mHandler;
    ProgressDialog dialogProgress;
    public DestroyTempDir(Activity context,String filePath){
        this.mContext = context;
        this.tempPath = EndPoints.STORAGE_TEMP_PATH;
        this.filePath = filePath;
        this.mHandler = new Handler();
//        dialogProgress = new ProgressDialog(mContext);
//        dialogProgress.setIndeterminate(true);
//        dialogProgress.setCancelable(false);
//        dialogProgress.setMessage("Cleaning directory...");
//        showProgress(true);
        Toast.makeText(mContext,"Destroy Temporary dir...",Toast.LENGTH_SHORT).show();
    }
    private void showProgress(boolean value){
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
        fileOrDirectory.delete();
    }
    @Override
    protected Void doInBackground(Void... objects) {

        File dirs = new File(tempPath);
        if(dirs.exists()){
            for(File dir:dirs.listFiles()) {
                if (dir.getName().equals("Views") ||
                        dir.getName().equals("Resources") ||
                        dir.getName().equals("Events") ||
                        dir.getName().equals("Models")||
                        dir.getName().equals("Config")) {
                    Log.d(TAG, "delete dir : " + dir.getName());
                    deleteRecursive(dir);
                }
            }
        }

        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        showProgress(false);
        Toast.makeText(mContext,"Destroy Finish ",Toast.LENGTH_SHORT).show();
        new CheckConfig(mContext,filePath).execute();

    }
}
