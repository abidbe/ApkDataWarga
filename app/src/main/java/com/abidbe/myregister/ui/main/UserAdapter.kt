package com.abidbe.myregister.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.abidbe.myregister.database.User
import com.abidbe.myregister.databinding.ItemUserBinding
import com.abidbe.myregister.formatDateString
import com.abidbe.myregister.getAddressFromLatLng
import com.abidbe.myregister.helper.UserDiffCallback
import com.bumptech.glide.Glide

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private val listUsers = ArrayList<User>()
    fun setListUsers(listUsers: List<User>) {
        val diffCallback = UserDiffCallback(this.listUsers, listUsers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listUsers.clear()
        this.listUsers.addAll(listUsers)
        diffResult.dispatchUpdatesTo(this)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(listUsers[position])
    }
    override fun getItemCount(): Int {
        return listUsers.size
    }
    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            with(binding) {
                tvItemTitle.text = user.name
                tvItemDate.text = formatDateString(user.date!!)
                val address = getAddressFromLatLng(itemView.context, user.latitude!!, user.longitude!!)
                binding.tvItemAddress.setText(address)
                Glide.with(itemView.context)
                    .load(user.photoUri)
                    .into(ivItemPhoto)
//                rv.setOnClickListener {
//                    val intent = Intent(itemView.context, RegisterActivity::class.java)
//                    intent.putExtra(RegisterActivity.EXTRA_USER, user)
//                    itemView.context.startActivity(intent)
//                }
            }

        }
    }


}