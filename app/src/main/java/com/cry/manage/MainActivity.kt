package com.cry.manage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.cry.manage.feature.home.HomeScreen
import com.cry.manage.feature.home.project.ProjectMenuScreen
import com.cry.manage.ui.theme.CryManageTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CryManageTheme {
                var isProjectMenu by remember {
                    mutableStateOf(false)
                }

                if (isProjectMenu) {
                    ProjectMenuScreen(
                        onBackToHome = {
                            isProjectMenu = false
                        }
                    )
                } else {
                    HomeScreen(
                        onStartClick = {
                            isProjectMenu = true
                        }
                    )
                }

                BackHandler(enabled = isProjectMenu) {
                    // ProjectMenuScreen tự xử lý các màn hình con.
                    // Callback này chỉ là lớp an toàn ở cấp Activity.
                }
            }
        }
    }
}
