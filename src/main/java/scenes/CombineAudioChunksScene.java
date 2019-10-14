package main.java.scenes;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import main.java.app.ApplicationFolder;
import main.java.app.AudioFactory;
import main.java.app.DownloadImage;
import main.java.app.SceneType;

import java.io.File;
import java.io.IOException;

public class CombineAudioChunksScene extends ApplicationScene {

    @FXML private Button _homeButton;
    @FXML private Button _nextSceneButton;
    @FXML private Button _quitButton;
    @FXML private Button _addChunkButton;
    @FXML private Button _removeChunkButton;
    @FXML private Button _shiftChunkUp;
    @FXML private Button _shiftChunkDown;
    @FXML private ListView<String> _searchTermsListView;
    @FXML private ListView<String> _audioChunksListView;
    @FXML private ListView<String> _selectedAudioChunksListView;

    AudioFactory _audioFactory;

    @FXML
    public void initialize() {
        _audioFactory = new AudioFactory();

        File[] searchTermList = new File(ApplicationFolder.AudioChunks.getPath()).listFiles();
        for (File searchTermDirectory : searchTermList) {
            _searchTermsListView.getItems().add(searchTermDirectory.getName());
        }
    }

    public void updateAudioChunksList() {
        String searchTermDirectory = _searchTermsListView.getSelectionModel().getSelectedItem();

        if (searchTermDirectory != null) {
            _audioChunksListView.getItems().clear();

            File[] audioChunksList = new File(ApplicationFolder.AudioChunks.getPath() + File.separator + searchTermDirectory + File.separator + "RegularAudioChunks").listFiles();
            for (File audioChunk : audioChunksList) {
                String audioChunkName = audioChunk.getName();
                _audioChunksListView.getItems().add(audioChunkName.substring(0, audioChunkName.length() - 4));
            }
        }
    }

    public void addChunkButtonHandler() {
        String audioChunkToAdd = _audioChunksListView.getSelectionModel().getSelectedItem();

        if (audioChunkToAdd == null) {
            createInformationAlert("No Audio Chunk Selected", "Please select an Audio Chunk");
        } else {
            if (_selectedAudioChunksListView.getItems().contains(audioChunkToAdd)) {
                createInformationAlert("Audio Chunk already added", "Audio Chunk " + audioChunkToAdd + " already added");
            } else {
                int index = _audioChunksListView.getItems().indexOf(audioChunkToAdd);
                _selectedAudioChunksListView.getItems().add(audioChunkToAdd);
                if (index < (_audioChunksListView.getItems().size() - 1)) {
                    _audioChunksListView.getSelectionModel().select(index + 1);
                }
            }
        }
    }

    public void shiftChunkUpButtonHandler() {
        int selectedChunk = _selectedAudioChunksListView.getSelectionModel().getSelectedIndex();
        if (selectedChunk == -1) {
            createInformationAlert("No Audio Chunk selected", "Please select an Audio Chunk");
        } else if (selectedChunk == 0){
            createInformationAlert("Cannot shift chunk up further", "Cannot shift chunk up further");
        } else {
            String audioChunk = _selectedAudioChunksListView.getItems().get(selectedChunk);
            _selectedAudioChunksListView.getItems().remove(selectedChunk);
            _selectedAudioChunksListView.getItems().add((selectedChunk - 1), audioChunk);
            _selectedAudioChunksListView.getSelectionModel().select(selectedChunk - 1);
        }
    }

    public void shiftChunkDownButtonHandler() {
        int selectedChunk = _selectedAudioChunksListView.getSelectionModel().getSelectedIndex();
        if (selectedChunk == -1) {
            createInformationAlert("No Audio Chunk selected", "Please select an Audio Chunk");
        } else if (selectedChunk == (_selectedAudioChunksListView.getItems().size() - 1)){
            createInformationAlert("Cannot shift chunk down further", "Cannot shift chunk down further");
        } else {
            String audioChunk = _selectedAudioChunksListView.getItems().get(selectedChunk);
            _selectedAudioChunksListView.getItems().remove(selectedChunk);
            _selectedAudioChunksListView.getItems().add((selectedChunk + 1), audioChunk);
            _selectedAudioChunksListView.getSelectionModel().select(selectedChunk + 1);
        }
    }

    public void removeChunkButtonHandler() {
        String audioChunkToRemove = _selectedAudioChunksListView.getSelectionModel().getSelectedItem();

        if (audioChunkToRemove == null) {
            createInformationAlert("No Audio Chunk Selected", "Please select an Audio Chunk");
        } else {
            int index = _selectedAudioChunksListView.getItems().indexOf(audioChunkToRemove);
            _selectedAudioChunksListView.getItems().remove(audioChunkToRemove);
            if (index > 0) {
                _selectedAudioChunksListView.getSelectionModel().select(index - 1);
            }
        }
    }

    public void homeButtonHandler(ActionEvent event) throws IOException {
        if (_selectedAudioChunksListView.getItems().size() != 0) {
            Alert returnToMenuAlert = createConfirmationAlert("Are you sure you want to quit to the main menu? All progress will be lost.");

            if (returnToMenuAlert.getResult() == ButtonType.YES) {
                changeScene(SceneType.MainMenuScene, event);
            }
        } else {
            changeScene(SceneType.MainMenuScene, event);
        }
    }

    public void quitButtonHandler() {
        if (_selectedAudioChunksListView.getItems().size() != 0) {
            createQuitAlert();
        } else {
            System.exit(0);
        }
    }

    public void nextSceneButtonHandler(ActionEvent event) throws IOException {
        String searchTerm = _searchTermsListView.getSelectionModel().getSelectedItem();

        if (_selectedAudioChunksListView.getItems().size() == 0) {
            createInformationAlert("No Audio Chunks selected", "Please select Audio Chunks to combine for creation");
        } else {
            _nextSceneButton.setDisable(true);

            new Thread( new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    String audioChunksString = "";

                    for (String audioChunkString : _selectedAudioChunksListView.getItems()) {
                        audioChunksString += audioChunkString + " ";
                    }

                    _audioFactory.combineAudioChunks(searchTerm, audioChunksString);

                    DownloadImage downloadImage = new DownloadImage(searchTerm,10);
                    downloadImage.run();
                    return null;
                }

                @Override
                protected void done() {
                    Platform.runLater( () -> {
                        try {
                            _nextSceneButton.setDisable(true);

                            ApplicationScene selectImagesSceneController = changeScene(SceneType.SelectImagesScene, event);
                            ((SelectImagesScene) selectImagesSceneController).setSearchTerm(searchTerm);
                        } catch (Exception e) {
                            System.out.println("Error changing to SelectImagesScene");
                        }
                    });
                }
            }).start();
        }
    }
}
