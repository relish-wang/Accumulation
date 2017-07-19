package wang.relish.accumulation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import wang.relish.accumulation.R;
import wang.relish.accumulation.entity.Goal;

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
public class GoalsAdapter extends BaseAdapter {


    List<Goal> goals = new ArrayList<>();

    public GoalsAdapter(List<Goal> goals) {
        this.goals = goals;
    }

    @Override
    public int getCount() {
        return goals.size();
    }

    @Override
    public Goal getItem(int position) {
        return goals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(goals.get(position).getName());
        return convertView;
    }

    private class ViewHolder {
        TextView tv_name;

        ViewHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.tv_name);
        }
    }
}