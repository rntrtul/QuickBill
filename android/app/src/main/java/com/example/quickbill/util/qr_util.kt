package com.example.quickbill.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import net.glxn.qrgen.android.QRCode

// Using this library: https://github.com/kenglxn/QRGen for QR code generation
//  as suggested by this Stack Overflow answer: https://stackoverflow.com/a/33727872

// Using this library: https://github.com/zxing/zxing for QR code scanning

fun generateQrBitmapFromString( str : String ) : Bitmap {
    return QRCode.from( str ).bitmap()
}

fun createDebugPopupAndShow( context : Context, view : View) {
    // Code taken (and modified) from here: https://stackoverflow.com/a/24946375
    val builder = Dialog( context )
    builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
    builder.getWindow()?.setBackgroundDrawable(
        ColorDrawable(Color.TRANSPARENT)
    )
    builder.setOnDismissListener {
        //nothing;
    }
    builder.addContentView( view, RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    ) )
    builder.show()
}

// Code to demonstrate QR code generation.
fun debugShowGeneratedPopupQrCode( context : Context, str : String ) {
    val qrCodeBitmap: Bitmap = generateQrBitmapFromString( str )
    val qrCodeImage = ImageView( context )
    qrCodeImage.setImageBitmap( qrCodeBitmap )
    createDebugPopupAndShow( context, qrCodeImage )
}

fun startScan( activity : Activity, ) {
    val intentIntegrator = IntentIntegrator( activity )
    intentIntegrator.setDesiredBarcodeFormats( listOf( IntentIntegrator.QR_CODE ) )
    intentIntegrator.setOrientationLocked( false )
    intentIntegrator.setPrompt( "Scan the QR code in the restaurant." )
    intentIntegrator.setBeepEnabled( false )
    intentIntegrator.initiateScan()
}

// Put this inside an onActivityResult() callback.
fun getScanResult( resultCode : Int, data : Intent? ): String? {
    var result: IntentResult? = IntentIntegrator.parseActivityResult( resultCode, data ) ?: return null
    if (result != null) {
        return result.contents
    }
    return null
}

fun debugPutScanResultInPopup( context : Context, scanResult : String ) {
    val textView = TextView( context )
    textView.text = "Scan result: $scanResult"
    createDebugPopupAndShow( context, textView )
}