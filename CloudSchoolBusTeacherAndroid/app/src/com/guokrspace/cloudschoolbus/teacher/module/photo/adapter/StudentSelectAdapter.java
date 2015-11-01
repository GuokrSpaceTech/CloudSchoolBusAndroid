package com.guokrspace.cloudschoolbus.teacher.module.photo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guokrspace.cloudschoolbus.teacher.R;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.teacher.module.photo.SelectStudentActivity;
import com.guokrspace.cloudschoolbus.teacher.widget.RoundCornerImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 学生选择adapter
 *
 * @author lenovo
 */
public class StudentSelectAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<StudentEntityT> mStudents = new ArrayList<>();
    private HashMap<Integer, StudentEntityT> mSelections = new HashMap<>();

    public StudentSelectAdapter(Context context, List<StudentEntityT> students) {
        mContext = context;
        mStudents = (ArrayList) students;
    }

    @Override
    public int getCount() {
        return mStudents.size() + 1;
    }

    @Override
    public Object getItem(int arg0) {
        if (0 == arg0) {
            return null;
        }
        return mStudents.get(arg0 - 1);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup arg2) {

        ViewHolder holder = new ViewHolder();
        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_stu_select_item, null);
            holder.linearLayout = (LinearLayout)view.findViewById(R.id.mainLayout);
            holder.avatarImageView = (RoundCornerImageView)view.findViewById(R.id.headImageView);
            holder.nameTextView = (TextView)view.findViewById(R.id.stuNameTextView);
            holder.selectIcon = (ImageView)view.findViewById(R.id.selectImageView);
            holder.selectAllTextView = (TextView)view.findViewById(R.id.selectAllTextView);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        //Select All /Unselect All
        if(position ==0)
        {
            holder.avatarImageView.setVisibility(View.INVISIBLE);
            holder.selectIcon.setVisibility(View.INVISIBLE);
            holder.nameTextView.setVisibility(View.INVISIBLE);
            holder.selectAllTextView.setVisibility(View.VISIBLE);
            holder.linearLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (!mSelections.containsKey(0)) {
                        StudentEntityT studentEntity = new StudentEntityT(); //Dummy object indicates select all
                        mSelections.put(0, studentEntity);
                        selectAllStudents();
                        notifyDataSetChanged();
                    } else {
                        mSelections.clear();
                        notifyDataSetChanged();
                    }

                    if (mSelections.size() == 0 || (mSelections.size()==1 && mSelections.containsKey(0)))
                        ((SelectStudentActivity) mContext).mUploadAction.setEnabled(false);
                    else
                        ((SelectStudentActivity) mContext).mUploadAction.setEnabled(true);
                }
            });


        } else {
            holder.avatarImageView.setVisibility(View.VISIBLE);
            holder.selectIcon.setVisibility(View.VISIBLE);
            holder.nameTextView.setVisibility(View.VISIBLE);
            holder.selectAllTextView.setVisibility(View.INVISIBLE);
            final int studentPosition = position - 1;
            final StudentEntityT student = mStudents.get(studentPosition);
            final ImageView selectImageView = (ImageView) view.findViewById(R.id.selectImageView);

            if (!TextUtils.isEmpty(student.getAvatar())) {
                String avatarpath = student.getAvatar();
                if (avatarpath.contains("jpg."))
                    avatarpath = student.getAvatar().substring(0, student.getAvatar().lastIndexOf('.'));
                if (!avatarpath.equals(""))
                    Picasso.with(mContext).load(avatarpath).fit().centerCrop().into(holder.avatarImageView);
            }
            holder.nameTextView.setText(student.getCnname());

            if (mSelections.containsKey(position)) {
                holder.selectIcon.setVisibility(View.VISIBLE);
            } else {
                holder.selectIcon.setVisibility(View.INVISIBLE);
            }

            final ViewHolder holderFinal = holder;
            holder.linearLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mSelections.containsKey(position)) {
                        holderFinal.selectIcon.setVisibility(View.GONE);
                        mSelections.remove(position);
                    } else {
                        mSelections.put(position, student);
                        holderFinal.selectIcon.setVisibility(View.VISIBLE);
                    }

                    if (mSelections.size() == 0 || (mSelections.size()==1 && mSelections.containsKey(0)))
                        ((SelectStudentActivity) mContext).mUploadAction.setEnabled(false);
                    else
                        ((SelectStudentActivity) mContext).mUploadAction.setEnabled(true);
                }
            });
        }

        return view;
    }

    public HashMap<Integer, StudentEntityT> getmSelections() {
        return mSelections;
    }

    private void selectAllStudents() {
        int i = 1; // 0 - Dummy Student indicating selecting all
        for (StudentEntityT student : mStudents) {
            mSelections.put(i, student);
            i++;
        }
    }

    class ViewHolder{
        public LinearLayout linearLayout;
        public RoundCornerImageView avatarImageView;
        public TextView nameTextView;
        public ImageView selectIcon;
        public TextView selectAllTextView;
    }
}
