package com.example.currencyconverter

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.currencyconverter.databinding.ActivityChooseBinding

class ChooseActivity : AppCompatActivity(), RecyclerViewAdapter.ItemClickListener {
    val name: String = "valute"
    val arrayExtra = "array"
    val stringExtra = "current"
    lateinit var binding: ActivityChooseBinding
    lateinit var array: ArrayList<Valute>
    override fun onCreate(savedInstanceState: Bundle?) {
        array = intent.getParcelableArrayListExtra<Valute>(arrayExtra) as ArrayList<Valute>
        val current = intent.getStringExtra(stringExtra)!!
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.menu.menuButton.setOnClickListener { cancel() }
        binding.recycler.layoutManager = GridLayoutManager(this, 1)
        val detailadapter = RecyclerViewAdapter(this, array, current)
        detailadapter.setClickListener(this)
        binding.recycler.adapter = detailadapter
        val space = resources.getDimensionPixelSize(R.dimen.space)
        binding.recycler.addItemDecoration(SpaceItemDecoration(space))
    }

    private fun cancel() {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onItemClick(view: View?, position: Int) {
        array[position]
        val intent = Intent()
        intent.putExtra(name, array[position])
        setResult(RESULT_OK, intent)
        finish()
    }
}