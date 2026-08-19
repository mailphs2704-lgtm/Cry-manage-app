package com.cry.manage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cry.manage.feature.home.HomeScreen
import com.cry.manage.feature.home.project.ProjectMenuScreen
import com.cry.manage.ui.theme.CryManageTheme
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CryManageTheme {

                var isProjectMenu by remember {
                    mutableStateOf(false)
                }

                if (isProjectMenu) {

                    ProjectMenuScreen()

                } else {

                    HomeScreen(
                        onStartClick = {
                            isProjectMenu = true
                        }
                    )
                }
            }
        }
    }
}