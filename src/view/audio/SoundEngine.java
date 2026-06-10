package view.audio;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundEngine {
    private final Map<String, AudioClip> allSounds = new HashMap<>();
    private final List<AudioClip> playingClips = new ArrayList<>();
    boolean enabled;

    static private SoundEngine engine;

    private SoundEngine(boolean enabled) {
        this.enabled = enabled;
    }

    public static void initEngine(){
        try{
            // test create media
            Media media = new Media(Paths.get("assets/audio/grass1.mp3").toUri().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }
        catch(MediaException e){
            IO.println("Failed to load sample file. Disabling all sounds.");
            IO.println(e.getMessage());
            engine = new SoundEngine(false);
        }
        engine = new SoundEngine(true);
    }

    public static SoundEngine getEngine(){
        if(engine == null){
            initEngine();
        }
        return engine;
    }

    public void registerSound(String eventName, Path filePath){
        if(!enabled) return;
        try {
            AudioClip sound = new AudioClip(filePath.toUri().toString());
            allSounds.put(eventName, sound);
            IO.println("Audio loaded for " + eventName);
        }
        catch(MediaException e){
            IO.println("Failed to load file " + filePath + " while register sound event " + eventName);
        }

    }

    public void playSound(String eventName){
        if(!enabled) return;
        playingClips.removeIf(audioClip -> !audioClip.isPlaying());
        if(!allSounds.containsKey(eventName)){
            IO.println("Sound event " + eventName + " unregistered");
        }
        AudioClip clip = allSounds.get(eventName);
        if(!playingClips.contains(clip)){
            playingClips.add(clip);
            clip.play();
        }
    }

}
