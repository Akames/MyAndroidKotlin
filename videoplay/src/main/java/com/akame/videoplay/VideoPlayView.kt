package com.akame.videoplay

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.net.ConnectivityManager
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import com.akame.developkit.util.NotchDisplayUtils
import com.akame.developkit.util.ScreenUtil
import com.akame.videoplay.unit.RotateScreenUnit
import com.akame.videoplay.unit.ScreenModule
import tv.danmaku.ijk.media.player.IjkMediaPlayer


open class VideoPlayView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {
    private val ijkPlayer = IjkMediaPlayer()
    private var statue: PlayStatue? = null
    private lateinit var progressThread: Thread
    private var isResumeStart = false //是否可以进行恢复暂停播放
    private var surface: Surface? = null
    private lateinit var surfaceTexture: SurfaceTexture
    private var videoPlayCallBack: VideoPlayCallBack? = null
    private var isDirectPlay = true //是否直接播放

    //负责渲染播放view
    private var textureView: NiceTextureView? = null

    //控制状态view
    private val controlView: VideoControlView by lazy {
        VideoControlView(context).apply {
            this.layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    //添加controlView
    private val containerView: FrameLayout by lazy {
        FrameLayout(context).apply {
            this.layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            this.setBackgroundColor(Color.BLACK)
            this.clipChildren = false
        }
    }

    private lateinit var videoUrl: String
    private var defaultCurrentDuration = 0L
    private var replayUrl = ""
    private var isBuffering = false //是否在缓冲
    private var isAdapterNotch = false//是否需要适配全面屏
    private var currentScreen = ScreenModule.SMALL //当前屏幕方向

    init {
        this.clipChildren = false
        //设置ijkPlayer参数 如果不设置 seekTo会回到起点
        ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
        //设置seekTo能够快速seek到指定位置并播放 如果不设置 seekTo很慢
        ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek")
        //播放前的探测Size，默认是1M, 改小一点会出画面更快
        ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024 * 10)
        //设置探测时间 达到首屏秒开效果
        ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1)
        //设置最大探测时间
        ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L)
        //断网重连次数
        ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 5)
        //设置缓冲区为100KB
        ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 100 * 1024)
        //视频的话，设置100帧即开始播放
        ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 100)
        ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1)
        //当cpu播放很慢当时候进行跳帧处理
        ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5)
        //设置最大当fps
        ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 30)
        //设置prepared后不直接播放
        //ijkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0)

        //断网重连
        ijkPlayer.setOnNativeInvokeListener { i, bundle ->
            true
        }

        ijkPlayer.setOnPreparedListener {
            //            controlView.setLayoutParamsSize(it.videoWidth, it.videoHeight)
            updateStatueView(PlayStatue.START)
            controlView.closeLoadingView()
            videoPlayCallBack?.onPreparedListener()
            seekDefaultCurrentDuration(defaultCurrentDuration)
        }

        ijkPlayer.setOnCompletionListener {
            controlView.showEndView()//显示播放结束的view
            updateStatueView(PlayStatue.STOP)
            videoPlayCallBack?.onCompletionListener()
        }

        ijkPlayer.setOnSeekCompleteListener {
            //每次seekTo 需要手动调用播放
            start()
        }

        ijkPlayer.setOnErrorListener { p0, p1, p2 ->
            controlView.showErrorView(p1)
            videoPlayCallBack?.onErrorListener(p1)
            true
        }

        ijkPlayer.setOnVideoSizeChangedListener { iMediaPlayer, i, i2, i3, i4 ->
            textureView?.adaptVideoSize(i, i2)
        }

        ijkPlayer.setOnBufferingUpdateListener { iMediaPlayer, i ->
            val current = ((getCurrentDuration() * 1f / getDuration()) * 100).toInt()
            isBuffering = if (current > i) {
                //说明正在缓冲中
                controlView.showLoadingView()
                pause()
                true
            } else {
                controlView.closeLoadingView()
                if (isResumeStart)
                    start()
                false
            }
        }

        //todo 都有协程了 为啥还用线程？？
        progressThread = Thread(Runnable {
            while (true) {
                try {
                    if (progressThread.isInterrupted) {
                        break
                    }
                    Thread.sleep(50)
                    if (ijkPlayer.isPlaying) {
                        controlView.post {
                            controlView.updateProgress(getCurrentDuration(), getDuration())
                        }
                    }
                } catch (e: Exception) {
                    break
                }
            }
        })
        progressThread.start()
    }


    private fun initPlayView() {
        if (textureView != null) {
            //如果不等于null 说明已经加载好了 可以直接播放了
            loadingPlay(videoUrl)
            return
        }
        textureView = NiceTextureView(context).apply {
            this.layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                this.gravity = Gravity.CENTER
            }
        }
        containerView.addView(textureView)
        containerView.addView(controlView)
        addView(containerView)
        textureView?.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                return textureView == null
            }

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                if (this@VideoPlayView.surface == null) {
                    this@VideoPlayView.surface = Surface(surface)
                    if (isDirectPlay) loadingPlay(videoUrl)
                    surfaceTexture = surface!!
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        textureView?.surfaceTexture = surfaceTexture
                    }
                }
            }
        }
        controlView.setControlListener(object : VideoControlView.VideoControlListener {
            override fun changeScreenListener(screenModule: ScreenModule) {
                if (currentScreen == ScreenModule.FUll_LIFE && screenModule == ScreenModule.FULL_RIGHT
                    || currentScreen == ScreenModule.FULL_RIGHT && screenModule == ScreenModule.FUll_LIFE
                ) {
                    currentScreen = screenModule
                    return
                }
                rsu.changeScreenWindow(
                    context as Activity,
                    this@VideoPlayView,
                    containerView,
                    screenModule == ScreenModule.SMALL
                )
                adapterNotch()
            }

            override fun shareClickListener() {
                videoPlayCallBack?.onShareClickListener()
            }

            override fun albumStartPlay() {
                loadingPlay(videoUrl)
            }

            override fun nextPlayOnClick() {
                controlView.closeEndView()
                videoPlayCallBack?.onNextPlayClickListener()
            }

            override fun replayOnClick() {
                controlView.closeEndView()
                controlView.showLoadingView()
                defaultCurrentDuration = 0L //重播重置默认播放时长
                loadingPlay(replayUrl)
                videoPlayCallBack?.onReplayClickListener()
            }

            override fun viewSeekListener(position: Int) {
                if (isBuffering) {
                    return
                }
                val pos = if (position == 100) 99 else position
                val seekToPos = (getDuration() * (pos / 100.0f)).toLong()

                ijkPlayer.seekTo(seekToPos)
            }

            override fun viewStatueListener() {
                //播放按钮监听
                isResumeStart = if (statue == PlayStatue.START) {
                    pause()
                    false
                } else {
                    start()
                    true
                }
            }
        })

    }

    fun setUp(
        videoUrl: String,
        videoTitle: String = "",
        defaultCurrentDuration: Long = 0L,
        albumUrl: String = "",
        hasNext: Boolean = false
    ) {
        this.videoUrl = videoUrl
        this.defaultCurrentDuration = defaultCurrentDuration
        this.isDirectPlay = TextUtils.isEmpty(albumUrl)
        if (!isDirectPlay) {
            showAlbum(albumUrl)
        }
        controlView.setVideoTitle(videoTitle)
        controlView.setNextBtnStatue(hasNext)
        initPlayView()
    }

    /**
     * 设置是否需要适配全面屏
     */
    fun isNeedAdapterNotch(isAdapterNotch: Boolean) {
        this.isAdapterNotch = isAdapterNotch
        controlView.isAdapterNotch(isAdapterNotch)
        adapterNotch()
    }


    private fun adapterNotch() {
        if (isAdapterNotch && context is Activity && NotchDisplayUtils().hasNotchInScreen(context as Activity)) {
            val orientation = context.resources.configuration.orientation
            setPadding(
                paddingLeft,
                paddingTop + if (orientation == Configuration.ORIENTATION_PORTRAIT) ScreenUtil.getStatusBarHeight() else -ScreenUtil.getStatusBarHeight(),
                paddingRight,
                paddingBottom
            )
        }
    }

    private fun loadingPlay(videoUrl: String) {
        this.replayUrl = videoUrl
        if (isNetworkAvailable()) {
            ijkPlayer.reset()
            ijkPlayer.dataSource =
                "ijkhttphook:$videoUrl" //加入这个头文件后 可进行断网重连 不加的话 断网直接进入error并且网络恢复不会重新加载 可惜加入后m3u8不支持播放
//        ijkPlayer.dataSource = videoUrl
            ijkPlayer.setSurface(surface)
            ijkPlayer.prepareAsync()
            //说明正在缓冲中
            controlView.showLoadingView()
        } else {
            controlView.showErrorView(100000)
        }
    }

    fun cancel() {
        progressThread.interrupt()
        textureView?.surfaceTexture?.release()
        ijkPlayer.reset()
        ijkPlayer.release()
        controlView.release()
    }

    fun pause() {
        if (ijkPlayer.isPlaying) {
            ijkPlayer.pause()
        }
        videoPlayCallBack?.onPause()
        updateStatueView(PlayStatue.PAUSE)
    }

    private fun stop() {
        ijkPlayer.stop()
        updateStatueView(PlayStatue.STOP)
    }

    fun start() {
        if (!ijkPlayer.isPlaying && !controlView.isShowEndView()) {
            ijkPlayer.start()
        }
        videoPlayCallBack?.onStart()
        updateStatueView(PlayStatue.START)
    }

    private fun updateStatueView(statue: PlayStatue) {
        VideoPlayManger@ this.statue = statue
        controlView.setStatueView(statue)
    }

    // 如果不是点击暂停 那就恢复状态
    fun playStart() {
        if (isResumeStart && !ijkPlayer.isPlaying) {
            start()
            isResumeStart = false
        }
    }

    fun playPause() {
        if (ijkPlayer.isPlaying) {
            pause()
            isResumeStart = true
        }
    }

    /**
     * 获取视频当前播放进度
     */
    fun getCurrentDuration(): Long = ijkPlayer.currentPosition

    /**
     * 获取视频总时长
     */
    fun getDuration(): Long = ijkPlayer.duration

    /**
     * 指定到某个位置播放
     */
    private fun seekDefaultCurrentDuration(duration: Long) {
        if (duration != 0L && duration != getDuration()) {
            ijkPlayer.seekTo(duration)
        }
    }

    /**
     * 设置播放回调
     */
    fun setVideoPlayCallBack(videoPlayCallBack: VideoPlayCallBack) {
        this.videoPlayCallBack = videoPlayCallBack
    }

    /**
     * 设置是否直接播放 默认为true
     */
    private fun showAlbum(albumUrl: String) {
        this.isDirectPlay = false
        controlView.setAlbumView(albumUrl)
    }

    /**
     * 设置屏幕方向监听
     */
    private val rsu: RotateScreenUnit by lazy {
        RotateScreenUnit()
    }

    fun gotoBack() {
        controlView.gotoBack()
    }

    fun goneScreen() {
        controlView.goneScreenView()
    }

    fun goneControlHeadView() {
        controlView.goneControlHeadView()
    }

    fun goneShareView() {
        controlView.goneShareView()
    }

    fun isPlaying() = ijkPlayer.isPlaying

    private fun isNetworkAvailable(): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netWorkInfo = manager.activeNetworkInfo
        return netWorkInfo?.isConnected ?: false
    }


}