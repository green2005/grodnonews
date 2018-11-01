package com.green.grodnonews.blogio;

public interface RequestListener {
      void onRequestDone(S13Connector.QUERY_RESULT result, String errorMessage);
}
