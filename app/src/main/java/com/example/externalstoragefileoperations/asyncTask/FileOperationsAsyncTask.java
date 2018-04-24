package com.example.externalstoragefileoperations.asyncTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.example.externalstoragefileoperations.MainActivity;
import com.example.externalstoragefileoperations.adapters.CustomAdapter;
import com.example.externalstoragefileoperations.model.FileOperationModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FileOperationsAsyncTask extends AsyncTask<Integer, Integer, Void> {

    Context context;
    Menu menu;
    Button button_stop, button_start;
    ProgressDialog progressDialog;
    private TextView averageFileSizeTextView, averageFileSizeHeaderTextView;
    private RecyclerView recyclerView, recyclerView2;
    FileOperationModel fileOperationModel;

    private TaskCompleted mCallback;

    private int NOTIFICATION_ID = 1;
    private Notification mNotification;
    private NotificationManager mNotificationManager;

    CustomAdapter customAdapter, customAdapter1;
    long size = 0;
    String name;
    String extension = null;
    int index;

    TreeMap<Long, String> sortedMap = new TreeMap<Long, String>(Collections.reverseOrder());
    Map<String, Integer> map_words = new HashMap<>();

    public FileOperationsAsyncTask(Context context, RecyclerView recyclerView, RecyclerView recyclerView2, TextView averageFileSizeHeaderTextView, TextView averageFileSizeTextView, Button button_stop, Button button_start, Menu menu) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.recyclerView2 = recyclerView2;
        this.averageFileSizeHeaderTextView = averageFileSizeHeaderTextView;
        this.averageFileSizeTextView = averageFileSizeTextView;
        this.button_stop = button_stop;
        this.button_start = button_start;
        this.menu = menu;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mCallback= (TaskCompleted) context;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        startScanningExternalStorage();
        publishProgress(params);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Fetching data from External Storage");
        progressDialog.setMax(10);
        progressDialog.setProgress(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.show();

        createNotification("Scanning external storage", "Scanning external storage is in progress");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(linearLayoutManager1);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        createNotification("Scanning external storage stopped", "Scanning external storage stopped");
    }

    @Override
    protected void onPostExecute(Void v) {
        if (progressDialog.isShowing() || progressDialog != null) {
            progressDialog.dismiss();
        }
        customAdapter = new CustomAdapter((MainActivity) context, fileOperationModel.getFileNames(), fileOperationModel.getFileSize());
        customAdapter1 = new CustomAdapter((MainActivity) context, fileOperationModel.getFrequentFileExtension(), fileOperationModel.getFrequentFileExtensionCount());

        recyclerView.setAdapter(customAdapter);
        recyclerView2.setAdapter(customAdapter1);

        averageFileSizeHeaderTextView.setEnabled(true);
        averageFileSizeTextView.setEnabled(true);

        averageFileSizeTextView.setText(fileOperationModel.getAverage().toString());

        createNotification("Scanning external storage", "Scanning external storage is complete!");

        menu.getItem(0).setEnabled(true);
        button_stop.setEnabled(false);
        mCallback.onTaskComplete(fileOperationModel);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

    }

    private void listFiles(File[] files) {
        for (File f : files) {
            if (f.isFile()) {
                name = f.getName();
                size = f.length() / 1024;
                index = name.lastIndexOf(".");
                extension = name.substring(index + 1);
                sortedMap.put(size, name);

                if (index > 0) {
                    extension = name.substring(index + 1);
                }
                fileOperationModel.getFrequentFileExtension().add(extension);
            } else if (f.isDirectory()) {
                listFiles(f.listFiles());
            }
        }
    }

    public FileOperationModel startScanningExternalStorage() {
        fileOperationModel = new FileOperationModel();
        File dir = Environment.getExternalStorageDirectory();
        listFiles(dir.listFiles());
        List<Long> k = new ArrayList<>(sortedMap.keySet());
        double sum = 0;

        Iterator<Long> iter1 = k.iterator();
        while (iter1.hasNext()) {
            sum += iter1.next();
        }
        double average = sum / k.size();

        List<String> v = new ArrayList<>(sortedMap.values());

        if (sortedMap.size() > 10) {
            k = k.subList(0, 10);
            v = v.subList(0, 10);
        }

        for (String words : fileOperationModel.getFrequentFileExtension()) {
            if (map_words.containsKey(words)) {
                map_words.put(words, map_words.get(words) + 1);
            } else {
                map_words.put(words, 1);
            }
        }

        Set<Map.Entry<String, Integer>> set = map_words.entrySet();
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        if (list.size() > 5) {
            list = list.subList(0, 5);
        }

        Map<String, Long> sortedMap1 = new LinkedHashMap<String, Long>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap1.put(entry.getKey(), Long.valueOf(entry.getValue()));
        }

        List<Long> vw = new ArrayList<>(sortedMap1.values());
        List<String> kw = new ArrayList<>(sortedMap1.keySet());

        fileOperationModel.setFrequentFileExtension((ArrayList<String>) kw);
        fileOperationModel.setFrequentFileExtensionCount((ArrayList<Long>) vw);

        fileOperationModel.setFileSize(k);
        fileOperationModel.setFileNames(v);

        fileOperationModel.setAverage(average);

        return fileOperationModel;
    }

    private void createNotification(String contentTitle, String contentText) {

        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText);

        mNotification = builder.getNotification();

        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

}