package com.keytop.ndkbuilddemo;

import android.content.Context;
import android.os.Build;

import com.tencent.tinker.lib.library.TinkerLoadLibrary;

public class MyNdk {
    //    public MyNdk(){
    static { //两种调用方式都行
        System.loadLibrary("MyLibrary");
    }

    public void hotUpdate(Context context){
        String abi = Build.CPU_ABI;
        TinkerLoadLibrary.installNavitveLibraryABI(context.getApplicationContext(), abi);
        System.loadLibrary("MyLibrary");
    }

    //自己定义的方法 正常应该是c/c++对外开放的方法名称
    public native String getString();

    //自己定义的方法 正常应该是c/c++对外开放的方法名称
    public native String getMyString();
}