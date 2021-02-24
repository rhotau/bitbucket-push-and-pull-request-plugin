/*
 * The MIT License
 *
 * Copyright 2021 CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.jenkins.plugins.bitbucketpushandpullrequest.auth;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernameListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import hudson.Extension;
import hudson.model.Job;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.bitbucketpushandpullrequest.auth.api.BitbucketOAuthApi;
import io.jenkins.plugins.bitbucketpushandpullrequest.auth.api.BitbucketOAuthApiService;
import java.util.List;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

public class BitBucketPPRPluginAuthOAuth2 extends BitBucketPPRPluginAuth {
  @DataBoundConstructor
  public BitBucketPPRPluginAuthOAuth2() {
    this.authMethod = BitBucketPPRPluginAuth.BBPPR_AUTH_OAUTH2;
  }

  @DataBoundSetter
  public void setCredentialsId(String credentialsId) {
    this.getDescriptor().setCredentialsId(credentialsId);
  }

  public String getCredentialsId() {
    return this.getDescriptor().getCredentialsId();
  }

  @Override
  public DescriptorImpl getDescriptor() {
    return Jenkins.get().getDescriptorByType(DescriptorImpl.class);
  }
  
  @Extension
  public static class DescriptorImpl extends BitBucketPPRPluginAuthDescriptor {
    private String credentialsId;

    public String getCredentialsId() {
      return credentialsId;
    }

    public void setCredentialsId(String credentialsId) {
      this.credentialsId = credentialsId;
    }

    @Override
    public String getDisplayName() {
      return "Use OAuth2 back-end";
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
      req.bindJSON(this, formData);
      save();
      return true;
    }

    public ListBoxModel doFillCredentialsIdItems() {
      Job owner = null;
      List<DomainRequirement> apiEndpoint = URIRequirementBuilder.fromUri(BitbucketOAuthApi.OAUTH_ENDPOINT).build();

      return new StandardUsernameListBoxModel()
        .withEmptySelection()
        .withAll(CredentialsProvider.lookupCredentials(StandardUsernamePasswordCredentials.class, owner, null, apiEndpoint));
    }

    public FormValidation doCheckCredentialsId(@QueryParameter final String credentialsId) {
      if (credentialsId.isEmpty()) {
        return FormValidation.ok();
      }

      Job owner = null;
      UsernamePasswordCredentials credentials = DescriptorImpl.getCredentials(credentialsId, owner);

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
  }
}
