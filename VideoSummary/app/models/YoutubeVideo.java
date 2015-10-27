package models;

import com.avaje.ebean.Model;
import java.util.*;
import javax.persistence.*;

import com.avaje.ebean.Model;
import play.data.format.*;
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
    public String summary;


    public static Finder<Long, YoutubeVideo> find = new Finder<Long,YoutubeVideo>(YoutubeVideo.class);
}
