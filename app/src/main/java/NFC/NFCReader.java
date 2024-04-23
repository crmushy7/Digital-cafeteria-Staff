package NFC;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Looper;

import Adapters.FoodSetGet;
import Coupon.NfcUtils;

public class NFCReader {
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private Activity activity;
    private NFCListener listener;

    public interface NFCListener {
        void onNFCScanned(String data);
    }

    public NFCReader(Activity activity, NFCListener listener) {
        this.activity = activity;
        this.listener = listener;
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (nfcAdapter == null) {
            throw new UnsupportedOperationException("NFC is not supported on this device");
        }
        pendingIntent = PendingIntent.getActivity(activity, 0,
                new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    public void startListening() {
        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, null, null);
    }

    public void stopListening() {
        nfcAdapter.disableForegroundDispatch(activity);
    }

    public void handleIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] msgs = NfcUtils.getNdefMessages(intent);
            if (msgs != null && msgs.length > 0) {
                String tagContent = NfcUtils.parseNdefMessages(msgs);
                if (listener != null) {
                    // Use Handler to post the result back on the UI thread
                    new Handler(Looper.getMainLooper()).post(() -> listener.onNFCScanned(tagContent));
                }
            }else {
                new Handler(Looper.getMainLooper()).post(() -> listener.onNFCScanned("tagContent"));
            }
        }else{
            new Handler(Looper.getMainLooper()).post(() -> listener.onNFCScanned("not valid"));
        }
    }
}

