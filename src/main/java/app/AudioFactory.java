package main.java.app;

import java.util.List;

public class AudioFactory {

    public void previewAudioChunk(String audioChunk, VoiceSynthesizerType voiceSynthesizerType) {

        String[] createAudioCommands = { "/bin/bash", "-c", "espeak " + voiceSynthesizerType.getFlag() + " " + audioChunk };
        ProcessBuilder createAudioBuilder = new ProcessBuilder(createAudioCommands);

        try {
            createAudioBuilder.start();
        } catch (Exception e) {
            System.out.print("Error previewing audio chunk");
        }
    }

    public void saveAudioChunk(String audioChunk, VoiceSynthesizerType voiceSynthesizerType, String searchTerm, String chunkName) {

    }

    public void combineAudioChunks(List<String> audioChunksList) {

    }

}
