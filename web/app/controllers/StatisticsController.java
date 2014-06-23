package controllers;

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
import org.gephi.statistics.spi.Statistics;
import org.openide.util.Lookup;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stats;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by michal on 23/06/14.
 */
public class StatisticsController extends Controller {
    public static Result basicStatistics(){
        DirectedGraph graph = getSimpleGraph();
        final AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();

        List<Statistics> statistics = new ArrayList<Statistics>();

        statistics.add(new ClusteringCoefficient());
        statistics.add(new ConnectedComponents());
        statistics.add(new Degree());
        statistics.add(new EigenvectorCentrality());
        statistics.add(new GraphDensity());
        statistics.add(new GraphDistance());
        statistics.add(new Hits());
        statistics.add(new Modularity());
        statistics.add(new PageRank());
        statistics.add(new WeightedDegree());

        String report = createReport(statistics, graph.getGraphModel(), attributeModel);

        return ok(stats.render(report));
    }

    public static DirectedGraph getSimpleGraph() {
        // Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get controllers and models
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

        //Import database
        EdgeListDatabaseImpl db = new EdgeListDatabaseImpl();
        db.setDBName(Play.application().configuration().getString("wikidb.dbName"));
        db.setHost(Play.application().configuration().getString("wikidb.host"));
        db.setUsername(Play.application().configuration().getString("wikidb.user"));
        db.setPasswd(Play.application().configuration().getString("wikidb.password"));
        db.setSQLDriver(new MySQLDriver());
        db.setPort(3306);
        db.setNodeQuery("select from_name as id, from_name as label from simple_graph group by from_person");
        db.setEdgeQuery("select from_name as source, to_name as target, weight as weight from simple_graph where weight > 0");
        ImporterEdgeList edgeListImporter = new ImporterEdgeList();
        Container container = importController.importDatabase(db, edgeListImporter);
        container.setAllowAutoNode(false);      //Don't create missing nodes
        container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        return graphModel.getDirectedGraph();
    }

    private static String createReport(List<Statistics> stats, GraphModel graphModel, AttributeModel attributeModel){
        StringBuilder builder = new StringBuilder();

        for(Statistics stat: stats){
            stat.execute(graphModel, attributeModel);
            String report = stat.getReport();

            // by default generated images are placed in /tmp
            // there is no way to change it using gephi API, it is hardcoded: https://github.com/gephi/gephi/blob/master/modules/StatisticsPlugin/src/main/java/org/gephi/statistics/plugin/ChartUtils.java
            // so we need to copy images to public/images and replace change src attributes respectively
            String modifiedReport = changeImageLocations(report);
            builder.append(modifiedReport);
        }

        return builder.toString();
    }

    private static String changeImageLocations(String html){
        List<ReplacedImage> toReplace = moveImages(html);
        for(ReplacedImage replacement : toReplace){
            html = html.replaceFirst(Pattern.quote(replacement.htmlToReplace), "src=\"/assets/images/" + replacement.imageName);
        }
        return html;
    }

    private static List<ReplacedImage> moveImages(String html){
        String[] splitted = html.split(Pattern.quote("IMG SRC=\"file:"));
        List<ReplacedImage> toReplace = new ArrayList<ReplacedImage>();
        for(int i=1; i<splitted.length; ++i){
            String imagePath = splitted[i].substring(0, splitted[i].indexOf("\""));
            System.out.println("imagePath:" + imagePath);
            String newFilePath = moveImage(imagePath);
            toReplace.add(new ReplacedImage("SRC=\"file:" + imagePath, newFilePath));
        }
        return toReplace;
    }

    private static String moveImage(String sourcePath){
        File source = new File(sourcePath);
        File destination = new File(imageDestination(sourcePath));
        source.renameTo(destination);
        return destination.getName();
    }

    private static String imageDestination(String oldPath){
        return "public/images/" + new File(oldPath).getName();
    }
}

class ReplacedImage {
    String htmlToReplace;
    String imageName;

    public ReplacedImage(String toReplace, String imageName){
        this.htmlToReplace = toReplace;
        this.imageName = imageName;
    }
}
