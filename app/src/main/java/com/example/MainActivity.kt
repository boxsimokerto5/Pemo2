package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.AdminDashboardScreen
import com.example.ui.LoginScreen
import com.example.ui.PendingApprovalScreen
import com.example.ui.RegisterScreen
import com.example.ui.SecurityDashboardScreen
import com.example.ui.UserDashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.SipemoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SipemoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SipemoApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun SipemoApp(viewModel: SipemoViewModel) {
    val state by viewModel.uiState.collectAsState()

    // Handle back button for secondary screens
    if (state.currentScreen != AppScreen.LOGIN) {
        BackHandler {
            when (state.currentScreen) {
                AppScreen.REGISTER -> viewModel.navigateTo(AppScreen.LOGIN)
                AppScreen.PENDING_APPROVAL -> viewModel.navigateTo(AppScreen.LOGIN)
                AppScreen.ADMIN_DASHBOARD -> viewModel.logout()
                AppScreen.USER_DASHBOARD -> viewModel.logout()
                AppScreen.SECURITY_DASHBOARD -> viewModel.logout()
                AppScreen.LOGIN -> {}
            }
        }
    }

    when (state.currentScreen) {
        AppScreen.LOGIN -> {
            LoginScreen(state = state, viewModel = viewModel)
        }
        AppScreen.REGISTER -> {
            RegisterScreen(state = state, viewModel = viewModel)
        }
        AppScreen.PENDING_APPROVAL -> {
            PendingApprovalScreen(state = state, viewModel = viewModel)
        }
        AppScreen.ADMIN_DASHBOARD -> {
            AdminDashboardScreen(state = state, viewModel = viewModel)
        }
        AppScreen.USER_DASHBOARD -> {
            UserDashboardScreen(state = state, viewModel = viewModel)
        }
        AppScreen.SECURITY_DASHBOARD -> {
            SecurityDashboardScreen(state = state, viewModel = viewModel)
        }
    }
}
