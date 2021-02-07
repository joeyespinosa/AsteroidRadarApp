package com.udacity.asteroidradarapp.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.udacity.asteroidradarapp.R
import com.udacity.asteroidradarapp.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val asteroid = DetailFragmentArgs.fromBundle(requireArguments()).selectedAsteroid
        binding.asteroid = asteroid
        binding.imageviewHelp.setOnClickListener {
            displayExplanationDialog()
        }

        return binding.root
    }

    private fun displayExplanationDialog() {
        val builder = AlertDialog.Builder(requireActivity())
            .setPositiveButton(android.R.string.ok, null)
            .setMessage(getString(R.string.astronomical_unit_explanation))
        builder.create().show()
    }
}
