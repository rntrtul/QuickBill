package com.example.quickbill.ui.pay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.quickbill.R
import com.example.quickbill.api.API
import com.example.quickbill.util.getScanResult
import com.example.quickbill.util.startScan
import org.w3c.dom.Text

class PayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val payViewModel: PayViewModel = viewModel()
                val text by payViewModel.text.observeAsState()

                val view = this
                Column {
                    text?.let { Text(text = it) }
                    // fixme: use inside code to transition to bill screen and populate bundle
                    Button(
                        onClick = {
                            activity?.let { startScan(it) };
                            /*val bundle =
                                bundleOf("location_id" to "deadbeef", "table_number" to 0)
                            Navigation.findNavController(view)
                                .navigate(R.id.action_navigation_pay_to_billFragment, bundle)*/
                        }) {
                        Text(text = "SCAN QR CODE")
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if ( API.instance.isQrCodeScanned() ) {
            API.instance.callBill() // Need to make call in case bill changes
            findNavController(this).navigate(R.id.action_navigation_pay_to_billFragment)
        }
    }
}
