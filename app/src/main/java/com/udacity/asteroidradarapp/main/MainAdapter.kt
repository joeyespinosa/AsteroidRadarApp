package com.udacity.asteroidradarapp.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradarapp.model.Asteroid
import com.udacity.asteroidradarapp.databinding.ItemAsteroidBinding

class MainAdapter(private val clickListener: AsteroidListener) :
    ListAdapter<Asteroid, MainAdapter.AsteroidViewHolder>(DiffCallback) {

    class AsteroidViewHolder(var binding: ItemAsteroidBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(asteroid: Asteroid) {
            binding.asteroid = asteroid
            binding.executePendingBindings()
        }
    }

    class AsteroidListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val binding = ItemAsteroidBinding.inflate(LayoutInflater.from(parent.context))
        return AsteroidViewHolder(binding)
    }


    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val asteroid = getItem(position)

        holder.also { item ->
            item.itemView.setOnClickListener{
                clickListener.onClick(asteroid)
            }
            item.bind(asteroid)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

    }
}