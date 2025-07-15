package com.itsthe1.webexoplayer.api

data class MuslimSalatResponse(
    val title: String,
    val query: String,
    val items: List<PrayerTimesItem>
)

data class PrayerTimesItem(
    val date_for: String,
    val fajr: String,
    val shurooq: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
) 