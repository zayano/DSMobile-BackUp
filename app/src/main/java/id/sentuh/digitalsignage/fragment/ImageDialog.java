package id.sentuh.digitalsignage.fragment;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import id.sentuh.digitalsignage.R;

import static id.sentuh.digitalsignage.app.EndPoints.TAG;

/**
 * Created by sony on 2/22/2018.
 */

public class ImageDialog extends Dialog {
    String image_url;
    ImageView imageView;
    Context mContext;
    Handler mHandler;
//    OnDialogListener mListener;
    public ImageDialog(@NonNull Context context,String image_url) {
        super(context);
        this.mContext=context;
        this.image_url=image_url;

        mHandler=new Handler();
        //mListener = (OnDialogListener)mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.image_dialog);
        imageView = this.findViewById(R.id.image_dialog1);
        imageView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        //String filepath = mParam1;
        Log.d(TAG,"image : "+this.image_url);
        File file = new File(image_url);
        Uri imageUri = Uri.fromFile(file);
        if(file.exists()){
            Picasso.with(mContext).setLoggingEnabled(true);
            Picasso.with(mContext)
                    .load(imageUri)
                    .error(R.drawable.pagenotfound)
                    .resize(1080,1920)
                    .into(imageView);
            imageView.setImageURI(imageUri);
//            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
//            imageView.setImageBitmap(bmp);
        }
    }

    @Override
    public void show() {
        super.show();
        mHandler.postDelayed(mRunnable,10000);
    }

    Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {
           // mListener.OnDialogClose(true);
            dismiss();
        }
    };
}
