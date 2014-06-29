package controllers;

import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import play.data.Form;
import models.JobFormData;
import models.BackgroundJob;
import models.Article;

import views.html.jobs.form;
import views.html.jobs.show;
import com.avaje.ebean.Ebean;

import play.libs.Akka;

import java.lang.Runnable;
import java.util.List;

import java.util.concurrent.TimeUnit;
import scala.concurrent.duration.Duration;
import models.GraphCreator;


public class JobsController extends Controller {
    public static Result form() {
        List<BackgroundJob> allJobs = Ebean.find(BackgroundJob.class).findList();
        return ok(form.render(allJobs));
    }


    public static Result createJob(){
        Form<JobFormData> formData = Form.form(JobFormData.class).bindFromRequest();
        BackgroundJob job = new BackgroundJob();
        job.category = formData.get().category;
        job.save();

        final Long jobId = job.id;

        Akka.system().scheduler().scheduleOnce(
                Duration.create(0, TimeUnit.SECONDS),
                new GraphCreator(job.id)
        , Akka.system().dispatcher());

        return redirect("/jobs/" + jobId);
    }

    public static Result show(Long id){

        BackgroundJob job =  Ebean.find(BackgroundJob.class, id);

        return ok(show.render(job));
    }
}