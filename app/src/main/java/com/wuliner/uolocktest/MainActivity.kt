package com.wuliner.uolocktest

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.wuliner.uolocktest.databinding.ActivityMainBinding
import kotlin.text.StringBuilder

class MainActivity : AppCompatActivity() {

    //保存9个点
    private lateinit var dotArray: Array<ImageView>
    //记录上一次被点亮的点的视图
    private var lastSelectedDot: ImageView? = null
    //记录密码
    private val passwordBuilder = StringBuilder()
    //模拟密码
    private val password = "123";
    //记录所有点亮的控件
    private val selectedArray = arrayListOf<ImageView>()
    //保存所有的模型对象
    private val modelsArray = arrayListOf<ViewModel>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //创建绑定类对象
        val binding = ActivityMainBinding.inflate(layoutInflater)
        //将绑定类里面对应的View设置给Activity
        setContentView(binding.root)

        //将9个点的视图保存到数组中
        dotArray = arrayOf(
            binding.dot1,
            binding.dot2,
            binding.dot3,
            binding.dot4,
            binding.dot5,
            binding.dot6,
            binding.dot7,
            binding.dot8,
            binding.dot9
        )
        dotArray.forEach {
            modelsArray.add(ViewModel(it, R.drawable.dot_normal, R.drawable.dot_selected))
        }
        //竖线
        val verticalLineArray = arrayOf(
            binding.line14,
            binding.line25,
            binding.line36,
            binding.line47,
            binding.line58,
            binding.line69
        )
        verticalLineArray.forEach {
            modelsArray.add(ViewModel(it, R.drawable.line_1_normal, R.drawable.line_1_error))
        }
        //横线
        val landscapeLineArray = arrayOf(
            binding.line12,
            binding.line23,
            binding.line45,
            binding.line56,
            binding.line78,
            binding.line89
        )
        landscapeLineArray.forEach {
            modelsArray.add(ViewModel(it, R.drawable.line_2_normal, R.drawable.line_2_error))
        }
        //左斜
        val leftSlashLineArray = arrayOf(
            binding.line24,
            binding.line35,
            binding.line57,
            binding.line68
        )
        leftSlashLineArray.forEach {
            modelsArray.add(ViewModel(it, R.drawable.line_4_normal, R.drawable.line_4_error))
        }
        //右斜
        val rightSlashLineArray = arrayOf(binding.line15, binding.line26, binding.line48, binding.line59)
        rightSlashLineArray.forEach {
            modelsArray.add(ViewModel(it, R.drawable.line_3_normal, R.drawable.line_3_error))
        }

        //给容器添加触摸事件
         binding.container.setOnTouchListener { v, event ->

             when (event.action) {
                 MotionEvent.ACTION_DOWN -> {
                     //判断触摸点是否在圆点内部
                     val dotView = isInView(event.x, event.y)
                     if (dotView != null) {
                        //点亮圆点
                         dotView.visibility = View.VISIBLE
                         //记录下来
                         lastSelectedDot = dotView
                         //记录密码
                         passwordBuilder.append(dotView.tag as String)
                         //保存
                         selectedArray.add(dotView)
                     }

                 }
                 MotionEvent.ACTION_MOVE -> {
                     //判断触摸点是否在圆点内部
                     val dotView = isInView(event.x, event.y)
                     //处理触摸的点内部移动触发move事件
                     if (lastSelectedDot != dotView) {
                         if (dotView != null) {
                             //判断是不是第一个点
                             if (lastSelectedDot == null) {
                                 dotView.visibility = View.VISIBLE
                                 lastSelectedDot = dotView
                                 passwordBuilder.append(dotView.tag as String)
                                 //保存
                                 selectedArray.add(dotView)
                             } else {
                                 //判断路线是否有
                                 //获取上一个点和当前的tag值 形成的线的tag
                                 val lastTag = (lastSelectedDot!!.tag as String).toInt()
                                 val currentTag = (dotView.tag as String).toInt()
                                 //形成线的tag small*10 + big
                                 val lineTag = if (lastTag < currentTag) lastTag*10 + currentTag else currentTag * 10 + lastTag
                                 //获取lineTag对应的控件
                                 val lineView = binding.container.findViewWithTag<ImageView>("$lineTag")
                                 Log.v("logcat", "lineTag = $lineTag $lineView")
                                 if (lineView != null) { //有路线
                                     dotView.visibility = View.VISIBLE
                                     lineView.visibility = View.VISIBLE
                                     lastSelectedDot = dotView
                                     passwordBuilder.append(dotView.tag as String)
                                     //保存
                                     selectedArray.add(dotView)
                                     selectedArray.add(lineView)
                                  }
                             }
                         }
                     }
                 }
                 MotionEvent.ACTION_UP -> {
                    binding.alertTitle.text = passwordBuilder.toString()
                    //判断密码是否正确
                     if (passwordBuilder.toString() == password) {
                         //密码正确
                         binding.alertTitle.text = "密码解锁成功"
                     } else {
                         binding.alertTitle.text = "密码解锁失败"
                         //切换图片
                         selectedArray.forEach {
                             //找到这个控件对应的model
                             for (model in modelsArray) {
                                 if (model.view == it) {
                                     model.changeImage(false)
                                     break
                                 }
                             }
                         }
                     }

                     Handler().postDelayed({ selectedArray.forEach {

                         it.visibility = View.INVISIBLE

                         //找到这个控件对应的model
                         for (model in modelsArray) {
                             if (model.view == it) {
                                 model.changeImage(true)
                                 break
                             }
                         }

                     } }, 500)
                 }
             }
             true
         }


    }

    /**
     * 判断触摸点是否在某个圆点内部
     * 在：返回这个圆点对象
     * 不在：返回空 null
     */

    private fun isInView(x: Float, y: Float): ImageView? {
        dotArray.forEach {
            if ((x >= it.left && x <= it.right) && (y >= it.top && y <= it.bottom)) {
                return it
            }
        }
        return null
    }
}









