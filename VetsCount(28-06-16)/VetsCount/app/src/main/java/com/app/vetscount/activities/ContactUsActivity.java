package com.app.vetscount.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;

import com.app.vetscount.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContactUsActivity extends AppCompatActivity {

    @Bind(R.id.nameTxtView)
    EditText nameTxtView;
    @Bind(R.id.emailTxtView)
    EditText emailTxtView;
    @Bind(R.id.phoneTxtView)
    EditText phoneTxtView;
    @Bind(R.id.feedbackTxtView)
    EditText feedbackTxtView;
    @Bind(R.id.postRV)
    RippleView postRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_action_bar)));

        getSupportActionBar().setTitle("Contact Us");
        postRV.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                sendEmail();
            }
        });
    }

    private void sendEmail() {


        if (isInputAvailable()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "info@wallmart.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Wallmart Support");
            intent.putExtra(Intent.EXTRA_TEXT, feedbackTxtView.getText().toString() + "\n\n" + nameTxtView.getText().toString());
            startActivity(intent);

        }
    }

    private boolean isInputAvailable() {

        if (TextUtils.isEmpty(nameTxtView.getText().toString())) {
            nameTxtView.setError("Name cannot be empty");
            return false;

        }
        if (TextUtils.isEmpty(feedbackTxtView.getText().toString())) {
            feedbackTxtView.setError("Feedback cannot be empty");
            return false;

        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
