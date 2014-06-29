package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity
public class SimpleGraph extends Model {

    @Id
    public Long id;

    public Long from_person;
    public Long to_person;
    public Long weight;

    public String from_name;
    public String to_name;

    public Long job_id;

    public static Finder find = new Finder(Long.class, SimpleGraph.class);


}