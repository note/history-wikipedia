package models;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.List;

import com.avaje.ebean.annotation.Sql;
import com.avaje.ebean.*;

/**
 *
 * Note the @Sql indicates to Ebean that this bean is not based on a table but
 * instead uses RawSql.
 *
 */
@Entity
@Sql
public class Article {


    static final String sql = "select page.page_id as id, page.page_title as title, text.old_text as text from categorylinks "+
            "join page on page.page_id = categorylinks.cl_from "+
            "join revision on revision.rev_page = categorylinks.cl_from "+
            "join text on text.old_id = revision.rev_id ";

    /*
     * Fetch all articles with texts from category
     */
    public static List<Article> find(String category){
        RawSql rawSql =
                RawSqlBuilder
                        .parse(sql)
                        .create();


        Query<Article> query = Ebean.find(Article.class);
        query.setRawSql(rawSql)
                .where().eq("categorylinks.cl_to", category);

        List<Article> list = query.findList();
        return list;
    }

    public Long id;
    public String title;
    public String text;

}