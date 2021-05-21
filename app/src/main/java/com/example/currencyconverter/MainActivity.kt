package com.example.currencyconverter

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyconverter.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.*
import java.lang.String.format
import java.math.BigDecimal
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var valute1: Valute = Valute()
    var valute2: Valute = Valute()
    val name: String = "valute"
    val arrayExtra = "array"
    val stringExtra = "current"
    var res: String? = null
    val filename = "file.xml"
    var list: ArrayList<Valute> = arrayListOf(Valute(34, "RUB", 1, "Рубль", 1.0))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.changeButton.setOnClickListener { changeCurrency() }
        binding.changeButton.setOnLongClickListener { message() }
        binding.leftButton.setOnClickListener { showChooseActivity(1) }
        binding.rightButton.setOnClickListener { showChooseActivity(2) }
        binding.leftEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (!binding.rightEdit.isFocused)
                binding.rightEdit.setText(
                    String.format(
                        calculate(
                            valute1,
                            valute2,
                            s.toString()
                        ).toString()
                    )
                )
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
        binding.rightEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (binding.rightEdit.isFocused)
                binding.leftEdit.setText(
                    String.format(
                        calculate(
                            valute2,
                            valute1,
                            s.toString()
                        ).toString()
                    )
                )
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
        downloadValutes()
    }

    private fun downloadValutes() {
        val loadRSSAsync = object : AsyncTask<String, String, String>() {
            var mDialog = ProgressDialog(this@MainActivity)

            override fun onPreExecute() {
                binding.layout.visibility = VISIBLE
                mDialog.show()
            }

            override fun onPostExecute(result: String?) {
                Handler().postDelayed({
                    mDialog.dismiss()
                    binding.layout.visibility = GONE
                }, 3000) //Only to show dialog
                valute1 = list.findByChar("RUB")
                valute2 = list.findByChar("USD")
                setValutes()
            }

            override fun doInBackground(vararg params: String): String {
                val result: String?
                val http = HTTPDataHandler()
                result = http.getHTTPDataHandler(params[0])
                writeXml(result)

                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()
                val path = applicationContext.filesDir.absolutePath
                val file = File("$path/$filename")
                val fis = FileInputStream(file)
                parser.setInput(InputStreamReader(fis))

                while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                    if (parser.eventType == XmlPullParser.START_TAG) {
                        if (parser.name == "NumCode") {
                            parser.next()
                            val number = parser.text.toString().toInt()
                            do (parser.next()) while (parser.name != "CharCode")
                            parser.next()
                            val charCode = parser.text.toString()
                            do (parser.next()) while (parser.name != "Nominal")
                            parser.next()
                            val nominal = parser.text.toString().toInt()
                            do (parser.next()) while (parser.name != "Name")
                            parser.next()
                            val name = parser.text.toString()
                            do (parser.next()) while (parser.name != "Value")
                            parser.next()
                            val value = parser.text.replace(',', '.').toDouble()
                            list.add(Valute(number, charCode, nominal, name, value))
                        }
                    }
                    parser.next()
                }
                return "done"
            }
        }
        val url_get_data = StringBuilder(getString(R.string.xml_link))
        loadRSSAsync.execute(url_get_data.toString())
    }

    private fun writeXml(result: String?) {
        try {
            val bw = BufferedWriter(
                OutputStreamWriter(
                    openFileOutput(filename, MODE_PRIVATE)
                )
            )
            bw.write(result)
            bw.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showChooseActivity(i: Int) {
        val intent = Intent(this, ChooseActivity::class.java)
        intent.putExtra(arrayExtra, list)
        if (i == 1) intent.putExtra(stringExtra, binding.leftText.text)
        else intent.putExtra(stringExtra, binding.rightText.text)
        startActivityForResult(intent, i)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((resultCode != RESULT_OK) || (data == null)) return
        if (requestCode == 1) valute1 = data.getParcelableExtra<Valute>(name)!!
        else if (requestCode == 2) valute2 = data.getParcelableExtra<Valute>(name)!!
        setValutes(requestCode)
    }

    private fun setValutes(nominal: String? = null) {
        binding.leftEdit.setText(nominal ?: valute1.Nominal.toString())
        binding.leftText.text = valute1.CharCode
        binding.rightText.text = valute2.CharCode
        binding.rightEdit.setText(
            String.format(
                calculate(
                    valute1,
                    valute2,
                    nominal ?: valute1.Nominal.toString()
                ).toString()
            )
        )
    }

    private fun setValutes(code: Int) {
        if (code == 1) binding.leftText.text = valute1.CharCode
        if (code == 2) binding.rightText.text = valute2.CharCode
        binding.rightEdit.setText(
            String.format(
                calculate(
                    valute1,
                    valute2,
                    binding.leftEdit.text.toString()
                ).toString()
            )
        )
    }

    private fun calculate(valute1: Valute, valute2: Valute, currentString: String): BigDecimal {
        val current: Double
        if (currentString.isEmpty()) current = 0.0
        else current = currentString.toDouble()
        val result: Double =
            current * (valute1.Value / valute1.Nominal) / (valute2.Value / valute2.Nominal)
        return format(
            Locale.ROOT,
            getString(R.string.float_format),
            result.toBigDecimal()
        ).toBigDecimal()
    }

    private fun changeCurrency() {
        binding.rightEdit.clearFocus()
        val valute = valute2
        valute2 = valute1
        valute1 = valute
        val length = binding.rightEdit.text.toString().length
        val x = if (length < 7) length else 7
        setValutes(binding.rightEdit.text.toString().substring(0, x))
    }

    private fun message(): Boolean {
        Snackbar.make(binding.root, getString(R.string.snack_text), Snackbar.LENGTH_LONG).show()
        return true
    }
}

private fun ArrayList<Valute>.findByChar(s: String): Valute {
    for (i in this) if (i.CharCode!!.equals(s, ignoreCase = true)) return i
    return Valute()
}