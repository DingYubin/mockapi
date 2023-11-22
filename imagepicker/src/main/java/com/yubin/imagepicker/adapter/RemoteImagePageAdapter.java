package com.yubin.imagepicker.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.yubin.imagepicker.R;

import java.util.ArrayList;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchImageView;
import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.display.DefaultImageDisplayer;
import me.panpf.sketch.http.ImageDownloader;
import me.panpf.sketch.request.CancelCause;
import me.panpf.sketch.request.DisplayListener;
import me.panpf.sketch.request.DisplayOptions;
import me.panpf.sketch.request.DisplayRequest;
import me.panpf.sketch.request.DownloadProgressListener;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.ImageFrom;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class RemoteImagePageAdapter extends PagerAdapter {
    private ArrayList<String> imageUrls = new ArrayList<>();
    private Activity mActivity;
    public ImagePageAdapter.PhotoViewClickListener listener;
    private LayoutInflater inflater;
    private DisplayOptions displayOptions;
    private boolean canCacheImage;

    public RemoteImagePageAdapter(Activity activity, ArrayList<String> imageUrls, boolean canCacheImage) {
        this.mActivity = activity;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(activity);
        Sketch.with(mActivity).getConfiguration().setMobileDataPauseDownloadEnabled(false);
        Sketch.with(mActivity).getConfiguration().setDownloader(new ImageDownloader());
        displayOptions = new DisplayOptions();
        displayOptions.setDecodeGifImage(true);
        displayOptions.setThumbnailMode(true);
        displayOptions.setCacheInDiskDisabled(false);
        this.canCacheImage = canCacheImage;
    }

    public void setData(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setPhotoViewClickListener(ImagePageAdapter.PhotoViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View contentView = inflater.inflate(R.layout.remote_image_preview_item, container, false);
        final SketchImageView sketchImageView = (SketchImageView) contentView.findViewById(R.id.photoView);
        final ProgressBar downloadProgressBar = (ProgressBar) contentView.findViewById(R.id.downloadProgressBar);
        final TextView downloadProgressTextView = (TextView) contentView.findViewById(R.id.downloadProgressTextView);
        final FrameLayout downloadProgressContainer = (FrameLayout) contentView.findViewById(R.id.downloadProgressContainer);
        final ImageView defaultImageView = (ImageView) contentView.findViewById(R.id.defaultImageView);
        final ImageView cacheImageView = (ImageView) contentView.findViewById(R.id.cacheImage);

        sketchImageView.setZoomEnabled(true);
        final String imageUrl = imageUrls.get(position);

        downloadProgressTextView.setText("0%");
        defaultImageView.setVisibility(View.VISIBLE);
        downloadProgressContainer.setVisibility(View.VISIBLE);
        sketchImageView.setShowDownloadProgressEnabled(true);
        if (canCacheImage) {
            cacheImageView.setVisibility(View.VISIBLE);
        } else {
            cacheImageView.setVisibility(View.GONE);
        }
        sketchImageView.setDisplayListener(new DisplayListener() {
            @Override
            public void onStarted() {
                cacheImageView.setVisibility(View.GONE);
                downloadProgressContainer.setVisibility(View.VISIBLE);
                defaultImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCompleted(@NonNull Drawable drawable, @NonNull ImageFrom imageFrom, @NonNull ImageAttrs imageAttrs) {
                cacheImageView.setVisibility(View.VISIBLE);
                downloadProgressContainer.setVisibility(View.GONE);
                defaultImageView.setVisibility(View.GONE);
            }

            @Override
            public void onError(@NonNull ErrorCause cause) {
                cacheImageView.setVisibility(View.GONE);
                downloadProgressContainer.setVisibility(View.GONE);
                defaultImageView.setVisibility(View.VISIBLE);
                defaultImageView.setImageResource(R.mipmap.default_error_image);
            }

            @Override
            public void onCanceled(@NonNull CancelCause cause) {
                cacheImageView.setVisibility(View.GONE);
                downloadProgressContainer.setVisibility(View.GONE);
                defaultImageView.setVisibility(View.VISIBLE);
            }
        });
        sketchImageView.setDownloadProgressListener(new DownloadProgressListener() {
            @Override
            public void onUpdateDownloadProgress(int totalLength, int completedLength) {
                int progress = (int) (completedLength * 1.0f / totalLength * 100);
                downloadProgressTextView.setText(progress + "%");
            }
        });
        sketchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnPhotoTapListener(v);
                }
            }
        });
        downloadProgressContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnPhotoTapListener(v);
                }
            }
        });
        final DisplayRequest displayRequest = Sketch.with(mActivity).display(imageUrl, sketchImageView)
                .options(displayOptions)
                .displayer(new DefaultImageDisplayer())
                .commit();

        cacheImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSaveImage(imageUrl, displayRequest.getDiskCacheKey());
                }
            }
        });
        container.addView(contentView, 0);
        return contentView;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
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
}
