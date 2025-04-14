package com.github.codeprometheus;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class CalcStarter {
  public static void main(String[] args) {
    CodePointCharStream charStream = CharStreams.fromString("service_relation_client_cpm = from(ServiceRelation.*).filter(detectPoint == DetectPoint.CLIENT).cpm()");
    CalcLexer calcLexer = new CalcLexer(charStream);
    CalcParser calcParser = new CalcParser(new CommonTokenStream(calcLexer));
    CalcParser.RootContext root = calcParser.root();
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(new CalcExecuteListener(), root);
    System.out.println(root.toStringTree(calcParser));
  }

  static class CalcExecuteListener extends CalcBaseListener {
    @Override
    public void enterRoot(CalcParser.RootContext ctx) {
      System.out.println("enterRoot: " + ctx.getText());
    }

    @Override
    public void exitRoot(CalcParser.RootContext ctx) {
      System.out.println("exitRoot: " + ctx.getText());
    }

    @Override
    public void enterAggregationStatement(CalcParser.AggregationStatementContext ctx) {
      System.out.println("enterAggregationStatement: " + ctx.getText());
    }
  }
}
