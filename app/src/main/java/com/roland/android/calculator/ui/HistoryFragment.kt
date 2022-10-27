package com.roland.android.calculator.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.roland.android.calculator.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MenuItemStuff", "History - OnCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        Log.i("MenuItemStuff", "History - OnCreateView")
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.i("MenuItemStuff", "History - onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i("MenuItemStuff", "History - onResume")
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }
}