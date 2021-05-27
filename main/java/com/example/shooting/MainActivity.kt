package com.example.shooting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

open class MainActivity : AppCompatActivity() {

    private lateinit var proDia: ProgressDialog
    private val VIDEO_PICK_GALLERY_CODE = 100
    private val VIDEO_PICK_CAMERA_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private lateinit var cameraPermissions: Array<String>
    private var videoUri: Uri? = null
    private var title: String = ""
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)

        cameraPermissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        button.setOnClickListener(){
            videoPickDialog()
//            val intent = Intent(this, VideoActivity::class.java)
//            intent.putExtra("url", videoUri)
//            startActivity(intent)
        }
    }

    fun requestCameraPermissions(){
        ActivityCompat.requestPermissions(
            this,
            cameraPermissions,
            CAMERA_REQUEST_CODE
        )
    }


    fun checkCameraPermissions(): Boolean{
        val result1 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val result2 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return result1 && result2
    }

    fun videoPickGallery(){
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent, "选择一个视频"),
            VIDEO_PICK_GALLERY_CODE
        )
    }

    fun videoPickCamera(){
        var intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)
    }

    fun videoPickDialog(){
        val options = arrayOf("拍照", "相册")
        val builder = AlertDialog.Builder(this)

        builder.setTitle("选择视频来源")
            .setItems(options){ dialogInterface, i->
                if (i==0){
                    if(!checkCameraPermissions()){
                        requestCameraPermissions()
                    }else{
                        videoPickCamera()
                    }
                }
                else{
                    videoPickGallery()
                }
            }
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQUEST_CODE ->
                if (grantResults.size > 0) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted) {

                    } else {
                        Toast.makeText(this, "需要相机和存储权限", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== RESULT_OK){
            if (requestCode==VIDEO_PICK_CAMERA_CODE){
                videoUri = data!!.data
                val intent = Intent(this, VideoActivity::class.java)
                intent.putExtra("url", videoUri.toString())
                startActivity(intent)
            }
            else if(requestCode==VIDEO_PICK_GALLERY_CODE){
                videoUri = data!!.data
                val intent = Intent(this, VideoActivity::class.java)
                intent.putExtra("url", videoUri.toString())
                startActivity(intent)
            }
        }else{
            Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NewApi")
    fun getPathFromUri(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        // 判斷是否為Android 4.4之後的版本
        val after44 = Build.VERSION.SDK_INT >= 19
        if (after44 && DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是Android 4.4之後的版本，而且屬於文件URI
            val authority = uri.authority
            // 判斷Authority是否為本地端檔案所使用的
            if ("com.android.externalstorage.documents" == authority) {
                // 外部儲存空間
                val docId = DocumentsContract.getDocumentId(uri)
                val divide = docId.split(":".toRegex()).toTypedArray()
                val type = divide[0]
                return if ("primary" == type) {
                    Environment.getExternalStorageDirectory().absolutePath.plus("/").plus(
                        divide[1]
                    )
                } else {
                    "/storage/" + type + "/" + divide[1]
                }
            } else if ("com.android.providers.downloads.documents" == authority) {
                // 下載目錄
                val docId = DocumentsContract.getDocumentId(uri)
                if (docId.startsWith("raw:")) {
                    return docId.replaceFirst("raw:".toRegex(), "")
                }
                val downloadUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    docId.toLong()
                )
                return queryAbsolutePath(context, downloadUri)
            } else if ("com.android.providers.media.documents" == authority) {
                // 圖片、影音檔案
                val docId = DocumentsContract.getDocumentId(uri)
                val divide = docId.split(":".toRegex()).toTypedArray()
                val type = divide[0]
                var mediaUri: Uri? = null
                mediaUri = if ("image" == type) {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                } else {
                    return null
                }
                mediaUri = ContentUris.withAppendedId(mediaUri, divide[1].toLong())
                return queryAbsolutePath(context, mediaUri)
            }
        } else {
            // 如果是一般的URI
            val scheme = uri.scheme
            var path: String? = null
            if ("content" == scheme) {
                // 內容URI
                path = queryAbsolutePath(context, uri)
            } else if ("file" == scheme) {
                // 檔案URI
                path = uri.path
            }
            return path
        }
        return null
    }

    fun queryAbsolutePath(context: Context, uri: Uri?): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        var cursor: Cursor? = null
        try {
            cursor = uri?.let { context.getContentResolver().query(it, projection, null, null, null) }
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                return cursor.getString(index)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            if (cursor != null) {
                cursor.close()
            }
        }
        return null
    }
}