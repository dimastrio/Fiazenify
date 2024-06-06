package id.dimas.fiazenify.ui.main.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.databinding.BottomSheetDateBinding
import java.util.Date

@AndroidEntryPoint
class DateBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDateBinding? = null
    private val binding get() = _binding!!

    private var clickDateStart: Boolean = false
    private var dateTemp: String = ""
    private var dateStart: String = ""
    private var dateEnd: String = ""

    var dateBottomSheetCallback: DateBottomSheetCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDateBinding.inflate(layoutInflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDateView("Tanggal Awal", "Pilih")
        setupDate()
    }

    private fun setupDate() {
        binding.calendarView.setOnDateChangeListener { _, year, month, day ->
            dateTemp = "$day/${month + 1}/$year"
        }

        binding.tvApply.setOnClickListener {
            when (clickDateStart) {
                false -> {
                    clickDateStart = true
                    dateStart = dateTemp
                    binding.calendarView.date = Date().time
                    setDateView("Tanggal Akhir", "Pilih")
                }

                true -> {
                    dateEnd = dateTemp
                    dateBottomSheetCallback?.onSucces(dateStart, dateEnd)
                    this.dismiss()
                }
            }
        }
    }

    private fun setDateView(title: String, apply: String) {
        binding.tvTitle.text = title
        binding.tvApply.text = apply
    }

    interface DateBottomSheetCallback {

        fun onSucces(dateStart: String, dateEnd: String)
    }
}