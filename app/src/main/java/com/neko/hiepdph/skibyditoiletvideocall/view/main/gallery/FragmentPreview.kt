package com.neko.hiepdph.skibyditoiletvideocall.view.main.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentPreviewBinding
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FragmentPreview : Fragment() {
    private lateinit var binding: FragmentPreviewBinding
    private val viewModel by activityViewModels<AppViewModel>()
    private val navArgs by navArgs<FragmentPreviewArgs>()
    private var job: Job? = null
    private var isPause = false
    private var isStarted = false
    private var isDone = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    private fun initView() {

        binding.playerView.apply {
            player = viewModel.getPlayer2()
            keepScreenOn = true
        }
        binding.cameraView.apply {
            player = viewModel.getPlayer()
            keepScreenOn = true

        }
        Log.d("TAG", "initView: "+navArgs.galleryModel.cameraVideoPath)
        viewModel.playAudio(MediaItem.fromUri(navArgs.galleryModel.cameraVideoPath),
            onPrepareDone = {
                if (isStarted) {
                    return@playAudio
                }
                isStarted = true
                Log.d("TAG", "initView: ")
                updateSeekBar(viewModel.getPlayer())
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                when (navArgs.galleryModel.videoType) {
                    0 -> {
                        viewModel.playAudio2(MediaItem.fromUri(
                            RawResourceDataSource.buildRawResourceUri(
                                R.raw.momo
                            )
                        ), onEnd = {})
                    }

                    1 -> {
                        viewModel.playAudio2(MediaItem.fromUri(
                            RawResourceDataSource.buildRawResourceUri(
                                R.raw.nun
                            )
                        ), onEnd = {})
                    }

                    2 -> {
                        viewModel.playAudio2(MediaItem.fromUri(
                            RawResourceDataSource.buildRawResourceUri(
                                R.raw.wednesday
                            )
                        ), onEnd = {})
                    }

                    3 -> {
                        viewModel.playAudio2(MediaItem.fromUri(
                            RawResourceDataSource.buildRawResourceUri(
                                R.raw.mommy
                            )
                        ), onEnd = {})
                    }

                    4 -> {
                        viewModel.playAudio2(MediaItem.fromUri(
                            RawResourceDataSource.buildRawResourceUri(
                                R.raw.john_porn
                            )
                        ), onEnd = {})
                    }
                }
            },
            onEnd = {
                isDone = true
                viewModel.pausePlayer()
                viewModel.pausePlayer2()
                job?.cancel()
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            })
        viewModel.pausePlayer2()

        initButton()
    }

    private fun updateSeekBar(player: Player?) {
        player?.let {
            Log.d("TAG", "updateSeekBar: " + it.duration.toInt())
            binding.progressVideo.max = it.duration.toInt()
            job?.cancel()
            job = lifecycleScope.launch(Dispatchers.Main) {
                while (!isPause) {
                    binding.progressVideo.progress = it.currentPosition.toInt()
                    delay(10)
                }
            }

            binding.progressVideo.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    Log.d("TAG", "onStopTrackingTouch: " + binding.progressVideo.progress.toLong())
//                    viewModel.seekTo2(binding.progressVideo.progress.toLong())
//                    viewModel.seekTo1(binding.progressVideo.progress.toLong())
                    viewModel.getPlayer2()?.seekTo(binding.progressVideo.progress.toLong())
                    viewModel.getPlayer()?.seekTo(binding.progressVideo.progress.toLong())

                }

            })
            job?.start()
        }

    }

    private fun initButton() {
        binding.btnBack.clickWithDebounce {
            job?.cancel()
            findNavController().popBackStack()
        }
        binding.btnPlayPause.clickWithDebounce {
            if (viewModel.isPlaying2()) {
                viewModel.pausePlayer()
                viewModel.pausePlayer2()
                isPause = true
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                isPause = false
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                if (!isDone) {
                    viewModel.resumePlayer()
                    viewModel.resumePlayer2()
                } else {
                    viewModel.seekTo1(0)
                    viewModel.seekTo2(0)
                    isDone = false
                }

                updateSeekBar(viewModel.getPlayer())
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
        viewModel.pausePlayer2()
        isPause = true
        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.pausePlayer2()
        viewModel.pausePlayer()
        job?.cancel()
    }

}