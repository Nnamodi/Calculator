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
import com.roland.android.calculator.R
import com.roland.android.calculator.databinding.FragmentHistoryBinding
import com.roland.android.calculator.ui.adapter.HistoryAdapter
import com.roland.android.calculator.viewmodel.CalculatorViewModel

class HistoryFragment : Fragment() {
    private val viewModel by viewModels<CalculatorViewModel>()
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(layoutInflater)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenuItem()
        val adapter = HistoryAdapter()
        binding.recyclerView.adapter = adapter
        viewModel.getEquation.observe(viewLifecycleOwner) { equation ->
            binding.noHistory = equation.isEmpty() // give binding-variable a value
            adapter.submitList(equation)
            Log.d("HistoryItem", "History: $equation")
        }
    }

    override fun onDestroy() {
        super.onDestroy(); _binding = null
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
}