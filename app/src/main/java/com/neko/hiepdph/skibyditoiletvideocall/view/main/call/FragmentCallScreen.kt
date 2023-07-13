package com.neko.hiepdph.skibyditoiletvideocall.view.main.call

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.clickWithDebounce
import com.neko.hiepdph.skibyditoiletvideocall.common.navigateToPage
import com.neko.hiepdph.skibyditoiletvideocall.data.model.OtherCallModel
import com.neko.hiepdph.skibyditoiletvideocall.databinding.FragmentCallScreenBinding
import com.neko.hiepdph.skibyditoiletvideocall.viewmodel.AppViewModel

class FragmentCallScreen : Fragment() {
    private lateinit var binding: FragmentCallScreenBinding
    private var action: (() -> Unit)? = null
    private val viewModel by activityViewModels<AppViewModel>()
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var mPlayer: Player? = null
    private val args by navArgs<FragmentCallScreenArgs>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCallScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        action = {
            if (args == null && args.characterModel == null) {
                val direction =
                    FragmentCallScreenDirections.actionFragmentCallScreenToFragmentScreenAccept(
                        OtherCallModel(
                            0,
                            R.drawable.ic_banner_progress_call,
                            "Skibidi Toilet",
                            R.raw.john_porn,
                            4
                        )
                    )
                findNavController().navigate(direction)
            } else {

                val direction =
                    FragmentCallScreenDirections.actionFragmentCallScreenToFragmentScreenAccept(
                        args.characterModel
                    )
                findNavController().navigate(direction)
            }

        }
        if (args != null && args.characterModel != null) {
            binding.title.text = args.characterModel.name
            binding.imvBanner.setImageResource(args.characterModel.circleImage)
        }

        setupSound()
    }

    private fun setupSound() {
        if (viewModel.getPlayer() == null) {
            mPlayer = ExoPlayer.Builder(requireContext()).setSeekForwardIncrementMs(15000).build()
            viewModel.setupPlayer(mPlayer!!)
        }
        val mediaItem =
            MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.sound_phone_ring))
        runnable = Runnable {
            viewModel.pausePlayer()
            navigateToPage(R.id.fragmentCallScreen, R.id.fragmentCallDecline)
        }
        viewModel.playAudio(mediaItem, onEnd = {}, repeat = Player.REPEAT_MODE_ONE)
        handler = Handler(Looper.getMainLooper())
        handler?.postDelayed(runnable!!, 30000)
    }

    private fun initView() {
        initButton()
    }

    private fun initButton() {
        binding.imvAccept.clickWithDebounce {
            checkPermission(action)
        }

        binding.imvDecline.clickWithDebounce {
            navigateToPage(R.id.fragmentCallScreen, R.id.fragmentCallDecline)
        }
    }

    private fun checkPermission(action: (() -> Unit)? = null) {

        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || requireContext().checkSelfPermission(
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("TAG", "checkPermission: true")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.CAMERA
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.RECORD_AUDIO
                )
            ) {
                cameraLauncher.launch(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireActivity().packageName, null)
                    )
                )
            } else {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
                    )
                )
            }
        } else {
            Log.d("TAG", "checkPermission: false")
            action?.invoke()
        }
    }


    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && requireContext().checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                action?.invoke()
            }
        }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && requireContext().checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                action?.invoke()
            }
        }


    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onStop() {
        super.onStop()
        viewModel.pausePlayer()
    }

}