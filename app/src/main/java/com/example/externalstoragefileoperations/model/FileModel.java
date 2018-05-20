package com.example.externalstoragefileoperations.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m794802 on 3/26/18.
 */

public class FileModel{

    private String fileNames;
    private Long fileSize;

    public FileModel(String fileNames, Long fileSize) {
        this.fileNames = fileNames;
        this.fileSize = fileSize;
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "fileNames='" + fileNames + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}
