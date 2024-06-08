package Coupon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Adapters.FoodSetGet;
import Adapters.HistorySetGet;
import Dashboard.DashBoard;
import Others.UniqueIDGenerator;

public class CouponGenerator {
    public static String uniqueID="";
    public static String couponNumber="";
    public static String couponRefNo="";

    public static void generateCoupon(Context context, FoodSetGet foodSetGet) {
        uniqueID=UniqueIDGenerator.generateUniqueID();
        Calendar calendar = Calendar.getInstance();
        String currentdate = DateFormat.getInstance().format(calendar.getTime());
        String[] dateSeparation=currentdate.split(" ");
        String dateOnlyFull=dateSeparation[0]+"";
        String[] tarehe=dateOnlyFull.split("/");
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Adding 1 because January is represented as 0
        int year = calendar.get(Calendar.YEAR);
        String dateOnly=day+"-"+month+"-"+year;



        DatabaseReference couponNumberRef = FirebaseDatabase.getInstance().getReference()
                .child("Coupons Used")
                .child(dateOnly);
        couponNumberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String usedtoday=snapshot.child("Used Today").getValue(String.class);
                String usedtotal=snapshot.child("Used Total").getValue(String.class);
                if (snapshot.exists()) {
                    if (usedtoday==null){
                        couponNumberRef.child("Used Today").setValue("1 sold");
                        couponNumberRef.child("Used Total").setValue("1 sold");
                        couponNumber="1";
                    }else{
                        String[] usedtodayString=usedtoday.split(" ");
                        String[] usedtotalString=usedtotal.split(" ");
                        int newCount_today=Integer.parseInt(usedtodayString[0])+1;
                        int newCount_total=Integer.parseInt(usedtotalString[0])+1;
                        couponNumber=newCount_today+"";
                        couponNumberRef.child("Used Today").setValue(newCount_today+" sold");
                        couponNumberRef.child("Used Total").setValue(newCount_total+" sold").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Coupons")
                                        .child(DashBoard.userID)
                                        .push(); // Generate a unique key for the coupon

                                couponRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        couponRefNo=snapshot.getKey().toString();
                                        couponRef.child("Menu Name").setValue(foodSetGet.getFoodName());
                                        couponRef.child("Menu Time").setValue(currentdate+"Hrs");
                                        couponRef.child("Menu Price").setValue(foodSetGet.getFoodPrice());
                                        couponRef.child("Status").setValue("pending");
                                        couponRef.child("Reference Number").setValue(uniqueID);
                                        couponRef.child("Served Time").setValue("Not served");

                                        couponRef.child("Coupon Number").setValue(couponNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                couponRefNo=snapshot.getKey().toString();
                                                DashBoard.aftercoupon(foodSetGet);
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



                }else{

                    couponNumberRef.child("Used Today").setValue("1 sold");
                    couponNumberRef.child("Used Total").setValue("1 sold");
                    couponNumber="1";
                    DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference()
                            .child("Coupons")
                            .child(DashBoard.userID)
                            .push(); // Generate a unique key for the coupon

                    couponRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            couponRefNo=snapshot.getKey().toString();
                            couponRef.child("Menu Name").setValue(foodSetGet.getFoodName());
                            couponRef.child("Menu Time").setValue(currentdate+"Hrs");
                            couponRef.child("Menu Price").setValue(foodSetGet.getFoodPrice());
                            couponRef.child("Status").setValue("pending");
                            couponRef.child("Reference Number").setValue(uniqueID);
                            couponRef.child("Served Time").setValue("Not served");

                            couponRef.child("Coupon Number").setValue(couponNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    couponRefNo=snapshot.getKey().toString();
                                    DashBoard.progressDialog2.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
//    public static Bitmap generateQRCodeBitmap(HistorySetGet historySetGet) {
//        // Construct data string for QR code
//        String data =", Reference Number: " + historySetGet.getCoupon_reference_Number()+
//                ", UID: "+FirebaseAuth.getInstance().getUid();
//
//        // Generate QR code bitmap
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        try {
//            Map<EncodeHintType, Object> hints = new HashMap<>();
//            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
//            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 512, 512, hints);
//            int width = bitMatrix.getWidth();
//            int height = bitMatrix.getHeight();
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
//                }
//            }
//            return bitmap;
//        } catch (WriterException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
