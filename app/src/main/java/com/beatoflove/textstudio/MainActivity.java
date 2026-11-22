package com.beatoflove.textstudio;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.webkit.*;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import org.json.JSONObject;
import java.io.*;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final int OPEN=11, CREATE=12, MAX=5*1024*1024;
    private final ExecutorService workers=Executors.newSingleThreadExecutor();
    private WebView web;
    private Uri current, candidate;
    private boolean candidateWritable, editorReady, opening;
    private Intent queuedExternal;
    private long openSequence;
    private byte[] pending;
    private String pendingName;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        FrameLayout root=new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(25,34,49));
        web=new WebView(this);
        root.addView(web,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(root);
        if(android.os.Build.VERSION.SDK_INT>=35) root.setOnApplyWindowInsetsListener((v,insets)->{
            android.graphics.Insets b=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());
            android.graphics.Insets keyboard=insets.getInsets(WindowInsets.Type.ime());
            v.setPadding(b.left,b.top,b.right,Math.max(b.bottom,keyboard.bottom));return insets;
        });
        WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(false);s.setAllowContentAccess(false);s.setBlockNetworkLoads(true);s.setSupportMultipleWindows(false);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient(){ @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r){return true;} });
        web.addJavascriptInterface(new Bridge(),"NativeText");
        if (isExternal(getIntent())) queuedExternal=getIntent();
        web.loadUrl("file:///android_asset/index.html");
    }
    private boolean isExternal(Intent intent) {
        return intent!=null && (Intent.ACTION_VIEW.equals(intent.getAction()) || Intent.ACTION_EDIT.equals(intent.getAction()));
    }
    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (!isExternal(intent)) return;
        if (!editorReady) { queuedExternal=intent; return; }
        openExternal(intent);
    }
    private void openExternal(Intent intent) {
        if (intent==null || !isExternal(intent)) return;
        readDocument(intent.getData(),true);
    }
    private void readDocument(Uri u, boolean external) {
        if (u==null || !"content".equals(u.getScheme())) {
            js("nativeOpenError","ไม่รองรับตำแหน่งไฟล์นี้ กรุณาเปิดจากภายในแอป");
            return;
        }
        if (opening) { js("nativeOpenError","กำลังเปิดไฟล์ กรุณาลองอีกครั้ง"); return; }
        opening=true;
        final String token=Long.toString(++openSequence);
        final boolean writable=checkUriPermission(u, android.os.Process.myPid(), android.os.Process.myUid(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION)==android.content.pm.PackageManager.PERMISSION_GRANTED;
        workers.execute(()->{
            try(InputStream in=getContentResolver().openInputStream(u); ByteArrayOutputStream out=new ByteArrayOutputStream()) {
                if(in==null) throw new IOException("ไม่สามารถเปิดไฟล์ได้");
                byte[] buf=new byte[16384]; int n,total=0;
                while((n=in.read(buf))!=-1) {
                    total+=n; if(total>MAX) throw new IOException("ไฟล์เกิน 5 MiB");
                    out.write(buf,0,n);
                }
                String filename=fileName(u,"Untitled.txt");
                String encoded=Base64.encodeToString(out.toByteArray(),Base64.NO_WRAP);
                runOnUiThread(()->{
                    if(isFinishing()||isDestroyed()) return;
                    candidate=u; candidateWritable=writable;
                    js("nativeOpened",filename,encoded,Boolean.toString(writable),Boolean.toString(external),token);
                });
            } catch(Exception e) {
                runOnUiThread(()->{opening=false;candidate=null;js("nativeOpenError",e.getMessage()==null?"ไม่สามารถเปิดไฟล์ได้":e.getMessage());});
            }
        });
    }
    private String mimeForName(String filename) {
        String lower=filename.toLowerCase(java.util.Locale.ROOT);
        if(lower.endsWith(".json")) return "application/json";
        if(lower.endsWith(".xml")) return "application/xml";
        if(lower.endsWith(".md")||lower.endsWith(".markdown")) return "text/markdown";
        if(lower.endsWith(".csv")) return "text/csv";
        if(lower.endsWith(".tsv")) return "text/tab-separated-values";
        if(lower.endsWith(".html")||lower.endsWith(".htm")) return "text/html";
        if(lower.endsWith(".yaml")||lower.endsWith(".yml")) return "application/yaml";
        if(lower.endsWith(".js")) return "application/javascript";
        if(lower.endsWith(".toml")) return "application/toml";
        return "text/plain";
    }
    private void js(String method,String... vals){StringBuilder b=new StringBuilder("window.").append(method).append('(');for(int i=0;i<vals.length;i++){if(i>0)b.append(',');b.append(JSONObject.quote(vals[i]));}b.append(')');runOnUiThread(()->{if(!isFinishing()&&!isDestroyed())web.evaluateJavascript(b.toString(),null);});}
    private void error(Exception e){js("nativeError",e.getMessage()==null?"ไม่สามารถดำเนินการได้":e.getMessage());}
    private byte[] bytes(String encoded)throws IOException{if(encoded.length()>((MAX+2)/3)*4)throw new IOException("ไฟล์เกิน 5 MiB");byte[] b=Base64.decode(encoded,Base64.DEFAULT);if(b.length>MAX)throw new IOException("ไฟล์เกิน 5 MiB");return b;}
    private String fileName(Uri u,String fallback){try(Cursor c=getContentResolver().query(u,new String[]{OpenableColumns.DISPLAY_NAME},null,null,null)){if(c!=null&&c.moveToFirst()&&!c.isNull(0))return c.getString(0);}catch(Exception ignored){}return fallback;}
    private void write(Uri u,byte[] b,String fallback){workers.execute(()->{try(OutputStream out=getContentResolver().openOutputStream(u,"wt")){if(out==null)throw new IOException("ไม่สามารถเปิดไฟล์เพื่อบันทึกได้");out.write(b);out.flush();}catch(Exception e){error(e);return;}runOnUiThread(()->{current=u;js("nativeSaved",fileName(u,fallback));});});}
    public final class Bridge {
        @JavascriptInterface public void open(){runOnUiThread(()->{try{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("*/*");i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);startActivityForResult(i,OPEN);}catch(Exception e){error(e);}});}
        @JavascriptInterface public void ready(){runOnUiThread(()->{
            if(editorReady)return;
            editorReady=true;
            Intent intent=queuedExternal;queuedExternal=null;
            if(intent!=null)openExternal(intent);
        });}
        @JavascriptInterface public void completeOpen(String token, boolean accepted){runOnUiThread(()->{
            if(!opening||!Long.toString(openSequence).equals(token))return;
            if(accepted&&candidate!=null)current=candidateWritable?candidate:null;
            candidate=null;candidateWritable=false;opening=false;
        });}
        @JavascriptInterface public void save(String b){runOnUiThread(()->{try{if(current==null)throw new IOException("กรุณาใช้ Save As");write(current,bytes(b),fileName(current,"Untitled.txt"));}catch(Exception e){error(e);}});}
        @JavascriptInterface public void saveAs(String name,String b){runOnUiThread(()->{try{pending=bytes(b);pendingName=name;Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType(mimeForName(name));i.putExtra(Intent.EXTRA_TITLE,name);i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);startActivityForResult(i,CREATE);}catch(Exception e){pending=null;error(e);}});}
        @JavascriptInterface public void share(String name,String b){workers.execute(()->{try{byte[] raw=bytes(b);String safe=name.replaceAll("[\\\\/\\p{Cntrl}]","_");final String shareName=(safe.isEmpty()||safe.equals(".")||safe.equals(".."))?"Untitled.txt":safe;File dir=new File(getCacheDir(),"share/"+UUID.randomUUID());if(!dir.mkdirs())throw new IOException("สร้างไฟล์ชั่วคราวไม่ได้");File f=new File(dir,shareName);try(FileOutputStream out=new FileOutputStream(f)){out.write(raw);}Uri u=new Uri.Builder().scheme("content").authority("com.beatoflove.textstudio.share").appendPath(dir.getName()).appendPath(shareName).build();runOnUiThread(()->{try{Intent i=new Intent(Intent.ACTION_SEND);i.setType("text/plain");i.putExtra(Intent.EXTRA_STREAM,u);i.setClipData(android.content.ClipData.newRawUri(shareName,u));i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivity(Intent.createChooser(i,"แชร์ไฟล์ข้อความ"));}catch(Exception e){error(e);}});}catch(Exception e){error(e);}});}
        @JavascriptInterface public void link(String address){runOnUiThread(()->{try{Uri u=Uri.parse(address);if("https".equals(u.getScheme())||"http".equals(u.getScheme()))startActivity(new Intent(Intent.ACTION_VIEW,u));}catch(Exception e){error(e);}});}
    }
    @Override protected void onActivityResult(int r,int result,Intent data){super.onActivityResult(r,result,data);if(r!=OPEN&&r!=CREATE)return;if(result!=RESULT_OK||data==null||data.getData()==null){pending=null;js("nativeCancelled");return;}Uri u=data.getData();if(r==CREATE){byte[] b=pending;String n=pendingName;pending=null;if(b==null){js("nativeError","ข้อมูลบันทึกไม่พร้อม กรุณาลอง Save As ใหม่");return;}write(u,b,n);return;}readDocument(u,false);}
    @Override public void onBackPressed(){web.evaluateJavascript("window.nativeBack ? window.nativeBack() : true",v->{if("true".equals(v))finish();});}
    @Override protected void onDestroy(){workers.shutdown();if(web!=null){web.removeJavascriptInterface("NativeText");web.destroy();}super.onDestroy();}
}
