package com.example.myapkplatform.ui.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.myapkplatform.util.LocaleManager

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.attachBaseContext(newBase))
    }

    /**
     * 一個輔助方法，用來快速設定 Toolbar 和標題。
     * @param toolbar 您在 layout 檔案中定義的 Toolbar 元件。
     * @param title 您想要顯示的標題文字。
     */
    protected fun setupToolbar(toolbar: Toolbar, title: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
    }
}
