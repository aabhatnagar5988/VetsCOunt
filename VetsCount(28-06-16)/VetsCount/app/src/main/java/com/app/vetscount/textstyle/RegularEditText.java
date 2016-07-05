package com.app.vetscount.textstyle;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class RegularEditText extends EditText {

	public RegularEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RegularEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RegularEditText(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/AvenirLTStd-Roman.otf");
			setTypeface(tf);
		}
	}

}