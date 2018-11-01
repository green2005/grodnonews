package com.green.grodnonews.mvp;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.green.grodnonews.FeedTypeEnum;
import com.green.grodnonews.R;
import com.green.grodnonews.blogio.AccountSettings;
import com.green.grodnonews.blogio.CommentEditDialog;
import com.green.grodnonews.blogio.RequestListener;
import com.green.grodnonews.blogio.S13Connector;
import com.green.grodnonews.network.S13DataSource;
import com.green.grodnonews.room.NewsDetailItem;
import com.green.grodnonews.ui.PreferencesActivity;

public class NewsDetailPresenter extends ViewModel implements NewsDetailContract.Presenter {
    private NewsDetailRepository mRepository;
    private NewsDetailContract.View mView;


    @Override
    public void onCreateView(@NonNull Context context, NewsDetailContract.View view, String url) {
        mRepository = new NewsDetailRepository(context.getApplicationContext(), view.getFeedType());
        mRepository.addObserver(view.getUrl(), view.getViewLifecycleOwner(), view.getObserver());
        //   mRepository.requestData(this, view.getUrl(), view.getFeedType());
        mView = view;

    }

    @Override
    public void onDestroyView(NewsDetailContract.View view) {
        mRepository.removeObserver(view.getUrl(), view.getViewLifecycleOwner());
        mView = null;
    }

    @Override
    public void onRequestData(@NonNull String url, @NonNull FeedTypeEnum feedType) {
        mRepository.requestData(this, url, feedType);
    }

    @Override
    public void processError(String errorText) {
        if (mView != null) {
            mView.processError(errorText);
        }
    }

    @Override
    public void onAddCommentClick(Context context, FragmentManager fm, NewsDetailItem detail) {
        AccountSettings settings = new AccountSettings(context);
        if (TextUtils.isEmpty(settings.getUserName())) {
            Intent i = new Intent(context, PreferencesActivity.class);
            context.startActivity(i);
            Toast.makeText(context, R.string.msg_need_auth, Toast.LENGTH_LONG).show();
        } else {
            addComment(context, fm, detail);
        }

    }

    private void addComment(final Context context, FragmentManager fm, final NewsDetailItem detailItem) {
        CommentEditDialog dlg = CommentEditDialog.getNewDialog("", new CommentEditDialog.OnDoneEditingListener() {
            @Override
            public void onDoneEditing(String comment) {
                if (!TextUtils.isEmpty(comment)) {
                    sendComment(context, comment, detailItem);
                }
            }
        });
        dlg.show(fm, "enter your dialog");

    }

    private void sendComment(final Context context, final String comment, NewsDetailItem detailItem) {
      S13DataSource source = (S13DataSource)S13DataSource.getNetworkDataSource(context , FeedTypeEnum.S13);
      source.addComment(comment, detailItem.akismet, detailItem.ak_js,  (detailItem.commentId) , new RequestListener() {
          @Override
          public void onRequestDone(S13Connector.QUERY_RESULT result, String errorMessage) {
              mView.commentAdded();
          }
      });
    }

}
