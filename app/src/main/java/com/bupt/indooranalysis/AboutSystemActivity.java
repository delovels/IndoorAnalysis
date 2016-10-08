package com.bupt.indooranalysis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AboutSystemActivity extends BaseAppCompatActivity {

    private ListView listView;
    private String[] arrName = { "检测版本更新" };
    private List<Map<String, Object>> listItems;
    private Bundle savedState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置布局请使用getLayoutId()方法
        getToolbarTitle().setText("关于系统");
        getSubTitle().setText("");
        initaboutsystem();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_system;
    }

    public void initaboutsystem() {

        listView = (ListView) findViewById(R.id.about_system_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int index, long id) {
                // Log.d("selected", arrName[index]);
                switch (index) {
                    case 0:
                        CheckVersion();
                        break;
                }

            }
        });
        listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < arrName.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("text", arrName[i]);
            listItems.add(listItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
                R.layout.array_test, new String[] { "text" },
                new int[] { R.id.set_itemView });
        listView.setAdapter(simpleAdapter);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    // 版本更新
    private void CheckVersion() {
//
//        CheckVersionTask checkversiontask = new CheckVersionTask(this);
//        checkversiontask.run();
    }
}
