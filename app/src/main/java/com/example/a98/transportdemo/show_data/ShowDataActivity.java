package com.example.a98.transportdemo.show_data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.R;
import com.google.gson.Gson;

import org.xutils.view.annotation.Event;
import org.xutils.x;

import java.util.List;

public class ShowDataActivity extends BaseActivity {
    private Context mContext;
    private ItemDragAdapter mAdapter;
//
//    @ViewInject(R.id.map)
    private  List<ShowData>datalist;
    @Event(R.id.btn_finish)
    private void finish_activity(View v) {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdata);
        x.view().inject(this);
        mContext= ShowDataActivity.this;
        init();
    }

    private void init() {
        set_adapter();
    }
    private void set_adapter(){
        datalist = getShowData();
        OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {}
            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {}
            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                log("删除");
                log(pos);
                datalist.remove(pos);
                updateShowData(datalist);

            }
            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
            }
        };
        mAdapter = new ItemDragAdapter(datalist);
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_showitem);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

// 开启滑动删除
        mAdapter.enableSwipeItem();
        mAdapter.setOnItemSwipeListener(onItemSwipeListener);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                log(position);
                Intent intent=new Intent(ShowDataActivity.this,DataDetailActivity.class);
                intent.putExtra("data_detail",new Gson().toJson(datalist.get(position)));
                startActivity(intent);
            }
        });

    }
    public class ItemDragAdapter extends BaseItemDraggableAdapter<ShowData, BaseViewHolder> {
        public ItemDragAdapter(List datalist) {

            super(R.layout.item_show_data ,datalist);


        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, ShowData item) {
            helper.setText(R.id.item_title, item.title);
            helper.setText(R.id.item_desc, item.desc);
//            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
//            ImageView.setImageUri(Uri.fromFile(new File("/sdcard/test.jpg")));
//            helper.setImageBitmap(R.id.photo, item.photo);

        }
    }
}
