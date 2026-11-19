
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
import com.example.testapplication.datamodels.DashboardData
import com.example.testapplication.datamodels.TeamFollowUpItem
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

    private var isManager = false

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDashboardBinding.bind(view)

        // Read logged-in user type
        isManager = getUserType() == LoginUsers.H_PROMISE_MANAGER

        setupTeamFollowUpRecyclerView()

        setupClickListeners()

        // Observe general dashboard API
        observeDashboardResponse()

        // Observe team API only for manager
        if (isManager) {
            observeTeamFollowUpResponse()
        }

        // Dashboard details API is called for all users
        viewModel.getDashboard()

        // Team Follow-ups API only for H Promise Manager
        if (isManager) {
            viewModel.getTeamFollowUps()
        }

        // Set role-based visibility immediately
        updateDashboardSections()
    }

    // ==========================================
    // GET USER TYPE
    // ==========================================

    private fun getUserType(): String {

        return GlobalSharedPreference.getSharedPreference(
            "login",
            requireActivity()
        ).getString("user", "").orEmpty()
    }

    // ==========================================
    // TEAM FOLLOW-UP RECYCLER VIEW
    // ==========================================

    private fun setupTeamFollowUpRecyclerView() {

        teamFollowUpAdapter = TeamFollowUpAdapter { selectedConsultant ->

            // Add TeamFollowUpActivity navigation here when ready.

            /*
            val intent = Intent(
                requireContext(),
                TeamFollowUpActivity::class.java
            )

            intent.putExtra("CONSULTANT_ID", selectedConsultant.id)

            intent.putExtra(
                "CONSULTANT_NAME",
                selectedConsultant.consultantName
            )

            startActivity(intent)
            */
        }

        binding.rvTeamFollowUps.apply {

            layoutManager = LinearLayoutManager(requireContext())

            adapter = teamFollowUpAdapter

            isNestedScrollingEnabled = true

            setHasFixedSize(true)
        }
    }

    // ==========================================
    // CLICK LISTENERS
    // ==========================================

    private fun setupClickListeners() {

        binding.cardSellingEnquiry.setOnClickListener {

            // Add SellingEnquiryListActivity navigation here.

            /*
            val intent = Intent(
                requireContext(),
                SellingEnquiryListActivity::class.java
            )

            startActivity(intent)
            */
        }
    }

    // ==========================================
    // OBSERVE DASHBOARD DETAILS API
    // ==========================================

    private fun observeDashboardResponse() {

        viewModel.dashboardResponse.observe(viewLifecycleOwner) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {
                    // Dashboard details loading.
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

                    Toast.makeText(
                        requireContext(),
                        resource.message ?: "Unable to load dashboard",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // ==========================================
    // DISPLAY DASHBOARD DETAILS
    // ==========================================

    private fun displayDashboardData(
        dashboard: DashboardData
    ) {

        binding.tvConsultantName.text =
            dashboard.consultantName

        binding.tvTotalFollowUps.text =
            dashboard.totalFollowUps.toString()

        binding.tvCallBackCount.text =
            String.format("%03d", dashboard.callBackCount)

        binding.tvDealerVisitCount.text =
            String.format("%03d", dashboard.dealerVisitCount)
    }

    // ==========================================
    // OBSERVE TEAM FOLLOW-UPS API
    // ==========================================

    private fun observeTeamFollowUpResponse() {

        viewModel.teamFollowUpResponse.observe(viewLifecycleOwner) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {
                    // Add team follow-ups loader if required.
                }

                ApiStatus.Status.SUCCESS -> {

                    val teamFollowUps = resource.data?.data.orEmpty()

                    displayTeamFollowUps(teamFollowUps)
                }

                ApiStatus.Status.ERROR -> {

                    teamFollowUpAdapter.submitList(emptyList())

                    Toast.makeText(
                        requireContext(),
                        resource.message
                            ?: "Unable to load team follow-ups",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // ==========================================
    // DISPLAY TEAM FOLLOW-UPS
    // ==========================================

    private fun displayTeamFollowUps(
        teamFollowUps: List<TeamFollowUpItem>
    ) {

        teamFollowUpAdapter.submitList(teamFollowUps)

        binding.layoutTeamFollowUps.visibility =
            if (teamFollowUps.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    // ==========================================
    // ROLE-BASED VISIBILITY
    // ==========================================

    private fun updateDashboardSections() {

        when (getUserType()) {

            LoginUsers.SALES_CONSULTANT -> {

                binding.layoutTeamFollowUps.visibility = View.GONE

                binding.cardSellingEnquiry.visibility = View.VISIBLE
            }

            LoginUsers.H_PROMISE_MANAGER -> {

                binding.layoutTeamFollowUps.visibility = View.VISIBLE

                binding.cardSellingEnquiry.visibility = View.GONE
            }

            else -> {

                binding.layoutTeamFollowUps.visibility = View.GONE

                binding.cardSellingEnquiry.visibility = View.GONE
            }
        }
    }

    // ==========================================
    // FRAGMENT DESTROY
    // ==========================================

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
