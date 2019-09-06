package com.example.a98.transportdemo.show_data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ShowDataActivity extends BaseActivity {
    private Context mContext;
    private ItemDragAdapter mAdapter;
    //
//    @ViewInject(R.id.map)
    private List<HashMap<String,String>> datalist;

    @Event(R.id.btn_finish)
    private void finish_activity(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdata);
        x.view().inject(this);
        init();
    }

    private void init() {
        set_adapter();
    }

    private void set_adapter() {
        datalist = getShowData();
        System.out.println(datalist);
        OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
                log(pos);

            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int  pos) {
                try {
                    updateShowData(datalist);
                    mAdapter.notifyDataSetChanged();
                }
                catch (Exception e){
                    log(e.toString());
                }
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
        itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START | ItemTouchHelper.END);

// 开启滑动删除

        mAdapter.enableSwipeItem();
        mAdapter.setOnItemSwipeListener(onItemSwipeListener);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                log(position);
                Intent intent = new Intent(ShowDataActivity.this, DataDetailActivity.class);
                intent.putExtra("show_detail", new Gson().toJson(datalist.get(position)));
                log(datalist.get(position));
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    public class ItemDragAdapter extends BaseItemDraggableAdapter<HashMap<String,String>, BaseViewHolder> {
        public ItemDragAdapter(List datalist) {

            super(R.layout.item_show_data, datalist);


        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, HashMap<String,String> item) {
            helper.setText(R.id.item_title, item.get("名称"));
            if(item.containsKey("经度")) {
                helper.setText(R.id.item_desc, "经度"+ item.get("经度") +" 纬度"+ item.get("纬度"));
            }
            if(item.containsKey("图片")) {
                File file = new File(item.get("图片"));
                Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
                helper.setImageBitmap(R.id.item_photo, bitmap);
            }

        }
    }
}
