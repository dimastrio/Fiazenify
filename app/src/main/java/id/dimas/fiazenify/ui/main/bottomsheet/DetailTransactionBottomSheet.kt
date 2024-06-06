package id.dimas.fiazenify.ui.main.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.dimas.fiazenify.R
import id.dimas.fiazenify.databinding.BottomSheetDetailTransactionBinding
import id.dimas.fiazenify.ui.main.transaction.EditTransactionFragment
import id.dimas.fiazenify.ui.main.transaction.TransactionViewModel
import id.dimas.fiazenify.util.Helper
import id.dimas.fiazenify.util.Result
import id.dimas.fiazenify.util.Status
import id.dimas.fiazenify.util.amountFormat

@AndroidEntryPoint
class DetailTransactionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDetailTransactionBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel: TransactionViewModel by viewModels()

    var bottomSheetCallback: BottomSheetCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDetailTransactionBinding.inflate(layoutInflater, container, false)
        return requireNotNull(_binding).root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBottomSheet() {
        val data = arguments?.getString("transactionId")
        observeTransactionDetail(data!!)

        val editTransactionFragment = EditTransactionFragment()
        val bundle = Bundle()
        bundle.putString("id", data)
        editTransactionFragment.arguments = bundle

        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnDelete.setOnClickListener { bottomSheetCallback?.onDelete() }
        binding.btnEdit.setOnClickListener {
            requireParentFragment().parentFragmentManager.beginTransaction().apply {
                add(R.id.main_nav_host, editTransactionFragment)
                addToBackStack(null)
                commit()
            }
            dismiss()
        }

    }

    private fun observeTransactionDetail(id: String) {
        transactionViewModel.getTransactionById(id).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }

                is Result.Success -> {
                    if (result.data != null) {
                        binding.tvTitle.text = result.data.title
                        binding.tvType.text = result.data.type
                        binding.tvCategory.text = result.data.category
                        binding.tvAmount.text = "Rp. ${amountFormat(result.data.amount)}"
                        binding.tvNote.text = result.data.note
                    }
                }

                is Result.Error -> {
                    Helper.showSnackbar(
                        requireContext(),
                        binding.root,
                        "Terjadi Kesalahan",
                        Status.FAILED
                    )
                }
            }
        }
    }


    interface BottomSheetCallback {
        fun onDelete()
        fun onUpdate()
    }

}