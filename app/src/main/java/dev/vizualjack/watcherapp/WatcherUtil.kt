package dev.vizualjack.watcherapp

import org.json.JSONArray
import org.json.JSONObject

class WatcherUtil {
    public var data:JSONObject
    private val watchInfos:JSONArray
    private val seriesLib:JSONArray

    constructor(data: String) {
        this.data = JSONObject(data)
        watchInfos = this.data.getJSONArray("userWatchInfos")
        seriesLib = this.data.getJSONArray("librarySeries")
    }

    public fun GetOverviewEntries(): List<OverviewEntry> {
        var overviewEntries = ArrayList<OverviewEntry>();
        for(i in 0 until watchInfos.length()) {
            val watchInfo = watchInfos.getJSONObject(i)
            overviewEntries.add(OverviewEntry(watchInfo.getInt("seriesId"), ""))
        }
        for(entry in overviewEntries) {
            for (i in 0 until seriesLib.length()) {
                val series = seriesLib.getJSONObject(i)
                if(series.getInt("id") == entry.id) {
                    entry.name = series.getString("name");
                    break
                }
            }
        }
        return overviewEntries
    }

    public fun GetSeriesForId(id:Int):Series? {
        for (i in 0 until seriesLib.length()) {
            val series = seriesLib.getJSONObject(i)
            if(series.getInt("id") == id) {
                return Series(id, series.getString("name"),
                    series.getString("link"),
                    series.getString("image"),
                    series.getString("desc"))
            }
        }
        return null
    }

    public fun GetWatchInfo(id:Int):WatchInfo? {
        var seriesObj:JSONObject? = null
        for (i in 0 until seriesLib.length()) {
            seriesObj = seriesLib.getJSONObject(i)
            if(seriesObj.getInt("id") == id) {
                break
            }
        }
        for(i in 0 until watchInfos.length()) {
            val watchInfo = watchInfos.getJSONObject(i)
            if (watchInfo.getInt("seriesId") == id) {
                val seriesSeasons = seriesObj!!.getJSONArray("seasons")
                val curSeason = watchInfo.getInt("season")
                return WatchInfo(id, watchInfo.getInt("episode"), seriesSeasons.getJSONObject(curSeason-1).getInt("episodes"), curSeason, seriesSeasons.length())
            }
        }
        return null
    }

    public fun NextEpisode(id: Int) {
        var seriesObj:JSONObject? = null
        for (i in 0 until seriesLib.length()) {
            seriesObj = seriesLib.getJSONObject(i)
            if(seriesObj.getInt("id") == id) {
                break
            }
        }
        for(i in 0 until watchInfos.length()) {
            val watchInfoObj = watchInfos.getJSONObject(i)
            if (watchInfoObj.getInt("seriesId") == id) {
                val curSeason = watchInfoObj.getInt("season")
                val seriesSeasons = seriesObj!!.getJSONArray("seasons")
                val seasonEpisodes = seriesSeasons.getJSONObject(curSeason-1).getInt("episodes")
                val curEpisode = watchInfoObj.getInt("episode")
                if (curSeason >= seriesSeasons.length() && curEpisode > seasonEpisodes) break
                var nextEpisode = curEpisode + 1
                var nextSeason = curSeason
                if(nextEpisode > seasonEpisodes && curSeason < seriesSeasons.length()) {
                    nextEpisode = 1
                    nextSeason += 1
                }
                watchInfoObj.put("season", nextSeason)
                watchInfoObj.put("episode", nextEpisode)
            }
        }
    }

    public fun PrevEpisode(id: Int) {
        var seriesObj:JSONObject? = null
        for (i in 0 until seriesLib.length()) {
            seriesObj = seriesLib.getJSONObject(i)
            if(seriesObj.getInt("id") == id) {
                break
            }
        }
        for(i in 0 until watchInfos.length()) {
            val watchInfoObj = watchInfos.getJSONObject(i)
            if (watchInfoObj.getInt("seriesId") == id) {
                val curSeason = watchInfoObj.getInt("season")
                val seriesSeasons = seriesObj!!.getJSONArray("seasons")
                val seasonEpisodes = seriesSeasons.getJSONObject(curSeason-1).getInt("episodes")
                val curEpisode = watchInfoObj.getInt("episode")
                // Here logic for prev episode
                var prevEpisode = curEpisode - 1
                var prevSeason = curSeason
                if(prevEpisode <= 0) {
                    prevEpisode = 1
                    prevSeason -= 1
                }
                if (prevSeason <= 0) prevSeason = 1
                watchInfoObj.put("season", prevSeason)
                watchInfoObj.put("episode", prevEpisode)
            }
        }
    }
}
