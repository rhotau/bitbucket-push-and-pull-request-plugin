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

import hudson.Extension;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
//import org.kohsuke.stapler.DataBoundSetter;

public class BitBucketPPRPluginAuthGit extends BitBucketPPRPluginAuth {
  @DataBoundConstructor
  public BitBucketPPRPluginAuthGit() {
    this.authMethod = BitBucketPPRPluginAuth.BBPPR_AUTH_GIT;
  }

 /* @DataBoundSetter
  public void setCredentialsId(String credentialsId) {
    this.getDescriptor().setCredentialsId(credentialsId);
  }

  public String getCredentialsId() {
    return this.getDescriptor().getCredentialsId();
  }*/

  @Override
  public DescriptorImpl getDescriptor() {
    return Jenkins.get().getDescriptorByType(DescriptorImpl.class);
  }

  @Extension
  public static class DescriptorImpl extends BitBucketPPRPluginAuthDescriptor {
    @Override
    public String getDisplayName() {
      return "Use Git Credentials";
    }
  }
}
