// Copyright 2017 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.analysis.platform;

import com.google.common.base.Objects;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.concurrent.ThreadSafety.Immutable;
import com.google.devtools.build.lib.events.Location;
import com.google.devtools.build.lib.packages.NativeInfo;
import com.google.devtools.build.lib.packages.NativeProvider;
import com.google.devtools.build.lib.skyframe.serialization.autocodec.AutoCodec;
import com.google.devtools.build.lib.skyframe.serialization.autocodec.AutoCodec.VisibleForSerialization;
import com.google.devtools.build.lib.skylarkbuildapi.platform.ConstraintSettingInfoApi;
import com.google.devtools.build.lib.skylarkinterface.SkylarkPrinter;
import com.google.devtools.build.lib.util.Fingerprint;
import javax.annotation.Nullable;

/** Provider for a platform constraint setting that is available to be fulfilled. */
@Immutable
@AutoCodec
public class ConstraintSettingInfo extends NativeInfo implements ConstraintSettingInfoApi {
  /** Name used in Skylark for accessing this provider. */
  public static final String SKYLARK_NAME = "ConstraintSettingInfo";

  /** Skylark constructor and identifier for this provider. */
  public static final NativeProvider<ConstraintSettingInfo> PROVIDER =
      new NativeProvider<ConstraintSettingInfo>(ConstraintSettingInfo.class, SKYLARK_NAME) {};

  private final Label label;
  @Nullable private final Label defaultConstraintValueLabel;

  @VisibleForSerialization
  ConstraintSettingInfo(Label label, Label defaultConstraintValueLabel, Location location) {
    super(PROVIDER, location);

    this.label = label;
    this.defaultConstraintValueLabel = defaultConstraintValueLabel;
  }

  @Override
  public Label label() {
    return label;
  }

  @Override
  @Nullable
  public ConstraintValueInfo defaultConstraintValue() {
    if (defaultConstraintValueLabel == null) {
      return null;
    }
    return ConstraintValueInfo.create(this, defaultConstraintValueLabel);
  }

  /** Add this constraint setting to the given fingerprint. */
  public void addTo(Fingerprint fp) {
    fp.addString(label.getCanonicalForm());
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof ConstraintSettingInfo)) {
      return false;
    }

    ConstraintSettingInfo otherConstraint = (ConstraintSettingInfo) other;
    return Objects.equal(label, otherConstraint.label);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(label);
  }

  @Override
  public void repr(SkylarkPrinter printer) {
    printer.format("ConstraintSettingInfo(%s", label.toString());
    if (defaultConstraintValueLabel != null) {
      printer.format(", default_constraint_value=%s", defaultConstraintValueLabel.toString());
    }
    printer.append(")");
  }

  /** Returns a new {@link ConstraintSettingInfo} with the given data. */
  public static ConstraintSettingInfo create(Label constraintSetting) {
    return create(constraintSetting, null, Location.BUILTIN);
  }

  /** Returns a new {@link ConstraintSettingInfo} with the given data. */
  public static ConstraintSettingInfo create(
      Label constraintSetting, Label defaultConstraintValue) {
    return create(constraintSetting, defaultConstraintValue, Location.BUILTIN);
  }

  /** Returns a new {@link ConstraintSettingInfo} with the given data. */
  public static ConstraintSettingInfo create(
      Label constraintSetting, Label defaultConstraintValue, Location location) {
    return new ConstraintSettingInfo(constraintSetting, defaultConstraintValue, location);
  }
}
