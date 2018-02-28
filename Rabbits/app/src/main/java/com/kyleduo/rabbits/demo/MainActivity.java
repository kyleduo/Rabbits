package com.kyleduo.rabbits.demo;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.demo.base.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@Page("/")
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup view = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        setContentView(view);

        List<Section> data = new ArrayList<>();

        data.add(new Section(
                "Standard Usages",
                "/test",
                "/test?param=value",
                "/test/value",
                "demo://rabbits.kyleduo.com/test/value",
                "/test_variety"
        ));
        data.add(new Section("Interceptors", "/test/interceptor", "/test/rules", "/test/interceptor?greenChannel=1", "/test/interceptor?ignore=1"));
        data.add(new Section("Fragment", "/test_fragment"));


        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(new TestAdapter(this, data));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_mappings) {
            Rabbit.from(this).to("/dump").start();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class Section {
        String name;
        List<Item> items;

        Section(String name, String... items) {
            this.name = name;
            this.items = new ArrayList<>();
            for (String item : items) {
                this.items.add(new Item(item));
            }
        }
    }

    static class Item {
        String name;

        Item(String name) {
            this.name = name;
        }
    }

    static class IndexPath {
        int section;
        int index;

        IndexPath(int section, int index) {
            this.section = section;
            this.index = index;
        }

        static IndexPath create(int section, int index) {
            return new IndexPath(section, index);
        }
    }

    static class SectionViewHolder extends RecyclerView.ViewHolder {

        TextView titleTv;

        SectionViewHolder(View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.section_title);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView titleTv;

        ItemViewHolder(View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.item_title);
        }
    }

    static class TestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<Section> mData;
        List<IndexPath> mIndexPaths;
        private WeakReference<MainActivity> mActRef;

        TestAdapter(MainActivity activity, List<Section> data) {
            mActRef = new WeakReference<>(activity);
            mData = data;
            mIndexPaths = new ArrayList<>();
            for (int i = 0; i < mData.size(); i++) {
                mIndexPaths.add(IndexPath.create(i, -1));
                Section s = mData.get(i);
                for (int j = 0; j < s.items.size(); j++) {
                    mIndexPaths.add(IndexPath.create(i, j));
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                View view = LayoutInflater.from(DemoApplication.get()).inflate(R.layout.item_section_header, parent, false);
                return new SectionViewHolder(view);
            } else if (viewType == 1) {
                View view = LayoutInflater.from(DemoApplication.get()).inflate(R.layout.item, parent, false);
                final ItemViewHolder holder = new ItemViewHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();
                        IndexPath indexPath = mIndexPaths.get(position);
                        if (indexPath.index >= 0) {
                            Item item = mData.get(indexPath.section).items.get(indexPath.index);
                            String url = item.name;
                            Rabbit.from(mActRef.get()).to(url).start();
                        }
                    }
                });
                return holder;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);
            IndexPath indexPath = mIndexPaths.get(position);
            if (type == 0) {
                ((SectionViewHolder) holder).titleTv.setText(mData.get(indexPath.section).name);
            } else if (type == 1) {
                ((ItemViewHolder) holder).titleTv.setText(mData.get(indexPath.section).items.get(indexPath.index).name);
            }
        }

        @Override
        public int getItemCount() {
            return mIndexPaths.size();
        }

        @Override
        public int getItemViewType(int position) {
            IndexPath indexPath = mIndexPaths.get(position);
            return indexPath.index == -1 ? 0 : 1;
        }
    }
}
