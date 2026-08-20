package com.example.data

import com.example.data.model.WorldCity

object WorldCitiesProvider {
    val defaultInitialCities = listOf(
        WorldCity("America/New_York", "New York", "United States", "America/New_York", 0),
        WorldCity("Europe/London", "London", "United Kingdom", "Europe/London", 1),
        WorldCity("Asia/Tokyo", "Tokyo", "Japan", "Asia/Tokyo", 2),
        WorldCity("Australia/Sydney", "Sydney", "Australia", "Australia/Sydney", 3)
    )

    val allSupportedCities = listOf(
        WorldCity("America/New_York", "New York", "United States", "America/New_York"),
        WorldCity("America/Los_Angeles", "Los Angeles", "United States", "America/Los_Angeles"),
        WorldCity("America/Chicago", "Chicago", "United States", "America/Chicago"),
        WorldCity("America/Toronto", "Toronto", "Canada", "America/Toronto"),
        WorldCity("America/Vancouver", "Vancouver", "Canada", "America/Vancouver"),
        WorldCity("America/Mexico_City", "Mexico City", "Mexico", "America/Mexico_City"),
        WorldCity("America/Sao_Paulo", "São Paulo", "Brazil", "America/Sao_Paulo"),
        WorldCity("America/Buenos_Aires", "Buenos Aires", "Argentina", "America/Buenos_Aires"),
        WorldCity("America/Santiago", "Santiago", "Chile", "America/Santiago"),
        WorldCity("America/Bogota", "Bogotá", "Colombia", "America/Bogota"),
        WorldCity("America/Lima", "Lima", "Peru", "America/Lima"),
        WorldCity("America/Honolulu", "Honolulu", "United States", "Pacific/Honolulu"),
        WorldCity("America/Anchorage", "Anchorage", "United States", "America/Anchorage"),
        WorldCity("Europe/London", "London", "United Kingdom", "Europe/London"),
        WorldCity("Europe/Paris", "Paris", "France", "Europe/Paris"),
        WorldCity("Europe/Berlin", "Berlin", "Germany", "Europe/Berlin"),
        WorldCity("Europe/Rome", "Rome", "Italy", "Europe/Rome"),
        WorldCity("Europe/Madrid", "Madrid", "Spain", "Europe/Madrid"),
        WorldCity("Europe/Amsterdam", "Amsterdam", "Netherlands", "Europe/Amsterdam"),
        WorldCity("Europe/Zurich", "Zurich", "Switzerland", "Europe/Zurich"),
        WorldCity("Europe/Stockholm", "Stockholm", "Sweden", "Europe/Stockholm"),
        WorldCity("Europe/Athens", "Athens", "Greece", "Europe/Athens"),
        WorldCity("Europe/Istanbul", "Istanbul", "Turkey", "Europe/Istanbul"),
        WorldCity("Europe/Moscow", "Moscow", "Russia", "Europe/Moscow"),
        WorldCity("Asia/Tokyo", "Tokyo", "Japan", "Asia/Tokyo"),
        WorldCity("Asia/Seoul", "Seoul", "South Korea", "Asia/Seoul"),
        WorldCity("Asia/Shanghai", "Shanghai", "China", "Asia/Shanghai"),
        WorldCity("Asia/Hong_Kong", "Hong Kong", "China", "Asia/Hong_Kong"),
        WorldCity("Asia/Taipei", "Taipei", "Taiwan", "Asia/Taipei"),
        WorldCity("Asia/Singapore", "Singapore", "Singapore", "Asia/Singapore"),
        WorldCity("Asia/Bangkok", "Bangkok", "Thailand", "Asia/Bangkok"),
        WorldCity("Asia/Jakarta", "Jakarta", "Indonesia", "Asia/Jakarta"),
        WorldCity("Asia/Manila", "Manila", "Philippines", "Asia/Manila"),
        WorldCity("Asia/Kolkata", "New Delhi", "India", "Asia/Kolkata"),
        WorldCity("Asia/Mumbai", "Mumbai", "India", "Asia/Kolkata"),
        WorldCity("Asia/Dubai", "Dubai", "United Arab Emirates", "Asia/Dubai"),
        WorldCity("Asia/Riyadh", "Riyadh", "Saudi Arabia", "Asia/Riyadh"),
        WorldCity("Asia/Jerusalem", "Jerusalem", "Israel", "Asia/Jerusalem"),
        WorldCity("Australia/Sydney", "Sydney", "Australia", "Australia/Sydney"),
        WorldCity("Australia/Melbourne", "Melbourne", "Australia", "Australia/Melbourne"),
        WorldCity("Australia/Brisbane", "Brisbane", "Australia", "Australia/Brisbane"),
        WorldCity("Australia/Perth", "Perth", "Australia", "Australia/Perth"),
        WorldCity("Pacific/Auckland", "Auckland", "New Zealand", "Pacific/Auckland"),
        WorldCity("Pacific/Fiji", "Suva", "Fiji", "Pacific/Fiji"),
        WorldCity("Africa/Cairo", "Cairo", "Egypt", "Africa/Cairo"),
        WorldCity("Africa/Johannesburg", "Johannesburg", "South Africa", "Africa/Johannesburg"),
        WorldCity("Africa/Nairobi", "Nairobi", "Kenya", "Africa/Nairobi"),
        WorldCity("Africa/Lagos", "Lagos", "Nigeria", "Africa/Lagos"),
        WorldCity("Africa/Casablanca", "Casablanca", "Morocco", "Africa/Casablanca"),
        WorldCity("Atlantic/Reykjavik", "Reykjavik", "Iceland", "Atlantic/Reykjavik")
    )
}
