package com.ericho.cashcalculator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ericho.cashcalculator.ui.screen.HomeScreen
import com.ericho.cashcalculator.ui.screen.SavedRecordScreen
import com.ericho.cashcalculator.ui.theme.CashCalculatorTheme
import com.ericho.cashcalculator.util.MainViewModelFactory
import com.ericho.cashcalculator.util.toHKCurrencyString

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            CashCalculatorTheme {
                val viewModel: MainViewModel =
                    viewModel(factory = MainViewModelFactory(applicationContext))

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onViewSavedRecordClick = { navController.navigate("saved_record") },
                            onShareClick = { shareIntent(this@MainActivity, viewModel) })
                    }
                    composable("saved_record") {
                        SavedRecordScreen(
                            saveRecords = viewModel.savedRecords,
                            onBackClick = navController::popBackStack,
                            onReverseClick = { viewModel.toggleRecordOrder() },
                            onDeleteClick = viewModel::deleteRecord,
                            onUndoDelete = viewModel::saveRecord
                        )
                    }
                }

            }
        }
    }
}

fun getShareResult(viewModel: MainViewModel): String {
    val result = StringBuilder()
    val valueMap = viewModel.denominations.associateWith {
        viewModel.counts[viewModel.denominations.indexOf(it)]
    }

    result.appendLine("Cash Calculator Result")
    result.appendLine()
    for ((denomination, count) in valueMap) {
        if (count.isNotEmpty()) {
            val value: Long = denomination * (count.toLongOrNull() ?: 0)
            result.appendLine("$denomination x $count = $value")
        }
    }

    result.appendLine("------------------")
    result.appendLine("Total: ${viewModel.total.toHKCurrencyString()}")

    return result.toString()
}

fun shareIntent(context: Activity, viewModel: MainViewModel) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, getShareResult(viewModel))
        type = "text/plain"
    }
    val shareIntent =
        Intent.createChooser(sendIntent, "Share cash calculation result via")
    context.startActivity(shareIntent)
}

