package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.Constraint;

/**
 * Created by brianzhao on 10/27/15.
 */
@Entity
public class YoutubeVideo extends Model {
    @Id
    public Long id;

    @Constraints.Required
    @Column(columnDefinition = "VARCHAR(32)")
    public String videoId;

    @Constraints.Required
    @Column(columnDefinition = "LONGTEXT")
    public String transcript;

    @Constraints.Required
//    @Column(columnDefinition = "VARCHAR(128)")
    public String title;

    public YoutubeVideo(String videoId, String transcript, String title) {
        this.videoId = videoId;
        this.transcript = transcript;
        this.title = title;
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
