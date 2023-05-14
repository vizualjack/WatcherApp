package dev.vizualjack.watcherapp

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.vizualjack.watcherapp.databinding.FragmentSeriesBinding
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SeriesFragment : Fragment() {

    private var _binding: FragmentSeriesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var mainActivity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSeriesBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSeriesInfo()
        loadWatchInfo()
        binding.fragmentSeriesPrev.setOnClickListener {
            mainActivity!!.watcherUtil.PrevEpisode(mainActivity!!.selectedSeriesId)
            loadWatchInfo()
        }
        binding.fragmentSeriesNext.setOnClickListener {
            mainActivity!!.watcherUtil.NextEpisode(mainActivity!!.selectedSeriesId)
            loadWatchInfo()
        }
    }

    private fun loadSeriesInfo() {
        val series = mainActivity!!.watcherUtil.GetSeriesForId(mainActivity!!.selectedSeriesId)
        binding.fragmentSeriesName.text = series!!.name
        val imageAsBase64 = series!!.imgAsDataUri.split("base64,")[1]
        val decodedImage = Base64.getDecoder().decode(imageAsBase64)
        val bitMap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
        binding.fragmentSeriesImage.setImageBitmap(bitMap)
    }

    private fun loadWatchInfo() {
        val watchInfo = mainActivity!!.watcherUtil.GetWatchInfo(mainActivity!!.selectedSeriesId)
        binding.fragmentSeriesWatchInfo.text = "Season ${watchInfo!!.curSeason}/${watchInfo!!.seasons}\n" +
                "Episode ${watchInfo!!.curEpisode}/${watchInfo!!.seasonEpisodes}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}