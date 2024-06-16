package Dashboard;

import static Coupon.CouponGenerator.uniqueID;

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
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;

import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.nfc.tech.Ndef;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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

import android.nfc.Tag;
import Adapters.FoodAdapter;
import Adapters.FoodAdapterStaff;
import Adapters.FoodSetGet;
import Adapters.FoodSetGetStaff;
import Adapters.HistoryAdapter;
import Adapters.HistorySetGet;
import Adapters.ImageModel;
import Coupon.CouponGenerator;
import Coupon.CouponValidation;
import Coupon.NfcUtils;
import Coupon.QRCode.QRScannerActivity;
import NFC.NFCReader;
import Others.BluetoothPrinter;
import Others.OurTime;
import Printing.MainActivity;
import Printing.PrintBluetooth;

import android.content.IntentFilter;

public class DashBoard extends AppCompatActivity implements NFCReader.NFCListener {
    private static final int REQUEST_CODE_QR_SCAN = 49374;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;
    DatePicker dobpk;
    private Tag tag;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    public static Bitmap userPhoto;
    Button next,registerCustomer;
    Spinner gender;
    private Uri imageUri;
    public static String userGender="";
    public static String staffStatusMenuUpdate="";
    public static String NFCData="";
    public static String login_staff="Incorrect information";
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
    private List<HistorySetGet> historyList = new ArrayList<>();
    public static HistoryAdapter historyAdapter;
    public static RecyclerView couponHistoryRecyclerView;
    public static RecyclerView recyclerView;
    public static RecyclerView recyclerViewStaff;
    Thread thread;
    public static AlertDialog dialog,tabledialog;
    TextView meal_clock,meal_status,backCustReg;

    public static String timeStatus="BreakFast";
    public static String cardNumber="null";
    public static String modeController="normal";
    public static String userID="null";
    public static Handler handler;
    public static ProgressDialog progressDialog;
    public static ProgressDialog progressDialog2,progressDialogNFC,progressDialogNFCReg;
    FoodAdapter adapter;
    FoodAdapterStaff adapterStaff;
//    NfcAdapter nfcAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
//    PendingIntent pendingIntent;
    public static TextView user_Name,user_Pno,ppUsername,ppUsertopphone,ppUserFname,ppUsersmallphone,ppUserLname;

    public static String scanstatus="null";
    public static String userexist="null";
    public static String fullName;
    public static String tableStatus;
    public static String user_email;
    public static String phonenumber;
    public static String userPassword;
    public static String user_dob;
    private ImageView imageView;
    ImageView switchMode,homeBtn,scan_qrCode,customerNav,reg_profile,historyNav;
    public static FoodSetGet foodSetGetMod=new FoodSetGet("","","","","");
    LinearLayout dashBoardlayout,settingsLayout,feedbackLayout,dashbordinsideLayout,profileLayout,customerReg1,customerReg2,couponsboughtlayout;
    public static LinearLayout navigationLayout;
    TextView tableNumber,menu_textv,scan_textv,customer_textv,dob,history_tv;
    ProgressBar progressBar;
    public static String accountNumber="";
    public static String accountUserID="";
    public static String dateOnly="";
    EditText searchEditText,fName,confPass,pass,pinNumConf,pinNumber,userEmail,pNumber,lName;;
    public static String official_staffEmail="";
    Button breakfast,dinner,lunch;
    public static Context myContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.dtcsstaff.R.layout.activity_dash_board);
        OurTime.init(getApplicationContext());
        progressBar=findViewById(R.id.progress_dashboard);

        myContext=DashBoard.this;


        tableNumber=findViewById(R.id.table_number);
        SharedPreferences sharedPreferences=getSharedPreferences("table_status",MODE_PRIVATE);
        tableStatus=sharedPreferences.getString("table_number",null);
        if (tableStatus==null){

            tableNumber.setText("Welcome");
        }else{
            tableNumber.setText("Welcome");
        }
        Calendar calendar = Calendar.getInstance();
        String currentdate = DateFormat.getInstance().format(calendar.getTime());
        String[] dateSeparation=currentdate.split(" ");
        String dateOnlyFull=dateSeparation[0]+"";
        String[] tarehe=dateOnlyFull.split("/");
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Adding 1 because January is represented as 0
        int year = calendar.get(Calendar.YEAR);
        dateOnly=day+"-"+month+"-"+year;

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);

        // Create an IntentFilter array to handle the NFC tag discovered intents.
        IntentFilter ndefIntentFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefIntentFilter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        intentFiltersArray = new IntentFilter[]{ndefIntentFilter};

        // Create a tech list array to handle the Ndef tech.
        techListsArray = new String[][]{{Ndef.class.getName()}};



        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        firebaseAuth= FirebaseAuth.getInstance();
        nfcReader = new NFCReader(this, this);
        switchMode=findViewById(R.id.db_mode_switch);
        menu_textv=findViewById(R.id.menu_tv);
        history_tv=findViewById(R.id.history_tv);
        scan_textv=findViewById(R.id.scan_tv);
        customer_textv=findViewById(R.id.customer_tv);
        next=(Button) findViewById(R.id.btnNext);
        registerCustomer=(Button) findViewById(R.id.registerCustomer);
        gender=(Spinner)findViewById(R.id.gendersp);
        dobpk=(DatePicker)findViewById(R.id.dobPicker);
        dob=(TextView) findViewById(R.id.dobEt);
        reg_profile=findViewById(R.id.rp_previewImage);
        fName=findViewById(R.id.rp_firstName);
        lName=findViewById(R.id.rp_lastName);
        pNumber=findViewById(R.id.rp_phoneNumber);
        userEmail=findViewById(R.id.rp_email);
        EditText cardNumber=findViewById(R.id.rp_cardNumber);
        pinNumber=findViewById(R.id.rp_pinNumber);
        pinNumConf=findViewById(R.id.rp_pinNumberConf);
        pass=findViewById(R.id.rp_password);
        confPass=findViewById(R.id.rp_confirmPassword);
        TextView dateofBirth=findViewById(R.id.dobEt);

        searchEditText = findViewById(R.id.searchbar);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called when the text is changed
                String query = s.toString().trim();
                searchMenu(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text is changed
            }
        });

        handler=new Handler(Looper.getMainLooper());
        ImageView topProfilePic=findViewById(R.id.db_topProfilepic);

        recyclerView=(RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        adapter=new FoodAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);




        recyclerViewStaff=(RecyclerView) findViewById(R.id.recyclerviewStaff);
        recyclerViewStaff.setLayoutManager(new LinearLayoutManager(DashBoard.this));
        adapterStaff=new FoodAdapterStaff(getApplicationContext(),new ArrayList<>());
        recyclerViewStaff.setAdapter(adapterStaff);

        couponHistoryRecyclerView=(RecyclerView) findViewById(R.id.recyclerview_couponcard);
        couponHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(DashBoard.this));
        historyAdapter=new HistoryAdapter(getApplicationContext(),new ArrayList<>());
        couponHistoryRecyclerView.setAdapter(historyAdapter);

        navigationLayout = (LinearLayout) findViewById(R.id.navigationLayout);
        String intentReceived=getIntent().getStringExtra("stat")+"";
        if (intentReceived.equals("cancel")){

            recyclerView.setVisibility(View.GONE);
            recyclerViewStaff.setVisibility(View.VISIBLE);
            navigationLayout.setVisibility(View.VISIBLE);
        }else{

        }

        meal_clock=(TextView) findViewById(R.id.clocktv);
        meal_status=(TextView) findViewById(R.id.mealStatustv);
       breakfast=(Button)findViewById(R.id.breakfastbtn);
       lunch=(Button)findViewById(R.id.lunchbtn);
       dinner=(Button)findViewById(R.id.dinnerbtn);
       switchMode.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               changeUserMode(DashBoard.this);
           }
       });


        homeBtn =  findViewById(R.id.homeBtn);
        scan_qrCode =  findViewById(R.id.scan_qrCode);
        customerNav =  findViewById(R.id.customerNav);
        historyNav =  findViewById(R.id.historyrNav);
        dashBoardlayout = (LinearLayout) findViewById(R.id.dashBoardLayout);
        settingsLayout = (LinearLayout) findViewById(R.id.settingsLayout);
        feedbackLayout = (LinearLayout) findViewById(R.id.feedbackLayout);
        dashbordinsideLayout = (LinearLayout) findViewById(R.id.dashbordInsideLayout);
        profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
        customerReg1 = (LinearLayout) findViewById(R.id.ll_customerReg1);
        customerReg2 = (LinearLayout) findViewById(R.id.ll_customerReg2);
        couponsboughtlayout = (LinearLayout) findViewById(R.id.ll_coupons_history);
        backCustReg=findViewById(R.id.customerBack);


        handler.post(() -> {
            progressDialog = new ProgressDialog(DashBoard.this);
            progressDialog.setMessage("Loading, Please wait...Make sure you have a stable internet connection!");
            progressDialog.setCancelable(false);
        });
        handler.post(() -> {
            progressDialog2 = new ProgressDialog(DashBoard.this);
            progressDialog2.setMessage("Loading, Please wait...Make sure you have a stable internet connection!");
            progressDialog2.setCancelable(false);
        });
        handler.post(() -> {
            progressDialogNFC = new ProgressDialog(DashBoard.this);
            progressDialogNFC.setMessage("place your card on the back side of this phone for scanning!!");
            progressDialogNFC.setCancelable(true);
        });
        handler.post(() -> {
            progressDialogNFCReg = new ProgressDialog(DashBoard.this);
            progressDialogNFCReg.setMessage("place your card on the back side of this phone for scanning!!");
            progressDialogNFCReg.setCancelable(false);
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
                    fName.requestFocus();
                } else if (last_name.isEmpty()) {
                    lName.setError("Field required");
                    lName.requestFocus();
                }else if (phone_number.isEmpty()) {
                    pNumber.setError("Field required");
                    pNumber.requestFocus();
                }else if (phone_number.trim().length()!=10) {
                    pNumber.setError("10 numbers are required");
                    pNumber.requestFocus();
                }else if (email.isEmpty()) {
                    userEmail.setError("Field required");
                    userEmail.requestFocus();
                }else if (!pat.matcher(email).matches()) {
                    userEmail.setError("Please Enter a valid Email");
                    userEmail.requestFocus();
                    return;
                }else if (date_birth.isEmpty()){
                    dateofBirth.setError("Field required");
                    dateofBirth.requestFocus();
                }else if (passwd.isEmpty()) {
                    pass.setText("Field required");
                    pass.requestFocus();
                } else if (passwd.length()<6) {
                    pass.setError("Must contain atleast 6 characters");
                    pass.requestFocus();
                } else if (confp.isEmpty()) {
                    confPass.setError("Field required");
                    confPass.requestFocus();
                } else if (!passwd.equals(confp)) {
                    confPass.setError("Password does not match");
                    confPass.requestFocus();
                }else{
                    user_dob=date_birth;
                    fullName=first_name+" "+last_name;
                    phonenumber=phone_number;
                    user_email=email;
                    userPassword=passwd;
                    customerReg1.setVisibility(View.GONE);
                    customerReg2.setVisibility(View.VISIBLE);

                }
            }
        });
        backCustReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerReg1.setVisibility(View.VISIBLE);
                customerReg2.setVisibility(View.GONE);
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
                } else if (cardN.length() <15) {
                    cardNumber.setError("15 digits required!");
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
                    hashMap.put("Card_PIN",cardPin);
                    hashMap.put("Account_Number",cardN);
                    hashMap.put("Amount","0 TZs");
                    hashMap.put("Deposit","0 TZs");
                    hashMap.put("Card_Status","Active");

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
//                                                            Toast.makeText(DashBoard.this, "Successful", Toast.LENGTH_SHORT).show();
                                                            Toast.makeText(DashBoard.this, "User Registered!", Toast.LENGTH_LONG).show();


                                                            DatabaseReference regref=FirebaseDatabase.getInstance().getReference().child("All Users");
                                                            DatabaseReference oneUser=regref.child("Details");
                                                            regref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()){
                                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                                            String regemail=dataSnapshot.child("Details").child("username").getValue(String.class);
                                                                            if (regemail.equals(user_email)){
                                                                                accountUserID=dataSnapshot.getKey().toString();
                                                                                accountNumber=cardN;
//                                                                                Toast.makeText(DashBoard.this, accountUserID+"", Toast.LENGTH_SHORT).show();

                                                                                userexist="yes";


                                                                            }
                                                                        }
                                                                        if (userexist.equals("yes")){
                                                                            uploadToFirestore(v);
                                                                        }

                                                                    }else{
                                                                        progressDialog.dismiss();
//                                                                        Toast.makeText(DashBoard.this, "noooooo", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });



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


        TextView historyView = (TextView) findViewById(R.id.historyTv);

        historyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                viewHistoryAll();

            }
        });


        historyNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan_textv.setTextColor(getResources().getColor(R.color.white));
                history_tv.setTextColor(getResources().getColor(R.color.white));
                historyNav.setBackgroundResource(R.drawable.time);
                customerNav.setBackgroundResource(R.drawable.time1);
                homeBtn.setBackgroundResource(R.drawable.time1);
                customer_textv.setTextColor(getResources().getColor(R.color.white));
                dashbordinsideLayout.setVisibility(View.GONE);
                settingsLayout.setVisibility(View.GONE);
                feedbackLayout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.GONE);
                customerReg1.setVisibility(View.GONE);
                customerReg2.setVisibility(View.GONE);
                couponsboughtlayout.setVisibility(View.VISIBLE);

                TextView nocoupon=findViewById(R.id.nocouponsfortoday);
                ProgressBar histbar=findViewById(R.id.progress_dashboard_coupons);
                histbar.setVisibility(View.VISIBLE);


                Calendar calendar = Calendar.getInstance();
                String currentdate = DateFormat.getInstance().format(calendar.getTime());
                String[] dateSeparation=currentdate.split(" ");
                String dateOnlyFull=dateSeparation[0]+"";
                String[] tarehe=dateOnlyFull.split("/");
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH) + 1; // Adding 1 because January is represented as 0
                int year = calendar.get(Calendar.YEAR);
                String dateOnly=day+"-"+month+"-"+year;
                DatabaseReference cardCouponRef = FirebaseDatabase.getInstance().getReference()
                        .child("Card Coupons")
                        .child(dateOnly);

                cardCouponRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            historyList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String menuName = dataSnapshot.child("Menu Name").getValue(String.class);
                                String menuDate = dataSnapshot.child("Menu Time").getValue(String.class);
                                String menuPrice = dataSnapshot.child("Menu Price").getValue(String.class);
                                String menuReference = dataSnapshot.getKey().toString();
                                String menuStatus = dataSnapshot.child("Status").getValue(String.class);
                                String menuServetime = dataSnapshot.child("Served Time").getValue(String.class);
                                String couponNumber = dataSnapshot.child("Coupon Number").getValue(String.class);
                                String user_id = dataSnapshot.child("UserID").getValue(String.class);


                                HistorySetGet historySetGet = new HistorySetGet(menuName, menuPrice, menuReference, menuDate,menuStatus,menuServetime,couponNumber,user_id+"");
                                historyList.add(historySetGet);


                            }
                            histbar.setVisibility(View.GONE);
                            nocoupon.setVisibility(View.GONE);
                            Collections.reverse(historyList); // Reverse the list after updating
                            historyAdapter.updateData(historyList);
                        }else{
                            histbar.setVisibility(View.GONE);
                            nocoupon.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        customerNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scan_textv.setTextColor(getResources().getColor(R.color.white));
                customerNav.setBackgroundResource(R.drawable.time);
                homeBtn.setBackgroundResource(R.drawable.time1);
                historyNav.setBackgroundResource(R.drawable.time1);
                customer_textv.setTextColor(getResources().getColor(R.color.white));
                history_tv.setTextColor(getResources().getColor(R.color.white));
               dashbordinsideLayout.setVisibility(View.GONE);
               settingsLayout.setVisibility(View.GONE);
               feedbackLayout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.GONE);
                customerReg1.setVisibility(View.VISIBLE);
                customerReg2.setVisibility(View.GONE);
                couponsboughtlayout.setVisibility(View.GONE);
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

        customerReg1.setVisibility(View.GONE);
        customerReg2.setVisibility(View.GONE);
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
        customerNav.setBackgroundResource(R.drawable.time1);
        historyNav.setBackgroundResource(R.drawable.time1);
        menu_textv.setTextColor(getResources().getColor(R.color.white));
        homeBtn.setBackgroundResource(R.drawable.time);
        scan_qrCode.setBackgroundResource(R.color.white);
        dashBoardlayout.setVisibility(View.VISIBLE);
        settingsLayout.setVisibility(View.GONE);
        feedbackLayout.setVisibility(View.GONE);
        dashbordinsideLayout.setVisibility(View.VISIBLE);
        profileLayout.setVisibility(View.GONE);
        navigationLayout.setVisibility(View.VISIBLE);
        customerReg1.setVisibility(View.GONE);
        customerReg2.setVisibility(View.GONE);
        couponsboughtlayout.setVisibility(View.GONE);

    }
});

       timeStatus=OurTime.getTimeStatus();
        if(timeStatus!=null)
        {
            switch (timeStatus)
            {
                case "BreakFast":
                    staffStatusMenuUpdate="BreakFast";
                    breakfast.setBackgroundResource(R.drawable.foodback);
                    breakfast.setTextColor(getResources().getColor(R.color.white));
                    lunch.setBackgroundResource(R.drawable.viewbalance);
                    lunch.setTextColor(getResources().getColor(R.color.black));
                    dinner.setBackgroundResource(R.drawable.viewbalance);
                    dinner.setTextColor(getResources().getColor(R.color.black));
                    progressBar.setVisibility(View.VISIBLE);
                    foodList.clear();
                    foodListStaff.clear();
                    DatabaseReference breakfastRef = FirebaseDatabase.getInstance().getReference()
                            .child("MENUS")
                            .child("Breakfast");

                    breakfastRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            foodList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String menuPrice = dataSnapshot.child("price").getValue(String.class);
                                String menuName = dataSnapshot.child("foodName").getValue(String.class);
                                String menuUrl = dataSnapshot.child("menuImage").getValue(String.class);
                                String menustatus = dataSnapshot.child("statusMode").getValue(String.class);
                                String snapID=dataSnapshot.getKey().toString();


                                DatabaseReference breakfastRefsold = FirebaseDatabase.getInstance().getReference().child("Coupons")
                                        .child("Coupons Used")
                                        .child(dateOnly).child(menuName);
                                breakfastRefsold.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String soldIdadi = snapshot.getValue(String.class);
                                            String[] sep = soldIdadi.split(" ");
                                            String idadi = sep[0];

                                            // Check if the menu item already exists in the list
                                            boolean found = false;
                                            for (FoodSetGetStaff item : foodListStaff) {
                                                if (item.getFoodName().equals(menuName)) {
                                                    // Update the existing item
                                                    item.setFoodPrice(menuPrice + " TZS");
                                                    item.setFoodStatus(menustatus + "");
                                                    item.setItemImage(menuUrl);
                                                    item.setSoldNumber(idadi);
                                                    found = true;
                                                    break;
                                                }
                                            }

                                            // If the menu item is not found, add it to the list
                                            if (!found) {
                                                FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                                FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, idadi,snapID);
                                                foodList.add(foodSetGet);
                                                foodListStaff.add(foodSetGetStaff);
                                            }
                                        }else{
                                            FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                            FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, "0",snapID);
                                            foodList.add(foodSetGet);
                                            foodListStaff.add(foodSetGetStaff);
                                        }
                                        adapter.updateData(foodList);
                                        adapterStaff.updateData(foodListStaff);
                                        adapterStaff.notifyDataSetChanged();
                                        Collections.reverse(foodList);
                                        Collections.reverse(foodListStaff);
                                        adapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled event if needed
                        }
                    });

                    break;

                case "Lunch":
                    staffStatusMenuUpdate="Lunch";
                    breakfast.setBackgroundResource(R.drawable.viewbalance);
                    breakfast.setTextColor(getResources().getColor(R.color.black));
                    lunch.setBackgroundResource(R.drawable.foodback);
                    lunch.setTextColor(getResources().getColor(R.color.white));
                    dinner.setBackgroundResource(R.drawable.viewbalance);
                    dinner.setTextColor(getResources().getColor(R.color.black));
                    progressBar.setVisibility(View.VISIBLE);
                    foodList.clear();
                    foodListStaff.clear();
                    DatabaseReference lunchRef = FirebaseDatabase.getInstance().getReference()
                            .child("MENUS")
                            .child("Lunch");

                    lunchRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            foodList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String menuPrice = dataSnapshot.child("price").getValue(String.class);
                                String menuName = dataSnapshot.child("foodName").getValue(String.class);
                                String menuUrl = dataSnapshot.child("menuImage").getValue(String.class);
                                String menustatus = dataSnapshot.child("statusMode").getValue(String.class);
                                String snapID=dataSnapshot.getKey().toString();

                                DatabaseReference lunchRefsold = FirebaseDatabase.getInstance().getReference().child("Coupons")
                                        .child("Coupons Used")
                                        .child(dateOnly).child(menuName);
                                lunchRefsold.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String soldIdadi = snapshot.getValue(String.class);
                                            String[] sep = soldIdadi.split(" ");
                                            String idadi = sep[0];

                                            // Check if the menu item already exists in the list
                                            boolean found = false;
                                            for (FoodSetGetStaff item : foodListStaff) {
                                                if (item.getFoodName().equals(menuName)) {
                                                    // Update the existing item
                                                    item.setFoodPrice(menuPrice + " TZS");
                                                    item.setFoodStatus(menustatus + "");
                                                    item.setItemImage(menuUrl);
                                                    item.setSoldNumber(idadi);
                                                    found = true;
                                                    break;
                                                }
                                            }

                                            // If the menu item is not found, add it to the list
                                            if (!found) {
                                                FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                                FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, idadi,snapID);
                                                foodList.add(foodSetGet);
                                                foodListStaff.add(foodSetGetStaff);
                                            }
                                        }else{
                                            FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                            FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, "0",snapID);
                                            foodList.add(foodSetGet);
                                            foodListStaff.add(foodSetGetStaff);
                                        }
                                        adapter.updateData(foodList);
                                        adapterStaff.updateData(foodListStaff);
                                        adapterStaff.notifyDataSetChanged();
                                        Collections.reverse(foodList);
                                        Collections.reverse(foodListStaff);
                                        adapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled event if needed
                        }
                    });

                    break;


                case "Dinner":
                    staffStatusMenuUpdate="Dinner";
                    breakfast.setBackgroundResource(R.drawable.viewbalance);
                    breakfast.setTextColor(getResources().getColor(R.color.black));
                    lunch.setBackgroundResource(R.drawable.viewbalance);
                    lunch.setTextColor(getResources().getColor(R.color.black));
                    dinner.setBackgroundResource(R.drawable.foodback);
                    dinner.setTextColor(getResources().getColor(R.color.white));;
                    progressBar.setVisibility(View.VISIBLE);
                    foodList.clear();
                    foodListStaff.clear();
                    DatabaseReference dinnerRef = FirebaseDatabase.getInstance().getReference()
                            .child("MENUS")
                            .child("Dinner");

                    dinnerRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            foodList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String menuPrice = dataSnapshot.child("price").getValue(String.class);
                                String menuName = dataSnapshot.child("foodName").getValue(String.class);
                                String menuUrl = dataSnapshot.child("menuImage").getValue(String.class);
                                String menustatus = dataSnapshot.child("statusMode").getValue(String.class);
                                String snapID=dataSnapshot.getKey().toString();

                                DatabaseReference dinnerRefsold = FirebaseDatabase.getInstance().getReference().child("Coupons")
                                        .child("Coupons Used")
                                        .child(dateOnly).child(menuName);
                                dinnerRefsold.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String soldIdadi = snapshot.getValue(String.class);
                                            String[] sep = soldIdadi.split(" ");
                                            String idadi = sep[0];

                                            // Check if the menu item already exists in the list
                                            boolean found = false;
                                            for (FoodSetGetStaff item : foodListStaff) {
                                                if (item.getFoodName().equals(menuName)) {
                                                    // Update the existing item
                                                    item.setFoodPrice(menuPrice + " TZS");
                                                    item.setFoodStatus(menustatus + "");
                                                    item.setItemImage(menuUrl);
                                                    item.setSoldNumber(idadi);
                                                    found = true;
                                                    break;
                                                }
                                            }

                                            // If the menu item is not found, add it to the list
                                            if (!found) {
                                                FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                                FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, idadi,snapID);
                                                foodList.add(foodSetGet);
                                                foodListStaff.add(foodSetGetStaff);
                                            }
                                        }else{
                                            FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                            FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, "0",snapID);
                                            foodList.add(foodSetGet);
                                            foodListStaff.add(foodSetGetStaff);
                                        }
                                        adapter.updateData(foodList);
                                        adapterStaff.updateData(foodListStaff);
                                        adapterStaff.notifyDataSetChanged();
                                        Collections.reverse(foodList);
                                        Collections.reverse(foodListStaff);
                                        adapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled event if needed
                        }
                    });
                    break;

                default:
                    break;

            }
        }

        historyAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, HistorySetGet historySetGet) {
                if (historySetGet.getCoupon_status().equals("pending")){
                    AlertDialog.Builder builder=new AlertDialog.Builder(DashBoard.this);
                    View popupView = LayoutInflater.from(DashBoard.this).inflate(R.layout.coupon_with_qrcode, null);
                    builder.setView(popupView);
                    dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();


                    TextView couponID=popupView.findViewById(R.id.cwq_couponID);
                    TextView dismissbtn=popupView.findViewById(R.id.cwq_dismissbtn);
                    Button print=popupView.findViewById(R.id.print_button);
                    print.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            progressDialog2.show();
                            PrintBluetooth printBT = new PrintBluetooth();
                            PrintBluetooth.printer_id = "LuckP_602-R58D-UB";
//                        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.coupon_top);
                            try {
                                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                String data=", Reference Number: " + historySetGet.getCoupon_reference_Number()+
                                        ", UID: "+historySetGet.getUserID();
                                BitMatrix bitMatrix = multiFormatWriter.encode(data+"", BarcodeFormat.QR_CODE,300,300);
                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                                printBT.findBT();
                                printBT.openBT();
                                printBT.printPicture();
                                printBT.printCouponNumberMod(historySetGet.getCoupon_No()+"");
                                printBT.printQrCode(bitmap);
                                printBT.printStruk();
                                printBT.printText();
                                progressDialog2.dismiss();
//                    printBT.closeBT();
                            }catch (IOException ex){ex.printStackTrace();} catch (
                                    WriterException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    couponID.setText("ID: "+historySetGet.getCoupon_reference_Number());



                    dismissbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }else {
                    Toast.makeText(myContext, historySetGet.getCoupon_status()+"!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        adapterStaff.setOnItemClickListener(new FoodAdapterStaff.OnItemClickListener() {
            @Override
            public void onItemClick(int position, FoodSetGetStaff foodSetGetStaffStaff) {
                updateMenu(foodSetGetStaffStaff);
            }
        });

        adapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, FoodSetGet foodSetGet) {
                String text=foodSetGet.getMenuAvailability()+"";
                if (text.equals("Available")){
                    alertdialogBuilder(foodSetGet);
                }else{
                    Toast.makeText(DashBoard.this, foodSetGet.getFoodName()+" not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
       breakfast.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               staffStatusMenuUpdate="Breakfast";
               breakfast.setBackgroundResource(R.drawable.foodback);
               breakfast.setTextColor(getResources().getColor(R.color.white));
               lunch.setBackgroundResource(R.drawable.viewbalance);
               lunch.setTextColor(getResources().getColor(R.color.black));
               dinner.setBackgroundResource(R.drawable.viewbalance);
               dinner.setTextColor(getResources().getColor(R.color.black));

               progressBar.setVisibility(View.VISIBLE);
               foodList.clear();
               foodListStaff.clear();
               DatabaseReference breakfastRef = FirebaseDatabase.getInstance().getReference()
                       .child("MENUS")
                       .child("Breakfast");

               breakfastRef.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       foodList.clear();
                       for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                           String menuPrice = dataSnapshot.child("price").getValue(String.class);
                           String menuName = dataSnapshot.child("foodName").getValue(String.class);
                           String menuUrl = dataSnapshot.child("menuImage").getValue(String.class);
                           String menustatus = dataSnapshot.child("statusMode").getValue(String.class);
                           String snapID=dataSnapshot.getKey().toString();


                           DatabaseReference breakfastRefsold = FirebaseDatabase.getInstance().getReference().child("Coupons")
                                   .child("Coupons Used")
                                   .child(dateOnly).child(menuName);
                           breakfastRefsold.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   if (snapshot.exists()) {
                                       String soldIdadi = snapshot.getValue(String.class);
                                       String[] sep = soldIdadi.split(" ");
                                       String idadi = sep[0];

                                       // Check if the menu item already exists in the list
                                       boolean found = false;
                                       for (FoodSetGetStaff item : foodListStaff) {
                                           if (item.getFoodName().equals(menuName)) {
                                               // Update the existing item
                                               item.setFoodPrice(menuPrice + " TZS");
                                               item.setFoodStatus(menustatus + "");
                                               item.setItemImage(menuUrl);
                                               item.setSoldNumber(idadi);
                                               found = true;
                                               break;
                                           }
                                       }

                                       // If the menu item is not found, add it to the list
                                       if (!found) {
                                           FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                           FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, idadi,snapID);
                                           foodList.add(foodSetGet);
                                           foodListStaff.add(foodSetGetStaff);
                                       }
                                   }else{
                                       FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                       FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, "0",snapID);
                                       foodList.add(foodSetGet);
                                       foodListStaff.add(foodSetGetStaff);
                                   }
                                   adapter.updateData(foodList);
                                   adapterStaff.updateData(foodListStaff);
                                   adapterStaff.notifyDataSetChanged();
                                   Collections.reverse(foodList);
                                   Collections.reverse(foodListStaff);
                                   adapter.notifyDataSetChanged();
                                   progressBar.setVisibility(View.GONE);
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {

                               }
                           });

                       }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {
                       // Handle onCancelled event if needed
                   }
               });
           }
           });
       lunch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               staffStatusMenuUpdate="Lunch";
               breakfast.setBackgroundResource(R.drawable.viewbalance);
               breakfast.setTextColor(getResources().getColor(R.color.black));
               lunch.setBackgroundResource(R.drawable.foodback);
               lunch.setTextColor(getResources().getColor(R.color.white));
               dinner.setBackgroundResource(R.drawable.viewbalance);
               dinner.setTextColor(getResources().getColor(R.color.black));;
               progressBar.setVisibility(View.VISIBLE);
               foodList.clear();
               foodListStaff.clear();

               DatabaseReference lunchRef = FirebaseDatabase.getInstance().getReference()
                       .child("MENUS")
                       .child("Lunch");

               lunchRef.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       foodList.clear();
                       for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                           String menuPrice = dataSnapshot.child("price").getValue(String.class);
                           String menuName = dataSnapshot.child("foodName").getValue(String.class);
                           String menuUrl = dataSnapshot.child("menuImage").getValue(String.class);
                           String menustatus = dataSnapshot.child("statusMode").getValue(String.class);
                           String snapID=dataSnapshot.getKey().toString();

                           DatabaseReference lunchRefsold = FirebaseDatabase.getInstance().getReference().child("Coupons")
                                   .child("Coupons Used")
                                   .child(dateOnly).child(menuName);
                           lunchRefsold.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   if (snapshot.exists()) {
                                       String soldIdadi = snapshot.getValue(String.class);
                                       String[] sep = soldIdadi.split(" ");
                                       String idadi = sep[0];

                                       // Check if the menu item already exists in the list
                                       boolean found = false;
                                       for (FoodSetGetStaff item : foodListStaff) {
                                           if (item.getFoodName().equals(menuName)) {
                                               // Update the existing item
                                               item.setFoodPrice(menuPrice + " TZS");
                                               item.setFoodStatus(menustatus + "");
                                               item.setItemImage(menuUrl);
                                               item.setSoldNumber(idadi);
                                               found = true;
                                               break;
                                           }
                                       }

                                       // If the menu item is not found, add it to the list
                                       if (!found) {
                                           FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                           FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, idadi,snapID);
                                           foodList.add(foodSetGet);
                                           foodListStaff.add(foodSetGetStaff);
                                       }
                                   }else{
                                       FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                       FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, "0",snapID);
                                       foodList.add(foodSetGet);
                                       foodListStaff.add(foodSetGetStaff);
                                   }
                                   adapter.updateData(foodList);
                                   adapterStaff.updateData(foodListStaff);
                                   adapterStaff.notifyDataSetChanged();
                                   Collections.reverse(foodList);
                                   Collections.reverse(foodListStaff);
                                   adapter.notifyDataSetChanged();
                                   progressBar.setVisibility(View.GONE);
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {

                               }
                           });
                       }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {
                       // Handle onCancelled event if needed
                   }
               });

           }
       });
       dinner.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               staffStatusMenuUpdate="Dinner";
               breakfast.setBackgroundResource(R.drawable.viewbalance);
               breakfast.setTextColor(getResources().getColor(R.color.black));
               lunch.setBackgroundResource(R.drawable.viewbalance);
               lunch.setTextColor(getResources().getColor(R.color.black));
               dinner.setBackgroundResource(R.drawable.foodback);
               dinner.setTextColor(getResources().getColor(R.color.white));;
               progressBar.setVisibility(View.VISIBLE);
               foodList.clear();
               foodListStaff.clear();

               DatabaseReference dinnerRef = FirebaseDatabase.getInstance().getReference()
                       .child("MENUS")
                       .child("Dinner");

               dinnerRef.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       foodList.clear();
                       for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                           String menuPrice = dataSnapshot.child("price").getValue(String.class);
                           String menuName = dataSnapshot.child("foodName").getValue(String.class);
                           String menuUrl = dataSnapshot.child("menuImage").getValue(String.class);
                           String menustatus = dataSnapshot.child("statusMode").getValue(String.class);
                           String snapID=dataSnapshot.getKey().toString();

                           DatabaseReference dinnerRefsold = FirebaseDatabase.getInstance().getReference().child("Coupons")
                                   .child("Coupons Used")
                                   .child(dateOnly).child(menuName);
                           dinnerRefsold.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   if (snapshot.exists()) {
                                       String soldIdadi = snapshot.getValue(String.class);
                                       String[] sep = soldIdadi.split(" ");
                                       String idadi = sep[0];

                                       // Check if the menu item already exists in the list
                                       boolean found = false;
                                       for (FoodSetGetStaff item : foodListStaff) {
                                           if (item.getFoodName().equals(menuName)) {
                                               // Update the existing item
                                               item.setFoodPrice(menuPrice + " TZS");
                                               item.setFoodStatus(menustatus + "");
                                               item.setItemImage(menuUrl);
                                               item.setSoldNumber(idadi);
                                               found = true;
                                               break;
                                           }
                                       }

                                       // If the menu item is not found, add it to the list
                                       if (!found) {
                                           FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                           FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, idadi,snapID);
                                           foodList.add(foodSetGet);
                                           foodListStaff.add(foodSetGetStaff);
                                       }
                                   }else{
                                       FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                       FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, "0",snapID);
                                       foodList.add(foodSetGet);
                                       foodListStaff.add(foodSetGetStaff);
                                   }
                                   adapter.updateData(foodList);
                                   adapterStaff.updateData(foodListStaff);
                                   adapterStaff.notifyDataSetChanged();
                                   Collections.reverse(foodList);
                                   Collections.reverse(foodListStaff);
                                   adapter.notifyDataSetChanged();
                                   progressBar.setVisibility(View.GONE);
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {

                               }
                           });
                       }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {
                       // Handle onCancelled event if needed
                   }
               });

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
                progressDialogNFC.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        scanstatus="null";
                    }
                });



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
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcReader.stopListening();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(userexist.equals("yes")) {
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (tag != null) {
                    // Call your method to write data to the tag
                    if (NFCData.isEmpty()) {

                    } else {
                        writeDataToTag(tag, NFCData);
                        userexist="null";
                    }
                    // Replace "YourDataToWriteHere" with the actual data
                } else {
                    Log.d("NFC", "Tag is null.");
                    progressDialogNFC.dismiss();
                }
            } else {
                Log.d("NFC", "Unknown intent action: " + intent.getAction());
                Toast.makeText(this, "Cannot write into this card!", Toast.LENGTH_SHORT).show();
//            progressDialogNFC.dismiss();
            }
        } else if (scanstatus.equals("scan")) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                NdefMessage[] msgs = NfcUtils.getNdefMessages(intent);
                if (msgs != null && msgs.length > 0) {
                    String tagContent = NfcUtils.parseNdefMessages(msgs);

                        // Use Handler to post the result back on the UI thread
                        onNFCScanned(tagContent);

                }else {
                    onNFCScanned("tagContent");
                }
            }else{
                onNFCScanned("not valid");
            }
        }else
        {

        }
    }



    private void writeDataToTag(Tag tag, String data) {
        if (tag != null) {
            NdefRecord record = NdefRecord.createTextRecord(null, data);
            NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{record});

            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                try {
                    ndef.connect();
                    ndef.writeNdefMessage(ndefMessage);
                    Toast.makeText(this, "Data written to NFC tag.", Toast.LENGTH_SHORT).show();
                    ndef.close();
//                    Toast.makeText(this, "successful written", Toast.LENGTH_SHORT).show();
                    NFCData="";

                    customerNav.setBackgroundResource(R.drawable.time1);
                    menu_textv.setTextColor(getResources().getColor(R.color.white));
                    homeBtn.setBackgroundResource(R.drawable.time);
                    scan_qrCode.setBackgroundResource(R.color.white);
                    dashBoardlayout.setVisibility(View.VISIBLE);
                    settingsLayout.setVisibility(View.GONE);
                    feedbackLayout.setVisibility(View.GONE);
                    dashbordinsideLayout.setVisibility(View.VISIBLE);
                    profileLayout.setVisibility(View.GONE);
                    navigationLayout.setVisibility(View.VISIBLE);
                    customerReg1.setVisibility(View.GONE);
                    customerReg2.setVisibility(View.GONE);

                    fName.setText("");
                    lName.setText("");
                    pNumber.setText("");
                    confPass.setText("");
                    pass.setText("");
                    pinNumber.setText("");
                    pinNumConf.setText("");
                    userEmail.setText("");
                    progressDialogNFCReg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error writing to NFC tag.", Toast.LENGTH_SHORT).show();
//                    progressDialogNFCReg.dismiss();
                } finally {
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, e+"", Toast.LENGTH_SHORT).show();
//                        progressDialogNFCReg.dismiss();
                    }
                }
            } else {
                Toast.makeText(this, "Tag does not support NDEF.", Toast.LENGTH_SHORT).show();
                progressDialogNFCReg.dismiss();
            }
        } else {
            Toast.makeText(this, "Tag is null.", Toast.LENGTH_SHORT).show();
            progressDialogNFCReg.dismiss();
        }
    }





    private NdefMessage createNdefMessage(String data) {
        // Create NdefRecord from the data
        NdefRecord record = NdefRecord.createTextRecord(null, data);

        // Construct NdefMessage with the NdefRecord
        return new NdefMessage(new NdefRecord[]{record});
    }


    @Override
    public void onNFCScanned(String tagContent) {
        // Handle NFC data here
        if (scanstatus.equals("null")){
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
        TextView dism=popupView.findViewById(R.id.ad_dismisSucces);


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
                    progressDialog2.show();
                    deductAmount(foodSetGetMod.getFoodPrice()+"",DashBoard.this,myPin+"");
                }
            }
        });


        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dism.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
    }

    private void deductAmount(String receivedAmount, Context context, String myPIN) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("All Users").child(userID).child("Details");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String acc_number = snapshot.child("Account_Number").getValue(String.class);
                    String acc_pin = snapshot.child("Card_PIN").getValue(String.class);
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
                                                    userRef.child("Amount").setValue(salioFinal + " TZS").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                        }
                                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            CouponGenerator.generateCoupon(context.getApplicationContext(), foodSetGetMod);



//
//                                                            // Declare the printer variable outside the onSuccess method and initialize it to null
//                                                            BluetoothPrinter printer = null;
//
//                                                            // Initialize and start Bluetooth printer discovery with callback
//                                                            Log.d("BluetoothPrinter", "Initializing Bluetooth printer and starting discovery");
//                                                            BluetoothPrinter finalPrinter = printer;
//                                                            printer = new BluetoothPrinter(context, new BluetoothPrinter.BluetoothPrinterCallback() {
//                                                                @Override
//                                                                public void onConnected() {
//                                                                    // Check if printer is not null before using it
//                                                                    if (finalPrinter != null) {
//                                                                        finalPrinter.printText("success");
//                                                                        finalPrinter.close();
//                                                                    } else {
//                                                                        Log.e("BluetoothPrinter", "Printer object is null");
//                                                                    }
//                                                                    progressDialog2.dismiss();
//                                                                }
//
//                                                                @Override
//                                                                public void onFailed(String error) {
//                                                                    Log.d("BluetoothPrinter", "Failed to connect to printer: " + error);
//                                                                    Toast.makeText(context, "Failed to connect to printer: " + error, Toast.LENGTH_SHORT).show();
//                                                                    progressDialog2.dismiss();
//                                                                }
//                                                            });
//                                                            printer.startDiscovery();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog2.dismiss();
                                                            Toast.makeText(context, "Failed due to " + e, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
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
                                Toast.makeText(context, "Insufficient balance!", Toast.LENGTH_SHORT).show();
                                progressDialog2.dismiss();
                            }
                        } else {
                            Toast.makeText(context, "Invalid Card Number", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Incorrect PIN!", Toast.LENGTH_SHORT).show();
                        progressDialog2.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
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
    public static void afterScan(){
        recyclerView.setVisibility(View.GONE);
        recyclerViewStaff.setVisibility(View.VISIBLE);
        navigationLayout.setVisibility(View.VISIBLE);
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
        LinearLayout changepass=view.findViewById(R.id.changePassword);
        LinearLayout changetablenumber=view.findViewById(R.id.updatetablenumber);
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
            changepass.setVisibility(View.GONE);
            changetablenumber.setVisibility(View.GONE);
            dialog1.show();
        }else{

            stafft.setText("Switch to normal mode");
            changepass.setVisibility(View.VISIBLE);
            changetablenumber.setVisibility(View.VISIBLE);
            changetablenumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                    AlertDialog.Builder builderpass=new AlertDialog.Builder(context);
                    LayoutInflater inflater=LayoutInflater.from(context);
                    View view=inflater.inflate(R.layout.password_update,null);
                    builderpass.setView(view);
                    AlertDialog dialogpass=builderpass.create();
                    dialogpass.show();
                    TextView passtv=view.findViewById(R.id.update_passwordtv);
                    TextView tabletv=view.findViewById(R.id.update_tabletv);
                    EditText passet=view.findViewById(R.id.update_passwordet);
                    EditText tableset=view.findViewById(R.id.update_tableet);
                    passtv.setVisibility(View.GONE);
                    passet.setVisibility(View.GONE);
                    tabletv.setVisibility(View.VISIBLE);
                    tableset.setVisibility(View.VISIBLE);
                    Button upd=view.findViewById(R.id.password_updateButton);
                    upd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String newtablenumber=tableset.getText().toString().trim();
                            if (newtablenumber.isEmpty()){
                                tableset.setError("Required");
                                tableset.requestFocus();
                            } else{
                                SharedPreferences sharedPreferences=getSharedPreferences("table_status",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("table_number","Window "+newtablenumber);
                                editor.apply();
                                tableNumber.setText("Window "+newtablenumber);
                                tableStatus="Window "+newtablenumber;
                                dialogpass.dismiss();
                            }
                        }
                    });
                }
            });
            changepass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                    AlertDialog.Builder builderpass=new AlertDialog.Builder(context);
                    LayoutInflater inflater=LayoutInflater.from(context);
                    View view=inflater.inflate(R.layout.password_update,null);
                    builderpass.setView(view);
                    AlertDialog dialogpass=builderpass.create();
                    dialogpass.show();
                    EditText passet=view.findViewById(R.id.update_passwordet);
                    Button upd=view.findViewById(R.id.password_updateButton);
                    upd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String newpassword=passet.getText().toString().trim();
                            if (newpassword.isEmpty()){
                                passet.setError("Required");
                                passet.requestFocus();
                            } else if (newpassword.length()<6) {
                                passet.setError("Too short,atleast 6 characters!");
                                passet.requestFocus();
                            }else{
                                progressDialog2.show();
                                DatabaseReference staffRefUpd = FirebaseDatabase.getInstance().getReference().child("Staff Members");
                                staffRefUpd.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                                String userEmail=dataSnapshot.child("email").getValue(String.class);
                                                String userpssw=dataSnapshot.child("password").getValue(String.class);
                                                String key=dataSnapshot.getKey();
                                                if (userEmail.trim().equals(official_staffEmail)){
                                                    staffRefUpd.child(key).child("password").setValue(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            dialogpass.dismiss();
                                                            progressDialog2.dismiss();
                                                        }
                                                    });
                                                    break;
                                                }
                                            }
                                        }else{
                                            Toast.makeText(DashBoard.this, "No registered staff!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    });
                }
            });
            dialog1.show();
        }
//        dialog1.show();
        staffmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                dialog1.dismiss();
                if (modeController.equals("normal")){
                    dialog2.setCancelable(false);
                    dialog1.dismiss();
                    ImageView cancel=view2.findViewById(R.id.cancel_dialogue);
                    Button signIn=view2.findViewById(R.id.btn_staffLogin);
                    EditText staffemail=view2.findViewById(R.id.staffUsername);
                    EditText staffpass=view2.findViewById(R.id.staff_password);
                    signIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String enteredEmail=staffemail.getText().toString().trim();
                            String passwordSt=staffpass.getText().toString().trim();
                            if (enteredEmail.isEmpty()){
                                staffemail.setError("Email required!");
                                staffemail.requestFocus();
                                return;
                            } else if (passwordSt.isEmpty()) {
                                staffpass.setError("Password required!");
                                staffpass.requestFocus();
                                return;
                            }else {
                                DatabaseReference staffRef = FirebaseDatabase.getInstance().getReference().child("Staff Members");
                                staffRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                                String userEmail=dataSnapshot.child("email").getValue(String.class);
                                                if (userEmail.trim().equals(enteredEmail)){
                                                    String key=dataSnapshot.getKey();
                                                    DatabaseReference passwdref=staffRef.child(key);
                                                    passwdref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            String userpssw=snapshot.child("password").getValue(String.class);

                                                            if (userpssw.trim().equals(passwordSt.trim())){
                                                                official_staffEmail=enteredEmail;
                                                                login_staff="success";
                                                                modeController="staff";
                                                                recyclerView.setVisibility(View.GONE);
                                                                recyclerViewStaff.setVisibility(View.VISIBLE);
                                                                navigationLayout.setVisibility(View.VISIBLE);
                                                                dialog2.dismiss();
                                                                SharedPreferences sharedPreferences=getSharedPreferences("table_status",MODE_PRIVATE);
                                                                tableStatus=sharedPreferences.getString("table_number",null);
                                                                if (tableStatus==null){

                                                                    tableNumber.setText("Welcome");
                                                                    AlertDialog.Builder builder_table=new AlertDialog.Builder(DashBoard.this);
                                                                    View popupView = LayoutInflater.from(DashBoard.this).inflate(R.layout.table_alert, null);
                                                                    builder_table.setView(popupView);
                                                                    tabledialog = builder_table.create();
                                                                    tabledialog.setCancelable(true);
                                                                    Button assign=popupView.findViewById(R.id.btn_staffLogin);
                                                                    assign.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            dialog1.dismiss();
                                                                            AlertDialog.Builder builderpass=new AlertDialog.Builder(context);
                                                                            LayoutInflater inflater=LayoutInflater.from(context);
                                                                            View view=inflater.inflate(R.layout.password_update,null);
                                                                            builderpass.setView(view);
                                                                            AlertDialog dialogpass=builderpass.create();
                                                                            dialogpass.show();
                                                                            TextView passtv=view.findViewById(R.id.update_passwordtv);
                                                                            TextView tabletv=view.findViewById(R.id.update_tabletv);
                                                                            EditText passet=view.findViewById(R.id.update_passwordet);
                                                                            EditText tableset=view.findViewById(R.id.update_tableet);
                                                                            passtv.setVisibility(View.GONE);
                                                                            passet.setVisibility(View.GONE);
                                                                            tabletv.setVisibility(View.VISIBLE);
                                                                            tableset.setVisibility(View.VISIBLE);
                                                                            Button upd=view.findViewById(R.id.password_updateButton);
                                                                            upd.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    String newtablenumber=tableset.getText().toString().trim();
                                                                                    if (newtablenumber.isEmpty()){
                                                                                        tableset.setError("Required");
                                                                                        tableset.requestFocus();
                                                                                    } else{
                                                                                        SharedPreferences sharedPreferences=getSharedPreferences("table_status",MODE_PRIVATE);
                                                                                        SharedPreferences.Editor editor=sharedPreferences.edit();
                                                                                        editor.putString("table_number","Window "+newtablenumber);
                                                                                        editor.apply();
//                                tableNumber.setText("TABLE "+newtablenumber);
                                                                                        tableStatus="Window "+newtablenumber;
                                                                                        tableNumber.setText(tableStatus);
                                                                                        dialogpass.dismiss();
                                                                                    }
                                                                                }
                                                                            });

                                                                            tabledialog.dismiss();

                                                                        }
                                                                    });
                                                                    builder_table.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                                        @Override
                                                                        public void onCancel(DialogInterface dialog) {
                                                                            Toast.makeText(DashBoard.this, "Can't operate without setting window number!", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    });
                                                                    tabledialog.show();
                                                                }else{
                                                                    tableNumber.setText(tableStatus);
                                                                }

                                                            }else{
                                                                Toast.makeText(DashBoard.this, "Incorrect information!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    break;
                                                }else{
                                                    Toast.makeText(DashBoard.this, "Incorrect information!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }else{
                                            Toast.makeText(DashBoard.this, "No registered staff!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }

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
                    tableNumber.setText("Welcome");
                    couponsboughtlayout.setVisibility(View.GONE);
                    dashbordinsideLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    customerReg2.setVisibility(View.GONE);
                    customerReg1.setVisibility(View.GONE);
                    recyclerViewStaff.setVisibility(View.GONE);
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
        // Retrieve the RadioGroup and RadioButtons from the inflated view
        RadioGroup modeRadioGroup = view.findViewById(R.id.modeRadioGroup);
        RadioButton availableRadioButton = view.findViewById(R.id.availableRadioButton);
        RadioButton finishedRadioButton = view.findViewById(R.id.finishedRadioButton);

        // Capture the foodstatus from the FoodSetGetStaff object
        String foodStatus = foodSetGetStaff.getFoodStatus();

        // Set the checked state of the corresponding RadioButton based on the foodstatus
        if (foodStatus.equals("Available")) {
            availableRadioButton.setChecked(true);
        } else if (foodStatus.equals("Finished")) {
            finishedRadioButton.setChecked(true);
        }else{
            finishedRadioButton.setChecked(true);
        }
        Button update_menu=view.findViewById(R.id.btn_staffUpdateMenu);
        update_menu.setVisibility(View.GONE);
        builder3.setView(view);
        AlertDialog dialog3 = builder3.create();
        dialog3.show();

        // Set OnClickListener for the RadioGroup to get the checked RadioButton
        modeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // Retrieve the text of the checked RadioButton
                RadioButton checkedRadioButton = view.findViewById(checkedId);
                String checkedText = checkedRadioButton.getText().toString();

                if (checkedText.equals(foodStatus)){
                    update_menu.setVisibility(View.GONE);
                }else{
                    update_menu.setVisibility(View.VISIBLE);
                    update_menu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DatabaseReference updtMenuref=FirebaseDatabase.getInstance().getReference()
                                    .child("MENUS")
                                    .child(staffStatusMenuUpdate)
                                    .child(foodSetGetStaff.getMenuID());
                            updtMenuref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        updtMenuref.child("statusMode").setValue(checkedText).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (staffStatusMenuUpdate.equals("Dinner")){
                                                    dialog3.dismiss();
                                                    breakfast.setBackgroundResource(R.drawable.viewbalance);
                                                    breakfast.setTextColor(getResources().getColor(R.color.black));
                                                    lunch.setBackgroundResource(R.drawable.viewbalance);
                                                    lunch.setTextColor(getResources().getColor(R.color.black));
                                                    dinner.setBackgroundResource(R.drawable.foodback);
                                                    dinner.setTextColor(getResources().getColor(R.color.white));

                                                } else if (staffStatusMenuUpdate.equals("Lunch")) {
                                                    dialog3.dismiss();
                                                    breakfast.setBackgroundResource(R.drawable.viewbalance);
                                                    breakfast.setTextColor(getResources().getColor(R.color.black));
                                                    lunch.setBackgroundResource(R.drawable.foodback);
                                                    lunch.setTextColor(getResources().getColor(R.color.white));
                                                    dinner.setBackgroundResource(R.drawable.viewbalance);
                                                    dinner.setTextColor(getResources().getColor(R.color.black));;
                                                }else if (staffStatusMenuUpdate.equals("Breakfast")){
                                                    dialog3.dismiss();
                                                    breakfast.setBackgroundResource(R.drawable.foodback);
                                                    breakfast.setTextColor(getResources().getColor(R.color.white));
                                                    lunch.setBackgroundResource(R.drawable.viewbalance);
                                                    lunch.setTextColor(getResources().getColor(R.color.black));
                                                    dinner.setBackgroundResource(R.drawable.viewbalance);
                                                    dinner.setTextColor(getResources().getColor(R.color.black));
                                                }
                                                foodListStaff.clear();
                                                DatabaseReference allRef = FirebaseDatabase.getInstance().getReference()
                                                        .child("MENUS")
                                                        .child(staffStatusMenuUpdate);

                                                allRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                            String menuPrice = dataSnapshot.child("price").getValue(String.class);
                                                            String menuName = dataSnapshot.child("foodName").getValue(String.class);
                                                            String menuUrl = dataSnapshot.child("menuImage").getValue(String.class);
                                                            String menustatus = dataSnapshot.child("statusMode").getValue(String.class);
                                                            String snapID=dataSnapshot.getKey().toString();

                                                            DatabaseReference dinnerRefsold = FirebaseDatabase.getInstance().getReference().child("Coupons")
                                                                    .child("Coupons Used")
                                                                    .child(dateOnly).child(menuName);
                                                            dinnerRefsold.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()) {
                                                                        String soldIdadi = snapshot.getValue(String.class);
                                                                        String[] sep = soldIdadi.split(" ");
                                                                        String idadi = sep[0];

                                                                        // Check if the menu item already exists in the list
                                                                        boolean found = false;
                                                                        for (FoodSetGetStaff item : foodListStaff) {
                                                                            if (item.getFoodName().equals(menuName)) {
                                                                                // Update the existing item
                                                                                item.setFoodPrice(menuPrice + " TZS");
                                                                                item.setFoodStatus(menustatus + "");
                                                                                item.setItemImage(menuUrl);
                                                                                item.setSoldNumber(idadi);
                                                                                found = true;
                                                                                break;
                                                                            }
                                                                        }

                                                                        // If the menu item is not found, add it to the list
                                                                        if (!found) {
                                                                            FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                                                            FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, idadi,snapID);
                                                                            foodList.add(foodSetGet);
                                                                            foodListStaff.add(foodSetGetStaff);
                                                                        }
                                                                    }else{
                                                                        FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuUrl,menustatus);
                                                                        FoodSetGetStaff foodSetGetStaff = new FoodSetGetStaff(menuPrice + " TZS", menuName, menustatus + "", menuUrl, "0",snapID);
                                                                        foodList.add(foodSetGet);
                                                                        foodListStaff.add(foodSetGetStaff);
                                                                    }
                                                                    adapter.updateData(foodList);
                                                                    adapterStaff.updateData(foodListStaff);
                                                                    adapterStaff.notifyDataSetChanged();
                                                                    Collections.reverse(foodList);
                                                                    Collections.reverse(foodListStaff);
                                                                    adapter.notifyDataSetChanged();
                                                                    progressBar.setVisibility(View.GONE);
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        // Handle onCancelled event if needed
                                                    }
                                                });


                                            }
                                        });
//                                        Toast.makeText(DashBoard.this, foodSetGetStaff.getMenuID()+"", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(DashBoard.this, foodSetGetStaff.getMenuID()+"", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }
                // Use the checked text as needed
                // For example, you can show a Toast with the checked text
//                Toast.makeText(DashBoard.this, "Selected status: " + checkedText, Toast.LENGTH_SHORT).show();
            }
        });


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


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog3.dismiss();
            }
        });
        dialog3.setCancelable(false);

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


        if (imageUri != null) {


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
                                    .child(accountUserID)
                                    .child("Details");
                            databaseReferenceUpld.child("profilePic").setValue(imageUrl);


                            // Save the image URL to Firestore
                            saveImageUrlToFirestore(imageUrl);
                        }
                    });
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
//                        Toast.makeText(DashBoard.this, "Image URL saved to Firestore", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        writedatatoNFCcard();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(DashBoard.this, "Error saving image URL to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void writedatatoNFCcard(){
        progressDialogNFCReg.show();
        String data = accountNumber+","+accountUserID;
        NFCData=data;
//        Toast.makeText(this, data+"", Toast.LENGTH_LONG).show();
//        writeDataToTag(data);
    }

    private void searchMenu(String query) {
        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference().child("MENUS");

        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                foodList.clear();
                boolean foundMatch = false;

                for (DataSnapshot mealSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot menuItemSnapshot : mealSnapshot.getChildren()) {
                        // Retrieve data from Firebase
                        String menuName = menuItemSnapshot.child("foodName").getValue(String.class);
                        String menuPrice = menuItemSnapshot.child("price").getValue(String.class);
                        String menuImage = menuItemSnapshot.child("menuImage").getValue(String.class);
                        String menuStatus = menuItemSnapshot.child("statusMode").getValue(String.class);
                        String menuID = menuItemSnapshot.getKey();

                        // Check if menu name matches the query
                        if (menuName != null && menuName.toLowerCase().contains(query.toLowerCase())) {
                            foundMatch = true;
                            foodList.clear();
                            FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuImage,menuStatus);
                            foodList.add(foodSetGet);
                        }
                    }

                }

                if (modeController.equals("staff")){
                    // Update RecyclerView with search results
                    if (foodListStaff.isEmpty()) {
                        // No matching items found
                        adapterStaff.setClickable(false);
                        foodListStaff.clear();
                        showNoMatchingItemsMessage();
                        // Make adapter unclickable
                    } else {
                        adapterStaff.updateData(foodListStaff);
                        Collections.reverse(foodListStaff);
                        adapterStaff.setClickable(true); // Make adapter clickable
                        adapterStaff.notifyDataSetChanged();
                    }
                }else{
                    // Update RecyclerView with search results
                    if (foodList.isEmpty()) {
                        // No matching items found
                        adapter.setClickable(false);
                        foodList.clear();
                        showNoMatchingItemsMessage();
                        // Make adapter unclickable
                    } else {
                        adapter.updateData(foodList);
                        Collections.reverse(foodList);
                        adapter.setClickable(true); // Make adapter clickable
                        adapter.notifyDataSetChanged();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }



    private void showNoMatchingItemsMessage() {

//        recyclerView.setVisibility(View.GONE);
        // Display a toast message indicating no matching items found
        Toast.makeText(DashBoard.this, "Item does not exist!", Toast.LENGTH_SHORT).show();
    }

    public static void aftercoupon(FoodSetGet foodSetGet){

        Calendar calendar = Calendar.getInstance();
        String currentdate = DateFormat.getInstance().format(calendar.getTime());
        String[] dateSeparation=currentdate.split(" ");
        String dateOnlyFull=dateSeparation[0]+"";
        String[] tarehe=dateOnlyFull.split("/");
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Adding 1 because January is represented as 0
        int year = calendar.get(Calendar.YEAR);
        String dateOnly=day+"-"+month+"-"+year;
        DatabaseReference cardCoupon = FirebaseDatabase.getInstance().getReference()
                .child("Card Coupons")
                .child(dateOnly)
                .child(CouponGenerator.couponRefNo);

        cardCoupon.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cardCoupon.child("Menu Name").setValue(foodSetGet.getFoodName());
                cardCoupon.child("Menu Time").setValue(currentdate+"Hrs");
                cardCoupon.child("Menu Price").setValue(foodSetGet.getFoodPrice());
                cardCoupon.child("Status").setValue("pending");
                cardCoupon.child("UserID").setValue(userID);
                cardCoupon.child("Reference Number").setValue(uniqueID);
                cardCoupon.child("Served Time").setValue("Not served");

                cardCoupon.child("Coupon Number").setValue(CouponGenerator.couponNumber).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(myContext, "success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        PrintBluetooth printBT = new PrintBluetooth();
                        PrintBluetooth.printer_id = "LuckP_602-R58D-UB";
//                        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.coupon_top);
                        try {
                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                            String data=", Reference Number: " + CouponGenerator.couponRefNo+
                                    ", UID: "+userID;
                            BitMatrix bitMatrix = multiFormatWriter.encode(data+"", BarcodeFormat.QR_CODE,300,300);
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                            printBT.findBT();
                            printBT.openBT();
                            printBT.printPicture();
                            printBT.printCouponNumber();
                            printBT.printQrCode(bitmap);
                            printBT.printStruk();
                            printBT.printText();
                            progressDialog2.dismiss();
//                    printBT.closeBT();
                        }catch (IOException ex){ex.printStackTrace();} catch (
                                WriterException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }



}