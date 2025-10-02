package com.example.sequeniamovies.presentation.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.sequeniamovies.MoviesApp
import com.example.sequeniamovies.R
import com.example.sequeniamovies.databinding.FragmentMovieDetailsBinding
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.ivBack.setOnClickListener { findNavController().navigateUp() }

        if (savedInstanceState == null) {
            viewModel.load(args.movieId)
        }

        viewModel.movie.observe(viewLifecycleOwner) { movie ->
            if (movie == null) {
                // Ошибки/загрузки по ТЗ не показываем — просто уходим назад
                findNavController().navigateUp()
                return@observe
            }

            // Top bar title
            binding.tvOriginalTitle.text = movie.originalName ?: movie.localizedName

            // Poster
            binding.ivCover.load(movie.imageUrl ?: "") {
                crossfade(true)
                placeholder(R.drawable.no_img)
                error(R.drawable.no_img)
                fallback(R.drawable.no_img)
            }

            // Localized name
            binding.tvLocalizedName.text = movie.localizedName

            // Genres + year
            val genresText = movie.genres.joinToString(", ") { it.name }
            binding.tvMeta.text = if (genresText.isNotBlank()) {
                getString(R.string.meta_format, genresText, movie.year)
            } else {
                "${movie.year} год"
            }

            // Rating (точка как разделитель)
            if (movie.rating != null) {
                binding.tvRatingValue.text = String.format(Locale.US, "%.1f", movie.rating)
                binding.tvRatingLabel.visibility = View.VISIBLE
            } else {
                binding.tvRatingValue.text = getString(R.string.rating_unknown)
                binding.tvRatingLabel.visibility = View.GONE
            }

            // Description
            binding.tvDescription.text = movie.description.orEmpty()

            // Scroll to top
            binding.scroll.post { binding.scroll.scrollTo(0, 0) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
