package space.iqbalsyafiq.moviedb.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import space.iqbalsyafiq.moviedb.R
import space.iqbalsyafiq.moviedb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_MovieDB)
        setContentView(binding.root)
    }
}