package io.jenkins.plugins.bitbucketpushandpullrequest.config.auth;

import jenkins.model.GlobalConfiguration;

public abstract class BitBucketPPRPluginConfigAuth extends GlobalConfiguration {
  public static String BBPPR_AUTH_GIT = "git";
  public static String BBPPR_AUTH_SSH = "userpwd";
  public static String BBPPR_AUTH_OAUTH2 = "oauth2";
  
  public BitBucketPPRPluginConfigAuth() {
  }
}
