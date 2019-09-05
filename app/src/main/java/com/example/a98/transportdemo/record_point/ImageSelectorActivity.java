package com.example.a98.transportdemo.record_point;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a98.transportdemo.BaseActivity;
import com.example.a98.transportdemo.R;
import com.example.a98.transportdemo.data.ImageListAdapter;
import com.example.a98.transportdemo.record_road.SatelliteActivity;
import com.lxj.xpopup.XPopup;
import com.yongchun.library.adapter.ImageFolderAdapter;
import com.yongchun.library.model.LocalMedia;
import com.yongchun.library.model.LocalMediaFolder;
import com.yongchun.library.utils.FileUtils;
import com.yongchun.library.utils.GridSpacingItemDecoration;
import com.yongchun.library.utils.LocalMediaLoader;
import com.yongchun.library.utils.ScreenUtils;
import com.yongchun.library.view.FolderWindow;
import com.yongchun.library.view.ImageCropActivity;
import com.yongchun.library.view.ImagePreviewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ImageSelectorActivity extends BaseActivity {
    public final static int REQUEST_IMAGE = 66;
    public final static int REQUEST_CAMERA = 67;
    public final static String BUNDLE_CAMERA_PATH = "CameraPath";

    public final static String REQUEST_OUTPUT = "outputList";

    public final static String EXTRA_SELECT_MODE = "SelectMode";
    public final static String EXTRA_SHOW_CAMERA = "ShowCamera";
    public final static String EXTRA_ENABLE_PREVIEW = "EnablePreview";
    public final static String EXTRA_ENABLE_CROP = "EnableCrop";
    public final static String EXTRA_MAX_SELECT_NUM = "MaxSelectNum";

    public final static int MODE_MULTIPLE = 1;
    public final static int MODE_SINGLE = 2;

    private int maxSelectNum = 9;
    private int selectMode = MODE_MULTIPLE;
    private boolean showCamera = true;
    private boolean enablePreview = true;
    private boolean enableCrop = false;

    private int spanCount = 3;

    private Toolbar toolbar;
    private TextView doneText;

    private TextView previewText;

    private RecyclerView recyclerView;
    private ImageListAdapter imageAdapter;

    private LinearLayout folderLayout;
    private TextView folderName;
    private FolderWindow folderWindow;

    public String cameraPath;
    public ArrayList<String> bearings=new ArrayList<>();
    public String bearing="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageselector);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        mContext=ImageSelectorActivity.this;
        maxSelectNum = getIntent().getIntExtra(EXTRA_MAX_SELECT_NUM, 9);
        selectMode = getIntent().getIntExtra(EXTRA_SELECT_MODE, MODE_MULTIPLE);
        showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        enablePreview = getIntent().getBooleanExtra(EXTRA_ENABLE_PREVIEW, true);
        enableCrop = getIntent().getBooleanExtra(EXTRA_ENABLE_CROP, false);

        if (selectMode == MODE_MULTIPLE) {
            enableCrop = false;
        } else {
            enablePreview = false;
        }
        if (savedInstanceState != null) {
            cameraPath = savedInstanceState.getString(BUNDLE_CAMERA_PATH);
        }
        initView();
        registerListener();
        new LocalMediaLoader(this, LocalMediaLoader.TYPE_IMAGE).loadAllImage(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                folderWindow.bindFolder(folders);
//                imageAdapter.bindImages(folders.get(0).getImages());
            }
        });
    }

    public void initView() {
        folderWindow = new FolderWindow(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("图片");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);

        doneText = (TextView) findViewById(R.id.done_text);
        doneText.setVisibility(selectMode == MODE_MULTIPLE ? View.VISIBLE : View.GONE);

        previewText = (TextView) findViewById(R.id.preview_text);
        previewText.setVisibility(enablePreview ? View.VISIBLE : View.GONE);

        folderLayout = (LinearLayout) findViewById(R.id.folder_layout);
        folderName = (TextView) findViewById(R.id.folder_name);

        recyclerView = (RecyclerView) findViewById(R.id.folder_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, ScreenUtils.dip2px(this, 2), false));
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        imageAdapter = new ImageListAdapter(this, maxSelectNum, selectMode, showCamera,enablePreview);
        recyclerView.setAdapter(imageAdapter);


    }

    public void registerListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        folderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (folderWindow.isShowing()) {
                    folderWindow.dismiss();
                } else {
                    folderWindow.showAsDropDown(toolbar);
                }
            }
        });
        imageAdapter.setOnImageSelectChangedListener(new ImageListAdapter.OnImageSelectChangedListener() {
            @Override
            public void onChange(List<LocalMedia> selectImages) {
                boolean enable = selectImages.size() != 0;
                doneText.setEnabled(enable ? true : false);
                previewText.setEnabled(enable ? true : false);
                if (enable) {
                    doneText.setText(getString(R.string.done_num, selectImages.size(), maxSelectNum));
                    previewText.setText(getString(R.string.preview_num, selectImages.size()));
                } else {
                    doneText.setText("完成");
                    previewText.setText("预览");
                }
            }

            @Override
            public void onTakePhoto() {
                startCamera();
            }

            @Override
            public void onPictureClick(ImageView imageView, LocalMedia media, int position) {
                if (enablePreview) {
                    startPreview(imageView,imageAdapter.getImages(), position);
                } else if (enableCrop) {
//                    startCrop(media.getPath());
                } else {
                    onSelectDone(media.getPath());
                }
            }

        });
        doneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ImageSelectorActivity.this,FinishActivity.class);
                startActivity(intent);
                finish();

            }
        });
        folderWindow.setOnItemClickListener(new ImageFolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name, List<LocalMedia> images) {
                folderWindow.dismiss();
                imageAdapter.bindImages(images);
                folderName.setText(name);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SHARE) {
                Intent intent = new Intent(ImageSelectorActivity.this, FinishActivity.class);
                startActivity(intent);
                finish();
            }
            // on take photo success
            if (requestCode == POINT_TAKE_PHOTO) {
                bearing = data.getStringExtra("bearing");
                if (enableCrop) {
//                    startCrop(cameraPath);
                } else {
//                    log(cameraPath);
//                    onSelectDone(cameraPath);
                    List<LocalMedia> images = null;
                    images=imageAdapter.getImages();
                    LocalMedia image=new LocalMedia(cameraPath);
                    images.add(image);
                    log(bearing);
                    log(images);

                    bearings.add(bearing);
                    log(images.size());
                    imageAdapter.bindImages(images);
                    imageAdapter.notifyDataSetChanged();

                }
            }
            //on preview select change
            else if (requestCode == ImagePreviewActivity.REQUEST_PREVIEW) {
                boolean isDone = data.getBooleanExtra(ImagePreviewActivity.OUTPUT_ISDONE, false);
                List<LocalMedia> images = (List<LocalMedia>) data.getSerializableExtra(ImagePreviewActivity.OUTPUT_LIST);
                if (isDone) {
                    onSelectDone(images);
                }else{
                    imageAdapter.bindSelectImages(images);
                }
            }
            // on crop success
            else if (requestCode == ImageCropActivity.REQUEST_CROP) {
                String path = data.getStringExtra(ImageCropActivity.OUTPUT_PATH);
                onSelectDone(path);
            }
            // on crop success

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_CAMERA_PATH, cameraPath);
    }

    /**
     * start to camera、preview、crop
     */
    public void startCamera() {
        Intent intent = new Intent(ImageSelectorActivity.this, TakePhotoActivity.class);
        File cameraFile = FileUtils.createCameraFile(this);
        cameraPath = cameraFile.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPath);
        startActivityForResult(intent, POINT_TAKE_PHOTO);
    }

    public void startPreview(ImageView imageView,List<LocalMedia> previewImages, int position) {
        log(imageView.toString());
        new XPopup.Builder(mContext)
                .asImageViewer(imageView, previewImages.get(position).getPath(), new SatelliteActivity.ImageLoader())
                .show();
    }

    public void startCrop(String path) {
        ImageCropActivity.startCrop(this, path);
    }

    /**
     * on select done
     *
     * @param medias
     */
    public void onSelectDone(List<LocalMedia> medias) {
        ArrayList<String> images = new ArrayList<>();
        for (LocalMedia media : medias) {
            images.add(media.getPath());
        }
//        onResult(images);
    }

    public void onSelectDone(String path) {
        ArrayList<String> images = new ArrayList<>();
        images.add(path);
        log(images);
//        onResult(images);
    }

//    public void onResult(ArrayList<String> images) {
//        Intent intent=new Intent();
//        intent.putStringArrayListExtra(REQUEST_OUTPUT, images);
//        intent.putStringArrayListExtra("bearings", bearings);
//        setResult(RESULT_OK, intent);

//        finish();
//    }
}
