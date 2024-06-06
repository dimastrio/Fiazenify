package id.dimas.fiazenify.ui.main.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.dimas.fiazenify.R
import id.dimas.fiazenify.data.model.Transaction
import id.dimas.fiazenify.databinding.ItemTransactionBinding
import id.dimas.fiazenify.util.amountFormat
import id.dimas.fiazenify.util.timestampToString

class TransactionAdapter(
    private val onClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(transaction: List<Transaction>) = differ.submitList(transaction)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding =
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun getItemCount() = differ.currentList.size


    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }


    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Transaction) {
            with(binding) {
                tvTitle.text = data.title
                if (data.type == "Pemasukan") {
                    tvAmount.setTextColor(Color.parseColor("#00BC4B"))
                    ivType.setImageResource(R.drawable.ic_income)
                } else if (data.type == "Pengeluaran") {
                    tvAmount.setTextColor(Color.parseColor("#e75757"))
                    ivType.setImageResource(R.drawable.ic_outcome)
                }
                tvCategory.text = data.category
                tvAmount.text = "Rp. ${amountFormat(data.amount)}"
                tvDate.text = timestampToString(data.created)
                itemView.setOnClickListener {
                    onClick(data)
                }
            }
        }


    }
}

