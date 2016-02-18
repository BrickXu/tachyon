/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.mesos;

import java.util.ArrayList;
import java.util.List;

import org.apache.mesos.Protos;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AttributeEvalutatorTest {

  private List<Protos.Attribute> mAttrs = new ArrayList<Protos.Attribute>();
  private AttributeEvaluator mEvaluator;

  @Before
  public void setUp() throws Exception {
    mAttrs.add(Protos.Attribute.newBuilder()
            .setType(Protos.Value.Type.TEXT)
            .setName("colo")
            .setText(Protos.Value.Text.newBuilder().setValue("sparkonly").build()).build());
    mAttrs.add(Protos.Attribute.newBuilder()
            .setType(Protos.Value.Type.TEXT)
            .setName("type")
            .setText(Protos.Value.Text.newBuilder().setValue("server").build()).build());
    mAttrs.add(Protos.Attribute.newBuilder()
            .setType(Protos.Value.Type.TEXT)
            .setName("network")
            .setText(Protos.Value.Text.newBuilder().setValue("none").build()).build());
    mAttrs.add(Protos.Attribute.newBuilder()
            .setType(Protos.Value.Type.TEXT)
            .setName("core")
            .setText(Protos.Value.Text.newBuilder().setValue("32").build()).build());

    mEvaluator = new AttributeEvaluator(this.mAttrs);
  }

  @Test
  public void matchSingleAttributeTest() throws Exception {
    assertTrue("Should match attribute here", this.mEvaluator.matchAttributes("colo:sparkonly"));
  }

  @Test
  public void matchAndAttributesTest() throws Exception {
    assertTrue("Should match attribute here", this.mEvaluator.matchAttributes("colo:sparkonly & "
            + "type:server"));
  }

  @Test
  public void matchOrAttributesTest() throws Exception {
    assertTrue("Should match attribute here", this.mEvaluator.matchAttributes("colo:sparkonly | "
            + "type:vm"));
  }

  @Test
  public void matchNotAttributesTest() throws Exception {
    assertTrue("Should match attribute here", this.mEvaluator.matchAttributes("!colo:spark"));
  }

  @Test
  public void matchComplexAttributesTest() throws Exception {
    assertTrue("Should match attribute here", this.mEvaluator.matchAttributes("!colo:spark & "
            + "type:server"));
    assertFalse("Should match attribute here", this.mEvaluator.matchAttributes("!colo:sparkonly &"
            + " type:server"));
  }

  @Test
  public void PriorityTest() throws Exception {
    assertFalse("Should not match attribute here", this.mEvaluator.matchAttributes(
            "network:calico & colo:sparkonly | type:vm"));
    assertTrue("Should match attribute here", this.mEvaluator.matchAttributes("network:calico & "
            + "colo:sparkonly | type:server"));
  }
}
