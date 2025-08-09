package com.example.climbinggradesconversion

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val systems = arrayOf("French", "UIAA", "YDS", "British")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinnerInput = findViewById<Spinner>(R.id.spinnerInputSystem)
        val spinnerOutput = findViewById<Spinner>(R.id.spinnerOutputSystem)
        val editGrade = findViewById<EditText>(R.id.editTextGrade)
        val switchSport = findViewById<Switch>(R.id.switchSportBoulder)
        val buttonConvert = findViewById<Button>(R.id.buttonConvert)
        val textResult = findViewById<TextView>(R.id.textViewResult)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, systems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInput.adapter = adapter
        spinnerOutput.adapter = adapter

        buttonConvert.setOnClickListener {
            val inputSystem = spinnerInput.selectedItem as String
            val outputSystem = spinnerOutput.selectedItem as String
            val grade = editGrade.text.toString().trim()
            val isBoulder = switchSport.isChecked

            if (grade.isEmpty()) {
                Toast.makeText(this, "Enter a grade", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = convertGrade(inputSystem, grade, outputSystem, isBoulder)
            textResult.text = result
        }
    }

    private fun convertGrade(
        inputSystem: String,
        grade: String,
        outputSystem: String,
        isBoulder: Boolean
    ): String {
        // Load conversion table from assets/grades.txt
        val lines = assets.open("grades.txt").bufferedReader().useLines { it.toList() }
        for (line in lines) {
            if (line.startsWith("#") || line.isBlank()) continue
            val parts = line.split(",")
            if (parts.size != 4) continue
            val (inSys, inGrade, outSys, outGrade) = parts
            if (inSys.equals(inputSystem, true) &&
                inGrade.equals(grade, true) &&
                outSys.equals(outputSystem, true)
            ) {
                return "Converted $grade ($inputSystem) → $outGrade ($outputSystem) [${if (isBoulder) "Boulder" else "Sport"}]"
            }
        }
        return "No conversion found for $grade from $inputSystem to $outputSystem"
    }
}
