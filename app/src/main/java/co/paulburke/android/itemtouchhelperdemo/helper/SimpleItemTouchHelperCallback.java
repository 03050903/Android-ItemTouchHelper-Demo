/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.paulburke.android.itemtouchhelperdemo.helper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

/**
 * An implementation of {@link ItemTouchHelper.Callback} that enables basic drag & drop and
 * swipe-to-dismiss. Drag events are automatically started by an item long-press.<br/>
 * </br/>
 * Expects the <code>RecyclerView.Adapter</code> to listen for {@link
 * ItemTouchHelperAdapter} callbacks and the <code>RecyclerView.ViewHolder</code> to implement
 * {@link ItemTouchHelperViewHolder}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    public static final float ALPHA_FULL = 1.0f;

    private final ItemTouchHelperAdapter mAdapter;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    /**
     * 是否允许长按拖拽
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 是否允许swipe手势
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * 拖拽和滑动的方向设定
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // Enable drag and swipe in both directions
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN; //上下拖拽
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END; //左向右 右向左滑都可以
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 移动item的回调
     * @param recyclerView
     * @param source
     * @param target
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        // Notify the adapter of the move 回调通知adapter item移动了
        mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     * 滑动删除的回调
     * @param viewHolder
     * @param i
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        // Notify the adapter of the dismissal 回调通知adapter item消失了
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    /**
     * 对所有的手势操作需要响应自定义的用户接口，需要重写此方法
     Called by ItemTouchHelper on RecyclerView's onDraw callback.
     * <p>
     * If you would like to customize how your View's respond to user interactions, this is
     * a good place to override.
     * <p>
     * Default implementation translates the child by the given <code>dX</code>,
     * <code>dY</code>.
     * ItemTouchHelper also takes care of drawing the child after other children if it is being
     * dragged. This is done using child re-ordering mechanism. On platforms prior to L, this
     * is
     * achieved via {@link android.view.ViewGroup#getChildDrawingOrder(int, int)} and on L
     * and after, it changes View's elevation value to be greater than all other children.)
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        // Fade out the view as it is swiped out of the parent's bounds
        // 左右滑动的view 根据滑动的距离设置view 的alpha值
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) { //如果是滑动手势
            View itemView = viewHolder.itemView;
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) itemView.getWidth();
            itemView.setAlpha(alpha);
        }
    }

    /**
     * 以下两个方法主要是 设置 ItemTouchHelperViewHolder回调
     * 让每个item选择的时候能改变 背景色
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            // Let the view holder know that this item is being moved or dragged
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemSelected();
            //通知viewHolder该item选中，调用回调onItemSelected
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        viewHolder.itemView.setAlpha(ALPHA_FULL);

        // Tell the view holder it's time to restore the idle state
        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
        itemViewHolder.onItemClear();
    }
}
