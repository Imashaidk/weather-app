package com.example.weatherapp  // keep whatever your actual package name is

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // Declare variables for every UI element we need to control
    private lateinit var cityEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var resultCard: LinearLayout
    private lateinit var cityNameText: TextView
    private lateinit var tempText: TextView
    private lateinit var conditionText: TextView
    private lateinit var humidityText: TextView
    private lateinit var windText: TextView
    private lateinit var errorText: TextView

    private val apiKey = "f6e36060ae8c491153e3ea0fd1e60c0b"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Connect each variable to its matching id in activity_main.xml
        cityEditText = findViewById(R.id.cityEditText)
        searchButton = findViewById(R.id.searchButton)
        resultCard = findViewById(R.id.resultCard)
        cityNameText = findViewById(R.id.cityNameText)
        tempText = findViewById(R.id.tempText)
        conditionText = findViewById(R.id.conditionText)
        humidityText = findViewById(R.id.humidityText)
        windText = findViewById(R.id.windText)
        errorText = findViewById(R.id.errorText)

        // Force focus on the search box when the app opens
        cityEditText.requestFocus()

        searchButton.setOnClickListener {
            val city = cityEditText.text.toString().trim()
            if (city.isNotEmpty()) {
                searchWeather(city)
            }
        }

        // Allow searching by pressing "Enter" on the keyboard
        cityEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val city = cityEditText.text.toString().trim()
                if (city.isNotEmpty()) {
                    searchWeather(city)
                }
                true
            } else {
                false
            }
        }
    }

    private fun searchWeather(city: String) {
        hideKeyboard()
        // Hide old results/errors before starting a new search
        resultCard.visibility = View.GONE
        errorText.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getWeather(city, apiKey)
                showResult(response)
            } catch (e: Exception) {
                // TEMPORARY: showing the real error so we can debug it
                showError("Error: ${e.javaClass.simpleName} - ${e.message}")
            }
        }
    }

    private fun showResult(response: WeatherResponse) {
        cityNameText.text = response.name
        tempText.text = "${response.main.temp}°C"
        conditionText.text = response.weather[0].description.replaceFirstChar { it.uppercase() }
        humidityText.text = "Humidity: ${response.main.humidity}%"
        windText.text = "Wind: ${response.wind.speed} km/h"

        resultCard.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        errorText.text = message
        errorText.visibility = View.VISIBLE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(cityEditText.windowToken, 0)
    }
}