import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.quickbill.Order
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SQLiteManager(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val sql: StringBuilder
        sql = StringBuilder()
            .append("CREATE TABLE ")
            .append(TABLE_NAME)
            .append("(")
            .append(COUNTER)
            .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(ID_FIELD)
            .append(" INT, ")
            .append(TITLE_FIELD)
            .append(" TEXT, ")
            .append(DESC_FIELD)
            .append(" TEXT, ")
            .append(DELETED_FIELD)
            .append(" TEXT)")
        sqLiteDatabase.execSQL(sql.toString())

//        val order = Order(1,"title","description")
//        addNoteToDatabase(order)
//        populateNoteListArray()
//        for (element in Order.noteArrayList) {
//            println("Anudeep: $element")
//        }
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        switch (oldVersion)
//        {
//            case 1:
//                sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + NEW_COLUMN + " TEXT");
//            case 2:
//                sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + NEW_COLUMN + " TEXT");
//        }
    }

    fun addNoteToDatabase(note: Order) {
        val sqLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID_FIELD, note.id)
        contentValues.put(TITLE_FIELD, note.title)
        contentValues.put(DESC_FIELD, note.description)
        contentValues.put(DELETED_FIELD, getStringFromDate(note.deleted))
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues)
    }

    fun populateNoteListArray() {
        val sqLiteDatabase = this.readableDatabase
        sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null).use { result ->
            if (result.count != 0) {
                while (result.moveToNext()) {
                    val id = result.getInt(1)
                    val title = result.getString(2)
                    val desc = result.getString(3)
                    val stringDeleted = result.getString(4)
//                    val deleted = getDateFromString(stringDeleted)
                    val note = Order(id, title, desc)//, deleted)
                    Order.noteArrayList.add(note)
                }
            }
        }
    }

    fun updateNoteInDB(note: Order) {
        val sqLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID_FIELD, note.id)
        contentValues.put(TITLE_FIELD, note.title)
        contentValues.put(DESC_FIELD, note.description)
        contentValues.put(DELETED_FIELD, getStringFromDate(note.deleted))
        sqLiteDatabase.update(
            TABLE_NAME,
            contentValues,
            ID_FIELD + " =? ",
            arrayOf(java.lang.String.valueOf(note.id))
        )
    }

    private fun getStringFromDate(date: Date?): String? {
        return if (date == null) null else dateFormat.format(date)
    }

    private fun getDateFromString(string: String): Date? {
        return try {
            dateFormat.parse(string)
        } catch (e: ParseException) {
            null
        } catch (e: NullPointerException) {
            null
        }
    }

    companion object {
        private var sqLiteManager: SQLiteManager? = null
        private const val DATABASE_NAME = "UserDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "UserOrders"
        private const val COUNTER = "Counter"
        private const val ID_FIELD = "id"
        private const val TITLE_FIELD = "title"
        private const val DESC_FIELD = "desc"
        private const val DELETED_FIELD = "deleted"

        @SuppressLint("SimpleDateFormat")
        private val dateFormat: DateFormat = SimpleDateFormat("MM-dd-yyyy HH:mm:ss")
        fun instanceOfDatabase(context: Context?): SQLiteManager? {
            if (sqLiteManager == null) sqLiteManager = SQLiteManager(context)
            return sqLiteManager
        }
    }
}