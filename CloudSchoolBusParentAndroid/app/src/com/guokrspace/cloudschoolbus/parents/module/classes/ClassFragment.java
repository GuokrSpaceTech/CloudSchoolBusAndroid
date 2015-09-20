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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;


import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.module.classes.adapter.PictureAdapter;
import com.squareup.picasso.Picasso;

import net.soulwolf.image.picturelib.model.Picture;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;
import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassFragment extends BaseFragment {

    private static final String ARG_POSITION = "position";
    private static final String TAG = ClassFragment.class.getName();
    private OnCompleteListener mListener;
    List<Picture> mPictureList = new ArrayList<>();
    PictureAdapter mPictureAdapter;
    private ImageView mTeacherAvatar;
    private TextView  mClassName;
    private TextView  mSchoolName;
    private DynamicGridView gridView;
    private Context mContext;
    private ActionBar mActionBar;
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

        ((MainActivity) mParentContext).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) mParentContext).getSupportActionBar().setTitle(getResources().getString(R.string.module_class));

        mTeacherAvatar = (ImageView)root.findViewById(R.id.teacher_avatar);
        mClassName = (TextView)root.findViewById(R.id.class_name);
        mSchoolName = (TextView)root.findViewById(R.id.kindergarten_name);

        Picasso.with(mParentContext).load(getMyself().getAvatar()).into(mTeacherAvatar);
        mClassName.setText(findCurrentClass(0).getClassname());
        mSchoolName.setText(mApplication.mSchoolsT.get(0).getName());

        gridView = (DynamicGridView) root.findViewById(R.id.dynamic_grid);
//        gridView.setAdapter(new ClassifyDynamicAdapter(getActivity(),
//                new ArrayList<>(Arrays.asList(ClassifyComponent.classifyModules)),
//                getResources().getInteger(R.integer.column_count)));
        gridView.setAdapter(new ClassifyDynamicAdapter(getActivity(),
                new ArrayList<>(Arrays.asList(ClassifyComponent.classifyModules)),
                getResources().getInteger(R.integer.column_count)));

        //add callback to stop edit mode if needed
        //add callback to stop edit mode if needed
        gridView.setOnDropListener(new DynamicGridView.OnDropListener() {
            @Override
            public void onActionDrop() {
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
                ClassifyDynamicAdapter.ClassifyModule classifyModule = (ClassifyDynamicAdapter.ClassifyModule) parent.getAdapter().getItem(position);

                if(classifyModule.getUrl()!="") {
                    WebviewFragment fragment = WebviewFragment.newInstance(classifyModule.getUrl());
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.activity_class_layout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        return root;
    }

    public static interface OnCompleteListener {
        public abstract void onComplete(String time);
    }

    public static class ClassifyComponent {

        final static String url = "http://m.yunxiaoche.com";

        public static ClassifyDynamicAdapter.ClassifyModule[] classifyModules = {

                new ClassifyDynamicAdapter.ClassifyModule("晨检考勤", R.drawable.ic_attendance,url),
                new ClassifyDynamicAdapter.ClassifyModule("班级报告", R.drawable.ic_report, url),
                new ClassifyDynamicAdapter.ClassifyModule("通知消息", R.drawable.ic_notice, url),
                new ClassifyDynamicAdapter.ClassifyModule("视频公开课", R.drawable.ic_streaming,url),
                new ClassifyDynamicAdapter.ClassifyModule("课程表", R.drawable.ic_schedule, url),
                new ClassifyDynamicAdapter.ClassifyModule("食谱", R.drawable.ic_food, url),
                new ClassifyDynamicAdapter.ClassifyModule("相册", R.drawable.ic_picture, url),
                new ClassifyDynamicAdapter.ClassifyModule("", 0, null),
                new ClassifyDynamicAdapter.ClassifyModule("", 0, null)
        };
    }

    public static class ClassifyDynamicAdapter extends BaseDynamicGridAdapter {
        public ClassifyDynamicAdapter(Context context, List<?> items, int columnCount) {
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

            ClassifyModule classifyModule = (ClassifyModule) getItem(position);

            holder.build(classifyModule.getTitle(), classifyModule.getImageRes());
            return convertView;
        }

        private class ClassViewHolder {
            private TextView titleText;
            private ImageView image;

            private ClassViewHolder(View view) {
                titleText = (TextView) view.findViewById(R.id.item_title);
                image = (ImageView) view.findViewById(R.id.item_img);
            }

            void build(String title, int imageRes) {
                titleText.setText(title);
                if(imageRes!=0)
                    image.setImageResource(imageRes);
            }
        }

        /**
         * Created by wangjianfeng on 15/7/10.
         */
        public static class ClassifyModule {
            private String title;
            private Integer imageRes;
            private String url;

            public ClassifyModule(String title, Integer imageRes, String url) {
                this.title = title;
                this.imageRes = imageRes;
                this.url = url;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public Integer getImageRes() {
                return imageRes;
            }

            public void setImageRes(Integer imageRes) {
                this.imageRes = imageRes;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}