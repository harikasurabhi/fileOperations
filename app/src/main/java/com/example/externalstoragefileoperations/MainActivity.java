package com.example.externalstoragefileoperations;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.externalstoragefileoperations.asyncTask.TaskCompleted;
import com.example.externalstoragefileoperations.model.FileOperationModel;
import com.example.externalstoragefileoperations.asyncTask.FileOperationsAsyncTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TaskCompleted {


    private Menu menu;
    private Button button_start, button_stop, shareMenu;
    ProgressDialog progressDialog;
    private TextView averageFileSizeTextView, averageFileSizeHeaderTextView;
    private RecyclerView recyclerView, recyclerView2;
    private static final int MY_PERMISSION = 1;
    FileOperationsAsyncTask fileOperations;
    FileOperationModel fileOperationModel = new FileOperationModel();


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
        } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION);
            }
            button_start.setOnClickListener(this);
            button_stop.setOnClickListener(this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        menu.getItem(0).setEnabled(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mShare:
                Intent ShareIntent = new Intent(Intent.ACTION_SEND);
                ShareIntent.setType("text/plain");
                ShareIntent.putExtra(Intent.EXTRA_SUBJECT, "Name and sizes of 10 biggest files");
                ShareIntent.putExtra(Intent.EXTRA_TEXT, String.valueOf(onTaskComplete(fileOperationModel).getFileNames())+  String.valueOf(onTaskComplete(fileOperationModel).getFileSize())+ String.valueOf(onTaskComplete(fileOperationModel).getFileExtensionName())+ String.valueOf(onTaskComplete(fileOperationModel).getFrequentFileExtensionCount())+ String.valueOf(onTaskComplete(fileOperationModel).getAverage()));
                startActivity(Intent.createChooser(ShareIntent, "Share using"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No Permission Required", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fileoperations_start:
                button_stop.setEnabled(true);
                fileOperations= new FileOperationsAsyncTask(MainActivity.this, recyclerView, recyclerView2, averageFileSizeHeaderTextView, averageFileSizeTextView, button_stop, button_start, menu);
                fileOperations.execute();
                break;

            case R.id.fileoperations_stop:
                if (fileOperations.isCancelled())
                    break;
                else {
                    fileOperations.cancel(true);
                }
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
        if (fileOperations != null) {
            fileOperations.cancel(true);
        }

    }

    @Override
    public FileOperationModel onTaskComplete(FileOperationModel fileOperationModel) {
        this.fileOperationModel= fileOperationModel;
        return fileOperationModel;
    }
}