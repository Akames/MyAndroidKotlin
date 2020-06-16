package com.akame.developkit.permission

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionFragment : Fragment() {
    var permissionCallBack: PermissionManger.PermissionCallBack? = null
    lateinit var permissions: Array<String>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments!!
        permissions = bundle.getStringArray("permissions") as Array<String>
        requestPermissions(permissions, 0x1100011)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionList = arrayListOf<String>()

        permissions.forEach {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(it)

            }
        }
        if (permissionList.size > 0) {
            permissionCallBack?.fail(permissions = permissionList)
        } else {
            permissionCallBack?.success()
        }
        fragmentManager?.beginTransaction()?.remove(this)?.commit()
    }


    fun setCallBack(callBack: PermissionManger.PermissionCallBack) {
        this.permissionCallBack = callBack
    }
}