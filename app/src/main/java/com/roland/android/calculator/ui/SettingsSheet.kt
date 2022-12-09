package com.roland.android.calculator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.roland.android.calculator.R
import com.roland.android.calculator.databinding.SettingsSheetBinding
import com.roland.android.calculator.util.Preference

class SettingsSheet : BottomSheetDialogFragment() {
    private var _binding: SettingsSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SettingsSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSheet()
    }

    override fun onDestroy() { super.onDestroy(); _binding = null }

    private fun setupSheet() {
        binding.apply {
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
        }
    }
}