package com.zamyatinmn.maps

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.zamyatinmn.maps.ui.theme.MapsTheme

class MainActivity : ComponentActivity() {

    private val viewModel = MapViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapsTheme {
                if (viewModel.permissionGranted) OpenApp(viewModel)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) checkPermission()
                else viewModel.changePermissionState(true)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permitted ->
        val gps = permitted.getValue(Manifest.permission.ACCESS_FINE_LOCATION)
        when {
            gps -> {
                viewModel.changePermissionState(true)
            }
            !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showGoSettings()
            }
            else -> {
                showRatio()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { checkPermission() }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermission() {
        val resultCall =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (resultCall == PermissionChecker.PERMISSION_GRANTED) {
            viewModel.changePermissionState(true)
        } else {
            requestPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermission() {
        permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showGoSettings() = AlertDialog.Builder(this)
        .setTitle(getString(R.string.to_settings_title))
        .setMessage(getString(R.string.to_settings_text))
        .setPositiveButton(getString(android.R.string.ok)) { _, _ -> openApplicationSettings() }
        .setNegativeButton(getString(android.R.string.cancel)) { _, _ -> finish() }
        .create()
        .show()

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openApplicationSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        )
        settingsLauncher.launch(appSettingsIntent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showRatio() = AlertDialog.Builder(this)
        .setTitle(getString(R.string.rationale_title))
        .setMessage(getString(R.string.rationale_text))
        .setPositiveButton(getString(android.R.string.ok)) { _, _ -> requestPermission() }
        .setNegativeButton(getString(android.R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        .create()
        .show()
}