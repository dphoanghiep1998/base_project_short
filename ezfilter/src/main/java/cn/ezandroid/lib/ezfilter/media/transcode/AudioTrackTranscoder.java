package cn.ezandroid.lib.ezfilter.media.transcode;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;

import cn.ezandroid.lib.ezfilter.media.util.CodecUtil;

/**
 * 音轨转码器
 *
 * @date 2017-08-23
 */
public class AudioTrackTranscoder implements TrackTranscoder {

    private static final int DRAIN_STATE_NONE = 0;
    private static final int DRAIN_STATE_SHOULD_RETRY_IMMEDIATELY = 1;
    private static final int DRAIN_STATE_CONSUMED = 2;

    private final MediaExtractor mExtractor;
    private final QueuedMuxer mMuxer;

    private final int mTrackIndex;
    private final MediaFormat mOutputFormat;

    private final MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private MediaCodec mDecoder;
    private MediaCodec mEncoder;
    private MediaFormat mActualOutputFormat;

    private boolean mIsExtractorEOS;
    private boolean mIsDecoderEOS;
    private boolean mIsEncoderEOS;
    private boolean mDecoderStarted;
    private boolean mEncoderStarted;

    private AudioChannel mAudioChannel;

    public AudioTrackTranscoder(MediaExtractor extractor, int trackIndex, MediaFormat outputFormat, QueuedMuxer muxer) {
        mExtractor = extractor;
        mTrackIndex = trackIndex;
        mOutputFormat = outputFormat;
        mMuxer = muxer;
    }

    @Override
    public void setup() throws IOException {
        mExtractor.selectTrack(mTrackIndex);
        mEncoder = MediaCodec.createEncoderByType(mOutputFormat.getString(MediaFormat.KEY_MIME));
        mEncoder.configure(mOutputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mEncoder.start();
        mEncoderStarted = true;

        final MediaFormat inputFormat = mExtractor.getTrackFormat(mTrackIndex);
        mDecoder = MediaCodec.createDecoderByType(inputFormat.getString(MediaFormat.KEY_MIME));
        mDecoder.configure(inputFormat, null, null, 0);
        mDecoder.start();
        mDecoderStarted = true;

        mAudioChannel = new AudioChannel(mDecoder, mEncoder, mOutputFormat);
    }

    @Override
    public boolean stepPipeline() {
        boolean busy = false;

        int status;
        while (drainEncoder(0) != DRAIN_STATE_NONE) busy = true;
        do {
            status = drainDecoder(0);
            if (status != DRAIN_STATE_NONE) busy = true;
            // NOTE: not repeating to keep from deadlock when encoder is full.
        } while (status == DRAIN_STATE_SHOULD_RETRY_IMMEDIATELY);

        while (mAudioChannel.feedEncoder(0)) busy = true;
        while (drainExtractor(0) != DRAIN_STATE_NONE) busy = true;

        return busy;
    }

    @Override
    public boolean isFinished() {
        return mIsEncoderEOS;
    }

    @Override
    public void release() {
        if (mDecoder != null) {
            if (mDecoderStarted) mDecoder.stop();
            mDecoder.release();
            mDecoder = null;
        }
        if (mEncoder != null) {
            if (mEncoderStarted) mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
    }

    private int drainExtractor(long timeoutUs) {
        if (mIsExtractorEOS) return DRAIN_STATE_NONE;
        int trackIndex = mExtractor.getSampleTrackIndex();
        if (trackIndex >= 0 && trackIndex != mTrackIndex) {
            return DRAIN_STATE_NONE;
        }

        int result = mDecoder.dequeueInputBuffer(timeoutUs);
        if (result < 0) return DRAIN_STATE_NONE;
        if (trackIndex < 0) {
            mIsExtractorEOS = true;
            mDecoder.queueInputBuffer(result, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            return DRAIN_STATE_NONE;
        }

        int sampleSize = mExtractor.readSampleData(CodecUtil.getInputBuffer(mDecoder, result), 0);
        boolean isKeyFrame = (mExtractor.getSampleFlags() & MediaExtractor.SAMPLE_FLAG_SYNC) != 0;
        mDecoder.queueInputBuffer(result, 0, sampleSize, mExtractor.getSampleTime(), isKeyFrame ? MediaCodec.BUFFER_FLAG_SYNC_FRAME : 0);
        mExtractor.advance();
        return DRAIN_STATE_CONSUMED;
    }

    private int drainDecoder(long timeoutUs) {
        if (mIsDecoderEOS) return DRAIN_STATE_NONE;

        int result = mDecoder.dequeueOutputBuffer(mBufferInfo, timeoutUs);
        switch (result) {
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                return DRAIN_STATE_NONE;
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                mAudioChannel.setActualDecodedFormat(mDecoder.getOutputFormat());
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                return DRAIN_STATE_SHOULD_RETRY_IMMEDIATELY;
        }

        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            mIsDecoderEOS = true;
            mAudioChannel.drainDecoderBufferAndQueue(AudioChannel.BUFFER_INDEX_END_OF_STREAM, 0);
        } else if (mBufferInfo.size > 0) {
            mAudioChannel.drainDecoderBufferAndQueue(result, mBufferInfo.presentationTimeUs);
        }

        return DRAIN_STATE_CONSUMED;
    }

    private int drainEncoder(long timeoutUs) {
        if (mIsEncoderEOS) return DRAIN_STATE_NONE;

        int result = mEncoder.dequeueOutputBuffer(mBufferInfo, timeoutUs);
        switch (result) {
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                return DRAIN_STATE_NONE;
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                if (mActualOutputFormat != null) {
                    throw new RuntimeException("Audio output format changed twice.");
                }
                mActualOutputFormat = mEncoder.getOutputFormat();
                mMuxer.setOutputFormat(QueuedMuxer.SampleType.AUDIO, mActualOutputFormat);
                return DRAIN_STATE_SHOULD_RETRY_IMMEDIATELY;
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                return DRAIN_STATE_SHOULD_RETRY_IMMEDIATELY;
        }

        if (mActualOutputFormat == null) {
            throw new RuntimeException("Could not determine actual output format.");
        }

        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            mIsEncoderEOS = true;
            mBufferInfo.set(0, 0, 0, mBufferInfo.flags);
        }
        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            // SPS or PPS, which should be passed by MediaFormat.
            mEncoder.releaseOutputBuffer(result, false);
            return DRAIN_STATE_SHOULD_RETRY_IMMEDIATELY;
        }
        mMuxer.writeSampleData(QueuedMuxer.SampleType.AUDIO, CodecUtil.getOutputBuffer(mEncoder, result), mBufferInfo);
        mEncoder.releaseOutputBuffer(result, false);
        return DRAIN_STATE_CONSUMED;
    }
}
