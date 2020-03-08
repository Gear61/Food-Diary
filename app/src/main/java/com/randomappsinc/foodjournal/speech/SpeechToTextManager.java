package com.randomappsinc.foodjournal.speech;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpeechToTextManager implements RecognitionListener, DialogInterface.OnDismissListener {

    public interface StringFormatter {
        String formatString(String input);
    }

    public interface Listener {
        void onTextSpoken(String spokenText);
    }

    private static final StringFormatter EMPTY_FORMATTER = input -> input;

    @BindView(R.id.voice_icon) TextView voiceIcon;
    @BindView(R.id.message) TextView message;
    @BindView(R.id.try_again) View tryAgain;

    private Context context;

    // This is lazily instantiated and is also nulled out when the user dismisses the prompt without speaking
    private @Nullable SpeechRecognizer speechRecognizer;

    private Intent speechRecognizerIntent;
    private Listener listener;
    private MaterialDialog dialog;
    private @StringRes int listeningPrompt;
    private StringFormatter stringFormatter = EMPTY_FORMATTER;

    public SpeechToTextManager(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().getLanguage());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.speech_to_text_dialog, false)
                .build();
        dialog.getWindow().getAttributes().windowAnimations = R.style.speech_dialog_animation;
        dialog.setOnDismissListener(this);
        ButterKnife.bind(this, dialog.getCustomView());
    }

    public void setListeningPrompt(@StringRes int listeningPrompt) {
        this.listeningPrompt = listeningPrompt;
    }

    public void setStringFormatter(@Nullable StringFormatter stringFormatter) {
        this.stringFormatter = stringFormatter == null ? EMPTY_FORMATTER : stringFormatter;
    }

    public void startSpeechToTextFlow() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(this);
        speechRecognizer.startListening(speechRecognizerIntent);
        changeUIStateToListening();
    }

    @Override
    public void onBeginningOfSpeech() {}

    @Override
    public void onBufferReceived(byte[] buffer) {}

    @Override
    public void onEndOfSpeech() {
        speechRecognizer.stopListening();
    }

    @Override
    public void onError(int error) {
        changeUIStateToRetry();
    }

    @Override
    public void onEvent(int eventType, Bundle params) {}

    @Override
    public void onPartialResults(Bundle partialResults) {
        List<String> data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (data != null && !data.isEmpty()) {
            String latestPartialTranscription = data.get(data.size() - 1);
            message.setText(stringFormatter.formatString(latestPartialTranscription));
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {}

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        speechRecognizer.destroy();
        speechRecognizer = null;
    }

    @Override
    public void onResults(Bundle results) {
        List<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && !matches.isEmpty()) {
            String finalTranscription = stringFormatter.formatString(matches.get(0));
            message.setText(finalTranscription);
            listener.onTextSpoken(finalTranscription);
            dialog.dismiss();
        } else {
            changeUIStateToRetry();
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {}

    private void changeUIStateToListening() {
        voiceIcon.setText(R.string.speech_to_text_listening_mic_icon);
        voiceIcon.setBackgroundResource(R.drawable.filled_blue_circle);
        message.setText(listeningPrompt);
        tryAgain.setVisibility(View.GONE);
    }

    private void changeUIStateToRetry() {
        voiceIcon.setText(R.string.speech_to_text_retry_mic_icon);
        voiceIcon.setBackgroundResource(R.drawable.red_ring_background);
        message.setText(R.string.did_not_catch_speech);
        tryAgain.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.try_again)
    void tryAgain() {
        startSpeechToTextFlow();
    }

    public void cleanUp() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        context = null;
    }
}
