package com.yubin.imagepicker.adapter;

import android.Manifest;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.yubin.imagepicker.ImagePicker;
import com.yubin.imagepicker.R;
import com.yubin.imagepicker.bean.ImageItem;
import com.yubin.imagepicker.ui.ImageBaseActivity;
import com.yubin.imagepicker.ui.ImageGridActivity;
import com.yubin.imagepicker.util.Utils;
import com.yubin.imagepicker.view.SuperCheckBox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 加载相册图片的RecyclerView适配器
 * 用于替换原项目的GridView，使用局部刷新解决选中照片出现闪动问题
 * 替换为RecyclerView后只是不再会导致全局刷新，
 * 但还是会出现明显的重新加载图片，可能是picasso图片加载框架的问题
 */

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final int ITEM_TYPE_CAMERA = 0;  //第一个条目是相机
    private static final int ITEM_TYPE_NORMAL = 1;  //第一个条目不是相机
    private ImagePicker imagePicker;
    private Activity mActivity;
    private ArrayList<ImageItem> images;       //当前需要显示的所有的图片数据
    private ArrayList<ImageItem> mSelectedImages; //全局保存的已经选中的图片数据
    private boolean isShowCamera;         //是否显示拍照按钮
    private boolean isShowVideo;          //是否显示视频
    private int mImageSize;               //每个条目的大小
    private LayoutInflater mInflater;
    private OnImageItemClickListener listener;   //图片被点击的监听
    private boolean loadVideo;

    public void setOnImageItemClickListener(OnImageItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnImageItemClickListener {
        void onImageItemClick(View view, ImageItem imageItem, int position);
    }

    public void addItem(ImageItem item) {
        if (item == null || TextUtils.isEmpty(item.path)) {
            return;
        }
        images.add(0, item);
        notifyItemInserted(0);
    }

    public void refreshData(ArrayList<ImageItem> images) {
        if (images == null || images.size() == 0) {
            this.images = new ArrayList<>();
        } else {
            this.images = images;
        }
        notifyDataSetChanged();
    }

    /**
     * 构造方法
     */
    public ImageRecyclerAdapter(Activity activity, ArrayList<ImageItem> images, boolean loadVideo) {
        this.mActivity = activity;
        if (images == null || images.size() == 0) {
            this.images = new ArrayList<>();
        } else {
            this.images = images;
        }

        mImageSize = Utils.getImageItemWidth(mActivity);
        imagePicker = ImagePicker.getInstance();
        isShowCamera = imagePicker.isShowCamera();
        mSelectedImages = imagePicker.getSelectedImages();
        mInflater = LayoutInflater.from(activity);
        this.loadVideo = loadVideo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CAMERA) {
            return new CameraViewHolder(mInflater.inflate(R.layout.adapter_camera_item, parent, false));
        }
        return new ImageViewHolder(mInflater.inflate(R.layout.adapter_image_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CameraViewHolder) {
            ((CameraViewHolder) holder).bindCamera();
        } else if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) {
            if (position == 0) {
                return ITEM_TYPE_CAMERA;
            } else {
                return ITEM_TYPE_NORMAL;
            }
        }
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return isShowCamera ? images.size() + 1 : images.size();
    }

    public ImageItem getItem(int position) {
        if (isShowCamera) {
            if (position == 0) {
                return null;
            }
            return images.get(position - 1);
        } else {
            return images.get(position);
        }
    }

    private class ImageViewHolder extends ViewHolder {
        View rootView;
        ImageView ivThumb;
        View mask;
        View checkView;
        SuperCheckBox cbCheck;
        LinearLayout videoContainer;
        TextView videoDurationTextView;

        ImageViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
            mask = itemView.findViewById(R.id.mask);
            checkView = itemView.findViewById(R.id.checkView);
            cbCheck = (SuperCheckBox) itemView.findViewById(R.id.cb_check);
            videoContainer = (LinearLayout) itemView.findViewById(R.id.videoContainer);
            videoDurationTextView = (TextView) itemView.findViewById(R.id.durationTextView);

            //让图片是个正方形
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize));
        }

        void bind(final int position) {
            final ImageItem imageItem = getItem(position);
            ivThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onImageItemClick(rootView, imageItem, position);
                    }
                }
            });
            checkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cbCheck.setChecked(!cbCheck.isChecked());
                    int selectLimit = imagePicker.getSelectLimit();
                    if (cbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
                        Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(loadVideo ? R.string.ip_select_limit_video : R.string.ip_select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                        cbCheck.setChecked(false);
                        mask.setVisibility(View.GONE);
                    } else {
                        imagePicker.addSelectedImageItem(position, imageItem, cbCheck.isChecked());
                        mask.setVisibility(View.VISIBLE);
                    }
                }
            });
            //根据是否多选，显示或隐藏checkbox
            if (imagePicker.isMultiMode()) {
                cbCheck.setVisibility(View.VISIBLE);
                boolean checked = mSelectedImages.contains(imageItem);
                if (checked) {
                    mask.setVisibility(View.VISIBLE);
                    cbCheck.setChecked(true);
                } else {
                    mask.setVisibility(View.GONE);
                    cbCheck.setChecked(false);
                }
            } else {
                cbCheck.setVisibility(View.GONE);
            }
            //显示图片
            if (!imageItem.isVideo) {
                videoContainer.setVisibility(View.GONE);
                imagePicker.getImageLoader().displayImage(mActivity, imageItem.path, ivThumb, mImageSize, mImageSize);
            } else {
                videoContainer.setVisibility(View.VISIBLE);
                //设置时长
                videoDurationTextView.setText(timeParse(imageItem.duration));
                imagePicker.getImageLoader().displayVideo(mActivity, imageItem.path, ivThumb, mImageSize, mImageSize);
            }
        }
    }

    private class CameraViewHolder extends ViewHolder {
        View mItemView;

        CameraViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
        }

        void bindCamera() {
            //让图片是个正方形
            mItemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize));
            mItemView.setTag(null);
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!((ImageBaseActivity) mActivity).checkPermission(Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, ImageGridActivity.REQUEST_PERMISSION_CAMERA);
                    } else {
                        imagePicker.takePicture(mActivity, ImagePicker.REQUEST_CODE_TAKE);
                    }
                }
            });
        }
    }

    private static SimpleDateFormat msFormat = new SimpleDateFormat("mm:ss");


    public static String timeParse(long duration) {
        String time = "";
        if (duration > 1000) {
            time = timeParseMinute(duration);
        } else {
            long minute = duration / 60000;
            long seconds = duration % 60000;
            long second = Math.round((float) seconds / 1000);
            if (minute < 10) {
                time += "0";
            }
            time += minute + ":";
            if (second < 10) {
                time += "0";
            }
            time += second;
        }
        return time;
    }

    public static String timeParseMinute(long duration) {
        try {
            return msFormat.format(duration);
        } catch (Exception e) {
            e.printStackTrace();
            return "0:00";
        }
    }

}
