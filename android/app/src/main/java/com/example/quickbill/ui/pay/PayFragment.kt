package com.example.quickbill.ui.pay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.quickbill.R
import com.example.quickbill.databinding.FragmentPayBinding

class PayFragment : Fragment() {

    private var _binding: FragmentPayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val payViewModel =
            ViewModelProvider(this).get(PayViewModel::class.java)

        _binding = FragmentPayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPay
        payViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // fixme: doesn't show up sometimes
        val button = binding.goToBillList

        // fixme: use inside code to transition to bill screen and populate bundle
        button.setOnClickListener { view ->
            val bundle = bundleOf("location_id" to "deadbeef", "table_number" to 0)
            view.findNavController()
                .navigate(R.id.action_navigation_pay_to_billFragment, bundle)

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}