package com.cytmxk.customview.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import com.cytmxk.customview.R;

/**
 * Created by wb-cy208209 on 2016/8/24.
 */
public class RecycleViewDivider extends RecyclerView.ItemDecoration {

    private static final String TAG = RecycleViewDivider.class.getCanonicalName();
    private Drawable mDivider;

    private LayoutManagerType layoutManagerType;
    private OrientationType orientationType;

    public enum LayoutManagerType {
        UNKNOWN(0), LINEAR(1), GRID(2), STAGGERED_GRID(3);

        private int value;
        LayoutManagerType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static LayoutManagerType valueOf(int value) {
            LayoutManagerType ret = UNKNOWN;

            for (LayoutManagerType layoutManagerType : LayoutManagerType.values()) {
                if (layoutManagerType.getValue() == value) {
                    ret = layoutManagerType;
                    break;
                }
            }

            return ret;
        }
    }

    public enum OrientationType {
        UNKNOWN(0), HORIZONTAL(1), VERTICAL(2), HORIZONTAL_AND_VERTICAL(3);

        private int value;
        OrientationType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static OrientationType valueOf(int value) {
            OrientationType ret = UNKNOWN;

            for (OrientationType orientationType : OrientationType.values()) {
                if (orientationType.getValue() == value) {
                    ret = orientationType;
                    break;
                }
            }

            return ret;
        }
    }

    public RecycleViewDivider(Context context, LayoutManagerType layoutManagerType, OrientationType orientationType) {
        mDivider = context.getResources().getDrawable(R.drawable.recycler_view_divider);

        switch (layoutManagerType) {
            case LINEAR:
                this.layoutManagerType = LayoutManagerType.LINEAR;
                break;
            case GRID:
                this.layoutManagerType = LayoutManagerType.GRID;
                break;
            case STAGGERED_GRID:
                this.layoutManagerType = LayoutManagerType.STAGGERED_GRID;
                break;
            case UNKNOWN:
                throw new IllegalArgumentException("UNKNOWN LayoutManagerType");
        }

        if (this.layoutManagerType == LayoutManagerType.GRID) {
            this.orientationType = OrientationType.HORIZONTAL_AND_VERTICAL;
            return;
        }

        switch (orientationType) {
            case HORIZONTAL:
                this.orientationType = OrientationType.HORIZONTAL;
                break;
            case VERTICAL:
                this.orientationType = OrientationType.VERTICAL;
                break;
            case UNKNOWN:
                throw new IllegalArgumentException("UNKNOWN OrientationType");
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        if (layoutManagerType == LayoutManagerType.LINEAR) {
            if (orientationType == OrientationType.VERTICAL) {
                drawLinearHorizontal(c, parent);
            } else if (orientationType == OrientationType.HORIZONTAL) {
                drawLinearVertical(c, parent);
            }
        } else if (layoutManagerType == LayoutManagerType.GRID) {
            drawGridHorizontal(c, parent);
            drawGridVertical(c, parent);
        } else if (layoutManagerType == LayoutManagerType.STAGGERED_GRID) {
            drawStaggeredGridHorizontal(c, parent);
            drawStaggeredGridVertical(c, parent);
        }
    }


    private void drawLinearHorizontal(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        if (childCount <= 0) {
            return;
        }
        for (int i = 0; i < childCount -1 ; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawLinearVertical(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        if (childCount <= 0) {
            return;
        }
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private int getSpanCount(RecyclerView parent) {

        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager)layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager)layoutManager).getSpanCount();
        }

        return spanCount;
    }

    private void drawGridHorizontal(Canvas c, RecyclerView parent) {

        int childCount = parent.getChildCount();
        GridLayoutManager.SpanSizeLookup spanSizeLookup = null;
        int spanSizeCount = 0;
        spanSizeLookup = ((GridLayoutManager) parent.getLayoutManager()).getSpanSizeLookup();
        int spanCount = getSpanCount(parent);
        for (int i = 0; i < childCount; i++) {
            spanSizeCount += spanSizeLookup.getSpanSize(i);
            if (spanSizeCount / spanCount > 0) {
                View child = parent.getChildAt(i - 1);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();
                int top = child.getBottom() + layoutParams.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
                spanSizeCount = spanSizeLookup.getSpanSize(i);
            }
        }
    }

    private void drawGridVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0;i < childCount; i++) {
            View child = parent.getChildAt(i);

            // 最后一列的右边不用绘制分隔线
            int itemPosition = ((RecyclerView.LayoutParams) child.getLayoutParams()).getViewLayoutPosition();
            int spanCount = getSpanCount(parent);
            if (isLastCol(parent, itemPosition, spanCount, childCount)) {
                continue;
            }

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + layoutParams.rightMargin;
            int right = left + mDivider.getIntrinsicWidth();
            int top = child.getTop() - layoutParams.topMargin;
            int bottom = child.getBottom() + layoutParams.bottomMargin;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawStaggeredGridHorizontal(Canvas c, RecyclerView parent) {

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            // 最后一行的底边不用绘制分隔线
            int itemPosition = ((RecyclerView.LayoutParams) child.getLayoutParams()).getViewLayoutPosition();
            int spanCount = getSpanCount(parent);
            if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
                continue;
            }

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getLeft() - layoutParams.leftMargin;
            int right = child.getRight() + layoutParams.rightMargin + mDivider.getIntrinsicWidth();
            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawStaggeredGridVertical(Canvas c, RecyclerView parent) {
        drawGridVertical(c, parent);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (layoutManagerType == LayoutManagerType.LINEAR) {
            int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
            int childCount = parent.getAdapter().getItemCount();
            if (orientationType == OrientationType.VERTICAL) {
                boolean isLastRow = (itemPosition + 1) == childCount;
                if (!isLastRow) {
                    outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
                }
            } else if (orientationType == OrientationType.HORIZONTAL) {
                boolean isLastCol = (itemPosition + 1) == childCount;
                if (!isLastCol) {
                    outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
                }
            }
        } else if (layoutManagerType == LayoutManagerType.GRID || layoutManagerType == LayoutManagerType.STAGGERED_GRID) {
            int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
            int spanCount = getSpanCount(parent);
            int childCount = parent.getAdapter().getItemCount();

            boolean isLastRow = isLastRaw(parent, itemPosition, spanCount, childCount);
            boolean isLastCol = isLastCol(parent, itemPosition, spanCount, childCount);
            if (isLastRow && isLastCol) {
                outRect.set(0, 0, 0, 0);
                return;
            }

            if (isLastRow) { // 最后一行的底边不用分配绘制分隔线的区域
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
                return;
            }

            if (isLastCol) { // 最后一列的右边不用分配绘制分隔线的区域
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
                return;
            }

            outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
        }
    }

    private boolean isLastCol(RecyclerView parent, int pos, int spanCount,
                                int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int posCol = getGridCol(parent, pos);
            if (posCol == spanCount) {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int posRow = getGridRow(parent, pos);
            int lineCount = getGridRow(parent, childCount - 1);
            if (posRow == lineCount) {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)
                    return true;
            } else {
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getGridRow(RecyclerView parent, int position) {
        GridLayoutManager.SpanSizeLookup spanSizeLookup = null;
        int row = 1;
        int spanSizeCount = 0;
        spanSizeLookup = ((GridLayoutManager) parent.getLayoutManager()).getSpanSizeLookup();
        int spanCount = getSpanCount(parent);
        for (int i = 0; i <= position; i++) {
            spanSizeCount += spanSizeLookup.getSpanSize(i);
            if (spanSizeCount / spanCount > 0) {
                if (spanSizeCount % spanCount == 0) {
                    spanSizeCount = 0;
                } else {
                    spanSizeCount = spanSizeLookup.getSpanSize(i);
                }
                row ++;
            }
        }
        if (spanSizeCount == 0) {
            row --;
        }
        return row;
    }

    private int getGridCol(RecyclerView parent, int position) {
        GridLayoutManager.SpanSizeLookup spanSizeLookup = null;
        int spanSizeCount = 0;
        spanSizeLookup = ((GridLayoutManager) parent.getLayoutManager()).getSpanSizeLookup();
        int spanCount = getSpanCount(parent);
        for (int i = 0; i <= position; i++) {
            spanSizeCount += spanSizeLookup.getSpanSize(i);
            if (spanSizeCount / spanCount > 0) {
                if (spanSizeCount % spanCount == 0) {
                    spanSizeCount = 0;
                } else {
                    spanSizeCount = spanSizeLookup.getSpanSize(i);
                }
            }
        }
        if (spanSizeCount == 0) {
            spanSizeCount = spanCount;
        }
        return spanSizeCount;
    }
}
