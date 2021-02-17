package io.jenkins.plugins.bitbucketpushandpullrequest.config;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;
import hudson.Extension;
import hudson.ExtensionList;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

import hudson.util.ListBoxModel;
import hudson.util.FormValidation;
import hudson.model.Job;
import hudson.model.Item;
import java.util.List;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernameListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import io.jenkins.plugins.bitbucketpushandpullrequest.auth.BitbucketOAuthApi;
import io.jenkins.plugins.bitbucketpushandpullrequest.auth.BitbucketOAuthApiService;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

@Extension
public class BitBucketPPRPluginConfig extends GlobalConfiguration {
  private static final Logger logger = Logger.getLogger(BitBucketPPRPluginConfig.class.getName());
  public static final String BITBUCKET_PPR_PLUGIN_CONFIGURATION_ID = "bitbucket-ppr-plugin-configuration";

  public String hookUrl;

  public boolean notifyBitBucket;

  public String authMethod;

  public String globalCredentialsIdOAuth;
  public String globalCredentialsIdSsh;

  public BitBucketPPRPluginConfig() {
    logger.fine("Read bitbucket push and pull request plugin global configuration.");
    load();
  }

  public static BitBucketPPRPluginConfig getInstance() {
    return ExtensionList.lookupSingleton(BitBucketPPRPluginConfig.class);
  }

  @DataBoundSetter
  public void setHookUrl(String hookUrl) {
    if (isEmpty(hookUrl)) {
      this.hookUrl = "";
    } else {
      this.hookUrl = hookUrl;
    }
    save();
  }

  public boolean isHookUrlSet() {
    return ! isEmpty(hookUrl);
  }

  public String getHookUrl() {
    return hookUrl;
  }

  public boolean shouldNotifyBitBucket() {
    return notifyBitBucket;
  }

  @DataBoundSetter
  public void setNotifyBitBucket(boolean notifyBitBucket) {
    this.notifyBitBucket = notifyBitBucket;
  }

  public boolean getNotifyBitBucket() {
    return notifyBitBucket;
  }

  public String getGlobalCredentialsIdSsh() {
    return globalCredentialsIdSsh;
  }

  @DataBoundSetter
  public void setGlobalCredentialsIdSsh(String globalCredentialsIdSsh) {
    this.globalCredentialsIdSsh = globalCredentialsIdSsh;
  }

  public String getGlobalCredentialsIdOAuth() {
    return globalCredentialsIdOAuth;
  }

  public ListBoxModel doFillGlobalCredentialsIdSshItems() {
    /* empty for now, just to get things started */
    return new StandardUsernameListBoxModel();
  }

  @DataBoundSetter
  public void setGlobalCredentialsIdOAuth(String globalCredentialsIdOAuth) {
    this.globalCredentialsIdOAuth = globalCredentialsIdOAuth;
  }

  public ListBoxModel doFillGlobalCredentialsIdOAuthItems() {
    Job owner = null;
    List<DomainRequirement> apiEndpoint = URIRequirementBuilder.fromUri(BitbucketOAuthApi.OAUTH_ENDPOINT).build();

    return new StandardUsernameListBoxModel()
      .withEmptySelection()
      .withAll(CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class, owner, null, apiEndpoint));
  }

  public FormValidation doCheckGlobalCredentialsIdOAuth(@QueryParameter final String globalCredentialsIdOAuth) {
    if (globalCredentialsIdOAuth.isEmpty()) {
      return FormValidation.ok();
    }

    Job owner = null;
    UsernamePasswordCredentials credentials = this.getCredentials(globalCredentialsIdOAuth, owner);

    return this.checkCredentials(credentials);
  }

  private FormValidation checkCredentials(UsernamePasswordCredentials credentials) {
    try {
      OAuthConfig config = new OAuthConfig(credentials.getUsername(), credentials.getPassword().getPlainText());
      BitbucketOAuthApiService apiService = (BitbucketOAuthApiService) new BitbucketOAuthApi().createService(config);
      Verifier verifier = null;
      Token token = apiService.getAccessToken(OAuthConstants.EMPTY_TOKEN, verifier);

      if (token.isEmpty()) {
          return FormValidation.error("Invalid Bitbucket OAuth credentials");
      }
    } catch (Exception e) {
        return FormValidation.error(e.getClass() + e.getMessage());
    }
    return FormValidation.ok();
  }

  public static StandardUsernamePasswordCredentials getCredentials(String credentialsId, Job<?,?> owner) {
    if (credentialsId != null) {
      for (StandardUsernamePasswordCredentials c : CredentialsProvider.lookupCredentials(
        StandardUsernamePasswordCredentials.class, owner, null,
        URIRequirementBuilder.fromUri(BitbucketOAuthApi.OAUTH_ENDPOINT).build())) {
        if (c.getId().equals(credentialsId)) {
          return c;
        }
      }
    }
    return null;
  }

  @DataBoundSetter
  public void setAuthMethod(String authMethod) {
    logger.fine("Bitbucket push and pull request plugin status auth:" + authMethod);
    this.authMethod = authMethod;
  }

  public String getAuthMethod() {
    return authMethod;
  }

  @Override
  public String getDisplayName() {
    return "Bitbucket Push and Pull Request";
  }

  @Override
  public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
    req.bindJSON(this, formData.getJSONObject(BITBUCKET_PPR_PLUGIN_CONFIGURATION_ID));
    save();
    return true;
  }
}
