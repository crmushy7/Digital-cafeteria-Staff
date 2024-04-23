package Coupon;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dtcsstaff.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Coupon.QRCode.QRScannerActivity;

public class CouponValidation extends AppCompatActivity {

    public static Handler handler;
    public static ProgressDialog progressDialog;
    private static final int REQUEST_CODE_QR_SCAN = 49374;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    public static String cardNumber="null";
    public static String userID="null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_validation);

        handler=new Handler(Looper.getMainLooper());
        handler.post(() -> {
            progressDialog = new ProgressDialog(CouponValidation.this);
            progressDialog.setMessage("Loading, Please wait...Make sure you have a stable internet connection!");
            progressDialog.setCancelable(false);
        });

        Button nfcReader=findViewById(R.id.btn_validateCard);
        nfcAdapter=NfcAdapter.getDefaultAdapter(CouponValidation.this);
        if (nfcAdapter== null){
            Toast.makeText(this, "NFC not supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Intent nfcIntent=new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent=PendingIntent.getActivity(this,0,nfcIntent,0);
        nfcReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // Find the button by its id
        Button btnValidateCoupon = findViewById(R.id.btn_validateCoupon);

        // Set a click listener on the button
        btnValidateCoupon.setOnClickListener(view -> {
            // Launch QRScannerActivity to scan QR code
            Intent intent = new Intent(CouponValidation.this, QRScannerActivity.class);
            startActivityForResult(intent, REQUEST_CODE_QR_SCAN);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (resultCode == RESULT_OK && data != null) {
                // Scanning was successful, handle the result
                String scannedData = data.getStringExtra("SCAN_RESULT");
                // Handle the scanned QR code data
                Log.d("QRScannerActivity", "Scanned QR Code: " + scannedData);
                Toast.makeText(this, "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh: " + scannedData, Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                // Scanning was canceled by the user
                Toast.makeText(this, "QR code scanning canceled", Toast.LENGTH_SHORT).show();
                Log.d("QRScannerActivity", "QR code scanning canceled");
            } else {
                // Other cases where scanning failed
                Toast.makeText(this, "QR code scanning failed", Toast.LENGTH_SHORT).show();
                Log.d("QRScannerActivity", "QR code scanning failed");
            }
        }else {
            Log.d("mmm","failure"+requestCode);
        }
    }

}