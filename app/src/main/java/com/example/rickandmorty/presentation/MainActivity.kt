package com.example.rickandmorty.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.ActivityMainBinding
import com.example.rickandmorty.presentation.ui.character.CharactersFragment
import com.example.rickandmorty.presentation.ui.episode.EpisodeFragment
import com.example.rickandmorty.presentation.ui.location.LocationFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        setTheme(R.style.Theme_RickAndMorty)
        setContentView(binding.root)

        replaceFragment(CharactersFragment(), savedInstanceState)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_characters -> replaceFragment(CharactersFragment(), savedInstanceState)
                R.id.menu_locations -> replaceFragment(LocationFragment(), savedInstanceState)
                R.id.menu_episode -> replaceFragment(EpisodeFragment(), savedInstanceState)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }
}
