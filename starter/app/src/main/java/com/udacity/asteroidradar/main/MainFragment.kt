package com.udacity.asteroidradar.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

private const val TAG = "MainFragment"
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
//            "You can only access the viewModel after onViewCreated()"
        }

        ViewModelProvider(
            this,
            MainViewModel.Factory(activity.application)
        ).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter =
            AsteroidsAdapter(AsteroidsAdapter.OnItemClickListener {
                viewModel.displayDetailAsteroid(it)
            })

        viewModel.navigateToDetailAsteroid.observe(this, Observer {
            if (it != null) {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayDetailAsteroidComplete()
            }
        })

//        viewModel.asteroidList.observe(this, Observer {
//            Log.i(TAG, "asteroidList changed: ")
//            (binding.asteroidRecycler.adapter as AsteroidsAdapter).submitList(it)
//        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_week_asteroids -> viewModel.onFilterChanged(AsteroidListFilter.WEEK)
            R.id.show_today_asteroids -> viewModel.onFilterChanged(AsteroidListFilter.TODAY)
            R.id.show_saved_asteroids -> viewModel.onFilterChanged(AsteroidListFilter.SAVED)
        }
        return true
    }
}
