package com.roland.android.calculator.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.roland.android.calculator.R
import com.roland.android.calculator.databinding.FragmentHistoryBinding
import com.roland.android.calculator.ui.adapter.HistoryAdapter
import com.roland.android.calculator.util.Constants.HISTORY
import com.roland.android.calculator.viewmodel.HistoryViewModel
import kotlinx.coroutines.flow.collectLatest

class HistoryFragment : Fragment() {
    private lateinit var menuHost: MenuHost
    private val viewModel by viewModels<HistoryViewModel>()
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private var noHistory = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(layoutInflater)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservables()
        setupMenuItem()
    }

    private fun setupObservables() {
        val adapter = HistoryAdapter(this::onClick)
        binding.recyclerView.adapter = adapter
        lifecycleScope.launchWhenStarted {
            viewModel.getEquation.collectLatest { equation ->
                adapter.submitData(equation)
            }
        }
        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { loadState ->
                // give binding-variable a value
                binding.noHistory = adapter.itemCount == 0 && loadState.refresh is LoadState.NotLoading
                noHistory = adapter.itemCount == 0
                Log.d("HistoryItem", "item(s) fetched: ${adapter.itemCount}")
                menuHost.invalidateMenu()
            }
        }
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(HISTORY, "")
            }
            if (event == Lifecycle.Event.ON_DESTROY) { _binding = null }
        })
    }

    private fun setupMenuItem() {
        menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_history, menu)
                Log.d("HistoryItem", "Created")
            }

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                menu.findItem(R.id.clear_history).isEnabled = noHistory
                Log.d("HistoryItem", "Prepared")
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                Log.d("HistoryItem", "Selected")
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
}