package com.dicoding.storyapp.ui.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.databinding.ItemStoryBinding

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.MyViewHolder>() {

    private val listStory = ArrayList<ListStoryItem>()
    private var onItemClickCallback: OnItemClickCallBack? = null

    interface OnItemClickCallBack{
        fun onItemClicked(data: ListStoryItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallBack) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem){
            binding.apply {
                Glide.with(ivItemPhoto.context)
                    .load(story.photoUrl)
                    .fitCenter()
                    .into(ivItemPhoto)
                tvItemName.text = story.name
                tvItemDescription.text = story.description

                itemView.setOnClickListener{
                    onItemClickCallback?.onItemClicked(story)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = listStory[position]
        holder.bind(story)
    }

    fun setList(newList: List<ListStoryItem>) {
        val diffResult = DiffUtil.calculateDiff(MainDiffCallback(listStory, newList))
        listStory.clear()
        listStory.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    class MainDiffCallback(
        private val oldList: List<ListStoryItem>,
        private val newList: List<ListStoryItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}