package wang.relish.accumulation.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wang.relish.accumulation.R;
import wang.relish.accumulation.base.BaseFragment;
import wang.relish.accumulation.entity.Record;
import wang.relish.accumulation.ui.activity.RecordActivity;

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
    private TextView tv_no_data;

    @Override
    protected void initViews(View contentView) {
        lv_records = (ListView) contentView.findViewById(R.id.lv_records);
        tv_no_data = (TextView) contentView.findViewById(R.id.tv_no_data);
        records = new ArrayList<>();// TODO Record.findAllUntitled();
        mAdapter = new RecordAdapter();
        lv_records.setAdapter(mAdapter);
        lv_records.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    private void update() {
        records = new ArrayList<>();// TODO Record.findAllUntitled();
        mAdapter.notifyDataSetChanged();
        if(records==null||records.size()==0){
            tv_no_data.setVisibility(View.VISIBLE);
            lv_records.setVisibility(View.GONE);
        }else{
            tv_no_data.setVisibility(View.GONE);
            lv_records.setVisibility(View.VISIBLE);
        }
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
                tv_start_time = (TextView) v.findViewById(R.id.tv_create_time);
            }
        }
    }
}
