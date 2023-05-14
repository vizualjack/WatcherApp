package dev.vizualjack.watcherapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.navigation.fragment.findNavController
import dev.vizualjack.watcherapp.databinding.FragmentOverviewBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var mainActivity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        loadSeries()
    }

    private fun loadSeries() {
        if (mainActivity!!.watcherUtil == null) return;
        val seriesList = requireView().findViewById<LinearLayout>(R.id.entries)
        seriesList.removeAllViews()
        for (overviewEntry in mainActivity!!.watcherUtil!!.GetOverviewEntries()) {
            val newEntry = LayoutInflater.from(context).inflate(R.layout.series_entry, null)
            val nameView = newEntry.findViewById<TextView>(R.id.series_entry_name)
            nameView.text = overviewEntry.name
            newEntry.setOnClickListener {
                mainActivity!!.selectedSeriesId = overviewEntry.id
                findNavController().navigate(R.id.action_OverviewFragment_to_SeriesFragment)
            }
            seriesList.addView(newEntry)
            newEntry.layoutParams.height = 100 * requireContext().resources.displayMetrics.density.toInt()
            val marginLayout = newEntry.layoutParams as ViewGroup.MarginLayoutParams
            marginLayout.setMargins(30)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}