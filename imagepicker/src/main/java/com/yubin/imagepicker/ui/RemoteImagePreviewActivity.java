package com.yubin.imagepicker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.getech.android.utils.ToastUtils;
import com.yubin.imagepicker.ImagePicker;
import com.yubin.imagepicker.R;
import com.yubin.imagepicker.adapter.ImagePageAdapter;
import com.yubin.imagepicker.adapter.RemoteImagePageAdapter;
import com.yubin.imagepicker.bean.ImageItem;
import com.yubin.imagepicker.util.Utils;
import com.yubin.imagepicker.view.ViewPagerFixed;

import java.io.File;
import java.util.ArrayList;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.DiskCache;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class RemoteImagePreviewActivity extends AppCompatActivity {
    /**
     * 跳转进ImagePreviewFragment的图片文件夹
     */
    protected ArrayList<String> imageUrls;
    /**
     * 跳转进ImagePreviewFragment时的序号，第几个图片
     */
    protected int mCurrentPosition = 0;
    protected View content;
    protected ViewPagerFixed mViewPager;
    protected RemoteImagePageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_image_previewer);
        if (getIntent() == null) {
            finish();
            return;
        }
        mViewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        imageUrls = getIntent().getStringArrayListExtra(ImagePicker.EXTRA_REMOTE_PREVIEW_URLS);
        mCurrentPosition = getIntent().getIntExtra(ImagePicker.EXTRA_REMOTE_PREVIEW_FIRST_POSITION, 0);
        boolean canCache = getIntent().getBooleanExtra(ImagePicker.EXTRA_REMOTE_PREVIEW_CAN_SAVE, false);
        //初始化控件
        content = findViewById(android.R.id.content);

        mAdapter = new RemoteImagePageAdapter(this, imageUrls, canCache);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
            }
        });
        mAdapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view) {
                finish();
            }

            @Override
            public void onVideoButtonClicked(ImageItem imageItem) {
            }

            @Override
            public void onSaveImage(String uri, String diskCacheKey) {
                if (TextUtils.isEmpty(uri)) {
                    ToastUtils.showShort(getString(R.string.image_file_save_failed));
                    return;
                }
                try {
                    //本地图片
                    if (!uri.startsWith("http")) {
                        File localFile = new File(uri);
                        if (localFile == null || !localFile.exists() || localFile.length() < 10) {
                            ToastUtils.showShort(getString(R.string.image_file_save_failed));
                            return;
                        }
                        String savedFilePath = Utils.saveImageToPhotoGallery(RemoteImagePreviewActivity.this, uri, RemoteImagePreviewActivity.this.getApplication().getPackageName());
                        if (!TextUtils.isEmpty(savedFilePath)) {
                            ToastUtils.showShort(getString(R.string.image_file_save_to) + savedFilePath);
                        } else {
                            ToastUtils.showShort(getString(R.string.image_file_save_failed));
                        }
                        return;
                    }
                    DiskCache diskCache = Sketch.with(RemoteImagePreviewActivity.this).getConfiguration().getDiskCache();
                    if (diskCache == null) {
                        ToastUtils.showShort(getString(R.string.image_file_save_failed));
                        return;
                    }
                    DiskCache.Entry entry = diskCache.get(diskCacheKey);
                    if (entry == null) {
                        ToastUtils.showShort(getString(R.string.image_file_save_failed));
                        return;
                    }
                    File file = entry.getFile();
                    if (file == null || !file.exists() || file.length() < 10) {
                        ToastUtils.showShort(getString(R.string.image_file_save_failed));
                        return;
                    }
                    String savedFilePath = Utils.saveImageToPhotoGallery(RemoteImagePreviewActivity.this, file.getAbsolutePath(), RemoteImagePreviewActivity.this.getApplication().getPackageName());
                    if (!TextUtils.isEmpty(savedFilePath)) {
                        ToastUtils.showShort(getString(R.string.image_file_save_to) + savedFilePath);
                    } else {
                        ToastUtils.showShort(getString(R.string.image_file_save_failed));
                    }
                } catch (Exception e) {
                    ToastUtils.showShort(getString(R.string.image_file_save_failed));
                }
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, false);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(ImagePicker.RESULT_CODE_BACK, intent);
        finish();
        super.onBackPressed();
    }
}
