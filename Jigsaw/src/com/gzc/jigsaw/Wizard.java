package com.gzc.jigsaw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;


public class Wizard extends Activity {
	int step=10;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wizard);
		Button btnstart=(Button)findViewById(R.id.btnStart);
		RadioGroup group=(RadioGroup)findViewById(R.id.radioGroup);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				int radiobuttonid=arg0.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton)findViewById(radiobuttonid);
				//Toast.makeText(Wizard.this, "�Ѷ�"+rb.getText(), Toast.LENGTH_SHORT).show();
				if(rb.getText().equals("Easy Level"))
				{
					step=10;
				}else if(rb.getText().equals("Middle Level"))
				{
					step=50;
				}else if(rb.getText().equals("Hard Level"))
				{
					step=100;
				}
				Toast.makeText(Wizard.this, "������Ҳ�����"+step, Toast.LENGTH_SHORT).show();
			}
			
		});
		btnstart.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(Wizard.this,MainActivity.class);
				//��BundleЯ������
				Bundle bundle=new Bundle();
				//����step_length����Ϊstep����ֵ
				bundle.putInt("step_length", step);
				intent.putExtras(bundle);
				Wizard.this.startActivity(intent);
			}
			
		});
	}
	
	
}
