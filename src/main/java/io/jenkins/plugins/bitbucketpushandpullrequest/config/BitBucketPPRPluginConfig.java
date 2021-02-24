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
package io.jenkins.plugins.bitbucketpushandpullrequest.config;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import java.util.logging.Logger;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.DescriptorVisibilityFilter;
import hudson.model.Item;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import io.jenkins.plugins.bitbucketpushandpullrequest.auth.BitBucketPPRPluginAuthDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.Stapler;

@Extension
public class BitBucketPPRPluginConfig extends GlobalConfiguration {
  private static final Logger logger = Logger.getLogger(BitBucketPPRPluginConfig.class.getName());
  public static final String BITBUCKET_PPR_PLUGIN_CONFIGURATION_ID = "bitbucket-ppr-plugin-configuration";

  public String hookUrl;

  public boolean notifyBitBucket;

  @DataBoundConstructor
  public BitBucketPPRPluginConfig() {
    /* Set some default values */
    this.notifyBitBucket = true;
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

  public Collection<BitBucketPPRPluginAuthDescriptor> getAuthDescriptors() {
    // Authentication methods are described by their own descriptor, this class
    // is merely of conduct to build the RadioButtonList rendering from these
    // respective subclasses.
    StaplerRequest req = Stapler.getCurrentRequest();
    Item it = (req != null) ? req.findAncestorObject(Item.class) : null;
    // All authentication methods' Descriptor will extend BitBucketPPRPluginAuthDescriptor
    return DescriptorVisibilityFilter.apply((it != null) ? it : Jenkins.get(), ExtensionList.lookup(BitBucketPPRPluginAuthDescriptor.class));
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
