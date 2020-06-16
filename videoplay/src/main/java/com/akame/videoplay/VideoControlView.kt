package com.akame.videoplay

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.provider.Settings
import android.view.*
import android.widget.*
import com.akame.developkit.image.ImageLoader
import com.akame.developkit.util.NotchDisplayUtils
import com.akame.developkit.util.ScreenUtil
import com.akame.videoplay.unit.AudioUtil
import com.akame.videoplay.unit.DateUtil
import com.akame.videoplay.unit.MyProgressView
import com.akame.videoplay.unit.ScreenModule
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class VideoControlView(context: Context) : FrameLayout(context) {
    private lateinit var ivBack: ImageView
    private lateinit var ivStatue: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var ivScree: ImageView
    private lateinit var tvProgress: TextView
    private var controlListener: VideoControlListener? = null
    private var screenModule = ScreenModule.SMALL
    private var isShowControl = false

    //屏幕旋转监听
    private var orientationEventListener: OrientationEventListener
    private var timer: Timer? = null
    private var isClickFull = false // 是否点击了全屏
    private var isClickSmall = false // 是否点了小屏
    private var screenTime = 0L
    private var isTouchSeekBar = false

    private var scrollModule = ScrollModule.NONE

    private val gestureDetector: GestureDetector
    private val gestureControlVie: View
    private lateinit var llGestureProgress: LinearLayout
    private lateinit var tvGestureProgress: TextView
    private lateinit var pbGesture: ProgressBar
    private lateinit var llBrightness: LinearLayout
    private lateinit var pbBrightness: ProgressBar
    private lateinit var llVolume: LinearLayout
    private lateinit var pbVolume: ProgressBar
    private lateinit var ivVolume: ImageView
    private lateinit var ivLock: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var ivShare: ImageView
    private lateinit var llControlHead: LinearLayout
    private lateinit var llControlButton: LinearLayout

    private val audioManger: AudioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private val audioUtil: AudioUtil by lazy { AudioUtil(context) }
    private var currentDuration: Long = 0L //当前播放进度
    private var duration: Long = 0L //视频总时长

    private var loadingView: View //加载view
    private var endView: View //播放结束view
    private var errorView: View //错误view
    private var albumView: View
    lateinit var ivAlbum: ImageView

    private lateinit var tvReplay: TextView

    private lateinit var tvNextPlay: TextView
    private lateinit var tvErrorCode: TextView
    private lateinit var tvReplayError: TextView
    private var isLock = false // 是否锁定屏幕
    private var playStatue = PlayStatue.PAUSE //当前视频播放状态，用来但视频暂停但时候 不启动重力感应旋转屏幕
    private var isShowControlHeadView = true
    private val notchPaddingHeight = ScreenUtil.dip2px(10f)
    private var isAdapterNotch = false

    private var seekBarBottom: MySeekBar
    private lateinit var myProgressLoading: MyProgressView

    init {
        clipChildren = false
        albumView = LayoutInflater.from(context).inflate(R.layout.layout_album_view, null).apply {
            this.layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        addView(albumView)
        albumView.visibility = View.GONE

        gestureControlVie =
            LayoutInflater.from(context).inflate(R.layout.layout_gesture_control_view, null).apply {
                this.layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        addView(gestureControlVie)
        gestureControlVie.visibility = View.GONE //默认隐藏

        loadingView =
            LayoutInflater.from(context).inflate(R.layout.layout_play_loading_view, null).apply {
                this.layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        addView(loadingView)
        loadingView.visibility = View.GONE

        endView = LayoutInflater.from(context).inflate(R.layout.layout_end_view, null).apply {
            this.layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        addView(endView)
        endView.visibility = View.GONE

        errorView = LayoutInflater.from(context).inflate(R.layout.layout_error_view, null).apply {
            this.layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        addView(errorView)
        errorView.visibility = View.GONE

        val controlView =
            LayoutInflater.from(context).inflate(R.layout.layout_control_view, null).apply {
                this.layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        addView(controlView)


        seekBarBottom = MySeekBar(context)
        seekBarBottom.layoutParams =
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                this.gravity = Gravity.BOTTOM
            }
        addView(seekBarBottom)

        //监听屏幕旋转角度
        orientationEventListener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                var screenChange = 1//是否开启自动旋转 1：开启 0：关闭
                try {
                    screenChange = Settings.System.getInt(
                        context.contentResolver,
                        Settings.System.ACCELEROMETER_ROTATION
                    )
                } catch (e: Settings.SettingNotFoundException) {
                    e.printStackTrace()
                }

                if (screenChange != 1 || isLock || (screenModule == ScreenModule.SMALL && playStatue == PlayStatue.PAUSE)) {
                    //如果是锁屏，未播放，或者播放了处于暂停状态 都不进行重力感应旋转屏幕
                    return
                }
                //因为这个方法会被调用多次 判断这个方法是在300毫秒调用以后 筛掉1000毫秒之前的值
                if (screenTime == 0L) {
                    screenTime = System.currentTimeMillis()
                }
                if (System.currentTimeMillis() - screenTime < 1000) {
                    return
                }
                when {
                    orientation > 315 || orientation < 45 -> {
                        if (screenModule != ScreenModule.SMALL && !isClickFull) {
                            changeScreenModule(
                                context,
                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            )
                        }
                        isClickSmall = false
                    }

                    orientation in 45..135 -> {
                        if (screenModule != ScreenModule.FUll_LIFE && !isClickSmall) {
                            changeScreenModule(
                                context,
                                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                            )
                        }
                        isClickFull = false
                    }

                    orientation in 225..315 -> {
                        if (screenModule != ScreenModule.FULL_RIGHT && !isClickSmall) {
                            changeScreenModule(
                                context,
                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            )
                        }
                        isClickFull = false
                    }
                }
            }
        }

        //判断设备是否支持监听屏幕旋转，如果不支持就关闭
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable()
        } else {
            orientationEventListener.disable()
        }

        //手势操作监听
        gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                var defaultBrightness = 0f
                var defaultVolume = 0
                override fun onDown(e: MotionEvent?): Boolean {
                    defaultBrightness = getAppBrightness(context)
                    defaultVolume = audioUtil.getCurrentVolume()
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                }

                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    if (isLock) {
                        return true
                    }
                    when (scrollModule) {
                        ScrollModule.NONE -> {
                            scrollModule =
                                if (abs(distanceX) - abs(distanceY) > 10) {
                                    ScrollModule.FF_REW
                                } else {
                                    if (e1?.x!! < width / 2) {
                                        ScrollModule.BRIGHTNESS
                                    } else {
                                        ScrollModule.VOLUME
                                    }
                                }
                            llBrightness.visibility = View.GONE
                            llGestureProgress.visibility = View.GONE
                            llVolume.visibility = View.GONE
                        }

                        ScrollModule.FF_REW -> {
                            //横着划
                            val offset = e2?.x!! - e1?.x!!
                            val space = offset / width
                            var newProgress = currentDuration + (space * duration).toLong()
                            if (newProgress < 0) {
                                newProgress = 0
                            } else if (newProgress > duration) {
                                newProgress = duration
                            }
                            tvGestureProgress.text = DateUtil.formatData(newProgress)
                            pbGesture.progress = ((newProgress / (duration * 1f)) * 100).toInt()
                            llGestureProgress.visibility = View.VISIBLE
                        }

                        ScrollModule.BRIGHTNESS -> {
                            //左边竖着划 调节亮度
                            val offset = -(e2?.y!! - e1?.y!!)
                            if (context is Activity) {
                                val window = context.window
                                var brightness = defaultBrightness + offset / height
                                brightness = min(brightness, 1f)
                                brightness = max(brightness, 0f)
                                val mAttrs = window.attributes
                                mAttrs.screenBrightness = brightness
                                window.attributes = mAttrs
                                pbBrightness.progress = (brightness * 100).toInt()
                            }
                            llBrightness.visibility = View.VISIBLE
                        }

                        ScrollModule.VOLUME -> {
                            //右边竖着划 调节音量
                            val offset = -(e2?.y!! - e1?.y!!)
                            pbVolume.progress =
                                audioUtil.setVolumePercent(offset / height, defaultVolume)
                            ivVolume.setImageResource(if (pbVolume.progress == 0) R.mipmap.ic_volume_off else R.mipmap.ic_volume)
                            llVolume.visibility = View.VISIBLE
                        }
                    }

                    gestureControlVie.visibility = View.VISIBLE
                    return super.onScroll(e1, e2, distanceX, distanceY)
                }

                //单击显示控制菜单
                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                    if (isShowControl) {
                        //如果是暂停并且是小屏状态 那么头部就不消失
                        if (playStatue == PlayStatue.PAUSE && screenModule == ScreenModule.SMALL) {
                            showControlHeadView()
                        } else {
                            llControlHead.visibility = View.GONE
                        }
                        llControlButton.visibility = View.GONE
                        ivLock.visibility = View.GONE
                        isShowControl = false
                    } else {
                        if (!isLock) {
                            if (errorView.visibility == View.VISIBLE || endView.visibility == View.VISIBLE) {
                                llControlButton.visibility = View.GONE
                            } else {
                                llControlButton.visibility = View.VISIBLE
                            }
                            showControlHeadView()
                        }
                        if (screenModule != ScreenModule.SMALL)
                            ivLock.visibility = View.VISIBLE
                        isShowControl = true
                        closeControlView()
                    }
                    return super.onSingleTapConfirmed(e)
                }
            })
        initView()

        //适配非全面屏需要向下移动
        notchAdapter()
    }

    private fun initView() {
        ivBack = findViewById(R.id.iv_back)
        ivBack.setOnClickListener {
            gotoBack()
        }
        ivStatue = findViewById(R.id.iv_statue)
        seekBar = findViewById(R.id.seek)
        ivScree = findViewById(R.id.iv_screen)
        tvProgress = findViewById(R.id.tv_progress)
        llControlHead = findViewById(R.id.ll_control_head)
        llControlButton = findViewById(R.id.ll_control_button)
        ivLock = findViewById(R.id.iv_lock)
        tvTitle = findViewById(R.id.tv_title)
        ivLock.visibility = View.GONE
        ivShare = findViewById(R.id.iv_share)
        ivShare.setOnClickListener {
            controlListener?.shareClickListener()
        }
        llControlButton.visibility = View.GONE

        ivStatue.setOnClickListener {
            controlListener?.viewStatueListener()
        }

        ivLock.setOnClickListener {
            if (isLock) {
                ivLock.setImageResource(R.mipmap.ic_screen_unlock)
                showControlHeadView()
                llControlButton.visibility = View.VISIBLE
            } else {
                ivLock.setImageResource(R.mipmap.ic_screen_lock)
                llControlHead.visibility = View.GONE
                llControlButton.visibility = View.GONE
            }
            isLock = !isLock
        }

        ivScree.setOnClickListener {
            val statue: Int
            if (screenModule == ScreenModule.SMALL) {
                statue = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                isClickFull = true
            } else {
                statue = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                isClickSmall = true
            }
            changeScreenModule(context!!, statue)
        }

        seekBar.max = 100
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isTouchSeekBar = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                controlListener?.viewSeekListener(seekBar.progress)
                isTouchSeekBar = false
                closeControlView()
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }
        })

        llGestureProgress = findViewById(R.id.ll_progress)
        tvGestureProgress = findViewById(R.id.tv_gesture_progress)
        pbGesture = findViewById(R.id.pb_gesture)
        llBrightness = findViewById(R.id.ll_brightness)
        pbBrightness = findViewById(R.id.pb_brightness)
        llVolume = findViewById(R.id.ll_volume)
        pbVolume = findViewById(R.id.pb_volume)
        ivVolume = findViewById(R.id.iv_volume)
        pbGesture.max = 100
        pbBrightness.max = 100
        pbVolume.max = 100

        tvReplay = findViewById(R.id.tv_replay)
        tvNextPlay = findViewById(R.id.tv_nextPlay)
        tvReplay.setOnClickListener {
            controlListener?.replayOnClick()
        }
        tvNextPlay.setOnClickListener { controlListener?.nextPlayOnClick() }

        tvErrorCode = findViewById(R.id.tv_error_code)
        tvReplayError = findViewById(R.id.tv_replay_error)
        tvReplayError.setOnClickListener {
            closeErrorView()
            controlListener?.replayOnClick()
        }

        ivAlbum = findViewById(R.id.iv_album)
        val ivPlay = findViewById<ImageView>(R.id.iv_play)
        ivPlay.setOnClickListener {
            closeAlbumView()
            showLoadingView()
            controlListener?.albumStartPlay()
        }

        seekBarBottom.max = 100

        myProgressLoading = findViewById(R.id.my_Progress)
        myProgressLoading.isShow(false)
    }

    fun setStatueView(statue: PlayStatue) {
        this.playStatue = statue
        if (statue == PlayStatue.START) {
            ivStatue.setImageResource(R.mipmap.ic_pause)
            if (context is Activity) {
                (context as Activity).window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //保持屏幕常亮
            }
        } else if (statue == PlayStatue.PAUSE || statue == PlayStatue.STOP) {
            ivStatue.setImageResource(R.mipmap.ic_play)
            if (context is Activity) {
                (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //去除屏幕常亮
            }
        }
    }

    interface VideoControlListener {
        fun viewStatueListener()
        fun viewSeekListener(position: Int)
        fun nextPlayOnClick()
        fun replayOnClick()
        fun albumStartPlay()
        fun shareClickListener()
        fun changeScreenListener(screenModule: ScreenModule)
    }

    fun setControlListener(videoControlListener: VideoControlListener) {
        VideoPlayView@ this.controlListener = videoControlListener
    }

    /**
     * 改变屏幕的方向
     */
    private fun changeScreenModule(context: Context, requestedOrientation: Int) {
        if (context is Activity) {
            when (requestedOrientation) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                    screenModule = ScreenModule.SMALL
                }

                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> {
                    screenModule = ScreenModule.FUll_LIFE
                }

                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                    screenModule = ScreenModule.FULL_RIGHT
                }
            }
            controlListener?.changeScreenListener(screenModule)
            context.requestedOrientation = requestedOrientation
        }
        screenTime = 0

        ivLock.visibility = if (screenModule != ScreenModule.SMALL) {
            View.VISIBLE
        } else {
            View.GONE
        }

        ivScree.visibility =
            if (screenModule == ScreenModule.SMALL) View.VISIBLE else View.INVISIBLE

        if (screenModule != ScreenModule.SMALL) {
            closeControlView()
        }
        notchAdapter()
    }

    /**
     * 更新进度条刷新
     */
    fun updateProgress(currentDuration: Long, duration: Long) {
        this.currentDuration = currentDuration
        this.duration = duration
        val data = DateUtil.formatData(currentDuration) + " / " +
                DateUtil.formatData(duration)
        tvProgress.text = data
        val progress = (currentDuration / (duration * 1f)) * 100
        //用户手离开栏seekBar才进行更新进度 否则会出现手拖动到后面 seekBar游标跑到前面去
        if (!isTouchSeekBar) {
            seekBar.progress = progress.toInt()
        }
        seekBarBottom.progress = progress.toInt()
    }

    private fun closeControlView() {
        if (timer != null) {
            timer?.cancel()
        }
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                //控制栏显示并且用户的手离开栏seekBar的状态隐藏
                if (isShowControl && !isTouchSeekBar) {
                    post {
                        if (screenModule == ScreenModule.SMALL && playStatue == PlayStatue.PAUSE || errorView.visibility == View.VISIBLE || endView.visibility == View.VISIBLE) {
                            showControlHeadView()
                        } else {
                            llControlHead.visibility = View.GONE
                        }
                        llControlButton.visibility = View.GONE
                        ivLock.visibility = View.GONE
                        isShowControl = false
                    }
                } else if (screenModule != ScreenModule.SMALL && llControlHead.visibility == View.VISIBLE) {
                    post {
                        llControlHead.visibility = View.GONE
                        ivLock.visibility = View.GONE
                        isShowControl = false
                    }
                }
            }
        }, 5000)
    }

    fun release() {
        myProgressLoading.onCancel()
        orientationEventListener.disable()
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            gestureControlVie.visibility = View.GONE
            if (scrollModule == ScrollModule.FF_REW) {
                //是快进
                controlListener?.viewSeekListener(pbGesture.progress)
            }
            scrollModule = ScrollModule.NONE //重制滑动状态
        }
        return gestureDetector.onTouchEvent(event)
    }

    /**
     * 根据视频长宽调整播放页面的长宽
     */
    fun setLayoutParamsSize(width: Int, height: Int) {
        val params = layoutParams
        val space = ScreenUtil.getScreenWidth() / (width * 1f)
        params.width = (width * space).toInt()
        params.height = (height * space).toInt()
        params.height = max(params.height, ScreenUtil.getScreenHeight() / 4)
        layoutParams = params
    }

    fun showErrorView(errorCode: Int) {
        tvErrorCode.text = "（$errorCode）"
        errorView.visibility = View.VISIBLE
        showControlHeadView()
        llControlButton.visibility = View.GONE
    }

    private fun closeErrorView() {
        errorView.visibility = View.GONE
    }

    fun showEndView() {
        endView.visibility = View.VISIBLE
        showControlHeadView()
        llControlButton.visibility = View.GONE
    }

    fun closeEndView() {
        endView.visibility = View.GONE
    }

    fun showLoadingView() {
        loadingView.visibility = View.VISIBLE
        myProgressLoading.isShow(true)
    }

    fun closeLoadingView() {
        loadingView.visibility = View.GONE
        myProgressLoading.isShow(false)
    }

    fun setAlbumView(albumUrl: String) {
        albumView.visibility = View.VISIBLE
        ImageLoader.with(context)
            .circleCrop()
            .url(albumUrl)
            .show(ivAlbum)
    }

    fun closeAlbumView() {
        albumView.visibility = View.GONE
    }

    fun setVideoTitle(title: String) {
        tvTitle.text = title
    }

    fun gotoBack() {
        if (screenModule == ScreenModule.SMALL) {
            if (context is Activity) {
                (context as Activity).finish()
            }
        } else {
            changeScreenModule(
                context!!,
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            )
            isClickSmall = true
        }
    }

    fun goneScreenView() {
        ivScree.visibility = View.INVISIBLE
    }

    fun goneControlHeadView() {
        llControlHead.visibility = View.GONE
        isShowControlHeadView = false
    }

    fun showControlHeadView() {
        if (isShowControlHeadView) {
            llControlHead.visibility = View.VISIBLE
        }
    }

    fun goneShareView() {
        ivShare.visibility = View.GONE
    }

    fun setNextBtnStatue(hasNext: Boolean) {
        tvNextPlay.visibility = if (hasNext) View.VISIBLE else View.GONE
    }

    fun isShowEndView(): Boolean = endView.visibility == View.VISIBLE

    private fun notchAdapter() {
        if (isAdapterNotch && context is Activity && !NotchDisplayUtils().hasNotchInScreen(context as Activity)) {
            llControlHead.setPadding(
                llControlHead.paddingLeft,
                llControlHead.paddingTop + if (screenModule == ScreenModule.SMALL) notchPaddingHeight else -notchPaddingHeight,
                llControlHead.paddingRight,
                llControlHead.paddingBottom
            )
        }
    }

    fun isAdapterNotch(isAdapterNotch: Boolean) {
        this.isAdapterNotch = isAdapterNotch
    }

    /**
     * 获取当前页面的亮度
     */
    fun getAppBrightness(context: Context?): Float {
        var brightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        if (context is Activity) {
            val window = context.window
            val layoutParams = window.attributes
            brightness = layoutParams.screenBrightness
        }
        if (brightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
            brightness = getSystemBrightness() / 255f
        }
        return brightness
    }

    /**
     * 获取系统亮度
     */
    private fun getSystemBrightness(): Int = Settings.System.getInt(
        context.contentResolver,
        Settings.System.SCREEN_BRIGHTNESS,
        255
    )

}