package com.example.externalstoragefileoperations.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m794802 on 3/26/18.
 */

public class FileOperationModel {

    private List<String> fileNames = new ArrayList<String>();
    private List<Long> fileSize = new ArrayList<Long>();
    private ArrayList<String> fileExtensionName = new ArrayList<String>();
    private ArrayList<String> frequentFileExtension = new ArrayList<String>();
    private ArrayList<Long> frequentFileExtensionCount = new ArrayList<Long>();
    private Double average;

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public List<String> getFrequentFileExtension() {
        return frequentFileExtension;
    }

    public void setFrequentFileExtension(ArrayList<String> frequestFileExtension) {
        this.frequentFileExtension = frequestFileExtension;
    }

    public List<Long> getFrequentFileExtensionCount() {
        return frequentFileExtensionCount;
    }

    public void setFrequentFileExtensionCount(ArrayList<Long> frequentFileExtensionCount) {
        this.frequentFileExtensionCount = frequentFileExtensionCount;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public List<Long> getFileSize() {
        return fileSize;
    }

    public void setFileSize(List<Long> fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileSize(ArrayList<Long> fileSize) {
        this.fileSize = fileSize;
    }

    public ArrayList<String> getFileExtensionName() {
        return fileExtensionName;
    }

    public void setFileExtensionName(ArrayList<String> fileExtensionName) {
        this.fileExtensionName = fileExtensionName;
    }
}
