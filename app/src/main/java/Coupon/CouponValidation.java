package Coupon;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dtcsstaff.R;

import Coupon.QRCode.QRScannerActivity;

public class CouponValidation extends AppCompatActivity {

    private static final int REQUEST_CODE_QR_SCAN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_validation);

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
                Toast.makeText(this, "Scanned QR Code: " + scannedData, Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                // Scanning was canceled by the user
                Toast.makeText(this, "QR code scanning canceled", Toast.LENGTH_SHORT).show();
                Log.d("QRScannerActivity", "QR code scanning canceled");
            } else {
                // Other cases where scanning failed
                Toast.makeText(this, "QR code scanning failed", Toast.LENGTH_SHORT).show();
                Log.d("QRScannerActivity", "QR code scanning failed");
            }
        }
    }

}
