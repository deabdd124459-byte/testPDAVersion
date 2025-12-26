package com.example.myapkplatform.dfm_loadmaterial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class LoadMaterialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_load_material, container, false)

        val editPartNo = view.findViewById<EditText>(R.id.edit_part_no)
        val editQty = view.findViewById<EditText>(R.id.edit_qty)
        val checkMsd = view.findViewById<CheckBox>(R.id.check_msd)
        val btnLoad = view.findViewById<Button>(R.id.btn_load)
        val btnClose = view.findViewById<Button>(R.id.btn_close) // ← 必須有

        btnLoad.setOnClickListener {
            val partNo = editPartNo.text.toString()
            val qty = editQty.text.toString()
            val isMsd = if (checkMsd.isChecked) "是" else "否"

            if (partNo.isEmpty() || qty.isEmpty()) {
                Toast.makeText(context, "請輸入料號與數量", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    context,
                    "料號 $partNo\n數量 $qty\nMSD: $isMsd\n領料成功！",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        btnClose.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .remove(this)
                .commit()
        }

        return view
    }
}
