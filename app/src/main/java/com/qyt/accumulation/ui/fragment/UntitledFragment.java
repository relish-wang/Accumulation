package com.qyt.accumulation.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qyt.accumulation.R;
import com.qyt.accumulation.base.BaseFragment;
import com.qyt.accumulation.entity.Record;
import com.qyt.accumulation.ui.activity.RecordActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : 王鑫
 *     e-mail : wangxin@souche.com
 *     time   : 2017/05/04
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class UntitledFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    @Override
    protected int layoutId() {
        return R.layout.fragment_untitled;
    }

    private ListView lv_records;
    private RecordAdapter mAdapter;
    private List<Record> records = new ArrayList<>();

    @Override
    protected void initViews(View contentView) {
        lv_records = (ListView) contentView.findViewById(R.id.lv_records);
        records = Record.findAllUntitled();
        mAdapter = new RecordAdapter();
        lv_records.setAdapter(mAdapter);
        lv_records.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        records = Record.findAllUntitled();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RecordActivity.open(getActivity(), records.get(position));
    }

    private class RecordAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return records.size();
        }

        @Override
        public Record getItem(int position) {
            return records.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_record, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Record record = records.get(position);
            holder.tv_name.setText(record.getName());
            holder.tv_start_time.setText(record.getStartTime());
            return convertView;
        }

        class ViewHolder {
            TextView tv_name;
            TextView tv_start_time;

            public ViewHolder(View v) {
                tv_name = (TextView) v.findViewById(R.id.tv_name);
                tv_start_time = (TextView) v.findViewById(R.id.tv_start_time);
            }
        }
    }
}
