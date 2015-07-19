/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guokrspace.cloudschoolbus.parents.module.classes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.module.classes.Streaming.IpcSelectionActivity;
import com.guokrspace.cloudschoolbus.parents.module.classes.notice.NoticeFragment;

import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.Arrays;

public class ClassFragment extends BaseFragment {

    private static final String ARG_POSITION = "position";
    private static final String TAG = ClassFragment.class.getName();

    private DynamicGridView gridView;
//    private int position;

    public static ClassFragment newInstance() {
        ClassFragment f = new ClassFragment();
//        Bundle b = new Bundle();
//        b.putInt(ARG_POSITION, position);
//        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_class_grid, container, false);

        gridView = (DynamicGridView) root.findViewById(R.id.dynamic_grid);
        gridView.setAdapter(new ClassDynamicAdapter(getActivity(),
                new ArrayList<ClassModule>(Arrays.asList(ClassComponent.classModules)),
                getResources().getInteger(R.integer.column_count)));

        //add callback to stop edit mode if needed
        gridView.setOnDropListener(new DynamicGridView.OnDropListener()
        {
            @Override
            public void onActionDrop()
            {
                gridView.stopEditMode();
            }
        });

        gridView.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
                Log.d(TAG, "drag started at position " + position);
            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                Log.d(TAG, String.format("drag item position changed from %d to %d", oldPosition, newPosition));
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridView.startEditMode(position);
                return true;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassModule classModule = (ClassModule)parent.getAdapter().getItem(position);
                Toast.makeText(getActivity(), classModule.getTitle(),
                        Toast.LENGTH_SHORT).show();

                switch (classModule.getTitle())
                {
                    case "视频公开课":
                        Intent intent = new Intent(getActivity(), IpcSelectionActivity.class);
                        startActivity(intent);
                        break;
                    case "通知消息":
                        NoticeFragment noticeFragment = NoticeFragment.newInstance(null, null);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.class_module_layout, noticeFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                }
            }
        });

        return root;
    }
}