package com.gammagame.vts

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*

class GameActivity : AppCompatActivity() {

    private val arrayOfHouses = Arrays.asList(
        R.drawable.iv_01_1,
        R.drawable.iv_02_2,
        R.drawable.iv_03_3,
        R.drawable.iv_04_4,
        R.drawable.iv_05_5,
        R.drawable.iv_06_6
    )

    private val arrayOfPeoples = Arrays.asList(
        R.drawable.iv_01,
        R.drawable.iv_02,
        R.drawable.iv_03,
        R.drawable.iv_04,
        R.drawable.iv_05,
        R.drawable.iv_06
    )

    private var listOfItems = arrayListOf(
        R.drawable.iv_01_but,
        R.drawable.iv_02_but,
        R.drawable.iv_03_but,
        R.drawable.iv_04_but,
        R.drawable.iv_05_but,
        R.drawable.iv_06_but
    )

    private var listPositions = arrayListOf(0, 1, 2, 3, 4, 5)
    var curHouse = 0

    lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        startGame()

        adapter = ImageAdapter(this, R.layout.list_item, listOfItems)
        gridview.adapter = adapter

        gridview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            Log.d("TAG", "Position: " + position)
            iv_people2.setImageDrawable(ContextCompat.getDrawable(this, arrayOfPeoples[position]))
            checkWin(position==listPositions[1])
        }
    }

    private fun startGame() {
        Collections.shuffle(listPositions)

        listPositions.take(3).also {
            iv_house1.setImageDrawable(ContextCompat.getDrawable(this, arrayOfHouses[it[0]]))
            iv_house2.setImageDrawable(ContextCompat.getDrawable(this, arrayOfHouses[it[1]]))
            iv_house3.setImageDrawable(ContextCompat.getDrawable(this, arrayOfHouses[it[2]]))
            curHouse = it[1]
            iv_people1.setImageDrawable(ContextCompat.getDrawable(this, arrayOfPeoples[it[0]]))
            iv_people2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.empty_field))
            iv_people3.setImageDrawable(ContextCompat.getDrawable(this, arrayOfPeoples[it[2]]))

            gridview.visibility = View.VISIBLE
            button.visibility = View.VISIBLE
            btn_again.visibility = View.GONE
            iv_right.visibility = View.INVISIBLE
            iv_lose.visibility = View.INVISIBLE
        }
    }

    private fun checkWin(isWin: Boolean) {
        gridview.visibility = View.GONE
        button.visibility = View.GONE

        run {
            if (isWin) iv_right else iv_lose
        }.visibility = View.VISIBLE

        btn_again.visibility = View.VISIBLE
        btn_again.setOnClickListener{
            startGame()
        }
    }


}
