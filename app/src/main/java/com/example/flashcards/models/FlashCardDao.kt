package com.example.flashcards.models

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.*

@Dao
interface FlashCardDao {

    @Query("SELECT * FROM flash_card_data WHERE isDelete=0")
    fun getAllFlashCard():LiveData<List<FlashCardData>>

    @Query("SELECT * FROM flash_card_data WHERE id=:id ORDER BY 1")
    fun getFlashCard(id:Int):Flowable<FlashCardData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFlashCard(flashCardData: FlashCardData):Single<Long>

    @Query("DELETE  FROM flash_card_data WHERE id=:flashId")
    fun deleteFlashCardData(flashId:Int)

}

@Dao
interface CardDao{

    @Query("SELECT * FROM card_data WHERE flash_card_id=:cardId")
    fun getFlashByCardData(cardId:Int):Flowable<List<CardData>>

    @Query("SELECT * FROM flash_card_data WHERE id=:id")
    fun getFlashCard(id: Int):Flowable<FlashCardData>

    @Update
    fun updateFlashCard(flashCardData: FlashCardData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCardData(cardData: CardData):Maybe<Long>

    @Update
    fun updateCardData( cardData: List<CardData>)

    @Delete(entity = CardData::class)
    fun deleteCardData(cardData: CardData)

}