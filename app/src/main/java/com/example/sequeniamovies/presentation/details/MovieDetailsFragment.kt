package com.example.sequeniamovies.presentation.details

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
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.sequeniamovies.MoviesApp
import com.example.sequeniamovies.R
import com.example.sequeniamovies.databinding.FragmentMovieDetailsBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class MovieDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MovieDetailsViewModel> { viewModelFactory }
    private val args by navArgs<MovieDetailsFragmentArgs>()

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    private var activityToolbar: MaterialToolbar? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as MoviesApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activityToolbar = requireActivity().findViewById(R.id.toolbar)
        if (savedInstanceState == null) {
            viewModel.load(args.movieId)
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is MovieDetailsState.Loading -> setLoadingState()
                is MovieDetailsState.Content -> setContentState(it)
                is MovieDetailsState.Error -> setErrorState(it)
            }
        }
    }

    private fun setLoadingState() {
        binding.progress.isVisible = true
        binding.scroll.isVisible = false
    }

    private fun setContentState(content: MovieDetailsState.Content) {
        val movie = content.movie
        activityToolbar?.apply {
            title = movie.originalName
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        binding.ivCover.load(movie.imageUrl ?: "") {
            crossfade(true)
            placeholder(R.drawable.ic_no_image)
            error(R.drawable.ic_no_image)
            fallback(R.drawable.ic_no_image)
        }

        binding.tvLocalizedName.text = movie.localizedName
        val genresText = movie.genres.joinToString(", ") { it.name }
        binding.tvMeta.text = if (genresText.isNotBlank())
            getString(R.string.meta_format, genresText, movie.year)
        else "${movie.year} год."

        binding.tvRating.text = movie.rating?.let { value ->
            getString(
                R.string.rating_with_label,
                value.toString(),
                getString(R.string.rating_label)
            )
        } ?: getString(R.string.rating_unknown)

        binding.tvDescription.text = movie.description.orEmpty()

        binding.progress.isVisible = false
        binding.scroll.isVisible = true
    }

    private fun setErrorState(error: MovieDetailsState.Error) {
        binding.progress.isVisible = false
        binding.scroll.isVisible = false
        Snackbar.make(binding.root, error.message, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.retry)) { viewModel.load(args.movieId) }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activityToolbar?.apply {
            title = getString(R.string.title_movies)
            navigationIcon = null
            setNavigationOnClickListener(null)
        }
        activityToolbar = null
        _binding = null
    }
}
