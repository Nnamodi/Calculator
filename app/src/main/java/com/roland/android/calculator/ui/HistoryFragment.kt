package com.roland.android.calculator.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
        addObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = HistoryAdapter()
        binding.recyclerView.adapter = adapter
        viewModel.getEquation.observe(viewLifecycleOwner) { equation ->
            binding.noHistory = equation.isEmpty() // give binding-variable a value
            adapter.submitList(equation)
            Log.d("HistoryItem", "History: $equation")
        }
    }

    private fun addObservers() {
        val activity = (activity as AppCompatActivity)
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) { activity.setSupportActionBar(binding.toolbar)
                activity.supportActionBar?.title = getString(R.string.history)
                activity.supportActionBar?.setDisplayHomeAsUpEnabled(true) }
            if (event == Lifecycle.Event.ON_DESTROY) { _binding = null }
        })
    }
}