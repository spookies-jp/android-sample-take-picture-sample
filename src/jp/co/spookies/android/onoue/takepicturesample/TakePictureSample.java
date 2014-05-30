package jp.co.spookies.android.onoue.takepicturesample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class TakePictureSample extends Activity {
    PictureCallback mJpeg = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream out = null;
            try {
                String path = Environment.getExternalStorageDirectory() + "/cameratest.jpg";
                out = new FileOutputStream(new File(path));
                out.write(data);
                out.flush();
                out.close();
                Toast.makeText(TakePictureSample.this, getString(R.string.saved_toast), Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                TakePictureSample.this.finish();
            }
        }
    };
    PreviewCallback previewCallback = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            camera.takePicture(null, null, mJpeg);
        }
    };
    Callback mCallback = new Callback() {
        Camera mCamera = null;

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mCamera.startPreview();
            mCamera.setPreviewCallback(previewCallback);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mCamera = Camera.open();
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(TakePictureSample.this, getString(R.string.failed_toast), Toast.LENGTH_LONG).show();
                TakePictureSample.this.finish();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SurfaceView view = new SurfaceView(this);
        setContentView(view);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
        view.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        view.getHolder().addCallback(mCallback);
    }
}