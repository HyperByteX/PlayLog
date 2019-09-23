package com.hyperbyte.playlogtest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.hyperbyte.playlogtest.adapters.CameraRollImagesAdapter
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.widget.LinearSmoothScroller
import com.bumptech.glide.Glide
import android.content.pm.PackageManager
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hyperbyte.playlogtest.interfaces.ImageFetchView
import com.hyperbyte.playlogtest.presenters.ImagePresenter
import com.hyperbyte.playlogtest.views.ImageWheel

class MainActivity : AppCompatActivity(), ImageFetchView {

    companion object {
        const val TAG = "MainActivity"
        const val READ_STORAGE_PERMISSION_REQUEST_CODE = 101
        const val GLIDE_ANIMATION_DURATION_MS = 1500
    }

    private val presenter = ImagePresenter(this)
    private val activity = this

    private lateinit var mImageWheel: ImageWheel

    private var mImageList = ArrayList<String>()
    private var mPreviousImageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when (hasPermission()) {
            true -> presenter.fetchGalleryImages(activity)
            false -> requestPermission()
        }

        mImageWheel = wheel

        val imageWheelInterface = object : ImageWheel.OnImageWheelEventListener {
            override fun onTap() {
                Log.d(TAG, "Image wheel button tapped. Fetching new random images.")
                presenter.fetchGalleryImages(activity)
            }

            override fun onPositionChanged(angle: Int) {
                Log.d(TAG, "Image wheel button position changed: $angle")

                if(presenter.getMaxImages() == 0){
                    Log.d(TAG, "No images in gallery.")
                    return
                }

                val newImageIndex = angle / (360 / presenter.getMaxImages())

                if (newImageIndex != mPreviousImageIndex) {
                    mPreviousImageIndex = newImageIndex
                    updateImageSelection(newImageIndex)
                }
            }
        }

        mImageWheel.setImageWheelEventListener(imageWheelInterface)
    }

    private fun hasPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = this.checkSelfPermission(READ_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED
        }

        // Any OS version under Marshmallow (6.0) doesn't require runtime permissions
        return true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), READ_STORAGE_PERMISSION_REQUEST_CODE)
    }

    override fun displayNewImageList(list: ArrayList<String>) {
        mImageList = list
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recyclerView.adapter = CameraRollImagesAdapter(mImageList)
        recyclerView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))
        updateBackground(0)
    }

    private fun updateImageSelection(index: Int) {
        val smoothScroller = object : LinearSmoothScroller(this) {
            override fun getHorizontalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

        smoothScroller.targetPosition = index
        recyclerView.layoutManager?.startSmoothScroll(smoothScroller)
        updateBackground(index)
    }

    private fun updateBackground(index: Int) {
        Glide.with(this)
            .load(mImageList[index])
            .transition(DrawableTransitionOptions.withCrossFade(GLIDE_ANIMATION_DURATION_MS))
            .centerCrop()
            .into(background)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, results: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, results)

        when (requestCode) {
            READ_STORAGE_PERMISSION_REQUEST_CODE -> {
                if (results.isEmpty() || results[0] != PackageManager.PERMISSION_GRANTED) {
                    requestPermission()
                    Toast.makeText(this, resources.getText(R.string.permission_error), Toast.LENGTH_LONG).show()
                    Log.d(TAG, "User denied permission.")
                } else {
                    presenter.fetchGalleryImages(activity)
                    Log.d(TAG, "User granted permission.")
                }
            }
        }
    }
}
