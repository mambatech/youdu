
package com.duyin.bookshelf.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.duyin.bookshelf.BuildConfig;
import com.duyin.bookshelf.DbHelper;
import com.duyin.bookshelf.MApplication;
import com.duyin.bookshelf.R;
import com.duyin.bookshelf.todo.view.activity.SearchBookActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.hwangjr.rxbus.RxBus;
import com.duyin.bookshelf.base.BaseTabActivity;
import com.duyin.bookshelf.todo.constant.RxBusTag;
import com.duyin.bookshelf.todo.help.FileHelp;
import com.duyin.bookshelf.todo.help.ProcessTextHelp;
import com.duyin.bookshelf.todo.help.permission.Permissions;
import com.duyin.bookshelf.todo.help.permission.PermissionsCompat;
import com.duyin.bookshelf.todo.help.storage.BackupRestoreUi;
import com.duyin.bookshelf.todo.model.UpLastChapterModel;
import com.duyin.bookshelf.todo.presenter.MainPresenter;
import com.duyin.bookshelf.todo.presenter.contract.MainContract;
import com.duyin.bookshelf.utils.ACache;
import com.duyin.bookshelf.utils.StringUtils;
import com.duyin.bookshelf.utils.theme.ThemeStore;
import com.duyin.bookshelf.todo.view.fragment.BookListFragment;
import com.duyin.bookshelf.todo.view.fragment.FindBookFragment;
import com.duyin.bookshelf.todo.widget.modialog.InputDialog;
import com.duyin.bookshelf.todo.widget.modialog.MoDialogHUD;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseTabActivity<MainContract.Presenter> implements MainContract.View,
        BookListFragment.CallbackValue {
    private final int requestSource = 14;
    private String[] mTitles;

    @BindView(R.id.toolbar)
    LinearLayout toolbar;
    @BindView(R.id.main_view)
    CoordinatorLayout mainView;
    @BindView(R.id.card_search)
    TextView cardSearch;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;

    private int group;
    private MoDialogHUD moDialogHUD;
    private long exitTime = 0;
    private boolean resumed = false;
    private Handler handler = new Handler();

    @Override
    protected MainContract.Presenter initInjector() {
        return new MainPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            resumed = savedInstanceState.getBoolean("resumed");
        }
        group = preferences.getInt("bookshelfGroup", 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("resumed", resumed);
    }

    @Override
    protected void onCreateActivity() {
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        String shared_url = preferences.getString("shared_url", "");
        assert shared_url != null;
        if (shared_url.length() > 1) {
            InputDialog.builder(this)
                    .setTitle(getString(R.string.add_book_url))
                    .setDefaultValue(shared_url)
                    .setCallback(new InputDialog.Callback() {
                        @Override
                        public void setInputText(String inputText) {
                            inputText = StringUtils.trim(inputText);
                            mPresenter.addBookUrl(inputText);
                        }

                        @Override
                        public void delete(String value) {

                        }
                    }).show();
            preferences.edit()
                    .putString("shared_url", "")
                    .apply();
        }
    }


    /**
     * 沉浸状态栏
     */
    @Override
    public void initImmersionBar() {
        super.initImmersionBar();
    }

    @Override
    protected void initData() {
        mTitles = new String[]{getString(R.string.find),getString(R.string.bookshelf)};
    }

    @Override
    public boolean isRecreate() {
        return isRecreate;
    }

    @Override
    public int getGroup() {
        return group;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected List<Fragment> createTabFragments() {
        BookListFragment bookListFragment = null;
        FindBookFragment findBookFragment = null;
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof BookListFragment) {
                bookListFragment = (BookListFragment) fragment;
            } else if (fragment instanceof FindBookFragment) {
                findBookFragment = (FindBookFragment) fragment;
            }
        }
        if (bookListFragment == null)
            bookListFragment = new BookListFragment();
        if (findBookFragment == null)
            findBookFragment = new FindBookFragment();
        return Arrays.asList(findBookFragment,bookListFragment);
    }

    @Override
    protected List<String> createTabTitles() {
        return Arrays.asList(mTitles);
    }

    @Override
    protected void bindView() {
        super.bindView();
        initTabLayout();
        upGroup(group);
        moDialogHUD = new MoDialogHUD(this);
        if (!preferences.getBoolean("behaviorMain", true)) {
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(0);
        }
        //点击跳转搜索页
        cardSearch.setOnClickListener(view -> startActivityByAnim(new Intent(this, SearchBookActivity.class),
                toolbar, "sharedView", android.R.anim.fade_in, android.R.anim.fade_out));
    }

    //初始化TabLayout和ViewPager
    private void initTabLayout() {
        mTlIndicator.setBackgroundColor(ThemeStore.backgroundColor(this));
        mTlIndicator.setSelectedTabIndicatorColor(ThemeStore.accentColor(this));
        //TabLayout使用自定义Item
        for (int i = 0; i < mTlIndicator.getTabCount(); i++) {
            TabLayout.Tab tab = mTlIndicator.getTabAt(i);
            if (tab == null) return;
            tab.setCustomView(tab_icon(mTitles[i]));
            View customView = tab.getCustomView();
            if (customView == null) return;
            TextView tv = customView.findViewById(R.id.tabtext);
            tab.setContentDescription(String.format("%s,%s", tv.getText(), getString(R.string.click_on_selected_show_menu)));
            ImageView im = customView.findViewById(R.id.tabicon);
            if (tab.isSelected()) {
                im.setVisibility(View.VISIBLE);
            } else {
                im.setVisibility(View.GONE);
            }
        }
        mTlIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView == null) return;
                ImageView im = customView.findViewById(R.id.tabicon);
                im.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView == null) return;
                ImageView im = customView.findViewById(R.id.tabicon);
                im.setVisibility(View.GONE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                View tabView = (View) Objects.requireNonNull(tab.getCustomView()).getParent();
                if (tab.getPosition() == 0) {
                    showBookGroupMenu(tabView);
                } else {
                    showFindMenu(tabView);
                }
            }
        });
    }

    /**
     * 显示分组菜单
     */
    private void showBookGroupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        for (int j = 0; j < getResources().getStringArray(R.array.book_group_array).length; j++) {
            popupMenu.getMenu().add(0, 0, j, getResources().getStringArray(R.array.book_group_array)[j]);
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            upGroup(menuItem.getOrder());
            return true;
        });
        popupMenu.setOnDismissListener(popupMenu1 -> updateTabItemIcon(0, false));
        popupMenu.show();
        updateTabItemIcon(0, true);
    }

    /**
     * 显示发现菜单
     */
    private void showFindMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add(0, 0, 0, getString(R.string.switch_display_style));
        popupMenu.getMenu().add(0, 0, 1, getString(R.string.clear_find_cache));
        boolean findTypeIsFlexBox = preferences.getBoolean("findTypeIsFlexBox", true);
        boolean showFindLeftView = preferences.getBoolean("showFindLeftView", true);
        if (findTypeIsFlexBox) {
            popupMenu.getMenu().add(0, 0, 2, showFindLeftView ? "隐藏左侧栏" : "显示左侧栏");
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            FindBookFragment findBookFragment = getFindFragment();
            switch (menuItem.getOrder()) {
                case 0:
                    preferences.edit()
                            .putBoolean("findTypeIsFlexBox", !findTypeIsFlexBox)
                            .apply();
                    if (findBookFragment != null) {
                        findBookFragment.upStyle();
                    }
                    break;
                case 1:
                    ACache.get(this, "findCache").clear();
                    if (findBookFragment != null) {
                        findBookFragment.refreshData();
                    }
                    break;
                case 2:
                    preferences.edit()
                            .putBoolean("showFindLeftView", !showFindLeftView)
                            .apply();
                    if (findBookFragment != null) {
                        findBookFragment.upUI();
                    }
                    break;
            }
            return true;
        });
        popupMenu.setOnDismissListener(popupMenu1 -> updateTabItemIcon(1, false));
        popupMenu.show();
        updateTabItemIcon(1, true);
    }

    /**
     * 更新Tab图标
     */
    private void updateTabItemIcon(int index, boolean showMenu) {
        TabLayout.Tab tab = mTlIndicator.getTabAt(index);
        if (tab == null) return;
        View customView = tab.getCustomView();
        if (customView == null) return;
        ImageView im = customView.findViewById(R.id.tabicon);
        if (showMenu) {
            im.setImageResource(R.drawable.ic_arrow_drop_up);
        } else {
            im.setImageResource(R.drawable.ic_arrow_drop_down);
        }
    }

    /**
     * 更新Tab文字
     */
    private void updateTabItemText(int group) {
        TabLayout.Tab tab = mTlIndicator.getTabAt(0);
        if (tab == null) return;
        View customView = tab.getCustomView();
        if (customView == null) return;
        TextView tv = customView.findViewById(R.id.tabtext);
        tv.setText(getString(R.string.recommend));
        tab.setContentDescription(String.format("%s,%s", tv.getText(), getString(R.string.click_on_selected_show_menu)));
    }

    private View tab_icon(String name) {
        @SuppressLint("InflateParams")
        View tabView = LayoutInflater.from(this).inflate(R.layout.tab_view_icon_right, null);
        TextView tv = tabView.findViewById(R.id.tabtext);
        tv.setText(name);
        ImageView im = tabView.findViewById(R.id.tabicon);
        im.setVisibility(View.VISIBLE);
        im.setImageResource(R.drawable.ic_arrow_drop_down);
        return tabView;
    }

    public ViewPager getViewPager() {
        return mVp;
    }

    public BookListFragment getBookListFragment() {
        try {
            return (BookListFragment) mFragmentList.get(1);
        } catch (Exception e) {
            return null;
        }
    }

    public FindBookFragment getFindFragment() {
        try {
            return (FindBookFragment) mFragmentList.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    private void upGroup(int group) {
        if (this.group != group) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("bookshelfGroup", group);
            editor.apply();
        }
        this.group = group;
        RxBus.get().post(RxBusTag.UPDATE_GROUP, group);
        RxBus.get().post(RxBusTag.REFRESH_BOOK_LIST, false);
        //更换Tab文字
        updateTabItemText(group);
    }

    private void selectBookshelfLayout() {
        new AlertDialog.Builder(this)
                .setTitle("选择书架布局")
                .setItems(R.array.bookshelf_layout, (dialog, which) -> {
                    preferences.edit().putInt("bookshelfLayout", which).apply();
                    recreate();
                }).show();
    }

    /**
     * 新版本运行
     */
    private void versionUpRun() {
        if (preferences.getInt("versionCode", 0) != MApplication.getVersionCode()) {
            //保存版本号
            preferences.edit()
                    .putInt("versionCode", MApplication.getVersionCode())
                    .apply();
        }
    }

    @Override
    protected void firstRequest() {
        if (!isRecreate) {
            versionUpRun();
        }
        if (!Objects.equals(MApplication.downloadPath, FileHelp.getFilesPath())) {
            new PermissionsCompat.Builder(this)
                    .addPermissions(Permissions.READ_EXTERNAL_STORAGE, Permissions.WRITE_EXTERNAL_STORAGE)
                    .rationale(R.string.get_storage_per)
                    .request();
        }
        handler.postDelayed(() -> {
            UpLastChapterModel.getInstance().startUpdate();
            if (BuildConfig.DEBUG) {
                ProcessTextHelp.setProcessTextEnable(false);
            }
        }, 60 * 1000);
    }

    @Override
    public void dismissHUD() {
        moDialogHUD.dismiss();
    }

    public void onRestore(String msg) {
        moDialogHUD.showLoading(msg);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Boolean mo = moDialogHUD.onKeyDown(keyCode, event);
        if (mo) {
            return true;
        } else if (mTlIndicator.getSelectedTabPosition() != 0) {
            Objects.requireNonNull(mTlIndicator.getTabAt(0)).select();
            return true;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                exit();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 退出
     */
    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            showSnackBar(toolbar, getString(R.string.double_click_exit));
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    @Override
    protected void onDestroy() {
        UpLastChapterModel.destroy();
        DbHelper.getDaoSession().getBookContentBeanDao().deleteAll();
        super.onDestroy();
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BackupRestoreUi.INSTANCE.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case requestSource:
                if (resultCode == RESULT_OK) {
                    FindBookFragment findBookFragment = getFindFragment();
                    if (findBookFragment != null) {
                        findBookFragment.refreshData();
                    }
                }
                break;
        }
    }

}
