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
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.List;

@Rule(
    key = "S2094",
    priority = Priority.MAJOR,
    tags = {})
@BelongsToProfile(title = "Sonar way", priority = Priority.MAJOR)
public class EmptyClassCheck extends SubscriptionBaseVisitor {
  @Override
  public List<Tree.Kind> nodesToVisit() {
    return ImmutableList.of(Tree.Kind.CLASS);
  }

  @Override
  public void visitNode(Tree tree) {
    if (isEmptyClass((ClassTree) tree)) {
      addIssue(tree, "Remove this empty class, write its code or make it an \"interface\".");
    }
  }

  private boolean isEmptyClass(ClassTree tree) {
    return tree.simpleName() != null && isNotExtending(tree) && tree.members().isEmpty();
  }

  private boolean isNotExtending(ClassTree tree) {
    return tree.superClass() == null && tree.superInterfaces().isEmpty();
  }
}
