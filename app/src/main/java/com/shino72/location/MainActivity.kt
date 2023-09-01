package com.shino72.location

import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.coroutineScope
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.shino72.location.databinding.ActivityMainBinding
import com.shino72.location.utils.Status
import com.shino72.location.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val locationViewModel : LocationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            btn.setOnClickListener {
                requestPermission {
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch {
                        locationViewModel.getLocation()
                    }
                }
            }
        }
        lifecycle.coroutineScope.launchWhenCreated {
            locationViewModel.location.collect {
                if(it.isLoading) {
                    binding.progress.visibility = View.VISIBLE
                    binding.text.text = "Loading..."
                }
                if(it.error.isNotBlank()) {
                    binding.progress.visibility = View.GONE
                    binding.text.text = "${it.error}"
                }
                it.data?.let {
                    binding.progress.visibility = View.GONE
                    binding.text.text = "Success : latitude => ${it.latitude} / longitude ${it.longitude}"
                }
            }
        }

    }
    private fun requestPermission(logic : () -> Unit) {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    logic()
                }
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(this@MainActivity, "권한을 허가해주세요", Toast.LENGTH_SHORT).show()
                }
            }).setDeniedMessage("위치 권한을 허용해주세요.").setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION).check()
    }
}