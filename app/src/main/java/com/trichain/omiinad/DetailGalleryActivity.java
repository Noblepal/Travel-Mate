package com.trichain.omiinad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.trichain.omiinad.entities.PhotoTable;

import java.util.List;

public class DetailGalleryActivity extends AppCompatActivity {


    String type;
    String TAG ="DetailGalleryActivity";
    int date,holiday,place,id;
    boolean descending;
    List<PhotoTable> photoTables;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_gallery);
        type=getIntent().getStringExtra("type");
        descending=getIntent().getBooleanExtra("descending",true);
        date=getIntent().getIntExtra("date",0);
        holiday=getIntent().getIntExtra("holiday",0);
        place=getIntent().getIntExtra("place",0);
        id=getIntent().getIntExtra("id",0);
        photoTables=(List<PhotoTable>) getIntent().getSerializableExtra("data");
        Log.e(TAG, "onCreate: "+photoTables.size() );
        if (id==0){
            finish();
        }
        if (type=="date"){

        }else if (type=="holiday"){

        }else if (type=="place"){

        }else{
            Log.e(TAG, "onCreate: Very Big error"+type);
        }
    }
}
