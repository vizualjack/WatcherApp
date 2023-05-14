package dev.vizualjack.watcherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import dev.vizualjack.watcherapp.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private var PATH_KEY = "DATA_FILE"
    private var RESULT_CODE_FILECHOOSER = 1337
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    public lateinit var watcherUtil:WatcherUtil
    public var selectedSeriesId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        loadData()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun loadData() {
        val saveFilePath = getPath()
        if (saveFilePath == null) {
            openFileChooser()
            return
        }
        val uri = android.net.Uri.parse(saveFilePath)
        val inStream = contentResolver.openInputStream(uri)
        var jsonString = BufferedReader(InputStreamReader(inStream)).readText()
        inStream!!.close()
        watcherUtil = WatcherUtil(jsonString)
    }

    private fun saveData() {
        val saveFilePath = getPath() ?: return
        val uri = android.net.Uri.parse(saveFilePath)
        val jsonString = watcherUtil.data.toString()
        val outputStream = contentResolver.openOutputStream((uri))
        outputStream!!.write(jsonString.toByteArray())
        outputStream!!.close()
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.setType("*/*")
        startActivityForResult(intent, RESULT_CODE_FILECHOOSER)
    }

    @SuppressLint("WrongConstant")
    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        resultData: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == RESULT_CODE_FILECHOOSER && resultCode == RESULT_OK) {
            if (resultData != null) {
                val uri = resultData.data
                val takeFlags = resultData.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                contentResolver.takePersistableUriPermission(uri!!,takeFlags) //safe to just ask once for folder
                savePath(uri.toString())
                loadData()
            }
        }
    }

    private fun savePath(path: String) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(PATH_KEY, path)
            apply()
        }
    }

    private fun getPath(): String? {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return null
        return sharedPref.getString(PATH_KEY, null)
    }
}