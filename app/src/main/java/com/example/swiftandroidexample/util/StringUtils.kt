package com.example.swiftandroidexample.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class StringUtils {
    companion object {
        fun getStringArray(tags: String?): List<String> {
            return tags?.split(",")?.map { it.trim() }?.toList() ?: mutableListOf()
        }

        fun getTagsStringEquivalent(tags: List<String>?): String {
            return tags?.joinToString(separator = ",") ?: ""
        }

        fun formatDate(epoch: Double): String {
            val date = Date((epoch * 1000).toLong())

            val calendar = Calendar.getInstance()
            val today = calendar.time

            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrow = calendar.time

            val dateFormat: SimpleDateFormat
            val isToday: Boolean
            val isTomorrow: Boolean

            calendar.time = date
            isToday = isSameDay(date, today)
            isTomorrow = isSameDay(date, tomorrow)

            dateFormat = if (isToday) {
                SimpleDateFormat("'Today, 'h:mm a", Locale.getDefault())
            } else if (isTomorrow) {
                SimpleDateFormat("'Tomorrow, 'h:mm a", Locale.getDefault())
            } else {
                SimpleDateFormat("d MMMM, yyyy", Locale.getDefault())
            }

            return dateFormat.format(date)
        }

        fun isSameDay(date1: Date, date2: Date): Boolean {
            val calendar1 = Calendar.getInstance()
            val calendar2 = Calendar.getInstance()
            calendar1.time = date1
            calendar2.time = date2
            return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                    calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
        }

         fun generateUniqueID(): Int {
            val timestamp = System.currentTimeMillis().toInt()
            val randomValue = Random.nextInt(0, 1000)
            return timestamp + randomValue
        }
    }
}