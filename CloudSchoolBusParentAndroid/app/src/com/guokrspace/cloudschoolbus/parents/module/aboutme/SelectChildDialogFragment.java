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

package com.guokrspace.cloudschoolbus.parents.module.aboutme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.entity.Student;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.ChildSwitchedEvent;
import com.guokrspace.cloudschoolbus.parents.module.classes.Streaming.IpcSelectionActivity;
import com.squareup.picasso.Picasso;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;
import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectChildDialogFragment extends DialogFragment {

    private static final String CHILDINFO = "childinfo";
    private static final String TAG = SelectChildDialogFragment.class.getName();
    private List<StudentEntity> mChildren;
    private int currentChild;

    private DynamicGridView gridView;
    private Button confirmButton;
    private Button cancelButton;
//    private int position;

    public static SelectChildDialogFragment newInstance(ArrayList<StudentEntity> childInfos) {
        SelectChildDialogFragment f = new SelectChildDialogFragment();
        Bundle b = new Bundle();
        b.putSerializable(CHILDINFO, childInfos);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChildren = (List<StudentEntity>)getArguments().getSerializable(CHILDINFO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_switch_children_layout, container, false);

        getDialog().setTitle(getResources().getString(R.string.switch_child));
        getDialog().setCancelable(true);

        cancelButton = (Button)root.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        gridView = (DynamicGridView) root.findViewById(R.id.dynamic_grid);
        gridView.setAdapter(new ChildrenSwitchDynamicAdapter(getActivity(), mChildren, getResources().getInteger(R.integer.column_count)));

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
//                gridView.startEditMode(position);
                return true;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchChildren(position);
            }
        });
        return root;
    }


    // make sure the Activity implemented it
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public static class ChildrenSwitchDynamicAdapter extends BaseDynamicGridAdapter {
        public ChildrenSwitchDynamicAdapter(Context context, List<?> items, int columnCount) {
            super(context, items, columnCount);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ClassViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_class_grid_item, null);
                holder = new ClassViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ClassViewHolder) convertView.getTag();
            }

            StudentEntity childInfo = (StudentEntity)getItem(position);
            holder.build(childInfo.getCnname(),childInfo.getAvatar());

            return convertView;
        }

        private class ClassViewHolder {
            private TextView titleText;
            private ImageView image;

            private ClassViewHolder(View view) {
                titleText = (TextView) view.findViewById(R.id.item_title);
                image = (ImageView) view.findViewById(R.id.item_img);
            }

            void build(String title, String avatarUrl) {
                titleText.setText(title);
                Picasso.with(getContext()).load(avatarUrl).into(image);
            }
        }
    }

    public void switchChildren(int currentChild)
    {
        CloudSchoolBusParentsApplication theApplication = (CloudSchoolBusParentsApplication) getActivity()
                .getApplicationContext();
        ConfigEntityDao configEntityDao = theApplication.mDaoSession.getConfigEntityDao();
        ConfigEntity oldConfigEntity = configEntityDao.queryBuilder().limit(1).list().get(0);
        oldConfigEntity.setCurrentChild(currentChild);
        ConfigEntity newConfigEntity = oldConfigEntity;
        configEntityDao.update(newConfigEntity);
        theApplication.mConfig = newConfigEntity;

        BusProvider.getInstance().post(new ChildSwitchedEvent(currentChild));

    }
}