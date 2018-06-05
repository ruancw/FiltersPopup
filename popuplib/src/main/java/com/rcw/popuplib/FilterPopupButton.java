package com.rcw.popuplib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义的二级筛选菜单的按钮，数据显示使用的recyclerView实现
 * Created by ruancw on 2018/5/30.
 */
public class FilterPopupButton extends AppCompatButton implements PopupWindow.OnDismissListener {
    private int mBackGround;//正常状态下的背景
    private int mIcon;//正常状态下的图标
    private int mClickBackGround;//点击时的背景
    private int mClickIcon;//点击时的图标
    //private FixPopupWindow mFixPopupWindow;//自定义的PopupWindow,适配>24
    private PopupWindow mFixPopupWindow;//自定义的PopupWindow,适配>24
    private Context context;//上下文对象
    private int mScreenWidth;//屏幕的宽度
    private int mScreenHeight;//屏幕的高度
    private int paddingTop, paddingLeft, paddingRight, paddingBottom;
    private LayoutInflater inflater;
    private List<String> mValuesList;//存储取值的集合
    private List<String> mParentList;//一级菜单数据集合
    private List<List<String>> mChildList;//二级菜单数据集合
    private FilterAdapter mParentAdapter, mChildAdapter;
    private RecyclerView mRvParent, mRvChild;
    private PopupButtonMonitor popupButtonMonitor;

    //三个有参构造方法
    public FilterPopupButton(Context context) {
        super(context);
        this.context = context;
    }

    public FilterPopupButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public FilterPopupButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflater = LayoutInflater.from(context);
        initAttrs(context, attrs);
        initParams(context);

    }

    /**
     * 设置数据
     * @param mParentList 一级菜单数据集合
     * @param mChildList  二级菜单数据集合
     */
    public void setValue(List<String> mParentList, List<List<String>> mChildList) {
        this.mParentList = mParentList;
        this.mChildList = mChildList;
        //初始化popupWindow的布局文件
        init();
    }

    /**
     * 初始化popupWindow的布局文件
     */
    private void init() {
        //初始化popupWindow的布局文件
        View view = inflater.inflate(R.layout.popup_layout, null);
        //展示一级菜单的view
        mRvParent = (RecyclerView) view.findViewById(R.id.rv_parent);
        //展示二级菜单的view
        mRvChild = (RecyclerView) view.findViewById(R.id.rv_child);
        //存储取值列表的集合
        mValuesList = new ArrayList<>();
        //默认添加一级菜单对应的二级菜单的第一个值列表
        //mValuesList.addAll(mChildList.get(0));

        mRvParent.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mRvParent.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        mRvChild.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mRvChild.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        //创建一级和二级菜单的adapter
        mParentAdapter = new FilterAdapter(context, R.layout.popup_item, mParentList);
        mChildAdapter = new FilterAdapter(context, R.layout.popup_item, mValuesList);
        //设置一级菜单的第一个默认选中状态
        mParentAdapter.recordSelectPosition(0);
        mRvParent.setAdapter(mParentAdapter);
        mRvChild.setAdapter(mChildAdapter);
        //初始化popupWindow
        initPopupView(view);
        //父条目点击事件
        mParentAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (position == 0) {
                    //position=0为筛选全部，点击popupWindow消失并设置取值为父条目的第一个值
                    setText(mParentList.get(position));
                    hidePopupWindow(mParentList.get(position));
                }
                //记录父条目点击的position
                mParentAdapter.recordSelectPosition(position);
                mParentAdapter.notifyDataSetChanged();
                mValuesList.clear();
                //将子条目添加到取值列表
                mValuesList.addAll(mChildList.get(position));
                mChildAdapter.notifyDataSetChanged();
                mChildAdapter.recordSelectPosition(-1);
                //设置子条目默认选中第一个
                //mRvChild.scrollToPosition(0);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }

        });
        //子条目点击事件
        mChildAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                //记录子条目点击的position
                mChildAdapter.recordSelectPosition(position);
                mChildAdapter.notifyDataSetChanged();
                //设置选中的字符
                setText(mValuesList.get(position));
                //隐藏popupWindow
                hidePopupWindow(mValuesList.get(position));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }

        });
    }

    /**
     * 初始化属性参数
     * @param context 上下文对象
     * @param attrs   属性
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.popupbtn);
        //正常状态下的背景色
        mBackGround = typedArray.getResourceId(R.styleable.popupbtn_normalBg, -1);
        //点击时的背景色
        mClickBackGround = typedArray.getResourceId(R.styleable.popupbtn_pressBg, -1);
        //正常状态下的图标
        mIcon = typedArray.getResourceId(R.styleable.popupbtn_normalIcon, -1);
        //点击状态下的图标
        mClickIcon = typedArray.getResourceId(R.styleable.popupbtn_pressIcon, -1);
        //回收
        typedArray.recycle();
    }

    /**
     * 初始化参数
     */
    private void initParams(Context context) {
        //初始化图标的padding值
        paddingTop = this.getPaddingTop();
        paddingLeft = this.getPaddingLeft();
        paddingRight = 20;
        paddingBottom = this.getPaddingBottom();
        //获取Window管理器
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //获取屏幕的宽和高
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        //设置正常状态下的参数
        setNormalStatus();
    }

    /**
     * 隐藏popupWindow
     * @param value 获取到的值
     */
    public void hidePopupWindow(String value) {
        if (mFixPopupWindow != null && mFixPopupWindow.isShowing()) {
            popupButtonMonitor.setFilterResult(value);
            mFixPopupWindow.dismiss();
        }
    }

    /**
     * 初始化popupWindow
     * @param view popupWindow的布局view
     */
    public void initPopupView(final View view) {
        //当点击button时弹出popupWindow
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFixPopupWindow == null) {
                    LinearLayout layout = new LinearLayout(context);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.6));
                    view.setLayoutParams(params);
                    layout.addView(view);
                    //设置背景色，不设置的话在有些机型会不显示popupWindow
                    layout.setBackgroundColor(Color.argb(60, 0, 0, 0));
                    //自定义的FixPopupWindow，解决在Build.VERSION.SDK_INT >= 24时，popupWindow显示位置在屏幕顶部问题
                    mFixPopupWindow = new FixPopupWindow(layout, mScreenWidth, mScreenHeight);
                    mFixPopupWindow.setFocusable(true);
                    mFixPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                    //设置点击popupWindow外部可消失
                    mFixPopupWindow.setOutsideTouchable(true);
                    mFixPopupWindow.setOnDismissListener(FilterPopupButton.this);
                    layout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFixPopupWindow.dismiss();
                        }
                    });
                }
                //设置点击popupButton时的状态
                setClickStatus();
                mFixPopupWindow.showAsDropDown(FilterPopupButton.this);
            }
        });
    }

    /**
     * 设置点击状态下的背景及图标
     */
    private void setClickStatus() {
        //设置点击时的背景色
        if (mClickBackGround != -1) {
            this.setBackgroundResource(mClickBackGround);
            this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
        //设置点击时的图标
        if (mClickIcon != -1) {
            Drawable drawable = getResources().getDrawable(mClickIcon);
            // 设置drawable的bounds以便展示图标
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            this.setCompoundDrawables(null, null, drawable, null);
        }
    }

    /**
     * 设置正常状态下的背景及图标
     */
    private void setNormalStatus() {
        //未点击状态背景
        if (mBackGround != -1) {
            this.setBackgroundResource(mBackGround);
            this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
        //未点击状态下的图标
        if (mIcon != -1) {
            Drawable drawable = getResources().getDrawable(mIcon);
            // 设置drawable的bounds以便展示图标
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            this.setCompoundDrawables(null, null, drawable, null);
        }
    }

    @Override
    public void onDismiss() {
        //在popupWindow消失时，将状态设置为正常状态
        setNormalStatus();
    }

    /**
     * 监听数据的接口
     */
    public interface PopupButtonMonitor {
        //设置回调的方法
        void setFilterResult(String filterResult);
    }

    /**
     * 接口绑定
     * @param popupButtonMonitor 接口
     */
    public void setPopupButtonMonitor(PopupButtonMonitor popupButtonMonitor) {
        this.popupButtonMonitor = popupButtonMonitor;
    }
}
