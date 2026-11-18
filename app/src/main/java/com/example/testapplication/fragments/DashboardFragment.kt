package com.example.testapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.R
import com.example.testapplication.adapters.TeamFollowUpAdapter
import com.example.testapplication.databinding.FragmentDashboardBinding
import com.example.testapplication.utils.ApiStatus
import com.example.testapplication.viewmodels.DashboardViewModel
import com.example.testapplication.viewmodels.DashboardViewModelFactory

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var teamFollowUpAdapter: TeamFollowUpAdapter

    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(
            requireContext().applicationContext
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDashboardBinding.bind(view)

        setupTeamFollowUpRecyclerView()

        setupClickListeners()

        observeDashboardResponse()

        // Load dashboard data from dummy JSON
        viewModel.getDashboard()
    }

    // ==================================================
    // TEAM FOLLOW-UP RECYCLER VIEW
    // ==================================================

    private fun setupTeamFollowUpRecyclerView() {

        teamFollowUpAdapter = TeamFollowUpAdapter { selectedConsultant ->

//            val intent = Intent(
//                requireContext(),
//                TeamFollowUpActivity::class.java
//            )
//
//            intent.putExtra(
//                "CONSULTANT_ID",
//                selectedConsultant.id
//            )
//
//            intent.putExtra(
//                "CONSULTANT_NAME",
//                selectedConsultant.consultantName
//            )
//
//            startActivity(intent)
        }

        binding.rvTeamFollowUps.apply {

            layoutManager = LinearLayoutManager(requireContext())

            adapter = teamFollowUpAdapter

            isNestedScrollingEnabled = true

            setHasFixedSize(true)
        }
    }

    // ==================================================
    // CLICK LISTENERS
    // ==================================================

    private fun setupClickListeners() {

        // Selling Enquiry List navigation

        binding.cardSellingEnquiry.setOnClickListener {

//            val intent = Intent(
//                requireContext(),
//                SellingEnquiryListActivity::class.java
//            )
//
//            startActivity(intent)
        }
    }

    // ==================================================
    // OBSERVE DASHBOARD RESPONSE
    // ==================================================

    private fun observeDashboardResponse() {

        viewModel.dashboardResponse.observe(viewLifecycleOwner) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {

                    // Add dashboard progress indicator if required.

                }

                ApiStatus.Status.SUCCESS -> {

                    val dashboard = resource.data?.data

                    if (dashboard == null) {

                        Toast.makeText(
                            requireContext(),
                            "Dashboard data not available",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@observe
                    }

                    displayDashboardData(dashboard)
                }

                ApiStatus.Status.ERROR -> {

                    binding.layoutTeamFollowUps.visibility = View.GONE

                    binding.cardSellingEnquiry.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        resource.message ?: "Unable to load dashboard",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // ==================================================
    // DISPLAY DASHBOARD DATA
    // ==================================================

    private fun displayDashboardData(
        dashboard: com.example.testapplication.datamodels.DashboardData
    ) {

        // Consultant name

        binding.tvConsultantName.text =
            dashboard.consultantName

        // Total Follow-ups

        binding.tvTotalFollowUps.text =
            dashboard.totalFollowUps.toString()

        // Call Back count

        binding.tvCallBackCount.text =
            String.format("%03d", dashboard.callBackCount)

        // Dealer Visit count

        binding.tvDealerVisitCount.text =
            String.format("%03d", dashboard.dealerVisitCount)

        // Team Follow-ups list

        teamFollowUpAdapter.submitList(
            dashboard.teamFollowUps
        )

        // ==================================================
        // ROLE-BASED VISIBILITY
        // ==================================================

        /*
         * Temporary values for testing.
         *
         * Later replace these with your actual role conditions.
         *
         * Team Follow-ups and Selling Enquiry List
         * must never be visible together.
         */

        val showTeamFollowUps = true
        val showSellingEnquiry = false

        updateDashboardSections(
            showTeamFollowUps = showTeamFollowUps,
            showSellingEnquiry = showSellingEnquiry
        )
    }

    // ==================================================
    // SHOW / HIDE DASHBOARD SECTIONS
    // ==================================================

    private fun updateDashboardSections(
        showTeamFollowUps: Boolean,
        showSellingEnquiry: Boolean
    ) {

        val shouldShowTeam =
            showTeamFollowUps && !showSellingEnquiry

        val shouldShowSelling =
            showSellingEnquiry && !showTeamFollowUps

        binding.layoutTeamFollowUps.visibility =
            if (shouldShowTeam) View.VISIBLE else View.GONE

        binding.cardSellingEnquiry.visibility =
            if (shouldShowSelling) View.VISIBLE else View.GONE
    }

    // ==================================================
    // FRAGMENT DESTROY
    // ==================================================

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}