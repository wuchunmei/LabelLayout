package cn.tianya.light.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.tianya.light.R;
import cn.tianya.light.adapter.PagerAdapterEx;
import cn.tianya.light.util.StyleUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by wuchunmei on 9/4/17.
 * 让TabPageIndicator扩展性更强，可自定义指示器的布局
 */
public class TabChannelIndicator extends HorizontalScrollView implements ViewPager.OnPageChangeListener {

    private int mIndicatorColor;
    private float mItemWidth;
    private float mIndicatorPadding;
    private float mIndicatorHeight;

    private TextView indicatorText;
    private ViewPager viewPager;
    private LinearLayout itemLayout;
    private LinearLayout tabLayout;
    private PagerAdapterEx pagerAdapter;
    private ViewPager.OnPageChangeListener listener;

    private int currentItem = -1;
    private int indicatorLeft;
    private Runnable tabSelectorRunnable;

    private Context context;
    private OnItemClickListener itemListener;
    private int mPosition;
    private Paint mLinePaint;
    private Paint mDividerPaint;
    private float mLineWidth;
    private int mDividerColor;
    private float mLineStrokeWidth;
    private float mDividerStrokeWidth;
    private PagerAdapterEx mPagerAdapterEx;

    public TabChannelIndicator(Context context) {
        this(context, null);
    }

    public TabChannelIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TabChannelIndicator);
        if (array == null) {
            return;
        }
        mItemWidth = array.getDimension(R.styleable.TabChannelIndicator_itemWidthEx, 200);
        mIndicatorPadding = array.getDimension(R.styleable.TabChannelIndicator_indicatorPaddingEx, 0);
        mIndicatorHeight = array.getDimension(R.styleable.TabChannelIndicator_indicatorHeightEx, 10);
        mLineWidth = array.getDimension(R.styleable.TabChannelIndicator_indicatorLineWidthEx, getResources().getDimension(R.dimen.line_width));
        mIndicatorColor = array.getColor(R.styleable.TabChannelIndicator_indicatorColorEx, getResources().getColor(R.color.color_308ee3));
        mDividerColor = array.getColor(R.styleable.TabChannelIndicator_dividerColorEx, getResources().getColor(R.color.color_e0e0e0));
        mLineStrokeWidth = array.getDimension(R.styleable.TabChannelIndicator_indicatorLineStrokeWidth, 6);
        mDividerStrokeWidth = array.getDimension(R.styleable.TabChannelIndicator_indicatorDividerStrokeWidth, 1);
        array.recycle();

        itemLayout = new LinearLayout(context);
        tabLayout = new LinearLayout(context);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        tabLayout.setOrientation(LinearLayout.VERTICAL);
        setHorizontalScrollBarEnabled(false);
        initPaint();
    }

    private void initPaint() {
        //选中下划线画笔
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(mLineStrokeWidth);
        mLinePaint.setColor(mIndicatorColor);

        //分割线画笔
        mDividerPaint = new Paint();
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setStyle(Paint.Style.FILL);
        mDividerPaint.setStrokeWidth(mDividerStrokeWidth);
        mDividerPaint.setColor(mDividerColor);
    }

    /**
     * 绑定ViewPager
     *
     * @param view
     */
    public void setViewPager(ViewPager view) {
        if (view != null) {
            pagerAdapter = (PagerAdapterEx) view.getAdapter();
            if (pagerAdapter == null) {
                throw new IllegalStateException("ViewPager does not have adapter instance.");
            }
            this.viewPager = view;
            viewPager.addOnPageChangeListener(this);
            notifyDataSetChanged(pagerAdapter);
        } else {
            throw new IllegalStateException("ViewPager is null.");
        }
    }

    private static int getDeviceScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        return w > h ? h : w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int height = getHeight();

        if (itemLayout != null) {
            View currentTab = itemLayout.getChildAt(currentItem);
            if (currentTab == null) {
                return;
            }
            float lineLeft = currentTab.getLeft();
            //减去这个具体值是为了补齐偏移量,让下划线跟文字一样长（因为需求没有时间去动态测量文字长度）
            float left = lineLeft + itemLayout.getChildAt(mPosition).getPaddingLeft() + itemLayout.getChildAt(mPosition).getMeasuredWidth() / 3 - 6;
            float right = (lineLeft + itemLayout.getChildAt(mPosition).getPaddingRight() + itemLayout.getChildAt(mPosition).getMeasuredWidth() / 3) + mLineWidth - 11;
            float top = height - mIndicatorHeight + 3;
            float width = tabLayout.getWidth();
            canvas.drawLine(0, top, width, top, mDividerPaint);
            canvas.drawLine(left, top, right, top, mLinePaint);

        }

    }

    /**
     * 设置分割线颜色
     *
     * @param dividerPaintColor
     */
    public void setPaintColor(int dividerPaintColor) {
        mDividerPaint.setColor(dividerPaintColor);
        invalidate();
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager view, int initialPosition) {
        currentItem = initialPosition;
        setViewPager(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemListener = listener;
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.listener = listener;
    }

    /**
     * 设置当前被选中的item
     *
     * @param item
     */
    public void setCurrentItem(int item) {
        if (viewPager != null) {
            indicatorLeft = (int) (item * mItemWidth + mIndicatorPadding);
            viewPager.setCurrentItem(item, false);
            animateToTab(item);
            setSelected(item);
            currentItem = item;
        }
    }

    /**
     * 返回当前选中item
     *
     * @return
     */
    public int getCurrentItem() {
        return currentItem;
    }

    /**
     * 设置指示器的内边距
     *
     * @param padding
     */
    public void setIndicatorPadding(int padding) {
        this.mIndicatorPadding = padding;
    }

    /**
     * 设置选项tab的宽度
     *
     * @param width
     */
    public void setItemWidth(int width) {
        if (width == 0) {
            throw new NullPointerException("width can't is 0");
        } else {
            this.mItemWidth = width;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (listener != null) {
            listener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
        indicatorLeft = (int) (position * mItemWidth + mIndicatorPadding);
        animateToTab(position);
        setSelected(position);
        currentItem = position;
        if (listener != null) {
            listener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (listener != null) {
            listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    private void animateToTab(final int position) {
        if (tabLayout == null) {
            return;
        }
        final View tabView = itemLayout.getChildAt(position);
        if (tabSelectorRunnable != null) {
            removeCallbacks(tabSelectorRunnable);
        }
        if (tabView != null) {
            tabSelectorRunnable = new Runnable() {
                public void run() {
                    final int scrollPos = tabView.getRight() - getWidth();
                    smoothScrollTo(scrollPos, 0);
                    tabSelectorRunnable = null;
                }
            };
        }
        post(tabSelectorRunnable);
    }

    private void setSelected(int position) {
        if (itemLayout == null || indicatorText == null) {
            return;
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicatorText.getLayoutParams();
        params.leftMargin = indicatorLeft;
        indicatorText.setLayoutParams(params);
        if (position < itemLayout.getChildCount()) {
            LinearLayout child = (LinearLayout) itemLayout.getChildAt(position);
            LinearLayout oldSelectedChild = (LinearLayout) itemLayout.getChildAt(currentItem);
            if (child != null && child.getChildCount() > 0) {
                //为了个夜间模式，非得把控件写成这个死样。。。。。。(在控件中一旦调用setTextColor的方式，会导致控件重新layout，所以会有闪一下的过程
                // 正常的写法是根据状态，然后用xml的selector方式，但是因为在这个场景下，文字要适配夜间模式。。这种代码好鄙视自己)
                for (int i = 0; i < child.getChildCount(); i++) {
//                    child.getChildAt(i).setSelected(true);
                    //选中的时候加粗
                    ((TextView) child.getChildAt(i)).setTextColor(getResources().getColor(R.color.color_308ee8));
                    ((TextView) child.getChildAt(i)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    if (position != currentItem && oldSelectedChild != null) {
//                        oldSelectedChild.getChildAt(i).setSelected(false);
                        ((TextView) oldSelectedChild.getChildAt(i)).setTextColor(StyleUtils.getColorOnMode(context, R.color.color_444444));
                        ((TextView) oldSelectedChild.getChildAt(i)).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    }
                }
            }
        }
    }

    protected void notifyDataSetChanged() {
        notifyDataSetChanged(mPagerAdapterEx);
    }

    private void notifyDataSetChanged(PagerAdapterEx pagerAdapterEx) {
        if (itemLayout.getChildCount() > 0) {
            itemLayout.removeAllViews();
            tabLayout.removeAllViews();
            removeAllViews();
        }
        if (pagerAdapterEx != null) {
            final int count = pagerAdapterEx.getCount();
            mPagerAdapterEx = pagerAdapterEx;
            if (count > 0) {
                itemLayout.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
                for (int i = 0; i < count; i++) {
                    View itemView = pagerAdapterEx.getTabView(i);
                    final int index = i;
                    itemView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewPager.setCurrentItem(index, false);
                            if (itemListener != null) {
                                itemListener.onItemClick(index);
                            }
                        }
                    });
                    itemLayout.addView(itemView, new LinearLayout.LayoutParams((int) mItemWidth, MATCH_PARENT));
                }
                tabLayout.addView(itemLayout);
                indicatorText = new TextView(context);
                indicatorText.setPadding((int) mIndicatorPadding, 0, (int) mIndicatorPadding, 0);
                indicatorText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                indicatorText.setBackgroundColor(mIndicatorColor);
                indicatorText.setTextColor(StyleUtils.getColorOnMode(context, R.color.color_444444));
                indicatorText.setTextSize(getResources().getDimension(R.dimen.text_size_16sp));
                //下划线改用drawLine的方式
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (mItemWidth - mIndicatorPadding * 2), (int) mIndicatorHeight);
//                params.topMargin = (int) -mIndicatorHeight;
//                params.leftMargin = (int) mIndicatorPadding;
//                params.rightMargin = (int) mIndicatorPadding;
//                indicatorText.setLayoutParams(params);
                tabLayout.addView(indicatorText);
                addView(tabLayout);
            }
            if (currentItem != -1) {
                setCurrentItem(currentItem);
            } else {
                setCurrentItem(0);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
