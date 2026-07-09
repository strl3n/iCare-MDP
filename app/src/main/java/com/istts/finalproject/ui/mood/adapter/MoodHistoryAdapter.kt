package com.istts.finalproject.ui.mood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.istts.finalproject.data.local.entity.MoodEntity
import com.istts.finalproject.databinding.ItemMoodHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class MoodHistoryAdapter(
    private val onDeleteClick: (MoodEntity) -> Unit
) : RecyclerView.Adapter<MoodHistoryAdapter.MoodViewHolder>() {

    private var moods: List<MoodEntity> = emptyList()

    fun submitList(newList: List<MoodEntity>) {
        moods = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(moods[position])
    }

    override fun getItemCount(): Int = moods.size

    class MoodViewHolder(
        private val binding: ItemMoodHistoryBinding,
        private val onDeleteClick: (MoodEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mood: MoodEntity) {
            val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))

            binding.tvMoodEmoji.text = getMoodEmoji(mood.moodLevel)
            binding.tvMoodLevel.text = "${mood.moodLevel}/10"
            binding.tvMoodNote.text = mood.note ?: "Tidak ada catatan"
            binding.tvMoodDate.text = dateFormat.format(mood.date)

            binding.btnDelete.setOnClickListener {
                onDeleteClick(mood)
            }
        }

        private fun getMoodEmoji(level: Int): String {
            return when (level) {
                1 -> "😢"
                2 -> "😞"
                3 -> "😟"
                4 -> "😕"
                5 -> "😐"
                6 -> "🙂"
                7 -> "😊"
                8 -> "😄"
                9 -> "😁"
                10 -> "🥰"
                else -> "😐"
            }
        }
    }
}