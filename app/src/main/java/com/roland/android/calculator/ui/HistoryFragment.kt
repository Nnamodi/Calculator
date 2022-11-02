package com.roland.android.calculator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.roland.android.calculator.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(layoutInflater)
        addObservers()
        return binding.root
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