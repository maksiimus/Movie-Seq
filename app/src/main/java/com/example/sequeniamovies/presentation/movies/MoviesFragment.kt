package com.example.sequeniamovies.presentation.movies

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sequeniamovies.MoviesApp
import com.example.sequeniamovies.R
import com.example.sequeniamovies.databinding.FragmentMoviesBinding
import com.example.sequeniamovies.presentation.adapter.MoviesScreenAdapter
import com.example.sequeniamovies.presentation.movies.decoration.MoviesSpacingDecoration
import com.example.sequeniamovies.presentation.movies.decoration.SectionSpacingDecoration
import javax.inject.Inject

class MoviesFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MoviesViewModel> { viewModelFactory }

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MoviesScreenAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as MoviesApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setAdapter()

        binding.rvList.layoutManager = makeGridLayoutManager()
        binding.rvList.setHasFixedSize(true)
        binding.rvList.adapter = adapter
        binding.rvList.addItemDecoration(SectionSpacingDecoration(adapter, requireContext()))
        binding.rvList.addItemDecoration(MoviesSpacingDecoration())


        // retry action
        binding.tvRetry.setOnClickListener { viewModel.loadIfNeeded() }

        observeViewModel()
        viewModel.loadIfNeeded()
    }

    private fun setAdapter() {
        adapter = MoviesScreenAdapter(
            onGenreClick = { viewModel.onGenreTapped(it) },
            onMovieClick = {
                val action = MoviesFragmentDirections.actionMoviesToDetails(movieId = it.id)
                findNavController().navigate(action)
            }
        )
    }

    private fun makeGridLayoutManager() = GridLayoutManager(requireContext(), 2).apply {
        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                when (adapter.getItemViewType(position)) {
                    MoviesScreenAdapter.TYPE_MOVIE -> 1
                    else -> 2
                }
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MoviesScreenState.Loading -> {
                    binding.progress.isVisible = true
                    binding.rvList.isVisible = false
                    binding.errorBar.isVisible = false
                }
                is MoviesScreenState.Content -> {
                    binding.progress.isVisible = false
                    binding.rvList.isVisible = true
                    binding.errorBar.isVisible = false
                    adapter.submitList(state.items)
                }
                is MoviesScreenState.Error -> {
                    binding.progress.isVisible = false
                    binding.rvList.isVisible = false
                    binding.errorBar.isVisible = true
                    binding.tvErrorMessage.text = state.message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
