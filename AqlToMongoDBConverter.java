import org.antlr.v4.runtime.tree.*;

import java.util.Stack;

public class AqlToMongoDBConverter extends AqlBaseListener {
    private StringBuilder mongoQueryBuilder;
    private StringBuilder select;
    private StringBuilder from;
    private StringBuilder where;
    private StringBuilder problem;
    private StringBuilder extension;

    public AqlToMongoDBConverter() {
        mongoQueryBuilder = new StringBuilder();
        select = new StringBuilder();
        from = new StringBuilder();
        where = new StringBuilder();
        problem = new StringBuilder();
        extension = new StringBuilder();
    }

    @Override
    public void enterSelectQuery(AqlParser.SelectQueryContext ctx) {
        mongoQueryBuilder.append("db.EHR.aggregate([\n");
    }

    @Override
    public void enterSelectClause(AqlParser.SelectClauseContext ctx) {
        select.append("  {\n    $match: {\n      ");
    }

    @Override
    public void enterFromClause(AqlParser.FromClauseContext ctx) {
        from.append("\"subjectOfCare.extension\": ");
    }

    @Override
    public void enterWhereClause(AqlParser.WhereClauseContext ctx) {
        where.append("\n    }\n  },\n  {\n    $project: {\n      \"Problem\": ");
    }

    @Override
    public void exitSelectQuery(AqlParser.SelectQueryContext ctx) {
        mongoQueryBuilder.append(select).append(from).append(extension).append(where).append("\n])");
    }

    @Override
    public void exitSelectClause(AqlParser.SelectClauseContext ctx) {
        // Add logic to process the select clause and append it to the MongoDB query
        String selectClause = ctx.getText().substring(8).replace("/", ".");
        int index = (selectClause.indexOf("A"));
        problem.append(selectClause.substring(0,index));
        select.append("\"").append(selectClause.substring(0,index));
        select.append(": {\n        $exists: true\n      },\n      ");
    }

    @Override
    public void exitWhereClause(AqlParser.WhereClauseContext ctx) {
        // Add logic to process the where clause and append it to the MongoDB query
        String s = ctx.getText().replace("'", "\"");
        extension.append(s.substring(s.indexOf("=") + 1));
        where.append("\"$").append(problem).append("\",\n");
        where.append("      \"_id\": 0\n    }\n  }");
    }

    @Override
    public void exitFromClause(AqlParser.FromClauseContext ctx) {
        // Add logic to process the from clause and append it to the MongoDB query
        String fromClause = ctx.getText();
        from.append("\"");
    }

    public String getMongoQuery() {
        return mongoQueryBuilder.toString();
    }
}
