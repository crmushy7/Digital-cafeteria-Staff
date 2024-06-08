package Others;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothPrinter {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice printerDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private Context context;
    private BluetoothPrinterCallback callback;

    private static final String TAG = "BluetoothPrinter";
    private static final UUID MY_UUID = UUID.randomUUID();

    public interface BluetoothPrinterCallback {
        void onConnected();
        void onFailed(String error);
    }

    public BluetoothPrinter(Context context, BluetoothPrinterCallback callback) {
        this.context = context;
        this.callback = callback;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Bluetooth is not available");
            callback.onFailed("Bluetooth is not available");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth is not enabled. Requesting enable.");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBtIntent);
        }

        Log.d(TAG, "Registering Bluetooth device discovery receivers");
        context.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        context.registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    public void startDiscovery() {
        Log.d(TAG, "Starting Bluetooth discovery");
        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "BroadcastReceiver received action: " + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "Found device: " + (device != null ? device.getName() : "null"));
                if (device != null && device.getName() != null && device.getName().contains("Printer")) {
                    printerDevice = device;
                    Log.d(TAG, "Printer found. Cancelling discovery");
                    bluetoothAdapter.cancelDiscovery();
                    connectToPrinter(printerDevice);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (printerDevice == null) {
                    Log.d(TAG, "Discovery finished. Printer not found");
                    Toast.makeText(context, "Printer not found", Toast.LENGTH_SHORT).show();
                    callback.onFailed("Printer not found");
                }
            }
        }
    };

    public void connectToPrinter(BluetoothDevice device) {
        try {
            Log.d(TAG, "Connecting to printer: " + device.getName());
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            Log.d(TAG, "Connected to printer and obtained output stream");
            Toast.makeText(context, "Connected to printer", Toast.LENGTH_SHORT).show();
            callback.onConnected();
        } catch (IOException e) {
            Log.e(TAG, "Failed to connect to printer", e);
            Toast.makeText(context, "Failed to connect to printer", Toast.LENGTH_SHORT).show();
            callback.onFailed("Failed to connect to printer: " + e.getMessage());
        }
    }

    public void printText(String text) {
        if (outputStream != null) {
            try {
                Log.d(TAG, "Printing text: " + text);
                outputStream.write(text.getBytes());
                outputStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "Failed to print text", e);
                Toast.makeText(context, "Failed to print text", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, "Output stream is null. Cannot print text.");
        }
    }

    public void close() {
        try {
            if (outputStream != null) {
                Log.d(TAG, "Closing output stream");
                outputStream.close();
            }
            if (bluetoothSocket != null) {
                Log.d(TAG, "Closing Bluetooth socket");
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to close Bluetooth connection", e);
            Toast.makeText(context, "Failed to close Bluetooth connection", Toast.LENGTH_SHORT).show();
        }
    }
}
