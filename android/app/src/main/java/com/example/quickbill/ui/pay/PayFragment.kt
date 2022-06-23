package com.example.quickbill.ui.pay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.quickbill.R

class PayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val view = this
                Column {
                    Text(text = "This is the pay fragment")
                    // fixme: use inside code to transition to bill screen and populate bundle
                    Button(
                        onClick = {
                            val bundle = bundleOf("location_id" to "deadbeef", "table_number" to 0)
                            Navigation.findNavController(view)
                                .navigate(R.id.action_navigation_pay_to_billFragment, bundle)
                        }) {
                        Text(text = "go to list")
                    }
                }
            }
        }
    }
}