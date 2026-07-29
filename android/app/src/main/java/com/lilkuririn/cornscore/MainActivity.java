package com.lilkuririn.cornscore;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewFeature;

/**
 * Coquille native autour de l'application web. Les fichiers sont servis
 * depuis les assets via une origine https, et non en file://, pour que
 * localStorage se comporte normalement — c'est là qu'est stockée la partie
 * en cours, le tournoi et le palmarès.
 */
public class MainActivity extends AppCompatActivity {

    private static final String ORIGIN = "https://appassets.androidplatform.net";

    /**
     * Le bouton retour d'Android s'appuie sur l'interface existante : il ferme
     * ce qui est ouvert, ou remonte d'un écran, sans rien changer à la page.
     */
    private static final String BACK_JS =
        "(function(){" +
        "  var t=document.getElementById('tossWrap');" +
        "  if(t && !t.hidden) return '1';" +
        "  var s=document.getElementById('sheetWrap');" +
        "  if(s && s.classList.contains('on')){ document.getElementById('closeSheet').click(); return '1'; }" +
        "  var b=document.getElementById('bkSheetWrap');" +
        "  if(b && b.classList.contains('on')){ document.getElementById('bkSheetClose').click(); return '1'; }" +
        "  var g=document.getElementById('rulesWrap');" +
        "  if(g && g.classList.contains('on')){ document.getElementById('rulesClose').click(); return '1'; }" +
        "  function on(id){ var e=document.getElementById(id); return e && e.classList.contains('on'); }" +
        "  if(on('s-hall')){ document.getElementById('hallBack').click(); return '1'; }" +
        "  if(on('s-tsetup')){ document.getElementById('tsBack').click(); return '1'; }" +
        "  if(on('s-bracket')){ document.getElementById('bkHome').click(); return '1'; }" +
        "  return '0';" +
        "})()";

    private WebView web;
    private long lastBack = 0L;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        web = new WebView(this);
        web.setOverScrollMode(WebView.OVER_SCROLL_NEVER);

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(false);

        /* Laisse la page choisir son thème clair ou sombre selon celui du système. */
        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
            WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, true);
        }

        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }
        });

        setContentView(web);
        web.loadUrl(ORIGIN + "/assets/index.html");
    }

    @Override
    public void onBackPressed() {
        web.evaluateJavascript(BACK_JS, value -> {
            if (value != null && value.contains("1")) return;
            long now = System.currentTimeMillis();
            if (now - lastBack < 2000L) {
                finish();
            } else {
                lastBack = now;
                Toast.makeText(this, R.string.quit_hint, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
