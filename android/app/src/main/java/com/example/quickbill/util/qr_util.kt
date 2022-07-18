package com.example.quickbill.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.print.PrintAttributes
import android.print.pdf.PrintedPdfDocument
import android.provider.DocumentsContract
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.quickbill.ui.pay.BillState
import com.journeyapps.barcodescanner.ScanIntentResult
import net.glxn.qrgen.android.QRCode
import java.io.File
import java.io.FileOutputStream


// Using this library: https://github.com/kenglxn/QRGen for QR code generation
//  as suggested by this Stack Overflow answer: https://stackoverflow.com/a/33727872

// Using this library: https://github.com/zxing/zxing for QR code scanning

fun generateQrBitmapFromString(str: String): Bitmap {
    return QRCode.from(str).bitmap()
}

fun generateQrCodePDF(context: Context, headers: List<String>, qrCodeStrings: List<String>, fileName: String, openFileImmediately: Boolean) {
    assert(headers.size == qrCodeStrings.size)

    val printAttributes = PrintAttributes.Builder().
        setColorMode(PrintAttributes.COLOR_MODE_COLOR).
        setMediaSize(PrintAttributes.MediaSize.ISO_A4).
        setMinMargins(PrintAttributes.Margins.NO_MARGINS).
        build()

    val document = PrintedPdfDocument(context, printAttributes);

    // start a page
    fun createPage( pageIndex : Int, headerText : String, qrCodeStr : String ) {
        val page = document.startPage( pageIndex )

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER

        // Add text to the page.
        val textView = TextView(context)
        textView.visibility = View.VISIBLE
        textView.text = headerText
        layout.addView(textView)

        // Add QR Code to page.
        val qrCodeImage = ImageView(context)
        qrCodeImage.setImageBitmap(generateQrBitmapFromString(qrCodeStr))
        layout.addView(qrCodeImage)

        // Draw the linear layout onto the page.
        val canvas = page.canvas
        layout.measure(canvas.width, canvas.height)
        layout.layout(0, 0, canvas.width, canvas.height)
        canvas.translate(0F, 0F)
        layout.draw(canvas)

        document.finishPage(page)
    }

    for ((index, qrCodeStr) in qrCodeStrings.withIndex()) {
        createPage(index, headers[index], qrCodeStr)
    }

    // Print the document to a file.
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    val absFilePath = downloadsDir.absolutePath + "/" + fileName
    val newFileOutputStream = FileOutputStream(absFilePath)
    document.writeTo(newFileOutputStream);

    //close the document
    document.close();

    fun openFile(pickerInitialUri: Uri) {
        val PICK_PDF_FILE = 2
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        context.getActivity()?.startActivityForResult(intent, PICK_PDF_FILE)
    }

    if ( openFileImmediately ) openFile(Uri.parse(absFilePath))
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

interface ParsedQRCode {
    fun parse( qrCodeStr : String ) : Boolean // Returns true if it was able to parse, false for that type of QR code, false otherwise
    fun setBill( billState : BillState)
}

class SquareQRCodeNoRestaurantName : ParsedQRCode {
    lateinit var locationId: String
    var tableNum: Int = -1

    override fun parse(qrCodeStr: String) : Boolean {
        val TAG = "SquareQRCodeNoRestaurantName.parse()"

        val scanTokens = qrCodeStr.split('-')
        Log.d(TAG, "Scan Tokens: $scanTokens")

        if (scanTokens.size != 2) {
            Log.e(TAG, "Too many tokens in QR")
            return false
        }

        locationId = scanTokens[0]
        tableNum = try {
            Integer.parseInt(scanTokens[1])
        } catch (exception: Exception) {
            Log.e(TAG, "Could not parse table number!")
            return false
        }

        return true
    }

    override fun setBill( billState: BillState ) {
        billState.locationId = locationId
        billState.tableNum = tableNum
        billState.restaurantName = null
    }
}

class SquareQRCodeWithRestaurantName : ParsedQRCode {
    lateinit var locationId: String
    lateinit var restaurantName: String
    var tableNum: Int = -1

    override fun parse(qrCodeStr: String) : Boolean {
        val TAG = "SquareQRCodeWithRestaurantName.parse()"

        val scanTokens = qrCodeStr.split('-')
        Log.d(TAG, "Scan Tokens: $scanTokens")

        if (scanTokens.size != 3) {
            Log.e(TAG, "Too many tokens in QR")
            return false
        }

        locationId = scanTokens[0]
        tableNum = try {
            Integer.parseInt(scanTokens[1])
        } catch (exception: Exception) {
            Log.e(TAG, "Could not parse table number!")
            return false
        }
        restaurantName = scanTokens[2]

        return true
    }

    override fun setBill( billState: BillState ) {
        billState.locationId = locationId
        billState.tableNum = tableNum
        billState.restaurantName = restaurantName
    }
}

fun parsedQRCodeFactory( result : ScanIntentResult ) : ParsedQRCode? {
    val contents = result.contents!!

    val possibleParsedQRCodes = listOf(SquareQRCodeNoRestaurantName(), SquareQRCodeWithRestaurantName())
    for (qrCodeType in possibleParsedQRCodes) {
        if (qrCodeType.parse(contents)) {
            return qrCodeType;
        }
    }
    return null;
}

