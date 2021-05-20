package com.example.currencyconverter

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyconverter.databinding.ActivityMainBinding
import org.xmlpull.v1.XmlPullParser
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var valute1: Valute = Valute()
    var valute2: Valute = Valute()
    var listOfValutes: Array<Valute> = arrayOf(valute1, valute2)
    val name: String = "valute"
    var res: String? = null
    var list: ArrayList<Valute> = arrayListOf(Valute(34, "RU", 1, "Рубль", 1.0))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.changeButton.setOnClickListener { changeCurrency() }
        binding.changeButton.setOnLongClickListener { buttonToast() }
        binding.leftButton.setOnClickListener { showChooseActivity(1) }
        binding.rightButton.setOnClickListener { showChooseActivity(2) }
        downloadValutes()
    }

    private fun downloadValutes() {
        val loadRSSAsync = object : AsyncTask<String, String, String>() {
            var mDialog = ProgressDialog(this@MainActivity)

            override fun onPreExecute() {
                mDialog.setMessage("Загрузка...")
                mDialog.show()
            }

            override fun onPostExecute(result: String?) {
                Handler().postDelayed({mDialog.dismiss()},5000) //Only to show dialog
                valute1 = list.findByChar("RU")
                valute2 = list.findByChar("USD")
                setValutes()

            }

            override fun doInBackground(vararg params: String): String {
                /*//these 3 strings for download xml from internet
                val result: String?
                val http = HTTPDataHandler()
                result = http.getHTTPDataHandler(params[0])*/

                val parser: XmlPullParser = resources.getXml(R.xml.valutes)
                while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                    var number: Int = 0
                    var charCode: String = ""
                    var nominal: Int = 1
                    var name: String = ""
                    var value: Double = 1.0
                    if (parser.eventType == XmlPullParser.START_TAG) {
                        if (parser.name == "NumCode") {
                            parser.next()
                            number = parser.text.toString().toInt()
                            do (parser.next()) while (parser.name != "CharCode")
                            parser.next()
                            charCode = parser.text.toString()
                            do (parser.next()) while (parser.name != "Nominal")
                            parser.next()
                            nominal = parser.text.toString().toInt()
                            do (parser.next()) while (parser.name != "Name")
                            parser.next()
                            name = parser.text.toString()
                            do (parser.next()) while (parser.name != "Value")
                            parser.next()
                            value = parser.text.replace(',', '.').toDouble()
                            list.add(Valute(number, charCode, nominal, name, value))
                        }
                    }
                    parser.next()
                }
                return "done"
            }
        }
        val url_get_data = StringBuilder("http://www.cbr.ru/scripts/XML_daily.asp")
        loadRSSAsync.execute(url_get_data.toString())
    }

    private fun showChooseActivity(i: Int) {
        val intent = Intent(this, ChooseActivity::class.java)
        intent.putExtra("array", listOfValutes)
        if (i == 1) intent.putExtra("current", binding.leftText.text)
        else intent.putExtra("current", binding.rightText.text)
        startActivityForResult(intent, i)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((resultCode != RESULT_OK) || (data == null)) return
        if (requestCode == 1) valute1 = data.getParcelableExtra<Valute>(name)!!
        else if (requestCode == 2) valute2 = data.getParcelableExtra<Valute>(name)!!
        setValutes(requestCode)
    }

    private fun setValutes() {
        binding.leftEdit.setText(valute1.Nominal.toString())
        binding.leftText.setText(valute1.CharCode)
        binding.rightText.setText(valute2.CharCode)
        binding.rightEdit.setText(
            calculate(
                valute1,
                valute2,
                valute1.Nominal.toString()
            ).toString()
        )
    }

    private fun setValutes(code: Int) {
        if (code == 1) {
            binding.leftText.text = valute1.CharCode
            binding.rightEdit.setText(
                calculate(
                    valute1,
                    valute2,
                    binding.leftEdit.text.toString()
                ).toString()
            )
        }
        if (code == 2) {
            binding.rightText.text = valute2.CharCode
            binding.leftEdit.setText(
                calculate(
                    valute2,
                    valute1,
                    binding.rightEdit.text.toString()
                ).toString()
            )
        }
    }

    private fun calculate(valute1: Valute, valute2: Valute, currentString: String): Double {
        val current: Double
        if (currentString.isEmpty()) current = 0.0
        else current = currentString.toDouble()
        return current * (valute2.Value / valute2.Nominal) / (valute1.Value / valute1.Nominal)
    }

    private fun changeCurrency() {
        val valute = valute2
        valute2 = valute1
        valute1 = valute
        setValutes()
    }

    private fun buttonToast(): Boolean {
        Toast.makeText(this, "Кнопка обмена валюты", Toast.LENGTH_SHORT).show()
        return true
    }
}

private fun ArrayList<Valute>.findByChar(s: String): Valute {
    for (i in this) if (i.CharCode!!.toUpperCase(Locale.ROOT) == s.toUpperCase(Locale.ROOT)) return i
    return Valute()
}
