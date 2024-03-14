package com.example.clothchecker

import org.tensorflow.lite.Interpreter
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ClothingClassifier(private val assetManager: AssetManager) {

    companion object {
        private const val TAG = "ClothingClassifier"
        private const val MODEL_FILE_NAME = "clothing_classifier_model.tflite"
        private const val INPUT_WIDTH = 28
        private const val INPUT_HEIGHT = 28
        private const val INPUT_CHANNEL = 1
        private const val MODEL_INPUT_NAME = "input"
        private const val MODEL_OUTPUT_NAME = "Identity"
    }

    private lateinit var interpreter: Interpreter

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val model = loadModelFile(assetManager, MODEL_FILE_NAME)
            interpreter = Interpreter(model)
            Log.d(TAG, "Model loaded successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading TensorFlow model: ${e.message}")
        }
    }

    private fun loadModelFile(assetManager: AssetManager, modelFileName: String): ByteBuffer {
        val modelInputStream = assetManager.open(modelFileName)
        val modelBuffer = modelInputStream.readBytes()
        return ByteBuffer.wrap(modelBuffer)
    }

    fun classifyImage(bitmap: Bitmap): Float {
        // Preprocess the input image
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_WIDTH, INPUT_HEIGHT, true)
        val byteBuffer = convertBitmapToByteBuffer(resizedBitmap)

        // Run inference
        val output = Array(1) { FloatArray(1) }
        interpreter.run(byteBuffer, output)

        // Return the classification result
        return output[0][0]
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(INPUT_WIDTH * INPUT_HEIGHT * INPUT_CHANNEL * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(INPUT_WIDTH * INPUT_HEIGHT)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until INPUT_WIDTH) {
            for (j in 0 until INPUT_HEIGHT) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16 and 0xFF) / 255.0).toFloat())
            }
        }
        return byteBuffer
    }
}
