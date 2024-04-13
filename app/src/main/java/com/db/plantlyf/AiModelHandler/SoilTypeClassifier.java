package com.db.plantlyf.AiModelHandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class SoilTypeClassifier {

    private Interpreter tfliteInterpreter;
    private int inputImageWidth;
    private int inputImageHeight;
    private ByteBuffer inputBuffer;
    private final String[] labels;
    private final int numLabels;

    public SoilTypeClassifier(Context context, int inputImageWidth, int inputImageHeight, String[] labels) throws IOException {
        this.inputImageWidth = inputImageWidth;
        this.inputImageHeight = inputImageHeight;
        this.labels = labels;
        this.numLabels = labels.length;

        // Load the TFLite model from the assets folder
        tfliteInterpreter = new Interpreter(loadModelFile(context));

        // Initialize the input ByteBuffer
        int bufferSize = inputImageWidth * inputImageHeight * 3 * Float.SIZE / Byte.SIZE; // 3 for RGB channels
        inputBuffer = ByteBuffer.allocateDirect(bufferSize);
        inputBuffer.order(ByteOrder.nativeOrder());

        // Print the input tensor shape
        int[] inputShape = tfliteInterpreter.getInputTensor(0).shape();
        Log.d("Input shape", "Input Tensor Shape: " + Arrays.toString(inputShape));
    }

    private ByteBuffer loadModelFile(Context context) throws IOException {
        // Load the TFLite model from the assets folder
        InputStream inputStream = context.getAssets().open("soil_classifier.tflite");
        int modelFileSize = inputStream.available();
        ByteBuffer buffer = ByteBuffer.allocateDirect(modelFileSize);
        byte[] modelData = new byte[modelFileSize];
        inputStream.read(modelData);
        buffer.put(modelData);
        buffer.rewind();
        inputStream.close();
        return buffer;
    }

    public String classifyImage(Bitmap bitmap) {
        // Resize the input image to the model input shape
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputImageWidth, inputImageHeight, true);

        //imageView.setImageBitmap(resizedBitmap);
        // Normalize the pixel values to [0, 1] and load them into the input ByteBuffer
        inputBuffer.rewind();

        int c = 0;
        for (int y = 0; y < inputImageHeight; y++) {
            for (int x = 0; x < inputImageWidth; x++) {
                int pixelValue = resizedBitmap.getPixel(x, y);

                // Extract RGB channels and normalize to [0, 1]
                float red = ((pixelValue >> 16) & 0xFF);
                float green = ((pixelValue >> 8) & 0xFF);
                float blue = (pixelValue & 0xFF);

                inputBuffer.putFloat(red);
                inputBuffer.putFloat(green);
                inputBuffer.putFloat(blue);
                if(c < 20)
                    Log.d("Image float values", red + "," + green + "," + blue);

                c++;
            }
        }

        // Run inference
        float[][] output = new float[1][numLabels];
        tfliteInterpreter.run(inputBuffer, output);

        // Process the output and get the predicted label
        return processOutput(output);
    }

    private String processOutput(float[][] output) {
        int maxIndex = 0;
        float maxProbability = output[0][0];
        for (int i = 0; i < output[0].length; i++) {
            Log.d("Predicted output : ", output[0][i]+"");
            if (output[0][i] > maxProbability) {
                maxProbability = output[0][i];
                maxIndex = i;
            }
        }

        // Return the predicted label based on maxIndex
        return labels[maxIndex];
    }

    // Close the interpreter to release resources
    public void close() {
        tfliteInterpreter.close();
    }
}
