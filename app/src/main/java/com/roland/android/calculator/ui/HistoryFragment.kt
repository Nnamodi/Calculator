package com.roland.android.calculator.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.roland.android.calculator.R
import com.roland.android.calculator.databinding.FragmentHistoryBinding
import com.roland.android.calculator.ui.adapter.HistoryAdapter
import com.roland.android.calculator.util.Constants.HISTORY
import com.roland.android.calculator.viewmodel.HistoryViewModel
import kotlinx.coroutines.flow.collectLatest

class HistoryFragment : Fragment() {
    private val viewModel by viewModels<HistoryViewModel>()
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

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
//                binding.noHistory = equation.isEmpty() // give binding-variable a value
                adapter.submitData(equation)
                Log.d("HistoryItem", "History: $equation")
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
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_history, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.clear_history -> {
                        Toast.makeText(
                            requireContext(),
                            "Feature will be availed soon!",
                            Toast.LENGTH_SHORT
                        ).show(); true
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