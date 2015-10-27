package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

import play.data.validation.*;

/**
 * Created by brianzhao on 10/27/15.
 */
@Entity
public class YoutubeVideo extends Model {
    @Id
    public Long id;

    @Constraints.Required
    @Column
    public String videoId;


    @Column(columnDefinition = "LONGTEXT")
    public String transcript;

    public YoutubeVideo(String videoId) {
        this.videoId = videoId;
    }

    public YoutubeVideo(String videoId, String transcript) {
        this.videoId = videoId;
        this.transcript = transcript;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public static Finder<Long, YoutubeVideo> find = new Finder<Long,YoutubeVideo>(YoutubeVideo.class);
}
