package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.CustomerRepository
import com.example.ui.FtthApp
import com.example.ui.FtthManagerViewModel
import com.example.ui.FtthManagerViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CustomerRepository(database.customerDao())
        val viewModelFactory = FtthManagerViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: FtthManagerViewModel = viewModel(factory = viewModelFactory)
                FtthApp(viewModel = viewModel)
            }
        }
    }
}
