package com.m5.android.avicola;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.m5.android.avicola.adapters.MainAdapter;
import com.m5.android.avicola.model.Advert;
import com.m5.android.avicola.model.AdvertWrapper;
import com.m5.android.avicola.model.Content;

import java.util.List;

public class ListFragment extends Fragment {

    public interface ListFragmentInterface {
        public void onContentItemSelected(Content item);
        public void onAdvertItemSelected(Advert item);
    }

    public static final String TAG = "ListFragment";

    private ListView list;
    private MainAdapter adapter;

    private TextView info;
    private ListFragmentInterface activityInterface;

    public ListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ListFragmentInterface) {
            activityInterface = (ListFragmentInterface) activity;
        }
        else {
            throw new IllegalArgumentException("Activity using ListFragment have to implement from ListFragmentInterface.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        list = (ListView) rootView.findViewById(R.id.list);
        adapter = new MainAdapter(getActivity());
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final MainAdapter.ItemHelper itemHelper = (MainAdapter.ItemHelper) adapter.getItem(i);
                switch (itemHelper.listType) {
                    case TOP_TEASER:
                    case CONTENT:
                        activityInterface.onContentItemSelected((Content)itemHelper.item);
                        break;
                    case AD:
                        activityInterface.onAdvertItemSelected((Advert)itemHelper.item);
                        break;
                }
            }
        });

        info = (TextView) rootView.findViewById(R.id.info);
        return rootView;
    }

    public void showData(final Content.Type type, final Content.Territory territory, final String search, final Content[] content, final AdvertWrapper advertWrapper) {
        //first clear the previous refined items so that we keep the original order of items from the feed
        adapter.clearItems();
        list.post(new Runnable() {
            @Override
            public void run() {
                info.setVisibility(View.GONE);
                adapter.setItems(type, territory, search, content, advertWrapper, new MainAdapter.OnNoItemsListener() {
                    @Override
                    public void onNoItems() {
                        ListFragment.this.onNoItems();
                    }
                });
            }
        });
    }

    public void showData(List<Content> content) {
        adapter.setItems(content, new MainAdapter.OnNoItemsListener() {
            @Override
            public void onNoItems() {
                ListFragment.this.onNoItems();
            }
        });
    }

    public void onNoItems() {
        info.setText(R.string.info_no_items);
        info.setVisibility(View.VISIBLE);
    }
}
