package com.android.fisher.sgface.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;

public class MessageAduioRecord {
    private MediaRecorder mr;

    public Context context;

    public File tmpFile = null;

    public long timeLong = 0;

    private boolean isRecording = false;
    public static MessageAduioRecord messageAduioRecord;

    public static MessageAduioRecord getInstance() {
        if (null == messageAduioRecord)
            messageAduioRecord = new MessageAduioRecord();
        return messageAduioRecord;
    }

    public File getTempFile() {
        return tmpFile;
    }

    public synchronized void start(Context c) {
        if (!isRecording) {
            isRecording = true;
            if (mr == null) {
                mr = new MediaRecorder();
            }
             this.context = c;
        audioInput();
        }
    }

    @SuppressLint("InlinedApi")
    public void audioInput() {
        tmpFile = getTempWavFile();
        if (tmpFile != null) {
            try {
                mr.setAudioSource(MediaRecorder.AudioSource.MIC);
                mr.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                mr.setOutputFile(tmpFile.getPath());
                mr.prepare();
                mr.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void stopRecording() {
        try {

            if (null != mr) {
                 mr.stop();
                mr.reset();
                mr.release();
                mr = null;
           //     mr.stop();
       //         mr.reset(); // You can reuse the object by going back to
                // setAudioSource() step
                // mr.release(); // Now the object cannot be reused

                isRecording = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doCancel() {
        ToolUtil.print("tmpFile:" + tmpFile);
        if (null != tmpFile) {
            tmpFile.delete();
            tmpFile = null;
        }
        stopRecording();
    }

    public void getaudio() {
        if (tmpFile != null) {
            MediaPlayer mp = MediaPlayer.create(context,
                    Uri.parse(tmpFile.getPath()));
            int duration = mp.getDuration();
            BigDecimal i = new BigDecimal(duration / 1000).setScale(0,
                    BigDecimal.ROUND_HALF_UP);
            timeLong = i.intValue();
        }
    }

    public static synchronized File getTempWavFile() {
    //    String tempPathString = AudioFileFunc.getAMRFilePath();
        String tempPathString =FileUtil.getCacheFilePath();
          Log.i("temPathString", tempPathString);
        File rootDir = new File(tempPathString);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
        File tempFile = null;
        if (tempPathString != null) {
            tempFile = new File(tempPathString, "newmessagewav"
                    + Calendar.getInstance(Locale.getDefault())
                    .getTimeInMillis() + ".wav");
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return tempFile;
    }
}
