package com.example.weatherapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {

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

        cityEditText = findViewById(R.id.cityEditText)
        searchButton = findViewById(R.id.searchButton)
        resultCard = findViewById(R.id.resultCard)
        cityNameText = findViewById(R.id.cityNameText)
        tempText = findViewById(R.id.tempText)
        conditionText = findViewById(R.id.conditionText)
        humidityText = findViewById(R.id.humidityText)
        windText = findViewById(R.id.windText)
        errorText = findViewById(R.id.errorText)

        searchButton.setOnClickListener {
            val city = cityEditText.text.toString().trim()
            searchWeather(city)
        }
    }

    private fun searchWeather(city: String) {
        resultCard.visibility = View.GONE
        errorText.visibility = View.GONE

        // Case 1: Empty city name
        if (city.isEmpty()) {
            showError("Please enter a city name.")
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getWeather(city, apiKey)
                showResult(response)

            } catch (e: UnknownHostException) {
                // Case 3: No internet / can't reach the server
                showError("No internet connection. Please check your network and try again.")

            } catch (e: HttpException) {
                if (e.code() == 404) {
                    // Case 2: Invalid city
                    showError("City not found. Please check the spelling and try again.")
                } else {
                    // Case 4: API returned some other error
                    showError("Weather service error. Please try again later.")
                }

            } catch (e: Exception) {
                // Catch-all for anything unexpected
                showError("Something went wrong. Please try again.")
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
}