/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amplifyframework.storage;

import android.content.Context;
import android.support.annotation.NonNull;

import com.amplifyframework.core.async.Callback;
import com.amplifyframework.core.category.Category;
import com.amplifyframework.core.category.CategoryType;
import com.amplifyframework.core.exception.ConfigurationException;
import com.amplifyframework.core.plugin.PluginException;
import com.amplifyframework.storage.exception.*;
import com.amplifyframework.storage.operation.*;
import com.amplifyframework.storage.options.*;
import com.amplifyframework.storage.result.StorageGetResult;
import com.amplifyframework.storage.result.StorageListResult;
import com.amplifyframework.storage.result.StoragePutResult;
import com.amplifyframework.storage.result.StorageRemoveResult;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Defines the Client API consumed by the application.
 * Internally routes the calls to the Storage Category
 * plugins registered.
 */

public class StorageCategory implements Category<StoragePlugin, StoragePluginConfiguration>, StorageCategoryBehavior {

    static class PluginDetails {
        StoragePlugin storagePlugin;
        StoragePluginConfiguration storagePluginConfiguration;

        public StoragePlugin getStoragePlugin() {
            return storagePlugin;
        }

        public PluginDetails storagePlugin(StoragePlugin storagePlugin) {
            this.storagePlugin = storagePlugin;
            return this;
        }

        public StoragePluginConfiguration getStoragePluginConfiguration() {
            return storagePluginConfiguration;
        }

        public PluginDetails storagePluginConfiguration(StoragePluginConfiguration storagePluginConfiguration) {
            this.storagePluginConfiguration = storagePluginConfiguration;
            return this;
        }
    }

    private Map<String, PluginDetails> plugins;

    /**
     * Currently selected plugin
     */
    private StoragePlugin plugin;

    /**
     * Flag to remember that Storage category is already configured by Amplify
     * and throw an error if configure method is called again
     */
    private boolean isConfigured;

    public StorageCategory() {
        this.plugins = new ConcurrentHashMap<String, PluginDetails>();
        this.isConfigured = false;
    }

    @Override
    public StorageGetOperation get(@NonNull String key) throws StorageGetException {
        return get(key, new StorageGetOptions(), null);
    }

    @Override
    public StorageGetOperation get(@NonNull String key,
                                   @NonNull StorageGetOptions options) throws StorageGetException {
        return get(key, options, null);
    }

    @Override
    public StorageGetOperation get(@NonNull String key,
                                   @NonNull StorageGetOptions options,
                                   Callback<StorageGetResult> callback) throws StorageGetException {
        return plugin.get(key, options, callback);
    }

    /**
     * Upload local file on given path to storage
     *
     * @param key   the unique identifier of the object in storage
     * @param local the path to a local file
     * @return an operation object that provides notifications and
     * actions related to the execution of the work
     * @throws StoragePutException
     */
    @Override
    public StoragePutOperation put(@NonNull String key, @NonNull String local) throws StoragePutException {
        return put(key, local, new StoragePutOptions(), null);
    }

    @Override
    public StoragePutOperation put(@NonNull String key,
                                   @NonNull String local,
                                   @NonNull StoragePutOptions options) throws StoragePutException {
        return put(key, local, options, null);
    }

    @Override
    public StoragePutOperation put(@NonNull String key,
                                   @NonNull String local,
                                   @NonNull StoragePutOptions options,
                                   Callback<StoragePutResult> callback) throws StoragePutException {
        return plugin.put(key, local, options, callback);
    }

    @Override
    public StorageListOperation list() throws StorageListException {
        return list(new StorageListOptions());
    }

    @Override
    public StorageListOperation list(@NonNull StorageListOptions options) throws StorageListException {
        return list(options, null);
    }

    @Override
    public StorageListOperation list(@NonNull StorageListOptions options,
                                     Callback<StorageListResult> callback) throws StorageListException {
        return plugin.list(options, callback);
    }

    @Override
    public StorageRemoveOperation remove(@NonNull String key) throws StorageRemoveException {
        return remove(key, new StorageRemoveOptions());
    }

    @Override
    public StorageRemoveOperation remove(@NonNull String key,
                                         StorageRemoveOptions options) throws StorageRemoveException {
        return remove(key, options, null);
    }

    @Override
    public StorageRemoveOperation remove(@NonNull String key,
                                         @NonNull StorageRemoveOptions options,
                                         Callback<StorageRemoveResult> callback) throws StorageRemoveException {
        return plugin.remove(key, options, callback);
    }

    /**
     * Read the configuration from amplifyconfiguration.json file
     *
     * @param context     Android context required to read the contents of file
     * @throws ConfigurationException thrown when already configured
     * @throws PluginException        thrown when there is no plugin found for a configuration
     */
    @Override
    public void configure(@NonNull Context context) throws ConfigurationException, PluginException {
        if (isConfigured) {
            throw new ConfigurationException.AmplifyAlreadyConfiguredException();
        }

        if (!plugins.values().isEmpty()) {
            if (plugins.values().iterator().hasNext()) {
                PluginDetails pluginDetails = plugins.values().iterator().next();
                if (pluginDetails.storagePluginConfiguration == null) {
                    pluginDetails.storagePlugin.configure(context);
                } else {
                    pluginDetails.storagePlugin.configure(pluginDetails.storagePluginConfiguration);
                }

            }
        }

        isConfigured = true;
    }

    /**
     * Register a Storage plugin with Amplify
     *
     * @param plugin an implementation of StoragePlugin
     * @throws PluginException when this plugin cannot be found
     */
    @Override
    public void addPlugin(@NonNull StoragePlugin plugin) throws PluginException {
        PluginDetails pluginDetails = new PluginDetails()
                .storagePlugin(plugin);

        try {
            if (plugins.put(plugin.getPluginKey(), pluginDetails) == null) {
                throw new PluginException.NoSuchPluginException();
            }
        } catch (Exception ex) {
            throw new PluginException.NoSuchPluginException();
        }
    }

    /**
     * Register a Storage plugin with Amplify
     *
     * @param plugin              an implementation of a StoragePlugin
     * @param pluginConfiguration configuration information for the plugin.
     * @throws PluginException when a plugin cannot be registered for Storage category
     */
    @Override
    public void addPlugin(@NonNull StoragePlugin plugin, @NonNull StoragePluginConfiguration pluginConfiguration) throws PluginException {
        PluginDetails pluginDetails = new PluginDetails()
                .storagePlugin(plugin)
                .storagePluginConfiguration(pluginConfiguration);

        try {
            if (plugins.put(plugin.getPluginKey(), pluginDetails) == null) {
                throw new PluginException.NoSuchPluginException();
            }
        } catch (Exception ex) {
            throw new PluginException.NoSuchPluginException();
        }
    }

    /**
     * Remove a registered Storage plugin
     *
     * @param plugin an implementation of StoragePlugin
     */
    @Override
    public void removePlugin(@NonNull StoragePlugin plugin) throws PluginException {
        if (plugins.containsKey(plugin.getPluginKey())) {
            plugins.remove(plugin.getPluginKey());
        } else {
            throw new PluginException.NoSuchPluginException();
        }
    }

    /**
     * Reset Storage category to state where it is not configured
     */
    @Override
    public void reset() {
        //TODO: Implement
    }

    /**
     * Retrieve a registered Storage plugin.
     *
     * @param pluginKey the key that identifies the plugin implementation
     * @return the Storage plugin object
     */
    @Override
    public StoragePlugin getPlugin(@NonNull String pluginKey) throws PluginException {
        if (plugins.containsKey(pluginKey)) {
            return plugins.get(pluginKey).storagePlugin;
        } else {
            throw new PluginException.NoSuchPluginException();
        }
    }

    /**
     * @return the set of plugins added to a Category.
     */
    @Override
    public Set<StoragePlugin> getPlugins() {
        Set<StoragePlugin> storagePlugins = new HashSet<StoragePlugin>();
        if (!plugins.isEmpty()) {
            for (PluginDetails pluginDetails : plugins.values()) {
                storagePlugins.add(pluginDetails.storagePlugin);
            }
        }
        return storagePlugins;
    }

    /**
     * Retrieve the Storage category type enum
     *
     * @return enum that represents Storage category
     */
    @Override
    public final CategoryType getCategoryType() {
        return CategoryType.STORAGE;
    }
}