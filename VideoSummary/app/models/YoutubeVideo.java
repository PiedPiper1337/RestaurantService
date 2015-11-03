package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by brianzhao on 10/27/15.
 */
@Entity
public class YoutubeVideo extends Model {
    @Id
    public Long id;

    @Constraints.Required
    @Column(columnDefinition = "VARCHAR(30)")
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

    public static Finder<Long, YoutubeVideo> find = new Finder<Long, YoutubeVideo>(YoutubeVideo.class);
}
