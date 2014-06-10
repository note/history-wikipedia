package controllers;

import com.sun.javafx.geom.transform.BaseTransform;
import org.gephi.graph.api.*;
import org.gephi.statistics.plugin.Degree;
import play.*;
import play.mvc.*;

import views.html.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.io.database.drivers.MySQLDriver;
import org.gephi.io.database.drivers.SQLUtils;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.plugin.database.EdgeListDatabaseImpl;
import org.gephi.io.importer.plugin.database.ImporterEdgeList;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static DirectedGraph getSimpleGraph() {
        // Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get controllers and models
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();

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

    public static Result gephi(){
        DirectedGraph graph = getSimpleGraph();
        final AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        final Degree degree = new Degree();
        degree.execute(graph.getGraphModel(), attributeModel);
        degree.getReport();
        System.out.println(degree.getReport());
        return ok(stats.render(degree.getReport()));
    }

}
