package com.hyperbyte.playlogtest.presenters

import android.app.Activity
import android.provider.MediaStore
import com.hyperbyte.playlogtest.MainActivity
import kotlin.random.Random

class ImagePresenter(private val view: MainActivity) {

    // use var instead of a constant val because this might change if the user has < 10 images
    private var mMaxImages = 10

    fun getMaxImages(): Int {
        return mMaxImages
    }

    fun fetchGalleryImages(activity: Activity) {
        val galleryImageUrls = ArrayList<String>()
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)

        val cursor = activity.managedQuery(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null
        )

        for (i in 0 until cursor.count) {
            cursor.moveToPosition(i)
            val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            galleryImageUrls.add(cursor.getString(dataColumnIndex))
        }

        view.displayNewImageList(getRandomImages(galleryImageUrls))
    }

    private fun getRandomImages(list: ArrayList<String>): ArrayList<String> {
        val totalImages = list.size
        val hashMap: HashMap<Int, Boolean> = HashMap<Int, Boolean>()
        val randomList = ArrayList<String>()

        // reduce maximum number of images if user has less in their gallery
        if (totalImages < mMaxImages) {
            mMaxImages = totalImages
        }

        // store used index values in a hashmap to prevent duplicate photos
        var i = 0

        while (i < mMaxImages) {
            val randomIndex = Random.nextInt(0, totalImages)

            if (!hashMap.containsKey(randomIndex)) {
                hashMap.put(randomIndex, true)
                randomList.add(list[randomIndex])
                i++
            }
        }

        return randomList
    }
}