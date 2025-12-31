package com.example.myapkplatform.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.myapkplatform.R
import com.example.myapkplatform.ui.base.BaseActivity
import com.example.myapkplatform.ui.login.LoginActivity
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest

// 步驟 1: 建立一個新的資料類別，用來同時儲存固定的 Key 和翻譯後的標題
data class MenuNode(val key: String, val title: String)

data class DfmInfo(val moduleName: String, val fragmentClass: String)

class MainActivity : BaseActivity() {

    private val installManager by lazy {
        SplitInstallManagerFactory.create(this)
    }

    private lateinit var drawerLayout: DrawerLayout

    // 步驟 2: 修改資料結構，儲存 MenuNode 物件
    private lateinit var groups: List<MenuNode>
    private lateinit var children: Map<String, List<MenuNode>>

    // 步驟 3: 修改 dfmMap，讓它的 Key 和 MenuNode 的 Key 一致
    private val dfmMap = mapOf(
        "p30_smt_load" to DfmInfo(
            moduleName = "dfm_loadmaterial",
            fragmentClass = "com.example.myapkplatform.dfm_loadmaterial.LoadMaterialFragment"
        ),
        "p32_stencil_load" to DfmInfo(
            moduleName = "dfm_mountstencil",
            fragmentClass = "com.example.myapkplatform.dfm_mountstencil.MountStencilFragment"
        )
        // 注意：這裡的 key ('p32_stencil_load') 已經和舊的 ('Mount Stencil') 不同了
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

        setupMenuData() // 準備好多國語言的資料
        setupButtons()
        setupMenu()     // 用準備好的資料來設定選單
    }

    // 步驟 4: 在這個方法中，從 string resources 讀取翻譯，並建立 MenuNode
    private fun setupMenuData() {
        groups = listOf(
            MenuNode("system", getString(R.string.menu_system)),
            MenuNode("p30_smt", getString(R.string.menu_p30_smt)),
            MenuNode("p32_stencil_manager", getString(R.string.menu_p32_stencil_manager)),
            MenuNode("p79_oqc", getString(R.string.menu_p79_oqc)),
            MenuNode("p96_picking", getString(R.string.menu_p96_picking))
        )

        children = mapOf(
            "system" to listOf(
                MenuNode("system_logout", getString(R.string.system_logout)),
                MenuNode("system_about", getString(R.string.system_about)),
                MenuNode("system_exit", getString(R.string.system_exit))
            ),
            "p30_smt" to listOf(
                MenuNode("p30_smt_load", getString(R.string.p30_smt_load)),
                MenuNode("p30_smt_msd_baking", getString(R.string.p30_smt_msd_baking)),
                MenuNode("p30_smt_dry_box_in_out", getString(R.string.p30_smt_dry_box_in_out))
            ),
            "p32_stencil_manager" to listOf(
                MenuNode("p32_stencil_load", getString(R.string.p32_stencil_load)),
                MenuNode("p32_stencil_rank_in_out", getString(R.string.p32_stencil_rank_in_out)),
                MenuNode("p32_stencil_begin_clean", getString(R.string.p32_stencil_begin_clean)),
                MenuNode("p32_stencil_finish_clean", getString(R.string.p32_stencil_finish_clean))
            ),
            "p79_oqc" to listOf(
                MenuNode("p79_oqc_lot_code_check", getString(R.string.p79_oqc_lot_code_check)),
                MenuNode("p79_oqc_lot_code_oqc_check", getString(R.string.p79_oqc_lot_code_oqc_check))
            ),
            "p96_picking" to listOf(
                MenuNode("p96_picking_picking", getString(R.string.p96_picking_picking))
            )
        )
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btn_open_menu).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    // 步驟 5: 修改 setupMenu，讓它處理 MenuNode，並在點擊時傳遞固定的 Key
    private fun setupMenu() {
        val expandableListView = findViewById<ExpandableListView>(R.id.function_list)

        // Adapter 現在會顯示 MenuNode 中的 title 屬性
        val groupData = groups.map { mapOf("NAME" to it.title) }
        val childData = groups.map { group ->
            children[group.key]?.map { mapOf("NAME" to it.title) } ?: emptyList()
        }

        val adapter = SimpleExpandableListAdapter(
            this,
            groupData,
            android.R.layout.simple_expandable_list_item_1,
            arrayOf("NAME"),
            intArrayOf(android.R.id.text1),
            childData,
            android.R.layout.simple_list_item_1,
            arrayOf("NAME"),
            intArrayOf(android.R.id.text1)
        )

        expandableListView.setAdapter(adapter)

        expandableListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val groupKey = groups[groupPosition].key
            // 從 children 中取得被點擊的 MenuNode 物件
            val clickedNode = children[groupKey]!![childPosition]
            
            drawerLayout.closeDrawer(GravityCompat.START)
            // 傳遞固定的 Key，而不是翻譯後的標題
            handleMenuClick(clickedNode.key)
            true
        }
    }

    // 步驟 6: handleMenuClick 現在接收的是 itemKey，可以直接用於 dfmMap
    private fun handleMenuClick(itemKey: String) {
        when (itemKey) {
            "system_logout" -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            "system_exit" -> {
                finishAffinity()
            }
            "system_about" -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
            else -> {
                val dfm = dfmMap[itemKey]

                if (dfm == null) {
                    Toast.makeText(this, "點擊的功能 Key: $itemKey (此功能尚未實作)", Toast.LENGTH_SHORT).show()
                    return
                }

                loadDfm(dfm)
            }
        }
    }

    private fun loadDfm(dfm: DfmInfo) {
        if (installManager.installedModules.contains(dfm.moduleName)) {
            renderFragment(dfm.fragmentClass)
            return
        }

        Toast.makeText(this, "下載模組中…", Toast.LENGTH_SHORT).show()

        val request = SplitInstallRequest.newBuilder()
            .addModule(dfm.moduleName)
            .build()

        installManager.startInstall(request)
            .addOnSuccessListener {
                renderFragment(dfm.fragmentClass)
            }
            .addOnFailureListener {
                Toast.makeText(this, "模組下載失敗", Toast.LENGTH_SHORT).show()
            }
    }

    private fun renderFragment(fragmentClassName: String) {
        try {
            val fragmentClass = Class.forName(fragmentClassName)
            val fragment = fragmentClass.getConstructor().newInstance() as Fragment

            supportFragmentManager.beginTransaction()
                .replace(R.id.dfm_container, fragment)
                .commit()

        } catch (e: Exception) {
            Toast.makeText(this, "Fragment 載入失敗", Toast.LENGTH_SHORT).show()
        }
    }
}
