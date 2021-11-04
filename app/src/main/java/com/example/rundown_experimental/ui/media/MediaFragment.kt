package com.example.rundown_experimental.ui.media

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
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
import com.example.rundown_experimental.databinding.FragmentMediaBinding
import com.example.rundown_experimental.model.Article
import com.example.rundown_experimental.util.Constants

class MediaFragment : Fragment(), MediaListAdapter.OnArticleListener {

    private lateinit var mediaViewModel: MediaViewModel
    private var _binding: FragmentMediaBinding? = null
    private val mediaAdapter by lazy { MediaListAdapter(this) }
    private lateinit var articleList: List<Article>
    private val parent = this.activity

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mediaViewModel =
            ViewModelProvider(this).get(MediaViewModel::class.java)

        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.apply {
            mediaRecycler.adapter = mediaAdapter
            mediaRecycler.layoutManager = LinearLayoutManager(this@MediaFragment.activity)
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
        val filter = Constants.teamMap.filterValues { it == selectedTeam }
        var teamName = filter.entries.iterator().next().key
        if (teamName == "Washington") {
            teamName = "Washington Football Team"
        }

        // GET request for headlines

        mediaViewModel.getHeadlines(teamName)
        mediaViewModel.headlinesResponse.observe(this.viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                val articles = response.body()!!.articles
                // sending to adapter
                mediaAdapter.setData(articles)
                articleList = articles
            } else {
                Toast.makeText(this.activity, response.code(), Toast.LENGTH_SHORT).show()
            }
        })


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

    override fun onArticleClick(position: Int) {
        val article = articleList[position]
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(article.url)
        this.activity?.startActivity(intent)
    }

}