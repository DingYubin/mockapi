package com.yubin.imagepicker.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getech.android.utils.DialogUtils;
import com.getech.android.utils.ImageUtils;
import com.getech.android.utils.ToastUtils;
import com.getech.imagepicker.bean.ImagePickerResult;
import com.yubin.imagepicker.DataHolder;
import com.yubin.imagepicker.ImageDataSource;
import com.yubin.imagepicker.ImagePicker;
import com.yubin.imagepicker.R;
import com.yubin.imagepicker.adapter.ImageFolderAdapter;
import com.yubin.imagepicker.adapter.ImageRecyclerAdapter;
import com.yubin.imagepicker.bean.ImageFolder;
import com.yubin.imagepicker.bean.ImageItem;
import com.yubin.imagepicker.util.Utils;
import com.yubin.imagepicker.view.FolderPopUpWindow;
import com.yubin.imagepicker.view.GridSpacingItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;


public class ImageGridActivity extends ImageBaseActivity implements ImageDataSource.OnImagesLoadedListener, ImageRecyclerAdapter.OnImageItemClickListener, ImagePicker.OnImageSelectedListener, View.OnClickListener {

    public static final int REQUEST_PERMISSION_STORAGE = 0x01;
    public static final int REQUEST_PERMISSION_CAMERA = 0x02;
    public static final String EXTRAS_TAKE_PICKERS = "TAKE";
    public static final String EXTRAS_IMAGES = "IMAGES";
    public static final String EXTRAS_VIDEOS = "VIDEOS";

    private ImagePicker imagePicker;

    private View mFooterBar;     //底部栏
    private Button mBtnOk;       //确定按钮
    private View mllDir; //文件夹切换按钮
    private TextView mtvDir; //显示当前文件夹
    private LinearLayout originContainer; //显示原始图片
    private TextView mBtnPre;      //预览按钮
    private ImageFolderAdapter mImageFolderAdapter;    //图片文件夹的适配器
    private FolderPopUpWindow mFolderPopupWindow;  //ImageSet的PopupWindow
    private List<ImageFolder> mImageFolders;   //所有的图片文件夹
    private boolean directPhoto = false; // 默认不是直接调取相机
    private RecyclerView mRecyclerView;
    private ImageRecyclerAdapter mRecyclerAdapter;
    private boolean selectOrigin = true;
    private boolean selectVideo = false;
    private TextView titleTextView;
    private Dialog loadingDialog;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        directPhoto = savedInstanceState.getBoolean(EXTRAS_TAKE_PICKERS, false);
        selectVideo = savedInstanceState.getBoolean(EXTRAS_VIDEOS, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRAS_TAKE_PICKERS, directPhoto);
        outState.putBoolean(EXTRAS_VIDEOS, selectVideo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);

        imagePicker = ImagePicker.getInstance();
        imagePicker.clear();
        imagePicker.addOnImageSelectedListener(this);

        Intent data = getIntent();
        // 新增可直接拍照
        if (data != null && data.getExtras() != null) {
            // 默认不是直接打开相机
            directPhoto = data.getBooleanExtra(EXTRAS_TAKE_PICKERS, false);
            selectVideo = data.getBooleanExtra(EXTRAS_VIDEOS, false);
            if (directPhoto) {
                if (!(checkPermission(Manifest.permission.CAMERA))) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ImageGridActivity.REQUEST_PERMISSION_CAMERA);
                } else {
                    imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE);
                }
            }
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(EXTRAS_IMAGES);
            imagePicker.setSelectedImages(images);
        }

        mRecyclerView = findViewById(R.id.recycler);

        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnOk = findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mBtnPre = findViewById(R.id.btn_preview);
        mBtnPre.setOnClickListener(this);
        mFooterBar = findViewById(R.id.footer_bar);
        mllDir = findViewById(R.id.ll_dir);
        mllDir.setOnClickListener(this);
        mtvDir = findViewById(R.id.tv_dir);
        titleTextView = findViewById(R.id.tv_des);
        if (selectVideo) {
            mtvDir.setText(R.string.ip_all_video);
            titleTextView.setText(getString(R.string.ip_video));
        } else {
            mtvDir.setText(R.string.ip_all_images);
            titleTextView.setText(getString(R.string.ip_images));
        }
        originContainer = findViewById(R.id.originContainer);
        if (!ImagePicker.getInstance().isShowOrigin() || selectVideo) {
            originContainer.setVisibility(View.GONE);
        } else {
            originContainer.setVisibility(View.VISIBLE);
        }
        originContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOrigin = !selectOrigin;
                Drawable drawable = ContextCompat.getDrawable(
                        ImageGridActivity.this,
                        selectOrigin ? R.mipmap.unselect : R.mipmap.selected);
                ((ImageView) findViewById(R.id.originImageView)).setImageDrawable(drawable);
            }
        });
        if (selectVideo) {
            findViewById(R.id.originImageView).setVisibility(View.GONE);
        } else {
            findViewById(R.id.originImageView).setVisibility(View.VISIBLE);
        }
        if (imagePicker.isMultiMode()) {
            mBtnOk.setVisibility(View.VISIBLE);
            mBtnPre.setVisibility(View.VISIBLE);
        } else {
            mBtnOk.setVisibility(View.GONE);
            mBtnPre.setVisibility(View.GONE);
        }

        mImageFolderAdapter = new ImageFolderAdapter(this, null, selectVideo);
        mRecyclerAdapter = new ImageRecyclerAdapter(this, null, selectVideo);

        onImageSelected(0, null, false);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new ImageDataSource(this, null, this, selectVideo);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
            }
        } else {
            new ImageDataSource(this, null, this, selectVideo);
        }
        showLoading(selectVideo ? "加载中..." : "加载中...");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ImageDataSource(this, null, this, selectVideo);
            } else {
                showToast("权限被禁止，无法选择本地图片");
                finish();
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE);
            } else {
                showToast("权限被禁止，无法打开相机");
            }
        }
    }

    @Override
    protected void onDestroy() {
        imagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            if (!imagePicker.isCompressImage() && !selectVideo) {
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                intent.putExtra(ImagePicker.EXTRA_RESULT_COMPRESS, selectOrigin);
                //多选不允许裁剪裁剪，返回数据
                setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
                finish();
            } else {
                //选择视频
                if (selectVideo) {
                    compressVideos(imagePicker.getSelectedImages());
                    return;
                }
                //处理图片
                compressImages(imagePicker.getSelectedImages());
            }
        } else if (id == R.id.ll_dir) {
            if (mImageFolders == null) {
                Log.i("ImageGridActivity", "您的手机没有图片");
                return;
            }
            //点击文件夹按钮
            createPopupFolderList();
            //刷新数据
            mImageFolderAdapter.refreshData(mImageFolders);
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.showAtLocation(mFooterBar, Gravity.NO_GRAVITY, 0, 0);
                //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
                int index = mImageFolderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                mFolderPopupWindow.setSelection(index);
            }
        } else if (id == R.id.btn_preview) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imagePicker.getSelectedImages());
            intent.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);
        } else if (id == R.id.btn_back) {
            //点击返回按钮
            finish();
        }
    }

    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList() {
        mFolderPopupWindow = new FolderPopUpWindow(this, mImageFolderAdapter);
        mFolderPopupWindow.setOnItemClickListener(new FolderPopUpWindow.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mImageFolderAdapter.setSelectIndex(position);
                imagePicker.setCurrentImageFolderPosition(position);
                mFolderPopupWindow.dismiss();
                ImageFolder imageFolder = (ImageFolder) adapterView.getAdapter().getItem(position);
                if (null != imageFolder) {
                    mRecyclerAdapter.refreshData(imageFolder.images);
                    mtvDir.setText(imageFolder.name);
                }
            }
        });
        mFolderPopupWindow.setMargin(mFooterBar.getHeight());
    }

    @Override
    public void onImagesLoaded(List<ImageFolder> imageFolders) {
        this.mImageFolders = imageFolders;
        imagePicker.setImageFolders(imageFolders);
        if (imageFolders.size() == 0) {
            mRecyclerAdapter.refreshData(null);
        } else {
            mRecyclerAdapter.refreshData(imageFolders.get(0).images);
        }
        mRecyclerAdapter.setOnImageItemClickListener(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, Utils.dp2px(this, 2), false));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mImageFolderAdapter.refreshData(imageFolders);
        hideLoading();
    }

    @Override
    public void onImageItemClick(View view, ImageItem imageItem, int position) {
        //根据是否有相机按钮确定位置
        position = imagePicker.isShowCamera() ? position - 1 : position;
        if (imagePicker.isMultiMode()) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
            // 但采用弱引用会导致预览弱引用直接返回空指针
            DataHolder.getInstance().save(DataHolder.DH_CURRENT_IMAGE_FOLDER_ITEMS, imagePicker.getCurrentImageFolderItems());
            //如果是多选，点击图片进入预览界面
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);
        } else {
            imagePicker.clearSelectedImages();
            imagePicker.addSelectedImageItem(position, imagePicker.getCurrentImageFolderItems().get(position), true);
            if (imagePicker.isCrop()) {
                Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                //单选需要裁剪，进入裁剪界面
                startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);
            } else {
                if (!imagePicker.isCompressImage()) {
                    Intent intent = new Intent();
                    intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                    //单选不需要裁剪，返回数据
                    setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
                    finish();
                } else {
                    //选择视频
                    if (selectVideo) {
                        compressVideos(imagePicker.getSelectedImages());
                        return;
                    }
                    //处理图片
                    compressImages(imagePicker.getSelectedImages());
                }
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (imagePicker.getSelectImageCount() > 0) {
            mBtnOk.setText(getString(R.string.ip_select_complete, imagePicker.getSelectImageCount(), imagePicker.getSelectLimit()));
            mBtnOk.setEnabled(true);
            mBtnPre.setEnabled(true);
            mBtnPre.setText(getResources().getString(R.string.ip_preview_count, imagePicker.getSelectImageCount()));
            mBtnPre.setTextColor(ContextCompat.getColor(this, R.color.ip_text_primary_inverted));
            mBtnOk.setTextColor(ContextCompat.getColor(this, R.color.ip_text_primary_inverted));
        } else {
            mBtnOk.setText(getString(R.string.ip_complete));
            mBtnOk.setEnabled(false);
            mBtnPre.setEnabled(false);
            mBtnPre.setText(getResources().getString(R.string.ip_preview));
            mBtnPre.setTextColor(ContextCompat.getColor(this, R.color.ip_text_secondary_inverted));
            mBtnOk.setTextColor(ContextCompat.getColor(this, R.color.ip_text_secondary_inverted));
        }
        for (int i = imagePicker.isShowCamera() ? 1 : 0; i < mRecyclerAdapter.getItemCount(); i++) {
            if (mRecyclerAdapter.getItem(i).path != null && mRecyclerAdapter.getItem(i).path.equals(item.path)) {
                mRecyclerAdapter.notifyItemChanged(i);
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null) {
            if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            } else {
                //从拍照界面返回
                //点击 X , 没有选择照片
                if (data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) == null) {
                    //什么都不做 直接调起相机
                    finish();
                } else {
                    //说明是从裁剪页面过来的数据，直接返回就可以
                    if (!imagePicker.isCompressImage() && !selectVideo) {
                        setResult(ImagePicker.RESULT_CODE_ITEMS, data);
                        finish();
                    } else {
                        ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        if (selectVideo) {
                            compressVideos(images);
                            return;
                        }
                        compressImages(images);
                    }
                }
            }
        } else {
            //如果是裁剪，因为裁剪指定了存储的Uri，所以返回的data一定为null
            if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_TAKE) {
                //发送广播通知图片增加了
                ImagePicker.galleryAddPic(this, imagePicker.getTakeImageFile());
                String path = imagePicker.getTakeImageFile().getAbsolutePath();

                ImageItem imageItem = ImagePicker.getInstance().getImageInfoByPath(this, path);
                if (directPhoto) {
                    imagePicker.clearSelectedImages();
                }
                //添加到第二个单元格中
                mRecyclerAdapter.addItem(imageItem);
                //第二个单元格选中
                imagePicker.addSelectedImageItem(0, imageItem, true);
                if (!directPhoto) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.scrollToPosition(0);
                        }
                    }, 50);
                }
                if (imagePicker.isCrop()) {
                    Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                    //单选需要裁剪，进入裁剪界面
                    startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);
                } else {
                    if (directPhoto) {
                        if (!imagePicker.isCompressImage()) {
                            Intent intent = new Intent();
                            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                            //单选不需要裁剪，返回数据
                            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
                            finish();
                            return;
                        } else {
                            if (selectVideo) {
                                compressVideos(imagePicker.getSelectedImages());
                                return;
                            }
                            compressImages(imagePicker.getSelectedImages());
                        }
                    }
                }
            } else if (directPhoto) {
                finish();
            }
        }
    }

    public void compressImages(final ArrayList<ImageItem> items) {
        showLoading("正在处理图片，请稍后...");
        io.reactivex.Observable.just(items)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<ArrayList<ImageItem>, ObservableSource<ArrayList<ImageItem>>>() {
                    @Override
                    public ObservableSource<ArrayList<ImageItem>> apply(ArrayList<ImageItem> imageItems) throws Exception {
                        ArrayList<ImageItem> compressedItems = new ArrayList<>();
                        for (ImageItem imageItem : items) {
                            try {
                                File compresedImage = Luban.with(ImageGridActivity.this).get(imageItem.path);
                                if (compresedImage == null || !compresedImage.exists()) {
                                    compressedItems.add(imageItem);
                                    continue;
                                }
                                ImageItem compressItem = new ImageItem();
                                compressItem.name = imageItem.name;
                                compressItem.path = compresedImage.getAbsolutePath();
                                compressItem.size = compresedImage.length();
                                int[] imageSize = ImageUtils.getSize(compresedImage);
                                compressItem.width = imageSize[0];
                                compressItem.height = imageSize[1];
                                compressItem.mimeType = imageItem.mimeType;
                                compressItem.addTime = System.currentTimeMillis();
                                compressItem.isVideo = false;
                                compressedItems.add(compressItem);
                            } catch (Throwable e) {
                                compressedItems.add(imageItem);
                            }
                        }
                        return io.reactivex.Observable.just(compressedItems);
                    }
                })
                .subscribe(new Consumer<ArrayList<ImageItem>>() {
                    @Override
                    public void accept(ArrayList<ImageItem> imageItems) throws Exception {
                        hideLoading();
                        ArrayList<HashMap<String, String>> originMaps = new ArrayList<>();
                        for (ImageItem imageItem : items) {
                            originMaps.add(imageItem.toImageHashmap());
                        }
                        ArrayList<HashMap<String, String>> compressMaps = new ArrayList<>();
                        for (ImageItem imageItem : imageItems) {
                            compressMaps.add(imageItem.toImageHashmap());
                        }
                        //返回数据
                        ImagePickerResult imagePickerResult = new ImagePickerResult(originMaps, compressMaps);
                        Intent intent = new Intent();
                        intent.putExtra(ImagePicker.EXTRA_RESULT_MEDIA_ITEMS, imagePickerResult);
                        intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                        intent.putExtra(ImagePicker.EXTRA_RESULT_COMPRESS, selectOrigin);
                        //单选不需要裁剪，返回数据
                        setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showShort("图片压缩失败...");
                        hideLoading();
                    }
                });
    }

    public void compressVideos(final ArrayList<ImageItem> videos) {
        showLoading("正在处理视频，请稍后...");
        io.reactivex.Observable.just(videos)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<ArrayList<ImageItem>, ObservableSource<ArrayList<HashMap<String, String>>>>() {
                    @Override
                    public ObservableSource<ArrayList<HashMap<String, String>>> apply(ArrayList<ImageItem> imageItems) throws Exception {
                        ArrayList<HashMap<String, String>> videoItems = new ArrayList<>();
                        for (ImageItem imageItem : videos) {
                            HashMap<String, String> map = imageItem.toVideoHashmap(ImageGridActivity.this);
                            videoItems.add(map);
                        }
                        return io.reactivex.Observable.just(videoItems);
                    }
                })
                .subscribe(new Consumer<ArrayList<HashMap<String, String>>>() {
                    @Override
                    public void accept(ArrayList<HashMap<String, String>> videos) throws Exception {
                        hideLoading();
                        //返回数据
                        Intent intent = new Intent();
                        intent.putExtra(ImagePicker.EXTRA_RESULT_VIDEO_ITEMS, videos);
                        //单选不需要裁剪，返回数据
                        setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastUtils.showShort("图片压缩失败...");
                        hideLoading();
                    }
                });
    }

    private void showLoading(String msg) {
        if (loadingDialog != null) {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
            loadingDialog = null;
        }
        loadingDialog = DialogUtils.showDialog(this, msg);
        loadingDialog.show();
    }

    private void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}