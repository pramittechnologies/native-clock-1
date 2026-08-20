package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.ClockApp
import com.example.ui.theme.ClockTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ClockTheme {
        ClockApp()
      }
    }
  }
}

@Composable
fun Greeting(name: String) {
  // Kept for screenshot test compatibility
  ClockTheme {
    ClockApp()
  }
}

@Preview(showBackground = true)
@Composable
fun ClockAppPreview() {
  ClockTheme {
    ClockApp()
  }
}

