package id.sentuh.digitalsignage.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import java.io.File;

import id.sentuh.digitalsignage.MainActivity;
import id.sentuh.digitalsignage.app.AppUtils;
import id.sentuh.digitalsignage.app.EndPoints;
import ir.mahdi.mzip.zip.ZipArchive;

/**
 * Created by sony on 31-May-18.
 */

public class ExtractDataFile extends AsyncTask<Void,Void,Void> {
    Activity mContext;
    String filePath;
    String destPath;
    Handler mHandler;
    ProgressDialog dialogProgress;
    public ExtractDataFile(Activity context, String file_path,String destPath){
        this.mContext = context;
        this.filePath = file_path;
        this.destPath = destPath;
        this.mHandler = new Handler();
        Toast.makeText(mContext,"Extract Data File",Toast.LENGTH_SHORT).show();
//        dialogProgress = new ProgressDialog(mContext);
//        dialogProgress.setIndeterminate(true);
//        dialogProgress.setCancelable(false);
//        dialogProgress.setMessage("Extracting Content ...");
//        showProgress(true);
    }
    private void showProgress(boolean value){

//        if(value){
//            dialogProgress.show();
//        } else {
//            dialogProgress.dismiss();
//        }
    }
    @SuppressLint("WrongConstant")
    private void loadMain(){
        Intent intent = new Intent(mContext,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
        mContext.startActivity(intent);
        mContext.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//        startBgService();
        mContext.finish();
    }
    @Override
    protected Void doInBackground(Void... params) {
//
        File dir = new File(destPath);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        File fs = new File(filePath);
        if(fs.exists()){
            ZipArchive zipArchive = new ZipArchive();

            ZipArchive.unzip(filePath, destPath,"");

        }


        return null;
    }

    @Override
    protected void onPostExecute(Void Void) {
        super.onPostExecute(Void);
        showProgress(false);
        Toast.makeText(mContext,"Reading data file...!",Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMain();
            }
        },2000);
    }
}
