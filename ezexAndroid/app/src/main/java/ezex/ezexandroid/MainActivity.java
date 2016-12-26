package ezex.ezexandroid;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import fs.ReceiveClient;

public class MainActivity extends AppCompatActivity {

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    String albumName = "Imported by ezex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("Info: ", "Requesting persmission");

        verifyStoragePermissions(this);

        if (!isExternalStorageWritable()) {
            Log.e("Error: ", "Directory not available");

        } else {

            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "test.jpg");

            try {
                file.createNewFile();
                Log.i("Info: ", "Created file successfully");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void onConnectButtonClick(View button) {

        EditText ip = (EditText) findViewById(R.id.ipField);
        EditText port = (EditText) findViewById(R.id.portField);

        Log.i("INFO: ", getString(R.string.onConnectMessage) + ip.getText());


        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);

        if (!file.mkdirs()) {
            Log.e("Error: ", "Directory not created");
        }

        new RecevieFilesTask().execute(
                new RecevieFilesTask.Args(
                        ip.getText().toString(),
                        Integer.parseInt(port.getText().toString()),
                        file
                )
        );


    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
