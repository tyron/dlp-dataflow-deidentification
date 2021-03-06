/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.swarm.tokenization.common;

import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.io.aws.options.AwsOptions;
import org.apache.beam.sdk.io.aws.options.S3Options;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.ValueProvider;

public interface S3ImportOptions extends DataflowPipelineOptions, AwsOptions, S3Options {
	@Description("Import location in the format s3://<BUCKET_NAME>")
	ValueProvider<String> getBucketUrl();

	void setBucketUrl(ValueProvider<String> bucketUrl);

	@Description("AWS S3 Key ID")
	ValueProvider<String> getAwsAccessKey();

	void setAwsAccessKey(ValueProvider<String> awsAccessKey);

	@Description("AWS S3 Secret Key")
	ValueProvider<String> getAwsSecretKey();

	void setAwsSecretKey(ValueProvider<String> awsSecretKey);

	@Description("Template to DeIdentiy")
	ValueProvider<String> getDeidentifyTemplateName();

	void setDeidentifyTemplateName(ValueProvider<String> value);

	@Description("Template to Inspect")
	ValueProvider<String> getInspectTemplateName();

	void setInspectTemplateName(ValueProvider<String> value);

	@Description("Path of the file to write to")
	ValueProvider<String> getOutputFile();

	void setOutputFile(ValueProvider<String> value);

	@Description("batch Size")
	ValueProvider<Integer> getBatchSize();

	void setBatchSize(ValueProvider<Integer> value);

	@Description("AWS Client Configuration")
	int getMaxConnections();

	void setMaxConnections(int maxConnections);

	int getConnectionTimeout();

	void setConnectionTimeout(int connectionTimeout);

	int getSocketTimeout();

	void setSocketTimeout(int socketTimeout);

}
