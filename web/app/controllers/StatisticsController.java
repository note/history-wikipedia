package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.stats;
import models.BackgroundJob;
import com.avaje.ebean.Ebean;

/**
 * Created by michal on 23/06/14.
 */
public class StatisticsController extends Controller {
    public static Result basicStatistics(Long id){
        BackgroundJob job =  Ebean.find(BackgroundJob.class, id);

        return ok(stats.render(job.report));
    }


}
