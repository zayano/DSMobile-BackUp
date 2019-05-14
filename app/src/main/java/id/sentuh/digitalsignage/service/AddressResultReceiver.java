package id.sentuh.digitalsignage.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import id.sentuh.digitalsignage.helper.Constants;

import static com.raizlabs.android.dbflow.config.FlowLog.TAG;

public class AddressResultReceiver extends ResultReceiver {

    private String mAddressOutput;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public AddressResultReceiver(Handler handler) {
        super(handler);
    }
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (resultData == null) {
            return;
        }

        // Display the address string
        // or an error message sent from the intent service.
        mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
        if (mAddressOutput == null) {
            mAddressOutput = "";
        }
        Log.d(TAG,"lokasi anda : "+mAddressOutput);
        //displayAddressOutput();

        // Show a toast message if an address was found.
        if (resultCode == Constants.SUCCESS_RESULT) {
            //showToast(getString(R.string.address_found));

        }

    }

}
