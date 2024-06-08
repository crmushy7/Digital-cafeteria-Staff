package Printing;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dtcsstaff.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import Coupon.CouponGenerator;
import Dashboard.DashBoard;

public class PrintBluetooth extends AppCompatActivity {

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    byte FONT_TYPE;

    public static String printer_id;

    public PrintBluetooth(){}

    @SuppressLint("MissingPermission")
    public void findBT() {
        System.out.println("Printer ID : "+printer_id);
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBluetoothAdapter == null) {
                Toast.makeText(this, "Device Bluetooth not supported", Toast.LENGTH_SHORT).show();
            }
            if(!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(this, "Coupon generated but not printed,see staff!", Toast.LENGTH_SHORT).show();
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
            @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(printer_id)) {
                        mmDevice = device;
                        break;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // tries to open a connection to the bluetooth printer device
    @SuppressLint("MissingPermission")
    public void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printQrCode(Bitmap qRBit) {
        try {
            PrintPic printPic1 = PrintPic.getInstance();
            printPic1.init(qRBit);
            byte[] bitmapdata2 = printPic1.printDraw();
            mmOutputStream.write(bitmapdata2);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void printPicture() {
        Bitmap qRBit = BitmapFactory.decodeResource(DashBoard.myContext.getResources(), R.drawable.coupon_top);
        try {
            PrintPic printPic1 = PrintPic.getInstance();
            printPic1.init(qRBit);
            byte[] bitmapdata2 = printPic1.printDraw();
            mmOutputStream.write(bitmapdata2);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void printText(){
        try {
            String text = "\n\n\n\n";

            mmOutputStream.write(text.getBytes());
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void printStruk(){
        Calendar calendar = Calendar.getInstance();
        String currentdate = DateFormat.getInstance().format(calendar.getTime());
        LocalDate currentDay = LocalDate.now();

        // Get the day of the week for the current date
        String dayName = currentDay.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        try {
            String text = "    ---------------------\n";
            text += "Day :            "+dayName+"\n";
            text += "Printed :        "+currentdate+"\n\n\n";
            text += " Thanks for using our services!\n";

            mmOutputStream.write(text.getBytes());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void printCouponNumber(){
        Calendar calendar = Calendar.getInstance();
        String currentdate = DateFormat.getInstance().format(calendar.getTime());
        LocalDate currentDay = LocalDate.now();

        // Get the day of the week for the current date
        String dayName = currentDay.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        try {
            String text = "    ---------------------\n";
            text += "             "+CouponGenerator.couponNumber+"\n";

            mmOutputStream.write(text.getBytes());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void printCouponNumberMod(String couponnumber){
        Calendar calendar = Calendar.getInstance();
        String currentdate = DateFormat.getInstance().format(calendar.getTime());
        LocalDate currentDay = LocalDate.now();

        // Get the day of the week for the current date
        String dayName = currentDay.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        try {
            String text = "    ---------------------\n";
            text += "             "+couponnumber+"\n";

            mmOutputStream.write(text.getBytes());
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    public void beginListenForData() {
        try {
            final Handler handler = new Handler();
            // this is the ASCII code for a newline character
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );
                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
//                                                myLabel.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //    this will update data printer name in ModelUser
    // close the connection to bluetooth printer.
    public void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
