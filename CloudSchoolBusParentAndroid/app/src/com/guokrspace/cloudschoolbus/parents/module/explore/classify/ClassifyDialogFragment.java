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

package com.guokrspace.cloudschoolbus.parents.module.explore.classify;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.module.classes.Streaming.IpcSelectionActivity;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;
import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassifyDialogFragment extends DialogFragment {

    private static final String ARG_POSITION = "position";
    private static final String TAG = ClassifyDialogFragment.class.getName();
    private OnCompleteListener mListener;

    private DynamicGridView gridView;
//    private int position;

    public static ClassifyDialogFragment newInstance() {
        ClassifyDialogFragment f = new ClassifyDialogFragment();
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

        getDialog().setTitle(getResources().getString(R.string.classify));
        getDialog().setCancelable(true);

        gridView = (DynamicGridView) root.findViewById(R.id.dynamic_grid);
        gridView.setAdapter(new ClassifyDynamicAdapter(getActivity(),
                new ArrayList<>(Arrays.asList(ClassifyComponent.classifyModules)),
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
                ClassifyDynamicAdapter.ClassifyModule classifyModule = (ClassifyDynamicAdapter.ClassifyModule)parent.getAdapter().getItem(position);
//                Toast.makeText(getActivity(), classModule.getTitle(),
//                        Toast.LENGTH_SHORT).show();

                FragmentTransaction transaction;
                switch (classifyModule.getTitle())
                {
                    case "视频公开课":
                        Intent intent = new Intent(getActivity(), IpcSelectionActivity.class);
                        startActivity(intent);
                        dismiss();

                        break;
                    case "通知消息":
                        mListener.onComplete("notice");
                        dismiss();
                        break;

                    case "晨检考勤":
                        mListener.onComplete("attendance");
                        dismiss();
                        break;
                    case "课程表":
                        mListener.onComplete("schedule");
                        dismiss();
                        break;
                    case "班级报告":
                        mListener.onComplete("report");
                        dismiss();
                        break;
                    case "食谱":
                        mListener.onComplete("food");
                        dismiss();
                        break;
                }
            }
        });

        return root;
    }

    public static interface OnCompleteListener {
        public abstract void onComplete(String time);
    }


    // make sure the Activity implemented it
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    public static class ClassifyComponent {

        public static ClassifyDynamicAdapter.ClassifyModule[] classifyModules = {
                new ClassifyDynamicAdapter.ClassifyModule("晨检考勤",R.drawable.ic_attendance), new ClassifyDynamicAdapter.ClassifyModule("班级报告",R.drawable.ic_report), new ClassifyDynamicAdapter.ClassifyModule("通知消息",R.drawable.ic_notice),
                new ClassifyDynamicAdapter.ClassifyModule("视频公开课",R.drawable.ic_streaming), new ClassifyDynamicAdapter.ClassifyModule("课程表",R.drawable.ic_schedule), new ClassifyDynamicAdapter.ClassifyModule("食谱",R.drawable.ic_food),
                new ClassifyDynamicAdapter.ClassifyModule("相册", R.drawable.ic_picture)
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

            ClassifyModule classifyModule = (ClassifyModule)getItem(position);

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
                image.setImageResource(imageRes);
            }
        }

        /**
         * Created by wangjianfeng on 15/7/10.
         */
        public static class ClassifyModule {
            private String title;
            private Integer imageRes;

            public ClassifyModule(String title, Integer imageRes) {
                this.title = title;
                this.imageRes = imageRes;
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
        }
    }
}