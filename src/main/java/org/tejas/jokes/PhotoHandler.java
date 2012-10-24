package org.tejas.jokes;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.Perceptron;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;

@SuppressLint("NewApi")
public class PhotoHandler implements PictureCallback {

	private int hand;

	public PhotoHandler(Context context, int hand) {
		this.hand=hand;
	}
	

	private static NeuralNetwork network2;
	
	private static TrainingSet<SupervisedTrainingElement> trainingSet;
           

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		if(network2==null){
			initNetwork();
		}


		Camera.Parameters parameters = camera.getParameters();

        int width = parameters.getPictureSize().width;
        int height = parameters.getPictureSize().height;

        ByteArrayOutputStream outstr = new ByteArrayOutputStream();
        Rect rect = new Rect(0, 0, width, height); 
        YuvImage yuvimage=new YuvImage(data, ImageFormat.NV21,width,height,null);
        yuvimage.compressToJpeg(rect, 100, outstr);
        Bitmap bmp = BitmapFactory.decodeByteArray(outstr.toByteArray(), 0, outstr.size());
        height--;
        int[] pix = new int[width * height];
        bmp.getPixels(pix, 0, width, 0, 0, width, height);
        double[] newSet = new double[width * height*3];
        for (int i = 0; i < pix.length; i++) {
            newSet[3*i] = (pix[i]) >> 16 & 0xff;
        	newSet[3*i+1] = (pix[i]) >> 8 & 0xff;
        	newSet[3*i+2] = (pix[i]) & 0xff;
        }
        trainingSet.addElement(new SupervisedTrainingElement(newSet, new double[]{hand}));
        network2.learn(trainingSet);
	}

	private void initNetwork() {
		network2 = new Perceptron(16*9*3, 1);
		trainingSet = new  TrainingSet<SupervisedTrainingElement>(16*9*3, 1);
	}

	private File getDir() {
		File sdDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		return new File(sdDir, "CameraAPIDemo");
	}
}
