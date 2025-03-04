package com.aidaole.ideepseek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.aidaole.ideepseek.home.HomePage
import com.aidaole.ideepseek.api.TokenManager
import com.aidaole.ideepseek.api.AndroidDeepSeekApi
import com.aidaole.ideepseek.db.ChatDatabaseManager
import com.aidaole.ideepseek.db.DatabaseDriverFactory
import com.aidaole.ideepseek.home.ChatViewModel

class MainActivity : ComponentActivity() {
    private val tokenManager by lazy { TokenManager(applicationContext) }
    private val api by lazy { AndroidDeepSeekApi(tokenManager, applicationContext) }
    private val chatViewModel by viewModels<ChatViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChatViewModel(api, ChatDatabaseManager(DatabaseDriverFactory(applicationContext))) as T
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 确保系统栏可以被控制
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // 设置状态栏颜色
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()
            
            DisposableEffect(systemUiController, useDarkIcons) {
                systemUiController.setSystemBarsColor(
                    color = Color.White,
                    darkIcons = true // 使用深色图标
                )
                onDispose {}
            }
            
            HomePage(chatViewModel)
        }
    }
}