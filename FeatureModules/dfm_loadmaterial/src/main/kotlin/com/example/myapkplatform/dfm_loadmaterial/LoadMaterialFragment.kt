package com.example.myapkplatform.dfm_loadmaterial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myapkplatform.R as AppR

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
            val isMsd = if (checkMsd.isChecked) getString(AppR.string.yes) else getString(AppR.string.no)

            if (partNo.isEmpty() || qty.isEmpty()) {
                Toast.makeText(context, AppR.string.load_material_enter_part_no_and_qty, Toast.LENGTH_SHORT).show()
            } else {
                val toastMessage = getString(AppR.string.load_material_success_toast, partNo, qty, isMsd)
                Toast.makeText(
                    context,
                    toastMessage,
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
