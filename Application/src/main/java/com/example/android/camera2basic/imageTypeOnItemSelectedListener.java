package com.example.android.camera2basic;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

/**
 * Created by Jonathan on 7/30/2016.
 */
public class imageTypeOnItemSelectedListener implements OnItemSelectedListener {

    public int imageType;
    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

        imageType = pos;
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + pos,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


}
