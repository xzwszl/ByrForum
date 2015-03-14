package xzw.szl.byr.assist;


import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import xzw.szl.byr.BaseActivity;
import xzw.szl.byr.R;

public class BaseDetailActivity extends BaseActivity{
	
	protected MenuItem refreshItem;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.detail, menu);
		refreshItem = menu.getItem(0);showRefreshAnimation();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	public void showRefreshAnimation() {
		
		clearRefreshAnimation();        //清除动画
//		ImageView imageview = new ImageView(this);
//		imageview.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
//				LayoutParams.WRAP_CONTENT));
//		imageview.setImageResource(R.drawable.ic_action_refresh);
//		
//		item.setActionView(imageview);
		
		ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.board_action_view,null);
		
		
		refreshItem.setActionView(imageView);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.refresh);
		animation.setRepeatMode(Animation.RESTART);
		animation.setRepeatCount(Animation.INFINITE);
		imageView.startAnimation(animation);
		
	}

	public void clearRefreshAnimation() {
		if (refreshItem != null) {
			View view = refreshItem.getActionView();
			if (view != null) {
				view.clearAnimation();
				refreshItem.setActionView(null);
			}
		}
}
}
