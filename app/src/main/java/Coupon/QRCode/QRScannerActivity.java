package Coupon.QRCode;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dtcsstaff.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.util.Calendar;

import Coupon.CouponValidation;
import Dashboard.DashBoard;

public class QRScannerActivity extends AppCompatActivity {

    Handler handler;
    ProgressDialog progressDialog;
    public static String userID="";
    public static String couponID="";
    public static String served_time="";
    public static String menu_name="";
    public static String menu_price="";
    public static String menu_status="";
    public static String menu_time="";
    private static final int REQUEST_CODE_QR_SCAN = 49374;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);


        handler=new Handler(Looper.getMainLooper());
        handler.post(() -> {
            progressDialog = new ProgressDialog(QRScannerActivity.this);
            progressDialog.setMessage("Loading, Please wait...Make sure you have a stable internet connection!");
            progressDialog.setCancelable(false);
        });

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
                    Intent intent=new Intent(QRScannerActivity.this,DashBoard.class);
                    intent.putExtra("stat","cancel");
                    startActivity(intent);
                } else {
                    progressDialog.show();

                    // Handle the scanned QR code here
                    String scannedData = result.getContents();

                    // Create an AlertDialog to display the scanned details



                    String[] keyValuePairs = scannedData.split(", ");
                    for (String pair : keyValuePairs) {
                        String[] parts = pair.split(": ");
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();
                            switch (key) {
                                case "UID":
                                    userID = value;
                                    break;
                                case "Reference Number":
                                    couponID = value;
                                    break;
                                default:
                                    // Handle unknown keys if needed

                                progressDialog.dismiss();
                                Toast.makeText(QRScannerActivity.this, "The QRCode is not recognized!", Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(QRScannerActivity.this,DashBoard.class);
                                intent.putExtra("stat","cancel");
                                startActivity(intent);

                                    break;
                            }
                        }

                    }

                    DatabaseReference couponRef= FirebaseDatabase.getInstance().getReference().child("Coupons")
                            .child(userID).child(couponID);
                    couponRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){

                                Calendar calendar = Calendar.getInstance();
                                String currentdate = DateFormat.getInstance().format(calendar.getTime());
                                String[] dateSeparation=currentdate.split(" ");
                                String dateOnlyFull=dateSeparation[0]+"";
                                String[] tarehe=dateOnlyFull.split("/");
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                int month = calendar.get(Calendar.MONTH) + 1; // Adding 1 because January is represented as 0
                                int year = calendar.get(Calendar.YEAR);
                                String dateOnly=day+"-"+month+"-"+year;

                                AlertDialog.Builder builder = new AlertDialog.Builder(QRScannerActivity.this);
                                LayoutInflater inflater = getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.coupon_details, null);
                                builder.setView(dialogView);
                                AlertDialog dialog = builder.create();
                                dialog.setCancelable(false);

                                // Set the scanned details to the TextViews
                                TextView textMenuName = dialogView.findViewById(R.id.text_menu_name);
                                TextView textMenuTime = dialogView.findViewById(R.id.text_menu_time);
                                TextView textMenuPrice = dialogView.findViewById(R.id.text_menu_price);
                                TextView textStatus = dialogView.findViewById(R.id.text_status);
                                TextView textServedTime = dialogView.findViewById(R.id.text_menu_serveTime);
                                TextView textReferenceNumber = dialogView.findViewById(R.id.text_reference_number);
                                Button confirm=dialogView.findViewById(R.id.btn_confirmCoupon);

                                menu_name=snapshot.child("Menu Name").getValue(String.class);
                                menu_price=snapshot.child("Menu Price").getValue(String.class);
                                menu_time=snapshot.child("Menu Time").getValue(String.class);
                                served_time=snapshot.child("Served Time").getValue(String.class);
                                menu_status=snapshot.child("Status").getValue(String.class);
                                String status=snapshot.child("Status").getValue(String.class);

                                textMenuName.setText("Menu Name: "+menu_name);
                                textMenuTime.setText("Ordered Time : "+menu_time);
                                textMenuPrice.setText("Menu Price : "+menu_price);
                                textStatus.setText("Coupon Status : "+menu_status);
                                textServedTime.setText("Served Time: : "+served_time);
                                textReferenceNumber.setText("Reference Number: "+couponID);

                                if (status.equals("pending")){
                                    confirm.setVisibility(View.VISIBLE);
                                    progressDialog.dismiss();
                                    dialog.show();
                                    confirm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            progressDialog.show();
                                            Calendar calendar = Calendar.getInstance();
                                            String currentdate = DateFormat.getInstance().format(calendar.getTime());
                                            couponRef.child("Status").setValue("Used");
                                            couponRef.child("Served Time").setValue(currentdate+"Hrs").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    DatabaseReference couponCardRef= FirebaseDatabase.getInstance().getReference().child("Card Coupons")
                                                            .child(dateOnly).child(couponID);
                                                    couponCardRef.child("Status").setValue("Used").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            DatabaseReference servercall=FirebaseDatabase.getInstance().getReference().child("Windows").child(DashBoard.tableStatus).child(dateOnly);
                                                            servercall.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    servercall.child("CouponNumber").setValue(couponID);
                                                                    servercall.child("MenuName").setValue(menu_name).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            progressDialog.dismiss();
                                                                            Toast.makeText(QRScannerActivity.this, "Success", Toast.LENGTH_SHORT).show();

                                                                            dialog.dismiss();
                                                                            Intent intent=new Intent(QRScannerActivity.this,DashBoard.class);
                                                                            intent.putExtra("stat","cancel");
                                                                            startActivity(intent);
                                                                        }
                                                                    });
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });

                                                        }
                                                    });


                                                }
                                            });

                                        }
                                    });

                                }else{
                                    progressDialog.dismiss();
                                    confirm.setText("Okay");
                                    confirm.setVisibility(View.VISIBLE);
                                    confirm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            Intent intent=new Intent(QRScannerActivity.this,DashBoard.class);
                                            intent.putExtra("stat","cancel");
                                            startActivity(intent);

                                        }
                                    });
                                    dialog.show();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Intent intent=new Intent(QRScannerActivity.this,DashBoard.class);
                            intent.putExtra("stat","cancel");
                            startActivity(intent);
                        }
                    });








                    // Show the AlertDialog

//                    dialog.show();
                }
            } else {
                Intent intent=new Intent(QRScannerActivity.this,DashBoard.class);
                intent.putExtra("stat","cancel");
                startActivity(intent);
                Log.d("QRScannerActivity", "Result is null");
            }
        }
    }

}

