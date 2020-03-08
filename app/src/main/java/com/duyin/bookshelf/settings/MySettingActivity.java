package com.duyin.bookshelf.settings;

import android.os.Build;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.duyin.bookshelf.R;
import com.duyin.bookshelf.base.MBaseActivity;
import com.duyin.bookshelf.todo.help.PrivacyAndUserLienceHelper;
import com.duyin.bookshelf.utils.theme.ThemeStore;
import com.edison.mvplib.impl.IPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * created by edison 2020-02-27
 */
public class MySettingActivity extends MBaseActivity {


//    @BindView(R.id.iv_back)
//    ImageView ivBack;
    @BindView(R.id.tv_privacy)
    TextView tvPrivacy;
    @BindView(R.id.tv_license)
    TextView tvLicense;

    @Override
    protected IPresenter initInjector() {
        return null;
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected void onCreateActivity() {
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_my_setting);
        ButterKnife.bind(this);

//        ivBack.setOnClickListener(v -> {
//            onBackPressed();
//        });

        tvPrivacy.setOnClickListener(v -> {
            PrivacyAndUserLienceHelper.INSTANCE.gotoPrivacyActivity(this);
        });

        tvLicense.setOnClickListener(v -> {
            PrivacyAndUserLienceHelper.INSTANCE.gotoUserLicenseActivity(this);
        });
    }

    @Override
    protected void initData() {

    }
}
