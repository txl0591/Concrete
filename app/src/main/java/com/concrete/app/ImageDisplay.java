package com.concrete.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.concrete.common.nlog;

/**
 * Created by Tangxl on 2017/12/24.
 */

public class ImageDisplay extends Activity {

    private ImageView mImageShow = null;
    private String ImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mImageShow = findViewById(R.id.ImageShow);
        Bundle bundle = this.getIntent().getExtras();
        ImagePath = bundle.getString("Path");
        final ImageLoadingDialog dialog = new ImageLoadingDialog(this);
        dialog.show();
        // 两秒后关闭后dialog
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Bitmap bm = BitmapFactory.decodeFile(ImagePath);
                mImageShow.setImageBitmap(bm);
            }
        }, 1000 * 1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        finish();
        return true;
    }

    class ImageLoadingDialog extends Dialog {

        public ImageLoadingDialog(Context context) {
            super(context, R.style.ImageloadingDialogStyle);
            //setOwnerActivity((Activity) context);// 设置dialog全屏显示
        }

        private ImageLoadingDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_imageloading);
        }

    }
}
