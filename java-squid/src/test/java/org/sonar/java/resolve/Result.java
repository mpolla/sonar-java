/*
 * SonarQube Java
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.java.resolve;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Parser;
import org.sonar.java.ast.parser.JavaParser;
import org.sonar.java.model.JavaTree;
import org.sonar.plugins.java.api.tree.CompilationUnitTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;

import java.io.File;

class Result {

  private static final Parser parser = JavaParser.createParser(Charsets.UTF_8);
  private final SemanticModel semanticModel;

  private Result(SemanticModel semanticModel) {
    this.semanticModel = semanticModel;
  }

  public static Result createFor(String name) {
    return createForJavaFile("src/test/files/sym/" + name);
  }

  public static Result createForJavaFile(String filePath) {
    File file = new File(filePath + ".java");
    AstNode astNode = parser.parse(file);
    return new Result(SemanticModel.createFor((CompilationUnitTree) astNode, Lists.newArrayList(new File("target/test-classes"), new File("target/classes"))));
  }

  public Symbol symbol(String name) {
    Symbol result = null;
    for (Symbol symbol : semanticModel.getSymbolsTree().values()) {
      if (name.equals(symbol.name)) {
        if (result != null) {
          throw new IllegalArgumentException("Ambiguous coordinates of symbol");
        }
        result = symbol;
      }
    }
    if (result == null) {
      throw new IllegalArgumentException("Symbol not found");
    }
    return result;
  }

  public Symbol symbol(String name, int line) {
    Symbol result = null;
    for (Symbol symbol : semanticModel.getSymbolsTree().values()) {
      if (name.equals(symbol.name) && ((JavaTree) semanticModel.getTree(symbol)).getAstNode().getTokenLine() == line) {
        if (result != null) {
          throw new IllegalArgumentException("Ambiguous coordinates of symbol");
        }
        result = symbol;
      }
    }
    if (result == null) {
      throw new IllegalArgumentException("Symbol not found");
    }
    return result;
  }

  public Symbol reference(int line, int column) {
    return (Symbol) referenceTree(line, column, true);
  }

  public IdentifierTree referenceTree(int line, int column) {
    return (IdentifierTree) referenceTree(line, column, false);
  }

  private Object referenceTree(int line, int column, boolean searchSymbol) {
    // In SSLR column starts at 0, but here we want consistency with IDE, so we start from 1:
    column -= 1;
    for (Symbol symbol : semanticModel.getSymbolUsed()) {
      for (IdentifierTree usage : semanticModel.getUsages(symbol)) {
        Token token = ((JavaTree) usage.identifierToken()).getAstNode().getToken();
        if (token.getLine() == line && token.getColumn() == column) {
          if(searchSymbol) {
            return symbol;
          } else {
            return usage;
          }
        }
      }
    }
    throw new IllegalArgumentException("Reference Tree not found");
  }



}
