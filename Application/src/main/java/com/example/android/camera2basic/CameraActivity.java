/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.camera2basic;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends Activity {

    private Spinner spinner;
    public int imageType;
    public imageTypeOnItemSelectedListener imageTypeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commit();
        }

//        addItemsOnSpinner();
//        addListenerOnSpinnerItemSelection();
    }

    public void addItemsOnSpinner() {

        spinner = (Spinner) findViewById(R.id.pickImageType);

        List<String> list = new ArrayList<String>();
        list.add("Brightfield");
        list.add("Fluorescent");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinnerItemSelection() {
        imageTypeListener =  new imageTypeOnItemSelectedListener();
        spinner = (Spinner) findViewById(R.id.pickImageType);
        spinner.setOnItemSelectedListener(imageTypeListener);

    }

}
