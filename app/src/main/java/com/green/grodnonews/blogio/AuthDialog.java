package com.green.grodnonews.blogio;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.green.grodnonews.R;

public class AuthDialog extends Dialog {
    private EditText mPwd;
    private EditText mUserName;
    private RequestListener mListener;

    public static void editAuth(Context context, AccountSettings accountSettings, RequestListener requestListener) {
        AuthDialog dialog = new AuthDialog(context, accountSettings, requestListener);
        dialog.show();
    }

    AuthDialog(Context context, AccountSettings settings, RequestListener listener) {
        super(context);
        mListener = listener;
        setTitle(context.getString(R.string.auth_dialog_title));
        setContentView(R.layout.dialog_authenticate);
        mPwd = findViewById(R.id.pwdEdit);
        mUserName = findViewById(R.id.userNameEdit);
        mPwd.setText(settings.getPwd());
        mUserName.setText(settings.getUserName());
        Button btnOk = findViewById(R.id.okbtn);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToLogin();
            }
        });

        mPwd.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tryToLogin();
                    return true;
                }
                return false;
            }
        });
    }

    private void tryToLogin() {
        S13Connector blogConnector = S13Connector.getBlogConnector();
        final Context context = getContext();

        final ProgressDialog pg = new ProgressDialog(context);
        pg.setMessage(context.getString(R.string.please_wait));
        pg.show();
        blogConnector.loginAsync(mUserName.getText().toString(), mPwd.getText().toString(),
                new RequestListener() {
                    @Override
                    public void onRequestDone(S13Connector.QUERY_RESULT result, String errorMessage) {
                        if (pg.isShowing()) {
                            pg.dismiss();
                        }
                        Context context = getContext();
                        switch (result) {
                            case OK: {
                                Toast.makeText(context, context.getString(R.string.auth_completed_ok), Toast.LENGTH_SHORT).show();
                                AccountSettings settings = new AccountSettings(null);
                                settings.saveSettings(mUserName.getText().toString(), mPwd.getText().toString(), context);

                                //setAuthSettings(mUserName.getText().toString(), mPwd.getText().toString(), context);
                                dismiss();
                                if (mListener != null) {
                                    mListener.onRequestDone(S13Connector.QUERY_RESULT.OK, null);
                                }
                                break;
                            }
                            case ACCESS_DENIED: {
                                Toast.makeText(context, context.getString(R.string.auth_error), Toast.LENGTH_SHORT).show();
                                if (mListener != null) {
                                    mListener.onRequestDone(S13Connector.QUERY_RESULT.ACCESS_DENIED, context.getString(R.string.auth_error));
                                }
                                break;
                            }
                            case ERROR: {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                                if (mListener != null) {
                                    mListener.onRequestDone(S13Connector.QUERY_RESULT.ERROR, errorMessage);
                                }
                                break;
                            }
                        }
                    }
                });

    }
}
