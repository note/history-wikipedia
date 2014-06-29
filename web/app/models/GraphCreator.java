package models;

import com.avaje.ebean.Ebean;

import java.io.File;
import java.util.List;
import models.*;

import play.Play;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.database.drivers.MySQLDriver;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.plugin.database.EdgeListDatabaseImpl;
import org.gephi.io.importer.plugin.database.ImporterEdgeList;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.*;
import org.gephi.preview.api.*;
import org.gephi.statistics.spi.Statistics;
import org.openide.util.Lookup;
import org.gephi.io.exporter.preview.PNGExporter;
import org.gephi.io.exporter.api.ExportController;
import java.io.FileOutputStream;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingold;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingController;
import org.gephi.ranking.api.Transformer;
import org.gephi.ranking.plugin.transformer.AbstractColorTransformer;
import org.gephi.ranking.plugin.transformer.AbstractSizeTransformer;
import java.awt.Color;

public class GraphCreator implements Runnable {
    Long id;
    BackgroundJob job;

    public GraphCreator(Long id){
        this.id = id;
    }

    public void run() {
        job =  Ebean.find(BackgroundJob.class, id);
        createDbEntries();
        createGephiProject();
        saveProjectFiles();

        job.finished = true;
        job.save();

    }

    void createDbEntries(){
        List<Article> articles = Article.find(job.category);

        for(Article fromArticle : articles){
            for(Article toArticle: articles){
                if(! fromArticle.title.equals(toArticle.title)){
                    String fixedTitle = toArticle.title.replace("_", " ");

                    int lastIndex = 0;
                    int count =0;

                    while(lastIndex != -1){

                        lastIndex = fromArticle.text.indexOf(fixedTitle,lastIndex);

                        if( lastIndex != -1){
                            count ++;
                            lastIndex+=fixedTitle.length();
                        }
                    }

                    if(count != 0) {
                        SimpleGraph graph = new SimpleGraph();
                        graph.from_person = fromArticle.id;
                        graph.from_name = fromArticle.title;
                        graph.to_person = toArticle.id;
                        graph.to_name = toArticle.title;
                        graph.weight = new Long(count);
                        ;
                        graph.job_id = new Long(id);
                        graph.save();
                    }

                }
            }
        }
    }

    ProjectController pc;
    GraphModel graphModel;

    void createGephiProject() {
        // Init a project - and therefore a workspace
        pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();


        //Get controllers and models
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);

        //Import database
        EdgeListDatabaseImpl db = new EdgeListDatabaseImpl();
        db.setDBName(Play.application().configuration().getString("wikidb.dbName"));
        db.setHost(Play.application().configuration().getString("wikidb.host"));
        db.setUsername(Play.application().configuration().getString("wikidb.user"));
        db.setPasswd(Play.application().configuration().getString("wikidb.password"));
        db.setSQLDriver(new MySQLDriver());
        db.setPort(3306);
        db.setNodeQuery("select from_name as id, from_name as label from simple_graph " + "where job_id = " + id  + " group by from_person");
        db.setEdgeQuery("select from_name as source, to_name as target, weight as weight from simple_graph where weight > 0 and job_id = " + id);
        ImporterEdgeList edgeListImporter = new ImporterEdgeList();
        Container container = importController.importDatabase(db, edgeListImporter);
        container.setAllowAutoNode(false);      //Don't create missing nodes
        container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        FruchtermanReingold layout = new FruchtermanReingold(null);
        graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        layout.setGraphModel(graphModel);

        layout.initAlgo();
        layout.resetPropertiesValues();

        for (int i = 0; i < 10000 && layout.canAlgo(); i++) {
            layout.goAlgo();
        }
        layout.endAlgo();

        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewProperties properties = previewController.getModel().getProperties();

        properties.getProperty(PreviewProperty.SHOW_NODE_LABELS).setValue(true);

        RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);
        Ranking degreeRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, Ranking.DEGREE_RANKING);
        AbstractColorTransformer colorTransformer = (AbstractColorTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_COLOR);
        colorTransformer.setColors(new Color[]{new Color(0xFEF0D9), new Color(0xB30000)});
        rankingController.transform(degreeRanking, colorTransformer);
    }


    void saveProjectFiles(){
        pc.saveProject(pc.getCurrentProject(), new File(Play.application().path() + "/public/reports/report" + id + ".gephi")).run();


        job.report = (new ReportGenerator(graphModel.getDirectedGraph(), id)).basicStatistics();

        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        PNGExporter pngExporter = (PNGExporter) ec.getExporter("png");

        try {
            FileOutputStream stream = new FileOutputStream(Play.application().path() + "/public/images/graph_" + id + ".png");
            ec.exportStream(stream, pngExporter);
            stream.close();
        } catch (Exception e){
            System.out.println(e.toString());
        }

    }

}


