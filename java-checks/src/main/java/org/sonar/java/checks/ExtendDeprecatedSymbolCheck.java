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
package org.sonar.java.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.model.AbstractTypedTree;
import org.sonar.java.resolve.Type;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;

import javax.annotation.Nullable;
import java.util.List;

@Rule(key = "S1724",
    priority = Priority.MAJOR,
    tags = {"cwe", "obsolete"})
public class ExtendDeprecatedSymbolCheck extends AbstractDeprecatedChecker {

  @Override
  public List<Tree.Kind> nodesToVisit() {
    return ImmutableList.of(Tree.Kind.CLASS, Tree.Kind.INTERFACE, Tree.Kind.ENUM, Tree.Kind.ANNOTATION_TYPE);
  }

  @Override
  public void visitNode(Tree tree) {
    ClassTree classTree = (ClassTree) tree;
    if (!hasDeprecatedAnnotation(tree)) {
      checkSuperTypeDeprecation(classTree.superClass(), false);
      for (Tree superInterface : classTree.superInterfaces()) {
        checkSuperTypeDeprecation(superInterface, true);
      }
    }
  }

  private void checkSuperTypeDeprecation(@Nullable Tree superTypeTree, boolean isInterface) {
    if (superTypeTree != null) {
      Type symbolType = ((AbstractTypedTree) superTypeTree).getSymbolType();
      if (symbolType.isTagged(Type.CLASS) && ((Type.ClassType) symbolType).getSymbol().isDeprecated()) {
        addIssue(superTypeTree, "\""+((Type.ClassType) symbolType).getSymbol().getName()+"\""+" is deprecated, "
            + (isInterface ? "implement" : "extend") + " the suggested replacement instead.");
      }
    }
  }


}
