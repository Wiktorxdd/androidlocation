package com.example.refresher2;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.refresher2.Model.Food;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static List<Food> foodlist;

    FusedLocationProviderClient flpc;
    TextView txt_lat, txt_lon, txt_alt, txt_dir;

    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        Log.d("MainActivity", "onCreate: starting app");
        flpc = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        initGui();
        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        askForPermissions(permissionsList);
        foodlist = new ArrayList<>();
        EditText input = findViewById(R.id.inputfood);
        Button btn = findViewById(R.id.btnfood);
        btn.setOnClickListener(v ->
                foodlist.add(new Food(input.getText().toString()))
        );


    }

    private void initGui() {
        txt_lat = findViewById(R.id.txt_lat);
        txt_lon = findViewById(R.id.txt_lon);
        txt_alt = findViewById(R.id.txt_alt);
        txt_dir = findViewById(R.id.txt_dir);
    }

    private void getUpdate() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000).build();
        flpc = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        flpc.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                txt_lat.setText("Latitude: " + location.getLatitude());
                txt_lon.setText("Longitude" + location.getLongitude());
                txt_alt.setText("Altitude" + location.getAltitude());
                txt_dir.setText("Direction" + location.getBearing());
            }
        }, Looper.myLooper());

    }

    ArrayList<String> permissionsList;
    String[] permissionsStr = {permission.CAMERA,
            permission.RECORD_AUDIO,
            permission.READ_EXTERNAL_STORAGE,
            permission.ACCESS_FINE_LOCATION,
            permission.ACCESS_COARSE_LOCATION,
            permission.ACCESS_BACKGROUND_LOCATION};
    int permissionsCount = 0;
    ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onActivityResult(Map<String, Boolean> result) {
                            ArrayList<Boolean> list = new ArrayList<>(result.values());
                            permissionsList = new ArrayList<>();
                            permissionsCount = 0;for (int i = 0; i < list.size(); i++) {
                                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                                    permissionsList.add(permissionsStr[i]);
                                }else if (!hasPermission(MainActivity.this, permissionsStr[i])){
                                    permissionsCount++;
                                }
                            }if (permissionsList.size() > 0) {
                                //Some permissions are denied and can be asked again.
                                askForPermissions(permissionsList);
                            } else if (permissionsCount > 0) {
                                //Show alert dialog
                                showPermissionDialog();
                            } else {
                                getUpdate();
                            }
                        }
                    });

    private void askForPermissions(ArrayList<String> permissionsList) {
        String[] newPermissionStr = new String[permissionsList.size()];
        for (int i = 0; i < newPermissionStr.length; i++) {
            newPermissionStr[i] = permissionsList.get(i);
        }
        if (newPermissionStr.length > 0) {
            permissionsLauncher.launch(newPermissionStr);
        } else {
        /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
        which will lead them to app details page to enable permissions from there. */
            showPermissionDialog();
        }
    }
    AlertDialog alertDialog;private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission required")
                .setMessage("Some permissions are need to be allowed to use this app without any problems.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    dialog.dismiss();
                });
        if (alertDialog == null) {
            alertDialog = builder.create();
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

}