package com.yubin.imagepicker.adapter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.viewpager.widget.PagerAdapter;

import com.yubin.imagepicker.ImagePicker;
import com.yubin.imagepicker.R;
import com.yubin.imagepicker.bean.ImageItem;
import com.yubin.imagepicker.util.Utils;

import java.util.ArrayList;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchImageView;
import me.panpf.sketch.display.DefaultImageDisplayer;
import me.panpf.sketch.request.DisplayOptions;

public class ImagePageAdapter extends PagerAdapter {

    private int screenWidth;
    private int screenHeight;
    private ImagePicker imagePicker;
    private ArrayList<ImageItem> images;
    private Activity mActivity;
    public PhotoViewClickListener listener;
    private LayoutInflater inflater;
    private DisplayOptions displayOptions;

    public ImagePageAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = activity;
        this.images = images;

        DisplayMetrics dm = Utils.getScreenPix(activity);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        imagePicker = ImagePicker.getInstance();
        inflater = LayoutInflater.from(activity);
        Sketch.with(mActivity).getConfiguration().setMobileDataPauseDownloadEnabled(false);
        displayOptions = new DisplayOptions();
        displayOptions.setLoadingImage(R.mipmap.default_image);
        displayOptions.setErrorImage(R.mipmap.default_error_image);
        displayOptions.setDecodeGifImage(true);
        displayOptions.setThumbnailMode(true);
    }

    public void setData(ArrayList<ImageItem> images) {
        this.images = images;
    }

    public void setPhotoViewClickListener(PhotoViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View contentView = inflater.inflate(R.layout.image_preview_item, container, false);
        SketchImageView sketchImageView = (SketchImageView) contentView.findViewById(R.id.photoView);


        sketchImageView.setZoomEnabled(true);
        final ImageItem imageItem = images.get(position);
        ImageButton videoButton = (ImageButton) contentView.findViewById(R.id.videoButton);
        if (!imageItem.isVideo) {
            Sketch.with(mActivity).display(imageItem.path, sketchImageView)
                    .options(displayOptions)
                    .displayer(new DefaultImageDisplayer())
                    .commit();
            videoButton.setVisibility(View.GONE);
        } else {
            imagePicker.getImageLoader().displayVideoPreview(mActivity, imageItem.path, sketchImageView, screenWidth, screenHeight);
            videoButton.setVisibility(View.VISIBLE);
        }
        sketchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnPhotoTapListener(v);
                }
            }
        });
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onVideoButtonClicked(imageItem);
                }
            }
        });
        container.addView(contentView, 0);
        return contentView;
    }

    @Override
    public int getCount() {
        if (images == null) {
            return 0;
        }
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public interface PhotoViewClickListener {
        /**
         * photoview tap
         *
         * @param view
         */
        void OnPhotoTapListener(View view);

        /**
         * 播放视频
         *
         * @param imageItem
         */
        void onVideoButtonClicked(ImageItem imageItem);

        /**
         * 保存图片
         *
         * @param uri          显示的图片地址
         * @param diskCacheKey 缓存key
         */
        void onSaveImage(String uri, String diskCacheKey);
    }
}
