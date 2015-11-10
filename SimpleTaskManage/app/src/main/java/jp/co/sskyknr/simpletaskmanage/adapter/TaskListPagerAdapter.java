package jp.co.sskyknr.simpletaskmanage.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

/**
 * タスクリストを表示するViewPager
 */
public class TaskListPagerAdapter extends FragmentStatePagerAdapter{

    ArrayList<Fragment> mList;

    public TaskListPagerAdapter(FragmentManager fm) {
        super(fm);
        mList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    /**
     * ViewPagerに要素を追加
     *
     * @param fragment
     */
    public void add(Fragment fragment) {
        mList.add(fragment);
        notifyDataSetChanged();
    }

    /**
     * 現在表示されているFragment
     *
     * @param viewPager
     * @param position
     * @return
     */
    public Fragment findFragmentByPosition(ViewPager viewPager, int position) {
        return (Fragment) instantiateItem(viewPager, position);
    }
}
