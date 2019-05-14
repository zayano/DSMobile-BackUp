package id.sentuh.digitalsignage.helper;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import id.sentuh.digitalsignage.app.EndPoints;

public class CopyUsbToLocal extends AsyncTask<Void,Integer,Void> {
    static String TAG="Copy USB Data";
    Activity mContex;
    UsbFile srcFile;
    String fileDest;
    boolean checking;
    //    ProgressDialog dialog;
    ProgressBar progressBar;
    InputStream inputStream;
    public CopyUsbToLocal(Activity context,ProgressBar progressBar,UsbFile source,String destination,boolean checking) {
        this.mContex = context;
        this.srcFile = source;
        this.fileDest = destination;
        this.checking = checking;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG,"copying file to "+fileDest);
        Toast.makeText(mContex,"Copy File "+this.srcFile.getName()+" to local",Toast.LENGTH_SHORT).show();
//        createDialog();

        progressBar.setMax(100);
    }
    //
//    private void createDialog(){
//        dialog = new ProgressDialog(mContex);
//        dialog.setTitle("Patient Please...");
//        dialog.setMessage("Copying "+srcFile.getName()+" to local.");
//        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        dialog.setMax(100);
//        dialog.setProgress(1);
//        dialog.show();
//    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            inputStream= new UsbFileInputStream(srcFile);
            File destFile = new File(this.fileDest);
            if(!destFile.exists()){
                destFile.createNewFile();
            }
            OutputStream outStream = new FileOutputStream(destFile);
            final long maxsize = srcFile.getLength();
            Log.d(TAG, "file size : "+Long.toString(maxsize));
//            ByteBuffer buffers = ByteBuffer.allocate(inputStream.available());
            byte[] buffer = new byte[8 * 1024];
            long total=0;
//            inputStream.read(buffer);
            int length=0;
//            Files.write(fileDest,buffer);
            while((length = inputStream.read(buffer)) != -1) {

                outStream.write(buffer,0,length);
                total+=length;
                final int progress = (int) (total * 100 / maxsize);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if(progressBar!=null){
                            progressBar.setProgress(progress);
                        }
                        onProgressUpdate(progress);

                    }
                }).start();
                Log.d(TAG," progress : "+Integer.toString(progress)+" filesize : "+Long.toString(maxsize));
            }
//            outStream.flush();
//            outStream.close();
//                java.nio.file.Files.copy(inputStream,destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//            FileUtils.copyInputStreamToFile(inputStream,destFile);

            // is.close();
//            inputStream.close();
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outStream);
        } catch (Exception ex){
            Log.e(TAG,"error copying file ...");
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

//        Toast.makeText(mContex,"Copying File "+this.srcFile.getName()+" finish... ",Toast.LENGTH_SHORT).show();
//            new DestroyTempDir(UpdateUSBActivity2.this,fileSthxPath).execute();

        if(checking){
            new CheckConfig(mContex,EndPoints.ZIP_DEST_FILE).execute();
        }
    }
}
