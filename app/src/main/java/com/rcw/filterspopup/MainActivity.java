package com.rcw.filterspopup;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.rcw.popuplib.FilterBean;
import com.rcw.popuplib.FilterPopupButton;
import com.rcw.popuplib.FlowPopupWindow;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FilterPopupButton.PopupButtonMonitor,FlowPopupWindow.FlowPopupMonitor{
    private Button btnFlowPopup;
    private ImageView ivArrow;
    private FilterPopupButton filterPopup;
    private List<List<String>> cList;
    private List<String>pList;
    private FlowPopupWindow mFixPopupWindow;
    private Context context;
    private List<FilterBean> lists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_main);
        initData();
        initView();
        btnFlowPopup.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                ivArrow.setImageResource(R.drawable.arrow_up_blue);
                initFlowPopup();
            }
        });

    }

    private void initView() {
        filterPopup=findViewById(R.id.filter_popup_button);
        btnFlowPopup = findViewById(R.id.btn_flow_popup);
        ivArrow=findViewById(R.id.iv_arrow);
        filterPopup.setValue(pList,cList);
        filterPopup.setPopupButtonMonitor(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initFlowPopup() {
        FlowPopupWindow.Builder builder=new FlowPopupWindow.Builder(context);
        //设置数据
        builder.setValues(lists);
        //设置标签字体的颜色，这里的color不是values目录下的color,而是res文件夹下的color
        builder.setLabelColor(R.color.color_popup);
        //设置标签的背景色
        builder.setLabelBg(R.drawable.flow_popup);
        //设置GridLayout的列数
        builder.setColumnCount(4);
        //初始化popupWindow的相关布局及数据展示
        builder.build();
        //创建popup
        mFixPopupWindow=builder.createPopup();
        //设置数据监听接口
        mFixPopupWindow.setFlowPopupMonitor(this);
        mFixPopupWindow.showAsDropDown(btnFlowPopup);
        mFixPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivArrow.setImageResource(R.drawable.arrow_down);
            }
        });
    }

    private void initData() {
        lists=new ArrayList<>();
        pList = new ArrayList<>();
        cList = new ArrayList<>();
        for(int i = 0; i < 10; i ++) {
            if (i==0){
                pList.add("全部年级");
                List<String> t = new ArrayList<>();
                cList.add(t);
            }else {
                pList.add(i+"年级");
                List<String> t = new ArrayList<>();
                for(int j = 0; j < 15; j++) {
                    if (j<9){
                        t.add(i + "-0" +(j+1)+"班");
                    }else t.add(i + "-" +(j+1)+"班");
                }
                cList.add(t);
            }

        }
        List<FilterBean.TableMode> list=new ArrayList<>();
        list.add(new FilterBean.TableMode("不限"));
        list.add(new FilterBean.TableMode("已开通"));
        list.add(new FilterBean.TableMode("未开通"));
        lists.add(new FilterBean("业务状态",new FilterBean.TableMode("不限"),list));
        List<FilterBean.TableMode> list1=new ArrayList<>();
        list1.add(new FilterBean.TableMode("不限"));
        list1.add(new FilterBean.TableMode("已打印"));
        list1.add(new FilterBean.TableMode("未打印"));
        lists.add(new FilterBean("打印状态",new FilterBean.TableMode("不限"),list1));
        List<FilterBean.TableMode> list2=new ArrayList<>();
        list2.add(new FilterBean.TableMode("不限"));
        list2.add(new FilterBean.TableMode("未知"));
        list2.add(new FilterBean.TableMode("制卡中"));
        list2.add(new FilterBean.TableMode("已完成"));
        lists.add(new FilterBean("制卡状态",new FilterBean.TableMode("不限"),list2));
    }

    @Override
    public void setFilterResult(String filterResult) {
        Log.e("rcw","filterResult--->"+filterResult);
    }

    @Override
    public void setFlowPopupResult(List<String> filterResult) {
        ivArrow.setImageResource(R.drawable.arrow_down);
        Log.e("rcw","filterResult===>"+filterResult);
    }
}
