package com.roland.android.calculator.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.roland.android.calculator.R
import com.roland.android.calculator.databinding.FragmentHistoryBinding
import com.roland.android.calculator.ui.adapter.HistoryAdapter
import com.roland.android.calculator.util.Constants.HISTORY
import com.roland.android.calculator.util.Constants.NAVIGATE
import com.roland.android.calculator.util.Preference
import com.roland.android.calculator.util.Utility.lifecycleObserver
import com.roland.android.calculator.viewmodel.HistoryViewModel
import kotlinx.coroutines.flow.collectLatest

class HistoryFragment : Fragment() {
    private val viewModel by viewModels<HistoryViewModel>()
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private var noHistory = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(layoutInflater)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        lifecycleObserver()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservables()
        setupMenuItem()
        setupBanner()
    }

    override fun onDestroy() { super.onDestroy(); _binding = null }

    private fun setupObservables() {
        val adapter = HistoryAdapter(this::onClick)
        binding.recyclerView.adapter = adapter
        lifecycleScope.launchWhenStarted {
            viewModel.getEquation.collectLatest {
                adapter.submitData(it)
            }
        }
        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest {
                // give binding-variable a value
                binding.noHistory = adapter.itemCount == 0 && it.refresh is LoadState.NotLoading
                noHistory = adapter.itemCount == 0
                Log.d("HistoryItem", "item(s) fetched: ${adapter.itemCount}")
            }
        }
    }

    private fun setupMenuItem() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_history, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                menu.findItem(R.id.clear_history).isEnabled = !noHistory
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.clear_history -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage(getString(R.string.delete_dialog_title))
                            .setPositiveButton(getString(R.string.dialog_dismiss)) { _, _ -> }
                            .setNeutralButton(getString(R.string.dialog_clear)) { _, _ ->
                                viewModel.clearHistory()
                            }.show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun onClick(text: String) {
        findNavController().apply {
            previousBackStackEntry?.savedStateHandle?.set(HISTORY, text)
            navigateUp()
        }
    }

    private fun setupBanner() {
        binding.apply {
            if (!Preference.getSaveHistory(requireContext()) &&
                !Preference.getDismissStatus(requireContext())) {
                historyInfo.visibility = View.VISIBLE
                if (this@HistoryFragment.noHistory) {
                    infoText.text = getString(R.string.info_text_1)
                }
                historyInfo.setOnClickListener {}
                actionButton.setOnClickListener {
                    if (!Preference.getDismissStatus(requireContext())) {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(NAVIGATE, 3)
                        Handler(getMainLooper()).postDelayed({ findNavController().popBackStack() }, 10)
                    }
                }
                dismiss.setOnClickListener {
                    Preference.setDismissStatus(requireContext(), true)
                    historyInfo.dismiss()
                }
            }
        }
    }

    private fun View.dismiss() {
        val start = left.toFloat()
        val end = binding.root.right.toFloat()
        val animator = ObjectAnimator
            .ofFloat(this, "x", start, end)
            .setDuration(200)
        animator.start()
        Handler(getMainLooper()).postDelayed({ visibility = View.GONE }, 200)
    }
}