package com.example.quickbill

import java.util.*


class Order {
    var id: Int
    var title: String
    var description: String
    var deleted: Date?

    constructor(id: Int, title: String, description: String, deleted: Date?) {
        this.id = id
        this.title = title
        this.description = description
        this.deleted = deleted
    }

    constructor(id: Int, title: String, description: String) {
        this.id = id
        this.title = title
        this.description = description
        deleted = null
    }

    companion object {
        var noteArrayList = ArrayList<Order>()
        var NOTE_EDIT_EXTRA = "noteEdit"
        fun getNoteForID(passedNoteID: Int): Order? {
            for (note in noteArrayList) {
                if (note.id == passedNoteID) return note
            }
            return null
        }

        fun nonDeletedNotes(): ArrayList<Order> {
            val nonDeleted = ArrayList<Order>()
            for (note in noteArrayList) {
                if (note.deleted == null) nonDeleted.add(note)
            }
            return nonDeleted
        }
    }
}