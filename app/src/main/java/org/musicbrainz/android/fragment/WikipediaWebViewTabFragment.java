package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.os.ConfigurationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Url;
import org.musicbrainz.android.communicator.GetUrlsCommunicator;
import org.musicbrainz.android.communicator.SetWebViewCommunicator;
import org.musicbrainz.android.ui.CustomWebViewClient;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;


public class WikipediaWebViewTabFragment extends Fragment {

    private WebView webView;
    private ProgressBar loading;
    private View error;

    public static WikipediaWebViewTabFragment newInstance() {
        Bundle args = new Bundle();
        WikipediaWebViewTabFragment fragment = new WikipediaWebViewTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_web_view_tab, container, false);

        loading = layout.findViewById(R.id.loading);
        error = layout.findViewById(R.id.error);

        webView = layout.findViewById(R.id.web_view);
        webView.setWebViewClient(new CustomWebViewClient(loading,
                webResourceError -> {
                    Toast.makeText(getActivity(), "Cannot load page", Toast.LENGTH_SHORT).show();
                }));
        ((SetWebViewCommunicator) getContext()).setWebView(webView);
        //WebSettings webSettings = webView.getSettings();
        //webSettings.setJavaScriptEnabled(true);

        load();
        return layout;
    }

    public void load() {
        List<Url> urls = ((GetUrlsCommunicator) getContext()).getUrls();
        if (urls != null && !urls.isEmpty()) {
            for (Url link : urls) {
                String resource = link.getResource();
                if (link.getType().equalsIgnoreCase("wikidata")) {
                    int pageSplit = resource.lastIndexOf("/") + 1;
                    String q = resource.substring(pageSplit);
                    String lang = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();
                    viewProgressLoading(true);
                    api.getSitelinks(
                            q,
                            result -> {
                                viewProgressLoading(false);
                                String url = null;
                                if (result.containsKey(lang)) {
                                    url = result.get(lang);
                                } else if (result.containsKey("en")) {
                                    url = result.get("en");
                                }
                                if (url != null) {
                                    webView.loadUrl(url);
                                }
                            },
                            this::showConnectionWarning,
                            lang);
                }
            }
        }
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        ShowUtil.showError(getActivity(), t);
        loading.setVisibility(View.GONE);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> load());
    }

}
