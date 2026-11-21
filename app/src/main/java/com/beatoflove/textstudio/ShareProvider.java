package com.beatoflove.textstudio;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import java.io.*;
public class ShareProvider extends ContentProvider {
    @Override public boolean onCreate(){return true;}
    private File file(Uri u)throws IOException{if(u.getPathSegments().size()!=2)throw new FileNotFoundException();File root=new File(getContext().getCacheDir(),"share").getCanonicalFile();File f=new File(root,u.getPathSegments().get(0)+"/"+u.getPathSegments().get(1)).getCanonicalFile();if(!f.getPath().startsWith(root.getPath()+File.separator)||!f.isFile())throw new FileNotFoundException();return f;}
    @Override public String getType(Uri u){return "text/plain";}
    @Override public ParcelFileDescriptor openFile(Uri u,String mode)throws FileNotFoundException{if(!"r".equals(mode))throw new FileNotFoundException();try{return ParcelFileDescriptor.open(file(u),ParcelFileDescriptor.MODE_READ_ONLY);}catch(IOException e){throw new FileNotFoundException();}}
    @Override public Cursor query(Uri u,String[] p,String s,String[] a,String sort){String[] cols=p==null?new String[]{OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE}:p;MatrixCursor c=new MatrixCursor(cols);try{File f=file(u);Object[] values=new Object[cols.length];for(int i=0;i<cols.length;i++){if(OpenableColumns.DISPLAY_NAME.equals(cols[i]))values[i]=f.getName();else if(OpenableColumns.SIZE.equals(cols[i]))values[i]=f.length();}c.addRow(values);}catch(IOException ignored){}return c;}
    @Override public Uri insert(Uri u,ContentValues v){throw new UnsupportedOperationException();}
    @Override public int delete(Uri u,String s,String[] a){throw new UnsupportedOperationException();}
    @Override public int update(Uri u,ContentValues v,String s,String[] a){throw new UnsupportedOperationException();}
}
