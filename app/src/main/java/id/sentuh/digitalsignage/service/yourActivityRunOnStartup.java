package id.sentuh.digitalsignage.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import id.sentuh.digitalsignage.MainActivity;

/**
 * Created by sony on 2/17/2018.
 */

public class yourActivityRunOnStartup extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
