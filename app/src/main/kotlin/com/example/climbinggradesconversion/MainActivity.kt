package com.example.climbinggradesconversion

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
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

        // Load pairs from assets/pairs.txt
        val pairLines = assets.open("pairs.txt").bufferedReader().readLines()
            .filter { it.isNotBlank() && !it.startsWith("#") }
        val pairMap = mutableMapOf<String, MutableList<String>>()
        for (line in pairLines) {
            val parts = line.split(",")
            if (parts.size != 2) continue
            val input = parts[0].trim()
            val output = parts[1].trim()
            pairMap.getOrPut(input) { mutableListOf() }.add(output)
        }
        val inputSystems = pairMap.keys.toList()
        val inputAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, inputSystems)
        inputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInput.adapter = inputAdapter

        // Initialize output spinner based on first input system
        var currentOutputs = pairMap[inputSystems.firstOrNull() ?: ""] ?: listOf()
        var outputAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currentOutputs)
        outputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOutput.adapter = outputAdapter

        // Update output spinner when input selection changes
        spinnerInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedInput = inputSystems[position]
                val outputs = pairMap[selectedInput] ?: listOf()
                outputAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, outputs)
                outputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerOutput.adapter = outputAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

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

        // Swap button to exchange selected input and output systems
        val buttonSwap = findViewById<Button>(R.id.buttonSwap)
        buttonSwap.setOnClickListener {
            val inputPos = spinnerInput.selectedItemPosition
            val outputPos = spinnerOutput.selectedItemPosition
            spinnerInput.setSelection(outputPos)
            spinnerOutput.setSelection(inputPos)
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
