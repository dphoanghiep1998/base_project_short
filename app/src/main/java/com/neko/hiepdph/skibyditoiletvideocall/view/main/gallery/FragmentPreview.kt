package com.neko.hiepdph.skibyditoiletvideocall.view.main.gallery

import android.os.Bundle
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
import com.google.android.exoplayer2.ExoPlayer
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
    private var mPlayer: ExoPlayer? = null
    private var mPlayer2: ExoPlayer? = null
    private val viewModel by activityViewModels<AppViewModel>()
    private val navArgs by navArgs<FragmentPreviewArgs>()
    private var job: Job? = null
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

        if (viewModel.getPlayer2() == null) {
            mPlayer2 = ExoPlayer.Builder(requireContext()).setSeekForwardIncrementMs(15000).build()
            binding.cameraView.apply {
                player = mPlayer2
                keepScreenOn = true

            }
            viewModel.setupPlayer2(mPlayer2!!)
        } else {
            binding.cameraView.apply {
                player = viewModel.getPlayer2()
                keepScreenOn = true

            }
        }
        viewModel.playAudio(MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(navArgs.galleryModel.videoPath)),
            onPrepareDone = {

            },
            onEnd = {

            })

        viewModel.playAudio2(MediaItem.fromUri(navArgs.galleryModel.cameraVideoPath),
            onPrepareDone = {
                updateSeekBar(viewModel.getPlayer2())
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            },
            onEnd = {
                viewModel.pausePlayer()
                job?.cancel()
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            })

        initButton()
    }

    private fun updateSeekBar(player: Player?) {
        player?.let {
            binding.progressVideo.max = it.duration.toInt()
            job?.cancel()
            job = lifecycleScope.launch(Dispatchers.Main) {
                while (true) {
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
                    viewModel.seekTo2(binding.progressVideo.progress.toLong())
                    viewModel.seekTo1(binding.progressVideo.progress.toLong())
                }

            })
        }

    }

    private fun initButton() {
        binding.btnBack.clickWithDebounce {
            job?.cancel()
            findNavController().popBackStack()
        }
        binding.btnPlayPause.clickWithDebounce {
            if (viewModel.isPlaying()) {
                viewModel.pausePlayer()
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                viewModel.resumePlayer()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.pausePlayer2()
        viewModel.pausePlayer()
        job?.cancel()
    }

}