package com.example.externalstoragefileoperations.asyncTask;

import com.example.externalstoragefileoperations.model.FileOperationModel;

/**
 * Created by m794802 on 4/23/18.
 */

public interface TaskCompleted {

    FileOperationModel onTaskComplete(FileOperationModel fileOperationModel);

}
