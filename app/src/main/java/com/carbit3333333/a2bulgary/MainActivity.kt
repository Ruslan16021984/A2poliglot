package com.carbit3333333.a2bulgary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.carbit3333333.a2bulgary.navigation.AppNavGraph
import com.carbit3333333.a2bulgary.ui.theme.A2BulgaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A2BulgaryTheme {
                A2BulgaryApp()
            }
        }
    }
}

@Composable
fun A2BulgaryApp() {
    AppNavGraph()
}

@Preview(showBackground = true)
@Composable
fun A2BulgaryPreview() {
    A2BulgaryTheme {
        A2BulgaryApp()
    }
}
