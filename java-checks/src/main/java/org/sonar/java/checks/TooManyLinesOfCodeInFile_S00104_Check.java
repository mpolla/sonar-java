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
import org.sonar.check.RuleProperty;
import org.sonar.java.model.InternalSyntaxToken;
import org.sonar.plugins.java.api.tree.SyntaxToken;
import org.sonar.plugins.java.api.tree.Tree;

import java.text.MessageFormat;
import java.util.List;

@Rule(
    key = "S00104",
    priority = Priority.MAJOR,
    tags = {"brain-overload"})
public class TooManyLinesOfCodeInFile_S00104_Check extends SubscriptionBaseVisitor {

  private static final int DEFAULT_MAXIMUM = 1000;

  @RuleProperty(
      key = "maximumFileLocThreshold",
      defaultValue = "" + DEFAULT_MAXIMUM)
  public int maximum = DEFAULT_MAXIMUM;


  @Override
  public List<Tree.Kind> nodesToVisit() {
    return ImmutableList.of(Tree.Kind.TOKEN);
  }

  @Override
  public void visitToken(SyntaxToken token) {
    InternalSyntaxToken internalSyntaxToken = (InternalSyntaxToken) token;
    if (internalSyntaxToken.isEOF()) {
      int lines = internalSyntaxToken.getLine();
      if (lines > maximum) {
        addIssueOnFile(MessageFormat.format("This file has {0} lines, which is greater than {1} authorized. Split it into smaller files.", lines, maximum));
      }
    }
  }
}
