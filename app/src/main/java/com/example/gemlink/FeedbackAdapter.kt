package com.example.gemlink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FeedbackAdapter(
    private val feedbacks: List<FeedbackItem>
) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    inner class FeedbackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvComment : TextView  = view.findViewById(R.id.tv_feedback_comment)
        val rbRating  : RatingBar = view.findViewById(R.id.rb_feedback_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feedback, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbacks[position]
        holder.tvComment.text  = feedback.comment
        holder.rbRating.rating = feedback.rating
    }

    override fun getItemCount() = feedbacks.size
}