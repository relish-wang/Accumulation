package wang.relish.accumulation.ui.activity.search;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wang.relish.accumulation.R;
import wang.relish.accumulation.base.OnTextChangedListener;
import wang.relish.accumulation.network.RetrofitManager;
import wang.relish.accumulation.network.SearchService;

/**
 * 文本框输入文字每1s内未连续输入则触发搜索
 *
 * @author Relish Wang
 * @since 2018/03/31
 */
public class SearchActivity extends Activity {

    public static final int WHAT = 0x623;
    private EditText mEtSearch;

    private List<String> mList = new ArrayList<>();
    private RecyclerView mSearchResult;
    private RvAdapter mAdapter;

    private static SearchHandler sHandler = new SearchHandler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mEtSearch = findViewById(R.id.et_search);
        mEtSearch.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                Message message = Message.obtain();
                message.what = WHAT;
                message.obj = text;
                sHandler.removeMessages(WHAT);// 先移除
                sHandler.sendMessageDelayed(message, 1000);//再发送
            }
        });

        mSearchResult = findViewById(R.id.rv);
        mAdapter = new RvAdapter();
        mSearchResult.setAdapter(mAdapter);
        mSearchResult.setLayoutManager(new LinearLayoutManager(this));
    }

    private static Call<List<String>> call;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String message = event.message.trim();
        if (call!=null &&!call.isExecuted()){
            call.cancel();
        }
        call = RetrofitManager.getRetrofit().create(SearchService.class).listRepos(message);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                List<String> body = response.body();
                if (body == null) return;
                mList.clear();
                mList.addAll(body);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    class RvAdapter extends RecyclerView.Adapter<RvAdapter.VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.tv.setText(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class VH extends RecyclerView.ViewHolder {

            TextView tv;

            VH(View itemView) {
                super(itemView);
                tv = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
