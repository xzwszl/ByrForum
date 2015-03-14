package xzw.szl.byr.assist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import xzw.szl.byr.BaseActivity;
import xzw.szl.byr.R;
import xzw.szl.byr.dialog.SDDirDialog;
import xzw.szl.byr.info.ByrFile;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.utils.ByrBase;
import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.FileUtils;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.ViewUtils;
import xzw.szl.byr.utils.HttpUtils.ProgressListener;
import xzw.szl.byr.utils.ImageUtils;
import xzw.szl.byr.view.FaceSelectView;
import xzw.szl.byr.view.FaceSelectView.OnAttachMentSelectListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PostArticleActivity extends BaseActivity implements View.OnClickListener{
	
	private ImageView mImageViewFace;
	private ImageView mImageViewCamera;
	private ImageView mImageViewPicture;
	private ImageView mImageViewAttach;
	private LinearLayout mLayout;
	private EditText mEditTextTitle;
	private EditText mEditTextContent;
	private FaceSelectView mFaceSelectView;
	
	private List<String> mFilepathList;
	private String mTitle;
	private String mContent;
	private int mAttachmentSize;
	
	private int mReid;
	private int mId;                //update 文章
	private List<ByrFile> byrFileList;
	private String boardName;

	private ProgressDialog dialog;
	private static Handler handler = new Handler();
	
	private final int REQUEST_PICTURE = 0x00;
//	private final int REQUEST_CAMERA = 0x01;
	
	
	private String cameraPath = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.switchTheme(this);
		setContentView(R.layout.activity_postarticle);
		
		
		init();
	}
	
	public void init() {
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mReid = getIntent().getIntExtra("reid", -1);
		boardName = getIntent().getStringExtra("board");
		mTitle = getIntent().getStringExtra("title");
		mContent = getIntent().getStringExtra("content");
		mId = getIntent().getIntExtra("id",-1);
		byrFileList = getIntent().getParcelableArrayListExtra("file");
	//	getOverflowMenu();
		mFaceSelectView  = (FaceSelectView) findViewById(R.id.post_face_layout);
		
		mEditTextTitle = (EditText) findViewById(R.id.post_title);
		mEditTextTitle.setText(mTitle);
		mEditTextContent = (EditText) findViewById(R.id.post_content);
		mEditTextContent.setText(mContent);
		
		mImageViewFace = (ImageView)findViewById(R.id.image_face);
		mImageViewCamera = (ImageView) findViewById(R.id.image_camera);
		mImageViewPicture = (ImageView) findViewById(R.id.image_picture);
		mImageViewAttach = (ImageView) findViewById(R.id.image_attach);
		
		mImageViewFace.setOnClickListener(this);
		mImageViewCamera.setOnClickListener(this);
		mImageViewPicture.setOnClickListener(this);
		mImageViewAttach.setOnClickListener(this);
		
		mLayout = (LinearLayout) findViewById(R.id.pictureLayout);
		
		mFilepathList = new ArrayList<String>();

		mAttachmentSize = 0;
		dialog = new ProgressDialog(this);

		if (byrFileList != null) {
			for (ByrFile file: byrFileList) {
				mFilepathList.add(file.getName());
				addView(file.getName());
			}
		}
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.image_face:
			if (mFaceSelectView.getVisibility() == View.VISIBLE) {
				mFaceSelectView.setVisibility(View.GONE);
			} else {
				mFaceSelectView.setVisibility(View.VISIBLE);
				mFaceSelectView.setOnAttachMentListener(new OnAttachMentSelectListener() {
					
					@Override
					public void selected(String faceTag) {
						int pos = mEditTextContent.getSelectionStart();
						Editable edit = mEditTextContent.getText();
						edit.insert(pos, faceTag);
					}
				});
			}
			break;
		case R.id.image_camera:
			takePicture();
			break;
		case R.id.image_picture:
			acquirePicture();
			break;
		case R.id.image_attach:
			SDDirDialog  sDDialog = new SDDirDialog(PostArticleActivity.this, new OnAttachMentSelectListener() {
				
				@Override
				public void selected(String filepath) {
					addFile(filepath);
				}
			});
			sDDialog.show();
			break;
		}
	}
	
	private void takePicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraPath = System.currentTimeMillis() + ".jpg";
		intent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtils.createCameraURI(cameraPath));
		startActivityForResult(intent,1);
	}
	public void dispalyMessage(String msg) {
		Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
	}
	
	private void addView(final String path) {
		mLayout.setVisibility(View.VISIBLE);
		final ImageView iv = new ImageView(this);
		Bitmap bitmap = ImageUtils.compressImage(path,DataUtils.getDisplayValue(50),DataUtils.getDisplayValue(50));
		//bitmap = ImageUtils.compressImage(bitmap, 100, 0);
		if (bitmap == null) 
			iv.setImageResource(R.drawable.ordfile);
		else iv.setImageBitmap(bitmap);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		iv.setMinimumHeight(DataUtils.getDisplayValue(50));
		iv.setMaxHeight(DataUtils.getDisplayValue(50));
		iv.setMaxWidth(DataUtils.getDisplayValue(50));
		
		iv.setPadding(0, 0, DataUtils.getDisplayValue(5), 0);
		iv.setLayoutParams(params);
		iv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int pos = mEditTextContent.getSelectionStart();
				Editable edit = mEditTextContent.getText();
				edit.insert(pos, "\n[upload=" + (mFilepathList.indexOf(path)+1)+ "][/upload]");
				dispalyMessage(path);
			}
		});
		
		iv.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				mLayout.removeView(iv);
				mFilepathList.remove(path);
				if (mFilepathList.size() == 0) mLayout.setVisibility(View.GONE);
				return true;
			}
		});
		mLayout.addView(iv);
	}
	
	private void acquirePicture() {
		Intent intent = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent,REQUEST_PICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_PICTURE) {
			if (resultCode == RESULT_OK) {
				
				String path = getRealPathFromURI(this,data.getData());

				addFile(path);
			}
		} else {
			addFile(FileUtils.getCameraFullPath(cameraPath));
		}
		
	}
	
	private String getRealPathFromURI(Context context, Uri contentUri) {
	    Cursor cursor = null;
	    try { 
	        String[] proj = { android.provider.MediaStore.Images.Media.DATA };
	        cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();
	        String path = cursor.getString(column_index);

	        return path;
	    } finally {
	        if (cursor != null) {
	            cursor.close();
	        }
	    }
	}
	
	private void addFile(String path) {
		
		if (mFilepathList.contains(path)) {
			dispalyMessage("列表中已包含同名文件");
		}else if (mFilepathList.size() ==  ByrBase.ATTACHMENT_COUNT) {
			dispalyMessage("附件数量已达上限 " + ByrBase.ATTACHMENT_COUNT);
		} else {
			File file = new File(path);
			int len = (int) file.length();
			if (len > ByrBase.ATTACHMENT_SIZE ||len + mAttachmentSize > ByrBase.ATTACHMENT_SIZE) {
				return;
			} else {
				mFilepathList.add(path);
				addView(path);
				mAttachmentSize+=len;
			}
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.post_article, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.post_article) {
			
			if (mEditTextTitle.getText().toString().equals("")) {
				dispalyMessage("文章标题不能为空~~");
				return true;
			} 
			
			dialog.show();
			dialog.setMessage("文章发表已完成: 0 / " + (mFilepathList.size() +1));
			Runnable r = new Runnable() {
					
					@Override
					public void run() {
						HttpUtils.postArticle(boardName,mEditTextTitle.getText().toString(),
								mEditTextContent.getText().toString(),mReid, mFilepathList,mId,
								new ProgressListener(){
							
									@Override
									public void updateProgress(final int current, final int total) {
										handler.post(new Runnable() {
											
											@Override
											public void run() {
												dialog.setMessage("文章发表已完成: " + current + " / " + total);
											}
										});
								}},new HttpUtils.HttpRequestListener() {
									
									@Override
									public void onSuccess(String content) {
										handler.postDelayed(new Runnable() {
											
											@Override
											public void run() {
												dialog.dismiss();
												ViewUtils.displayMessage(PostArticleActivity.this,"发表成功");
											}
										},500);
									}
									
									@Override
									public void onFailed(String reason) {
										handler.postDelayed(new Runnable() {
											
											@Override
											public void run() {
												dialog.setMessage("reason");
											}
										},500);
									}
									
									@Override
									public void onError(Throwable e) {
										handler.post(new Runnable() {
											
											@Override
											public void run() {
												ViewUtils.displayMessage(PostArticleActivity.this, getResources().getString(R.string.network_error));
											}
										});
									}
								});
					}
			}; 
			ByrThreadPool.getTHreadPool().execute(r);
			
		} else if (item.getItemId() == android.R.id.home) {
			
			this.finish();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
//	private void getOverflowMenu() {  
//        try {  
//           ViewConfiguration config = ViewConfiguration.get(this);  
//           Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");  
//           if(menuKeyField != null) {  
//               menuKeyField.setAccessible(true);  
//               menuKeyField.setBoolean(config, false);  
//           }  
//       } catch (Exception e) {  
//           e.printStackTrace();  
//       }  
//   }

	@Override
	protected void onDestroy() {
		
		dialog.dismiss();
		super.onDestroy();
	}
	
	
}
