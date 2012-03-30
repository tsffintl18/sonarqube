/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.api.resources;

import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * <p>Experimental extension to declare types of resources.</p>
 * <p>
 * Since 2.15, ResourceType object can declare properties that give information about the capabilities of the
 * resource type. Those properties may be used, of instance, to adapt the Web UI according to the type of 
 * the resource being displayed.
 * <br>
 * Currently, the following properties can be defined:
 * </p>
 * <ul>
 *   <li>"deletable": if set to "true", then this resource can be deleted/purged.</li>
 *   <li>"availableForFilters": if set to "true", then this resource can be displayed in the filters results</li>
 *   <li>"modifiable_history": if set to "true", then the history of this resource may be modified (deletion of snapshots, modification of events, ...)</li>
 * </ul>
 *
 * @since 2.14
 */
@Beta
@Immutable
public final class ResourceType {

  /**
   * Builder used to create {@link ResourceType} objects.
   */
  public static class Builder {
    private String qualifier;
    private String iconPath;
    private boolean hasSourceCode = false;
    private Map<String, String> properties = Maps.newHashMap();

    /**
     * Creates a new {@link Builder}
     * @param qualifier
     */
    public Builder(String qualifier) {
      this.qualifier = qualifier;
    }

    /**
     * Relative path of the icon used to represent the resource type.
     * 
     * @param iconPath path to icon, relative to context of web-application (e.g. "/images/q/DIR.png")
     */
    public Builder setIconPath(@Nullable String iconPath) {
      this.iconPath = iconPath;
      return this;
    }

    /**
     * @deprecated since 2.15. Use {@link #setProperty(String, String)} with "availableForFilters" set to "true".
     */
    @Deprecated
    public Builder availableForFilters() {
      setProperty("availableForFilters", "true");
      return this;
    }

    /**
     * Tells that the resources of this type will have source code.
     */
    public Builder hasSourceCode() {
      this.hasSourceCode = true;
      return this;
    }

    /**
     * Sets a property on the resource type. See the description of {@link ResourceType} class for more information.
     * 
     * @since 2.15
     */
    public Builder setProperty(String key, String value) {
      Preconditions.checkNotNull(key);
      Preconditions.checkNotNull(value);
      properties.put(key, value);
      return this;
    }

    /**
     * Creates an instance of {@link ResourceType} based on all information given to the builder.
     */
    public ResourceType build() {
      if (Strings.isNullOrEmpty(iconPath)) {
        iconPath = "/images/q/" + qualifier + ".png";
      }
      return new ResourceType(this);
    }
  }

  /**
   * Creates a new {@link Builder}
   * @param qualifier
   */
  public static Builder builder(String qualifier) {
    Preconditions.checkNotNull(qualifier);
    Preconditions.checkArgument(qualifier.length() <= 10, "Qualifier is limited to 10 characters");
    return new Builder(qualifier);
  }

  private final String qualifier;
  private final String iconPath;
  private final boolean hasSourceCode;
  private Map<String, String> properties;

  private ResourceType(Builder builder) {
    this.qualifier = builder.qualifier;
    this.iconPath = builder.iconPath;
    this.hasSourceCode = builder.hasSourceCode;
    this.properties = Maps.newHashMap(builder.properties);
  }

  /**
   * Qualifier is the unique key.
   * 
   * @return the qualifier
   */
  public String getQualifier() {
    return qualifier;
  }

  /**
   * Returns the relative path of the icon used to represent the resource type
   * 
   * @return the relative path.
   */
  public String getIconPath() {
    return iconPath;
  }

  /**
   * @deprecated since 2.15. Use {@link #getBooleanProperty(String)} with "availableForFilters".
   */
  @Deprecated
  public boolean isAvailableForFilters() {
    Boolean availableForFilters = getBooleanProperty("availableForFilters");
    return availableForFilters == null ? false : availableForFilters.booleanValue();
  }

  /**
   * Tells whether resources of this type has source code or not.
   * 
   * @return true if the type has source code
   */
  public boolean hasSourceCode() {
    return hasSourceCode;
  }

  /**
   * Returns the value of the property for this resource type.
   * 
   * @return the String value of the property, or NULL if the property hasn't been set.
   * @since 2.15
   */
  public String getStringProperty(String key) {
    Preconditions.checkNotNull(key);
    return properties.get(key);
  }

  /**
   * Returns the value of the property for this resource type.
   * 
   * @return the Boolean value of the property. If the property hasn't been set, False is returned.
   * @since 2.15
   */
  public Boolean getBooleanProperty(String key) {
    Preconditions.checkNotNull(key);
    return Boolean.valueOf(properties.get(key));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ResourceType that = (ResourceType) o;
    return qualifier.equals(that.qualifier);
  }

  @Override
  public int hashCode() {
    return qualifier.hashCode();
  }
}
