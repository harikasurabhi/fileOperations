package com.example.externalstoragefileoperations;

import android.Manifest;
import android.app.LoaderManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.externalstoragefileoperations.adapters.CustomAdapter;
import com.example.externalstoragefileoperations.model.FileOperationModel;
import com.example.externalstoragefileoperations.asyncTask.FileOperationsAsyncTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<FileOperationModel> {

    private Menu menu;
    private Button button_start, button_stop, shareMenu;
    ProgressDialog progressDialog;
    private TextView averageFileSizeTextView, averageFileSizeHeaderTextView;
    private RecyclerView recyclerView, recyclerView2;
    private static final int MY_PERMISSION = 1;
    LoaderManager mLoaderManager;
    CustomAdapter customAdapter, customAdapter1;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        averageFileSizeTextView = (TextView) findViewById(R.id.averageFileSize);
        averageFileSizeHeaderTextView = (TextView) findViewById(R.id.averageFileSizeHeader);
        button_start = (Button) findViewById(R.id.fileoperations_start);
        button_stop = (Button) findViewById(R.id.fileoperations_stop);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerViewID2);
        shareMenu = (Button) findViewById(R.id.mShare);
        button_stop.setEnabled(false);
        averageFileSizeTextView.setEnabled(false);
        averageFileSizeHeaderTextView.setEnabled(false);

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            button_start.setEnabled(false);
            button_stop.setEnabled(false);
        } else {
            button_start.setOnClickListener(this);
            button_stop.setOnClickListener(this);
        }

        mLoaderManager = getLoaderManager();
        if (mLoaderManager.getLoader(0) != null) {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                        button_stop.setEnabled(true);
                        progressDialog = new ProgressDialog(this);
                        progressDialog.setTitle("Fetching data from External Storage");
                        progressDialog.setMax(10);
                        progressDialog.setProgress(0);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                        createNotification("Scanning external storage", "Scanning external storage is in progress");
                        mLoaderManager.initLoader(0, null, this);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied by User", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fileoperations_start:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    button_stop.setEnabled(true);
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Fetching data from External Storage");
                    progressDialog.setMax(10);
                    progressDialog.setProgress(0);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    createNotification("Scanning external storage", "Scanning external storage is in progress");
                    mLoaderManager.initLoader(0, null, this);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION);
                }
                break;

            case R.id.fileoperations_stop:
                mLoaderManager.destroyLoader(0);
                createNotification("Scanning external storage", "Scanning external storage is Cancelled!");
                button_stop.setEnabled(false);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        mLoaderManager.destroyLoader(0);
        createNotification("Scanning external storage", "Scanning external storage is Cancelled!");
    }

    @Override
    public Loader<FileOperationModel> onCreateLoader(int id, Bundle args) {
        return new FileOperationsAsyncTask(this);
    }

    @Override
    public void onLoadFinished(Loader<FileOperationModel> loader, FileOperationModel fileOperationModel) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(linearLayoutManager1);
        customAdapter = new CustomAdapter(this, fileOperationModel.getFileNames(), fileOperationModel.getFileSize());
        customAdapter1 = new CustomAdapter(this, fileOperationModel.getFrequentFileExtension(), fileOperationModel.getFrequentFileExtensionCount());
        recyclerView.setAdapter(customAdapter);
        recyclerView2.setAdapter(customAdapter1);
        averageFileSizeHeaderTextView.setEnabled(true);
        averageFileSizeTextView.setEnabled(true);
        averageFileSizeTextView.setText(fileOperationModel.getAverage().toString());
        createNotification("Scanning external storage", "Scanning external storage is complete!");
//        menu.getItem(0).setEnabled(true);
        button_stop.setEnabled(false);
    }

    @Override
    public void onLoaderReset(Loader<FileOperationModel> loader) {
    }

    private void createNotification(String contentTitle, String contentText) {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText);

        mNotification = builder.getNotification();
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }
}