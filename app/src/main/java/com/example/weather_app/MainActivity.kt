package com.example.weather_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_app.ui.theme.Weather_appTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Weather_appTheme {
                WeatherApp()
            }
        }
    }
}

@Composable
fun WeatherAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF64B5F6),
            secondary = Color(0xFF81C784),
            background = Color(0xFF0D1B2A),
            surface = Color(0xFF1B263B)
        ),
        content = content
    )
}

// Data Models
data class WeatherData(
    val city: String,
    val temperature: Int,
    val condition: String,
    val humidity: Int,
    val windSpeed: Int,
    val feelsLike: Int,
    val uvIndex: Int,
    val visibility: Int,
    val pressure: Int,
    val hourlyForecast: List<HourlyWeather>,
    val dailyForecast: List<DailyWeather>
)

data class HourlyWeather(
    val time: String,
    val temp: Int,
    val icon: WeatherIcon
)

data class DailyWeather(
    val day: String,
    val high: Int,
    val low: Int,
    val icon: WeatherIcon,
    val rainChance: Int
)

enum class WeatherIcon {
    SUNNY, CLOUDY, RAINY, STORMY, PARTLY_CLOUDY, NIGHT
}

@Composable
fun WeatherApp() {
    var weatherData by remember { mutableStateOf(getSampleWeatherData()) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1500)
            weatherData = getSampleWeatherData()
            isRefreshing = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1B2A),
                        Color(0xFF1B263B),
                        Color(0xFF415A77)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            TopBar(
                city = weatherData.city,
                onRefresh = { isRefreshing = true },
                isRefreshing = isRefreshing
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Current Weather
            CurrentWeatherCard(weatherData)

            Spacer(modifier = Modifier.height(24.dp))

            // Weather Details
            WeatherDetailsGrid(weatherData)

            Spacer(modifier = Modifier.height(24.dp))

            // Hourly Forecast
            Text(
                text = "Dự báo theo giờ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            HourlyForecast(weatherData.hourlyForecast)

            Spacer(modifier = Modifier.height(24.dp))

            // Daily Forecast
            Text(
                text = "Dự báo 7 ngày",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            DailyForecast(weatherData.dailyForecast)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(city: String, onRefresh: () -> Unit, isRefreshing: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF64B5F6),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = city,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun CurrentWeatherCard(data: WeatherData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B263B).copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = getWeatherIconVector(WeatherIcon.PARTLY_CLOUDY),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = Color(0xFFFFD54F)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${data.temperature}°",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = data.condition,
                fontSize = 24.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cảm giác như ${data.feelsLike}°",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val dateFormat = SimpleDateFormat("EEEE, dd MMMM", Locale("vi"))
            Text(
                text = dateFormat.format(Date()),
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun WeatherDetailsGrid(data: WeatherData) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeatherDetailCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.WaterDrop,
                label = "Độ ẩm",
                value = "${data.humidity}%"
            )
            WeatherDetailCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Air,
                label = "Gió",
                value = "${data.windSpeed} km/h"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeatherDetailCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.WbSunny,
                label = "UV Index",
                value = data.uvIndex.toString()
            )
            WeatherDetailCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Visibility,
                label = "Tầm nhìn",
                value = "${data.visibility} km"
            )
        }
    }
}

@Composable
fun WeatherDetailCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B263B).copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF64B5F6),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f)

            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun HourlyForecast(forecast: List<HourlyWeather>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(forecast) { hour ->
            HourlyWeatherCard(hour)
        }
    }
}

@Composable
fun HourlyWeatherCard(weather: HourlyWeather) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B263B).copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .width(80.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = weather.time,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Icon(
                imageVector = getWeatherIconVector(weather.icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color(0xFFFFD54F)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${weather.temp}°",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun DailyForecast(forecast: List<DailyWeather>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        forecast.forEach { day ->
            DailyWeatherCard(day)
        }
    }
}

@Composable
fun DailyWeatherCard(weather: DailyWeather) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B263B).copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = weather.day,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF64B5F6)
                )
                Text(
                    text = "${weather.rainChance}%",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = getWeatherIconVector(weather.icon),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color(0xFFFFD54F)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Row {
                Text(
                    text = "${weather.high}°",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = " / ${weather.low}°",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

fun getWeatherIconVector(icon: WeatherIcon): ImageVector {
    return when (icon) {
        WeatherIcon.SUNNY -> Icons.Default.WbSunny
        WeatherIcon.CLOUDY -> Icons.Default.Cloud
        WeatherIcon.RAINY -> Icons.Default.WaterDrop
        WeatherIcon.STORMY -> Icons.Default.Thunderstorm
        WeatherIcon.PARTLY_CLOUDY -> Icons.Default.WbCloudy
        WeatherIcon.NIGHT -> Icons.Default.NightsStay
    }
}

fun getSampleWeatherData(): WeatherData {
    return WeatherData(
        city = "Hà Nội",
        temperature = 28,
        condition = "Có mây",
        humidity = 65,
        windSpeed = 12,
        feelsLike = 30,
        uvIndex = 6,
        visibility = 10,
        pressure = 1013,
        hourlyForecast = listOf(
            HourlyWeather("Bây giờ", 28, WeatherIcon.PARTLY_CLOUDY),
            HourlyWeather("14:00", 29, WeatherIcon.PARTLY_CLOUDY),
            HourlyWeather("15:00", 30, WeatherIcon.SUNNY),
            HourlyWeather("16:00", 29, WeatherIcon.SUNNY),
            HourlyWeather("17:00", 28, WeatherIcon.PARTLY_CLOUDY),
            HourlyWeather("18:00", 26, WeatherIcon.CLOUDY),
            HourlyWeather("19:00", 25, WeatherIcon.CLOUDY),
            HourlyWeather("20:00", 24, WeatherIcon.NIGHT)
        ),
        dailyForecast = listOf(
            DailyWeather("Hôm nay", 30, 24, WeatherIcon.PARTLY_CLOUDY, 20),
            DailyWeather("Thứ Năm", 29, 23, WeatherIcon.RAINY, 60),
            DailyWeather("Thứ Sáu", 28, 22, WeatherIcon.RAINY, 70),
            DailyWeather("Thứ Bảy", 27, 21, WeatherIcon.CLOUDY, 40),
            DailyWeather("Chủ Nhật", 29, 23, WeatherIcon.PARTLY_CLOUDY, 30),
            DailyWeather("Thứ Hai", 31, 24, WeatherIcon.SUNNY, 10),
            DailyWeather("Thứ Ba", 32, 25, WeatherIcon.SUNNY, 5)
        )
    )
}
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Weather_appTheme {
//        Greeting("Android")
//    }
//}