package id.sentuh.digitalsignage.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.helper.OnDialogListener;

@SuppressLint("Registered")
public class PopupDialog extends Dialog implements OnDialogListener
{

    public PopupDialog(@NonNull Context context, int width)
    {
        super(context);
    }

    public void quitDialog(View view){
        if(isShowing()) dismiss();
    }

    @Override
    public void OnDialogClose(boolean started) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_image);

    }
}
