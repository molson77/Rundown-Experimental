package com.example.rundown_experimental.ui.schedule

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.*
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rundown_experimental.R
import com.example.rundown_experimental.databinding.FragmentScheduleBinding
import com.example.rundown_experimental.model.Event
import com.example.rundown_experimental.util.Constants

class ScheduleFragment : Fragment() {

    private lateinit var scheduleViewModel: ScheduleViewModel
    private var _binding: FragmentScheduleBinding? = null
    private val eventAdapter by lazy { EventListAdapter() }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        scheduleViewModel =
            ViewModelProvider(this)[ScheduleViewModel::class.java]

        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.apply {
            scheduleRecycler.adapter = eventAdapter
            scheduleRecycler.layoutManager = LinearLayoutManager(this@ScheduleFragment.activity)
        }

        setHasOptionsMenu(true)

        return root
    }

    fun retrieveData() {
        // Accessing SharedPreferences for user's team, defaults to Eagles

        val sharedPreferences = this.requireActivity().getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val selectedTeam = sharedPreferences.getInt(
            getString(R.string.saved_team_id_string), 134936)


        // GET request for team name

        var teamName: String
        scheduleViewModel.getTeam(selectedTeam)
        scheduleViewModel.teamResponse.observe(this.viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                teamName = response.body()!!.teams[0].strTeam
                this.requireActivity().title = teamName
            } else {
                Toast.makeText(this.activity, response.code(), Toast.LENGTH_SHORT).show()
            }
        })


        // GET request for seasons

        scheduleViewModel.getSeasons()
        scheduleViewModel.seasonsResponse.observe(this.viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                val last = response.body()!!.seasons.last()
                val editor = sharedPreferences.edit()
                editor.putString(getString(R.string.current_season_string), last.strSeason)
                editor.apply()
            } else {
                Toast.makeText(this.activity, response.code(), Toast.LENGTH_SHORT).show()
            }
        })
        val currSeason = sharedPreferences.getString(
            getString(R.string.current_season_string), "2021")


        // GET requests for events by round, then filtered by user team

        val relevantEvents: ArrayList<Event> = arrayListOf()
        for (round in 1..18) {
            if (currSeason != null) {

                scheduleViewModel.getEvents(4391, round, currSeason)
                scheduleViewModel.eventsResponse.observe(this.viewLifecycleOwner, { response ->
                    if (response.isSuccessful) {
                        // iterate over response and add relevant events
                        response.body()!!.events.forEach {
                            if (it.idAwayTeam == selectedTeam || it.idHomeTeam == selectedTeam) {
                                if (!relevantEvents.contains(it)){
                                    Log.d("TEST", "EVENT FOUND")
                                    relevantEvents.add(it)
                                    //Log.d("TEST", "ITEM ADDED")
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this.activity, response.code(), Toast.LENGTH_SHORT).show()
                    }
                })

            }
        }

        // sending to recyclerview
        relevantEvents.sortWith(SortByDate())

        Log.d("TEST2", relevantEvents.toString())

        eventAdapter.setData(relevantEvents)
    }

    override fun onResume() {
        if (hasConnection()) {
            retrieveData()
        } else {
            Toast.makeText(this.activity, "No Internet Connection", Toast.LENGTH_LONG)
        }
        super.onResume()
    }

    private fun hasConnection(): Boolean {
        val cm = this.activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val activeNetwork = cm.activeNetwork
        val networkCapabilities = cm.getNetworkCapabilities(activeNetwork)
        val connection = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        return connection == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        super.onCreateOptionsMenu(menu, inflater)

        val parent = this.activity

        inflater.inflate(R.menu.options_menu, menu)
        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem?.actionView as SearchView

        searchView.queryHint = getString(R.string.search)
        searchView.findViewById<AutoCompleteTextView>(R.id.search_src_text).threshold = 1

        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_label)
        val cursorAdapter = SimpleCursorAdapter(
            parent, R.layout.search_item, null, from,
            to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        val suggestions = Constants.teamMap.keys

        searchView.suggestionsAdapter = cursorAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query in Constants.teamMap.keys && query != null) {
                    // Adding new selected team to SharedPreferences
                    val sharedPref = parent!!.getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    val editor = sharedPref?.edit()
                    editor?.putInt(getString(R.string.saved_team_id_string), Constants.teamMap[query]!!)
                    editor?.apply()
                    searchView.clearFocus()
                    parent.title = query
                    if(hasConnection()) {
                        retrieveData()
                    }
                    return true
                }
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                query?.let {
                    suggestions.forEachIndexed { index, suggestion ->
                        if (suggestion.contains(query, true))
                            cursor.addRow(arrayOf(index, suggestion))
                    }
                }
                cursorAdapter.changeCursor(cursor)
                return true
            }

        })

        searchView.setOnSuggestionListener(object: SearchView.OnSuggestionListener {

            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
                val index = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)
                val selection = cursor.getString(index)
                searchView.setQuery(selection, true)
                return true
            }

        })

    }

    class SortByDate: Comparator<Event> {
        override fun compare(p0: Event, p1: Event): Int {
            val dateToInt0 = p0.dateEvent.replace("-", "").toInt()
            val dateToInt1 = p0.dateEvent.replace("-", "").toInt()
            return dateToInt0-dateToInt1
        }
    }

}