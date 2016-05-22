package github.com.pwalan.dfood.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * 与ListView相关的工具
 */
public class ListViewUtils {

    /**
     * 动态扩展ListView的高度
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            if(i<1){
                //多出的高度作为预留空间
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }
}
