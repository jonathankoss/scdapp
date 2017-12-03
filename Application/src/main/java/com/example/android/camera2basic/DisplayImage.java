package com.example.android.camera2basic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import android.view.View.OnClickListener;


/**
 * Created by Jonathan on 5/17/2016.
 *
 *
 */



public class DisplayImage extends Activity implements OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback{

    static{ System.loadLibrary("opencv_java3"); }
    private static final String TAG = "MyActivity";
    public Mat imageMat = new Mat();
    public Bitmap croppedPhoto;
    public Bitmap thresholdedBitmap;
    public Mat filledContourMat = new Mat();
    public Bitmap filledContourBitmap;
    public String dir;
    public Bitmap uncroppedPhoto;
    public File imageFilePath;
    private boolean mDemoMode;

    private BaseLoaderCallback  mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Mat imageMat = new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }

        }

    };

    public Bitmap cropToSquare(Bitmap bitmap){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();

        //Print the size of original photo to toast for debugging only
        Context context = getApplicationContext();
        //CharSequence text = "width:" + width + "height: " + height;

        //Initialize the top left corner to begin cropping
        //Real
//        int cropW = 1250;
//        int cropH = 225;

        //Test
//        int cropW = 225;
//        int cropH = 1250;

        //Choose the size of the square to be cropped. Must represent 1mm x 1mm.
        // TODO: Should be made dynamic based on the width and height from above.
        int newWidth = (int) Math.floor(0.47238372093*width);
        int newHeight = (int) Math.floor(0.83979328165*height);

        int cropW;
        int cropH;

        if (mDemoMode) {
            //test
            cropW = (width-newWidth)/2;
            cropH = (height-newHeight)/2;
        } else {
            // real
            cropW = (width-newWidth)/2;
            cropH = (height-newHeight)/2;
        }

        CharSequence text = "width:" + newWidth + "height: " + newHeight;

        Log.e(TAG,"height:" + newWidth + "new height:" + newHeight);
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }

    //makes it black and white
    public Bitmap threshold(Bitmap bitmap){
        Utils.bitmapToMat(bitmap, imageMat);
        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY);
        //Imgproc.equalizeHist(imageMat,imageMat);
        Imgproc.GaussianBlur(imageMat, imageMat, new Size(21, 21), 0);
        Imgproc.adaptiveThreshold(imageMat, imageMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 35,  4);


        Utils.matToBitmap(imageMat, bitmap);
        return bitmap;
    }

    //fills in donut like cells so we can count better
    public Bitmap fillContours(Bitmap bitmap){

        Utils.bitmapToMat(bitmap,filledContourMat);
        Imgproc.cvtColor(filledContourMat, filledContourMat, Imgproc.COLOR_BGR2GRAY, 3);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hier = new Mat();
        Scalar white = new Scalar( 255, 255, 255 );
        Scalar red = new Scalar( 255, 0, 0 );
        Imgproc.findContours(filledContourMat,contours,hier, Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(filledContourMat,contours,-1,white,-1);
        Utils.matToBitmap(filledContourMat, bitmap);

        return bitmap;

    }

//    private void takeScreenshot() {
//        Date now = new Date();
//        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
//
//        try {
//            // image naming and path  to include sd card  appending name you choose for file
//            String mPath = Environment.getExternalStorageDirectory().toString() + now + ".jpg";
//
//            // create bitmap screen capture
//            View v1 = getWindow().getDecorView().getRootView();
//            v1.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
//            v1.setDrawingCacheEnabled(false);
//
//            File imageFile = new File(mPath);
//
//            FileOutputStream outputStream = new FileOutputStream(imageFile);
//            int quality = 100;
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
//
//            outputStream.flush();
//            outputStream.close();
//            bitmap.recycle();
//
//        } catch (Throwable e) {
//            // Several error may come out with file handling or OOM
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onResume()
    {
        super.onResume();

        findViewById(R.id.process).setEnabled(true);

        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mOpenCVCallBack);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mOpenCVCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

            System.out.println("It worked!");

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_image);



        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0 , this, mOpenCVCallBack))
        {
            Log.e("TEST", "Cannot connect to OpenCV Manager");
        }
        Button button =(Button) findViewById(R.id.process);
        Button button1 = (Button) findViewById(R.id.newPicture);
        Button button2 = (Button) findViewById(R.id.savePicture);
        button.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);


        Date now = new Date();
        CharSequence date = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm", now);

        ((TextView)findViewById(R.id.dateText)).setText(date);

        Intent i = getIntent();

        // Receiving the Data
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        ImageView image = (ImageView) findViewById(R.id.capturedImage);

        uncroppedPhoto = BitmapHolder.bitmap;
        croppedPhoto = cropToSquare(uncroppedPhoto);

                    //croppedPhoto = uncroppedPhoto;
                    image.setImageBitmap(croppedPhoto);

//        int count = 0;
//        int maxTries = 100;
//
//            while(true){
//                try {
//
//                    uncroppedPhoto = BitmapFactory.decodeFile(imageFilePath);
//                    croppedPhoto = cropToSquare(uncroppedPhoto);
//
//                    //croppedPhoto = uncroppedPhoto;
//                    image.setImageBitmap(croppedPhoto);
//                    break;
//                } catch (Exception e) {
//                    if (++count == maxTries) throw e;
//                }
//            }


    }

    @Override
    public void onClick(View v) {

        ImageView image = (ImageView) findViewById(R.id.capturedImage);
            switch(v.getId())
            {
                case R.id.process:
                    findViewById(R.id.process).setEnabled(false);

                    thresholdedBitmap = threshold(croppedPhoto);

                    image.setImageBitmap(thresholdedBitmap);

                    MatOfKeyPoint matOfKeyPoints1 = new MatOfKeyPoint();
                    Mat thresholdedMat = new Mat();

                    Utils.bitmapToMat(thresholdedBitmap, thresholdedMat);
                    Imgproc.cvtColor(thresholdedMat, thresholdedMat, Imgproc.COLOR_BGR2GRAY, 3);

                    filledContourBitmap = fillContours(thresholdedBitmap);

                    FeatureDetector blobDetector = FeatureDetector.create(FeatureDetector.SIMPLEBLOB);
                    //blobDetector.write("sdcard/blob.xml");

                    Mat croppedMat = new Mat();
                    Bitmap cellsCircledBitmap;
                    Utils.bitmapToMat(croppedPhoto,croppedMat);
                    Scalar red = new Scalar(255, 0, 0);

                    blobDetector.read("/storage/emulated/0/DCIM/blob.xml");
                    blobDetector.detect(thresholdedMat, matOfKeyPoints1);
                    Features2d.drawKeypoints(filledContourMat,matOfKeyPoints1,croppedMat,red,Features2d.DRAW_RICH_KEYPOINTS);
                    Utils.matToBitmap(croppedMat,croppedPhoto);
                    image.setImageBitmap(croppedPhoto);

                    Context context = getApplicationContext();
                    CharSequence text = "" + matOfKeyPoints1.height();



                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    ((TextView)findViewById(R.id.cellCountText)).setText(text);
                    try{
                        Thread.sleep(100);
                    }

                    catch(InterruptedException e){

                    }

                    //takeScreenshot();
                    break;

                case R.id.newPicture:
                    Intent nextScreen = new Intent(DisplayImage.this.getApplicationContext(), CameraActivity.class);

                    startActivity(nextScreen);
                    break;

                case R.id.savePicture:
                    if (image.getDrawable() != null) {

                        takeScreenshot();
//                        MediaStore.Images.Media.insertImage(getContentResolver(),
//                                ((BitmapDrawable)image.getDrawable()).getBitmap(),
//                                getIntent().getStringExtra("imagefilepath")+"processed.png", null);
//
//                        String filename = getIntent().getStringExtra("imagefilepath");
//                        Toast.makeText(getApplicationContext(), "Saved to "+(filename.substring(0, filename.length() - 3)+"processed.png"), Toast.LENGTH_SHORT).show();
                    }
        }
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
//            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            MediaStore.Images.Media.insertImage(getContentResolver(),
                    bitmap,
                    getIntent().getStringExtra("imagefilepath")+"processed.png", null);

            String filename = getIntent().getStringExtra("imagefilepath");
            Toast.makeText(getApplicationContext(), "Saved to "+(filename.substring(0, filename.length() - 3)+"processed.png"), Toast.LENGTH_SHORT).show();

        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }
}
