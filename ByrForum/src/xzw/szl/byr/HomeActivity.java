package xzw.szl.byr;

import java.lang.reflect.Field;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import xzw.szl.byr.db.DBListTableHandler;
import xzw.szl.byr.fragment.HomeFragment;
import xzw.szl.byr.fragment.OptionsFragment;
import xzw.szl.byr.fragment.SectionFragment;
import xzw.szl.byr.info.MyFavorite;
import xzw.szl.byr.info.User;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.mananger.PrefernceManager;
import xzw.szl.byr.service.BroadcastService;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.JsonUtils;
import xzw.szl.byr.utils.ViewUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewConfiguration;


public class HomeActivity extends BaseActivity {
	
	private ActionBar mActionBar;
	private ViewPager mViewPager;
	private Fragment mHomeFragment;
	private Fragment mSectionFragment;
	private Fragment mOptionsFragment;
	private HomeHandler handler = new HomeHandler();
	
	private MyFavorite mFavorite;
	private FavorListener mFavorListener1;
	private FavorListener mFavorListener2;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.switchTheme(this);
        setContentView(R.layout.activity_home);
        HomeActivity homeActivity = (HomeActivity) getLastCustomNonConfigurationInstance();
        if (homeActivity == null) {
        	init();
        } else {
        	init(homeActivity);
        }
        
        if (PrefernceManager.getInstance().canMessageRemind)
        	startService(new Intent(this,BroadcastService.class));
    }
    
    public Fragment getFragment(int k) {
    	switch (k) {
		case 0:
			return mOptionsFragment;
		case 1:
			return mHomeFragment;
		case 2:
		default:
			return mSectionFragment;
		}
    }
    
    public void setHomeFragment(HomeFragment fragment) {
    	mHomeFragment = fragment;
    }
    public void setSectionFragment(SectionFragment fragment) {
    	mSectionFragment = fragment;
    }
    public void setOptionsFragment(OptionsFragment fragment) {
    	mOptionsFragment = fragment;
    }
    
    private void init(HomeActivity activity) {
/*    	mHomeFragment = activity.mHomeFragment;
    	//((HomeFragment) mHomeFragment).setData();
    	mSectionFragment =activity.mSectionFragment;
    	mOptionsFragment = activity.mOptionsFragment;*/
    	handler = activity.handler;
    	mFavorite = activity.mFavorite;
    	mFavorListener1 = activity.mFavorListener1;
    	mFavorListener2 = activity.mFavorListener2;
    	initViewPager();
    	initActionBar();
    } 
    
    private void init() {
    	mFavorListener1 = null;
    	mFavorListener2 = null;
    	initViewPager();
    	initActionBar();
//    	String json = "{\"section_count\":9,\"section\":[{\"name\":\"0\",\"description\":\"本站站务\",\"is_root\":true,\"parent\":null}]}";
//    	ByrSection bs = (ByrSection)JsonUtils.toBean(json, ByrSection.class);
//    	System.out.println(bs.getSection().get(0).getDescription());
    }
        
    private void initViewPager() {
    	mViewPager = (ViewPager)findViewById(R.id.viewpager);
    	
      	mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public int getCount() {
				return 3;
			}
			
			@Override
			public Fragment getItem(int position) {
				switch (position) {
				case 1:
					if (mHomeFragment == null) {
						mHomeFragment = new HomeFragment();
					}
					return mHomeFragment;
				case 2:
					if (mSectionFragment == null) {
						mSectionFragment = new SectionFragment();
					}
					return mSectionFragment;
				case 0:
					if (mOptionsFragment == null) {
						mOptionsFragment = new OptionsFragment();
					}
					return mOptionsFragment;
				}
				return mHomeFragment;
			}
		});
    	mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){

			@Override
			public void onPageSelected(int position) {
				mActionBar.setSelectedNavigationItem(position);
			}
    		
    	});
    	
    	mViewPager.setOffscreenPageLimit(2);
    }
    

    private void getOverflowMenu() {
        try {
           ViewConfiguration config = ViewConfiguration.get(this);
           Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
           if(menuKeyField != null) {
               menuKeyField.setAccessible(true);
               menuKeyField.setBoolean(config, false);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
	}
    
    private void initActionBar () {
    	getOverflowMenu();
    	mActionBar = getSupportActionBar();
    	mActionBar.setDisplayHomeAsUpEnabled(false);
    	mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    	
    	ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabSelected(Tab tab,
					android.support.v4.app.FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab tab,
					android.support.v4.app.FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabReselected(Tab tab,
					android.support.v4.app.FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

		};
		
		mActionBar.addTab(
				mActionBar.newTab()
					.setText("常用")
					.setTabListener(tabListener));
		mActionBar.addTab(
				mActionBar.newTab()
					.setText("十大")
					.setTabListener(tabListener));
		mActionBar.addTab(
				mActionBar.newTab()
					.setText("分区")
					.setTabListener(tabListener));
		
		mActionBar.setSelectedNavigationItem(1);
		
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.home, menu);  
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
		case R.id.logout:
			logout();
			break;

		default:
			break;
		}
       
        return super.onOptionsItemSelected(item);
    }

    //保留数据
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return this;
	}

	private void logout() {
		ByrThreadPool.getTHreadPool().execute(new Runnable() {
			
			@Override
			public void run() {
				
			
				
				HttpUtils.httpRequest("/user/logout.json",new HttpUtils.HttpRequestListener() {
					
					@Override
					public void onSuccess(String content) {
						User user = (User) JsonUtils.toBean(content, User.class);
						//成功
						if (user.getId() != null ) {
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									PrefernceManager.getInstance().updateCurrentUser(getApplicationContext(), null, null);
									HomeActivity.this.finish();
								}
							});
						} 
						
					}
					
					@Override
					public void onFailed(String reason) {
						ViewUtils.displayMessage(HomeActivity.this,reason);
					}

					@Override
					public void onError(Throwable e) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
	}
	
	private static class HomeHandler extends Handler{
		
	}
	
    public void select(int id) {
    	mActionBar.setSelectedNavigationItem(id);
    }
    
    public void setFavorListener1(FavorListener listener) {
    	this.mFavorListener1 = listener;
    }
    
    public void setFavorListener2(FavorListener listener) {
    	this.mFavorListener2 = listener;
    }
    
	public void updateFavor(MyFavorite favorite) {
		mFavorite = favorite;
	}
	
	public MyFavorite getFavorite() {
		return mFavorite;
	}
	
	public void getMyFavor(int level) {
		ByrThreadPool.getTHreadPool().execute(new FvaorRunnable(level));
	}
	
	public void dealMyFavor(String type, int level, String dir, String name) {
		ByrThreadPool.getTHreadPool().execute(new DealFavor(type, level, dir, name));
	}
	
	private synchronized void getFavor(int level) {
		
		if (mFavorite != null) {
			
			if (mFavorListener1 != null) {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						//if (mFavorListener != null) {
							mFavorListener1.onUpdate(mFavorite);
						//}
					}
				});
			}
			if (mFavorListener2 != null) {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						//if (mFavorListener != null) {
							mFavorListener2.onUpdate(mFavorite);
						//}
					}
				});
			}
		}
		
		String content = DBListTableHandler.getInstance().queryItemContent(DBListTableHandler.TYPE_FAVORITE, 1);
		
		if (content != null) {
			MyFavorite f = (MyFavorite) JsonUtils.toBean(content, MyFavorite.class);
			
			if (f != null) {
				updateFavor(f);
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						if (mFavorListener1 != null) {
							mFavorListener1.onUpdate(mFavorite);
						}
						if (mFavorListener2 != null) {
							mFavorListener2.onUpdate(mFavorite);
						}
					}
				});
			}
		} else {
		
		HttpUtils.httpRequest("/favorite/"+ level + ".json",new HttpUtils.HttpRequestListener() {
			
			@Override
			public void onSuccess(String content) {
				MyFavorite f = (MyFavorite) JsonUtils.toBean(content, MyFavorite.class);
				
				if (f != null) {
					updateFavor(f);
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							if (mFavorListener1 != null) {
								mFavorListener1.onUpdate(mFavorite);
							}
							if (mFavorListener2 != null) {
								mFavorListener2.onUpdate(mFavorite);
							}
						}
					});
					
					DBListTableHandler.getInstance().insertListTable(DBListTableHandler.TYPE_FAVORITE,1,content);
				}
			}
			
			@Override
			public void onFailed(final String reason) {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						ViewUtils.displayMessage(HomeActivity.this,reason);
					}
				});
			}
			
			@Override
			public void onError(Throwable e) {
				
			}
		});
	  }
	}
	
	private synchronized void  dealFavor(String type, int level, String dir,String name) {
		
		HttpUtils.dealFavorite(type, level, name, dir, new HttpUtils.HttpRequestListener() {
			
			@Override
			public void onSuccess(String content) {
				MyFavorite f = (MyFavorite) JsonUtils.toBean(content, MyFavorite.class);
				
				if (f != null) {
					updateFavor(f);
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							if (mFavorListener2 != null) {
								mFavorListener2.onUpdate(mFavorite);
							}
							if (mFavorListener1 != null) {
								mFavorListener1.onUpdate(mFavorite);
							}
						}
					});
				}
			}
			
			@Override
			public void onFailed(final String reason) {
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						ViewUtils.displayMessage(HomeActivity.this,reason);
					}
				});
			}
			
			@Override
			public void onError(Throwable e) {

			}
		});
	}
	
	class DealFavor  implements Runnable {

		private String type;
		private int level;
		private String dir;
		private String name;
		
		
		public DealFavor(String type, int level, String dir, String name) {
			super();
			this.type = type;
			this.level = level;
			this.dir = dir;
			this.name = name;
		}

		@Override
		public void run() {
			dealFavor(type, level, dir, name);
		}
	}
	
	 class FvaorRunnable implements Runnable {
		 
		 private int level;
		 
		public FvaorRunnable(int level) {
			this.level = level;
		}
			@Override
			public void run() {
				getFavor(level);
			}
		};
		
	
	public interface FavorListener {
		void onUpdate(MyFavorite favor);
		void onNofity(String type,String info);
	}
	
	private BroadcastReceiver br = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String type = intent.getStringExtra("type");
			String info = intent.getStringExtra("info");
			mFavorListener1.onNofity(type, info);
		}
	};


	@Override
	protected void onPause() {
		super.onPause();
		
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unregisterReceiver(br);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		registerReceiver(br, new IntentFilter(BroadcastService.BR_INFO));
		super.onStart();
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

 }
