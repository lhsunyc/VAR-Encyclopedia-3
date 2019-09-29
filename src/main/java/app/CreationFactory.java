package main.java.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CreationFactory {

    public void combineImagesToVideo(String imageNames, int numberOfImages) {
        String getDurationCommand = "soxi -D ./VAR-Encyclopedia/.temp/tempCombinedChunks.wav";
        ProcessBuilder getDuration = new ProcessBuilder("bash", "-c", getDurationCommand);

        try{
            Process getDurationProcess = getDuration.start();
            BufferedReader stdout = new BufferedReader(new InputStreamReader(getDurationProcess.getInputStream()));
            getDurationProcess.waitFor();

            double duration = Double.parseDouble(stdout.readLine());
//            ffmpeg -y -framerate $((${1}/${2})) -pattern_type glob -i './downloads/*.jpg' -c:v libx264 -vf fps=25 -pix_fmt yuv420p combinedImages.mp4

//            String combineImageFile = "./src/main/resources/shellscripts/combineImageFile.sh " + duration + " " + numberOfImages;
            //System.out.println(imageNames);

            System.out.println(numberOfImages);
            System.out.println(duration);

            String combineImageFile = "cat " + imageNames + " | " + "ffmpeg -y -framerate "+ numberOfImages/duration + " -i -"+ " -c:v libx264 -vf fps=25 -pix_fmt yuv420p ./VAR-Encyclopedia/.temp/combinedImages.mp4";
            ProcessBuilder combineImagesToVideoBuilder = new ProcessBuilder("bash", "-c", combineImageFile);

            Process combineImagesToVideoProcess = combineImagesToVideoBuilder.start();
            int exitStatus = combineImagesToVideoProcess.waitFor();
            System.out.println(exitStatus);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error combing image to video");
        }

    }

    public void combineVideoAndText(String searchTerm) {
//        ffmpeg -i "combinedImages.mp4" -vf drawtext="fontfile=/path/to/font.ttf: text='${1}': fontcolor=white: fontsize=24: box=1: boxcolor=black@0.5: boxborderw=5: x=(w-text_w)/2: y=(h-text_h)/2" -codec:a copy combinedVideo.mp4

//        String addTermToFile = "./src/main/resources/shellscripts/addTermToFile.sh " + searchTerm;
        String addTermToFile = "ffmpeg -y -i \"./VAR-Encyclopedia/.temp/combinedImages.mp4\" -vf drawtext=\"fontfile=/path/to/font.ttf: text="+searchTerm+": fontcolor=white: fontsize=24: box=1: boxcolor=black@0.5: boxborderw=5: x=(w-text_w)/2: y=(h-text_h)/2\" -codec:a copy ./VAR-Encyclopedia/.temp/combinedVideo.mp4" ;
        ProcessBuilder combineVideoAndTextBuilder = new ProcessBuilder("bash", "-c", addTermToFile);
        try{
            Process combineVideoAndTextProcess = combineVideoAndTextBuilder.start();
            int exitStatus = combineVideoAndTextProcess.waitFor();
            System.out.println(exitStatus);
        } catch (Exception e) {
            System.out.println("Error combing Video and Text");
        }

    }

    public void combineVideoAndAudio(String nameOfCreation) {
//        ffmpeg -i combinedVideo.mp4 -i "./VAR-Encyclopedia/.temp/tempCombinedChunks.wav" -c:v copy -c:a aac -strict experimental "./VAR-Encyclopedia/Creations/${1}.mp4"
//        String combineVideoAndAudioCommand = "./src/main/resources/shellscripts/combine.sh " + nameOfCreation;
        String combineVideoAndAudioCommand = "ffmpeg -y -i ./VAR-Encyclopedia/.temp/combinedVideo.mp4 -i ./VAR-Encyclopedia/.temp/tempCombinedChunks.wav -c:a aac -strict experimental ./VAR-Encyclopedia/Creations/"+nameOfCreation+".mp4";
        ProcessBuilder combineVideoAndAudioBuilder = new ProcessBuilder("bash", "-c", combineVideoAndAudioCommand);
        try{
            Process combineVideoAndAudioProcess = combineVideoAndAudioBuilder.start();
            combineVideoAndAudioProcess.waitFor();
        } catch (Exception e) {
            System.out.print("Error combing Video and Audio");
        }

    }
    public void deleteCreation(String creationName) {
        String[] deleteCreationCommands = { "sh", "-c", "./src/main/resources/shellscripts/deleteCreation.sh" + " \"" + creationName +"\"" };
        ProcessBuilder deleteCreationBuilder = new ProcessBuilder(deleteCreationCommands);

        try {
            Process deleteCreationProcess = deleteCreationBuilder.start();
            deleteCreationProcess.waitFor();
        } catch (Exception e) {
            System.out.print("Error playing audio chunk");
        }
    }
}
