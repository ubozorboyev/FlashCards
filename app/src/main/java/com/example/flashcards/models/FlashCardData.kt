package com.example.flashcards.models

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.room.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


@Entity(tableName = "flash_card_data")
data class FlashCardData(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    var name:String,
    var cardCount:Int=0,
    @ColorInt
    var backgroundColor:Int= Color.parseColor("#F7E378"),
    var isDelete:Boolean=false
)

@Entity(tableName = "card_data",foreignKeys = arrayOf(ForeignKey(
    entity = FlashCardData::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("flash_card_id"))),
    indices = arrayOf(Index(value = ["flash_card_id"]))
)
data class CardData(
    @PrimaryKey(autoGenerate = true) var id:Int=0,
    @ColumnInfo(name = "flash_card_id") val flashCardId:Int,
    var forText:String?,
    var forImage:String?,
    var backText:String?,
    var backImage:String?
)