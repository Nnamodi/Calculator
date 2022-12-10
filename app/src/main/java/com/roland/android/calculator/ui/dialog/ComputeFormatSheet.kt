package com.roland.android.calculator.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.roland.android.calculator.R
import com.roland.android.calculator.databinding.FormatComputeBinding
import com.roland.android.calculator.util.Constants.COMPUTE_FORMAT
import com.roland.android.calculator.util.Preference.getComputeFormat
import com.roland.android.calculator.util.Preference.setComputeFormat

class ComputeFormatSheet : BottomSheetDialogFragment() {
    private var _binding: FormatComputeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FormatComputeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
    }

    override fun onDestroy() { super.onDestroy(); _binding = null }

    private fun setupDialog() {
        binding.apply {
            when (getComputeFormat(requireContext())) {
                R.id.plain -> radioGroup.check(R.id.plain)
                R.id.exponent -> radioGroup.check(R.id.exponent)
                else -> radioGroup.check(R.id.fraction)
            }
            computeInfo.text = info()
            dialogTitle.setOnClickListener { findNavController().navigateUp() }
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                setComputeFormat(requireContext(), checkedId)
                computeInfo.text = info()
                findNavController().previousBackStackEntry?.savedStateHandle?.set(COMPUTE_FORMAT, checkedId)
            }
        }
    }

    private fun info() = when (binding.radioGroup.checkedRadioButtonId) {
        R.id.plain -> { getString(R.string.plain_info) }
        R.id.exponent -> { getString(R.string.exponent_info) }
        else -> { getString(R.string.fraction_info) }
    }
}