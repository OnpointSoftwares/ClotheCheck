package com.example.clothchecker

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.squareup.picasso.Picasso
class ImagePagerAdapter : PagerAdapter() {

    private var imageUrlList: List<String> = listOf()
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private val delayTime: Long = 3000 // Delay time in milliseconds (3 seconds)
    private var currentItem = 0
    fun setImageUrls(imageUrls: List<String>) {
        imageUrlList = imageUrls
        notifyDataSetChanged()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = LayoutInflater.from(container.context)
            .inflate(R.layout.image_slider_item, container, false) as LinearLayout
        Picasso.get().load(imageUrlList[position]).into(imageView.findViewById<ImageView>(R.id.imageViewSlider))
        container.addView(imageView)
        startAutomaticSliding(container as ViewPager)
        return imageView
    }
    private fun startAutomaticSliding(viewPager:ViewPager) {
        runnable = object : Runnable {
            override fun run() {
                if (currentItem == imageUrlList.size - 1) {
                    currentItem = 0
                } else {
                    currentItem++
                }
                viewPager.setCurrentItem(currentItem, true)
                handler.postDelayed(this, delayTime)
            }
        }
        handler.postDelayed(runnable!!, delayTime)
    }
    override fun getCount(): Int {
        return imageUrlList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
