package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity() {

    private lateinit var inferenceModel: InferenceModel
    private lateinit var promptEditText: EditText
    private lateinit var generateButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var clearButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inferenceModel = InferenceModel.getInstance(this)

        promptEditText = findViewById(R.id.promptEditText)
        generateButton = findViewById(R.id.generateButton)
        resultTextView = findViewById(R.id.resultTextView)
        clearButton = findViewById(R.id.clearButton)

        generateButton.setOnClickListener {
            val prompt = promptEditText.text.toString()
            inferenceModel.generateResponseAsync(prompt)
            resultTextView.text = "" // Clear the result text as well

            lifecycleScope.launchWhenStarted {
                inferenceModel.partialResults.collectIndexed { index, (partialResult, done) ->
                    resultTextView.text = buildString {
                        append(resultTextView.text.toString())
                        append(partialResult)
                    }
                    if (done) {
                        inferenceModel.clearInternalState()
                    }
                }
            }
        }

        clearButton.setOnClickListener { // Add clearButton click listener
            promptEditText.text.clear()
            resultTextView.text = "" // Clear the result text as well
        }
    }
}