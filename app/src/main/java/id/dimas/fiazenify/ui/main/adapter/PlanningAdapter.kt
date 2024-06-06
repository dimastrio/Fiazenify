package id.dimas.fiazenify.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.dimas.fiazenify.data.model.Planning
import id.dimas.fiazenify.databinding.ItemPlannerBinding
import id.dimas.fiazenify.util.amountFormat
import id.dimas.fiazenify.util.percentFormat

class PlanningAdapter(
    private val onClick: (Planning) -> Unit,
    private val onUpdateAmount: (Planning) -> Unit
) : RecyclerView.Adapter<PlanningAdapter.PlanningViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Planning>() {
        override fun areItemsTheSame(oldItem: Planning, newItem: Planning): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Planning, newItem: Planning): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(planning: List<Planning>) = differ.submitList(planning)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanningViewHolder {
        val binding = ItemPlannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanningViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PlanningViewHolder, position: Int) {
        holder.onBind(differ.currentList[position])
    }

    inner class PlanningViewHolder(
        private val binding: ItemPlannerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(data: Planning) {
            with(binding) {
//                val formatPercent: Double = decimalFormat.format(data.percent)
                val percent = data.percent
                tvTitlePlanner.text = data.title
                tvTargetAmount.text = "Rp. ${amountFormat(data.targetAmount)}"
                tvCurrentAmount.text = "Rp. ${data.currentAmount}"
                tvPercent.text = percentFormat(data.percent)
                progressBar.progress = percent.toInt()
                itemView.setOnClickListener {
                    onClick(data)
                }
                btnUpdateAmount.setOnClickListener {
                    onUpdateAmount(data)
                }
            }
        }
    }
}