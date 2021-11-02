package com.example.rundown_experimental.util

class Constants {

    companion object {
        const val BASE_URL_SPORTS = "https://www.thesportsdb.com/api/v1/json/1/"
        const val BASE_URL_NEWS = "https://newsapi.org/v2/"
        const val NEWS_API_KEY = "979e3d1aba9848deb006299cba06d1dd"

        val teamMap: HashMap<String, Int> = hashMapOf(

            "Atlanta Falcons" to 134942,
            "Baltimore Ravens" to 134922,
            "Buffalo Bills" to 134918,
            "Carolina Panthers" to 134943,
            "Chicago Bears" to 134938,
            "Cincinnati Bengals" to 134923,
            "Cleveland Browns" to 134924,
            "Dallas Cowboys" to 134934,
            "Denver Broncos" to 134930,
            "Detroit Lions" to 134939,
            "Green Bay Packers" to 134940,
            "Houston Texans" to 134926,
            "Indianapolis Colts" to 134927,
            "Jacksonville Jaguars" to 134928,
            "Kansas City Chiefs" to 134931,
            "Las Vegas Raiders" to 134932,
            "Los Angeles Chargers" to 135908,
            "Los Angeles Rams" to 135907,
            "Miami Dolphins" to 134919,
            "Minnesota Vikings" to 134941,
            "New England Patriots" to 134920,
            "New Orleans Saints" to 134944,
            "New York Giants" to 134935,
            "New York Jets" to 134921,
            "Philadelphia Eagles" to 134936,
            "Pittsburgh Steelers" to 134925,
            "San Francisco 49ers" to 134948,
            "Seattle Seahawks" to 134949,
            "Tampa Bay Buccaneers" to 134945,
            "Tennessee Titans" to 134929,
            "Washington" to 134937

        )
    }
}