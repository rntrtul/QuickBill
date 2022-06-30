package com.example.quickbill.util

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import net.glxn.qrgen.android.QRCode

// Using this library: https://github.com/kenglxn/QRGen for QR code generation
//  as suggested by this Stack Overflow answer: https://stackoverflow.com/a/33727872

// Using this library: https://github.com/zxing/zxing for QR code scanning

fun generateQrBitmapFromString(str: String): Bitmap {
    return QRCode.from(str).bitmap()
}

fun createDebugPopupAndShow(context: Context, view: View) {
    // Code taken (and modified) from here: https://stackoverflow.com/a/24946375
    val builder = Dialog(context)
    builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
    builder.window?.setBackgroundDrawable(
        ColorDrawable(Color.TRANSPARENT)
    )
    builder.setOnDismissListener {
        //nothing;
    }
    builder.addContentView(
        view, RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    )
    builder.show()
}

// Code to demonstrate QR code generation.
fun debugShowGeneratedPopupQrCode(context: Context, str: String) {
    val qrCodeBitmap: Bitmap = generateQrBitmapFromString(str)
    val qrCodeImage = ImageView(context)
    qrCodeImage.setImageBitmap(qrCodeBitmap)
    createDebugPopupAndShow(context, qrCodeImage)
}

