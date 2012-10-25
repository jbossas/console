/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.google.gwt.debugpanel.client;

import com.google.gwt.debugpanel.common.StatisticsEvent;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsModel;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsModel.GwtNode;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link GwtDebugStatisticsModel.EventHandler} that handles the startup
 * events.
 */
public class DefaultStatisticsModelStartupEventHandler extends AbstractStatisticsModelEventHandler {
  private static final String STARTUP = "startup";
  private static final String BOOTSTRAP = "bootstrap";
  private static final String REFERENCES = "loadExternalRefs";
  private static final String MODULE_START = "moduleStartup";
  private static final String PERMUTATIONS = "selectingPermutation";
  private static final String MODULE_REQUEST = "moduleRequested";
  private static final String MODULE_EVAL_START = "moduleEvalStart";
  private static final String MODULE_EVAL_END = "moduleEvalEnd";
  private static final String MODULE_LOAD = "onModuleLoadStart";

  private Map<String, StartUpTree> startups;

  public DefaultStatisticsModelStartupEventHandler() {
    startups = new HashMap<String, StartUpTree>();
  }

  //@Override
  public boolean handle(GwtDebugStatisticsModel model, StatisticsEvent event) {
    if (STARTUP.equals(event.getSubSystem())) {
      String module = event.getModuleName();
      String group = event.getEventGroupKey();
      double millis = event.getMillis();
      StartUpTree tree = getStartupTree(model, module, millis);
      GwtNode parent = findOrCreateChild(model, tree.startup, group, millis, millis);
      updateStartupTree(model, event, tree, parent, module, group, getType(event), millis);
      return true;
    }
    return false;
  }

  private void updateStartupTree(GwtDebugStatisticsModel model, StatisticsEvent event,
      StartUpTree tree, GwtNode parent, String module, String group, String type, double millis) {
    model.updateNodeAndItsParents(parent, parent.getValue().withChildTime(millis));

    // Check and update permutation selection stats.
    if (PERMUTATIONS.equals(type)) {
      tree.permutations = findOrCreateChild(
          model, parent, PERMUTATIONS, millis, tree.bootstrapEnd);
    } else if (BOOTSTRAP.equals(group) && END.equals(type)) {
      tree.bootstrapEnd = millis;
      if (tree.permutations != null) {
        model.updateNodeAndItsParents(tree.permutations, tree.permutations.withChildTime(millis));
      }
    } else if (REFERENCES.equals(group)) {

      // Update external refs and its dependent stats.
      if (END.equals(type)) {
        tree.externalRefsEnd = Math.max(tree.externalRefsEnd, millis);
        if (tree.requestedNode != null) {
          model.updateNodeAndItsParents(
              tree.requestedNode, tree.requestedNode.withStartTime(millis));
        }
      }
    } else if (MODULE_START.equals(group)) {
      updateModuleStart(model, event, tree, parent, module, type, millis);
    }
  }

  private void updateModuleStart(GwtDebugStatisticsModel model, StatisticsEvent event,
      StartUpTree tree, GwtNode parent, String module, String type, double millis) {
    if (MODULE_REQUEST.equals(type)) {
      tree.requestedNode = findOrCreateChild(model, parent, type, tree.externalRefsEnd, millis);
      tree.requested = millis;
      if (tree.receivedNode != null) {
        model.updateNodeAndItsParents(tree.receivedNode, tree.receivedNode.withChildTime(millis));
      }
    } else if (MODULE_EVAL_START.equals(type)) {
      tree.receivedNode = findOrCreateChild(
          model, parent, "moduleReceived", tree.requested, millis);
      tree.received = millis;
      if (tree.evalNode != null) {
        model.updateNodeAndItsParents(tree.evalNode, tree.evalNode.withChildTime(millis));
      }
    } else if (MODULE_EVAL_END.equals(type)) {
      tree.evalNode = findOrCreateChild(model, parent, "moduleEval", tree.received, millis);
      tree.eval = millis;
      if (tree.injectionNode != null) {
        model.updateNodeAndItsParents(tree.injectionNode, tree.injectionNode.withChildTime(millis));
      }
    } else if (MODULE_LOAD.equals(type)) {
      tree.injectionNode = findOrCreateChild(model, parent, "injection", tree.eval, millis);

      parent = findOrCreateChild(model, parent, "onModuleLoad", millis, millis);
      int idx = 0;
      double end = millis;
      for (; idx < parent.getChildCount(); idx++) {
        if ((end = parent.getChild(idx).getValue().getStartTime()) > millis) {
          break;
        }
      }
      if (idx > 0) {
        GwtNode node = (GwtNode) parent.getChild(idx - 1);
        model.updateNodeAndItsParents(node, node.withEndTime(millis));
      }
      if (idx >= parent.getChildCount()) {
        end = Math.max(millis, parent.getValue().getEndTime());
      }
      GwtNode node = new GwtNode(getClassName(event), module, millis, end);
      model.addNodeAndUpdateItsParents(parent, node, idx);
    } else if (END.equals(type)) {
      parent = findOrCreateChild(model, parent, "onModuleLoad", millis, millis);
      if (parent.getChildCount() > 0) {
        GwtNode node = (GwtNode) parent.getChild(parent.getChildCount() - 1);
        model.updateNodeAndItsParents(node, node.withEndTime(millis));
      }
    }
  }

  public StartUpTree getStartupTree(GwtDebugStatisticsModel model, String module, double millis) {
    StartUpTree tree = startups.get(module);
    if (tree == null) {
      GwtNode node = new GwtNode(STARTUP, module, millis, millis);
      model.addNode(null, node, -1);
      tree = new StartUpTree(node);
      startups.put(module, tree);
    }
    return tree;
  }

  /**
   * A subtree representing the startup/bootstrap events of a module.
   */
  public static class StartUpTree {
    public GwtNode startup;
    public GwtNode permutations;
    public double bootstrapEnd;
    public double externalRefsEnd;
    public GwtNode requestedNode;
    public double requested;
    public GwtNode receivedNode;
    public double received;
    public GwtNode evalNode;
    public double eval;
    public GwtNode injectionNode;

    public StartUpTree(GwtNode startup) {
      this.startup = startup;
    }
  }
}
