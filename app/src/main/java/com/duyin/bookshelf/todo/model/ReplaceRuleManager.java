package com.duyin.bookshelf.todo.model;

import android.text.TextUtils;

import com.duyin.bookshelf.DbHelper;
import com.duyin.bookshelf.base.net.BaseModelImpl;
import com.duyin.bookshelf.todo.bean.ReplaceRuleBean;
import com.duyin.bookshelf.dao.ReplaceRuleBeanDao;
import com.duyin.bookshelf.todo.model.analyzeRule.AnalyzeHeaders;
import com.duyin.bookshelf.todo.model.impl.IHttpGetApi;
import com.duyin.bookshelf.utils.GsonUtils;
import com.duyin.bookshelf.utils.NetworkUtils;
import com.duyin.bookshelf.utils.RxUtils;
import com.duyin.bookshelf.utils.StringUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;

/**
 * Created by GKF on 2018/2/12.
 * 替换规则管理
 */

public class ReplaceRuleManager {
    private static List<ReplaceRuleBean> replaceRuleBeansEnabled;

    public static List<ReplaceRuleBean> getEnabled() {
        if (replaceRuleBeansEnabled == null) {
            replaceRuleBeansEnabled = DbHelper.getDaoSession()
                    .getReplaceRuleBeanDao().queryBuilder()
                    .where(ReplaceRuleBeanDao.Properties.Enable.eq(true))
                    .orderAsc(ReplaceRuleBeanDao.Properties.SerialNumber)
                    .list();
        }
        return replaceRuleBeansEnabled;
    }

    public static Single<List<ReplaceRuleBean>> getAll() {
        return Single.create((SingleOnSubscribe<List<ReplaceRuleBean>>) emitter -> emitter.onSuccess(DbHelper.getDaoSession()
                .getReplaceRuleBeanDao().queryBuilder()
                .orderAsc(ReplaceRuleBeanDao.Properties.SerialNumber)
                .list())).compose(RxUtils::toSimpleSingle);
    }

    public static Single<Boolean> saveData(ReplaceRuleBean replaceRuleBean) {
        return Single.create((SingleOnSubscribe<Boolean>) emitter -> {
            if (replaceRuleBean.getSerialNumber() == 0) {
                replaceRuleBean.setSerialNumber((int) (DbHelper.getDaoSession().getReplaceRuleBeanDao().queryBuilder().count() + 1));
            }
            DbHelper.getDaoSession().getReplaceRuleBeanDao().insertOrReplace(replaceRuleBean);
            refreshDataS();
            emitter.onSuccess(true);
        }).compose(RxUtils::toSimpleSingle);
    }

    public static void delData(ReplaceRuleBean replaceRuleBean) {
        DbHelper.getDaoSession().getReplaceRuleBeanDao().delete(replaceRuleBean);
        refreshDataS();
    }

    public static void addDataS(List<ReplaceRuleBean> replaceRuleBeans) {
        if (replaceRuleBeans != null && replaceRuleBeans.size() > 0) {
            DbHelper.getDaoSession().getReplaceRuleBeanDao().insertOrReplaceInTx(replaceRuleBeans);
            refreshDataS();
        }
    }

    public static void delDataS(List<ReplaceRuleBean> replaceRuleBeans) {
        for (ReplaceRuleBean replaceRuleBean : replaceRuleBeans) {
            DbHelper.getDaoSession().getReplaceRuleBeanDao().delete(replaceRuleBean);
        }
        refreshDataS();
    }

    private static void refreshDataS() {
        replaceRuleBeansEnabled = DbHelper.getDaoSession()
                .getReplaceRuleBeanDao().queryBuilder()
                .where(ReplaceRuleBeanDao.Properties.Enable.eq(true))
                .orderAsc(ReplaceRuleBeanDao.Properties.SerialNumber)
                .list();
    }

    public static Observable<Boolean> importReplaceRule(String text) {
        if (TextUtils.isEmpty(text)) return null;
        text = text.trim();
        if (text.length() == 0) return null;
        if (StringUtils.isJsonType(text)) {
            return importReplaceRuleO(text)
                    .compose(RxUtils::toSimpleSingle);
        }
        if (NetworkUtils.isUrl(text)) {
            return BaseModelImpl.getInstance().getRetrofitString(StringUtils.getBaseUrl(text), "utf-8")
                    .create(IHttpGetApi.class)
                    .get(text, AnalyzeHeaders.getMap(null))
                    .flatMap(rsp -> importReplaceRuleO(rsp.body()))
                    .compose(RxUtils::toSimpleSingle);
        }
        return Observable.error(new Exception("不是Json或Url格式"));
    }

    private static Observable<Boolean> importReplaceRuleO(String json) {
        return Observable.create(e -> {
            try {
                List<ReplaceRuleBean> replaceRuleBeans = GsonUtils.parseJArray(json, ReplaceRuleBean.class);
                addDataS(replaceRuleBeans);
                e.onNext(true);
            } catch (Exception e1) {
                e1.printStackTrace();
                e.onNext(false);
            }
            e.onComplete();
        });
    }
}
