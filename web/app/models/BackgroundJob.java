package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity
public class BackgroundJob extends Model {

    public BackgroundJob(){
        finished = false;
    }

    @Id
    public Long id;

    public Boolean finished;
    public String category;

    @Column(columnDefinition = "TEXT")
    public String report;

    public static Finder find = new Finder(Long.class, BackgroundJob.class);


}