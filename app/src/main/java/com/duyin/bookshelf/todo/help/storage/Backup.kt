package com.duyin.bookshelf.todo.help.storage

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.duyin.bookshelf.DbHelper
import com.duyin.bookshelf.MApplication
import com.duyin.bookshelf.base.observer.MySingleObserver
import com.duyin.bookshelf.todo.help.BookshelfHelp
import com.duyin.bookshelf.todo.help.FileHelp
import com.duyin.bookshelf.todo.model.BookSourceManager
import com.duyin.bookshelf.todo.model.ReplaceRuleManager
import com.duyin.bookshelf.todo.model.TxtChapterRuleManager
import com.duyin.bookshelf.utils.DocumentUtil
import com.duyin.bookshelf.utils.FileUtils
import com.duyin.bookshelf.utils.GSON
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit


object Backup {

    val backupPath = MApplication.getInstance().filesDir.absolutePath + File.separator + "backup"

    val defaultPath by lazy {
        FileUtils.getSdCardPath() + File.separator + "YueDu"
    }

    val backupFileNames by lazy {
        arrayOf(
                "myBookShelf.json",
                "myBookSource.json",
                "myBookSearchHistory.json",
                "myBookReplaceRule.json",
                "myTxtChapterRule.json",
                "config.xml"
        )
    }

    fun autoBack() {
        val lastBackup = MApplication.getConfigPreferences().getLong("lastBackup", 0)
        if (System.currentTimeMillis() - lastBackup < TimeUnit.DAYS.toMillis(1)) {
            return
        }
        val path = MApplication.getConfigPreferences().getString("backupPath", defaultPath)
        if (path == null) {
            backup(MApplication.getInstance(), defaultPath, null)
        } else {
            backup(MApplication.getInstance(), path, null)
        }
    }

    fun backup(context: Context, path: String, callBack: CallBack?) {
        MApplication.getConfigPreferences().edit().putLong("lastBackup", System.currentTimeMillis()).apply()
        Single.create(SingleOnSubscribe<Boolean> { e ->
            BookshelfHelp.getAllBook().let {
                if (it.isNotEmpty()) {
                    val json = GSON.toJson(it)
                    FileHelp.createFileIfNotExist(backupPath + File.separator + "myBookShelf.json").writeText(json)
                }
            }
            BookSourceManager.getAllBookSource().let {
                if (it.isNotEmpty()) {
                    val json = GSON.toJson(it)
                    FileHelp.createFileIfNotExist(backupPath + File.separator + "myBookSource.json").writeText(json)
                }
            }
            DbHelper.getDaoSession().searchHistoryBeanDao.queryBuilder().list().let {
                if (it.isNotEmpty()) {
                    val json = GSON.toJson(it)
                    FileHelp.createFileIfNotExist(backupPath + File.separator + "myBookSearchHistory.json")
                            .writeText(json)
                }
            }
            ReplaceRuleManager.getAll().blockingGet().let {
                if (it.isNotEmpty()) {
                    val json = GSON.toJson(it)
                    FileHelp.createFileIfNotExist(backupPath + File.separator + "myBookReplaceRule.json").writeText(json)
                }
            }
            TxtChapterRuleManager.getAll().let {
                if (it.isNotEmpty()) {
                    val json = GSON.toJson(it)
                    FileHelp.createFileIfNotExist(backupPath + File.separator + "myTxtChapterRule.json")
                            .writeText(json)
                }
            }
            Preferences.getSharedPreferences(context, backupPath, "config")?.let { sp ->
                val edit = sp.edit()
                MApplication.getConfigPreferences().all.map {
                    when (val value = it.value) {
                        is Int -> edit.putInt(it.key, value)
                        is Boolean -> edit.putBoolean(it.key, value)
                        is Long -> edit.putLong(it.key, value)
                        is Float -> edit.putFloat(it.key, value)
                        is String -> edit.putString(it.key, value)
                        else -> Unit
                    }
                }
                edit.commit()
            }
            WebDavHelp.backUpWebDav(backupPath)
            if (path.isContentPath()) {
                copyBackup(context, Uri.parse(path))
            } else {
                copyBackup(path)
            }
            e.onSuccess(true)
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySingleObserver<Boolean>() {
                    override fun onSuccess(t: Boolean) {
                        callBack?.backupSuccess()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        callBack?.backupError(e.localizedMessage ?: "ERROR")
                    }
                })
    }

    @Throws(Exception::class)
    private fun copyBackup(context: Context, uri: Uri) {
        DocumentFile.fromTreeUri(context, uri)?.let { treeDoc ->
            for (fileName in backupFileNames) {
                val file = File(backupPath + File.separator + fileName)
                if (file.exists()) {
                    val doc = treeDoc.findFile(fileName) ?: treeDoc.createFile("", fileName)
                    doc?.let {
                        DocumentUtil.writeBytes(context, file.readBytes(), doc)
                    }
                }
            }
        }
    }

    @Throws(java.lang.Exception::class)
    private fun copyBackup(path: String) {
        for (fileName in backupFileNames) {
            val file = File(backupPath + File.separator + fileName)
            if (file.exists()) {
                file.copyTo(FileHelp.createFileIfNotExist(path + File.separator + fileName), true)
            }
        }
    }

    interface CallBack {
        fun backupSuccess()
        fun backupError(msg: String)
    }
}