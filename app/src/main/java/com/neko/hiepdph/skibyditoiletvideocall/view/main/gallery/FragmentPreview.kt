package com.neko.hiepdph.skibyditoiletvideocall.view.main.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentPreviewBinding
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel


class FragmentPreview : Fragment() {
    private lateinit var binding: FragmentPreviewBinding
    private var mPlayer: ExoPlayer? = null
    private val viewModel by activityViewModels<AppViewModel>()
    private val navArgs by navArgs<FragmentPreviewArgs>()
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
        viewModel.playAudio(MediaItem.fromUri(navArgs.galleryModel.videoPath), onPrepareDone = {
            binding.progressVideo.max = it.toInt()
        }, onEnd = {

        })
        initButton()
    }

    private fun initButton() {
        binding.btnBack.clickWithDebounce {
            findNavController().popBackStack()
        }
        binding.btnPlayPause.clickWithDebounce {
            if (viewModel.isPlaying()) {
                viewModel.pausePlayer()
            } else {
                viewModel.resumePlayer()
            }
        }
    }


}