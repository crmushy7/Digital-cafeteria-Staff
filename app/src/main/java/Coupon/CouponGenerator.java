package Coupon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import Others.UniqueIDGenerator;

public class CouponGenerator {
    public static String uniqueID="";

    public static void generateCoupon(Context context, FoodSetGet foodSetGet) {
        uniqueID= UniqueIDGenerator.generateUniqueID();
        Calendar calendar = Calendar.getInstance();
        String currentdate = DateFormat.getInstance().format(calendar.getTime());
        DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference()
                .child("Coupons")
                .child(FirebaseAuth.getInstance().getUid())
                .push(); // Generate a unique key for the coupon

        couponRef.child("Menu Name").setValue(foodSetGet.getFoodName());
        couponRef.child("Menu Time").setValue(currentdate+"Hrs");
        couponRef.child("Menu Price").setValue(foodSetGet.getFoodPrice());
        couponRef.child("Status").setValue("pending");
        couponRef.child("Reference Number").setValue(uniqueID);
        couponRef.child("Served Time").setValue("Not served");



    }
    public static Bitmap generateQRCodeBitmap(HistorySetGet historySetGet) {
        // Construct data string for QR code
        String data =", Reference Number: " + historySetGet.getCoupon_reference_Number()+
                ", UID: "+FirebaseAuth.getInstance().getUid();

        // Generate QR code bitmap
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 512, 512, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
