package com.carbit3333333.a2bulgary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.carbit3333333.a2bulgary.course.BulgarianA2CourseRepository
import com.carbit3333333.a2bulgary.ui.course.A2CourseScreen
import com.carbit3333333.a2bulgary.ui.theme.A2BulgaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A2BulgaryTheme {
                A2CourseApp()
            }
        }
    }
}

@Composable
fun A2CourseApp() {
    A2CourseScreen(units = BulgarianA2CourseRepository.units)
}

@Preview(showBackground = true)
@Composable
fun A2CoursePreview() {
    A2BulgaryTheme {
        A2CourseApp()
    }
}
