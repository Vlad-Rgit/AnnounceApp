package com.announce.presenter.user_announces.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.announce.common.domain.Announcment
import com.announce.databinding.UserAnnounceItemBinding
import com.announce.framework.utils.AnnounceDiffUtilCallback
import java.text.SimpleDateFormat

class AnnounceAdapter: RecyclerView.Adapter<AnnounceAdapter.ViewHolder>() {

    class ViewHolder(private val binding: UserAnnounceItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

            companion object {
                private val dateFormat = SimpleDateFormat.getDateInstance()
            }

            fun bind(announcment: Announcment) {
                binding.run {
                    tvAnnounce.text = announcment.text
                    tvDateCreated.text =
                            dateFormat.format(announcment.dateCreated!!.toDate())
                    tvPhoneNumber.text = announcment.phoneNumber
                }
            }
        }


    private var items: List<Announcment> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserAnnounceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setItems(newItems: List<Announcment>) {
        val diffUtilCallback = AnnounceDiffUtilCallback(newItems, items)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

}