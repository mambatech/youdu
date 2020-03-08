package com.duyin.bookshelf.todo.view.activity;

import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.duyin.bookshelf.R;
import com.duyin.bookshelf.utils.bar.ImmersionBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * created by edison 2020-02-27
 */
public class CommonWebActivity extends AppCompatActivity {

    public static final String KEY_URL = "key_url";
    public static final String KEY_TITLE = "key_title";
    private WebView mWebView;
    private TextView mTvTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_web);
        //修改状态栏颜色
        ImmersionBar.with(this).statusBarColor(R.color.white).init();
        if (ImmersionBar.isSupportStatusBarDarkFont()) {
            ImmersionBar.with(this).statusBarDarkFont(true).init();
        }

        mWebView = findViewById(R.id.web_view);
        mTvTitle = findViewById(R.id.tv_title);
        ImageView ivBack = findViewById(R.id.iv_back);
        String url = getIntent().getStringExtra(KEY_URL);
        String titleTxt = getIntent().getStringExtra(KEY_TITLE);
        if (TextUtils.isEmpty(url)){
            finish();
        }

        ivBack.setOnClickListener(v -> {
            onBackPressed();
        });

        configWebViewSettings(mWebView);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (view != null) {
                    //判断加入给定的title
                    if (!TextUtils.isEmpty(titleTxt)) {
                        setTitle(titleTxt);
                    } else {
                        setTitle(view.getTitle());
                    }
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (null != view) {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (view != null) {
                    //判断加入给定的title
                    if (!TextUtils.isEmpty(titleTxt)) {
                        setTitle(titleTxt);
                    } else {
                        setTitle(view.getTitle());
                    }
                }
            }
        });

        mWebView.loadUrl(url);
    }

    private void setTitle(String title) {
        if (isFinishing() || mTvTitle == null) return;
        mTvTitle.setText(title);
    }

    private void configWebViewSettings(WebView webView) {
        if (null != webView) {
            WebSettings settings = webView.getSettings();
            settings.setBlockNetworkImage(false);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setDomStorageEnabled(true);
            settings.setDefaultTextEncodingName("UTF-8");
            settings.setAllowContentAccess(true);
            settings.setAllowFileAccess(true);
            //开启JavaScript支持
            settings.setJavaScriptEnabled(true);
            // 支持缩放
            settings.setSupportZoom(true);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            settings.setDatabaseEnabled(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
        }
    }


}
