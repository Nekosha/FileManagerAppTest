package edu.hust.it4875.hungvt.filemanager

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import edu.hust.it4875.hungvt.filemanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    inner class DialogCallback: DialogInterface.OnClickListener {
        override fun onClick(p0: DialogInterface?, button: Int): Unit {
            if (button == DialogInterface.BUTTON_POSITIVE)
                acquirePermissions()
            else
                finish()
        }
    }
    private lateinit var binding: ActivityMainBinding
    private val necessary_permissions: Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var necessary_permissions_granted = false
    private val INIT_PERMISSIONS_REQUEST_KEY = 0x12345678.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        acquirePermissions()
        val dialogCallback = DialogCallback()
        while (!permissionsOk()) {
            Log.v("initCheckPerms", "Not enough permissions")
            AlertDialog.Builder(this)
                .setTitle("Permission denied")
                .setMessage("To use File Manager, please grant access to all files")
                .setCancelable(false)
                .setPositiveButton("Ok", dialogCallback)
                .setNegativeButton("Exit", dialogCallback)
                .show()
        }
        val folder_view = FolderView(this)
        binding.folderView.adapter = folder_view
        binding.buttonChangeColor.setOnClickListener {
            folder_view.goUp()
        }

    }
    private fun permissionsOk(): Boolean { return necessary_permissions_granted }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != INIT_PERMISSIONS_REQUEST_KEY) return
        necessary_permissions_granted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    }
    private fun acquirePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 23 - 29
            if (necessary_permissions.any {
                    val denied: Boolean = checkSelfPermission(it) == PackageManager.PERMISSION_DENIED
                    Log.v("initCheckPermsList", "${it.toString()} = ${if (denied) "Fail" else "Ok"}")
                    denied
            })
                requestPermissions(necessary_permissions, INIT_PERMISSIONS_REQUEST_KEY)
            else
                necessary_permissions_granted = true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30+
            if (!Environment.isExternalStorageManager())
                startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
            necessary_permissions_granted = Environment.isExternalStorageManager()
        } else {
            // Assume permission granted by manifest
            necessary_permissions_granted = true
            return
        }
    }

    override fun onStop() {
        super.onStop()
    }
}