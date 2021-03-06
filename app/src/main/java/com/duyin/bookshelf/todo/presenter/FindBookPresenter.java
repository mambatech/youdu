
package com.duyin.bookshelf.todo.presenter;

import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.duyin.bookshelf.utils.ReadAssets;
import com.edison.mvplib.BasePresenterImpl;
import com.edison.mvplib.impl.IView;
import com.duyin.bookshelf.MApplication;
import com.duyin.bookshelf.bean.BookSourceBean;
import com.duyin.bookshelf.bean.FindKindBean;
import com.duyin.bookshelf.todo.bean.FindKindGroupBean;
import com.duyin.bookshelf.todo.model.BookSourceManager;
import com.duyin.bookshelf.todo.model.analyzeRule.AnalyzeRule;
import com.duyin.bookshelf.todo.presenter.contract.FindBookContract;
import com.duyin.bookshelf.utils.ACache;
import com.duyin.bookshelf.todo.widget.recycler.expandable.bean.RecyclerViewData;

import java.util.ArrayList;
import java.util.List;

import javax.script.SimpleBindings;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.duyin.bookshelf.todo.constant.AppConstant.SCRIPT_ENGINE;

public class FindBookPresenter extends BasePresenterImpl<FindBookContract.View> implements FindBookContract.Presenter {
    private Disposable disposable;
    private AnalyzeRule analyzeRule;
    private String findError = "发现规则语法错误";

    @SuppressWarnings("unchecked")
    @Override
    public void initData() {
        if (disposable != null) return;
        
        loadAssestBookObservable().map(aBoolean -> loadFromDB())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<RecyclerViewData>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(List<RecyclerViewData> data) {
                        mView.upData(data);
                        disposable.dispose();
                        disposable = null;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        disposable.dispose();
                        disposable = null;
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    
    private Observable<Boolean> loadAssestBookObservable() {
        return Observable.create(emitter -> {
            String str = ReadAssets.getText(MApplication.getInstance(),"localbook_data.json");
            BookSourceManager.importBookSourceFromJson(str);
            emitter.onNext(true);
        });
    }

    private List<RecyclerViewData> loadFromDB() {
        ACache aCache = ACache.get(mView.getContext(), "findCache");

        List<RecyclerViewData> group = new ArrayList<>();
        boolean showAllFind = MApplication.getConfigPreferences().getBoolean("showAllFind", true);
        List<BookSourceBean> sourceBeans = new ArrayList<>(showAllFind ? BookSourceManager.getAllBookSourceBySerialNumber() : BookSourceManager.getSelectedBookSourceBySerialNumber());
        for (BookSourceBean sourceBean : sourceBeans) {
            try {
                String[] kindA;
                String findRule;
                if (!TextUtils.isEmpty(sourceBean.getRuleFindUrl()) && !sourceBean.containsGroup(findError)) {
                    boolean isJsAndCache = sourceBean.getRuleFindUrl().startsWith("<js>");
                    if (isJsAndCache) {
                        findRule = aCache.getAsString(sourceBean.getBookSourceUrl());
                        if (TextUtils.isEmpty(findRule)) {
                            String jsStr = sourceBean.getRuleFindUrl().substring(4, sourceBean.getRuleFindUrl().lastIndexOf("<"));
                            findRule = evalJS(jsStr, sourceBean.getBookSourceUrl()).toString();
                        } else {
                            isJsAndCache = false;
                        }
                    } else {
                        findRule = sourceBean.getRuleFindUrl();
                    }
                    kindA = findRule.split("(&&|\n)+");
                    List<FindKindBean> children = new ArrayList<>();
                    for (String kindB : kindA) {
                        if (kindB.trim().isEmpty()) continue;
                        String[] kind = kindB.split("::");
                        FindKindBean findKindBean = new FindKindBean();
                        findKindBean.setGroup(sourceBean.getBookSourceName());
                        findKindBean.setTag(sourceBean.getBookSourceUrl());
                        findKindBean.setKindName(kind[0]);
                        findKindBean.setKindUrl(kind[1]);
                        children.add(findKindBean);
                    }
                    FindKindGroupBean groupBean = new FindKindGroupBean();
                    groupBean.setGroupName(sourceBean.getBookSourceName());
                    groupBean.setGroupTag(sourceBean.getBookSourceUrl());
                    group.add(new RecyclerViewData(groupBean, children, false));
                    if (isJsAndCache) {
                        aCache.put(sourceBean.getBookSourceUrl(), findRule);
                    }
                }
            } catch (Exception exception) {
                sourceBean.addGroup(findError);
                BookSourceManager.addBookSource(sourceBean);
            }
        }

        return group;
    }

    /**
     * 执行JS
     */
    private Object evalJS(String jsStr, String baseUrl) throws Exception {
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("java", getAnalyzeRule());
        bindings.put("baseUrl", baseUrl);
        return SCRIPT_ENGINE.eval(jsStr, bindings);
    }

    private AnalyzeRule getAnalyzeRule() {
        if (analyzeRule == null) {
            analyzeRule = new AnalyzeRule(null);
        }
        return analyzeRule;
    }

    @Override
    public void attachView(@NonNull IView iView) {
        super.attachView(iView);
    }

    @Override
    public void detachView() {

    }

}