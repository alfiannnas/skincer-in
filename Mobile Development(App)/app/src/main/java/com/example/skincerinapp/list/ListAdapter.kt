package com.example.skincerinapp.list

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skincerinapp.databinding.CardListBinding
import com.example.skincerinapp.model.Cancer

class ListAdapter (private val cancerList: List<Cancer>): RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    inner class ListViewHolder(private val binding: CardListBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(cancer: Cancer) = binding.apply {
            cancerName.text = cancer.name
            descriptionText.text =cancer.desc
            imgItemPhoto.setImageResource(cancer.photo)

            root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra("cancerId", cancer.id)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ListViewHolder {
        val binding = CardListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ListViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(cancerList[position])
    }

    override fun getItemCount() = cancerList.size

}