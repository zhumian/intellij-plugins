package com.intellij.javascript.karma.execution;

import com.google.common.collect.ImmutableList;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.javascript.karma.scope.KarmaScopeKind;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
import com.intellij.javascript.nodejs.util.NodePackage;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ComparatorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class KarmaRunSettings {

  private final String myConfigPath;
  private final NodePackage myKarmaPackage;
  private final String myKarmaOptions;
  private final NodeJsInterpreterRef myInterpreterRef;
  private final String myNodeOptions;
  private final String myWorkingDirectory;
  private final EnvironmentVariablesData myEnvData;
  private final KarmaScopeKind myScopeKind;
  private final String myTestFilePath;
  private final List<String> myTestNames;

  public KarmaRunSettings(@NotNull Builder builder) {
    myConfigPath = FileUtil.toSystemDependentName(builder.myConfigPath);
    myKarmaPackage = builder.myKarmaPackage;
    myKarmaOptions = builder.myKarmaOptions;
    myInterpreterRef = builder.myInterpreterRef;
    myNodeOptions = builder.myNodeOptions;
    myWorkingDirectory = FileUtil.toSystemDependentName(builder.myWorkingDirectory);
    myEnvData = builder.myEnvData;
    myScopeKind = builder.myScopeKind;
    myTestFilePath = FileUtil.toSystemDependentName(builder.myTestFilePath);
    myTestNames = ImmutableList.copyOf(builder.myTestNames);
  }

  @NotNull
  public String getConfigPathSystemDependent() {
    return myConfigPath;
  }

  @NotNull
  public String getConfigPathSystemIndependent() {
    return FileUtil.toSystemIndependentName(myConfigPath);
  }

  @Nullable
  public NodePackage getKarmaPackage() {
    return myKarmaPackage;
  }

  @NotNull
  public String getKarmaOptions() {
    return myKarmaOptions;
  }

  @NotNull
  public NodeJsInterpreterRef getInterpreterRef() {
    return myInterpreterRef;
  }

  @NotNull
  public String getNodeOptions() {
    return myNodeOptions;
  }

  @NotNull
  public String getWorkingDirectorySystemDependent() {
    return myWorkingDirectory;
  }

  @NotNull
  public String getWorkingDirectorySystemIndependent() {
    return FileUtil.toSystemIndependentName(myWorkingDirectory);
  }

  @NotNull
  public EnvironmentVariablesData getEnvData() {
    return myEnvData;
  }

  @NotNull
  public KarmaScopeKind getScopeKind() {
    return myScopeKind;
  }

  @NotNull
  public String getTestFileSystemDependentPath() {
    return myTestFilePath;
  }

  @NotNull
  public String getTestFileSystemIndependentPath() {
    return FileUtil.toSystemIndependentName(myTestFilePath);
  }

  @NotNull
  public List<String> getTestNames() {
    return myTestNames;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    KarmaRunSettings that = (KarmaRunSettings)o;

    return myConfigPath.equals(that.myConfigPath) &&
           ComparatorUtil.equalsNullable(myKarmaPackage, that.myKarmaPackage) &&
           myKarmaOptions.equals(that.myKarmaOptions) &&
           myInterpreterRef.getReferenceName().equals(that.myInterpreterRef.getReferenceName()) &&
           myNodeOptions.equals(that.myNodeOptions) &&
           myWorkingDirectory.equals(that.myWorkingDirectory) &&
           myEnvData.equals(that.myEnvData) &&
           myScopeKind.equals(that.myScopeKind) &&
           myTestFilePath.equals(that.myTestFilePath) &&
           myTestNames.equals(that.myTestNames);
  }

  @Override
  public int hashCode() {
    int result = myConfigPath.hashCode();
    result = 31 * result + (myKarmaPackage != null ? myKarmaPackage.hashCode() : 0);
    result = 31 * result + myKarmaOptions.hashCode();
    result = 31 * result + myInterpreterRef.getReferenceName().hashCode();
    result = 31 * result + myNodeOptions.hashCode();
    result = 31 * result + myWorkingDirectory.hashCode();
    result = 31 * result + myEnvData.hashCode();
    result = 31 * result + myScopeKind.hashCode();
    result = 31 * result + myTestFilePath.hashCode();
    result = 31 * result + myTestNames.hashCode();
    return result;
  }

  @NotNull
  public Builder toBuilder() {
    return new Builder(this);
  }

  public static class Builder {

    private String myConfigPath = "";
    private NodePackage myKarmaPackage = null;
    private String myKarmaOptions = "";
    private NodeJsInterpreterRef myInterpreterRef = NodeJsInterpreterRef.createProjectRef();
    private String myNodeOptions = "";
    private EnvironmentVariablesData myEnvData = EnvironmentVariablesData.DEFAULT;
    private KarmaScopeKind myScopeKind = KarmaScopeKind.ALL;
    private String myTestFilePath = "";
    private List<String> myTestNames = Collections.emptyList();
    private String myWorkingDirectory = "";

    public Builder() {}

    public Builder(@NotNull KarmaRunSettings settings) {
      myConfigPath = settings.getConfigPathSystemDependent();
      myKarmaPackage = settings.myKarmaPackage;
      myKarmaOptions = settings.myKarmaOptions;
      myInterpreterRef = settings.getInterpreterRef();
      myNodeOptions = settings.myNodeOptions;
      myWorkingDirectory = settings.myWorkingDirectory;
      myEnvData = settings.myEnvData;
      myScopeKind = settings.myScopeKind;
      myTestFilePath = settings.myTestFilePath;
      myTestNames = settings.myTestNames;
    }

    @NotNull
    public Builder setConfigPath(@Nullable String configPath) {
      myConfigPath = StringUtil.notNullize(configPath);
      return this;
    }

    @NotNull
    public Builder setKarmaPackage(@Nullable NodePackage karmaPackage) {
      myKarmaPackage = karmaPackage;
      return this;
    }

    @NotNull
    public Builder setKarmaOptions(@Nullable String karmaOptions) {
      myKarmaOptions = StringUtil.notNullize(karmaOptions);
      return this;
    }

    @NotNull
    public Builder setInterpreterRef(@NotNull NodeJsInterpreterRef interpreterRef) {
      myInterpreterRef = interpreterRef;
      return this;
    }

    @NotNull
    public Builder setNodeOptions(@NotNull String nodeOptions) {
      myNodeOptions = nodeOptions;
      return this;
    }

    @NotNull
    public Builder setWorkingDirectory(@Nullable String workingDirectory) {
      myWorkingDirectory = StringUtil.notNullize(workingDirectory);
      return this;
    }

    @NotNull
    public Builder setEnvData(@NotNull EnvironmentVariablesData envData) {
      myEnvData = envData;
      return this;
    }

    @NotNull
    public Builder setScopeKind(@NotNull KarmaScopeKind scopeKind) {
      myScopeKind = scopeKind;
      return this;
    }

    @NotNull
    public Builder setTestFilePath(@Nullable String testFilePath) {
      myTestFilePath = StringUtil.notNullize(testFilePath);
      return this;
    }

    @NotNull
    public Builder setTestNames(@NotNull List<String> testNames) {
      myTestNames = testNames;
      return this;
    }

    @NotNull
    public KarmaRunSettings build() {
      return new KarmaRunSettings(this);
    }
  }
}
