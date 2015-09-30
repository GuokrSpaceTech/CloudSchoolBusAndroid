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
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.InfoSwitchedEvent;
import com.squareup.picasso.Picasso;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;
import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.List;

public class SelectUserDialogFragment extends DialogFragment {

    private static final String USERINFO = "userinfo";
    private static final String TAG = SelectUserDialogFragment.class.getName();
    private List<StudentEntity> mChildren;
    private List<ClassEntityT> mClasses;
    private int currentChild;

    private DynamicGridView gridView;
    private Button cancelButton;

    public static SelectUserDialogFragment newInstance(ArrayList<?> infos, String type) {
        SelectUserDialogFragment f = new SelectUserDialogFragment();
        Bundle b = new Bundle();
        b.putSerializable(USERINFO, infos);
        b.putString("type",type);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Object infos = getArguments().get(USERINFO);
        String type = getArguments().getString("type");
        if(type.equals("class"))
            mClasses = (ArrayList<ClassEntityT>)infos;
        else if(type.equals("student"))
            mChildren = (ArrayList<StudentEntity>)infos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_switch_children_layout, container, false);

        if(Version.PARENT) getDialog().setTitle(getResources().getString(R.string.switch_child));
        else getDialog().setTitle(getResources().getString(R.string.switch_class));

        gridView = (DynamicGridView) root.findViewById(R.id.dynamic_grid);

        if(mChildren!=null)
            gridView.setAdapter(new SwitchAdapter(getActivity(), mChildren, getResources().getInteger(R.integer.column_count)));
        else
            gridView.setAdapter(new SwitchAdapter(getActivity(), mClasses,  getResources().getInteger(R.integer.column_count)));

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
                switchUser(position);
            }
        });
        return root;
    }


    // make sure the Activity implemented it
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public static class SwitchAdapter extends BaseDynamicGridAdapter {
        public SwitchAdapter(Context context, List<?> items, int columnCount) {
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

            if(getItem(position) instanceof StudentEntity) {
                StudentEntity childInfo = (StudentEntity) getItem(position);
                holder.build(childInfo.getCnname(), childInfo.getAvatar());
            } else if(getItem(position) instanceof ClassEntityT) {
                ClassEntityT theClass = (ClassEntityT)getItem(position);
                holder.build(theClass.getClassname(), "");
            }

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

                if(!avatarUrl.equals("")) {
                    //Trim the .
                    if(avatarUrl.contains("jpg."))
                    avatarUrl = avatarUrl.substring(0,avatarUrl.lastIndexOf('.'));
                    Picasso.with(getContext()).load(avatarUrl).fit().centerCrop().into(image);
                }
            }
        }
    }

    public void switchUser(int current)
    {
        CloudSchoolBusParentsApplication theApplication =
                (CloudSchoolBusParentsApplication) getActivity().getApplicationContext();
        ConfigEntityDao configEntityDao = theApplication.mDaoSession.getConfigEntityDao();
        List<ConfigEntity> configEntitys = configEntityDao.queryBuilder().list();
        if(configEntitys.size()>0) {
            configEntitys.get(0).setCurrentChild(current);
            configEntityDao.update(configEntitys.get(0));
            theApplication.mConfig = configEntitys.get(0);
            BusProvider.getInstance().post(new InfoSwitchedEvent(current));
        }

        dismiss();
    }
}