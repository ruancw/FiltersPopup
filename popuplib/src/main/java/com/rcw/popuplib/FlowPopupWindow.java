package com.rcw.popuplib;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ruancw on 2018/5/31.
 * 自定义类似流式布局的筛选popupWindow
 */

public class FlowPopupWindow extends PopupWindow{

    private FlowPopupWindow(Context context, View view){
        //这里可以修改popupWindow的宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(view);
        //设置popupWindow弹出和消失的动画效果
        //setAnimationStyle(R.style.popwin_anim_style);
        //设置有焦点
        setFocusable(true);
        //设置点击外部可消失
        setOutsideTouchable(true);
    }

    /**
     * 重写showAsDropDown方法，解决高版本不在控件下方显示的问题
     * @param anchor popupWindow要显示在的控件
     */
    @Override
    public void showAsDropDown(View anchor) {
        if(Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }

    /**
     * 自定义的内部类，用于设置参数
     */
    public static class Builder {

        private Context context;//上下文对象
        private List<FilterBean> listData;//要显示的数据集合
        private int columnCount;//列数
        private GridLayout mGridLayout;//用于显示流式布局
        private LinearLayout llContent;//popupWindow的内容显示
        //背景颜色
        private int colorBg = Color.parseColor("#F8F8F8");
        //默认的标题和标签的大小（sp）
        private int titleTextSize = 16;
        private int tabTextSize = 16;
        //标题字体颜色
        private int titleTextColor = Color.parseColor("#333333");
        //tab标签字体颜色
        private int labelTextColor = R.color.color_popup;
        //tab标签背景颜色
        private int labelBg = R.drawable.shape_circle_bg;
        //当前加载的行数
        private int row = -1;
        private FlowPopupWindow mFlowPopupWindow;
        private List<String> labelLists=new ArrayList<>();

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置数据集合
         *
         */
        public void setValues(List<FilterBean> listData) {
            this.listData = listData;
        }

        /**
         * 设置gridLayout的列数
         * @param columnCount 列数
         */
        public void setColumnCount(int columnCount){
            this.columnCount = columnCount;
        }

        /**
         * 设置内容区域的背景色
         * @param color 颜色
         */
        public void setBgColor(int color){
            colorBg = context.getResources().getColor(color);
        }

        /**
         * 标题字体大小
         * @param titleTextSize 字体大小
         */
        public void setTitleSize(int titleTextSize) {
            this.titleTextSize = titleTextSize;
        }

        /**
         * tab标签字体大小
         * @param tabTextSize 标签字体大小
         */
        public void setLabelSize(int tabTextSize) {
            this.tabTextSize = tabTextSize;
        }

        /**
         * 标题字体颜色
         * @param titleTextColor 颜色
         */
        public void setTitleColor(int titleTextColor) {
            this.titleTextColor = titleTextColor;
        }

        /**
         * tab标签字体颜色
         * @param labelTextColor 颜色
         */
        public void setLabelColor(int labelTextColor) {
            this.labelTextColor = labelTextColor;
        }

        /**
         * 设置标签的背景色
         * @param labelBg 背景色（drawable）
         */
        public void setLabelBg(int labelBg) {
            this.labelBg = labelBg;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void build(){
            //初始化popupWindow的布局文件
            initPopup(getRowCount(),columnCount);
            //设置gridLayout的数据
            setGridData();
        }

        /**
         * 将数据设置给GridLayout
         */
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void setGridData() {
            for (int i = 0; i < listData.size(); i++){
                //行数++
                ++row;
                //显示每个条目类型的控件
                TextView tvType = new TextView(context);
                tvType.setText(listData.get(i).getTypeName());
                tvType.setTextColor(titleTextColor);
                tvType.setTextSize(titleTextSize);
                //配置列 第一个参数是起始列标 第二个参数是占几列 title（筛选类型）应该占满整行，so -> 总列数
                GridLayout.Spec columnSpec = GridLayout.spec(0,columnCount);
                //配置行 第一个参数是起始行标  起始行+起始列就是一个确定的位置
                GridLayout.Spec rowSpec = GridLayout.spec(row);
                //将Spec传入GridLayout.LayoutParams并设置宽高为0或者WRAP_CONTENT，必须设置宽高，否则视图异常
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
                layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                layoutParams.bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_8);
                mGridLayout.addView(tvType,layoutParams);
                //添加tab标签
                addTabs(listData.get(i),i);
            }
        }

        /**
         * 初始化PopupWindow的布局
         * @param rowCount 行数
         * @param columnCount 列数
         */
        private void initPopup(int rowCount,int columnCount){
            //初始化popupWindow的布局文件
            View view = LayoutInflater.from(context).inflate(R.layout.flow_popup,null);
            //主要用于设置数据显示区域的背景色
            LinearLayout ll=view.findViewById(R.id.ll);
            RelativeLayout rlBtn=view.findViewById(R.id.rl_btn);
            //流布局数据展示控件
            mGridLayout=view.findViewById(R.id.grid_layout);
            //确定按钮
            Button btnConfirm=view.findViewById(R.id.btn_confirm);
            //设置数据展示区域的背景色
            ll.setBackgroundColor(colorBg);
            llContent = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(params);
            llContent.addView(view);
            //设置背景色，不设置的话在有些机型会不显示popupWindow
            llContent.setBackgroundColor(Color.argb(60, 0, 0, 0));
            llContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hidePopup();
                }
            });
            //设置“确定”按钮父类消费触摸事件
            rlBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
            //确定按钮的点击事件
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //监听接口的数据回调方法
                    flowPopupMonitor.setFlowPopupResult(labelLists);
                    //隐藏popupWindow
                    hidePopup();
                }
            });
            //设置mGridLayout的属性参数
            mGridLayout.setOrientation(GridLayout.HORIZONTAL);
            mGridLayout.setRowCount(rowCount);
            mGridLayout.setColumnCount(columnCount);
            //设置gridLayout消费触摸事件
            mGridLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            int padding = context.getResources().getDimensionPixelSize(R.dimen.dp_10);
            mGridLayout.setPadding(padding,padding,padding,padding);
        }

        /**
         * 隐藏popupWindow
         */
        private void hidePopup() {
            if (mFlowPopupWindow != null&&mFlowPopupWindow.isShowing()){
                mFlowPopupWindow.dismiss();
            }
        }

        /**
         * 添加tab标签
         * @param model 数据bean
         * @param labelIndex 标签的标号
         */
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void addTabs(final FilterBean model, final int labelIndex){
            List<FilterBean.TableMode> tabs = model.getTabs();
            for (int i = 0; i < tabs.size(); i++){
                if (i % columnCount == 0){
                    row ++;
                }
                final FilterBean.TableMode tab = tabs.get(i);
                //显示标签的控件
                final TextView label = new TextView(context);
                //设置默认选中第一个
                if (i==0) {
                    //每个tab的第一个设置为选中
                    label.setSelected(true);
                    //记录选中的tab值
                    model.setTab(tab);
                    //新增未点击筛选条件，返回默认的不限选项
                    labelLists.add(model.getTypeName()+"-"+tab.name);
                }
                label.setTextSize(tabTextSize);
                label.setTextColor(context.getResources().getColorStateList(labelTextColor));
                label.setBackgroundDrawable(context.getResources().getDrawable(labelBg));
                label.setSingleLine(true);
                label.setGravity(Gravity.CENTER);
                label.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                //上下padding值
                int paddingT = context.getResources().getDimensionPixelSize(R.dimen.dp_5);
                //左右padding值
                int paddingL = context.getResources().getDimensionPixelSize(R.dimen.dp_8);
                label.setPadding(paddingL,paddingT,paddingL,paddingT);
                //getItemLayoutParams用于设置label标签的参数
                mGridLayout.addView(label,getItemLayoutParams(i,row));
                label.setText(tab.name);
                //记录上次选中状态
                if (tabs.get(i) == model.getTab()){
                    label.setSelected(true);
                    //不默认选中状态
                    //labelLists.add(model.getTypeName()+"-"+tab.name);
                }
                //标签的点击事件
                label.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tab != model.getTab()){
                            Log.e("rcw","index--->"+getIndex(model,labelIndex));
                            //清空上次选中的状态
                            mGridLayout.getChildAt(getIndex(model,labelIndex)).setSelected(false);
                            //设置当前点击选中的tab值
                            model.setTab(tab);
                            label.setSelected(true);
                            String labelText=label.getText().toString();
                            //解决tab未被点击时 ，不添加默认的“不限”数据到集合中
                            int flag=-1;//用于记录需要替换的位置
                            //默认选中
                            for (int i=0;i<labelLists.size();i++){
                                String tvDes=labelLists.get(i);
                                //判断当前集合中是否包含TypeName
                                if (tvDes.contains(model.getTypeName())){
                                    flag=i;
                                }
                            }
                            if (flag!=-1){
                                //先删除返回数据集合中的之前选中的
                                labelLists.remove(flag);
                                //添加当前选中的数据到集合
                                labelLists.add(flag,model.getTypeName()+"-"+labelText);
                            }
                            //不默认选中状态下
                            //recordStatus(labelText, flag, model);
                            //labelLists.add(model.getTypeName()+"-"+labelText);
                            Log.e("rcw","labelText--->"+model.getTypeName()+"-"+labelText);
                        }
                    }
                });
            }
        }

        private void recordStatus(String labelText, int flag, FilterBean model) {
            if (labelLists.size()!=0){
                for (int i=0;i<labelLists.size();i++){
                    String tvDes=labelLists.get(i);
                    //判断当前集合中是否包含TypeName
                    if (tvDes.contains(model.getTypeName())){
                        flag=i;
                        break;//匹配成功，跳出循环
                    }else {
                        flag=-1;
                    }
                }
                if (flag!=-1){
                    //先删除返回数据集合中的之前选中的
                    labelLists.remove(flag);
                    //添加当前选中的数据到集合
                    labelLists.add(flag,model.getTypeName()+"-"+labelText);
                }else {
                    labelLists.add(model.getTypeName()+"-"+labelText);
                }
            }else {
                labelLists.add(model.getTypeName()+"-"+labelText);
            }
        }

        //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private GridLayout.LayoutParams getItemLayoutParams(int i, int row){
            //使用Spec定义子控件的位置和比重
            GridLayout.Spec rowSpec = GridLayout.spec(row,1f);
            GridLayout.Spec columnSpec = GridLayout.spec(i%columnCount,1f);
            //将Spec传入GridLayout.LayoutParams并设置宽高为0，必须设置宽高，否则视图异常
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams(rowSpec, columnSpec);
            lp.width = 0;
            lp.height = GridLayout.LayoutParams.WRAP_CONTENT;
            lp.bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_8);
            if(i % columnCount == 0) {//最左边
                lp.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_10);
                lp.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_20);
            }else if((i + 1) % columnCount == 0){//最右边
                lp.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_10);
            }else {//中间
                lp.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.dp_20);
            }
            return lp;
        }

        /**
         * 获取当前选中标签在整个GridLayout的索引
         * @return 标签下标
         */
        private int getIndex(FilterBean model, int labelIndex){
            int index = 0;
            for (int i = 0; i < labelIndex; i++){
                //计算当前类型之前的元素所占的个数 title算一个
                index += listData.get(i).getTabs().size() + 1;
            }
            //加上当前 title下的索引
            FilterBean.TableMode tableModel = model.getTab();
            index += model.getTabs().indexOf(tableModel) + 1;
            return index;
        }

        /**
         * 获取内容行数
         * @return 行数
         */
        private int getRowCount(){
            int row = 0;
            for (FilterBean model : listData){
                //计算当前类型之前的元素所占的个数 标题栏也算一行
                row ++;
                int size = model.getTabs().size();
                row += (size / columnCount) + (size % columnCount > 0 ? 1 : 0) ;
            }
            return row;
        }

        /**
         * 创建popupWindow
         * @return FlowPopupWindow实例
         */
        public FlowPopupWindow createPopup(){
            if (listData == null || listData.size() == 0){
                try {
                    throw new Exception("没有筛选标签");
                } catch (Exception e) {
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                return null;
            }
            mFlowPopupWindow = new FlowPopupWindow(context,llContent);
            return mFlowPopupWindow;
        }

    }

    private static FlowPopupMonitor flowPopupMonitor;

    public interface FlowPopupMonitor{
        void setFlowPopupResult(List<String> filterResult);
    }

    public void setFlowPopupMonitor(FlowPopupMonitor flowPopupMonitor){
        this.flowPopupMonitor=flowPopupMonitor;
    }

}
