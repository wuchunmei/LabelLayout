package cn.tianya.light.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.tianya.bo.Entity;
import cn.tianya.light.R;
import cn.tianya.light.bo.QuestionType;
import cn.tianya.light.util.StyleUtils;

/**
 * Created by wuchunmei on 9/4/17.
 */
public class LabelLayout extends LinearLayout implements View.OnClickListener,AdapterView.OnItemClickListener{

    class LabelAdapter extends BaseAdapter {
        private List<Entity> titles;
        private Context mContext;
        private int mSeletctItemPosition;

        public LabelAdapter(Context context, List<Entity> titles){
            this.mContext = context;
            this.titles = titles;
        }

        @Override
        public int getCount() {
            return titles == null ? 0 : titles.size();
        }

        public void setSeletctPosition(int position){
            mSeletctItemPosition = position;
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return titles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.channel_item,null);
                holder = new ViewHolder();
                holder.title = (TextView)convertView.findViewById(R.id.tv_item);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.title.setTextColor(StyleUtils.getColorOnMode(mContext,R.color.color_444444));
            holder.title.setBackgroundResource(StyleUtils.getChannelColorRes(mContext, R.drawable.shape_channel_pop_item_bg, R.drawable.shape_channel_pop_item_night_bg));
            QuestionType questionType = (QuestionType) titles.get(position);
            if(questionType != null){
                String name = questionType.getName();
                holder.title.setText(name);
            }
            if(this.mSeletctItemPosition == position){
                holder.title.setTextColor(getResources().getColor(R.color.color_308ee3));
                holder.title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            return convertView;
        }

        class ViewHolder {
            TextView title;
        }
    }

    private Context mContext;
    private TextView mTabDown;
    private TabChannelIndicator mTabChannelIndicator;
    private PopupWindow mPopupWindow;
    private List<Entity> mTitles;
    private  RelativeLayout mTablayout = null;
    private  LabelAdapter mAdapter = null;
    private RelativeLayout mContainer;
    private View mSpiltLine;
    private TextView mPopTitle;
    private int mTitleResId = R.string.channel_pop_title;

    public LabelLayout(Context context) {
        this(context,null);

    }

    public LabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.tab_label_layout, this);
        mTabDown = (TextView) view.findViewById(R.id.tab_down);
        mTabDown.setOnClickListener(this);
        mTablayout = (RelativeLayout)view.findViewById(R.id.tab_down_layout);
        mTablayout.setOnClickListener(this);
        mTabChannelIndicator = (TabChannelIndicator) view.findViewById(R.id.tabIndicator);
        mContainer = (RelativeLayout)view.findViewById(R.id.container);
        mSpiltLine = view.findViewById(R.id.diver);
        mTitles = new ArrayList<>();

    }

    /**
     * 获取频道指示器
     * @return
     */
    public TabChannelIndicator getTabChannel(){
        return mTabChannelIndicator;
    }

    /**
     * 设置频道数据源,数据类型暂时用测试的String,后面接口出来再做调整
     * @param list
     */
    public void setTitles(List<Entity> list){
        if(list != null){
            mTitles.clear();
            mTitles.addAll(list);
        }
    }

    /**
     * tab频道夜间模式
     */
    public void onNightModeChanged(){
        mTabChannelIndicator.setPaintColor(StyleUtils.getColorOnMode(mContext, R.color.color_e0e0e0));
        mTabChannelIndicator.notifyDataSetChanged();
        mContainer.setBackgroundColor(mContext.getResources().getColor(StyleUtils.getChannelColorRes(mContext, R.color.white, R.color.color_393939)));
        mSpiltLine.setBackgroundColor(StyleUtils.getColorOnMode(mContext, R.color.color_e0e0e0));
    }

    private void showChannelPopwindow() {
        View view = null;
        GridView gridView = null;
        TextView up = null;
        view =  LayoutInflater.from(mContext).inflate(R.layout.channel_pop, null);
        mPopTitle = (TextView)view.findViewById(R.id.topTitle);
        mPopTitle.setText(getContext().getString(mTitleResId));
        View divider = view.findViewById(R.id.divider);
        divider.setBackgroundColor(mContext.getResources().getColor(StyleUtils.getChannelColorRes(mContext, R.color.color_e0e0e0, R.color.color_484848)));
        gridView = (GridView) view.findViewById(R.id.gridview);
        up = (TextView)view.findViewById(R.id.tab_up);
        up.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
        mAdapter = new LabelAdapter(mContext,mTitles);
        mAdapter.setSeletctPosition(mTabChannelIndicator.getCurrentItem());
        gridView.setAdapter(mAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(getResources().getDrawable( StyleUtils.getChannelColorRes(mContext, R.drawable.channel_pop_bg, R.drawable.channel_pop_night_bg)));
        }
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((Activity)mContext).getWindow().getAttributes();
                lp.alpha = 1f;
                ((Activity)mContext).getWindow().setAttributes(lp);
            }
        });

        WindowManager.LayoutParams lp =((Activity)mContext).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity)mContext).getWindow().setAttributes(lp);
        mPopupWindow.showAtLocation(((Activity)mContext).getWindow().getDecorView(), Gravity.TOP, 0, 50);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tab_down_layout:
            case R.id.tab_down:
                showChannelPopwindow();
                break;
            case R.id.tab_up:
                mPopupWindow.dismiss();
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mTabChannelIndicator.setCurrentItem(position);
        mPopupWindow.dismiss();
    }

    public void setTitleResId(int titleResId) {
        mTitleResId = titleResId;
    }
}
