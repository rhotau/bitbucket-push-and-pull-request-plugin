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

import com.cloudbees.plugins.credentials.common.StandardUsernameListBoxModel;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class BitBucketPPRPluginAuthUserPwd extends BitBucketPPRPluginAuth {
  @DataBoundConstructor
  public BitBucketPPRPluginAuthUserPwd() {
    this.authMethod = BitBucketPPRPluginAuth.BBPPR_AUTH_SSH;
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
      return "Use dedicated username/password";
    }

    public ListBoxModel doFillCredentialsIdItems() {
      /* empty for now, just to get things started */
      return new StandardUsernameListBoxModel();
    }

    public FormValidation doCheckCredentialsId(@QueryParameter final String credentialsId) {
      return FormValidation.ok();
    }
  }
}
