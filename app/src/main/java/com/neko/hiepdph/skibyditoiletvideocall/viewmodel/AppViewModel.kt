package com.neko.hiepdph.skibyditoiletvideocall.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.neko.hiepdph.skibyditoiletvideocall.R
import com.neko.hiepdph.skibyditoiletvideocall.common.ConnectionType
import com.neko.hiepdph.skibyditoiletvideocall.data.MonsterModel

class AppViewModel : ViewModel() {
    private var _player: Player? = null
    private var playerListener: Player.Listener? = null
    private var playerListener1: Player.Listener? = null
    private var isLoadingVideo = MutableLiveData(false)
    var typeNetwork = MutableLiveData(ConnectionType.WIFI)
    private var isConnectivityChanged: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>()
    val mConnectivityChanged: LiveData<Boolean>
        get() = isConnectivityChanged


    val data = mutableListOf(
        MonsterModel(
            0, R.drawable.ic_1, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_1.mp4"
        ), MonsterModel(
            1, R.drawable.ic_2, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_2.mp4"
        ), MonsterModel(
            2, R.drawable.ic_3, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_3.mp4"
        ), "ads", MonsterModel(
            4, R.drawable.ic_4, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_4.mp4"
        ), MonsterModel(
            5, R.drawable.ic_5, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_5.mp4"
        ), MonsterModel(
            6, R.drawable.ic_6, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_6.mp4"
        ), MonsterModel(
            7, R.drawable.ic_7, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_7.mp4"
        ), MonsterModel(
            8, R.drawable.ic_8, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_8.mp4"
        ), MonsterModel(
            9, R.drawable.ic_9, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_9.mp4"
        ), MonsterModel(
            10, R.drawable.ic_10, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_10.mp4"
        ), MonsterModel(
            11, R.drawable.ic_11, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_11.mp4",true
        ), MonsterModel(
            12, R.drawable.ic_12, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_12.mp4"
        ), MonsterModel(
            13, R.drawable.ic_13, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_13.mp4"
        ), MonsterModel(
            14, R.drawable.ic_14, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_14.mp4"
        ), MonsterModel(
            15, R.drawable.ic_15, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_15.mp4"
        ), MonsterModel(
            16, R.drawable.ic_16, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_16.mp4"
        ), MonsterModel(
            17, R.drawable.ic_17, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_17.mp4"
        ), MonsterModel(
            18, R.drawable.ic_18, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_18.mp4",true
        ), MonsterModel(
            19, R.drawable.ic_19, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_19.mp4"
        ), MonsterModel(
            20, R.drawable.ic_20, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_20.mp4",true
        ), MonsterModel(
            21, R.drawable.ic_21, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_21.mp4"
        ), MonsterModel(
            22, R.drawable.ic_22, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_22.mp4"
        ), MonsterModel(
            23, R.drawable.ic_23, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_23.mp4"
        ), MonsterModel(
            24, R.drawable.ic_24, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_24.mp4"
        ), MonsterModel(
            25, R.drawable.ic_25, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_25.mp4"
        ), MonsterModel(
            26, R.drawable.ic_26, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_26.mp4"
        ), MonsterModel(
            27, R.drawable.ic_27, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_27.mp4",true
        ), MonsterModel(
            28, R.drawable.ic_28, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_28.mp4"
        ), MonsterModel(
            29, R.drawable.ic_29, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_29.mp4"
        ), MonsterModel(
            30, R.drawable.ic_30, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_30.mp4",true
        ), MonsterModel(
            31, R.drawable.ic_31, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_31.mp4"
        ), MonsterModel(
            32, R.drawable.ic_32, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_32.mp4"
        ), MonsterModel(
            33, R.drawable.ic_33, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_33.mp4"
        ), MonsterModel(
            34, R.drawable.ic_34, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_34.mp4",true
        ), MonsterModel(
            35, R.drawable.ic_35, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_35.mp4"
        ), MonsterModel(
            36, R.drawable.ic_36, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_36.mp4"
        ), "ads", MonsterModel(
            38, R.drawable.ic_37, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_37.mp4"
        ), MonsterModel(
            39, R.drawable.ic_38, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_38.mp4"
        ), MonsterModel(
            40, R.drawable.ic_39, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_39.mp4",true
        ), MonsterModel(
            41, R.drawable.ic_40, "https://github.com/ConfigNeko/FakeWC/raw/main/lv_40.mp4"
        )
    )

    private var currentModel: MonsterModel = data[0] as MonsterModel

    fun getCurrentModel(): MonsterModel {
        return currentModel
    }

    fun seekTo(position: Int) {
        _player?.seekTo(position, 0)
    }

    fun setCurrentModel(monsterModel: MonsterModel) {
        currentModel = monsterModel
    }

    fun setupPlayer(player: Player) {
        if (_player == null) {
            _player = player

        }
    }

    fun getPlayer(): Player? {
        return _player
    }

    fun reloadVideo() {
        _player?.seekTo(0)
    }


    fun isPlaying(): Boolean {
        return _player?.isPlaying == true
    }

    fun setMediaItemList(list: List<MediaItem>) {
        _player?.setMediaItems(list)
    }

    fun setMediaItem(mediaItem: MediaItem) {
        _player?.setMediaItem(mediaItem)
    }

    fun playAudio(mediaItem: MediaItem, onEnd: () -> Unit) {

        try {
            _player?.repeatMode = Player.REPEAT_MODE_OFF
            playerListener1?.let { _player?.removeListener(it) }
            playerListener?.let { _player?.removeListener(it) }
            _player?.stop()

            playerListener = object : Player.Listener {
                override fun onIsLoadingChanged(isLoading: Boolean) {
                    super.onIsLoadingChanged(isLoading)
                    isLoadingVideo.postValue(isLoading)
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)


                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    _player?.stop()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                        onEnd()
                    }
                }
            }
            _player?.setMediaItem(mediaItem)
            _player?.addListener(playerListener!!)
            _player?.prepare()
            _player?.playWhenReady = true

        } catch (e: Exception) {
        }

    }


    fun resetPlayer() {
        playerListener?.let { _player?.removeListener(it) }
        playerListener1?.let { _player?.removeListener(it) }
        if (_player?.isPlaying == true || _player?.isLoading == true) {
            _player?.stop()
        }
        _player?.clearMediaItems()

    }

    fun pausePlayer() {
        _player?.pause()
        playerListener?.let { _player?.removeListener(it) }
        playerListener1?.let { _player?.removeListener(it) }
    }

    fun resetAll() {
        if (_player?.isPlaying == true || _player?.isLoading == true) {
            _player?.stop()
        }
        _player?.apply {
            playWhenReady = false
            playbackState
        }
        _player?.release()
        _player = null

    }

    fun addIsConnectivityChangedSource(data: LiveData<Boolean>) {
        isConnectivityChanged.addSource(data, isConnectivityChanged::setValue)
    }

    fun removeIsConnectivityChangedSource(data: LiveData<Boolean>) {
        isConnectivityChanged.removeSource(data)
    }

}