package com.example.quickbill.ui.qr_code_manager

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quickbill.R

class QRCodeManager : Fragment() {

    companion object {
        fun newInstance() = QRCodeManager()
    }

    private lateinit var viewModel: QRCodeManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_q_r_code_manager, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(QRCodeManagerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}