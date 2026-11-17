package com.example.testapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.R
import com.example.testapplication.adapters.StockAdapter
import com.example.testapplication.databinding.FragmentStockBinding
import com.example.testapplication.utils.ApiStatus

class StockFragment : Fragment(R.layout.fragment_stock) {

    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!

    private lateinit var stockAdapter: StockAdapter

    private val viewModel: StockViewModel by viewModels {
        StockViewModelFactory(requireContext().applicationContext)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentStockBinding.bind(view)

        setupStockRecyclerView()

        observeStocks()

        viewModel.getStocks()
    }

    // ==========================================
    // RECYCLER VIEW
    // ==========================================

    private fun setupStockRecyclerView() {

        stockAdapter = StockAdapter { selectedStock ->

            val intent = Intent(
                requireContext(),
                StockDetailsActivity::class.java
            )

            intent.putExtra(
                StockDetailsActivity.EXTRA_STOCK_ID,
                selectedStock.id
            )

            startActivity(intent)
        }

        binding.rvStocks.apply {

            layoutManager = LinearLayoutManager(requireContext())

            adapter = stockAdapter

            setHasFixedSize(false)
        }
    }

    // ==========================================
    // OBSERVE STOCK RESPONSE
    // ==========================================

    private fun observeStocks() {

        viewModel.stockResponse.observe(viewLifecycleOwner) { resource ->

            binding.progressStocks.visibility = View.GONE
            binding.rvStocks.visibility = View.GONE
            binding.layoutNoStocks.visibility = View.GONE
            binding.layoutStockError.visibility = View.GONE

            when (resource.status) {

                ApiStatus.Status.LOADING -> {

                    binding.progressStocks.visibility = View.VISIBLE
                }

                ApiStatus.Status.SUCCESS -> {

                    val stocks = resource.data?.data.orEmpty()

                    if (stocks.isNotEmpty()) {

                        binding.rvStocks.visibility = View.VISIBLE
                        stockAdapter.submitList(stocks)

                    } else {

                        binding.layoutNoStocks.visibility = View.VISIBLE
                    }
                }

                ApiStatus.Status.ERROR -> {

                    binding.layoutStockError.visibility = View.VISIBLE

                    binding.tvStockError.text =
                        resource.message ?: "Unable to load stocks"
                }
            }
        }

        binding.btnRetryStocks.setOnClickListener {
            viewModel.getStocks()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}