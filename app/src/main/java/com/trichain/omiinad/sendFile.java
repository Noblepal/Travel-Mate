package com.trichain.omiinad;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.trichain.omiinad.Entities.PhotoTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class sendFile extends FileProvider {
    public void sendMyFile(Context context, List<PhotoTable> photoTables2){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("image/jpeg"); /* This example is sharing jpeg images. */

        ArrayList<Uri> files = new ArrayList<Uri>();
        for (int i = 0; i < photoTables2.size(); i++) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/" + photoTables2.get(i).getPhotoName());
            Uri uri = Uri.fromFile(file);
            files.add(uri);
        }


        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        context.startActivity(intent);
    }
}
