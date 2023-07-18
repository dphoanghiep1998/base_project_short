package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.graphics.PixelFormat
import android.hardware.Camera
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceHolder.Callback
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.hide
import com.neko.hiepdph.skibyditoiletvideocall.common.show
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
    private var camera: Camera? = null
    private var mediaRecorder: MediaRecorder? = null
    private var count = 0L
    private var path = ""
    private val arg by navArgs<FragmentScreenAcceptArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentScreenAcceptBinding.inflate(inflater, container, false)
        changeBackPressCallBack()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
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
                        arg?.characterModel?.videoType ?: 4
                    )
                )
                val direction =
                    FragmentScreenAcceptDirections.actionFragmentScreenAcceptToFragmentCallClose(arg.characterModel)
                findNavController().navigate(direction)
            },
            onPrepareDone = {
                if (!timeRunning) {
                    startTimer()
                }
                binding.playerView.apply {
                    player = viewModel.getPlayer()
                    keepScreenOn = true
                }
                startRecording()
            })
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
                    arg?.characterModel?.videoType ?: 4
                )
            )
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


    private fun recordVideo() {
        try {
            binding.sufaceView.holder.addCallback(object : Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    try {
                        val cameraId = getFrontCameraId()
                        if (cameraId == -1) {
                            return
                        }
                        camera = Camera.open(cameraId).also {
                            camera?.setDisplayOrientation(90)
                            camera?.setPreviewDisplay(holder)
                            camera?.startPreview()
                        }
                        binding.sufaceView.visibility = View.VISIBLE
                        binding.sufaceView.setZOrderMediaOverlay(true)
                        binding.sufaceView.setZOrderOnTop(true)
                        Log.d("TAG", "surfaceCreated: " +  binding.sufaceView.visibility)

                    } catch (e: IOException) {
                        Log.d("TAG", "surfaceCreated: co loii")
                        e.printStackTrace()
                    } catch (e: RuntimeException) {
                        Log.d("TAG", "surfaceCreated: co loi")
                        e.printStackTrace()
                    }
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder, format: Int, width: Int, height: Int
                ) {
//                    if (holder.surface == null) {
//                        return
//                    }
//                    try {
//                        camera?.stopPreview()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//
//                    }
//                    try {
//                        camera?.setDisplayOrientation(90)
//                        camera?.setPreviewDisplay(holder)
//                        camera?.startPreview()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//
//                    }
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    releaseCamera()
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startRecording() {
        try {
            mediaRecorder = MediaRecorder()

            camera?.unlock()
            mediaRecorder?.setCamera(camera)

            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)

            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP)
            path = getOutputMediaFile()?.path.toString()
            mediaRecorder?.setOutputFile(path)

            mediaRecorder?.setPreviewDisplay(binding.sufaceView.holder.surface)
            mediaRecorder?.setOrientationHint(270)
            mediaRecorder?.prepare()
            mediaRecorder?.start()

        } catch (e: IOException) {
            Log.d("TAG", "startRecording: " + e)
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null

            camera?.lock()
        } catch (e: Exception) {
        }
    }

    private fun pauseRecording() {
        try {
            mediaRecorder?.pause()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null

            camera?.lock()
        } catch (e: Exception) {
        }
    }

    private fun releaseCamera() {
        camera?.stopPreview()
        camera?.release()
        camera = null
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
            mediaStorageDir.path + File.separator + "VID_$timeStamp.mp4"
        )
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
        stopRecording()
        viewModel.pausePlayer()
    }

    override fun onStop() {
        super.onStop()
        pauseTimer()
        stopRecording()
        viewModel.pausePlayer()
    }

    override fun onResume() {
        super.onResume()

        recordVideo()

        if (!timeRunning) {
            startTimer()
//            stopRecording()
            viewModel.resumePlayer()
        }
    }


    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }


}