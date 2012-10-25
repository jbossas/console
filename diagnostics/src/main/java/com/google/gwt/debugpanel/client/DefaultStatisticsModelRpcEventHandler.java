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
import com.google.gwt.debugpanel.models.GwtDebugStatisticsValue;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link GwtDebugStatisticsModel.EventHandler} that handles the RPC events.
 */
public class DefaultStatisticsModelRpcEventHandler extends AbstractStatisticsModelEventHandler {
  private static final String RPC = "rpc";
  private static final String RPC_SERIALIZED = "requestSerialized";
  private static final String RPC_SENT = "requestSent";
  private static final String RPC_RESPONSE = "responseReceived";
  private static final String RPC_DESERIALIZED = "responseDeserialized";

  private Map<String, RpcTree> rpcs;

  public DefaultStatisticsModelRpcEventHandler() {
    rpcs = new HashMap<String, RpcTree>();
  }

  //@Override
  public boolean handle(GwtDebugStatisticsModel model, StatisticsEvent event) {
    if (RPC.equals(event.getSubSystem())) {
      String module = event.getModuleName();
      String group = event.getEventGroupKey();
      double millis = event.getMillis();
      RpcTree tree = getRpcRoot(model, null, module, group, millis);
      updateRpcTree(model, event, tree, module, getType(event), millis);
      return true;
    }
    return false;
  }

  private void updateRpcTree(GwtDebugStatisticsModel model, StatisticsEvent event,
      RpcTree tree, String module, String type, double millis) {
    if (!tree.root.getValue().hasRpcMethod()) {
      setRpcMethod(model, tree.root, event);
    }
    if (BEGIN.equals(type)) {
      tree.begin = millis;
      if (tree.serializedNode != null) {
        model.updateNodeAndItsParents(
            tree.serializedNode, tree.serializedNode.withChildTime(millis));
      }
    } else if (RPC_SERIALIZED.equals(type)) {
      tree.serializedNode = findOrCreateChild(model, tree.root, type, tree.begin, millis);
      tree.serialized = millis;
      if (tree.sentNode != null) {
        model.updateNodeAndItsParents(tree.sentNode, tree.sentNode.withChildTime(millis));
      }
    } else if (RPC_SENT.equals(type)) {
      tree.sentNode = findOrCreateChild(model, tree.root, type, tree.serialized, millis);
      tree.sent = millis;
      if (tree.responseNode != null) {
        model.updateNodeAndItsParents(tree.responseNode, tree.responseNode.withChildTime(millis));
      }
    } else if (RPC_RESPONSE.equals(type)) {
      tree.responseNode = findOrCreateChild(model, tree.root, type, tree.sent, millis);
      tree.response = millis;
      if (tree.deserializedNode != null) {
        model.updateNodeAndItsParents(
            tree.deserializedNode, tree.deserializedNode.withChildTime(millis));
      }
    } else if (RPC_DESERIALIZED.equals(type)) {
      tree.deserializedNode = findOrCreateChild(model, tree.root, type, tree.response, millis);
      tree.deserialized = millis;
      if (tree.callbackNode != null) {
        model.updateNodeAndItsParents(tree.callbackNode, tree.callbackNode.withChildTime(millis));
      }
    } else if (END.equals(type)) {
      tree.callbackNode = findOrCreateChild(
          model, tree.root, "callback", tree.deserialized, millis);
    }
  }

  private void setRpcMethod(GwtDebugStatisticsModel model, GwtNode node, StatisticsEvent event) {
    Object method = event.getExtraParameter("method");
    if (method != null) {
      GwtDebugStatisticsValue value = node.getValue();
      value.setRpcMethod(String.valueOf(method));
    }
  }

  public RpcTree getRpcRoot(
      GwtDebugStatisticsModel model, GwtNode parent, String module, String group, double millis) {
    String key = "__" + module + "__" + group;
    RpcTree tree = rpcs.get(key);
    if (tree == null) {
      GwtNode node = null;
      if (millis != 0) {
        node = new GwtNode(RPC + group, module, millis, millis);
        model.addNodeAndUpdateItsParents(parent, node, -1);
      }
      tree = new RpcTree(parent, node);
      rpcs.put(key, tree);
    } else if (tree.root == null) {
      tree.root = new GwtNode(RPC + group, module, millis, millis);
      model.addNodeAndUpdateItsParents(tree.parent, tree.root, -1);
    }
    return tree;
  }

  /**
   * A subtree representing a single RPC call.
   */
  public static class RpcTree {
    public GwtNode parent;
    public GwtNode root;
    public double begin;
    public GwtNode serializedNode;
    public double serialized;
    public GwtNode sentNode;
    public double sent;
    public GwtNode responseNode;
    public double response;
    public GwtNode deserializedNode;
    public double deserialized;
    public GwtNode callbackNode;

    public RpcTree(GwtNode parent, GwtNode root) {
      this.parent = parent;
      this.root = root;
    }
  }
}
