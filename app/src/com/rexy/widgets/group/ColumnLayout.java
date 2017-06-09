package com.rexy.widgets.group;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;

import com.rexy.widgetlayout.R;

import java.util.regex.Pattern;

/**
 * <!--列个数-->
 * <attr name="columnNumber" format="integer" />
 * <!--列内内容全展开的索引 * 或 1,3,5 类似列索引0 开始-->
 * <attr name="stretchColumns" format="string" />
 * <!--列内内容全靠中间 * 或 1,3,5 类似列索引0 开始-->
 * <attr name="alignCenterColumns" format="string" />
 * <!--列内内容全靠右 * 或 1,3,5 类似列索引0 开始-->
 * <attr name="alignRightColumns" format="string" />
 * <p>
 * <!--每行内容垂直居中-->
 * <attr name="columnCenterVertical" format="boolean"/>
 * <p>
 * <!--列宽和高的最大最小值限定-->
 * <attr name="columnMinWidth" format="dimension" />
 * <attr name="columnMaxWidth" format="dimension" />
 * <attr name="columnMinHeight" format="dimension" />
 * <attr name="columnMaxHeight" format="dimension" />
 *
 * @author: rexy
 * @date: 2015-11-27 17:43
 */
public class ColumnLayout extends PressViewGroup {
    //列个数-
    int mColumnNumber = 1;
    //列内内容全展开的索引 * 或 1,3,5 类似列索引0 开始
    SparseBooleanArray mStretchColumns;
    //列内内容全靠中间 * 或 1,3,5 类似列索引0 开始
    SparseBooleanArray mAlignCenterColumns;
    //列内内容全靠右 * 或 1,3,5 类似列索引0 开始
    SparseBooleanArray mAlignRightColumns;

    //列的最小宽和高限定。
    int mColumnMinWidth = -1;
    int mColumnMaxWidth = -1;
    int mColumnMinHeight = -1;
    int mColumnMaxHeight = -1;
    boolean mColumnCenterVertical = true;

    private boolean mStretchAllColumns;
    private boolean mAlignCenterAllColumns;
    private boolean mAlignRightAllColumns;
    private int mColumnWidth;
    private SparseIntArray mLineHeight = new SparseIntArray(2);
    private SparseIntArray mLineLastIndex = new SparseIntArray(2);

    public ColumnLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ColumnLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ColumnLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ColumnLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mDividerMargin = DividerMargin.from(context, attrs);
        TypedArray attr = attrs == null ? null : context.obtainStyledAttributes(attrs, R.styleable.ColumnLayout);
        if (attr != null) {
            mColumnNumber = attr.getInt(R.styleable.ColumnLayout_columnNumber, mColumnNumber);
            mColumnMinWidth = attr.getDimensionPixelSize(R.styleable.ColumnLayout_columnMinWidth, mColumnMinWidth);
            mColumnMaxWidth = attr.getDimensionPixelSize(R.styleable.ColumnLayout_columnMaxWidth, mColumnMaxWidth);
            mColumnMinHeight = attr.getDimensionPixelSize(R.styleable.ColumnLayout_columnMinHeight, mColumnMinHeight);
            mColumnMaxHeight = attr.getDimensionPixelSize(R.styleable.ColumnLayout_columnMaxHeight, mColumnMaxHeight);
            mColumnCenterVertical = attr.getBoolean(R.styleable.ColumnLayout_columnCenterVertical, mColumnCenterVertical);
            String stretchableColumns = attr.getString(R.styleable.ColumnLayout_stretchColumns);
            if (stretchableColumns != null) {
                if (stretchableColumns.contains("*")) {
                    mStretchAllColumns = true;
                } else {
                    mStretchColumns = parseColumns(stretchableColumns);
                }
            }

            String alignCenterColumns = attr.getString(R.styleable.ColumnLayout_alignCenterColumns);
            if (alignCenterColumns != null) {
                if (alignCenterColumns.contains("*")) {
                    mAlignCenterAllColumns = true;
                } else {
                    mAlignCenterColumns = parseColumns(alignCenterColumns);
                }
            }

            String alignRightColumns = attr.getString(R.styleable.ColumnLayout_alignRightColumns);
            if (alignRightColumns != null) {
                if (alignRightColumns.contains("*")) {
                    mAlignRightAllColumns = true;
                } else {
                    mAlignRightColumns = parseColumns(alignRightColumns);
                }
            }
            attr.recycle();
        }
    }

    private static SparseBooleanArray parseColumns(String sequence) {
        SparseBooleanArray columns = new SparseBooleanArray();
        Pattern pattern = Pattern.compile("\\s*,\\s*");
        String[] columnDefs = pattern.split(sequence);
        for (String columnIdentifier : columnDefs) {
            try {
                int columnIndex = Integer.parseInt(columnIdentifier);
                if (columnIndex >= 0) {
                    columns.put(columnIndex, true);
                }
            } catch (NumberFormatException e) {
            }
        }
        return columns;
    }


    private int computeColumnWidth(int selfWidthNoPadding, int middleMarginHorizontal, int columnCount) {
        if (middleMarginHorizontal > 0) {
            selfWidthNoPadding -= (middleMarginHorizontal * (columnCount - 1));
        }
        selfWidthNoPadding = selfWidthNoPadding / columnCount;
        if (mColumnMaxWidth > 0 && selfWidthNoPadding > mColumnMaxWidth) {
            selfWidthNoPadding = mColumnMaxWidth;
        }
        if (selfWidthNoPadding < mColumnMinWidth) {
            selfWidthNoPadding = mColumnMinWidth;
        }
        return Math.max(selfWidthNoPadding, 0);
    }

    private int computeColumnHeight(int measureHeight) {
        if (mColumnMaxHeight > 0 && measureHeight > mColumnMaxHeight) {
            measureHeight = mColumnMaxHeight;
        }
        if (measureHeight < mColumnMinHeight) {
            measureHeight = mColumnMinHeight;
        }
        return measureHeight;
    }

    private void adjustMeasureAndSave(int lineIndex, int endIndex, int columnHeight, int columnCount) {
        mLineHeight.put(lineIndex, columnHeight);
        mLineLastIndex.put(lineIndex, endIndex);
        for (int columnIndex = columnCount - 1; columnIndex >= 0 && endIndex >= 0; endIndex--) {
            final View child = getChildAt(endIndex);
            if (skipChild(child)) continue;
            if (isColumnStretch(columnIndex)) {
                ColumnLayout.LayoutParams params = (LayoutParams) child.getLayoutParams();
                int childMarginVertical = params.getMarginVertical();
                int childHeightWithMargin = child.getMeasuredHeight() + childMarginVertical;
                if (childHeightWithMargin != columnHeight) {
                    params.measure(child
                            , MeasureSpec.makeMeasureSpec(child.getMeasuredWidth(), MeasureSpec.EXACTLY)
                            , MeasureSpec.makeMeasureSpec(Math.max(0, columnHeight - childMarginVertical), MeasureSpec.EXACTLY));
                }
            }
            columnIndex--;
        }
    }

    @Override
    protected void dispatchMeasure(int widthMeasureSpecNoPadding, int heightMeasureSpecNoPadding, int maxSelfWidthNoPadding, int maxSelfHeightNoPadding) {
        final int childCount = getChildCount();
        final int columnCount = Math.max(1, mColumnNumber);
        final int middleMarginHorizontal = mDividerMargin.getMiddleMarginHorizontal();
        final int middleMarginVertical = mDividerMargin.getMiddleMarginVertical();
        final int contentMarginHorizontal = mDividerMargin.getContentMarginLeft() + mDividerMargin.getContentMarginRight();
        final int contentMarginVertical = mDividerMargin.getContentMarginTop() + mDividerMargin.getContentMarginBottom();
        mLineHeight.clear();
        mColumnWidth = computeColumnWidth(maxSelfWidthNoPadding-contentMarginHorizontal, middleMarginHorizontal, columnCount);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxSelfHeightNoPadding-contentMarginVertical,MeasureSpec.getMode(heightMeasureSpecNoPadding));
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(mColumnWidth, MeasureSpec.getMode(widthMeasureSpecNoPadding));
        int currentLineMaxHeight = 0;
        int contentHeight = 0, childState = 0, measuredCount = 0;
        int lineIndex, preLineIndex = 0, columnIndex = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (skipChild(child)) continue;
            lineIndex = measuredCount / columnCount;
            if (lineIndex != preLineIndex) {
                currentLineMaxHeight = computeColumnHeight(currentLineMaxHeight);
                contentHeight += currentLineMaxHeight;
                adjustMeasureAndSave(preLineIndex, i - 1, currentLineMaxHeight, columnCount);
                preLineIndex = lineIndex;
                columnIndex = 0;
                currentLineMaxHeight = 0;
                if (middleMarginVertical > 0) {
                    contentHeight += middleMarginVertical;
                }
            }
            ColumnLayout.LayoutParams params = (LayoutParams) child.getLayoutParams();
            boolean stretchMeasure = isColumnStretch(columnIndex);
            int marginHorizontal = params.getMarginHorizontal();
            int marginVertical = params.getMarginVertical();
            int childMeasureSpecWidth=getChildMeasureSpec(widthMeasureSpec,marginHorizontal,stretchMeasure?-1:params.width);
            int childMeasureSpecHeight=getChildMeasureSpec(heightMeasureSpec,marginVertical+contentHeight,params.height);
            params.measure(child,childMeasureSpecWidth,childMeasureSpecHeight);
            childState = childCount | child.getMeasuredState();
            int childHeightWithMargin = child.getMeasuredHeight() + marginHorizontal;
            if (currentLineMaxHeight < childHeightWithMargin) {
                currentLineMaxHeight = childHeightWithMargin;
            }
            measuredCount++;
            columnIndex++;
        }
        if (childCount > 0 && currentLineMaxHeight > 0) {
            currentLineMaxHeight = computeColumnHeight(currentLineMaxHeight);
            contentHeight += currentLineMaxHeight;
            adjustMeasureAndSave(preLineIndex, childCount - 1, currentLineMaxHeight, columnIndex);
        }
        int contentWidth = mColumnWidth * columnCount + (middleMarginHorizontal <= 0 ? 0 : (middleMarginHorizontal * (columnCount - 1)));
        setContentSize(contentWidth+contentMarginHorizontal, contentHeight+contentMarginVertical);
        setMeasureState(childState);
    }

    private int getAlignHorizontalGravity(int columnIndex, int defaultGravity) {
        if (isColumnAlignCenter(columnIndex)) {
            defaultGravity = Gravity.CENTER_HORIZONTAL;
        } else if (isColumnAlignRight(columnIndex)) {
            defaultGravity = Gravity.RIGHT;
        }
        return defaultGravity;
    }

    @Override
    protected void dispatchLayout(int contentLeft, int contentTop, int paddingLeft, int paddingTop, int selfWidth, int selfHeight) {
        final int lineCount = mLineHeight.size();
        final int columnWidth = mColumnWidth;
        final int middleMarginHorizontal = mDividerMargin.getMiddleMarginHorizontal();
        final int middleMarginVertical = mDividerMargin.getMiddleMarginVertical();
        final int contentMarginLeft=mDividerMargin.getContentMarginLeft();
        final int contentMarginTop=mDividerMargin.getContentMarginTop();

        int childIndex = 0, childLastIndex, columnIndex;
        int columnLeft, columnTop = contentTop+contentMarginTop, columnRight, columnBottom;
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            columnIndex = 0;
            childLastIndex = mLineLastIndex.get(lineIndex);
            columnLeft = contentLeft+contentMarginLeft;
            columnBottom = columnTop + mLineHeight.get(lineIndex);
            for (; childIndex <= childLastIndex; childIndex++) {
                final View child = getChildAt(childIndex);
                if (skipChild(child)) continue;
                ColumnLayout.LayoutParams params = (LayoutParams) child.getLayoutParams();
                columnRight = columnLeft + columnWidth;
                int gravityHorizontal = getAlignHorizontalGravity(columnIndex, params.gravity);
                int gravityVertical = mColumnCenterVertical ? Gravity.CENTER_VERTICAL : params.gravity;
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                int childLeft = getContentStartH(columnLeft, columnRight, childWidth, params.leftMargin, params.rightMargin, gravityHorizontal);
                int childTop = getContentStartV(columnTop, columnBottom, childHeight, params.topMargin, params.bottomMargin, gravityVertical);
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                columnLeft = columnRight;
                if (middleMarginHorizontal > 0) {
                    columnLeft += middleMarginHorizontal;
                }
                columnIndex++;
            }
            childIndex = childLastIndex + 1;
            columnTop = columnBottom;
            if (middleMarginVertical > 0) {
                columnTop += middleMarginVertical;
            }
        }
    }

    @Override
    protected void dispatchDrawAfter(Canvas canvas) {
        final int lineCount = mLineHeight.size();
        boolean dividerHorizontal = mDividerMargin.isVisibleDividerHorizontal(true) && lineCount > 1;
        boolean dividerVertical = mDividerMargin.isVisibleDividerVertical(true) && mColumnNumber > 1;
        if (dividerHorizontal || dividerVertical) {
            final int columnWidth = mColumnWidth;
            final int middleMarginHorizontal = mDividerMargin.getMiddleMarginHorizontal();
            final int middleMarginVertical = mDividerMargin.getMiddleMarginVertical();
            final int contentMarginLeft=mDividerMargin.getContentMarginLeft();
            final int contentMarginTop=mDividerMargin.getContentMarginTop();
            final int contentMarginBottom=mDividerMargin.getContentMarginBottom();
            int parentLeft = getPaddingLeft(), parentRight = getWidth() - getPaddingRight(),parentBottom=getHeight()-getPaddingBottom();
            int contentLeft = getContentLeft();
            int mMaxColumnIndex = Math.max(mColumnNumber - 1, 0), mColumnIndex;
            int childIndex = 0, childLastIndex;
            int columnLeft, columnTop = getContentTop()+contentMarginTop, columnRight, columnBottom;
            int halfMiddleVertical=middleMarginVertical>0?(middleMarginVertical/2):0;
            boolean bottomCoincide=false;
            for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
                childLastIndex = mLineLastIndex.get(lineIndex);
                columnLeft = contentLeft+contentMarginLeft;
                columnBottom = columnTop + mLineHeight.get(lineIndex)+halfMiddleVertical;
                if (dividerHorizontal) {
                    if(lineIndex<lineCount-1||(bottomCoincide=(columnBottom+contentMarginBottom)<parentBottom)){
                        mDividerMargin.drawDividerH(canvas, parentLeft, parentRight, columnBottom);
                    }
                }
                if (dividerVertical) {
                    mColumnIndex = 0;
                    int dividerTop=columnTop-(lineIndex==0?contentMarginTop:halfMiddleVertical);
                    int dividerBottom=columnBottom+(lineIndex==lineCount-1&&!bottomCoincide?contentMarginBottom:0);
                    for (; childIndex <= childLastIndex; childIndex++) {
                        final View child = getChildAt(lineIndex);
                        if (mColumnIndex == mMaxColumnIndex || skipChild(child)) continue;
                        columnRight = columnLeft + columnWidth;
                        mDividerMargin.drawDividerV(canvas, dividerTop, dividerBottom, columnRight + (middleMarginHorizontal > 0 ? middleMarginHorizontal / 2 : 0));
                        columnLeft = columnRight;
                        if (middleMarginHorizontal > 0) {
                            columnLeft += middleMarginHorizontal;
                        }
                        mColumnIndex++;
                    }
                }
                childIndex = childLastIndex + 1;
                columnTop = columnBottom+halfMiddleVertical;
            }
        }
        super.dispatchDrawAfter(canvas);
    }

    public boolean isColumnAlignCenter(int columnIndex) {
        return mAlignCenterAllColumns || (mAlignCenterColumns != null && mAlignCenterColumns.get(columnIndex, false));
    }

    public boolean isColumnAlignRight(int columnIndex) {
        return mAlignRightAllColumns || (mAlignRightColumns != null && mAlignRightColumns.get(columnIndex, false));
    }

    public boolean isColumnAlignLeft(int columnIndex) {
        return !(isColumnAlignCenter(columnIndex) || isColumnAlignRight(columnIndex));
    }

    public boolean isColumnStretch(int columnIndex) {
        return mStretchAllColumns || (mStretchColumns != null && mStretchColumns.get(columnIndex, false));
    }

    public boolean isColumnCenterVertical() {
        return mColumnCenterVertical;
    }

    public int getColumnMinWidth() {
        return mColumnMinWidth;
    }

    public int getColumnMaxWidth() {
        return mColumnMaxWidth;
    }

    public int getColumnMinHeight() {
        return mColumnMinHeight;
    }

    public int getColumnMaxHeight() {
        return mColumnMaxHeight;
    }

    public void setColumnNumber(int columnNumber) {
        if (mColumnNumber != columnNumber) {
            mColumnNumber = columnNumber;
            requestLayout();
        }
    }

    public void setColumnCenterVertical(boolean columnCenterVertical) {
        if (mColumnCenterVertical != columnCenterVertical) {
            mColumnCenterVertical = columnCenterVertical;
            requestLayout();
        }
    }

    public void setColumnMinWidth(int columnMinWidth) {
        if (mColumnMinWidth != columnMinWidth) {
            mColumnMinWidth = columnMinWidth;
            requestLayout();
        }
    }

    public void setColumnMaxWidth(int columnMaxWidth) {
        if (mColumnMaxWidth != columnMaxWidth) {
            mColumnMaxWidth = columnMaxWidth;
            requestLayout();
        }
    }

    public void setColumnMinHeight(int columnMinHeight) {
        if (mColumnMinHeight != columnMinHeight) {
            mColumnMinHeight = columnMinHeight;
            requestLayout();
        }
    }

    public void setColumnMaxHeight(int columnMaxHeight) {
        if (mColumnMaxHeight != columnMaxHeight) {
            mColumnMaxHeight = columnMaxHeight;
            requestLayout();
        }
    }
}
