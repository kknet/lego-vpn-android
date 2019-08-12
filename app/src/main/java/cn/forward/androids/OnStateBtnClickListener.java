package cn.forward.androids;

import android.view.View;

public class OnStateBtnClickListener implements View.OnClickListener {
    private PluginListAdapter mAdapter;

    @Override
    public void onClick(final View v) {
        PluginListAdapter.PluginItem item = (PluginListAdapter.PluginItem) v.getTag();
        if (item == null) {
            return;
        }
        if (mAdapter.isAdded(item)) { // 已添加
            mAdapter.removeItem(item);
        } else {
            mAdapter.addItem(item);
        }
    }
}
