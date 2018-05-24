package com.example.externalstoragefileoperations.asyncTask;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Environment;

import com.example.externalstoragefileoperations.model.FileModel;
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

public class FileOperationsAsyncTask extends AsyncTaskLoader<FileOperationModel> {

    FileOperationModel fileOperationModel;
    long size;
    String name;
    String extension = null;
    int index;
    LinkedList<FileModel> linkedList;

    Map<String, Integer> map_words = new HashMap<>();

    public FileOperationsAsyncTask(Context context) {
        super(context);
    }

    private void listFiles(File[] files) {
        for (File f : files) {
            if (f.isFile()) {
                name = f.getName();
                size = f.length() / 1024;
                index = name.lastIndexOf(".");
                extension = name.substring(index + 1);
                linkedList.add(new FileModel(name, size));
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
        linkedList = new LinkedList<FileModel>();
        File dir = Environment.getExternalStorageDirectory();
        listFiles(dir.listFiles());
        Collections.sort(linkedList, new SortLinkedViasize());

        List<Long> fileSize = new ArrayList<Long>();
        List<String> fileName = new ArrayList<String>();
        if (linkedList.size() > 10) {
            for (int i = 0; i < 10; i++) {
                fileSize.add(linkedList.get(i).getFileSize());
                fileName.add(linkedList.get(i).getFileNames());
            }
        } else {
            for (int j = 0; j < linkedList.size(); j++) {
                fileSize.add(linkedList.get(j).getFileSize());
                fileName.add(linkedList.get(j).getFileNames());
            }
        }
        fileOperationModel.setFileNames(fileName);
        fileOperationModel.setFileSize(fileSize);

        double sum = 0;

        Iterator<FileModel> itr = linkedList.iterator();
        while (itr.hasNext()) {
            sum += itr.next().getFileSize();
        }

        double average = sum / linkedList.size();

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
        fileOperationModel.setAverage(average);
        return fileOperationModel;
    }

    @Override
    public FileOperationModel loadInBackground() {
        startScanningExternalStorage();
        return fileOperationModel;
    }

    class SortLinkedViasize implements Comparator<FileModel> {
        @Override
        public int compare(FileModel o1, FileModel o2) {
            return (o2.getFileSize()).compareTo(o1.getFileSize());
        }
    }

    @Override
    protected void onStartLoading() {

        super.onStartLoading();

        if (fileOperationModel != null) {
            deliverResult(fileOperationModel);
        }

        if (fileOperationModel == null || takeContentChanged()) {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(FileOperationModel data) {
        super.deliverResult(data);
    }
}