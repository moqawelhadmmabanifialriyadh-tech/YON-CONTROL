package com.zyon.control

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.view.Gravity
import android.graphics.Color
import android.content.Context
import android.os.Build

class MainActivity : Activity() {
    private val requestCapture = 4102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(8, 17, 31)
        window.navigationBarColor = Color.rgb(8, 17, 31)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 40, 28, 28)
            setBackgroundColor(Color.rgb(8, 17, 31))
        }

        fun label(text: String, size: Float, color: Int = Color.WHITE): TextView =
            TextView(this).apply {
                this.text = text
                textSize = size
                setTextColor(color)
                gravity = Gravity.CENTER
                setPadding(8, 16, 8, 16)
            }

        fun action(text: String, onClick: () -> Unit) {
            val button = Button(this).apply {
                this.text = text
                textSize = 15f
                setOnClickListener { onClick() }
            }
            layout.addView(button, LinearLayout.LayoutParams(-1, -2).apply {
                bottomMargin = 12
            })
        }

        layout.addView(label("ZYON CONTROL", 28f, Color.rgb(66, 165, 245)))
        layout.addView(label("لوحة إدارة الجهاز", 17f))
        layout.addView(label(
            "الجهاز: ${Build.MANUFACTURER} ${Build.MODEL}\nAndroid ${Build.VERSION.RELEASE}\nالبطارية: ${batteryPercent()}%",
            15f, Color.LTGRAY
        ))

        action("بدء مشاركة الشاشة") { requestScreenCapture() }
        action("إعداد خدمة إيماءات التحكم") {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        action("معلومات التطبيق") {
            Toast.makeText(this, "الإصدار التجريبي 0.1.0", Toast.LENGTH_LONG).show()
        }

        layout.addView(label(
            "مشاركة الشاشة تتطلب موافقة نظام Android عند بدء الجلسة. " +
            "خدمة الإيماءات لا تعمل إلا بعد تفعيلها يدوياً من إعدادات الجهاز.",
            12f, Color.rgb(180, 190, 205)
        ))
        setContentView(layout)
    }

    private fun requestScreenCapture() {
        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(manager.createScreenCaptureIntent(), requestCapture)
    }

    @Deprecated("For compatibility with the starter project")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCapture) {
            val message = if (resultCode == RESULT_OK) {
                "تم منح إذن مشاركة الشاشة لهذه الجلسة. لم يُفعّل البث الشبكي بعد."
            } else {
                "لم يتم منح إذن مشاركة الشاشة."
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun batteryPercent(): Int {
        val manager = getSystemService(BATTERY_SERVICE) as android.os.BatteryManager
        return manager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
}
