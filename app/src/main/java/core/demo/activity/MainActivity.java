package core.demo.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import core.demo.R;
import core.demo.activity.main.ResDlgFrag;
import core.mate.app.CoreActivity;
import core.mate.app.CoreFrag;
import core.mate.util.ClassUtil;

public class MainActivity extends CoreActivity {

    /*继承*/

    private TabLayout tabLayout;
    private Fragment curFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout_main_tabs);

        //反射获取指定包下的子类
        List<Class> frags = null;
        try {
            frags = ClassUtil.getSubClassUnderPackage(CoreFrag.class, "core.demo.activity.main");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //填充TabLayout
        for (Class clz : frags) {
            tabLayout.addTab(tabLayout.newTab().setText(clz.getSimpleName()).setTag(clz));
        }
        TabLayout.OnTabSelectedListener listener;
        tabLayout.addOnTabSelectedListener(listener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //使用FragHelper快速切换Fragment
                //CoreFrag已经处理了重建Activity后Frag重叠的问题
                Class clz = (Class) tab.getTag();
                curFrag = getFragHelper().switchFragment(R.id.frameLayout_main_fragContaienr, curFrag, clz);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //选中第一个tab
        tabLayout.getTabAt(0).select();
        listener.onTabSelected(tabLayout.getTabAt(0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("查看资源");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        new ResDlgFrag().show(this);
        return true;
    }
}
