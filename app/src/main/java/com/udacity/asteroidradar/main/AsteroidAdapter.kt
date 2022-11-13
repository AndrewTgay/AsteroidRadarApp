package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.databinding.ListItemAsteroidBinding

class AsteroidAdapter(private val onCLickListner: AsteroidOnCLickListener )
    :ListAdapter<Asteroid, AsteroidAdapter.AsteroidViewHolder>(DiffCallback){

    class AsteroidViewHolder(private val binding : ListItemAsteroidBinding)
        :RecyclerView.ViewHolder(binding.root){
        fun bind(asteroid: Asteroid){
            binding.asteroidVariable = asteroid
            //to update the property immediatly without it will
            // perform extra calculations to know how to display the list
            binding.executePendingBindings()
        }
    }
    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            /// === this return true when the refrences is the same
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder(ListItemAsteroidBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.itemView.setOnClickListener{
            onCLickListner.onClick(asteroid)
        }
        holder.bind(asteroid)
    }
}
class AsteroidOnCLickListener(val cLickListener : (asteroid : Asteroid) -> Unit){
    fun onClick(asteroid: Asteroid) = cLickListener(asteroid)
}