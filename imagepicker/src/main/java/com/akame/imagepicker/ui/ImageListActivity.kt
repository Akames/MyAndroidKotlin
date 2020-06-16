package com.akame.imagepicker.ui

import android.app.Activity
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akame.developkit.EventSimple
import com.akame.developkit.newIndexIntent
import com.akame.developkit.util.FileUtil
import com.akame.developkit.util.SystemUtil
import com.akame.imagepicker.EventCode
import com.akame.imagepicker.R
import com.akame.imagepicker.adapter.ImageListAdapter
import com.akame.imagepicker.been.ImageBeen
import com.akame.imagepicker.been.MenuBeen
import com.akame.imagepicker.crop.CropActivity
import com.akame.imagepicker.wigth.ImageMenuPopWindow
import com.akame.imagepicker.wigth.SpacesItemDecoration
import kotlinx.android.synthetic.main.activity_image_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class ImageListActivity : AppCompatActivity() {
    companion object {
        private const val CAMERA_REQUEST_CODE = 0x11

        private const val CROP_REQUEST_COED = 0X12 // 裁切完毕code
    }

    private lateinit var allImage: HashMap<String, ArrayList<ImageBeen>> //从本地获取所以图片资源对象集合
    private lateinit var menuBeenList: ArrayList<MenuBeen> //菜单列表对象
    private lateinit var adapter: ImageListAdapter
    private var menuPopWindow: ImageMenuPopWindow? = null
    private val imageBeenList = arrayListOf<ImageBeen>() //展示图片的对象

    private val cameraFile: File by lazy {
        if (isMany) {
            val parentPath = Environment.getExternalStorageDirectory().absolutePath + "/Akame/"
            File(
                FileUtil.createDir(parentPath).absolutePath,
                System.currentTimeMillis().toString() + ".jpg"
            )
        } else {
            FileUtil.createFile(FileUtil.getCachePath() + "/" + System.currentTimeMillis() + ".jpg")
        }
    }
    //是否为多选
    private var isMany = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_list)
        initListener()
        initData()
        EventBus.getDefault().register(this)
    }

    private fun initListener() {
        iv_back.setOnClickListener { finish() }
        tv_title.setOnClickListener {
            if (menuPopWindow == null) {
                menuPopWindow = ImageMenuPopWindow(
                    this@ImageListActivity,
                    conversionSelectList()
                )
                menuPopWindow?.setOnDismissListener {
                    changeTitleShowType(false)
                    vbg.visibility = View.GONE
                }
                menuPopWindow?.itemClickListener = {
                    imageBeenList.clear()
                    imageBeenList.addAll(allImage[menuBeenList[it].tag]!!)
                    adapter.notifyDataSetChanged()
                    menuPopWindow?.dismiss()
                    tv_title.text = menuBeenList[it].title
                }
            }

            if (!menuPopWindow?.isShowing!!) {
                vbg.visibility = View.VISIBLE
                menuPopWindow?.showAsDropDown(tv_title)
                changeTitleShowType(true)
            } else {
                menuPopWindow?.dismiss()
            }
        }

        tvConfirm.setOnClickListener {
            EventBus.getDefault().post(
                EventSimple(
                    EventCode.SELECT_IMAGE_CODE,
                    adapter.getSelectList()
                )
            )
            finish()
        }
    }

    private fun changeTitleShowType(isShow: Boolean) {
        tv_title.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            if (isShow) R.mipmap.ic_up else R.mipmap.ic_down,
            0
        )
    }

    private fun conversionSelectList(): ArrayList<MenuBeen> {
        menuBeenList = arrayListOf()
        allImage.forEach { (key, value) ->
            val index = key.lastIndexOf("/")
            val name = key.substring(if (index == -1) 0 else index + 1, key.length)
            if (name == "全部图片") {
                menuBeenList.add(
                    0,
                    MenuBeen(
                        value[0].path,
                        name,
                        value.size.toString(),
                        key
                    )
                )
            } else {
                menuBeenList.add(
                    MenuBeen(
                        value[0].path,
                        name,
                        value.size.toString(),
                        key
                    )
                )
            }
        }
        return menuBeenList
    }

    private fun initData() {
        isMany = intent.getBooleanExtra("isMany", false)
        rv_content.layoutManager =
            GridLayoutManager(this, 3, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager
        rv_content.addItemDecoration(SpacesItemDecoration())
        adapter = ImageListAdapter(imageBeenList, isMany)
        rv_content.adapter = adapter
        //如果是多选图片模式 需要给适配器设置容器
        if (isMany) {
            adapter.setSelectList(intent.getSerializableExtra("selectList") as ArrayList<ImageBeen>)
        }

        GlobalScope.launch(Dispatchers.IO) {
            allImage = getImageList()
            launch(Dispatchers.Main) {
                imageBeenList.addAll(allImage["全部图片"]!!)
                adapter.notifyDataSetChanged()
            }
        }

        adapter.setItemClickListener(object :
            ImageListAdapter.ItemClickListener {
            override fun onHeadClick() {
                openCamera()
            }

            override fun onItemClick(pos: Int, imagePath: String) {
                if (isMany) {
                    startActivity(
                        newIndexIntent(PreviewImageActivity::class.java)
                            .putExtra("imageList", imageBeenList)
                            .putExtra("selectList", adapter.getSelectList())
                            .putExtra("currentIndex", pos)
                    )
                } else {
                    intentCropActivity(imagePath)
                }
            }

            override fun onSelectClick(selectCount: Int) {
                tvConfirm.text = "完成(${selectCount})"
            }
        })

        tvConfirm.visibility = if (isMany) View.VISIBLE else View.GONE
        if (isMany && adapter.getSelectList().size > 0) tvConfirm.text =
            "完成(${adapter.getSelectList().size})"
    }

    fun intentCropActivity(imagePath: String, isCamera: Boolean = false) {
        startActivityForResult(
            newIndexIntent(CropActivity::class.java).putExtra("imagePath", imagePath)
                .putExtra("isCamera", isCamera)
            , CROP_REQUEST_COED
        )
    }

    /**
     * 获取系统所有图片
     */
    private fun getImageList(): HashMap<String, ArrayList<ImageBeen>> {
        val proImage = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME
        )
        val course = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            proImage,
            MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
            arrayOf("image/jpeg", "image/png"),
            MediaStore.Images.Media.DATE_MODIFIED + " desc"
        )
        val beenList = arrayListOf<ImageBeen>()
        val allImageList = HashMap<String, ArrayList<ImageBeen>>()
        if (course != null) {
            while (course.moveToNext()) {
                val path = course.getString(course.getColumnIndex(MediaStore.Images.Media.DATA))
                val displayName =
                    course.getString(course.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                var position = 0
                if (isMany) {
                    for (it in adapter.getSelectList()) {
                        if (it.path == path) {
                            position = it.position
                            break
                        }
                    }
                }
                val been = ImageBeen(path, displayName, position)
                //这里会添加所有图片进去，用来展示所有图片
                beenList.add(been)

                // 获取这张图片父路径的地址 根据父地址进行图片分类
                val dirPath = File(path).parentFile.absolutePath
                if (allImageList.containsKey(dirPath)) {
                    //如果之前有存入这个地址的类目 就找到它 添加进去
                    val listBeen = allImageList[dirPath]
                    listBeen?.add(been)
                } else {
                    val listBeen = arrayListOf<ImageBeen>()
                    listBeen.add(been)
                    allImageList[dirPath] = listBeen
                }
            }
            course.close()
        }
        allImageList["全部图片"] = beenList
        return allImageList
    }

    /**
     * 打开相机
     */
    private fun openCamera() {
        val fileUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                this,
                SystemUtil.getPackName() + ".fileprovider",
                cameraFile
            )
        } else {
            Uri.fromFile(cameraFile)
        }
        val intentCamera = Intent()
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intentCamera.action = MediaStore.ACTION_IMAGE_CAPTURE
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        startActivityForResult(
            intentCamera,
            CAMERA_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    if (isMany) {
                        //通知相册刷新
                        adapter.getSelectList().add(
                            ImageBeen(
                                cameraFile.absolutePath,
                                cameraFile.name,
                                0
                            )
                        )
                        MediaScannerConnection.scanFile(
                            application,
                            arrayOf(cameraFile.parent),
                            null,
                            null
                        )
                        tvConfirm.performClick()
                    } else {
                        intentCropActivity(cameraFile.absolutePath, true)
                    }
                }

                CROP_REQUEST_COED -> {
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(eventSimple: EventSimple<Any>) {
        when (eventSimple.code) {
            EventCode.UPDATE_SELECT_LIST -> {
                adapter.setSelectList(eventSimple.data as ArrayList<ImageBeen>)
                adapter.notifyDataSetChanged()
                tvConfirm.text = "完成(${adapter.getSelectList().size})"
            }
        }
    }
}
