package com.example.skincerinapp.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skincerinapp.databinding.FragmentListBinding
import com.example.skincerinapp.model.Cancer
import com.example.skincerinapp.model.CancerData


class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)

        val adapter = ListAdapter(getCancer())

        binding.rvCancer.adapter = adapter
        binding.rvCancer.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCancer(): List<Cancer> {
        return CancerData.cancer
    }
}