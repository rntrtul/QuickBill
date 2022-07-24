package com.example.quickbill.ui.qr_code_manager

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.quickbill.util.QRUtil

class QRCodeCreatorViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var squareLocationId: String = ""
    var restaurantName: String = ""
    var tableIndexStartFromStr: String = ""
    var numTables: String = ""

    private var parsedTableStartIndex: Int = 0
    private var parsedNumTables: Int = 0

    private fun tryParseNum(numStr: String?, rangeLow: Int, rangeHigh: Int): Integer? {
        if (numStr == null) return null
        try {
            val num = Integer.parseInt(numStr)
            if (num < rangeLow) return null
            if (num >rangeHigh) return null
            return Integer(num)
        } catch(exception: Exception) {
            return null
        }
    }

    fun setInput(squareLocationId: String, restaurantName : String, tableIndexStartFromStr: String, numTables: String) {
        this.squareLocationId = squareLocationId.trim()
        this.restaurantName = restaurantName.trim()
        this.tableIndexStartFromStr = tableIndexStartFromStr.trim()
        this.numTables = numTables.trim()
    }

    // Returns null if there are no errors
    fun checkForInputError(): String? {
        if (squareLocationId == null || squareLocationId!!.isEmpty()) return "Invalid Square location ID"

        val parsedTableStartIndexResult = tryParseNum(tableIndexStartFromStr, 1, 1000000000)
        if (parsedTableStartIndexResult != null) {
            parsedTableStartIndex = parsedTableStartIndexResult.toInt()
        } else {
            return "Invalid table number start"
        }

        val parsedNumTablesResult = tryParseNum(numTables, 1, 1000000000)
        if (parsedNumTablesResult != null) {
            parsedNumTables = parsedNumTablesResult.toInt()
        } else {
            return "Invalid # of tables"
        }

        return null
    }

    // Returns filename
    fun genQrPdf(context: Context): String {
        val fileName = "QuickBill-MyQRCodes-" + System.currentTimeMillis() + ".pdf"

        // Create the header strings.
        var headerList = ArrayList<String>()
        var qrCodeStrList = ArrayList<String>()
        val lastTableIndex = parsedTableStartIndex + parsedNumTables - 1
        for (tableIndex in parsedTableStartIndex..lastTableIndex) {
            headerList.add("Please scan code for table # $tableIndex")
            if (restaurantName.isEmpty()) {
                qrCodeStrList.add("$squareLocationId-$tableIndex")
            } else {
                qrCodeStrList.add("$squareLocationId-$tableIndex-$restaurantName")
            }
        }

        QRUtil.generateQrCodePDF(context, headerList, qrCodeStrList, fileName, true)

        return fileName
    }
}