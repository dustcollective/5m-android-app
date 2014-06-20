package com.m5.android.avicola.adapters;

import android.content.Context;
import android.graphics.Point;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hanacek.android.utilLib.ui.view.PresetSizeImageView;
import com.m5.android.avicola.R;
import com.m5.android.avicola.app.AppContext;
import com.m5.android.avicola.model.AdvertWrapper;
import com.m5.android.avicola.model.Content;
import com.m5.android.avicola.model.ListItemInterface;
import com.m5.android.avicola.util.Cfg;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends BaseAdapter {

    public interface OnNoItemsListener {
        public void onNoItems();
    }

    private Context context;
    private List<ItemHelper> items;

    public MainAdapter(Context context) {
        this.context = context;
    }

    public void setItems(Content.Type type, Content.Territory territory, String search, Content[] content, AdvertWrapper advertWrapper, OnNoItemsListener onNoItems) {
        items = new ArrayList<ItemHelper>();
        int i = advertWrapper.inlineFrequency;
        int adsCount = 0;
        for (Content contentItem : content) {
            if (contentItem.type == Content.Type.HELP) {
                continue;
            }

            //type
            if (type != Content.Type.ALL && type != contentItem.type) {
                continue;
            }

            //search
            if (!TextUtils.isEmpty(search)) {
                boolean found = false;
                if (contentItem.headline != null && contentItem.headline.indexOf(search) != -1) {
                    found = true;
                }
                if (contentItem.body != null && contentItem.body.indexOf(search) != -1) {
                    found = true;
                }
                if (contentItem.snippet != null && contentItem.snippet.indexOf(search) != -1) {
                    found = true;
                }
                if (!found) {
                    continue;
                }
            }

            //territory
            if (territory != Content.Territory.ALL && (contentItem.territory == null || territory != contentItem.territory)) {
                continue;
            }

            items.add(new ItemHelper(contentItem, ItemHelper.ListType.CONTENT));
            if (--i < 1 && adsCount < advertWrapper.inline.length) {
                items.add(new ItemHelper(advertWrapper.inline[adsCount++], ItemHelper.ListType.AD));
                i = advertWrapper.inlineFrequency;
            }

            if (i == 0) {
                items.get(i).listType = ItemHelper.ListType.TOP_TEASER;
            }
        }

        if (items.size() == 0) {
            onNoItems.onNoItems();
        }

        notifyDataSetChanged();
    }

    public void setItems(List<Content> content, OnNoItemsListener onNoItems) {
        items = new ArrayList<ItemHelper>();
        for (Content contentItem : content) {
            items.add(new ItemHelper(contentItem, ItemHelper.ListType.CONTENT));
        }

        if (items.size() == 0) {
            onNoItems.onNoItems();
        }

        notifyDataSetChanged();
    }

    public void clearItems() {
        this.items = null;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (items == null) ? 0 : items.size();
    }

    @Override
    public Object getItem(int i) {
        return (items == null || i >= items.size()) ? null : items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    abstract private static class Holder {
        public PresetSizeImageView image;
        public ViewGroup root;

        abstract protected int getLayoutId();

        public View createView(Context context) {
            root = (ViewGroup) LayoutInflater.from(context).inflate(getLayoutId(), null);
            image = (PresetSizeImageView) root.findViewById(R.id.image);
            return root;
        }

        public void showData(ListItemInterface item) {
            final String imageUrl = item.getImageUrl();
            final Point size = getImageSize();
            if (size != null) {
                image.presetDimensions(size.x, size.y);
            }
            if (TextUtils.isEmpty(imageUrl)) {
                image.setBackgroundColor(AppContext.context().getResources().getColor(android.R.color.transparent));
                final int imgRes = (item.getType() == Content.Type.NEWS) ? R.drawable.news_calendar : R.drawable.event_calendar ;
                image.setImageResource(imgRes);
            }
            else {
                image.setBackgroundColor(AppContext.context().getResources().getColor(android.R.color.black));
                AppContext.imageCache().displayImage(item.getImageUrl(), image);
            }
        }

        protected Point getImageSize() {
            int imageWidth = AppContext.context().getResources().getDimensionPixelOffset(R.dimen.listImageWidth);
            return new Point(imageWidth, (int)(Cfg.IMAGE_HEIGHT_RATIO*imageWidth));
        }
    }

    private static class AdHolder extends Holder {
        @Override
        protected int getLayoutId() {
            return R.layout.list_item_ad;
        }

        @Override
        protected Point getImageSize() {
            int imageWidth = AppContext.getDisplayWidth();
            return new Point(imageWidth, (int)(0.18*imageWidth));
        }
    }

    private static class ContentHolder extends Holder {
        public TextView headline;
        public TextView date;

        @Override
        protected int getLayoutId() {
            return R.layout.list_item_content;
        }

        @Override
        public View createView(Context context) {
            super.createView(context);
            headline = (TextView) root.findViewById(R.id.headline);
            date = (TextView) root.findViewById(R.id.date);
            return root;
        }

        @Override
        public void showData(ListItemInterface item) {
            super.showData(item);
            headline.setText(Html.fromHtml(item.getHeadline()));
            date.setText(item.getTeaser());
        }
    }

    private static class TopTeaserHolder extends ContentHolder {
        @Override
        protected int getLayoutId() {
            return R.layout.list_item_top_teaser;
        }

        @Override
        protected Point getImageSize() {
            int imageWidth = AppContext.getDisplayWidth();
            return new Point(imageWidth, (int)(0.75357*imageWidth));
        }
    }

    @Nullable
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder = null;
        if (view == null) {
            switch (getItemViewType(i)) {
                case 0:
                    holder = new AdHolder();
                    break;
                case 1:
                    holder = new ContentHolder();
                    break;
                case 2:
                    holder = new TopTeaserHolder();
                    break;
            }
            view = holder.createView(context);
            view.setTag(holder);
        }
        else {
            holder = (Holder) view.getTag();
        }

//        switch (getItemViewType(i)) {
//            case 1:
//                final ViewGroup root = holder.root;
//                final ImageView button = (ImageView) root.findViewById(R.id.button_image);
//                Log.debug("pos " + i);
//                if (button != null) {
//                    root.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            button.getLayoutParams().height = root.getMeasuredHeight() - root.getPaddingBottom() - root.getPaddingTop();
//                            Log.debug("pos measured " + button.getLayoutParams().height);
//                        }
//                    });
//                }
//                break;
//        }

        holder.showData(((ItemHelper) getItem(i)).item);
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        final ItemHelper itemHelper = (ItemHelper) getItem(position);
        switch (itemHelper.listType) {
            case AD:
                return 0;
            case CONTENT:
                return 1;
            case TOP_TEASER:
                return 2;
        }
        throw new IllegalArgumentException("Unknown type");
    }

    @Override
    /**
     * Ad or Content
     */
    public int getViewTypeCount() {
        return 3;
    }

    public static class ItemHelper {
        public enum ListType {CONTENT, AD, TOP_TEASER}
        public ListItemInterface item;
        public ListType listType;

        public ItemHelper(ListItemInterface item, ListType listType){
            this.item = item;
            this.listType = listType;
        }
    }
}
