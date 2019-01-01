/*
 * Copyright 2019 Mitsunori Komatsu (komamitsu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.komamitsu.fluency.treasuredata;

import org.komamitsu.fluency.BaseFluencyBuilder;
import org.komamitsu.fluency.Fluency;
import org.komamitsu.fluency.buffer.Buffer;
import org.komamitsu.fluency.flusher.AsyncFlusher;
import org.komamitsu.fluency.ingester.sender.ErrorHandler;
import org.komamitsu.fluency.treasuredata.ingester.TreasureDataIngester;
import org.komamitsu.fluency.treasuredata.ingester.sender.TreasureDataSender;
import org.komamitsu.fluency.treasuredata.recordformat.TreasureDataRecordFormatter;

public class FluencyBuilder
{
    private static FluencyConfig ensuredConfig(FluencyConfig config)
    {
        return config == null ? new FluencyConfig() : config;
    }

    public static Fluency build(String apikey, FluencyConfig config)
    {
        return buildInternal(createSenderConfig(config, null, apikey), ensuredConfig(config));
    }

    private static TreasureDataSender.Config createSenderConfig(
            FluencyConfig config,
            String endpoint,
            String apikey)
    {
        if (apikey == null) {
            throw new IllegalArgumentException("`apikey` should be set");
        }

        TreasureDataSender.Config senderConfig = new TreasureDataSender.Config();
        senderConfig.setApikey(apikey);

        if (endpoint != null) {
            senderConfig.setEndpoint(endpoint);
        }

        return senderConfig;
    }

    private static Fluency buildInternal(
            TreasureDataSender.Config senderConfig,
            FluencyConfig config)
    {
        BaseFluencyBuilder.Configs configs = BaseFluencyBuilder.buildConfigs(config.baseConfig);

        Buffer.Config bufferConfig = configs.getBufferConfig();
        AsyncFlusher.Config flusherConfig = configs.getFlusherConfig();

        TreasureDataIngester.Config transporterConfig = new TreasureDataIngester.Config();

        if (config.getErrorHandler() != null) {
            senderConfig.setErrorHandler(config.getErrorHandler());
        }

        TreasureDataSender sender = senderConfig.createInstance();

        return BaseFluencyBuilder.buildFromConfigs(
                new TreasureDataRecordFormatter.Config(),
                bufferConfig,
                flusherConfig,
                transporterConfig.createInstance(sender)
        );
    }

    public static class FluencyConfig
    {
        private BaseFluencyBuilder.FluencyConfig baseConfig = new BaseFluencyBuilder.FluencyConfig();

        public Long getMaxBufferSize()
        {
            return baseConfig.getMaxBufferSize();
        }

        public FluencyConfig setMaxBufferSize(Long maxBufferSize)
        {
            baseConfig.setMaxBufferSize(maxBufferSize);
            return this;
        }

        public Integer getBufferChunkInitialSize()
        {
            return baseConfig.getBufferChunkInitialSize();
        }

        public FluencyConfig setBufferChunkInitialSize(Integer bufferChunkInitialSize)
        {
            baseConfig.setBufferChunkInitialSize(bufferChunkInitialSize);
            return this;
        }

        public Integer getBufferChunkRetentionSize()
        {
            return baseConfig.getBufferChunkRetentionSize();
        }

        public FluencyConfig setBufferChunkRetentionSize(Integer bufferChunkRetentionSize)
        {
            baseConfig.setBufferChunkRetentionSize(bufferChunkRetentionSize);
            return this;
        }

        public Integer getFlushIntervalMillis()
        {
            return baseConfig.getFlushIntervalMillis();
        }

        public FluencyConfig setFlushIntervalMillis(Integer flushIntervalMillis)
        {
            baseConfig.setFlushIntervalMillis(flushIntervalMillis);
            return this;
        }

        public String getFileBackupDir()
        {
            return baseConfig.getFileBackupDir();
        }

        public FluencyConfig setFileBackupDir(String fileBackupDir)
        {
            baseConfig.setFileBackupDir(fileBackupDir);
            return this;
        }

        public Integer getWaitUntilBufferFlushed()
        {
            return baseConfig.getWaitUntilBufferFlushed();
        }

        public FluencyConfig setWaitUntilBufferFlushed(Integer waitUntilBufferFlushed)
        {
            baseConfig.setWaitUntilBufferFlushed(waitUntilBufferFlushed);
            return this;
        }

        public Integer getWaitUntilFlusherTerminated()
        {
            return baseConfig.getWaitUntilFlusherTerminated();
        }

        public FluencyConfig setWaitUntilFlusherTerminated(Integer waitUntilFlusherTerminated)
        {
            baseConfig.setWaitUntilFlusherTerminated(waitUntilFlusherTerminated);
            return this;
        }

        public Boolean getJvmHeapBufferMode()
        {
            return baseConfig.getJvmHeapBufferMode();
        }

        public FluencyConfig setJvmHeapBufferMode(Boolean jvmHeapBufferMode)
        {
            baseConfig.setJvmHeapBufferMode(jvmHeapBufferMode);
            return this;
        }

        public ErrorHandler getErrorHandler()
        {
            return baseConfig.getErrorHandler();
        }

        public FluencyConfig setErrorHandler(ErrorHandler errorHandler)
        {
            baseConfig.setErrorHandler(errorHandler);
            return this;
        }

        @Override
        public String toString()
        {
            return "FluencyConfig{" +
                    "baseConfig=" + baseConfig +
                    '}';
        }
    }
}
