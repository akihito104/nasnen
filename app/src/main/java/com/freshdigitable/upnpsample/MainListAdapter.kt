package com.freshdigitable.upnpsample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.freshdigitable.upnpsample.model.RecordScheduleItem
import kotlinx.android.synthetic.main.view_record_schedule_item.view.record_schedule_date
import kotlinx.android.synthetic.main.view_record_schedule_item.view.record_schedule_icon
import kotlinx.android.synthetic.main.view_record_schedule_item.view.record_schedule_title
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.TextStyle
import java.util.Locale

class MainListAdapter : RecyclerView.Adapter<ViewHolder>() {
    private val items = mutableListOf<RecordScheduleItem>()

    fun setItems(items: List<RecordScheduleItem>) {
        this.items.clear()
        this.items.addAll(items)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_record_schedule_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(
            when (item.hasWarnings) {
                true -> R.drawable.ic_warning_black_24dp
                false -> R.drawable.ic_archive_black_24dp
            }
        )
        holder.title.text = item.title
        holder.date.setListItemDatetime(RecordScheduleItem.NASNE_DATE_TIME_FORMAT.parse(
            item.scheduledStartDateTime, Instant.FROM
        ))
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val icon: ImageView = itemView.record_schedule_icon
    val title: TextView = itemView.record_schedule_title
    val date: TextView = itemView.record_schedule_date
}

private fun TextView.setListItemDatetime(
    instant: Instant,
    zoneOffset: ZoneOffset = OffsetDateTime.now().offset
) {
    val dateTime = instant.atOffset(zoneOffset)
    text = context.getString(
        R.string.listitem_datetime_format,
        dateTime.monthValue,
        dateTime.dayOfMonth,
        dateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
        dateTime.hour,
        dateTime.minute
    )
}
