package com.example.lsoco_user.app.afpromotion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.lsoco_user.app.afpromotion.util.Constants;
import com.example.lsoco_user.app.afpromotion.R;

/**
 * Simple fragment holding a web-view
 */
public class WebViewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WebView webView =  (WebView) inflater.inflate(R.layout.fragment_webview, container, false);
        String url = getArguments().getString(Constants.KEY_URL);
        webView.setWebViewClient(new WebViewController());
        webView.loadUrl(url);
        return webView;
    }

    /**
     * Simple custom WebViewClient
     */
    public class WebViewController extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}