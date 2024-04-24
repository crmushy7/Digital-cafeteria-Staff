package Dashboard;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dtcsstaff.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import Adapters.FoodAdapter;
import Adapters.FoodAdapterStaff;
import Adapters.FoodSetGet;
import Adapters.FoodSetGetStaff;
import Adapters.HistoryAdapter;
import Adapters.HistorySetGet;
import Adapters.ImageModel;
import Coupon.CouponValidation;
import Coupon.NfcUtils;
import Coupon.QRCode.QRScannerActivity;
import NFC.NFCReader;
import Others.OurTime;

public class DashBoard extends AppCompatActivity implements NFCReader.NFCListener {
    private static final int REQUEST_CODE_QR_SCAN = 49374;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;
    DatePicker dobpk;
    public static Bitmap userPhoto;
    Button next,registerCustomer;
    Spinner gender;
    private Uri imageUri;
    public static String userGender="";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";
    Pattern pat = Pattern.compile(emailRegex);
    private NFCReader nfcReader;
    private List<FoodSetGet>foodList=new ArrayList<>();
    private List<FoodSetGetStaff>foodListStaff=new ArrayList<>();
    public static HistoryAdapter historyAdapter;
    public static RecyclerView myHistoryRecyclerView;
    RecyclerView recyclerView,recyclerViewStaff;
    Thread thread;
    public static AlertDialog dialog;
    TextView meal_clock,meal_status,menuCategory;
    public static String timeStatus="BreakFast";
    public static String cardNumber="null";
    public static String modeController="normal";
    public static String userID="null";
    public static Handler handler;
    public static ProgressDialog progressDialog;
    public static ProgressDialog progressDialog2,progressDialogNFC;
    FoodAdapter adapter;
    FoodAdapterStaff adapterStaff;
    NfcAdapter nfcAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    PendingIntent pendingIntent;
    public static TextView user_Name,user_Pno,ppUsername,ppUsertopphone,ppUserFname,ppUsersmallphone,ppUserLname;

    public static String scanstatus="null";
    public static String fullName;
    public static String uploadedPicID;
    public static String user_email;
    public static String phonenumber;
    public static String userPassword;
    public static String user_dob;
    private ImageView imageView;
    ImageView switchMode,homeBtn,scan_qrCode,customerNav,reg_profile;
    public static FoodSetGet foodSetGetMod=new FoodSetGet("","","","");
    LinearLayout dashBoardlayout,settingsLayout,feedbackLayout,dashbordinsideLayout,profileLayout,myhistoryLayout,navigationLayout,customerReg1,customerReg2;
    TextView menu_textv,scan_textv,customer_textv,dob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.dtcsstaff.R.layout.activity_dash_board);
        OurTime.init(getApplicationContext());


        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        firebaseAuth= FirebaseAuth.getInstance();
        nfcReader = new NFCReader(this, this);
        switchMode=findViewById(R.id.db_mode_switch);
        menuCategory=findViewById(R.id.menu_category);
        menu_textv=findViewById(R.id.menu_tv);
        scan_textv=findViewById(R.id.scan_tv);
        customer_textv=findViewById(R.id.customer_tv);
        next=(Button) findViewById(R.id.btnNext);
        registerCustomer=(Button) findViewById(R.id.registerCustomer);
        gender=(Spinner)findViewById(R.id.gendersp);
        dobpk=(DatePicker)findViewById(R.id.dobPicker);
        dob=(TextView) findViewById(R.id.dobEt);
        reg_profile=findViewById(R.id.rp_previewImage);
        EditText fName=findViewById(R.id.rp_firstName);
        EditText lName=findViewById(R.id.rp_lastName);
        EditText pNumber=findViewById(R.id.rp_phoneNumber);
        EditText userEmail=findViewById(R.id.rp_email);
        EditText cardNumber=findViewById(R.id.rp_cardNumber);
        EditText pinNumber=findViewById(R.id.rp_pinNumber);
        EditText pinNumConf=findViewById(R.id.rp_pinNumberConf);
        EditText pass=findViewById(R.id.rp_password);
        EditText confPass=findViewById(R.id.rp_confirmPassword);
        TextView dateofBirth=findViewById(R.id.dobEt);

        handler=new Handler(Looper.getMainLooper());
        ImageView topProfilePic=findViewById(R.id.db_topProfilepic);

        recyclerView=(RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        adapter=new FoodAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        recyclerViewStaff=(RecyclerView) findViewById(R.id.recyclerviewStaff);
        recyclerViewStaff.setLayoutManager(new LinearLayoutManager(DashBoard.this));
        adapterStaff=new FoodAdapterStaff(new ArrayList<>());
        recyclerViewStaff.setAdapter(adapterStaff);
        meal_clock=(TextView) findViewById(R.id.clocktv);
        meal_status=(TextView) findViewById(R.id.mealStatustv);
       Button breakfast=(Button)findViewById(R.id.breakfastbtn);
       Button lunch=(Button)findViewById(R.id.lunchbtn);
       Button dinner=(Button)findViewById(R.id.dinnerbtn);
       switchMode.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               changeUserMode(DashBoard.this);
           }
       });


        homeBtn =  findViewById(R.id.homeBtn);
        scan_qrCode =  findViewById(R.id.scan_qrCode);
        customerNav =  findViewById(R.id.customerNav);
        dashBoardlayout = (LinearLayout) findViewById(R.id.dashBoardLayout);
        settingsLayout = (LinearLayout) findViewById(R.id.settingsLayout);
        feedbackLayout = (LinearLayout) findViewById(R.id.feedbackLayout);
        dashbordinsideLayout = (LinearLayout) findViewById(R.id.dashbordInsideLayout);
        profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
        myhistoryLayout = (LinearLayout) findViewById(R.id.myhistoryLayout);
        navigationLayout = (LinearLayout) findViewById(R.id.navigationLayout);
        customerReg1 = (LinearLayout) findViewById(R.id.ll_customerReg1);
        customerReg2 = (LinearLayout) findViewById(R.id.ll_customerReg2);


        handler.post(() -> {
            progressDialog = new ProgressDialog(DashBoard.this);
            progressDialog.setMessage("Loading, Please wait...Make sure you have a stable internet connection!");
            progressDialog.setCancelable(false);
        });
        handler.post(() -> {
            progressDialog2 = new ProgressDialog(DashBoard.this);
            progressDialog2.setMessage("ttttttttt, Please wait...Make sure you have a stable internet connection!");
            progressDialog2.setCancelable(false);
        });
        handler.post(() -> {
            progressDialogNFC = new ProgressDialog(DashBoard.this);
            progressDialogNFC.setMessage("place your card on the back side of this phone for scanning!!");
            progressDialogNFC.setCancelable(false);
        });
        reg_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(DashBoard.this);
                View view= LayoutInflater.from(DashBoard.this).inflate(R.layout.choose_image,null);
                builder.setView(view);

                LinearLayout chooseFromFilebtn=view.findViewById(R.id.upl_choosefromFile);
                LinearLayout takeCamerabtn=view.findViewById(R.id.upl_choosefromCamera);
                Button confirm=view.findViewById(R.id.ci_confirmbtn);
                imageView = view.findViewById(R.id.upl_previewImage);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imageUri != null){
                            dialog.dismiss();
                        }else {
                            dialog.dismiss();

                        }
                    }
                });
                imageView = view.findViewById(R.id.upl_previewImage);

                chooseFromFilebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseFromFileManager(v);
                    }
                });
                takeCamerabtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePhoto(v);
                    }
                });
                dialog=builder.create();
                dialog.show();
                reg_profile.setImageBitmap(userPhoto);
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                Calendar currentDate = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(DashBoard.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Create a Calendar instance for the selected date
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, dayOfMonth);

                        // Compare the selected date with the current date
                        if (selectedCalendar.compareTo(currentDate) >= 0) {
                            // Selected date is greater than or equal to the current date
                            Toast.makeText(getApplicationContext(), "Selected date is invalid", Toast.LENGTH_SHORT).show();
                        } else {
                            // Increment month by 1 since it's zero-based
                            month += 1;
                            // Format month to display it correctly
                            String formattedMonth = (month < 10) ? "0" + month : String.valueOf(month);
                            String formattedDate = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                            dob.setText(formattedDate + "/ " + formattedMonth + " / " + year);
                        }
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });



        String jinsia[] = {"MALE", "FEMALE"};
        ArrayAdapter<String> adapterg = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jinsia);
        adapterg.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapterg);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = jinsia[position];
                userGender=selected;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                userGender="Male";
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String first_name=fName.getText().toString();
                String last_name=lName.getText().toString();
                String phone_number=pNumber.getText().toString();
                String email=userEmail.getText().toString();
                String date_birth=dateofBirth.getText().toString();
                String passwd=pass.getText().toString();
                String confp=confPass.getText().toString();

                if (first_name.isEmpty()){
                    fName.setError("Field required");
                } else if (last_name.isEmpty()) {
                    lName.setError("Field required");
                }else if (phone_number.isEmpty()) {
                    pNumber.setError("Field required");
                }else if (phone_number.trim().length()!=10) {
                    pNumber.setError("10 numbers are required");
                }else if (email.isEmpty()) {
                    userEmail.setError("Field required");
                }else if (!pat.matcher(email).matches()) {
                    userEmail.setError("Please Enter a valid Email");
                    return;
                }else if (date_birth.isEmpty()){
                    dateofBirth.setError("Field required");
                }else if (passwd.isEmpty()) {
                    pass.setText("Field required");
                } else if (passwd.length()<6) {
                    pass.setError("Must contain atleast 6 characters");
                } else if (confp.isEmpty()) {
                    confPass.setError("Field required");
                } else if (!passwd.equals(confp)) {
                    confPass.setError("Password does not match");
                }else{
                    user_dob=date_birth;
                    fullName=first_name+" "+last_name;
                    phonenumber=phone_number;
                    user_email=email;
                    userPassword=passwd;

                }
            }
        });
        registerCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardN=cardNumber.getText().toString();
                String cardPin=pinNumber.getText().toString();
                String pincon=pinNumConf.getText().toString();
                if (cardN.isEmpty()){
                    cardNumber.setError("required!");
                    cardNumber.requestFocus();
                } else if (cardPin.length() < 4) {
                    pinNumber.setError("A four digit is required as your Card pin");
                    pinNumber.requestFocus();
                } else if (!pincon.equals(cardPin)) {
                    pinNumConf.setError("PIN does not match");
                    pinNumConf.requestFocus();
                }else if (imageUri != null){
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("Fullname",fullName);
                    hashMap.put("username",user_email);
                    hashMap.put("PhoneNumber",phonenumber);
                    hashMap.put("Gender",userGender);
                    hashMap.put("Date_of_Birth",user_dob);
                    hashMap.put("Password",userPassword);
                    hashMap.put("Card PIN",cardPin);
                    hashMap.put("Account Number",cardN);
                    hashMap.put("Amount","50000 TZs");

                    progressDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(user_email, userPassword)
                            .addOnCompleteListener(DashBoard.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //our database operations here
                                        databaseReference.child("All Users")
                                                .child(firebaseAuth.getUid().toString())
                                                .child("Details")
                                                .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(DashBoard.this, "Successful", Toast.LENGTH_SHORT).show();
                                                            Toast.makeText(DashBoard.this, "User Registered!", Toast.LENGTH_LONG).show();
                                                            uploadToFirestore(v);


                                                        } else {
                                                            Toast.makeText(DashBoard.this, "Failed", Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                            Toast.makeText(DashBoard.this, "Fail! User not registered!", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        progressDialog.dismiss();

                                        // If sign in fails, check the exception and handle specific errors
                                        if (task.getException() != null && task.getException() instanceof FirebaseAuthException) {
                                            FirebaseAuthException firebaseAuthException = (FirebaseAuthException) task.getException();
                                            String errorCode = firebaseAuthException.getErrorCode();
                                            Toast.makeText(DashBoard.this, "User not registered! "+errorCode, Toast.LENGTH_SHORT).show();
//                                                userEmail.setError("Email already in use");
                                        }
                                    }
                                }
                            });

                }



            }
        });
//        backtoprofile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                customerNav.setTextColor(getResources().getColor(R.color.black));
//                customerNav.setBackgroundResource(R.color.white);
//                homeBtn.setTextColor(getResources().getColor(R.color.black));
//                homeBtn.setBackgroundResource(R.color.white);
//                profileBtn.setTextColor(getResources().getColor(R.color.white));
//                profileBtn.setBackgroundResource(R.drawable.time);
//                scan_qrCode.setTextColor(getResources().getColor(R.color.black));
//                scan_qrCode.setBackgroundResource(R.color.white);
//                dashbordinsideLayout.setVisibility(View.GONE);
//                settingsLayout.setVisibility(View.GONE);
//                feedbackLayout.setVisibility(View.GONE);
//                dashBoardlayout.setVisibility(View.GONE);
//                profileLayout.setVisibility(View.VISIBLE);
//                myhistoryLayout.setVisibility(View.GONE);
//                navigationLayout.setVisibility(View.VISIBLE);
//
//            }
//        });

        TextView historyView = (TextView) findViewById(R.id.historyTv);

        historyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                viewHistoryAll();

            }
        });


        customerNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scan_textv.setTextColor(getResources().getColor(R.color.white));
                customerNav.setBackgroundResource(R.drawable.time);
                menu_textv.setTextColor(getResources().getColor(R.color.black));
                homeBtn.setBackgroundResource(R.color.white);
                customer_textv.setTextColor(getResources().getColor(R.color.white));
                scan_qrCode.setBackgroundResource(R.color.white);
               dashbordinsideLayout.setVisibility(View.GONE);
               settingsLayout.setVisibility(View.GONE);
               feedbackLayout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.GONE);
                myhistoryLayout.setVisibility(View.GONE);
                customerReg1.setVisibility(View.VISIBLE);
                customerReg1.setVisibility(View.GONE);
                ImageView topProfile=findViewById(R.id.sa_topProfilePic);
                ImageView cardProfile=findViewById(R.id.sa_cardProfilePic);
                TextView name=findViewById(R.id.sa_user_Fullname);
                TextView email=findViewById(R.id.sa_user_email);
                TextView pNo=findViewById(R.id.sa_user_phone);
                LinearLayout logout=findViewById(R.id.se_logout);
                LinearLayout deposit=findViewById(R.id.dashboard_deposit);
                deposit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        depositDialogue();
                    }
                });
                navigationLayout.setVisibility(View.VISIBLE);
            }
        });

        scan_qrCode.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(DashBoard.this, QRScannerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_QR_SCAN);
//        scan_textv.setTextColor(getResources().getColor(R.color.black));
//        customerNav.setBackgroundResource(R.color.white);
//        menu_textv.setTextColor(getResources().getColor(R.color.black));
//        homeBtn.setBackgroundResource(R.color.white);
//        customer_textv.setTextColor(getResources().getColor(R.color.white));
//        scan_qrCode.setBackgroundResource(R.drawable.time);
//        dashbordinsideLayout.setVisibility(View.GONE);
//        settingsLayout.setVisibility(View.GONE);
//        feedbackLayout.setVisibility(View.VISIBLE);
//        dashBoardlayout.setVisibility(View.VISIBLE);
//        profileLayout.setVisibility(View.GONE);
//        myhistoryLayout.setVisibility(View.GONE);
//        navigationLayout.setVisibility(View.VISIBLE);
    }
});

//        profileBtn.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        scan_textv.setTextColor(getResources().getColor(R.color.black));
//        customerNav.setBackgroundResource(R.color.white);
//        menu_textv.setTextColor(getResources().getColor(R.color.black));
//        homeBtn.setBackgroundResource(R.color.white);
//        customer_textv.setTextColor(getResources().getColor(R.color.black));
//        scan_qrCode.setBackgroundResource(R.color.white);
//        dashbordinsideLayout.setVisibility(View.GONE);
//        settingsLayout.setVisibility(View.GONE);
//        feedbackLayout.setVisibility(View.GONE);
//        dashBoardlayout.setVisibility(View.GONE);
//        profileLayout.setVisibility(View.VISIBLE);
//        myhistoryLayout.setVisibility(View.GONE);
//        navigationLayout.setVisibility(View.VISIBLE);
//
//    }
//    });
        Button validate_coupon=findViewById(R.id.btn_validateCouponpr);
        validate_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this, QRScannerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_QR_SCAN);
            }
        });

    homeBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        scan_textv.setTextColor(getResources().getColor(R.color.black));
        customerNav.setBackgroundResource(R.color.white);
        menu_textv.setTextColor(getResources().getColor(R.color.white));
        homeBtn.setBackgroundResource(R.drawable.time);
        customer_textv.setTextColor(getResources().getColor(R.color.black));
        scan_qrCode.setBackgroundResource(R.color.white);
        dashBoardlayout.setVisibility(View.VISIBLE);
        settingsLayout.setVisibility(View.GONE);
        feedbackLayout.setVisibility(View.GONE);
        dashbordinsideLayout.setVisibility(View.VISIBLE);
        profileLayout.setVisibility(View.GONE);
        myhistoryLayout.setVisibility(View.GONE);
        navigationLayout.setVisibility(View.VISIBLE);

    }
});

       timeStatus=OurTime.getTimeStatus();
        if(timeStatus!=null)
        {
            switch (timeStatus)
            {
                case "BreakFast":
                    breakfast.setBackgroundResource(R.drawable.foodback);
                    breakfast.setTextColor(getResources().getColor(R.color.white));
                    lunch.setBackgroundResource(R.drawable.viewbalance);
                    lunch.setTextColor(getResources().getColor(R.color.black));
                    dinner.setBackgroundResource(R.drawable.viewbalance);
                    dinner.setTextColor(getResources().getColor(R.color.black));
                    foodList.clear();
                    foodListStaff.clear();
                    for(int i=0;i<19;i++)
                    {
                        FoodSetGet foodSetGet=new FoodSetGet("2000 TZS", "Chai Chapati","VIP","data:image/jpeg;base64,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");
                        foodList.add(foodSetGet);
                        FoodSetGetStaff foodSetGetStaff=new FoodSetGetStaff("2000 TZS", "Chai Chapati","Available","data:image/jpeg;base64,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","120");
                        foodListStaff.add(foodSetGetStaff);
                    }
                    adapter.updateData(foodList);
                    adapterStaff.updateData(foodListStaff);
                    adapterStaff.notifyDataSetChanged();
                    Collections.reverse(foodList);
                    Collections.reverse(foodListStaff);
                    adapter.notifyDataSetChanged();
                    break;

                case "Lunch":
                    breakfast.setBackgroundResource(R.drawable.viewbalance);
                    breakfast.setTextColor(getResources().getColor(R.color.black));
                    lunch.setBackgroundResource(R.drawable.foodback);
                    lunch.setTextColor(getResources().getColor(R.color.white));
                    dinner.setBackgroundResource(R.drawable.viewbalance);
                    dinner.setTextColor(getResources().getColor(R.color.black));
                    foodList.clear();
                    foodListStaff.clear();
                    for(int i=0;i<16;i++)
                    {
                        FoodSetGet foodSetGet=new FoodSetGet("4000 TZS", "Wali makange kuku","VIP","data:image/jpeg;base64,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");
                        foodList.add(foodSetGet);
                        FoodSetGetStaff foodSetGetStaff=new FoodSetGetStaff("4000 TZS", "Wali makange kuku","Available","data:image/jpeg;base64,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","120");
                        foodListStaff.add(foodSetGetStaff);
                    }
                    adapter.updateData(foodList);
                    Collections.reverse(foodList);
                    Collections.reverse(foodListStaff);
                    adapter.notifyDataSetChanged();
                    adapterStaff.updateData(foodListStaff);
                    adapterStaff.notifyDataSetChanged();
                    break;


                case "Dinner":
                    breakfast.setBackgroundResource(R.drawable.viewbalance);
                    breakfast.setTextColor(getResources().getColor(R.color.black));
                    lunch.setBackgroundResource(R.drawable.viewbalance);
                    lunch.setTextColor(getResources().getColor(R.color.black));
                    dinner.setBackgroundResource(R.drawable.foodback);
                    dinner.setTextColor(getResources().getColor(R.color.white));
                    foodList.clear();
                    foodListStaff.clear();
                    for(int i=0;i<20;i++)
                    {
                        FoodSetGet foodSetGet=new FoodSetGet("3000 TZS", "Wali kuku","VIP","data:image/jpeg;base64,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");
                        foodList.add(foodSetGet);
                        FoodSetGetStaff foodSetGetStaff=new FoodSetGetStaff("3000 TZS", "Wali kuku","Available","data:image/jpeg;base64,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","120");
                        foodListStaff.add(foodSetGetStaff);
                    }
                    adapter.updateData(foodList);
                    Collections.reverse(foodList);
                    Collections.reverse(foodListStaff);
                    adapter.notifyDataSetChanged();
                    adapterStaff.updateData(foodListStaff);
                    adapterStaff.notifyDataSetChanged();
                    break;

                default:
                    break;

            }
        }

        adapterStaff.setOnItemClickListener(new FoodAdapterStaff.OnItemClickListener() {
            @Override
            public void onItemClick(int position, FoodSetGetStaff foodSetGetStaffStaff) {
                updateMenu(foodSetGetStaffStaff);
            }
        });

        adapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, FoodSetGet foodSetGet) {
                alertdialogBuilder(foodSetGet);
            }
        });
       breakfast.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               breakfast.setBackgroundResource(R.drawable.foodback);
               breakfast.setTextColor(getResources().getColor(R.color.white));
               lunch.setBackgroundResource(R.drawable.viewbalance);
               lunch.setTextColor(getResources().getColor(R.color.black));
               dinner.setBackgroundResource(R.drawable.viewbalance);
               dinner.setTextColor(getResources().getColor(R.color.black));
                foodList.clear();
               foodListStaff.clear();
               for(int i=0;i<20;i++)
               {
                   FoodSetGet foodSetGet=new FoodSetGet("2000 TZS", "Chai Chapati","VIP","data:image/jpeg;base64,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");
                   foodList.add(foodSetGet);
                   FoodSetGetStaff foodSetGetStaff=new FoodSetGetStaff("2000 TZS", "Chai Chapati","Available","data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAoHCBISEhISEhIYEhEYERIRGBESGBESERIRGBQZGRgUGBgcIS4lHB4rIRgYJjgoKy8xNTU1GiQ7QDs0Py40NTEBDAwMEA8QHBISHzQsIyw0NDc3MTQ0NDQxNDE0NDQxNDQ0MTQ0NDQ3NDQ0NDQxNDE0NDE0NDQ0NDQ0NDQ0NDExMf/AABEIALcBEwMBIgACEQEDEQH/xAAbAAABBQEBAAAAAAAAAAAAAAAAAQIDBAUGB//EADsQAAIBAgQDBQcCAwgDAAAAAAECAAMRBAUSITFBUSJhcYGRBhMyUqGxwULRYpLhFBUjU3KCovAzQ9L/xAAZAQEBAQEBAQAAAAAAAAAAAAAAAQIDBAX/xAAkEQEBAAIBBAMAAgMAAAAAAAAAAQIRAxIhMUEEE1EikRQygf/aAAwDAQACEQMRAD8A9TAjwI1RHATaFAjgIgEcJFKIsQRwgAiwEJARYkWARYQgEIQgOhCEAhCEAhCEAhCEAhCEAhCEAjY6EBsSPjYCRI6JASEWJAS0IsJRXEURBHCEKIsBFEKURYkWAsIQkCxYkWAQEIQHQhCAgiwhAIQhAIQhAIQhAIQhAIQhAIQhAIQhAbCBhASBgYGAkIQlEAiiII4QhRFiCOEKBFEBFkCiAgIkBYsSEBYQhAdCNjoBCRVKqoLswA75n187pre3atOefLhh5reOGWXiNWEwXz4CxsLHuPpLGDzf3hsqE24sNlUd5PCc8fk8eV1K1lwZybsa0Jm4rN6abDtnu4esya+dVG4HSO7j6zpc8YxMbXURpYdZx74t24ux8SY0VD1k+2NfXXZBh1EdONVz1kyYp14MfUyzkiXCushOfpZtUHHtDvmlhszR9j2T38JqZSpcbF6ES8WaZEIQgNMIQgJAwgYCQhCBAI4RBFEqFEcIgiwpRFiQkCiEIQFhEEZVrom7MB4yWyd6slvhLCUjmVPqfSNqZqgBIBb6D1nO82E9xqceX4us4UXJsOpmNmOeKl1S2q3E/cCZmYZi9QHexG9jfSJlh1tqqDUb7AbknrblPFzfKuXbHtP16+L48nfL+k9fFPU7RY37+v7R1BL/ABEbix84lFGY3KlF6seXhLdDAK12DEt8o2vacZjPPt3t129LOBwgZgttrfF0UcTJsdXCjRTGlR021HqZPlGoJV1JpIGw6ix3mRmdfTsN2PADcnwnqw6ccOr9eXLeWevxWrVAJn4jMaafE4Xuvv6SriS7Hckn5EJCjxbifK0rJlrH+EdFFvU8TOeWbpjxrNTPaY+c+Ckfe0rn2lpj9D/8P/qTJkq8xJBk6fLJ1RroRJ7TU+auP5T9jLtDP6LcXK/6lZR6ys2TJ8sgfJF5C3hLMy8boqGLRxdWDD+EgyyjdJxjZc6G449dw3qN5oYLH1FIDXcdD8Y7wf1eHGbx5XPLidxluNIIVjdftNmchg8QHAZTcH/vrOsVwB2jawW9+tp68MuqPLnj01LGylWzWiu2u56Ldj9I/A45KurTfskA3Fjvw+02wtQhCUJEixIBCEIEIjo0RwlCiLEEWAsURISBZhZvnopErTAZvmNyAZpZjVK02tsT2Qel+P0vOC0VP7Q4Lh1Ivot8I4b/ALzx/J5bL043V8vX8bimXfLw3P7XiKgD69vlGwsee0Eps3xv6XJ9TGooAsoCsBu1wFX95SxGZOgsF1gG2pdxfvtPDll3/l3/AOvXMd/69l9kppuWud/iIlWvi1cDQeySRfgLjkL8+6U6dGpXu73RAdyb3J6C282Voj3YQLZRYhdt7b28esmt9i6xQtgSqqx221abXH+49ZPhsEoGrYXF7/15SzRwwZAN16g3P5khWyWubDoLTt0Sd3LqvhXqYJTp0mzWPG5up7jJfdpSXUb+NpLQVWXVckfxXvGM5+Fad16mwH5l6Z5Tqvg/D44agw3G6kEWuJRzLAEFnXtI1gGHFF+U9N+fOWq5FhYANte0dh65XgfLkfGenDGZYarhll05bjDpYLul6jlzdLeO016bU/l0HqvD0kwUHgwPnY/WZnx9eW7z78MlMu6kekkGXDr9Jp+7bp6bxNB6H0l+rGek+y32zWy4dfpIXy08iD9JsaD0P1immelvHb7x9WN9H2We3N1svYcV8xvM6tgug3nXVK9NfiqDwXtH6TOrZkgN6aDV87gFh3gcBJ/j78H3yI8qwHuiKtTski60v1O/JiOX5289ilg0qku923tpuQvAH8zFpOztdiSTzO86PA/APFvvb8T1Y4zGajzZZXK7qvjKFNFAVFXY8AJW9nlP+KerKPQH95LnFcKLk8rRchS1LV87s3lsB9pZ5T004kWIZpBEhCAQhCUQiOjRHQFEURBFgKIQhIMn2hF0QXI7R4cfhmRSo06SaVtdh2mbdyL35+M0c2ra6q09tK2vxHaI3+hHqZRxOAQXITXf9JJNvDrPl/IynXbHv4dzCSsfN8190tqY94SeErYDViG1OpUaQTuRz2Atz6TTphLhPdnUTaxtt0uDwk6UdJK2BANyRaxa2wt4TzSb7vV1dM0nRSwAB0gAAKOUmfD6Fue1478drWlYvtflYHa8ndmKgk2bYW4G06zHbjamBrA2BW3I2v5yagxGosSb9QLDuiUnHM9OP4jWNPVq/Va1x06TvMbHK3Z7k8VsOPCRjE6E/wAVgDfiOcbVqU0F7cidrXmFjs3pvdQbHmOczlelrGbWGzNXrhNRtoNhe9973I4cu+aVN5wtHFWxdLfYlx/wY/idlh3vO/xbem7cvkSbml8NC8hVooaet5UhcjgbeEa2Icfrb+Zo1jInMBz4mp87fzN+8q1HJ4m/jvHOZCxjQY0QRGMFl0L2CG4lytnXuy1NVJcMwFt73N/zK2BFjedDRwlNCXCjW25bib/iTIjEoZVUrsHxBKpx92PiPj0nQogUBVFlAsAOAERjFR7yQp0SLEmkBiQgYBCEJRFFEaI6AoixsLwHXjHcKCx2ABJPQDcxbzNz2oRQcDixVB5n+kxnl042tYzqykc62L1VHc7XYsO4HlNIVUcBiT02JE4+hjENZ6ZJ3uy35sPiUHmNrjzm5QfskKLnp1nxct7t/X1Omai9SxAao7bHQAgud97En7ekGOm9xuxJPn/SYWX5olOs1OptrIYG4N7bbde8ftNzEU3LB1IKFNipvbqbc9pvG/xSzVJhQH16SGsSLD9NjYjvNxJGvqsyXSws62BvaxuDz8JBQoKjF6fxnjvZSdt9F7AmSYhqbMabJ2mXVZlb3bbb2bhfj9J1x1I53ynpsw7J358r2HdJ9CncHaYaYBKVZK6l9qZQqSai2JHfe/D0mvh6oYAg7WBHzEW8N+M6TKeGMsfcVsZcK3YY9FG5Iv4gCcLmNN0cm4Lb3te17cLef0nW51mNiUDbkWsOXUA9d/HwnHY6oQSVZSwIOk73248LTnllLdRvHGybrI/tRXEYcsbFagBHjcfmekYOtcCeQZ1ie2hv2g6m3HYNf8T0XJMbrRT3CevhmsXn5buuqR5KplGjUlpGnoedM0haPLSNjKiJ5E0leRNAjMcojTHoI2L+E4geH3nTsbTmsCLsviPvOhdpnamO0bSaxg0T9j9oFuJC8JpCQhCAkIQlEQixIsBYkIGAhmNndQGw30oQ5A/U/wCld/L1E2CZzGKqhq9VWOkE6dLcNgBcd5sJ5vk5dOOv134JvLf44L2jpFGSojtrA42toI4Ad1tpoZLn4dQHslSx7PW21xOjxmXUtBV9QF7q2z6fpuLzg2yinWrVKJbTUF3R0J0sL3279+E+fJPFe+3fhq59hdYVtQVxutviHfccIuSe07Uz7uq3Cw130q3eRwB+kzfeVqH+DihdPhWtxUjlqPIxmIylH7VM358pemJ1PQ8HXpuLqApJve+/TeFRzqAGx4lgCN+s83wmOxGEPZ3TiUa+m3d0nV5V7Z0nKrWXQeFzwHnw+0z3narr3G4UYXNw4ItoO1+8d+0Y4KrqRdQtf3Z4nwP/AHykeIr1Klvd6HS+pSpKm38Q5xMVUGhkKm1itrAjh38omULGJje1qewVjtoNyQDwIHdYevOczmlU3PC9yNXBjsbjv+82MwrCmfdhlZgmrSC1wp4bmcxjcRfUWuCCT1vfp38ZvCW1nO6jn8zqanA4kX38Z13spj9VMC+47JHfOYwuFNR7nmbzRpYeph3Dp5jkR0M+hjNTTw27r0/CVrgTSpvOTyXNqdQAE6H+RuZ7jznRU6k3K52L2uNZ5B7yMapLtErPGM0gapGl42J9UchldbmAxiKdKkO/duo8T+0lyXTcwOxB58pso9/GYmABtduM00aSUWSY3Ve3ebeQ3P2jSC3HYRlN7tccANI8OZmoi8DFvIlMeDNIdEhCAkIQlEcIkWAsQwiGA1jM3McBTq7sLN867N59ZoMZUrPM5YzKasaxyuN3HPYoPROhmFSmR+sbHuvvvOYzrBs7Cph7h1OsKLl1NtxbmOPlO1xTgggi4IsQdwRMTE4JG2Xb+E3K+R4jynh5Pi3e8f6e3j55rWSDKa6Y2jpqKNe6Oh3sfPqN5hY7IsRh2LYY60v/AOJuH+08pq0sBVpVNdMA32ZQw0kd+q3rNV8YEIWoPds3wtcaX62bgT3cZ58sM+PvZ2dplhl4ri6eMdyUqp7s8NLgXPh1jcTlIK6k49BOxxmFpVlKuA624lRqB6gj9pyOY4PE4RtVFy9P5H3IHS/GZlmS6sU8Li8ThGBRiVB3Q3KEeHLynV0PaM16YfSNQFmGxYEcj5c5zOGz+lUISuhpsdtRsUJ6X5edozH0zhqi1qJuvAryYcwZrLA6ouZlUSo4qEaWAKkdQdrn1+s5zMBdgo7ifLh/3um9j3SogqKQVZD4jbj5H7TNwODao5Yjib+A6Tvw4+3n5su2lz2eyou6LbdmC+pm5Vy0EEEd03PZbLgKgJHwqW8+H5k+IodprDmZ63l24TEZaaZuBcSzhMwqJsHNujdofWdHicLflMfEYDoIRZp5zU5qreBK/TeSf3x1pt5FD+ZjGiwgFeXuNV856Um8ygH3kD5vVPwoid5LOfxKyYdzNDDZZfjvArI1aqbM7MOmyr6DadJlOWhbE8ZJgsCF5TWpJaTSbT0RaXKS8zKoIUamOlep5+AmTj881Xp0+HAtNSI1sZjgT7tD3Ejp0EmwxnP4EE7mdBhhOmtC8keDI0jxCHQvEhAWESEBkWNheAsaxikxjGBFUaUcQ8t1Jn4kQsZuJqSj7zeS4y+8zS+8jpGirx2u4KkBkPFHAZG8QZRSpJleWdxFUypDvQqth3vfQxL0f6eco4qnjqQ7dMV0+ZCAT39D6TV1R9OsyfCxHdy9Jxz+Lhl6bx5ssfbhMbQoYgldDUanyupS57r7N5RTQdcK61NyitZjzA+Hxnc1np1BarSR+8dk/tKAyLL2YlldATcrdlF+vZnHL42U8eHSc89xxeS4d2oKm9i9/wB52mT5RYA2mpluQ4Gmf8Oo3+lmVwPsZ0FLDUwNnHmCJ0x47PLjnnMr2UMNQKbrsbEeRkhw80Qi/OvrA0x8y+qzppz2yXwsqVcCDym8aQ+ZfVY1qS83X1EaNuYbLB0iLlY6To2FMcag8rmVq2Ow1P4qn1VfuY1RnUsuA5S9QwnQXMpV/abDp8C6z4M37CZWJ9rKrbU00jv2+i/vL00dX7tU+Ngvdxb0mdjfaGjSuKfbfyO/2HnOOr4yrU+NyR8o7K+g4+cjRJqYjUxea1a57Rsvyg8u8yfBU5UwmHJM6HAYXhtNC7gaXCbVBZWw1G0vqIZPEcI0R0gWESEBYRIQGwiRZUEYY6IZFQusq1ad5eIkTpAxMThbzKxGCnUvTlSrh401MnKPQIiBiJu18L3TPrYUyrtWV48PIXpsOUZdukos3iGQazD3koeyiMsRwJHgSIvvIheAe/qDhUf+Zo1sXW/zG9YFowtIEbGV/wDMb1kL4msf/Y/8xEexjGaNCu+tvidj4sxkPuJaZo0tCbQClF0CSqjHgJaoZezcoFNEvwEv4TBE8pqYTKuFxNrDYADlIm1HBYC1tpt4bD2klKiBLCLCFRZKI0RwkCxYkICwiQgLeESEobeLCEIIQhASNIhCFMZZG6RIQIHpSu+FEIQqu+CEiOAEIQGnLhI3ywQhAgfKhK75WesISiFsvPWRNgG6whKG/wB3t1irlhPOEIEyZRLdLJRCEg0KGUqJfpYFRyhCRFpKAElVIsIDwI4CEJA4RYQgEWEIBeJeEJQXhCED/9k=","120");
                   foodListStaff.add(foodSetGetStaff);
               }
               adapter.updateData(foodList);
               Collections.reverse(foodList);
               Collections.reverse(foodListStaff);
               adapter.notifyDataSetChanged();
               adapterStaff.updateData(foodListStaff);
               adapterStaff.notifyDataSetChanged();
           }
       });
       lunch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               breakfast.setBackgroundResource(R.drawable.viewbalance);
               breakfast.setTextColor(getResources().getColor(R.color.black));
               lunch.setBackgroundResource(R.drawable.foodback);
               lunch.setTextColor(getResources().getColor(R.color.white));
               dinner.setBackgroundResource(R.drawable.viewbalance);
               dinner.setTextColor(getResources().getColor(R.color.black));
               foodList.clear();
               foodListStaff.clear();

               for(int i=0;i<16;i++)
               {
                   FoodSetGet foodSetGet=new FoodSetGet("4000 TZS", "Wali makange kuku","VIP","data:image/jpeg;base64,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");
                   foodList.add(foodSetGet);
                   FoodSetGetStaff foodSetGetStaff=new FoodSetGetStaff("4000 TZS", "Wali makange kuku","Available","data:image/jpeg;base64,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","120");
                   foodListStaff.add(foodSetGetStaff);
               }
               adapter.updateData(foodList);
               Collections.reverse(foodList);
               Collections.reverse(foodListStaff);
               adapter.notifyDataSetChanged();
               adapterStaff.updateData(foodListStaff);
               adapterStaff.notifyDataSetChanged();
           }
       });
       dinner.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               breakfast.setBackgroundResource(R.drawable.viewbalance);
               breakfast.setTextColor(getResources().getColor(R.color.black));
               lunch.setBackgroundResource(R.drawable.viewbalance);
               lunch.setTextColor(getResources().getColor(R.color.black));
               dinner.setBackgroundResource(R.drawable.foodback);
               dinner.setTextColor(getResources().getColor(R.color.white));
               foodList.clear();
               foodListStaff.clear();

               for(int i=0;i<15;i++)
               {
                   FoodSetGet foodSetGet=new FoodSetGet("3000 TZS", "Wali kuku","VIP","data:image/jpeg;base64,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");
                   foodList.add(foodSetGet);
                   FoodSetGetStaff foodSetGetStaff=new FoodSetGetStaff("3000 TZS", "Wali kuku","Available","data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAoHCBYWFRgWFhUYGRgaHBocHBwaHBoaJBoaHBoaGh4eHBocIS4lHB4rHxkaJjgmKy8xNTU1HCQ7QDs0Py40NTEBDAwMEA8QHxISHjYrJCs0NjQ0NDQ0NDQ0NDQ0NDQ0NDY0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NP/AABEIAMIBAwMBIgACEQEDEQH/xAAcAAABBQEBAQAAAAAAAAAAAAAEAAIDBQYBBwj/xAA5EAABAwIEBAQGAgEEAgIDAAABAAIRAyEEEjFBBVFhcQYigaEykbHB0fATQuEUUmLxBzMjsnKCkv/EABkBAAMBAQEAAAAAAAAAAAAAAAECAwAEBf/EACkRAAICAgIBBAEDBQAAAAAAAAABAhEhMQMSQQQyUXETImGhQoGRsdH/2gAMAwEAAhEDEQA/APL6YlStaoqSJpsJIABJJgACSSdAANSpFxuRaXw94TqYiHvJZSNw6PM8f8Adv+RtylXvhrwi1kVMQA5+radiG9X7Od00HXbZtCZRoWUq0D8M4bSoMyUmBo3OpcebnG5+2yOATQkXo2ISSlmQmJxjWNLnuDQOf25rHcV8aESKQj/kbk9hopS5Yp1tluLglPK18vRun1ALkgDqqzHeIKFPV+Y8m399F5piOMVamZz3kxFididgg24ou2UpS5HqkdUPT8Mfc238LBtMZ4wefgaGj5n3sgK3i+vHxNHXKJWXNXYz2SaA4x/lKoO8tl5PjqowSX+WXNTxLUddxJ+nyTafHif7Obm1NlRubGo0358j2UjBvISS4IPxkPHzSj4VfRpMPxfYO6olnFnzZyx7idc1wSIm4FtuUk+6lp4o81GXpFtFo88Je5ZNaeNvb/aPVQ4ziT3sIc8x9+SzTqh6o+o/KwN/tAJ6n8JPw9ayaUoSf6V/c494FpjfdMbPy/d0Ox5mSCiwrNUKS03sJEZhH17qx4XxOvSBFOpkDuxHycCAeqrW1IujKbx0EfRRcnHQXFSWUbnhXjAOytqsy83tkj1ZEj0JWkwPEadYZqb2vAiY2mYkG40Oq8ubWHKR0KP4Ti3U35mEgjUGYcORG4+hVuP1koupZX8nHyeji03HD/g9MBXQVW8O4qyrZph4+Jh1H5HUKxBXqQnGauLPOlBxdNHSsj4h8FU6svoRTfqW6MeeoHwHqLdN1rpSTATa0eEY/h76TyyowscNjy5g6EdQgXMXu3F+EUsSzJUbP+1ws5p5tO3bQ7ryrxF4bqYZ3m8zCfK8Cx6O/wBrum+28TlDyi0Z3gzbmqNwRL2qJzVMYhypJ+VJEFAGGoOe5rGNLnOMBouSV6h4Z8OtwwD3w6sdTqGTszrzd9tY/DHAG4ZuZ0Gq4eY/7B/tb9zv2WiamWCcpfBM1PDlG0py1i0OL0BxDijKbZkTyQfGuI5GkN1Av+F5vi8a9znEkkzvdccuVzbjDxt/8O/h9KuqnPXwWHHOMOquMmw+Qv0VOyNSf8i0fQoepXds0KIUnOMm/wC7J4caSKz5f6UsfCCzig34bzz+f2T2VidSfdCNpAfEZ9VKXToPUotIVSfwT1XB0CINyDfYExrF7D5JlNpF/v8AVMa6LnbRcdVNo16WWzoLpu2FE7b8vcqOqNhrv0/yhr6kqVsjcrUKn8EtXDPa2TccwQdecaeqiYwqSm/Yk9Y5KPES3+1tv8jZNYHjIXSrXa2I/P4Vzka1suvP17rPF5bDom4VwxwePMZAFvVcfNHKfg6eOVqhteoDBGo+SjYCbypGUmDbSb6rlUg6JE/CKpIRjmE+k+bW6IKoHSPLPO6IpNuIF768kzSoK2WWHqT9EfTdGv8A0qmi/wA5ERsACTeff5Kepio7/vJc04NvA9h1PEFtUObMtOZsdOfrYr03h3EGVmCowktJIEgjQkb9l5PRqhzTPxfK3f5reeEMWw0sjXEuBzOBFhPlhp3HlB//AG+Xb6SbjLq/Jw+t4049ltf6NPmTpUAcngr1DyiWVDiaDXtLHtDmuEFpEghPBSlEB5V4s8Kuw5L2S6iT3NMnZ3McnehvE5N7V77VYHAggEEEEESCDqCNwvLvGHhg4c/yUwTRce5puOx/4nY+hvEpKN5RWM7wzHwkpciSkVPUmhStUUxqga3HqDDBf8ghKcY7ZKMJS0rLcKHFVsrbGDt+UyljGPbnYcwOkKk45jw1pvc/NR5uWo48lOLibnTKXi+J1Gb/ACspial7IjG4qbTKrXPUvTcXVHp8/L1j0HOMkeYj0t81w0Ds5Oiwvrt+VG5pNgTHVdNHHa8o4MOZ1lStB5rjA4bj5KTL/wAp6LOzKloa2mTqCe6kayU9vQSuDYR+nkhkZpHWMTnOB9Fb4bg9d7SWYd7ttMoBkcyJtIPKZ2Wrwfglj8KBkdTrua3M5+ZxD2zMAmA0kat1BtslRpSilR5u5lzBKie4kRMr02v/AOOafl/jqvaR8WcNeHdQBlLTPUhNr/8Aj9jmxnyut5mtItN/LmIEjf8A6RtCdlWDzsCwRuAr/wBTstQ/wBWDw1r2lhBJeW/CdgW5vMTztvpaaLinhvE0Q55puysuXtu2NydxG9rfJJKPdUNCai7snxBaWiLXv9PuhmtOgUeBxzXDK716HRH4ZjZJ2XG044Z2RmmrRC03PfQptRpFwVDXqQSR2HzSa6RI216JlHyP2Jy8iLC900i0kwFE94BB2+6dVpOdBFgfmisGbsNwhAE6dZj3Wj8KY0U6jc9g9pZIvBkEadYHYrMPDQ0NJ69tOilw2IuLkgadN0I3fZbFmlKLiz2MOTw5U3BuKNrU2uBkgAO6O39DqrRr16sJKStHiyi4umTgp0qEOTwVRCDioq9NrmlrgHNcCHA3BB1BHJPlcciA8/xngJ2d38b25J8uZpJA5ExeNJ6JLewktS+B+8jzniuLcWF1w3YGyxFbFEvJkz0U3E+LVKxhzhHIaBVbqgaTf5LzI8eW3ls9GMuirRcYHxFVpgtbBbfUG3aCh8bxRzzJ/fRVBxSc3Fbwqvjum0IuVJtp5Ca2GcBmO+3fmocrN5npEd+ak/17jsD3+43UQbKMbrOBZNN2s/Yj0uE5hC7TI0InunYPKHtL/hm/XojfyGraoQHNP0ExHdairwRlUtqNZkEhoawNA1kvdsLWAEbXMrUcC4LRa1zC3OA/MDUa10PygW8o0G/UpHJbGtRTsx/AfDT8RD3eRhJbMHNOWZAIjLMCehXoXBPDGHoBjsgNUNALy57pdlhzmtJhs30GhVrhqAa3n2HupzqIBI6BByZGUnIfTZF4HRT1PhJO1+0KIlwaO+nSbn0Cja4uMZbXmTboQBz9Erl4FSHMrN1PupW+a8COeiiFJzB5dJ0dcX91PTqZhY+YDQc0E3phZxjmklov0XMRhQ5pBFiII2g812iwAkxc8lM2nabjmE8bFZhOKeCGVC5whjy4EOGpAEQT1G8LDY7C1sM406jYOx5j9/wV7m8AXmeSquOcKZiGw9gtoYuJ5eqVxTVFYcri8niVd9gReLxzuftCdh3mCL31j90VnjvDWJpVnhrC9jSfMLAt0k8iJ9t1XYilluBbcISSWDphJydk9OL6WvdTMqQNpUOHY1zSf7GwBECL69ZTaWGcXEZgCLGduyk0ns6FLAhWzGJ0/dVLSe4wMo0Ine6HxFFwiDa0GLfsz8kVTpyy5v2HMRr39impCp7suPD1Z7HtPLlaRN55iF6LhsQHAELyxlUAZWk3I2tAk7315jn66jw/xGwbP706JuHk6Sp6Zzep4+y7LwbVr1K1yBpVJRDHL0kzzWEZkiVGHLspgHUk1JYB82Orck1rOacxo5aogMELlbS0dkYuWWNfhPJnne4UAZCJ/kMZZtySp0/MAe57D9hBSa2Fxi2qRIym2NzZJ4XKj+Siz80qTeSknFYJmMPMI7C8PdUfDPOLEguDCP07hV7X8lb+HqefEU2lxALrkcgCSPWIWkmaLVG74Dgy9kBuRkmbkkmbwT1m60lFgs0Dyj3KFwNDyljbNAsj6NPKFEnJ2wimPZOgD+8Dlb7rjNJ6rj3tIhwnkd0GKdEXBdc/NS0xYQosPDSSHSHfXuomB2ctBMZtBuNY/eSwQ2qx5aSI00Mx/wBx9lC1/wDGwWl1ifv3TspDuQvN9T15pr2BwAOh5DRF/PkCEysZzAkBzgLxuYn3RzWbEz7SFDUDQILQQQdh7BQDM9uZh0IIjlv7bLLBthbnZnkRAGvf7KPEPFxEmLDTsm0SQHST2PP9hCUXh7yWmf8AqPqEewKOOpNdmadYh1zuLQV5Vx/geIw7iC05MxDXDK6QN7bxBjVeu4WiS5xJgbo3F4Fj2FjmgtOxGh5jkeqKV7GjyOLweA0HgkA87nmu419xkGW829NbrVeJPCdSm41KQL2XLmht2xeSBqInS4WOpAzmJka6JOtOzsjydkkiYvEXM205dQosNUymXX5DmiKrCfPl5WIgfvZRDBPgF1htzJPRBOLWR6dhtNgcCdje7tBpBva82Vhg3FsZTsTY7NMffVC4Vg1OWBlIF4Glj1/NpUpqtLi4sDTeNdDedj81KSvY9pYNzwrFS0K3pvWDwGMLCDqw6gbdlrsJiA4AgyF2+m5e0ae0eXz8fWVrRahydmQzHqUOXWmcxLKSizJJjHz1TaY9Pkk4FSUxmsJLp0A23PRPktvlnuJXDeT0awDvKkw0mSegCie6SiKLYb6k+iLeAca/UQ1jeybkTqbHPcco6+idUYW6m6ZOsCSV2xrW3V54d4M/EvcGPDA3KS68tk2ygau8p5d1RtK2/wD4+HmeYNgHSCQDMgNLdCRcz1+YloEdNm/w2HaxrRmu207nvzRVBhkySdw2BYczuhMHOZxPRENeG5ng6gX6DruFD9xTuKqmM2jQd9zonNw7XMJBJMSCDonMe1xa2PLFj1/d1yk8h72nb3H7CFK8hIqrfLlGgHujuGlxDQ74g2/sPuq/CUzEkmNupPfqi8NWLJzkTBsOpsJWjh2F5VHKzZzkvgy4CdOlgnYamxjQCXOMfFfXoNBroo3AuaM2vTn+FMGOgRA7LAJ3MLqcO1F+X7ZQ4EFjhA8m/Sd+yNpmSdBtdcoUmMkNN9xqB+Eatpi34HYuoWAFrc3qB++iqH8QIdBAB5bXOyJw1YuEuBAdcNtunuwIcG5zeZMcuX+UJW9BVLZLgKkuIIGgKPc6YhVtYteZa+LRLT3vI7qWgXExmMJ4usCtXkfiG7DTUryTxXwhlCq9zZZTLgROxcATl5tme0L1+oPKZ206oJ9IPDg4AgtI05gg39UWrwxoTcXaPC6tZgAAdO3br+8k+k+bzPuifFGEptxDxSY5jW2IILZdeS1pu1pEW9YEqmpuLD0Sy4lWGXjzO8lm0mdTrNyiRSvEz0Q9AzdFU7Lmk2dNqsBLHkW6I7g/Ff43Q4nLvAmDzVYKvNNp1IOo9Qlg3F9kDrGdxkek4euHAEEEIlj1huHcTewgH4LiImIJFj09dFf8K4w2qNC0jnuvR4/UReJbPPn6eUbaykX0pIX+VJdHdEep4vw8Q0kdiUzEYlswPMfZBAmIkwUgVx/juTbPQXI1FJCfKmrGGDsB+VC18kLuJfeNtU1W0hU6i5EFNzmnMCR1U7q5cZdBOmkIcmwCeCqtLbIKTWEyfDMzOaOZA+Zhev8ADMKKdAMYQCwASe8kkDcmfmvJ+Hva2owuBLQ9pdFzAIJtuvVqVdwsSAx0Q7Qybb87aqU3gZqlQdhamc6QR8XY6ehI9kbTaHuvoNhv3QlOnlbDTBOpO+unIpmBxPmIg2MXBEnpzUfOQfQQ58ElujXH2KNqVmTng3DRuNybj1Sa8Xtcm6jOJGcsyyYmTpC1UbZI9+YiDI9rfdCZ2vqPG7MonuJRucZQ4QSBoIt3G3+FWYCgBL3/ABOOYjlyFuSVp2FaLCo15gQJMD0m57wUa2mGA30EwTM/PcqDDuJ80QPtzRFaow5my3M1oMHrMfoTpLYrZOzK3zkXt6KsqYoEVCDrP3HsoamMe3OXi0WHp9ygsBh2ZczjM7E3Syl4QYx8sPwBfJzNloALRaSTtblG/NF53OsZZzEXIjadp36FTsdlywBEC7R9vupKpJEGLXHy/wC0yjS2BvIJhcCGzE3cTtqfRE/A4CLG4PUG/wC9V1hy0xDo5E3+vRCOqkw4za/aei1JI2WFuf5juHW10gRpsmMY7PEjLAPzkXUtIta2Tq+/e1vZPw7QBzJTrLF0YDx3w5oqNcCMzgQR0BkH5uPyWFxOBi8L1LxpTBa1xjMHQP8A8SNP3ksVWoyNE7WTJ4M1h3lpIKMZXaRCg4hTyyUPQEzJj01UOSC2dPE7ww99RPZVDbx++iAcCRPUb3lTgF7JgzIb979dfkp9V5Kyj8MOoYrM+Tcnlb5R0Wm4JRDgToQRp1/6WV4e+CHBgOxub9YJvubfZazgriCTaHCY3kf9laMYvkVg5XKMHRc5kk6yS9KkedbPDAVxxXR7ppUzovBLRAkT+7pV7uMIZjzmEaSiaryCeqDVMaMlKDX7kBCZ6pVAVLhqJeYCfRDbDOCk/wArLTuZ3A2XqDKwfkh99fl91guFUg0ZSeccwei2WFZ5W5fisJ+5UJyvRRJ+TSU6ocCAYcL/ACXcFUzS6QdLW+apv43NMGPQ6rpqGmAW2J1Hz5KTdPJupfGp5xmMWJHUD6qSliWkOfljUXtIVZh8Zna0xcSJPpb2Cfme8lhAyzPeI19UGzUE0cQCyWkQXXPLvyRP8Li2RcnSbWSwzoAiIJ/Y9UXxGrla0DUm0bgC/wBQili2a8jKDDYOEAaJzyHkFkSLIYufE5oHp9Uxuhe2JibWnlK1+DBXEacBri4XOUtjaCZH09UCxzA9rWsmSBNrSd/8K5wRLmDO3XUG/ssxSqgYl0nIxpNnWmCMomYB39FpLT+TR8o0z6wYMrRJ5cu/4VdQo13Pzl0iILNI5GZibKyc3Nle2Dp6g6FdbTLHWJg3ImRKLjf0BOjn8IeMpJBbcAcwCII5IVuHcRlPwf7jGo2E+vyVp/ED5v7C4P27JtRwe249COqZxsVSIskkA7AD/P0RLWwEyq7QAXSpu5m6aKVgbwU/iCg18NdyJ7afj2Kx1akIsFpPEONIeWA/1B+ZP+FRkSFRVYpmuKYaWlZg1iDB1C3eLpWKyvE8BN23cOV1OSSeSkZYwwGlWFwUdh6ZdZpkb73/ACgG4KoP6O//AJP4Vtw6GtgszEGTeLqU1j9JeHIm/wBRZcM4c2YeTB19dStJgGQPl7T+VTYSo55lx+yvcMLBHg4XfaWWDm5rj1joNzJJiS7TjPEWv3XKr+Sa8WUcFIkmVbaVCz+ynrYkai5+ihDNJ3Tn09wi0mxYykk0hjnkq58Ns87s3L3kKoazQq/4Vl8okNcBYj+3P1Qk0kGEW3bLfDUWEuDfiBlx1ubjtYq94XIDQSCCJBHtJ7LP4d7sxa6DNiRYkHS3zV5QIaA0WA0i0dlC0yrTRf0D5gA0Sdyoq1ODlc4Oy7xE2QuHxXmEm+iXEMPLS4uJk6bAKclgC2E0cKSQ4O8s6DorFzi0jI0kCx9YvPRVfC8SwNay8zAHfeTsjq2Na1waHDUD3QrFmd2WbHMa9hOgn2Fj1uQpH4lj3iXQIIHPb6/hAYlg8p3Ex7BScMqQ4Fw18v4RvNGryW1VzXQ1oBIE3CqsRVc12VzT6e3VXDx/ZpAPbUb/AL0Qppue8F1g2fXt9U0kxUyN+MexkugagXna3qheGNfkzG4e4km2+/zTeO4ljMrSwGCDLgfWJ6LtNz8ktJE6C4F+bTole/obwWtHGkODIBd9R+bhNxWLIJ2gib6C2/2Q2Hphr2ucbg9gZEfdGYiiJLjE2t3sPomt0Lixr8Y8kBpseg+sKegHteATIcCe0a/UKLCYeDH6IR1apliBJJjlAQjby2BtLCJHsEzv+7bKObG973TKepLjraOiruIcTZQbLjrbnAVU/IjKbxJi6JzDODUbECNRykCIifUKlpvLh5RdDVHjEVnmQ8T8cQMogAAc4/bq9wlAACApy5GnURQFnDs3xku6bfLf1lGMwAAsB6CFYALspN7NRXuwI5IerwtjtWj5ffZXC7CODGbPDiwyJLeW4/KMoPBEhWz6QIVfiMIWeZvqP934PVVhLqBtoklJRU6zSAkunsjWeTYzBht9iqx7eS9Ax/A2OkxEqmHCG0nZozRpmk+w3XMp9fcdkkuTRliCCJB5XU1J82VpjWGZeI5BVmJAEFqpGXbwI+Pqrs45kHMjsM9oAcNZ13QTXyFLhgLn9haWsm41nBouFYttR4zABzRbrvPsPmrUPz3vBtuPYrG0sRkqNc2xBHvY+xWrwuLLxcC2t/dTlEaWyzwByw07b9NlcY6oIazpJVC3EANLtgL9eyJfUdULfNBsLcvylehdsh/kDX5TsZjmFf0i14ywABHoslj6Ls2WZcIuNpj8hc4JintLwM75cDrrtv0hSi0yso4s3WXK0wc3roOW67w8uewhwh28X7EdFWsZDsxJjlbVWDK7gBlaADzO3ZP5JFzSe7QEEiwldwdUEwZ9UHhq/nbF7H99lzGuyuLw/LBE6bgflN+4pF4ge/M0wMokWFxB1Jm4+l0/DVnloBbvcqOhjKbyQ57pGk++llM2TSzB2USJ7EgH6pPN2HSpoJgneYIMchOqOfVzQAdjJ5aR9/kqtuIa2TnEEQdDb9KNoEBsC+YTPQ21/dVRCNhNEc/SExriYLoBE6fb5JgeQYA8sWuLqv4hi2uaWZspGrdDpod4g7I1gFhWNx+UkNEu9O0k/uizXiXigbS/jIaX1BABEwDMuE8oN+cJmP4yGAmCTYEja1lj+HTVrl7iXS6STe2wlLKVJsRmr4VhQxgAEWEq3ZZA4Z9rIkVFzrCMTl6b/MOaq8djRoDdDYes5xg3HRBzoFl0/FAaqek8OEg2WfLiCZJKPwuKgQbdFozd5MWzCnPpgi6Gw9cO0RjXLohKzFNVwAkpK2cxdT9UJSMq4JhYpE1y6GkUTMb4lxAL8kaXJ73gdIKzlWlyWo8S8Lc52dt+Y7bhZWuwt2PqkisnR2XWiJg2CkpPIMb7eqPwbWFgkAOjXqhMQC0hw2IIW7W2mg/jcYqSZIMO4mTYj7I7B47YkB2k8+/IprRUeA5rXSdREX2MlSDgzpzvOVp21g7+iRtPDGlWGgh3EntAZlBE6yb7wR91bcJqHM103JEN5Ac/wq1opsF3F0aRefUxCP4VVDnghoaP6jXbeeyjNtoaMVdjsViZc7rvybO3IrnD6gAsYJ5D6KjxuJOYjbvuFJgMU6AAJSqDpDNrKNhRxUEF+Yjpee4Vxh8VILiR2mwGw7rGP4gQBqY2EfXZFUOJEMiBJumsk44NpSx7TBDwI1H1lS1sfScPiDiRtf3WHGKJBvZ23NSYV8ua3KTmIENEnsAFnLGgdTY4QsDtoH7JVh/rGxDSCJi3PVAYfhpgZjlHIXPzP+UXTwrG856lBOtISVA9fEZHiwIJg9yCbKSpxhjQQDcRAg8lLVw7Ha7fu6pOL8HL7seBHcG3XQ+yZOhCerxx5ADJETmmNjEdRr7Ks4pxNjyCRJFvKZlV+OZUacpBb339dx2UdKmdXR6LdnLBsIGx2Jc4OkQD+36qXgOFePNlN9NkbRw2ciR5fqVfYaiBFlKWXSJvIPTY/sntoP8A97vYfQKwgJhcEvVeQUVWIwJN8x9vwoqdJzbgBw9WlXBcmFgQ6RNRXvrNIMgsJ3N/cKH+MgjKZurR9EHZV1XClhlluY2/wlcWglpgG3Ks2qo4bjA7ymzhqFascr8WgWTZUlxJdVAMlmXQog5SNVWMQ4hkhYvxHla/LGwJ7mfwt04LPeIOHMcDUdqBrz5D5lK0PF0zF0njNHPTuiadcNdLhI3H4UX+nEGNVzANDqkP0AnuldPPwdC7JKL8l2zxAMoa1jh1/wAoeo+o8k5mtBNjqTOwB1Kbj2tAhrULTfLmuG1x6iPqopJq0qHcerpuyxGDykZ3Fx5CIPeE44oMBsMx0j+o6dfwoGvdIGUwSNxf1XH02PcYBB5Hf3SVfuH7ViIxtdwBgM7xP6U01z/Z09NB7KFzbOA1sfe6DfY852VYxTIyn12XOGxsObENAM+Wxgdd/VPxOJcx7gbmdQZE8hHLT0VYAWgHnH5UjalkOlsP5K1st+GufVqNawFzzZo+/QAalelcF4W2g2SQ6oR5nx7N5N+u6rvCXBP9OzO8f/K8DNOrG6hn569gr2q+AptZpEpTskq10O6odesDqftaUO18+YmBz/dSpTUzNgNIv6kHny0+i5ObncX1iPHjtWzrWk6mYUlJgG3TS89+n3UbCLgDyjTlA39lOwwJJ/baLmlyze2UUEiDimGFSlcAEEQeRAJP0j1WWZTJfkIiLEcoWyY8OIAuGkknuB+NUDxDAj/2NGhLHdsxyn3j1byXT6ecpJo5+aNZB8NSgIoWUVI2Ti5dGEROuehMRiA3VOq1gNVWYp2a4NlKU6MWGGxoNjblKINVo1joqCm4b9Y/fmpnvKRTwYuRUXXCVX4atIRrHqkZWYFxND+zTBG6P4Xjc4IdZw91G7RVNd2R4cCRsSOW3yMe6dPq7FaNX/KOY+SSof8AXf8AEpLp/IJZXs1U7FxJdBU6VR+KP/S7u3/7BJJLLQ8fcjGhD4P/ANoXUkkdP6Ome19h2I+Ieqmwg8wSSUX7R5+4n4n8Y7hA1/jb3+6SS0NCy9o/GC7ex+pVXiPiCSSpxEeQIxOjfX7K18KtBxGHkT/8jNb/ANgkki9GW2ewlDYv4SkkosmgWpo3u77I3Dan95JJLyJe5nWtHG/Cu4nRvZJJLL2m8kmF0Klr/BV7N+rEkl1ek2yXMVdLRcekkuqRzFZjdChKGg9UklzyMcbqF0ahJJIYnwqPYkkrcegIeq7iPwu7FJJWejMckkkmJH//2Q==","120");
                   foodListStaff.add(foodSetGetStaff);
               }
               adapter.updateData(foodList);
               Collections.reverse(foodList);
               Collections.reverse(foodListStaff);
               adapter.notifyDataSetChanged();
               adapterStaff.updateData(foodListStaff);
               adapterStaff.notifyDataSetChanged();
           }
       });
        Thread thread=new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Calendar calendar = Calendar.getInstance();
                                String currentdate = DateFormat.getInstance().format(calendar.getTime());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                                String formattedTime = simpleDateFormat.format(new Date());

                                meal_clock.setText(formattedTime);

                                int currentHour=calendar.get(Calendar.HOUR_OF_DAY);
                                if(currentHour>=0 && currentHour<12)
                                {
                                    meal_status.setText("BreakFast");
                                }else if(currentHour>=12 && currentHour<16)
                                {
                                    meal_status.setText("Lunch");
                                } else if (currentHour>=16 && currentHour<24) {
                                    meal_status.setText("Dinner");
                                }else{
                                    meal_status.setText("Ngano");
                                }

                            }
                        });
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        };
        thread.start();

    }


    public void alertdialogBuilder(FoodSetGet foodSetGet){
        AlertDialog.Builder builder=new AlertDialog.Builder(DashBoard.this);
        View popupView = LayoutInflater.from(DashBoard.this).inflate(R.layout.alert_dialogue, null);
        builder.setView(popupView);

        LinearLayout confirm=popupView.findViewById(R.id.ad_confirm_layout);
        LinearLayout error=popupView.findViewById(R.id.ad_error_layout);
        LinearLayout success=popupView.findViewById(R.id.ad_success_layout);
        Button confirmbtn=popupView.findViewById(R.id.ad_confirm_button);
        Button depositbtn=popupView.findViewById(R.id.ad_deposit_button);
        Button viewCouponbtn=popupView.findViewById(R.id.ad_viewCoupon_button);
        ImageView foodImage=popupView.findViewById(R.id.fc_foodImage);
        TextView foodName=popupView.findViewById(R.id.fc_foodName);
        TextView foodprice=popupView.findViewById(R.id.fc_foodPrice);
        TextView dismissbutton=popupView.findViewById(R.id.ad_dismissbtn);
        TextView alertmessage=popupView.findViewById(R.id.fc_alertMessage);

        alertmessage.setText(foodSetGet.getFoodPrice()+" will be deducted from your account");

        Glide.with(DashBoard.this)
                .load(foodSetGet.getItemImage())
                .into(foodImage);
        foodName.setText(foodSetGet.getFoodName()+"");
        foodprice.setText(foodSetGet.getFoodPrice());

        dismissbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView alertmessageSucces=popupView.findViewById(R.id.fc_foodStatus);

        alertmessageSucces.setText(foodSetGet.getFoodPrice()+" deducted from your account");
        TextView dismissbuttonSucces=popupView.findViewById(R.id.ad_dismisSucces);
        dismissbuttonSucces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView dismissbuttonError=popupView.findViewById(R.id.ad_dismissError);
        dismissbuttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialogNFC.show();
                foodSetGetMod=foodSetGet;
                scanstatus="scan";
                nfcReader.startListening();



             }
        });
        viewCouponbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            dialog.dismiss();
//            viewHistoryAll();
            }
        });
        depositbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                confirm.setVisibility(View.VISIBLE);
//                success.setVisibility(View.GONE);
//                error.setVisibility(View.GONE);
                dialog.dismiss();
//                viewHistoryAll();

            }
        });

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void depositDialogue(){

    }
    public void threadDestroy(){
        thread.interrupt();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcReader.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcReader.stopListening();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        nfcReader.handleIntent(intent);
    }

    @Override
    public void onNFCScanned(String tagContent) {
        // Handle NFC data here
        if (scanstatus=="null"){

        }else {
            Toast.makeText(DashBoard.this, "NFC Tag successful read ", Toast.LENGTH_SHORT).show();
            progressDialogNFC.dismiss();
            progressDialog.show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (tagContent.indexOf(',') != -1 && tagContent.indexOf(',') == tagContent.lastIndexOf(',')) {
                        String[] parts = tagContent.trim().split(",");
                        cardNumber = parts[0] + "";
                        userID = parts[1].trim() + "";
                        progressDialog.dismiss();
                        Toast.makeText(DashBoard.this, cardNumber+"", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        pinConfirm();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(DashBoard.this, "Card is invalid", Toast.LENGTH_SHORT).show();
                    }
                }
            },3000);


            scanstatus="null";

        }
    }
    public void pinConfirm(){
        AlertDialog.Builder builder=new AlertDialog.Builder(DashBoard.this);
        View popupView = LayoutInflater.from(DashBoard.this).inflate(R.layout.card_pin, null);
        builder.setView(popupView);
        EditText[] pinBoxes = new EditText[4];
        Button confirmPin=popupView.findViewById(R.id.confirm_pin_button);

        // Find EditText views by their IDs and store them in the pinBoxes array
        pinBoxes[0] = popupView.findViewById(R.id.et_cardpin1);
        pinBoxes[1] = popupView.findViewById(R.id.et_cardpin2);
        pinBoxes[2] = popupView.findViewById(R.id.et_cardpin3);
        pinBoxes[3] = popupView.findViewById(R.id.et_cardpin4);

        // Set up TextWatcher for each EditText
        for (int i = 0; i < pinBoxes.length; i++) {
            final int index = i;
            pinBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < pinBoxes.length - 1) {
                        // Move focus to the next EditText
                        pinBoxes[index + 1].requestFocus();
                    }
                }
            });
        }

        // Retrieve inputs from all EditText boxes
        String[] pinInputs = new String[pinBoxes.length];
        for (int i = 0; i < pinBoxes.length; i++) {
            pinInputs[i] = pinBoxes[i].getText().toString();
        }

        // Use the pinInputs array as needed
        // For example, you can concatenate them into a single string:
        StringBuilder pinStringBuilder = new StringBuilder();
        for (String input : pinInputs) {
            pinStringBuilder.append(input);
        }
        String pinCode = pinStringBuilder.toString();

        TextView warning = popupView.findViewById(R.id.tv_warning);

        String firstWord = "CAUTION: ";
        String secondWord = "ENTERING WRONG PIN 3 TIMES WILL RESULT TO BLOCKAGE OF THIS CARD!";

        SpannableString spannableString = new SpannableString(firstWord + " " + secondWord);

// Set the color for the first word

        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.RED);
        spannableString.setSpan(colorSpan1, 0, firstWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Set the color for the second word
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(Color.BLACK);
        spannableString.setSpan(colorSpan2, firstWord.length() + 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        warning.setText(spannableString);


        confirmPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myPin=pinBoxes[0].getText().toString().trim()+pinBoxes[1].getText().toString().trim()+pinBoxes[2].getText().toString().trim()+pinBoxes[3].getText().toString().trim();
                if (myPin.trim().length() < 4){
                    Toast.makeText(DashBoard.this, "Incorect pin", Toast.LENGTH_SHORT).show();
                }else{
//                    dialog.dismiss();
                    deductAmount(foodSetGetMod.getFoodPrice()+"",DashBoard.this,myPin);
                }
            }
        });


        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void deductAmount(String receivedAmount, Context context, String myPIN){

        progressDialog.show();
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("All Users").child(userID).child("Details");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String acc_number = snapshot.child("Account Number").getValue(String.class);
                    String acc_pin = snapshot.child("Card PIN").getValue(String.class);
                    if (myPIN.trim().equals(acc_pin.trim())) {
                        if (acc_number.equals(cardNumber)) {
                            String amount1 = snapshot.child("Amount").getValue(String.class);
                            String[] amount2 = amount1.split(" ");
                            int amount = Integer.parseInt(amount2[0]);
                            int availableAmount = amount;
                            String[] amount_todeduce = receivedAmount.split(" ");
                            int finalDeduction = Integer.parseInt(amount_todeduce[0]);

                            if (availableAmount >= finalDeduction) {

                                int salioFinal = availableAmount - finalDeduction;

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("All Users")
                                                .child(userID)
                                                .child("Details");

                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    // Retrieve user details from Firebase snapshot
                                                    String Amount = snapshot.child("Amount").getValue(String.class);
                                                    userRef.child("Amount").setValue(salioFinal + " TZS").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                        }
                                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(context, "Failed due to " + e, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                } else {

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                // Handle error

                                            }
                                        });
                                    }
                                }, 3000);


                            } else {


                                Toast.makeText(context, amount + "", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(context, "Invalid Card Number", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context, "Incorrect PIN!", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Image selected from file manager
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            reg_profile.setImageURI(imageUri);
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            // Image captured from camera
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            reg_profile.setImageBitmap(photo);

            // Convert Bitmap to Uri
            imageUri = getImageUri(DashBoard.this, photo);

        }else if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (resultCode == RESULT_OK && data != null) {
                // Scanning was successful, handle the result
                String scannedData = data.getStringExtra("SCAN_RESULT");
                // Handle the scanned QR code data
                Log.d("QRScannerActivity", "Scanned QR Code: " + scannedData);
                Toast.makeText(DashBoard.this, "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh: " + scannedData, Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                // Scanning was canceled by the user
                Toast.makeText(DashBoard.this, "QR code scanning canceled", Toast.LENGTH_SHORT).show();
                Log.d("QRScannerActivity", "QR code scanning canceled");
            } else {
                // Other cases where scanning failed
                Toast.makeText(DashBoard.this, "QR code scanning failed", Toast.LENGTH_SHORT).show();
                Log.d("QRScannerActivity", "QR code scanning failed");
            }
        }else {
            Log.d("mmm","failure"+requestCode);
        }
    }
    public void changeUserMode(Context context){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.mode_control,null);
        builder.setView(view);
        AlertDialog dialog1=builder.create();

        AlertDialog.Builder builder1=new AlertDialog.Builder(context);
        LayoutInflater inflater2=LayoutInflater.from(context);
        View view2=inflater2.inflate(R.layout.staff_login,null);
        builder1.setView(view2);
        AlertDialog dialog2=builder1.create();

        LinearLayout staffmode=view.findViewById(R.id.staffMode);
        ImageView stafficon=view.findViewById(R.id.staffDot);
        TextView stafft=view.findViewById(R.id.staffText);
        if (modeController.equals("normal")){
//            Glide.with(context)
//                    .load(R.drawable.orange_dot)
//                    .into(normalicon);
//            Glide.with(context)
//                    .load(R.drawable.white_dot)
//                    .into(stafficon);
            stafft.setText("Switch to staff mode");
            dialog1.show();
        }else{
//            Glide.with(context)
//                    .load(R.drawable.orange_dot)
//                    .into(stafficon);
//            Glide.with(context)
//                    .load(R.drawable.white_dot)
//                    .into(normalicon);
            stafft.setText("Switch to normal mode");
            dialog1.show();
        }
//        dialog1.show();
        staffmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                Toast.makeText(DashBoard.this, modeController+"", Toast.LENGTH_SHORT).show();
                if (modeController.equals("normal")){
                    dialog2.setCancelable(false);
                    dialog1.dismiss();
                    ImageView cancel=view2.findViewById(R.id.cancel_dialogue);
                    Button signIn=view2.findViewById(R.id.btn_staffLogin);
                    signIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            modeController="staff";
                            recyclerView.setVisibility(View.GONE);
                            recyclerViewStaff.setVisibility(View.VISIBLE);
                            menuCategory.setVisibility(View.VISIBLE);
                            navigationLayout.setVisibility(View.VISIBLE);
                            dialog2.dismiss();
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            modeController="normal";
                            dialog2.dismiss();
                        }
                    });

                    dialog2.show();
                }else{
                    modeController="normal";
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerViewStaff.setVisibility(View.GONE);
                    menuCategory.setVisibility(View.GONE);
                    navigationLayout.setVisibility(View.GONE);
                    dialog1.dismiss();
                }


            }
        });

        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params=dialog1.getWindow().getAttributes();
        params.gravity= Gravity.TOP|Gravity.END;
        params.x=100;
        params.y=200;
        dialog1.getWindow().setAttributes(params);

    }
    public void updateMenu(FoodSetGetStaff foodSetGetStaff){
        AlertDialog.Builder builder3=new AlertDialog.Builder(DashBoard.this);
        LayoutInflater inflater=LayoutInflater.from(DashBoard.this);
        View view=inflater.inflate(R.layout.staff_update_menu,null);
        builder3.setView(view);
        AlertDialog dialog3=builder3.create();
        dialog3.show();
        TextView food_name;
        TextView food_price;
        TextView food_status;
        ImageView foodPic;
        TextView soldCount;
        food_name=view.findViewById(R.id.fc_foodName);
        food_price = view.findViewById(R.id.fc_foodPrice);
        food_status = view.findViewById(R.id.fc_foodStatus);
        foodPic=view.findViewById(R.id.fc_foodImage);
        soldCount=view.findViewById(R.id.fc_soldAmount);
        ImageView cancel=view.findViewById(R.id.cancel_dialogue);

        food_name.setText(foodSetGetStaff.getFoodName());
        food_price.setText(foodSetGetStaff.getFoodPrice());
        food_status.setText(foodSetGetStaff.getFoodStatus());
        soldCount.setText(foodSetGetStaff.getSoldNumber());
        Glide.with(view.getContext())
                .load(foodSetGetStaff.getItemImage())
                .into(foodPic);

        LinearLayout normalmode=view.findViewById(R.id.normalMode);
        LinearLayout staffmode=view.findViewById(R.id.staffMode);
        ImageView normalicon=view.findViewById(R.id.normalDot);
        ImageView stafficon=view.findViewById(R.id.staffDot);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog3.dismiss();
            }
        });
        dialog3.setCancelable(false);
        normalmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(DashBoard.this)
                        .load(R.drawable.orange_dot)
                        .into(normalicon);
                Glide.with(DashBoard.this)
                        .load(R.drawable.white_dot)
                        .into(stafficon);
            }
        });
        staffmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(DashBoard.this)
                        .load(R.drawable.orange_dot)
                        .into(stafficon);
                Glide.with(DashBoard.this)
                        .load(R.drawable.white_dot)
                        .into(normalicon);
            }
                });
    }
    public void chooseFromFileManager(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void takePhoto(View view) {
        // Check if the camera permission is not granted yet
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request the camera permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, proceed with capturing image
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST);
        }


    }



    public Uri getImageUri(AppCompatActivity inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public void uploadToFirestore(View view) {

        //tu upload firebase kwanza

        if (imageUri != null) {

            handler.post(() -> {
                progressDialog = new ProgressDialog(DashBoard.this);
                progressDialog.setMessage("Loading, Please wait...Make sure you have a stable internet connection!");
                progressDialog.setCancelable(false);
                progressDialog.show();
            });

            Calendar calendar = Calendar.getInstance();
            String currentdate = DateFormat.getInstance().format(calendar.getTime());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
            String formattedTime = simpleDateFormat.format(new Date());

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child("images/" + FirebaseAuth.getInstance().getUid().toString());

            UploadTask uploadTask = imagesRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                    Toast.makeText(DashBoard.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                    // Get the download URL
                    imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Image download URL retrieved
                            String imageUrl = uri.toString();
                            DatabaseReference databaseReferenceUpld = FirebaseDatabase.getInstance().getReference().child("All Users")
                                    .child(firebaseAuth.getUid().toString())
                                    .child("Details");
                            databaseReferenceUpld.child("profilePic").setValue(imageUrl);


                            // Save the image URL to Firestore
                            saveImageUrlToFirestore(imageUrl);
                        }
                    });
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Image upload failed
                    progressDialog.dismiss();
                    Toast.makeText(DashBoard.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        // Add code to save imageUrl to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // For example, you can create a collection named "images" and add imageUrl as a document field
        // You can also add more fields like timestamp, user ID, etc.
        // Replace "collectionName" with your actual collection name
        db.collection("images")
                .add(new ImageModel(imageUrl))
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(DashBoard.this, "Image URL saved to Firestore", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        startActivity(new Intent(DashBoard.this, DashBoard.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DashBoard.this, "Error saving image URL to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


//    public void couponHistory(Context context) {
//
//
//        historyAdapter = new HistoryAdapter(new ArrayList<>());
//        myHistoryRecyclerView.setAdapter(historyAdapter);
//
//        DatabaseReference couponHistoryRef = FirebaseDatabase.getInstance().getReference()
//                .child("Coupons")
//                .child(FirebaseAuth.getInstance().getUid());
//
//        couponHistoryRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<HistorySetGet> historyList = new ArrayList<>();
//                int totalSpent=0;
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    String menuName = dataSnapshot.child("Menu Name").getValue(String.class);
//                    String menuDate = dataSnapshot.child("Menu Time").getValue(String.class);
//                    String menuPrice = dataSnapshot.child("Menu Price").getValue(String.class);
//                    String menuReference = dataSnapshot.getKey().toString();
//                    String menuStatus = dataSnapshot.child("Status").getValue(String.class);
//                    String menuServetime = dataSnapshot.child("Served Time").getValue(String.class);
//
//                    if (menuPrice !=null){
//                        String[] amount=menuPrice.split(" ");
//                        int actualAmount=Integer.parseInt(amount[0]);
//                        totalSpent=totalSpent+actualAmount;
//                    }
//
//                    HistorySetGet historySetGet = new HistorySetGet(menuName, menuPrice, menuReference, menuDate,menuStatus,menuServetime);
//                    historyList.add(historySetGet);
//
//
//                }
//                Collections.reverse(historyList); // Reverse the list after updating
//                historyAdapter.updateData(historyList);
//                DatabaseReference userDb=FirebaseDatabase.getInstance().getReference().child("All Users")
//                        .child(FirebaseAuth.getInstance().getUid())
//                        .child("Details");
//                userDb.child("Used Amount").setValue("TZS "+totalSpent);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle onCancelled event if needed
//            }
//        });
//
//        historyAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position, HistorySetGet historySetGet) {
//                if (historySetGet.getCoupon_status().equals("pending")){
//                    AlertDialog.Builder builder=new AlertDialog.Builder(DashBoard.this);
//                    View popupView = LayoutInflater.from(DashBoard.this).inflate(R.layout.coupon_with_qrcode, null);
//                    builder.setView(popupView);
//
//
//                    TextView couponID=popupView.findViewById(R.id.cwq_couponID);
//                    TextView dismissbtn=popupView.findViewById(R.id.cwq_dismissbtn);
//                    ImageView qrcodeImage=popupView.findViewById(R.id.cwq_qrCode);
//
//                    couponID.setText("ID: "+historySetGet.getCoupon_reference_Number());
//
//
//                    Bitmap qrCode=coupon.generateQRCodeBitmap(historySetGet);
//
//                    Glide.with(DashBoard.this)
//                            .load(qrCode)
//                            .into(qrcodeImage);
//
//                    dialog = builder.create();
//                    dialog.setCancelable(false);
//                    dialog.show();
//                    dismissbtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//                }else {
//                    Toast.makeText(context, historySetGet.getCoupon_status()+"!", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//    }
//    public void viewHistoryAll(){
//        // Set colors and backgrounds for buttons
//        customerNav.setTextColor(getResources().getColor(R.color.black));
//        customerNav.setBackgroundResource(R.color.white);
//        homeBtn.setTextColor(getResources().getColor(R.color.black));
//        homeBtn.setBackgroundResource(R.color.white);
//        profileBtn.setTextColor(getResources().getColor(R.color.white));
//        profileBtn.setBackgroundResource(R.drawable.time);
//        scan_qrCode.setTextColor(getResources().getColor(R.color.black));
//        scan_qrCode.setBackgroundResource(R.color.white);
//
//        // Hide other layouts and show the coupon history layout
//        dashbordinsideLayout.setVisibility(View.GONE);
//        settingsLayout.setVisibility(View.GONE);
//        feedbackLayout.setVisibility(View.GONE);
//        dashBoardlayout.setVisibility(View.GONE);
//        profileLayout.setVisibility(View.GONE);
//        myhistoryLayout.setVisibility(View.VISIBLE);
//        navigationLayout.setVisibility(View.GONE);
//
//        // Call the couponHistory method to populate the RecyclerView with coupon history
//        myHistoryRecyclerView =findViewById(R.id.recyclerviewHistory);
//        myHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(DashBoard.this));
//        couponHistory(getApplicationContext());
//        TextView totalSpent=findViewById(R.id.my_totalSpends);
//        TextView totalbalance=findViewById(R.id.mh_mybalance);
//        DatabaseReference userDb=FirebaseDatabase.getInstance().getReference().child("All Users")
//                .child(FirebaseAuth.getInstance().getUid())
//                .child("Details");
//        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String spent=snapshot.child("Used Amount").getValue(String.class);
//                String balance=snapshot.child("Amount").getValue(String.class);
//                totalSpent.setText(spent+".00");
//                totalbalance.setText(balance+"");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }


}