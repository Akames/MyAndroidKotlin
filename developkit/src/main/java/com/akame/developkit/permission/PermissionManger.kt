package com.akame.developkit.permission

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class PermissionManger {
    lateinit var permissions: Array<String>

    lateinit var activity: FragmentActivity

    fun with(context: Any): PermissionManger {
        when (context) {
            is AppCompatActivity -> activity = context
            is Fragment -> activity = context.activity!!
            else -> Throwable("参数类型不合法")
        }
        return this
    }

    fun request(permissions: Array<String>): PermissionManger {
        this.permissions = permissions
        return this
    }

    fun callBack(permissionCallBack: PermissionCallBack) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //6.0以下系统 直接返回成功 不需要动态获取权限
            permissionCallBack.success()
        } else {
            val permissionFragment = PermissionFragment()
            val bundle = Bundle()
            bundle.putStringArray("permissions", permissions)
            permissionFragment.arguments = bundle
            permissionFragment.permissionCallBack = permissionCallBack
            activity.supportFragmentManager.beginTransaction()
                .add(permissionFragment, activity::class.java.simpleName).commit()
        }
    }


    interface PermissionCallBack {
        fun success()

        fun fail(permissions: ArrayList<String>)
    }

}