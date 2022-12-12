package com.roland.android.calculator.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.roland.android.calculator.R
import com.roland.android.calculator.databinding.SettingsSheetBinding
import com.roland.android.calculator.util.Constants.SET_THEME
import com.roland.android.calculator.util.Constants.THEME
import com.roland.android.calculator.util.Preference
import com.roland.android.calculator.util.Preference.getComputeFormat
import com.roland.android.calculator.viewmodel.CalculatorViewModel

class SettingsSheet : BottomSheetDialogFragment() {
    private var _binding: SettingsSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SettingsSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBackStackEntry = findNavController().getBackStackEntry(R.id.settingsSheet)
        navBackStackEntry.savedStateHandle.getLiveData<Boolean>(SET_THEME)
            .observe(viewLifecycleOwner) {
                if (it) { binding.setTheme.text = setTheme() }
            }
        setupSheet()
    }

    override fun onDestroy() { super.onDestroy(); _binding = null }

    private fun setupSheet() {
        binding.apply {
            changeTheme.setOnClickListener {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(THEME, true)
            }
            setTheme.text = setTheme()

            hapticField.setOnClickListener { hapticSwitch.isChecked = !hapticSwitch.isChecked }
            hapticSwitch.apply {
                isChecked = Preference.getHaptic(context)
                setOnCheckedChangeListener { _, switched ->
                    Preference.setHaptic(context, switched)
                    hapticInfo.text = if (switched) { getString(R.string.disable_haptic) }
                                        else { getString(R.string.enable_haptic) }
                }
            }
            hapticInfo.text = if (hapticSwitch.isChecked) { getString(R.string.disable_haptic) }
                                else { getString(R.string.enable_haptic) }

            computeFormatField.setOnClickListener {
                findNavController().navigate(R.id.computeFormatSheet)
            }
            setFormat.text = format(getComputeFormat(requireContext()))
        }
    }

    val format: (Int) -> String = { when (it) {
        R.id.plain -> { getString(R.string.plain) }
        R.id.exponent -> { getString(R.string.exponent) }
        else -> { getString(R.string.fraction) }
    }}

    val setTheme = { when (Preference.getTheme(requireContext())) {
        0 -> { getString(R.string.dark) }
        1 -> { getString(R.string.light) }
        else -> { getString(R.string.follow_system) }
    }}
}