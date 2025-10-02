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
import com.google.android.material.snackbar.Snackbar
import java.util.Locale
import javax.inject.Inject

class MovieDetailsFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MovieDetailsViewModel> { viewModelFactory }
    private val args by navArgs<MovieDetailsFragmentArgs>()

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

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
        // Кнопка "назад" в собственной Top App Bar
        binding.ivBack.setOnClickListener { findNavController().navigateUp() }

        if (savedInstanceState == null) {
            viewModel.load(args.movieId)
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { st ->
            when (st) {
                is MovieDetailsState.Loading -> setLoadingState()
                is MovieDetailsState.Content -> setContentState(st)
                is MovieDetailsState.Error -> setErrorState(st)
            }
        }
    }

    private fun setLoadingState() {
        binding.progress.isVisible = true
        binding.scroll.isVisible = false
    }

    private fun setContentState(content: MovieDetailsState.Content) {
        val movie = content.movie

        // Заголовок в Top App Bar
        binding.tvOriginalTitle.text = movie.originalName ?: movie.localizedName

        // Постер
        binding.ivCover.load(movie.imageUrl ?: "") {
            crossfade(true)
            placeholder(R.drawable.no_img)
            error(R.drawable.no_img)
            fallback(R.drawable.no_img)
        }

        // Локализованное название
        binding.tvLocalizedName.text = movie.localizedName

        // Жанры + год
        val genresText = movie.genres.joinToString(", ") { it.name }
        binding.tvMeta.text = if (genresText.isNotBlank()) {
            getString(R.string.meta_format, genresText, movie.year)
        } else {
            getString(R.string.year_format, movie.year)
        }

        // Рейтинг: значение + метка
        if (movie.rating != null) {
            binding.tvRatingValue.text = movie.rating.let {
                String.format(Locale.US, "%.1f", it)
            }
            binding.tvRatingLabel.isVisible = true
        } else {
            binding.tvRatingValue.text = getString(R.string.rating_unknown)
            binding.tvRatingLabel.isVisible = false
        }

        // Описание
        binding.tvDescription.text = movie.description.orEmpty()

        // Показ контента
        binding.progress.isVisible = false
        binding.scroll.isVisible = true

        // На всякий случай возвращаемся к началу
        binding.scroll.post { binding.scroll.scrollTo(0, 0) }
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
        _binding = null
    }
}
