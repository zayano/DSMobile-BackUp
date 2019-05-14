package id.sentuh.digitalsignage.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ChangedApplication extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent
            intent)   {

        String act = intent.getAction();
        if(act != null && act.equals("Updated")  )
        {
            Toast.makeText(context, act + "Mematikan Aplikasi Sementara" ,
                    Toast.LENGTH_SHORT).show();

//            Intent i = context.getPackageManager().
//                    getLaunchIntentForPackage("id.sentuh.digitalsignage");
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
            System.exit(1);
//            Process.killProcess(Process.myPid());
//
        }System.exit(1);

    }
}
