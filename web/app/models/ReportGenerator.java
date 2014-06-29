package models;

import com.avaje.ebean.Ebean;

import java.io.File;
import java.util.List;
import models.SimpleGraph;

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
import org.gephi.statistics.spi.Statistics;
import org.openide.util.Lookup;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ReportGenerator {

    DirectedGraph graph;
    Long id;

    public ReportGenerator(DirectedGraph graph, Long id) {
        this.graph = graph;
        this.id =  id;
    }

    public String basicStatistics(){
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

        return createReport(statistics, graph.getGraphModel(), attributeModel);
    }


    private String createReport(List<Statistics> stats, GraphModel graphModel, AttributeModel attributeModel){
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

    private String changeImageLocations(String html){
        List<ReplacedImage> toReplace = moveImages(html);
        for(ReplacedImage replacement : toReplace){
            html = html.replaceFirst(Pattern.quote(replacement.htmlToReplace), "src=\"/assets/images/" + replacement.imageName);
        }
        return html;
    }

    private  List<ReplacedImage> moveImages(String html){
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

    private String moveImage(String sourcePath){
        File source = new File(sourcePath);
        File destination = new File(imageDestination(sourcePath));
        source.renameTo(destination);
        return destination.getName();
    }

    private String imageDestination(String oldPath){
        return "public/images/" + id + new File(oldPath).getName();
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
