package com.example.testapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.adapters.EnquiryAdapter
import com.example.testapplication.databinding.FragmentEnquriesListBinding
import com.example.testapplication.enquiryFeature.EnquiryViewModel
import com.example.testapplication.enquiryFeature.EnquiryViewModelFactory
import com.example.testapplication.enquiryFeature.SellingEnquiryDetailsActivity
import com.example.testapplication.utils.ApiStatus

class enquriesListFragment : Fragment() {

    private var _binding: FragmentEnquriesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var enquiryAdapter: EnquiryAdapter

    private val viewModel: EnquiryViewModel by viewModels {
        EnquiryViewModelFactory(requireContext())
    }

    // Replace this with your actual saved login token.
    private val token: String
        get() = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEnquriesListBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeEnquiries()

        viewModel.getEnquiries(token)
    }

    private fun setupRecyclerView() {

        enquiryAdapter = EnquiryAdapter { enquiry ->

            val intent = Intent(
                requireContext(),
                SellingEnquiryDetailsActivity::class.java
            )

            intent.putExtra(
                SellingEnquiryDetailsActivity.EXTRA_ENQUIRY,
                enquiry
            )

            startActivity(intent)
        }

        binding.rvEnquiries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = enquiryAdapter
        }
    }

    private fun setupListeners() {

        binding.btnRetry.setOnClickListener {
            viewModel.getEnquiries(token)
        }

        binding.fabAddEnquiry.setOnClickListener {
            // Navigate to Add Enquiry screen.
        }
    }

    private fun observeEnquiries() {

        viewModel.enquiryResponse.observe(viewLifecycleOwner) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {

                    hideAllStates()
                    binding.progressBar.visibility = View.VISIBLE
                }

                ApiStatus.Status.SUCCESS -> {

                    hideAllStates()

                    val response = resource.data

                    if (response?.status == true) {

                        if (response.data.isNotEmpty()) {

                            binding.rvEnquiries.visibility = View.VISIBLE

                            enquiryAdapter.submitList(response.data)

                        } else {

                            binding.tvEmpty.visibility = View.VISIBLE
                        }

                    } else {

                        binding.layoutError.visibility = View.VISIBLE
                        binding.tvErrorMessage.text =
                            response?.message ?: "Unable to load enquiries"
                    }
                }

                ApiStatus.Status.ERROR -> {

                    hideAllStates()

                    binding.layoutError.visibility = View.VISIBLE

                    binding.tvErrorMessage.text =
                        resource.message ?: "Something went wrong"
                }
            }
        }
    }

    private fun showLoading() {

        hideAllStates()
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideAllStates() {

        binding.progressBar.visibility = View.GONE
        binding.rvEnquiries.visibility = View.GONE
        binding.tvEmpty.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}