package org.musicbrainz.android.ui;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.musicbrainz.android.functions.Consumer;

public class CustomWebViewClient extends WebViewClient {

    private ProgressBar progressBar;
    private Consumer<WebResourceError> errorConsumer;

    public CustomWebViewClient() {
        super();
    }

    public CustomWebViewClient(ProgressBar progressBar, Consumer<WebResourceError> errorConsumer) {
        super();
        this.progressBar = progressBar;
        this.errorConsumer = errorConsumer;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        view.loadUrl(request.getUrl().toString());
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (errorConsumer != null) {
            errorConsumer.accept(error);
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setErrorConsumer(Consumer<WebResourceError> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }
}
