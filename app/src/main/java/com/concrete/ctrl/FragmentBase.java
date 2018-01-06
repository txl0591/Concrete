package com.concrete.ctrl;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;

import com.concrete.app.R;
import com.concrete.common.IntentDef;
import com.concrete.common.nlog;

@SuppressLint("ValidFragment")
public class FragmentBase extends PreferenceFragment {
	
	public Context mContext = null;
	public IntentDef.OnFragmentListener mOnFragmentListener = null;

	public FragmentBase(Context context, int SelfId){
		mContext = context;
	}

	public void setOnFragmentListener(IntentDef.OnFragmentListener Listener){
		mOnFragmentListener = Listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}


}
