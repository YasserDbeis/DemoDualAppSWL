package com.example.demodualappswl.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.demodualappswl.MainActivity
import com.example.demodualappswl.R
import com.example.demodualappswl.models.Issue

class IssueAdapter (
    private val issues: MutableList<Issue> = mutableListOf<Issue>(),
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<IssueAdapter.IssueViewHolder>() {

    inner class IssueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition

            if(position != RecyclerView.NO_POSITION)
                listener.onItemClick(position = adapterPosition)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        return IssueViewHolder(
            LayoutInflater.from(parent.context).inflate (
                R.layout.issue_item,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {

        if(issues != null && issues.isNotEmpty()) {

            val curIssue = issues[position]

            holder.itemView.apply {
                findViewById<TextView>(R.id.tvIssueTitle).text = curIssue.issueTitle
            }
        }

    }

    override fun getItemCount(): Int {
        return issues.size
    }


}