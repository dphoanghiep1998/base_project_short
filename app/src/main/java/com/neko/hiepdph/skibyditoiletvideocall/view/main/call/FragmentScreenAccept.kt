package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceHolder.Callback
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.hide
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
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
    private var mPlayer: Player? = null
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        recordVideo()
        Handler(Looper.getMainLooper()).post {
            startRecording()
        }
    }

    private fun initView() {
        binding.btnDecline.hide()
        if (viewModel.getPlayer() == null) {
            mPlayer = ExoPlayer.Builder(requireContext()).setSeekForwardIncrementMs(15000).build()
            binding.playerView.apply {
                player = mPlayer
                keepScreenOn = true

            }
            viewModel.setupPlayer(mPlayer!!)

        } else {
            binding.playerView.apply {
                player = viewModel.getPlayer()
                keepScreenOn = true
            }
        }

        initButton()

        viewModel.playAudio(MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(arg.characterModel.videoRaw)),
            onEnd = {
                navigateToPage(R.id.fragmentScreenAccept, R.id.fragmentCallClose)
            },
            onPrepareDone = {
                startTimer(it)

            })
    }

    private fun initButton() {
        binding.btnDecline.clickWithDebounce {
            viewModel.insertGallery(
                GalleryModel(
                    -1, "", Calendar.getInstance().timeInMillis, count, path, 0
                )
            )
            navigateToPage(R.id.fragmentScreenAccept, R.id.fragmentCallClose)
        }
    }

    private fun useRecord() {
        val size = camera?.Size(400, 400)

    }

    private fun startTimer(time: Long) {
        countDownTimer = object : CountDownTimer(time, 1000L) {
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

                    } catch (e: IOException) {
                        Log.d("TAG", "surfaceCreated: " + e)
                        Toast.makeText(
                            requireContext(), "Error setting up camera preview", Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: RuntimeException) {
                        e.printStackTrace()
                    }
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder, format: Int, width: Int, height: Int
                ) {
                    if (holder.surface == null) {
                        return
                    }
                    try {
                        camera?.stopPreview()
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(), "Error changing camera preview", Toast.LENGTH_SHORT
                        ).show()
                    }
                    try {
                        camera?.setDisplayOrientation(90)
                        camera?.setPreviewDisplay(holder)
                        camera?.startPreview()
                    } catch (e: IOException) {
                        Toast.makeText(
                            requireContext(), "Error setting up camera preview", Toast.LENGTH_SHORT
                        ).show()
                    }
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
            camera?.unlock()

            mediaRecorder = MediaRecorder()
            mediaRecorder?.setCamera(camera)
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)
            mediaRecorder?.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))
            path = getOutputMediaFile()?.absolutePath.toString()
            mediaRecorder?.setOutputFile(path)
            mediaRecorder?.setPreviewDisplay(binding.sufaceView?.holder?.surface)
            mediaRecorder?.setOrientationHint(90)
            mediaRecorder?.prepare()
            mediaRecorder?.start()

            Toast.makeText(requireContext(), "Recording started", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.d("TAG", "startRecording: " + e)
            Toast.makeText(requireContext(), "Error starting recording", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null

            camera?.lock()

//            Toast.makeText(requireContext(), "Recording stopped", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
//            Toast.makeText(requireContext(), "Error stopping recording", Toast.LENGTH_SHORT).show()
        }
    }

    private fun releaseCamera() {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    private fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ), "Camera"
        )

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mediaFile = File(
            mediaStorageDir.path + File.separator + "VID_$timeStamp.mp4"
        )

        return mediaFile
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
        stopRecording()
    }

    override fun onStop() {
        super.onStop()
        pauseTimer()
        viewModel.pausePlayer()
        stopRecording()
    }


}