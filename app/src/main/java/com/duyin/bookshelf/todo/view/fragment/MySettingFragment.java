package com.duyin.bookshelf.todo.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.duyin.bookshelf.R;
import com.duyin.bookshelf.base.MBaseFragment;
import com.duyin.bookshelf.todo.help.PrivacyAndUserLienceHelper;
import com.edison.mvplib.impl.IPresenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * created by edison 2020-02-29
 */
public class MySettingFragment extends MBaseFragment {

    @BindView(R.id.rl_privacy)
    RelativeLayout mRlPrivacy;
    @BindView(R.id.rl_license)
    RelativeLayout mRlLicense;

    @Override
    public int createLayoutId() {
        return R.layout.activity_my_setting;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container) {
        return super.createView(inflater, container);
    }

    @Override
    protected void bindView() {
        super.bindView();
        ButterKnife.bind(this,view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRlPrivacy.setOnClickListener(v -> {
            PrivacyAndUserLienceHelper.INSTANCE.gotoPrivacyActivity(getContext());
        });

        mRlLicense.setOnClickListener(v -> {
            PrivacyAndUserLienceHelper.INSTANCE.gotoUserLicenseActivity(getContext());
        });
    }

    @Override
    protected IPresenter initInjector() {
        return null;
    }
}
