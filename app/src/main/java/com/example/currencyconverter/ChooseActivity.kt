package com.example.currencyconverter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyconverter.databinding.ActivityChooseBinding


class ChooseActivity : AppCompatActivity() {
    lateinit var binding: ActivityChooseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        val array: Array<Valute> = intent.getParcelableArrayExtra("array") as Array<Valute>
        val current = intent.getStringExtra("current")!!
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.menu.menuButton.setOnClickListener { cancel() }
        binding.recycler.adapter = RecyclerViewAdapter(this, array, current)
    }

    private fun cancel() {
        setResult(RESULT_CANCELED)
        finish()
    }
    /*
    val intent = Intent()
                intent.putExtra("name", etName.getText().toString())
                setResult(RESULT_OK, intent)
                ChooseActivity().finish()*/
}