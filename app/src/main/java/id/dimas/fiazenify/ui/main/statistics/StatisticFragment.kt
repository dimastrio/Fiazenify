package id.dimas.fiazenify.ui.main.statistics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.databinding.FragmentStatisticBinding
import id.dimas.fiazenify.util.Helper
import id.dimas.fiazenify.util.Result


@AndroidEntryPoint
class StatisticFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!

    private val statisticViewModel: StatisticViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStatisticBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStatistic()
        setupRefresh()
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            observeStatistic()
        }
    }

    fun refresh() {
        observeStatistic()
    }

    private fun setupChart(dataList: ArrayList<PieEntry>) {

        binding.apply {

            pieChart.setUsePercentValues(true)


            val dataSet = PieDataSet(dataList, "")
            val data = PieData(dataSet)


            data.setValueFormatter(PercentFormatter())
            pieChart.data = data
            pieChart.invalidate()



            pieChart.description.isEnabled = false

            pieChart.isDrawHoleEnabled = true
            pieChart.transparentCircleRadius = 25f
            pieChart.holeRadius = 18f

            var counter = 0
            val colors = arrayListOf<Int>()
            for (i in ColorTemplate.MATERIAL_COLORS) {
                colors.add(i)
                counter++
            }

            for (i in ColorTemplate.JOYFUL_COLORS) {
                colors.add(i)
                counter++
            }

            for (i in ColorTemplate.COLORFUL_COLORS) {
                colors.add(i)
                counter++
            }


            dataSet.colors = colors


            data.setValueTextSize(13f)
            data.setValueTextColor(Color.WHITE)


            val legend: Legend = pieChart.legend

            legend.isEnabled = false
        }
    }


    private fun observeStatistic() {
        statisticViewModel.getStatisctic().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null) {
                        binding.swipeRefresh.isRefreshing = false

                        val yvalues = ArrayList<PieEntry>()

                        if (result.data.isNotEmpty()) {
                            binding.pieChart.isVisible = true
                            result.data.forEach {
                                yvalues.add((PieEntry(it.totalData, it.name)))
                            }

                            if (yvalues.isNotEmpty()) {
                                binding.tvAlertMessage.isGone = true
                                setupChart(yvalues)
                            }
                        } else {
                            binding.tvAlertMessage.isVisible = true
                            setupChart(yvalues)
                            binding.pieChart.isGone = true
                        }


                    }

                }

                is Result.Error -> {
                    Helper.showToast(requireContext(), result.message!!)
                    Log.d("ResultError", result.message)
                }
            }
        }
    }

}