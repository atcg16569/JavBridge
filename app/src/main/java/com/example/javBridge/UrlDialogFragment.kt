package com.example.javBridge

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.viewModelScope
import com.example.javBridge.database.Url
import com.example.javBridge.viewModel.UrlViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UrlDialogFragment(private val urlViewModel: UrlViewModel) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_url_dialog, null)
            builder.apply {
                setView(view)
                setPositiveButton("save") { _, _ ->
                    val name = view.findViewById<EditText>(R.id.urlName).text.toString()
                    val link = view.findViewById<EditText>(R.id.urlLink).text.toString()
                    if (name.isNotEmpty() && link.isNotEmpty() && link.startsWith("http")) {
                        urlViewModel.viewModelScope.launch(Dispatchers.IO) {
                            if (urlViewModel.urlByName(name) == null) {
                                val ur = Url(name, link)
                                urlViewModel.add(ur)
                            } else {
                                Log.d("addUrl", "$name existed")
                            }
                        }
                    }
                }
                setNegativeButton("cancel") { _, _ ->
                    dialog?.cancel()
                }
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}