package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.hardware.Camera
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gianghv.libads.AppOpenResumeAdManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.hide
import com.neko.hiepdph.skibyditoiletvideocall.common.show
import com.neko.hiepdph.skibyditoiletvideocall.common.showBannerAds
import com.neko.hiepdph.skibyditoiletvideocall.data.model.GalleryModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentScreenAcceptBinding
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class FragmentScreenAccept : Fragment() {

    private lateinit var binding: FragmentScreenAcceptBinding
    private val viewModel by activityViewModels<AppViewModel>()
    private var countDownTimer: CountDownTimer? = null
    private var mTimeLeftInMillis: Long = 0
    private var timeRunning = false
    private var count = 0L
    private var path = ""
    private val arg by navArgs<FragmentScreenAcceptArgs>()
    private var mediaRecorder: MediaRecorder? = null
    private var cam: Camera? = null
    private var isSaved = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentScreenAcceptBinding.inflate(inflater, container, false)
        changeBackPressCallBack()
        showBannerAds(binding.bannerAds)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initAndRecordSurfaceView()
    }


    private fun initView() {
        binding.btnDecline.hide()
        initButton()
        viewModel.playAudio(MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(arg.characterModel.videoRaw)),
            onEnd = {
                viewModel.insertGallery(
                    GalleryModel(
                        -1,
                        "",
                        Calendar.getInstance().timeInMillis,
                        count,
                        arg.characterModel.videoRaw,
                        path,
                        arg.characterModel?.videoType ?: 4
                    )
                )
                isSaved = true
                val direction =
                    FragmentScreenAcceptDirections.actionFragmentScreenAcceptToFragmentCallClose(arg.characterModel)
                findNavController().navigate(direction)
            },
            onPrepareDone = {
                if (!timeRunning) {
                    startTimer()
                }
                binding.playerView.apply {
                    viewModel.getPlayer()?.setVideoTextureView(this)
                    keepScreenOn = true
                    val mediaController = MediaController(requireActivity())
                    mediaController.setAnchorView(binding.playerView)
                }
            })
    }


    private fun initAndRecordSurfaceView() {
        binding.sufaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                initCameraRecorder()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
//                val parameters = cam!!.parameters
//                parameters.setRotation(270)
//                setPreviewSize(parameters,false)
//                cam!!.parameters = parameters
                cam!!.setDisplayOrientation(90)
                cam?.setPreviewDisplay(holder)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                stopRecord()
            }

        })
    }

    fun findBestPreviewSize(
        sizes: List<Camera.Size>,
        width: Int,
        height: Int
    ): Camera.Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = width.toDouble() / height
        var minDiff = Double.MAX_VALUE
        var minDiffAspect = Double.MAX_VALUE
        var bestSize: Camera.Size? = null
        var bestSizeAspect: Camera.Size? = null
        for (size in sizes) {
            val diff = Math.abs(size.height - height).toDouble() +
                    Math.abs(size.width - width)
            if (diff < minDiff) {
                bestSize = size
                minDiff = diff
            }
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) < ASPECT_TOLERANCE &&
                diff < minDiffAspect
            ) {
                bestSizeAspect = size
                minDiffAspect = diff
            }
        }
        return bestSizeAspect ?: bestSize
    }

    private fun initCameraRecorder() {
        cam = Camera.open(getFrontCameraId())
        mediaRecorder = MediaRecorder()
        cam?.setPreviewDisplay(binding.sufaceView.holder)
        val parameters: Camera.Parameters = cam!!.parameters
        val sizes = findBestPreviewSize(parameters.supportedPictureSizes,binding.sufaceView.measuredWidth,binding.sufaceView.measuredHeight)
        parameters["orientation"] = "portrait"
        parameters.setPreviewSize(sizes!!.width, sizes.height)

        cam?.parameters = parameters

        cam?.startPreview()
        cam!!.unlock()
        mediaRecorder!!.setCamera(cam)
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP)
        path = getOutputMediaFile()!!.path
        Log.d("TAG", "initCameraRecorder: $path")
        mediaRecorder!!.setOutputFile(path)
        mediaRecorder!!.setPreviewDisplay(binding.sufaceView.holder.surface)
        mediaRecorder!!.setOrientationHint(270)
        try {
            mediaRecorder!!.prepare()
            mediaRecorder!!.start()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }


    private fun stopRecord() {
        try {
            mediaRecorder!!.stop()
            mediaRecorder!!.reset()
            mediaRecorder!!.release()
            mediaRecorder = null
            cam!!.lock()
        } catch (e: java.lang.Exception) {
        }
    }

    private fun initButton() {
        binding.btnDecline.clickWithDebounce {
            viewModel.insertGallery(
                GalleryModel(
                    -1,
                    "",
                    Calendar.getInstance().timeInMillis,
                    count,
                    arg.characterModel.videoRaw,
                    path,
                    arg.characterModel?.videoType ?: 4
                )
            )
            isSaved = true
            val direction =
                FragmentScreenAcceptDirections.actionFragmentScreenAcceptToFragmentCallClose(arg.characterModel)
            findNavController().navigate(direction)
        }
    }


    private fun startTimer() {
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000L) {
            override fun onTick(p0: Long) {
                mTimeLeftInMillis = p0
                count += 1000L
                updateCountText(count)
                if (count > 3000) {
                    binding.btnDecline.show()
                }

            }

            override fun onFinish() {
                timeRunning = false
            }

        }
        timeRunning = true
        countDownTimer?.start()
    }

    private fun pauseTimer() {
        Log.d("TAG", "pauseTimer: ")
        countDownTimer?.cancel()
        timeRunning = false
    }

    private fun updateCountText(time: Long) {
        val minutes = (time / 1000).toInt() / 60
        val seconds = (time / 1000).toInt() % 60
        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        binding.time.text = timeLeftFormatted
    }

    private fun getFrontCameraId(): Int {
        val cameraCount = Camera.getNumberOfCameras()
        var cameraId = -1
        for (i in 0 until cameraCount) {
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i
                break
            }
        }
        return cameraId
    }

    override fun onPause() {
        super.onPause()
        pauseTimer()
        viewModel.pausePlayer()
        mediaRecorder?.pause()
    }

    override fun onStop() {
        super.onStop()
        pauseTimer()
        viewModel.pausePlayer()
        mediaRecorder?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (!timeRunning && !AppOpenResumeAdManager.isShowingAd) {
            startTimer()
            viewModel.resumePlayer()
            mediaRecorder?.resume()
        }
    }


    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(
            requireActivity().filesDir, "video"
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(
            mediaStorageDir.path + File.separator + "VID_" + timeStamp + ".mp4"
        )
    }

}