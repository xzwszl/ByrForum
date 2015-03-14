package xzw.szl.byr;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

public class SearchActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		handleIntent(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		handleIntent(getIntent());
		super.onNewIntent(intent);
	}


	public void handleIntent(Intent intent) {
		
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Toast.makeText(this,query, 0).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
			
//			case android.R.id.home:
//				Intent upIntent = NavUtils.getParentActivityIntent(this);
//				if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//					TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
//				} else {
//					upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					NavUtils.navigateUpTo(this, upIntent);
//				}
//				return true;
			
		default:break;
		}

		return super.onOptionsItemSelected(item);
	}
}
