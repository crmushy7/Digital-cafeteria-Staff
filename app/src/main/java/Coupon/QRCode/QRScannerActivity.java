package Coupon.QRCode;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dtcsstaff.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import Coupon.CouponValidation;

public class QRScannerActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_QR_SCAN = 49374;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        // Initialize the QR code scanner
        IntentIntegrator integrator = new IntentIntegrator(QRScannerActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code");
        integrator.setCameraId(0);  // Use the default camera
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(false); // Allow any orientation
        integrator.setCaptureActivity(CustomScannerActivity.class); // Set custom scanner activity
        integrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QR_SCAN) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    // If QR code scanning was canceled
                    Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the scanned QR code here
                    String scannedData = result.getContents();
                    // Toast the scanned data
                    Log.d("hhh", "Scanned QR Code: " + scannedData);
                    Toast.makeText(this, "Scanned QR Code: " + scannedData, Toast.LENGTH_LONG).show();

                    // Start CouponValidation activity and pass the scanned data
                    Intent intent = new Intent(this, CouponValidation.class);
                    intent.putExtra("SCAN_RESULT", scannedData);
                    startActivity(intent);

                    // Finish this activity
                    finish();
                }
            } else {
                Log.d("here","not there");
                super.onActivityResult(requestCode, resultCode, data);
            }
        }else {
            Log.d("here1","not there"+requestCode);
        }
    }

}

