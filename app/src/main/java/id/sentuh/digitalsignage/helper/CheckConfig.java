package id.sentuh.digitalsignage.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

import id.sentuh.digitalsignage.MainActivity;
import id.sentuh.digitalsignage.app.Configurate;
import id.sentuh.digitalsignage.app.EndPoints;
import ir.mahdi.mzip.zip.ZipArchive;

/**
 * Created by sony on 04/06/2018.
 */

public class CheckConfig extends AsyncTask<Void,Boolean,Boolean> {
    Activity mContext;
    String filePath;
//    Storage storage;
    Configurate config;
    public CheckConfig(Activity context, String file_path){
        this.mContext = context;
        this.filePath = file_path;
        config = new Configurate(mContext);
        Toast.makeText(context,"Checking Configuration...",Toast.LENGTH_SHORT).show();
    }
    @Override
    protected Boolean doInBackground(Void... params) {

        File temp = new File(EndPoints.STORAGE_TEMP_PATH);
        if(!temp.exists()){
            temp.mkdirs();
        }

        ZipArchive zipArchive = new ZipArchive();
        File zip = new File(this.filePath);
        if(zip.exists()){
            // Toast.makeText(mContext,"Extract Zip File!",Toast.LENGTH_SHORT).show();
            zipArchive.unzip(filePath, EndPoints.STORAGE_TEMP_PATH,"");

        }
        return true;
    }
    private void loadMain(){
        Intent intent = new Intent(mContext,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
        mContext.startActivity(intent);
        mContext.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//        startBgService();

    }
    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        String fileconfig = EndPoints.STORAGE_TEMP_PATH+"/Config/config.txt";
        File file = new File(fileconfig);
        if(file.exists()){
            Toast.makeText(mContext,"Destroying Old Data!", Toast.LENGTH_SHORT).show();
            new DestroyAllData(mContext,EndPoints.ZIP_DEST_FILE).execute();

        } else {
            Toast.makeText(mContext,"USB Tidak terindetifikasi!", Toast.LENGTH_SHORT).show();
            loadMain();
        }

//        if(file.exists()){
//            Toast.makeText(mContext,"Copying Data", Toast.LENGTH_SHORT).show();
//            new CopyUsbToLocal(mContext,EndPoints.ZIP_DEST_FILE).execute();
//
//        } else {
//            Toast.makeText(mContext,"USB Tidak terindetifikasi!", Toast.LENGTH_SHORT).show();
//            loadMain();
//        }
    }
}
