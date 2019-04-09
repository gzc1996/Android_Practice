package com.gzc.jigsaw;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	//打乱步数
	int step;
    //当前移动动画是否是正在执行
	private boolean isAnimRun = false;
	//判断游戏是否开始
	private boolean isGameStart = false;
	/** 利用二位数组创建若干个游戏小方块  **/
	private ImageView[][] iv_game_arr = new ImageView[3][5];
	
	/* 游戏主界面*/
	private GridLayout gl_main_game;
	//当前空方块的实例的保存
	private ImageView iv_null_ImageView;
	//当前手势
	private GestureDetector mDetector;
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		
		return mDetector.onTouchEvent(event);
	}
	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		mDetector.onTouchEvent(ev);
		return super.dispatchGenericMotionEvent(ev);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//新页面接收数据
		Bundle bundle=this.getIntent().getExtras();
		//接收step_length值
		step=bundle.getInt("step_length");
		Toast.makeText(MainActivity.this, "成功打乱："+step+"步", Toast.LENGTH_SHORT).show();
		mDetector = new GestureDetector(this,new OnGestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
					float arg3) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
					float arg3) {
				// TODO Auto-generated method stub
				int type = getDirByGes(arg0.getX(), arg0.getY(), arg1.getX(), arg1.getY());
//				Toast.makeText(MainActivity.this,""+type , Toast.LENGTH_SHORT).show();
				changeByDir(type);
				return false;
			}
			
			@Override
			public boolean onDown(MotionEvent arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		setContentView(R.layout.activity_main);
		/*初始化游戏的若干个小方块*/
		Bitmap bigBm =((BitmapDrawable)getResources().getDrawable(R.drawable.ic_game_tu)).getBitmap();//获取一张大图
	    int tuWandH = bigBm.getWidth()/5;//每个游戏小方块的宽和高
		int ivWandH = getWindowManager().getDefaultDisplay().getWidth()/5;//小方块的宽高应该是整个屏幕的宽/5
	    for (int i = 0; i < iv_game_arr.length; i++) {
			for (int j = 0; j < iv_game_arr[0].length; j++) {
				Bitmap bm = Bitmap.createBitmap(bigBm,j*tuWandH,i*tuWandH,tuWandH,tuWandH);//根据行和列来切成若干个游戏小图片
				iv_game_arr[i][j] = new ImageView(this);
				iv_game_arr[i][j].setImageBitmap(bm);//设置每一个游戏小方块的图案
				iv_game_arr[i][j].setLayoutParams(new RelativeLayout.LayoutParams(ivWandH, ivWandH));
				iv_game_arr[i][j].setPadding(2, 2, 2, 2);//设置方块之间的间距
				iv_game_arr[i][j].setTag(new GameData(i, j, bm));//绑定自定义的数据
				iv_game_arr[i][j].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						boolean flag = isHasByNullImageView((ImageView)v);
//						Toast.makeText(MainActivity.this, "位置关系是否存在:"+flag, Toast.LENGTH_SHORT).show();
						if(flag){
							changeDataByImageView((ImageView)v);
						}
					}
				});
			}
		}
	    /* 初始化游戏主界面，并添加若干个小方块*/
		gl_main_game = (GridLayout) findViewById(R.id.gl_main_game);
		for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
            	gl_main_game.addView(iv_game_arr[i][j]);
			}
		}
		/* 设置最后一个方块为空的*/
		setNullImageView(iv_game_arr[2][4]);
		//初始化随机打乱顺序方块
		randomMove();
		isGameStart = true;//开始状态
	}
	
	/**
	 * 根据手势的方向，获取空方块相应的相邻位置如果存在方块，那么进行数据交换
	 * @param type 
	 * 1:上，2:下，3：左，4：右
	 */
	public void changeByDir(int type){
		changeByDir(type,true);
		
	}
	
	/**
	 * 根据手势的方向，获取空方块相应的相邻位置如果存在方块，那么进行数据交换
	 * @param type 
	 * 1:上，2:下，3：左，4：右
	 * @param isAnim true:有动画，false：没有动画
	 */
	public void changeByDir(int type,boolean isAnim){
		//获取当前空方块的位置
		GameData mnullGameData = (GameData)iv_null_ImageView.getTag();
		//根据方向，设置相应的相邻位置的坐标
		int new_x = mnullGameData.x;
		int new_y = mnullGameData.y;
		if(type == 1){//要移动的方块在当前空方块的下边
			new_x++;
		}else if(type==2){
			new_x--;
		}else if(type==3){
			new_y++;
		}else if(type==4){
			new_y--;
		}
		//判断这个新坐标，是否存在
		if(new_x>=0&&new_x<iv_game_arr.length&&new_y>=0&&new_y<iv_game_arr[0].length){
			//存在的话，开始移动
			if(isAnim){
				changeDataByImageView(iv_game_arr[new_x][new_y]);
			}else{
				changeDataByImageView(iv_game_arr[new_x][new_y],isAnim);
			}
		}else{
			//什么也不做
		}		
	}
	//判断游戏结束的方法
	public void isGameOver(){
		boolean isGameOver = true;
		//要遍历每个游戏小方块
		for (int i = 0; i < iv_game_arr.length; i++) {
			for (int j = 0; j < iv_game_arr[0].length; j++) {
				//为空的方块数据不判断跳转
				if(iv_game_arr[i][j]==iv_null_ImageView){
					continue;
				}
				GameData mgGameData = (GameData) iv_game_arr[i][j].getTag();
				if(!mgGameData.isTrue()){
					isGameOver = false;
					break;
				}
			}
		}
		
		
		//根据一个开关变量决定游戏是否结束，结束时给提示
		if(isGameOver){
			Toast.makeText(this, "拼图复原成功！", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 手势判断，是向左滑，还是向右滑
	 * @param Start_x 手势的起始点x
	 * @param start_y 手势的起始点y
	 * @param end_x 手势的终止点x
	 * @param end_y 手势的终止点y
	 * @return 1:上，2:下，3：左，4：右
	 */
	public int getDirByGes(float start_x,float start_y,float end_x,float end_y){
		boolean isLeftOrRight = (Math.abs(start_x-end_x)>Math.abs(start_y-end_y))?true:false;//是否是左右
		if(isLeftOrRight){//左右
			boolean isLeft = start_x-end_x>0?true:false;//
			if(isLeft){
				return 3;
			}else{
				return 4;
			}
		}else{//上下
			boolean isUp = start_y-end_y>0?true:false;//
			if(isUp){
				return 1;
			}else{
				return 2;
			}
		}
	}
	
	//随机打乱顺序
	public void randomMove(){
		//打乱的次数
		for (int i = 0; i < step; i++) {
			//根据手势开始交换，无动画
			int type = (int) (Math.random()*4)+1;
			changeByDir(type,false);
		}
		
		
	}
	
	/**
	 * 动画结束之后，交换两个方块的数据
	 * @param mImageView 
	 * 点击的方块
	 */
	public void changeDataByImageView(final ImageView mImageView){
		changeDataByImageView(mImageView, true);
	}
	
	/**
	 * 动画结束之后，交换两个方块的数据
	 * @param mImageView 
	 * 点击的方块
	 * @param isAnim true:有动画，false：没有动画
	 */
	public void changeDataByImageView(final ImageView mImageView,boolean isAnim){
		if(isAnimRun){
			return;
		}
		if(!isAnim){
			GameData mGameData = (GameData) mImageView.getTag();
			iv_null_ImageView.setImageBitmap(mGameData.bm);
			GameData mNullGameData = (GameData) iv_null_ImageView.getTag();
			mNullGameData.bm = mGameData.bm;
			mNullGameData.p_x = mGameData.p_x;
			mNullGameData.p_y = mGameData.p_y;
			setNullImageView(mImageView);//设置当前点击的是空方块
			if(isGameStart){
				isGameOver();//成功时，会弹一个toast
			}
			return ;
		}
		
		//创建一个动画，设置好方向，移动的距离
		TranslateAnimation translateAnimation = null;
		if(mImageView.getX()>iv_null_ImageView.getX()){//当前点击的方块在空方块的下边
			//往上移动
			translateAnimation = new TranslateAnimation(0.1f,-mImageView.getWidth(),0.1f,0.1f);
		}else if (mImageView.getX()<iv_null_ImageView.getX()) {
			//往下移动
			translateAnimation = new TranslateAnimation(0.1f,+mImageView.getWidth(),0.1f,0.1f);
		}else if (mImageView.getY()>iv_null_ImageView.getY()) {
			//往左移动
			translateAnimation = new TranslateAnimation(0.1f,0.1f,0.1f,-mImageView.getWidth());
		}else if (mImageView.getY()<iv_null_ImageView.getY()) {
			//往右移动
			translateAnimation = new TranslateAnimation(0.1f,0.1f,0.1f,+mImageView.getWidth());
		}
		
		//设置动画的时长
		translateAnimation.setDuration(70);
		
		//设置动画结束之后是否停留
		translateAnimation.setFillAfter(true);
		
		//设置动画结束之后要真正的把数据交换了
		translateAnimation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				isAnimRun = true;
			}			

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				isAnimRun = false;
				mImageView.clearAnimation();
				GameData mGameData = (GameData) mImageView.getTag();
				iv_null_ImageView.setImageBitmap(mGameData.bm);
				GameData mNullGameData = (GameData) iv_null_ImageView.getTag();
				mNullGameData.bm = mGameData.bm;
				mNullGameData.p_x = mGameData.p_x;
				mNullGameData.p_y = mGameData.p_y;
				setNullImageView(mImageView);//设置当前点击的是空方块
				if(isGameStart){
					isGameOver();//成功时，会弹一个toast
				}
			}
			
		});
		
		//执行动画
		mImageView.startAnimation(translateAnimation);
	}
	
	/**
	 * 设置某个方块为空方块
	 * @param mImageView 
	 * 当前要设置为空的方块的实例
	 */
	public void setNullImageView(ImageView mImageView){
		mImageView.setImageBitmap(null);
		iv_null_ImageView = mImageView;
	}

	/**
	 * 判断当前点击方块，是否与空方块的位置关系是相邻关系
	 * @param mImageView 所点击的方块
	 * @return true：相邻，false：不相邻
	 */
	public boolean isHasByNullImageView(ImageView mImageView){
		//分别获取当前空方块的位置与点击方块的位置，通过x y两边差1的方式判断
		GameData mNullmGameData = (GameData) iv_null_ImageView.getTag();
		GameData mGameData = (GameData) mImageView.getTag();
		
		if(mNullmGameData.y==mGameData.y&&mGameData.x+1==mNullmGameData.x){//当前点击的方块在空方块的上边
			return true;
		}
	    else if(mNullmGameData.y==mGameData.y&&mGameData.x-1==mNullmGameData.x){//当前点击的方块在空方块的下边
	    	return true;
	    }
        else if(mNullmGameData.y==mGameData.y+1&&mGameData.x==mNullmGameData.x){//当前点击的方块在空方块的左边
        	return true;
        }
	    else if(mNullmGameData.y==mGameData.y-1&&mGameData.x==mNullmGameData.x){//当前点击的方块在空方块的右边
	    	return true;
	    }
	    return false;
	}
	
	//每个游戏小方块上要绑定的数据
	class GameData{
		//每个小方块的实际位置x
		public int x=0;
		
		//每个小方块的实际位置y
		public int y=0;
		
		//每个小方块的图片
		public Bitmap bm;
		
		//每个小方块的图片位置
		public int p_x = 0;
		
		//每个小方块的图片位置
		public int p_y = 0;

		public GameData(int x, int y, Bitmap bm) {
			super();
			this.x = x;
			this.y = y;
			this.bm = bm;
			this.p_x = x;
			this.p_y = y;
		}
		
        /**
         * 每个小方块的位置，是否是正确
         * @return true：正确，false：不确定
         */
		public boolean isTrue() {
			if(x==p_x&&y==p_y){
				return true;
			}
			return false;
		}
		
	}
}
