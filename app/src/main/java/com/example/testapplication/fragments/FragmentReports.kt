
package com.example.testapplication.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.R
import com.example.testapplication.adapters.LeaderboardAdapter
import com.example.testapplication.databinding.FragmentReportsBinding
import com.example.testapplication.datamodels.ReportsData
import com.example.testapplication.utils.ApiStatus

import com.example.testapplication.viewmodels.ReportsViewModel
import com.example.testapplication.viewmodels.ReportsViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class FragmentReports : Fragment(R.layout.fragment_reports) {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private lateinit var leaderboardAdapter: LeaderboardAdapter

    private val viewModel: ReportsViewModel by viewModels {
        ReportsViewModelFactory(
            requireContext().applicationContext
        )
    }

    private var isManager = false

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentReportsBinding.bind(view)

        setupLeaderboardRecyclerView()

        setupPieChart()

        // Get logged-in user type
        isManager = getUserType() == LoginUsers.H_PROMISE_MANAGER

        // Set initial visibility
        binding.layoutLeaderboard.visibility =
            if (isManager) View.VISIBLE else View.GONE

        observeReports()

        // Observe leaderboard only for manager
        if (isManager) {
            observeLeaderboard()
        }

        // Reports API is called for all users
        viewModel.getReports()

        // Leaderboard API is called only for H Promise Manager
        if (isManager) {
            viewModel.getLeaderboard()
        }
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
    // LEADERBOARD RECYCLER VIEW
    // ==========================================

    private fun setupLeaderboardRecyclerView() {

        leaderboardAdapter = LeaderboardAdapter()

        binding.rvLeaderboard.apply {

            layoutManager = LinearLayoutManager(requireContext())

            adapter = leaderboardAdapter

            isNestedScrollingEnabled = false

            setHasFixedSize(true)
        }
    }

    // ==========================================
    // PIE CHART INITIALIZATION
    // ==========================================

    private fun setupPieChart() {

        binding.pieChartSellingReport.apply {

            description.isEnabled = false

            legend.isEnabled = false

            setDrawEntryLabels(false)

            setDrawMarkers(false)

            setUsePercentValues(false)

            isRotationEnabled = false

            isHighlightPerTapEnabled = false

            isDrawHoleEnabled = true

            holeRadius = 65f

            transparentCircleRadius = 68f

            setHoleColor(Color.WHITE)

            setTransparentCircleColor(Color.WHITE)

            setTransparentCircleAlpha(0)

            setDrawCenterText(false)

            setNoDataText("No report data available")

            setExtraOffsets(0f, 0f, 0f, 0f)

            setTouchEnabled(false)
        }
    }

    // ==========================================
    // UPDATE PIE CHART
    // ==========================================

    private fun updatePieChart(
        enquiredCount: Int,
        invoiceConfirmedCount: Int
    ) {

        val entries = arrayListOf<PieEntry>()

        if (enquiredCount > 0) {
            entries.add(
                PieEntry(enquiredCount.toFloat(), "Enquired")
            )
        }

        if (invoiceConfirmedCount > 0) {
            entries.add(
                PieEntry(
                    invoiceConfirmedCount.toFloat(),
                    "Invoice Confirmed"
                )
            )
        }

        if (entries.isEmpty()) {

            binding.pieChartSellingReport.clear()

            binding.pieChartSellingReport.setNoDataText(
                "No report data available"
            )

            binding.pieChartSellingReport.invalidate()

            return
        }

        val dataSet = PieDataSet(entries, "").apply {

            colors = listOf(
                Color.rgb(0, 169, 199),
                Color.rgb(165, 233, 0)
            )

            sliceSpace = 0f

            selectionShift = 0f

            setDrawValues(false)
        }

        val pieData = PieData(dataSet)

        binding.pieChartSellingReport.apply {

            data = pieData

            setDrawEntryLabels(false)

            legend.isEnabled = false

            description.isEnabled = false

            rotationAngle = 270f

            animateY(900, Easing.EaseInOutQuad)

            invalidate()
        }
    }

    // ==========================================
    // OBSERVE REPORTS API
    // ==========================================

    private fun observeReports() {

        viewModel.reportsResponse.observe(viewLifecycleOwner) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {
                    // Show reports loader if required.
                }

                ApiStatus.Status.SUCCESS -> {

                    val reportData = resource.data?.data

                    if (reportData == null) {

                        Toast.makeText(
                            requireContext(),
                            "Report data not available",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@observe
                    }

                    displayReportData(reportData)
                }

                ApiStatus.Status.ERROR -> {

                    Toast.makeText(
                        requireContext(),
                        resource.message ?: "Unable to load reports",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // ==========================================
    // DISPLAY REPORT DATA
    // ==========================================

    private fun displayReportData(
        reportData: ReportsData
    ) {

        binding.tvEnquiredCount.text =
            reportData.enquiredCount.toString()

        binding.tvInvoiceConfirmedCount.text =
            reportData.invoiceConfirmedCount.toString()

        updatePieChart(
            enquiredCount = reportData.enquiredCount,
            invoiceConfirmedCount = reportData.invoiceConfirmedCount
        )
    }

    // ==========================================
    // OBSERVE LEADERBOARD API
    // ==========================================

    private fun observeLeaderboard() {

        viewModel.leaderboardResponse.observe(viewLifecycleOwner) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {
                    // Add leaderboard-specific loader if required.
                }

                ApiStatus.Status.SUCCESS -> {

                    val leaderboard = resource.data?.data.orEmpty()

                    leaderboardAdapter.submitList(leaderboard)

                    binding.layoutLeaderboard.visibility =
                        View.VISIBLE
                }

                ApiStatus.Status.ERROR -> {

                    leaderboardAdapter.submitList(emptyList())

                    binding.layoutLeaderboard.visibility =
                        View.GONE

                    Toast.makeText(
                        requireContext(),
                        resource.message ?: "Unable to load leaderboard",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // ==========================================
    // FRAGMENT DESTROY
    // ==========================================

    override fun onDestroyView() {

        binding.pieChartSellingReport.clear()

        super.onDestroyView()

        _binding = null
    }
}
