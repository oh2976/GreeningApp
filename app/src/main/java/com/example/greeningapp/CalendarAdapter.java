package com.example.greeningapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CalendarAdapter extends BaseAdapter {
    private Context context;
    private List<Calendar> calendarList;

    // 생성자 - CalendarAdapter 객체를 생성할 때 호출, Context와 Calendar 데이터 리스트를 받아옴
    public CalendarAdapter(Context context, List<Calendar> calendarList) {
        this.context = context;
        this.calendarList = calendarList;
    }

    public int getCount() {
        return calendarList.size();
    }

    public Object getItem(int position) {
        return calendarList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.calendar_item, parent, false);
            holder = new ViewHolder();
            holder.dayTextView = convertView.findViewById(R.id.dayTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Calendar calendarModel = calendarList.get(position);
        holder.dayTextView.setText(String.valueOf(calendarModel.getDay()));

        // 출석체크가 완료된 경우 텍스트 색상을 검정색으로, 완료되지 않은 경우 검정색으로 설정
        if (calendarModel.isAttendanceCompleted()) {
            holder.dayTextView.setTextColor(context.getResources().getColor(R.color.btn));
        } else {
            holder.dayTextView.setTextColor(context.getResources().getColor(R.color.black));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView dayTextView;
    }
}